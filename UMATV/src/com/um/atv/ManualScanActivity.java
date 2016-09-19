package com.um.atv;

import java.text.DecimalFormat;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.listener.OnChannelScanListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.vo.ChannelScanInfo;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.um.atv.R;
import com.um.atv.interfaces.ATVChannelInterface;
import com.um.atv.util.Constant;

/**
 * ManualScanActivity
 *
 * @author wangchuanjian
 *
 */
public class ManualScanActivity extends Activity implements OnKeyListener,
        OnFocusChangeListener, OnClickListener {
    protected static final String TAG = "ManualScanActivity";
	// message of finish activity
    private static final int ACTIVITY_FINISH = 0;
    // image of left
    private ImageView manualLeftImg;
    // image of right
    private ImageView manualRightImg;
    // text of channel
    private TextView channelTxt = null;
	// text of save channel
    private EditText saveChannelTxt = null;
    // text of frequency
    private TextView freqTxt = null;
    // seekBar of manualScan
    private SeekBar manualSeekbar;
    // button of exit
    private Button manualExitBtn;
	// button of save
    private Button manualSaveBtn;
    // layout of manualScan
    private RelativeLayout manuaLayout;

    private TextView mFreqBand = null;
    // min value of frequency
    private int mMinFreq = 0;
    // max value of frequency
    private int mMaxFreq = 0;
    // current value of frequency
    private int mCurFreq = 0;
    // -1 mean unknow 0 mean left 1 mean rigth
   private int Scan_Direction = -1;
    /**
     * listener of channel scan
     */
    OnChannelScanListener mOnChannelScanListener = new OnChannelScanListener() {

        @Override
        public void onChannelScanFinish() {
        }

        @Override
        public void onChannelScanStart() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onChannelScanLock(TvProgram arg0) {
            // TODO Auto-generated method stub
        	delay();
            Log.d(TAG, "onChannelScanLock()----->program:" + arg0.getStrName()
                    + "   freq:" + arg0.getlFreq());
        }

        @Override
        public void onChannelScanProgress(ChannelScanInfo arg0) {
            // TODO Auto-generated method stub
            Log.d(TAG,
                    "onChannelScanProgress--->currentFreq:"
                            + arg0.getCurrFreq());
            manualSeekbar.setProgress(arg0.getCurrFreq() - mMinFreq);
            DecimalFormat df = new DecimalFormat("#.00");
            float f = ((float) arg0.getCurrFreq()) / 1000;
            freqTxt.setText(getString(R.string.all_scan_freq_rate,df.format(f)));
            showFreqBand(arg0.getCurrFreq());
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_scan);
        mMaxFreq = ATVChannelInterface.getMaxTuneFreq();
        mMinFreq = ATVChannelInterface.getMinTuneFreq();

        if (ATVChannelInterface.getAvailProgCount() == 0) {
            mCurFreq = mMinFreq;
        } else {
            mCurFreq = ATVChannelInterface.getCurrentFrequency();
        }
        
        channelTxt = (TextView) findViewById(R.id.channel_value_txt);
        channelTxt.setText("" + ATVChannelInterface.getCurProgNumber());

        saveChannelTxt = (EditText) findViewById(R.id.edit_channel_num_edittext);
        saveChannelTxt.setInputType(InputType.TYPE_NULL);
        saveChannelTxt.setText("" + ATVChannelInterface.getCurProgNumber());
        saveChannelTxt.setOnFocusChangeListener(this);
        saveChannelTxt.setOnKeyListener(this);
        
        freqTxt = (TextView) findViewById(R.id.freq_value_txt);

        DecimalFormat df = new DecimalFormat("#.00");
        float f = ((float) mCurFreq) / 1000;
        freqTxt.setText(getString(R.string.all_scan_freq_rate,df.format(f)));
        mFreqBand = (TextView)findViewById(R.id.band_value_txt);
        showFreqBand(mCurFreq);
        initView();
        manualSeekbar.setMax(mMaxFreq - mMinFreq);
        manualSeekbar.setProgress(mCurFreq - mMinFreq);
        
        delay();
    }

    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SCAN_PROGRESS, mOnChannelScanListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SCAN_LOCK, mOnChannelScanListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SCAN_FINISH, mOnChannelScanListener);
    	super.onResume();
    }
    
    /**
     * initView widget
     */
    public void initView() {
        manualLeftImg = (ImageView) findViewById(R.id.manual_left_img);
        manualRightImg = (ImageView) findViewById(R.id.manual_right_img);
        manualSeekbar = (SeekBar) findViewById(R.id.manual_seekbar);
        manualSeekbar.setOnKeyListener(this);
        manualSeekbar.setOnFocusChangeListener(this);
        manualExitBtn = (Button) findViewById(R.id.manual_exit);
        manualExitBtn.setOnClickListener(this);
        manualSaveBtn = (Button) findViewById(R.id.manual_save);
        manualSaveBtn.setOnClickListener(this);
        manuaLayout = (RelativeLayout) findViewById(R.id.manual_layout);
        manuaLayout.setGravity(1);
        manualLeftImg.setVisibility(View.VISIBLE);
        manualRightImg.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_MENU:	
	    	  new Thread() {
	    		   public void run() {
	    		    try {
	    		     Instrumentation inst = new Instrumentation();
	    		     inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
	    		    } catch (Exception e) {
	    		     Log.i(TAG,"Exception when sendKeyDownUpSync e="+e.toString());
	    		    }
	    		   }
	    		  }.start();
            break;
        case KeyEvent.KEYCODE_HOME:
        case KeyEvent.KEYCODE_SOURCE:
        case KeyEvent.KEY_USB:
        case KeyEvent.KEY_APPLICATION:
        case KeyEvent.KEYCODE_TV:
            return true;
   	    case KeyEvent.KEY_SOURCEENTER:	
	    	    Log.i(TAG,"KEY_SOURCEENTER is clicked!");
	    	    Log.i(TAG,"saveChannelTxt.isFocused()="+saveChannelTxt.isFocused());
            if(manualSaveBtn.isFocused()){
            	manualSave();
            }
            if(manualExitBtn.isFocused()){
                finish();
            }
            if(saveChannelTxt.isFocused()){
        		saveChannelTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);	        		
    			delay();  
            }
	    return true;   	    
        default:
            break;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override
    protected void onPause(){
    	ATVChannelInterface.exitManualScanProgram();
    	ATVChannelInterface.clearManualScanProgram();
        ATVChannelInterface.endManualScan();
        ATVChannelInterface.exitScan();
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SCAN_PROGRESS, mOnChannelScanListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SCAN_LOCK, mOnChannelScanListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SCAN_FINISH, mOnChannelScanListener);
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onKey(View v, int keycode, KeyEvent event) {
        switch (v.getId()) {
        case R.id.manual_seekbar:
            if (event.getAction() == KeyEvent.ACTION_DOWN) {

                switch (keycode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:

                    manualLeftImg
                            .setBackgroundResource(R.drawable.selector_arrow_left_blue);
                    manualRightImg
                            .setBackgroundResource(R.drawable.selector_arrow_right);


                    ATVChannelInterface.manualScan(false);
                   Scan_Direction = 0;
                   mHandler.removeMessages(ACTIVITY_FINISH);
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:

                    manualLeftImg
                            .setBackgroundResource(R.drawable.selector_arrow_left);
                    manualRightImg
                            .setBackgroundResource(R.drawable.selector_arrow_right_blue);
                    ATVChannelInterface.manualScan(true);
                   Scan_Direction = 1;
                   mHandler.removeMessages(ACTIVITY_FINISH);
                    return true;
                default:
                    break;
                }
            }
            break;
        case R.id.edit_channel_num_edittext:
        	if (event.getAction() == KeyEvent.ACTION_DOWN) {
        		switch (keycode) {
        		case KeyEvent.KEYCODE_DPAD_CENTER:
        			saveChannelTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        			//delay();
        			mHandler.removeMessages(ACTIVITY_FINISH);
        			break;
        		case KeyEvent.KEYCODE_0:
        		case KeyEvent.KEYCODE_1:
        		case KeyEvent.KEYCODE_2:
        		case KeyEvent.KEYCODE_3:
        		case KeyEvent.KEYCODE_4:
        		case KeyEvent.KEYCODE_5:
        		case KeyEvent.KEYCODE_6:
        		case KeyEvent.KEYCODE_7:
        		case KeyEvent.KEYCODE_8:
        		case KeyEvent.KEYCODE_9:
        			delay();
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
    	delay();
        switch (v.getId()) {
        case R.id.edit_channel_num_edittext:
            if (!hasFocus)
            {
            	int prognum = 0;
            	prognum = Integer.valueOf(saveChannelTxt.getText().toString());
            	if (prognum <= 0 || prognum > 235)
            	{
            		Toast.makeText(ManualScanActivity.this, R.string.change_invalid_number, 1000).show();
            		saveChannelTxt.setText(""+235);
            	}
            }
            else{
            	mHandler.removeMessages(ACTIVITY_FINISH);
            }
            break;

        default:
            break;
        }
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.manual_exit:
            finish();
            break;
        case R.id.manual_save:
        	manualSave();
        	break;
        default:
            break;
        }
    }

	private void manualSave() {
		int prognum = 0;
		prognum = Integer.valueOf(saveChannelTxt.getText().toString());
		if (prognum > 0 && prognum <= 235)
		{
			if (0 == ATVChannelInterface.saveManualScanProgram(prognum))
			{
				Toast.makeText(ManualScanActivity.this, R.string.save_success, 1000).show();
			}
			else
			{
				Toast.makeText(ManualScanActivity.this, R.string.save_failure, 1000).show();
			}
		}
		else
		{
			Toast.makeText(ManualScanActivity.this, R.string.invalid_number, 1000).show();
		}
	}


    protected void showFreqBand(int freqvalue) {
        if (freqvalue <= 120000)
            mFreqBand.setText("VHF-L");
        else if (freqvalue <= 470000)
            mFreqBand.setText("VHF-H");
        else
            mFreqBand.setText("UHF");
    }
    
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
    
    /**
     * if do nothing in 30s,activity finish.
     */
    public void delay() {
        mHandler.removeMessages(ACTIVITY_FINISH);
        mHandler.sendEmptyMessageDelayed(ACTIVITY_FINISH,
                Constant.DISPEAR_TIME_30s);
    }
}
