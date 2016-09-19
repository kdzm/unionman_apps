package com.um.atv;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.listener.OnChannelScanListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.vo.ChannelScanInfo;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.um.atv.R;
import com.um.atv.util.Constant;

/**
 * AutoScanActivity
 *
 * @author wangchuanjian
 *
 */
public class AutoScanActivity extends Activity {
    protected static final String TAG = "AutoScanActivity";
    // message to update channel number
    private static final int MSG_UPDATE_CHANNEL_NUM = 1001;
    // message to update channel frequency
    private static final int MSG_UPDATE_CHANNEL_FREQ = 1002;
    // text of channel number
    private TextView channelNumTxt = null;
    // text of frequency
    private TextView freqTxt = null;
    // text of frequency range
    private TextView freqRangeTxt = null;
    private Context mContext = null;
    // button of exit
    private Button exitBtn;
    private SeekBar autoSeekbar;
    // min frequency value
    private int mMinFreq = 0;
    // max frequency value
    private int mMaxFreq = 0;
    // number of channel
    private int mChannelNum = 0;
    private boolean onClickFinish = false;

    /**
     * Listener of channel scan
     */
    OnChannelScanListener mOnChannelScanListener = new OnChannelScanListener() {

        @Override
        public void onChannelScanFinish() {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onChannelScanFinish()");
            }
            UmtvManager.getInstance().getAtvChannel().exitScan();
            mContext.sendBroadcast(new Intent(Constant.ACTION_FINISH_RF_SCAN));
            finish();
        }

        @Override
        public void onChannelScanStart() {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onChannelScanStart()----------------->");
            }
            mContext.sendBroadcast(new Intent(Constant.ACTION_START_RF_SCAN));
        }

        @Override
        public void onChannelScanLock(TvProgram arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG,
                        "onChannelScanLock()----->program:" + arg0.getStrName()
                                + "   freq:" + arg0.getlFreq());
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_CHANNEL_NUM,
                    ++mChannelNum, 0));
        }

        @Override
        public void onChannelScanProgress(ChannelScanInfo arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG,
                        "onChannelScanProgress--->currentFreq:"
                                + arg0.getCurrFreq());
            }
            autoSeekbar.setProgress(arg0.getCurrFreq() - mMinFreq);
            mHandler.sendMessage(mHandler.obtainMessage(
                    MSG_UPDATE_CHANNEL_FREQ, arg0.getCurrFreq(), 0));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_scan);
        mContext = this;
        initView();
        mMaxFreq = UmtvManager.getInstance().getAtvChannel().getMaxTuneFreq();
        mMinFreq = UmtvManager.getInstance().getAtvChannel().getMinTuneFreq();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onCreate()--->ATV--->MaxFreq:" + mMaxFreq
                    + "        MinFreq:" + mMinFreq + "        FreqDuration:"
                    + (mMaxFreq - mMinFreq));
        }
        onClickFinish = false;
        autoSeekbar.setMax(mMaxFreq - mMinFreq);

        exitBtn.requestFocus();

        exitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "exitBtn onclick()");
                }
                UmtvManager.getInstance().getAtvChannel().exitScan();
                onClickFinish = true;
            }
        });
    }

    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	registerListener();
    	UmtvManager.getInstance().getAtvChannel().autoScan();
    	super.onResume();
    }
    
    /**
     * The initialization of all views
     */
    private void initView() {
        exitBtn = (Button) findViewById(R.id.auto_exit_btn);
        autoSeekbar = (SeekBar) findViewById(R.id.auto_seekbar);
        channelNumTxt = (TextView) findViewById(R.id.channel_value_txt);
        freqTxt = (TextView) findViewById(R.id.freq_value_txt);
        freqRangeTxt = (TextView) findViewById(R.id.band_value_txt);

    }

    /**
     * register all listeners
     */
    private void registerListener() {
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SCAN_PROGRESS, mOnChannelScanListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SCAN_LOCK, mOnChannelScanListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SCAN_FINISH, mOnChannelScanListener);
    }

    @Override
    public boolean onKeyDown(int arg0, KeyEvent arg1) {
        switch (arg0) {
        case KeyEvent.KEYCODE_BACK:
        case KeyEvent.KEYCODE_HOME:
        case KeyEvent.KEYCODE_SOURCE:
        case KeyEvent.KEY_USB:
        case KeyEvent.KEY_APPLICATION:
        case KeyEvent.KEYCODE_TV:
            return true;
   	    case KeyEvent.KEY_SOURCEENTER:	
   	    	    Log.i(TAG,"KEY_SOURCEENTER is clicked!");
                if(exitBtn.isFocused()){
    		    	UmtvManager.getInstance().getAtvChannel().exitScan();
                    onClickFinish = true;
                }
		    return true;  
   	    case KeyEvent.KEYCODE_MENU:
	    	UmtvManager.getInstance().getAtvChannel().exitScan();
	        onClickFinish = true; 
   	    break;
        default:
            break;
        }
        return super.onKeyDown(arg0, arg1);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if(!onClickFinish){
            UmtvManager.getInstance().getAtvChannel().exitScan();
        }
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SCAN_PROGRESS, mOnChannelScanListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SCAN_LOCK, mOnChannelScanListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SCAN_FINISH, mOnChannelScanListener);
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    /**
     * handler of refresh all values
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_CHANNEL_NUM:
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "handleMessage------>MsgUpdateChannelNum--->"
                            + msg.arg1);
                }
                channelNumTxt.setText("" + msg.arg1);
                break;
            case MSG_UPDATE_CHANNEL_FREQ:
                int currentFreq = msg.arg1;
                DecimalFormat df = new DecimalFormat("#.00");
                float f = ((float) currentFreq) / 1000;
                freqTxt.setText(getString(R.string.all_scan_freq_rate,
                        df.format(f)));
                // mFreqRate.setText("" + currentFreq);
                if (currentFreq <= 120000)
                    freqRangeTxt.setText("VHF-L");
                else if (currentFreq <= 470000)
                    freqRangeTxt.setText("VHF-H");
                else
                    freqRangeTxt.setText("UHF");
                break;
            default:
                break;
            }
        };
    };
}
