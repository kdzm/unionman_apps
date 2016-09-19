package com.um.controller;

import com.um.dvbsearch.R;
import com.um.dvbstack.DVB;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class AppBaseActivity extends Activity
{
	Context mContext;
	public MediaPlayer mMediaPlayer = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!DVB.isServerAlive()) {
			finish();
		}
	}
	
	public void setMediaPlayer(MediaPlayer mediaplayer)
	{
	    Log.i("AppBaseActivity","setMediaPlayer....");
	    mMediaPlayer = mediaplayer;
	}
	public void setContext(Context c)
	{
		mContext =c;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_UP:

			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:

			break;
        /*
		case 1181: // BTV jian
			if(this instanceof Dvbplayer_Activity)
			{
				break;
			}
			Intent it1 = new Intent(AppBaseActivity.this, Dvbplayer_Activity.class);
        	ProgManage.GetInstance().SetCurMode(ProgManage.TVPROG);
        	startActivity(it1);
			break;
		*/
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);

	}

	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;

		}
		return super.onKeyUp(keyCode, event);

	}

}
