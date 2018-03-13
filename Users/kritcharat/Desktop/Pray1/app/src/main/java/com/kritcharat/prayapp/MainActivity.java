package com.kritcharat.prayapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	// ViewHolder class structure
	static class ViewHolder {
		TextView tvName;
		TextView tvDescription;
		TextView tvAudio;
		TextView tvText;
		ImageView IvIconimage;
	}

	static final String KEY_TAG = "chapter"; // parent node
	static final String KEY_ID = "id";
	static final String KEY_NAME = "name";
	static final String KEY_DESC = "description";
	static final String KEY_AUDIO = "audio";
	static final String KEY_TEXT = "text";
	static final String KEY_ICON = "icon";

	// List items
	ListView list;
	BinderData adapter = null;
	List<HashMap<String, String>> BookDataCollection;

	String xmlfile = "praylist.xml";

	// set wake app
	//protected PowerManager.WakeLock mWakeLock;
	// ----------------------------------------------------
	// Method
	// ----------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		// Button Event listener
		findViewById(R.id.btn_about).setOnClickListener(About_OnClickListener);
		findViewById(R.id.strabout).setOnClickListener(About_OnClickListener);

		try {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(getAssets().open(xmlfile));

			BookDataCollection = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = null;

			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList chapterList = doc.getElementsByTagName("chapter");

			for (int i = 0; i < chapterList.getLength(); i++) {

				map = new HashMap<String, String>();
				Node firstChapterNode = chapterList.item(i);

				if (firstChapterNode.getNodeType() == Node.ELEMENT_NODE) {

					Element firstChapterElement = (Element) firstChapterNode;

					// --id
					NodeList idList = firstChapterElement
							.getElementsByTagName(KEY_ID);
					Element firstIdElement = (Element) idList.item(0);
					NodeList textIdList = firstIdElement.getChildNodes();

					// --name
					NodeList nameList = firstChapterElement
							.getElementsByTagName(KEY_NAME);
					Element firstNameElement = (Element) nameList.item(0);
					NodeList textNameList = firstNameElement.getChildNodes();

					// --description
					NodeList descList = firstChapterElement
							.getElementsByTagName(KEY_DESC);
					Element firstDescElement = (Element) descList.item(0);
					NodeList textDescList = firstDescElement.getChildNodes();
					
					// --audio
					NodeList audioList = firstChapterElement
							.getElementsByTagName(KEY_AUDIO);
					Element firstAudioElement = (Element) audioList.item(0);
					NodeList textAudioList = firstAudioElement.getChildNodes();
					
					// --text
					NodeList textList = firstChapterElement
							.getElementsByTagName(KEY_TEXT);
					Element firstTextElement = (Element) textList.item(0);
					NodeList textTextList = firstTextElement.getChildNodes();

					// --icon
					NodeList iconList = firstChapterElement
							.getElementsByTagName(KEY_ICON);
					Element firstIconElement = (Element) iconList.item(0);
					NodeList textIconList = firstIconElement.getChildNodes();

					// --put into map of Array list
					map.put(KEY_ID, ((Node) textIdList.item(0)).getNodeValue()
							.trim());
					map.put(KEY_NAME, ((Node) textNameList.item(0))
							.getNodeValue().trim());
					map.put(KEY_DESC, ((Node) textDescList.item(0))
							.getNodeValue().trim());
					map.put(KEY_AUDIO, ((Node) textAudioList.item(0))
							.getNodeValue().trim());
					map.put(KEY_TEXT, ((Node) textTextList.item(0))
							.getNodeValue().trim());
					map.put(KEY_ICON, ((Node) textIconList.item(0))
							.getNodeValue().trim());

					// Add to the Array list
					BookDataCollection.add(map);
				}
			}

			BinderData bindingData = new BinderData(this, BookDataCollection);
			list = (ListView) findViewById(R.id.list);
			list.setAdapter(bindingData);

			// Click event for single list row
			list.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					 Intent playintent = new Intent(); 
					 playintent.setClass(MainActivity.this,PlayActivity.class);
					 
					 //pub intent data
					 playintent.putExtra("position", String.valueOf(position + 1));
					 playintent.putExtra("name", BookDataCollection.get(position).get(KEY_NAME));
					 playintent.putExtra("description", BookDataCollection.get(position).get(KEY_DESC));
					 playintent.putExtra("audio", BookDataCollection.get(position).get(KEY_AUDIO));
					 playintent.putExtra("text", BookDataCollection.get(position).get(KEY_TEXT));
					 playintent.putExtra("iconfile", BookDataCollection.get(position).get(KEY_ICON));
					 
					 startActivity(playintent);
					 overridePendingTransition(R.animator.push_right_anim, R.animator.pull_right_anim);
				}
			});

		} catch (IOException ex) {
			Log.e("Error", ex.getMessage());
		} catch (Exception ex) {
			Log.e("Error", ex.toString());
		}

	}

	
	// OnClick EventListener Method About
	final OnClickListener About_OnClickListener = new OnClickListener() {
		public void onClick(final View v) {
			// set to about page
			Intent about_intent = new Intent();
			about_intent.setClass(MainActivity.this, aboutActivity.class);
			startActivity(about_intent);
			overridePendingTransition(R.animator.push_right_anim, R.animator.pull_right_anim);
		}
	};


	/**
	 * Create menu option for application
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// fix no menu now
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// ------------------------------------------------
	// Inner Class
	// ------------------------------------------------
	public class BinderData extends BaseAdapter {

		LayoutInflater inflater;
		ImageView thumb_image;
		List<HashMap<String, String>> BookDataCollection;
		ViewHolder holder;

		public BinderData() {
			// TODO Auto-generated constructor stub
		}

		public BinderData(Activity act, List<HashMap<String, String>> map) {

			this.BookDataCollection = map;

			inflater = (LayoutInflater) act
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			// TODO Auto-generated method stub
			// return idlist.size();
			return BookDataCollection.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			View vi = convertView;
			if (convertView == null) {

				vi = inflater.inflate(R.layout.list_index, null);
				holder = new ViewHolder();

				holder.tvName = (TextView) vi.findViewById(R.id.name);
				holder.tvDescription = (TextView) vi.findViewById(R.id.desc);
				holder.IvIconimage = (ImageView) vi
						.findViewById(R.id.list_image); // thumb image

				vi.setTag(holder);
			} else {

				holder = (ViewHolder) vi.getTag();
			}

			// Setting all values in list view
			holder.tvName.setText(BookDataCollection.get(position)
					.get(KEY_NAME));
			holder.tvDescription.setText(BookDataCollection.get(position).get(
					KEY_DESC));

			// Setting an image
			String uri = "drawable/"
					+ BookDataCollection.get(position).get(KEY_ICON);
			int imageResource = vi
					.getContext()
					.getApplicationContext()
					.getResources()
					.getIdentifier(
							uri,
							null,
							vi.getContext().getApplicationContext()
									.getPackageName());
			Drawable image = vi.getContext().getResources()
					.getDrawable(imageResource);
			holder.IvIconimage.setImageDrawable(image);

			return vi;
		}
	}// end Binder data class
}
