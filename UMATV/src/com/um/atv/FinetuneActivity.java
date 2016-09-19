package com.um.atv;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.um.atv.R;
import com.um.atv.interfaces.ATVChannelInterface;
import com.um.atv.util.Constant;

/**
 * FinetuneActivity
 *
 * @author wangchuanjian
 *
 */
public class FinetuneActivity extends Activity implements OnKeyListener,
        OnFocusChangeListener, OnClickListener {
    protected static final String TAG = "FinetuneActivity";
    private static final int TUNER_STEP = 50;
    // message of finish activity
    private static final int ACTIVITY_FINISH = 0;
    // seekBar of fineTune
    private SeekBar fineTuneSeekbar;
    private TextView curFreqTxt, curProgTxt, curBandTxt;
    // image of fine left
    private ImageView fineLeftImg;
    // current value of progress
	private int mCurrentProgress = 0;
	// image of fine right
    private ImageView fineRightImg;
    // butTon of exit fineTune
    private Button exitFinetuneBtn;
	// butTon of save fineTune
    private Button saveFinetuneBtn;
    // current frequency number
    private int mCurrentFreq = 0;

    private int mOldFreq = 0;
    // max value of frequency
    private int mMaxFreq = 0;
    // min value of frequency
    private int mMinFreq = 0;
	// max value of frequency setting
    private int SettingMaxFreq = 0;
	// min value of frequency setting
    private int SettingMinFreq = 0;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ACTIVITY_FINISH:
                Log.d("ACTIVITY_FINISH", ACTIVITY_FINISH + "");
                finish();
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fine_tune);
        initView();

        mMaxFreq = ATVChannelInterface.getMaxTuneFreq();
        mMinFreq = ATVChannelInterface.getMinTuneFreq();

        if (ATVChannelInterface.getAvailProgCount() == 0) {
            Log.d(TAG, "AvailProgCount is null");
            mCurrentFreq = mMinFreq;
        }else{

            mCurrentFreq = ATVChannelInterface.getCurrentFrequency();
        }
        mOldFreq = mCurrentFreq;
        //����Χ������500KHZ
        SettingMaxFreq = (mCurrentFreq + 10*TUNER_STEP);
        SettingMinFreq = (mCurrentFreq - 10*TUNER_STEP);
        
        if (SettingMaxFreq > mMaxFreq)
        {
        	SettingMaxFreq = mMaxFreq;
        }
        
        if (SettingMinFreq < mMinFreq)
        {
        	SettingMinFreq = mMinFreq;
        }
        
        mCurrentProgress = ATVChannelInterface.getCurProgNumber();
        showCurFreq(mCurrentFreq);
        curProgTxt.setText("" + mCurrentProgress);

        //fineTuneSeekbar.setMax(mMaxFreq - mMinFreq);
        //fineTuneSeekbar.setProgress(mCurrentFreq - mMinFreq);
        fineTuneSeekbar.setMax(SettingMaxFreq - SettingMinFreq);
        fineTuneSeekbar.setProgress(mCurrentFreq - SettingMinFreq);

        if (mCurrentFreq <= 120000)
            curBandTxt.setText("VHF-L");
        else if (mCurrentFreq <= 470000)
            curBandTxt.setText("VHF-H");
        else
            curBandTxt.setText("UHF");
        
        delay();
    }

    /**
     * The initialization of all views
     */
    private void initView() {
        fineTuneSeekbar = (SeekBar) findViewById(R.id.finetune_seekbar);
        fineTuneSeekbar.setOnKeyListener(this);
        fineTuneSeekbar.setOnFocusChangeListener(this);

        curFreqTxt = (TextView) findViewById(R.id.freq_value_txt);
        curProgTxt = (TextView) findViewById(R.id.channel_value_txt);
        curBandTxt = (TextView) findViewById(R.id.band_value_txt);

        exitFinetuneBtn = (Button) findViewById(R.id.fine_exit_btn);
        //exitFinetuneBtn.setOnFocusChangeListener(this);
        exitFinetuneBtn.setOnClickListener(this);

        saveFinetuneBtn = (Button) findViewById(R.id.fine_save_btn);
        saveFinetuneBtn.setOnClickListener(this);
        
        fineLeftImg = (ImageView) findViewById(R.id.fine_left_img);
        fineRightImg = (ImageView) findViewById(R.id.fine_right_img);

    }

    @Override
    public boolean onKeyDown(int arg0, KeyEvent arg1) {
        switch (arg0) {
        case KeyEvent.KEYCODE_BACK:
        case KeyEvent.KEYCODE_MENU: 	
            finish();
            break;
        case KeyEvent.KEYCODE_HOME:
        case KeyEvent.KEYCODE_SOURCE:
            return true;
   	    case KeyEvent.KEY_SOURCEENTER:	
	    	    Log.i(TAG,"KEY_SOURCEENTER is clicked!");
            if(saveFinetuneBtn.isFocused()){
	        	mOldFreq = mCurrentFreq;
	        	Toast.makeText(FinetuneActivity.this, R.string.save_success, 1000).show();
            }
            if(exitFinetuneBtn.isFocused()){
	            finish();
	            recoverFineTune();
            }
	    return true;             
        default:
            break;
        }
        return super.onKeyDown(arg0, arg1);
    }

    /**
     * show current frequency
     *
     * @param curfreq
     */
    private void showCurFreq(int curfreq) {
        DecimalFormat df = new DecimalFormat("#.00");
        float f = ((float) curfreq) / 1000;
        curFreqTxt
                .setText(getString(R.string.all_scan_freq_rate, df.format(f)));
    }
    
    // @Override
    // protected void onDestroy() {
    // UmtvManager.getInstance().unregisterListener(TVMessage.HI_TV_EVT_SCAN_SCHED,
    // mOnChannelScanListener);
    // super.onDestroy();
    // }
    /**
     * if do nothing in 10s,activity finish.
     */
    public void delay() {
        mHandler.removeMessages(ACTIVITY_FINISH);
        mHandler.sendEmptyMessageDelayed(ACTIVITY_FINISH,
                Constant.DISPEAR_TIME_30s);
    }

    @Override
    public boolean onKey(View v, int keycode, KeyEvent event) {
        switch (v.getId()) {
        case R.id.finetune_seekbar:
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keycode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    fineLeftImg
                            .setBackgroundResource(R.drawable.selector_arrow_left_blue);
                    fineRightImg
                            .setBackgroundResource(R.drawable.selector_arrow_right);
                    if((mCurrentFreq-TUNER_STEP) < SettingMinFreq){
                        return true;
                    }
                    if(mCurrentFreq >= SettingMinFreq){
                        ATVChannelInterface.fineTune(0-TUNER_STEP);
                        mCurrentFreq = ATVChannelInterface.getCurrentFrequency();
                        Log.d(TAG, "KEYCODE_DPAD_LEFT-----mCurrentFreq ="+mCurrentFreq);
                        showCurFreq(mCurrentFreq);
                        ATVChannelInterface.enableAFT(false);
                        fineTuneSeekbar.setProgress(mCurrentFreq-SettingMinFreq);
                    }

                    delay();
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    fineLeftImg
                            .setBackgroundResource(R.drawable.selector_arrow_left);
                    fineRightImg
                            .setBackgroundResource(R.drawable.selector_arrow_right_blue);
                    if((mCurrentFreq+TUNER_STEP) > SettingMaxFreq){
                            return true;
                    }
                    if(mCurrentFreq<= SettingMaxFreq){
                        ATVChannelInterface.fineTune(TUNER_STEP);
                        mCurrentFreq = ATVChannelInterface.getCurrentFrequency();
                        Log.d(TAG, "KEYCODE_DPAD_RIGHT-----mCurrentFreq ="+mCurrentFreq);
                        showCurFreq(mCurrentFreq);
                        ATVChannelInterface.enableAFT(false);
                        fineTuneSeekbar.setProgress(mCurrentFreq-SettingMinFreq);
                    }

                    delay();
                    return true;
                default:
                    break;
                }

            }
            break;

        default:
            break;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
        case R.id.finetune_seekbar:
            delay();
            if (hasFocus) {
                //fineTuneSeekbar.setProgressDrawable(getResources().getDrawable(
                        //R.drawable.seek_bar_progress));
                fineLeftImg
                        .setBackgroundResource(R.drawable.selector_arrow_left);
                fineRightImg
                        .setBackgroundResource(R.drawable.selector_arrow_right);
            } else {
                //fineTuneSeekbar.setProgressDrawable(getResources().getDrawable(
                        //R.drawable.seek_bar_progress_focus));
                fineLeftImg
                        .setBackgroundResource(R.drawable.selector_arrow_left);
                fineRightImg
                        .setBackgroundResource(R.drawable.selector_arrow_right);
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
	        case R.id.fine_exit_btn:
	            finish();
	            recoverFineTune();
	            break;
	        case R.id.fine_save_btn:
	        	mOldFreq = mCurrentFreq;
	        	Toast.makeText(FinetuneActivity.this, R.string.save_success, 1000).show();
	        	break;
	        default:
	            break;
        }
    }
    
    private void recoverFineTune()
    {
    	if (mCurrentFreq == mOldFreq)
    	{
    		return;
    	}
    	
    	ATVChannelInterface.fineTune(mOldFreq - mCurrentFreq);
    	Toast.makeText(FinetuneActivity.this, R.string.recover_finetune, 1000).show();

    	mCurrentFreq = mOldFreq;
    }

    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	recoverFineTune();
    	super.onPause();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	recoverFineTune();
    	super.onDestroy();
    }
}
