package com.kritcharat.prayapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class SplashScreen extends Activity {
	  
	@Override
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splashscreen);
		
		// ok button
		Button buttonOK = (Button) findViewById(R.id.aboutbtn);
        buttonOK.setOnClickListener(new OnClickListener() {
 
			  @Override
			  public void onClick(View arg0) {
				 Intent mainintent = new Intent(); 
				 mainintent.setClass(SplashScreen.this,MainActivity.class);
				 startActivity(mainintent);
				 finish();
				 overridePendingTransition(R.animator.push_right_anim, R.animator.pull_right_anim);
			  }
		});
        
        // share button
        Button buttonShare = (Button) findViewById(R.id.sharebtn);
        buttonShare.setOnClickListener(new OnClickListener() {
 
			  @Override
			  public void onClick(View v) {
				  shareApp();
			  }
		});
	}
	
	/**
	 * Share intent
	 */
	private void shareApp(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_TEXT,"Kritcharat Duangin");
		intent.putExtra(android.content.Intent.EXTRA_STREAM,R.drawable.sc_shot_a);
		startActivity(Intent.createChooser(intent, "นักพัฒนาเเอพพลิเคชั่น"));
	}
}// end Activity class
