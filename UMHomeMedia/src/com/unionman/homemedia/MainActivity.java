package com.unionman.homemedia;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager.OnCancelListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("lwn","enter umhomemedia oncreate");
		ImageView ImgBtn = (ImageView) findViewById(R.id.ImgBtn);
		ImageView AudioBtn = (ImageView) findViewById(R.id.AudioBtn);
		ImageView VideoBtn = (ImageView) findViewById(R.id.VideoBtn);
		ImageView FileBtn = (ImageView) findViewById(R.id.FileBtn);
		ImgBtn.setOnClickListener(this);
		AudioBtn.setOnClickListener(this);
		VideoBtn.setOnClickListener(this);
		FileBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ImgBtn:
			  Intent Imgintent = new Intent();
			  Imgintent.setClassName("tv.cmcc.vendor", "tv.cmcc.vendor.Main");
			  Imgintent.putExtra("actiontype", "Gallery");
			  startActivity(Imgintent);
			break;
		case R.id.AudioBtn:
			  Intent Auintent = new Intent();
			  Auintent.setClassName("tv.cmcc.vendor", "tv.cmcc.vendor.Main");
			  Auintent.putExtra("actiontype", "MusicPlayer");
			  startActivity(Auintent);
			break;
		case R.id.VideoBtn:
			  Intent Viintent = new Intent();
			  Viintent.setClassName("tv.cmcc.vendor", "tv.cmcc.vendor.Main");
			  Viintent.putExtra("actiontype", "VideoPlayer");
			  startActivity(Viintent);
			break;
		case R.id.FileBtn:
			  Intent Fiintent = new Intent();
			  Fiintent.setClassName("tv.cmcc.vendor", "tv.cmcc.vendor.Main");
			  Fiintent.putExtra("actiontype", "FileBrowser");
			  startActivity(Fiintent);
			break;
		default:
				
		}
		
	}
}
