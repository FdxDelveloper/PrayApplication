package com.kritcharat.prayapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {

	private static final int UPDATE_FREQUENCY = 500;
	private static final double SPEED_STEP = 0.25;
    private static final double MIN_SPEED = 1.0;
    private static final double MAX_SPEED = 4.0;
    
	String position = "1";
	String name = "";
	String description = "";
	String audio = "";
	String text = "";
	String iconfile = "";

	ProgressDialog mProgressDialog;
	ScrollView Scrollview;
	LinearLayout OuterLayout;
	TextView headtxt; // for title
	MediaPlayer player;
	PopupWindow popUpPlay;
	LinearLayout layout;
	LayoutParams params;
	Button puPlayBtn;
	SeekBar seekbar;
	

	private boolean isStarted = true;
	public boolean isMuteSound = false;
	private int verticalScrollMax;
	private Timer scrollTimer = null;
	private TimerTask scrollerSchedule;
	private int scrollPos = 0;
	private long delay = 150;
	public double speed = 1.5;
	public float volume = 0.0f;
	
	private boolean isMoveingSeekBar = false;
	AudioManager am;
	private final Handler handler = new Handler();
     
    private final Runnable updatePositionRunnable = new Runnable() {
            public void run() {
                    updatePosition();
            }
    };
    
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// Create pop up window for floating button
		popUpPlay = new PopupWindow(this);
		layout = new LinearLayout(this);
		puPlayBtn = new Button(this);
		puPlayBtn.setBackgroundResource(R.drawable.playoverlay_play);

		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(puPlayBtn, params);
		popUpPlay.setContentView(layout);
		popUpPlay.setBackgroundDrawable(null);
		//Find screen density scale factor
		
		popUpPlay.setWidth(LayoutParams.WRAP_CONTENT);
		popUpPlay.setHeight(LayoutParams.WRAP_CONTENT);

		setContentView(R.layout.activity_play);

		try {

			// Get position to display
			Intent getin = getIntent();

			this.position = getin.getStringExtra("position");
			this.name = getin.getStringExtra("name");
			this.description = getin.getStringExtra("description");
			this.audio = getin.getStringExtra("audio");
			this.text = getin.getStringExtra("text");
			this.iconfile = getin.getStringExtra("icon");

			player = new MediaPlayer();

			headtxt = (TextView) findViewById(R.id.textHead);
			Scrollview = (ScrollView) findViewById(R.id.vertical_scrollview_id);
			OuterLayout = (LinearLayout) findViewById(R.id.vertical_outer_layout_id);
			seekbar = (SeekBar) findViewById(R.id.seekbar);
			headtxt.setText(name);

			mProgressDialog = new ProgressDialog(PlayActivity.this);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.show();

			seekbar.setOnSeekBarChangeListener(seekBarChanged);
			am = (AudioManager) this.getSystemService(PlayActivity.AUDIO_SERVICE);
			
			new LoadHtmlFileTask().execute(text);

			ViewTreeObserver vto = OuterLayout.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					// OuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					getScrollMaxAmount();
				}
			});
			
			// on pop up window play push
			// instant play btn
			
			puPlayBtn.setOnClickListener(new OnClickListener() {
				Button buttonplayer = (Button) findViewById(R.id.btn_playlist);
				@Override
				public void onClick(View v) {
					popUpPlay.dismiss();
					buttonplayer.setBackgroundResource(R.drawable.playpause_btn);
					startMediaPlay();
					startAutoScrolling();
					
				}
			});

			// back to main activity
			Button buttonback = (Button) findViewById(R.id.btn_back);
			buttonback.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});

			// speed decrease
			Button buttonSpeeddec = (Button) findViewById(R.id.btn_speeddec);
			buttonSpeeddec.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					speed -= SPEED_STEP;
					if (speed < MIN_SPEED)
						speed = MIN_SPEED;
					
				}
			});

			// play/pause
			Button buttonplayer = (Button) findViewById(R.id.btn_playlist);
			buttonplayer.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Button playbtn = (Button) v.findViewById(R.id.btn_playlist);
					
					if (player.isPlaying()) {
						//handler.removeCallbacks(updatePositionRunnable);
						player.pause();
						stopAutoScrolling();
						playbtn.setBackgroundResource(R.drawable.playplay_btn);
					} else {
						if (isStarted) {
							popUpPlay.dismiss();
							player.start();
							startAutoScrolling();
							playbtn.setBackgroundResource(R.drawable.playpause_btn);
							updatePosition();
						} else {
							startMediaPlay();
						}
					}
					
				}
			});

			// speed increase
			Button buttonSpeedinc = (Button) findViewById(R.id.btn_speedinc);
			buttonSpeedinc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					speed += SPEED_STEP;
					if (speed > MAX_SPEED)
						speed = MAX_SPEED;
					
				}
			});

			// sound mute
			Button buttonmute = (Button) findViewById(R.id.btn_mute);
			buttonmute.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Button btnmute = (Button) v.findViewById(R.id.btn_mute);
					
					if (isMuteSound) {
						// fixed bug : volume more than 1.0 the sound will disappear
						volume *= 0.1;
						if(volume > 0.95){
							volume = 0.95f;
						}
						//Toast.makeText(getApplicationContext(), "volume is " + volume, Toast.LENGTH_SHORT).show();
						player.setVolume(volume, volume);
						btnmute.setBackgroundResource(R.drawable.playmute_btn);
					} 
					else {
						volume = (float) am.getStreamVolume(AudioManager.STREAM_MUSIC);
						player.setVolume(0, 0);
						btnmute.setBackgroundResource(R.drawable.playvolon_btn);
					}
					isMuteSound = !isMuteSound;
				}
			});

		} catch (Exception ex) {
			Toast.makeText(getApplicationContext(),
					"Loading exception [ " + ex.toString() + " ]",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		handler.removeCallbacks(updatePositionRunnable);
		player.stop();
		player.reset();
		player.release();

		player = null;
	}

	/**
	 * Start play sound
	 */
	private void startMediaPlay() {
		
		seekbar.setProgress(0);

		try {
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//playButton.setImageResource(android.R.drawable.ic_media_pause);

		updatePosition();
		isStarted = true;
	}

	// update position of seekbar
	private void updatePosition() {
		handler.removeCallbacks(updatePositionRunnable);
		seekbar.setProgress(player.getCurrentPosition());
		handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
	}

	/**
	 * Auto ScrollView
	 */
	public void getScrollMaxAmount() {
		int actualWidth = OuterLayout.getMeasuredHeight();
		verticalScrollMax = actualWidth;
	}

	public void startAutoScrolling() {
		if (scrollTimer == null) {
			scrollTimer = new Timer();
			final Runnable Timer_Tick = new Runnable() {
				public void run() {
					moveScrollView();
				}
			};

			if (scrollerSchedule != null) {
				scrollerSchedule.cancel();
				scrollerSchedule = null;
			}
			scrollerSchedule = new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(Timer_Tick);
				}
			};

			scrollTimer.schedule(scrollerSchedule, 30, delay);
		}
	}

	public void moveScrollView() {
		scrollPos = (int) (Scrollview.getScrollY() + speed);
		if (scrollPos >= verticalScrollMax) {
			stopAutoScrolling();
			scrollPos = 0;
		}
		Scrollview.scrollTo(0, scrollPos);
	}

	public void stopAutoScrolling() {
		if (scrollTimer != null) {
			scrollTimer.cancel();
			scrollTimer = null;
		}
	}
	
	public void pauseAutoScrolling() {
		if(scrollTimer != null) {
			
		}
	}

	/**
	 * AsyncTask for load html text file
	 */
	public class LoadHtmlFileTask extends AsyncTask<String, String, String> {
		TextView showtxt = (TextView) findViewById(R.id.textView1);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// Do your loading here
			AssetManager am = getApplicationContext().getAssets();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int i;

			try {
				InputStream is = am.open("text/" + text + ".html");
				int lenghtOfFile = is.available();

				long total = 0;
				i = is.read();
				while (i != -1) {
					total++;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					byteArrayOutputStream.write(i);
					i = is.read();
				}
				is.close();
			} catch (IOException ex) {
				Toast.makeText(
						getApplicationContext(),
						"Error exception:[" + text + "] [ " + ex.toString()
								+ " ]", Toast.LENGTH_LONG).show();
			}

			// Play Media
			try {
				AssetFileDescriptor afd = getApplicationContext().getAssets()
						.openFd("audio/" + audio + ".mp3");
				player.setDataSource(afd.getFileDescriptor(),
						afd.getStartOffset(), afd.getLength());
				afd.close();
				player.prepare();
				seekbar.setMax(player.getDuration());
			} catch (IOException e) {
				e.printStackTrace();
			}

			return byteArrayOutputStream.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			showtxt.setText(Html.fromHtml(result));
			//showtxt.setText(result);
			mProgressDialog.dismiss();
			popUpPlay.showAtLocation(Scrollview, Gravity.CENTER, 0, 0);
			// popUpPlay.update(0,0,72,72);
		}

		@Override
		public void onProgressUpdate(String... args) {
			mProgressDialog.setProgress(Integer.parseInt(args[0]));

		}

	}

	// seek bar listener
	private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			isMoveingSeekBar = false;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			isMoveingSeekBar = true;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
			if (isMoveingSeekBar) {
				player.seekTo(progress);
			}
		}
	};

}// end play activity class
