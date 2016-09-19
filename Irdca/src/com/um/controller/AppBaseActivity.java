package com.um.controller;

import com.um.dvbstack.ProgManage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;

public class AppBaseActivity extends Activity
{
	Context mContext;
	public MediaPlayer mMediaPlayer = null;
    private CountDownTimer countDownTimer = null;
    private AutoFinishListener autoFinishListener = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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

    protected void setAutoFinish(int second, AutoFinishListener autoFinishListener) {
        this.autoFinishListener = autoFinishListener;
        countDownTimer = new CountDownTimer(1000 * second, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if (AppBaseActivity.this.autoFinishListener != null) {
                    AppBaseActivity.this.autoFinishListener.onFinish();
                }
                finish();
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    protected void restarAutoFinishTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
    }

    protected void startAutoFinishTimer() {
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }

    protected void stopAutoFinishTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
        restarAutoFinishTimer();
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_UP:

			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:

			break;
		case 1181: // BTV jian
            /*
			if(this instanceof Dvbplayer_Activity)
			{
				break;
			}
			Intent it1 = new Intent(AppBaseActivity.this, Dvbplayer_Activity.class);
        	ProgManage.GetInstance().SetCurMode(ProgManage.TVPROG);
        	startActivity(it1);
        	*/
			break;
			
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

     public interface AutoFinishListener{
         public void onFinish();
     }
}
