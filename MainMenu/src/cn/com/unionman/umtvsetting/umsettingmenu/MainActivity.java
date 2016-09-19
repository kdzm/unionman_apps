package cn.com.unionman.umtvsetting.umsettingmenu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnFocusChangeListener, OnClickListener {
	private static final String TAG = "MainActivity";

	private View[] imgView;
	private View[] imgFocusView;
	public TextView[] mTextView;
	public ImageView[] mImageViewReflect;
   
   private static final String PIC_SET_FINISH_ACTION = "cn.com.unionman.picture.finish";

private static final int ACTIVITY_FINISH = 1;

private static final long DISPEAR_TIME_30s = 30000;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_page);		
	     initView();  
	}
	
    private void initView() {
        imgView = new View[] {
               findViewById(R.id.set_item_net),
               findViewById(R.id.set_item_pic),
                findViewById(R.id.set_item_sound),
               findViewById(R.id.set_item_advanced),
                findViewById(R.id.set_item_powersaving),
                findViewById(R.id.set_item_systeminfo)//,
                //parent.findViewById(R.id.set_item_help)
        };
        
        imgFocusView = new View[] {
                findViewById(R.id.set_net_img_focus),
                findViewById(R.id.set_pic_img_focus),
                 findViewById(R.id.set_sound_img_focus),
           
                findViewById(R.id.set_advance_img_focus),
                 findViewById(R.id.set_powersaving_img_focus),
                 findViewById(R.id.set_systeminfo_img_focus)//,
                 //parent.findViewById(R.id.set_item_help)
         };

        for (int i = 0; i < imgView.length; i++) {
            imgView[i].setOnClickListener(this);
            imgFocusView[i].getBackground().setAlpha(0);
            imgView[i].setOnFocusChangeListener(this);
        }
        
        mImageViewReflect = new ImageView[]{
        	
        		(ImageView)findViewById(R.id.set_advanced_img_reflect),
        		(ImageView)findViewById(R.id.set_powersaving_img_reflect),
        		(ImageView)findViewById(R.id.set_systeminfo_img_reflect),	
        };
        
        mTextView = new TextView[]{
                (TextView)findViewById(R.id.set_net_txt),
                (TextView)findViewById(R.id.set_pic_txt),
                (TextView)findViewById(R.id.set_sound_txt),
  
                (TextView)findViewById(R.id.set_advance_txt),
                (TextView)findViewById(R.id.set_powersaving_txt),
                (TextView)findViewById(R.id.set_systeminfo_txt)
        };
        
        for (int i = 0; i < mTextView.length; i++)
        {
        	mTextView[i].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0)); 
        }
    }
	
	 private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {  
	        @Override  
	        public void onReceive(Context context, Intent intent) {  
	            if(intent.getAction().equals(PIC_SET_FINISH_ACTION)){  
	                setVisible(true);
	            }  
	        }  
	 };  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "onFocusChange---");
        }
        if (hasFocus) {
			v.bringToFront();	
            switch (v.getId()) {
                case R.id.set_item_net:
                    mTextView[Constant.NUMBER_0].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    imgFocusView[Constant.NUMBER_0].getBackground().setAlpha(255);
                    break;
                case R.id.set_item_pic:
                    mTextView[Constant.NUMBER_1].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    imgFocusView[Constant.NUMBER_1].getBackground().setAlpha(255);
                    break;
                case R.id.set_item_sound:     
                    mTextView[Constant.NUMBER_2].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    imgFocusView[Constant.NUMBER_2].getBackground().setAlpha(255);
                    break;
              /*  case R.id.set_item_appmanage:
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    imgFocusView[Constant.NUMBER_3].getBackground().setAlpha(255);
                    break;*/
                case R.id.set_item_advanced:
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    imgFocusView[Constant.NUMBER_3].getBackground().setAlpha(255);
                    break;
                case R.id.set_item_powersaving:
                    mTextView[Constant.NUMBER_4].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    imgFocusView[Constant.NUMBER_4].getBackground().setAlpha(255);
                    break;
                case R.id.set_item_systeminfo:
                    mTextView[Constant.NUMBER_5].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    imgFocusView[Constant.NUMBER_5].getBackground().setAlpha(255);
                    break;
                default:
                    break;
            }
			v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start();
        } else {            
            switch (v.getId()) {
            case R.id.set_item_net:
                mTextView[Constant.NUMBER_0].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                imgFocusView[Constant.NUMBER_0].getBackground().setAlpha(0);
                break;
            case R.id.set_item_pic:
            	mTextView[Constant.NUMBER_1].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            	imgFocusView[Constant.NUMBER_1].getBackground().setAlpha(0);
                break;
            case R.id.set_item_sound:
            	mTextView[Constant.NUMBER_2].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            	imgFocusView[Constant.NUMBER_2].getBackground().setAlpha(0);
                break;
           /* case R.id.set_item_appmanage:
            	mTextView[Constant.NUMBER_3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            	imgFocusView[Constant.NUMBER_3].getBackground().setAlpha(0);
                break;*/
            case R.id.set_item_advanced:
            	mTextView[Constant.NUMBER_3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            	imgFocusView[Constant.NUMBER_3].getBackground().setAlpha(0);
                break;
            case R.id.set_item_powersaving:
            	mTextView[Constant.NUMBER_4].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            	imgFocusView[Constant.NUMBER_4].getBackground().setAlpha(0);
                break;
            case R.id.set_item_systeminfo:
            	mTextView[Constant.NUMBER_5].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            	imgFocusView[Constant.NUMBER_5].getBackground().setAlpha(0);
                break;
            default:
                break;
            }
        	v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
        }
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.set_item_net:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);            
			ComponentName cn = new ComponentName("com.unionman.netsetup", "com.unionman.netsetup.MainActivity");            
			intent.setComponent(cn);
			startActivity(intent);
			break;
		case R.id.set_item_pic:
//			Intent intent_pic = new Intent(Intent.ACTION_MAIN);
//			intent_pic.addCategory(Intent.CATEGORY_LAUNCHER);   
//			intent_pic.putExtra("isInSettingWizard", false);
//			ComponentName cn_pic = new ComponentName("com.unionman.dvbcitysetting", "com.unionman.dvbcitysetting.CitySettingActivity");            
//			intent_pic.setComponent(cn_pic);
//			startActivity(intent_pic);将城市设置更换为软件升级。
			Intent intent_upgrade = new Intent();
			intent_upgrade.setClassName("cn.com.unionman.umtvsetting.umsysteminfo",
					"cn.com.unionman.umtvsetting.umsysteminfo.UpgradeMainActivity");
			startActivity(intent_upgrade);
			break;
		/*case R.id.set_item_sound:
			Intent intent_voice = new Intent("cn.com.unionman.umtvsetting.sound.service.ACTION");
			startService(intent_voice);	
//			Toast.makeText(MainActivity.this, getResources().getString(R.string.not_support), 1).show();
			Intent intent_lock = new Intent(Intent.ACTION_MAIN);
			intent_lock.addCategory(Intent.CATEGORY_LAUNCHER);            
			ComponentName cn_lock = new ComponentName("com.um.umpwdlock", "com.um.umpwdlock.PwdLockMainActivity");            
			intent_lock.setComponent(cn_lock);
			startActivity(intent_lock);
			break;*/
		case R.id.set_item_powersaving:
			Intent intent_energy = new Intent(Intent.ACTION_MAIN);
			intent_energy.addCategory(Intent.CATEGORY_LAUNCHER);            
			ComponentName cn_energy = new ComponentName("cn.com.unionman.umtvsetting.powersave", "cn.com.unionman.umtvsetting.powersave.PowerMainActivity");            
			intent_energy.setComponent(cn_energy);
			startActivity(intent_energy);
			break;
		case R.id.set_item_advanced:
			Intent intent_settings = new Intent(Intent.ACTION_MAIN);
			intent_settings.addCategory(Intent.CATEGORY_LAUNCHER);            
			ComponentName cn_settings = new ComponentName("cn.com.unionman.umtvsetting.system", "cn.com.unionman.umtvsetting.system.SysSettingMainActivity");            
			intent_settings.setComponent(cn_settings);
			startActivity(intent_settings);
			break;
		case R.id.set_item_systeminfo:
			Intent intent_aboutme = new Intent(Intent.ACTION_MAIN);
			intent_aboutme.addCategory(Intent.CATEGORY_LAUNCHER);            
			ComponentName cn_aboutme = new ComponentName("cn.com.unionman.umtvsetting.umsysteminfo", "cn.com.unionman.umtvsetting.umsysteminfo.MainActivity");            
			intent_aboutme.setComponent(cn_aboutme);
			startActivity(intent_aboutme);
			break;
		case R.id.set_item_sound:
			Intent intent_apk = new Intent(Intent.ACTION_MAIN);
			intent_apk.addCategory(Intent.CATEGORY_LAUNCHER);            
			ComponentName cn_apk = new ComponentName("cn.com.unionman.umtvsetting.appmanage", "cn.com.unionman.umtvsetting.appmanage.AppManageMainActivity");            
			intent_apk.setComponent(cn_apk);
			startActivity(intent_apk);
			break;
		}
		
	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            delay();
        } else {
            finishHandle.removeMessages(ACTIVITY_FINISH);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * handler of finish activity
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ACTIVITY_FINISH)
                finish();
        };
    };

    /**
     * set delay time to finish activity
     */
    public void delay() {
        finishHandle.removeMessages(ACTIVITY_FINISH);
        Message message = new Message();
        message.what = ACTIVITY_FINISH;
        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {  
			delay();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
