package cn.com.unionman.umtvsetting.umsettingmenu;



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainLayout extends RelativeLayout implements
View.OnClickListener, OnFocusChangeListener{
    private static final String TAG = "MainLayout";
	
    private View[] imgView;
    private View[] imgFocusView;
    public TextView[] mTextView;
    public ImageView[] mImageViewReflect;
    //The first interface package name
    private String[] thridPkg = new String[] {
            "cn.com.unionman.umtvsetting.system",//sysset
            "cn.com.unionman.umtvsetting.picture",//pic
            "cn.com.unionman.umtvsetting.sound",//sound
            "cn.com.unionman.umtvsetting.appmanage",//appmanage
            "com.unionman.netsetup",//net
            "cn.com.unionman.umtvsetting.powersave",//powersaving
            "cn.com.unionman.umtvsetting.systeminfo",//sysinfo
    };

    // The main Activity in the first interface
    private String[] thridCls = new String[] {
            "cn.com.unionman.umtvsetting.system.SysSettingMainActivity",//sysset
            "cn.com.unionman.umtvsetting.picture.PicMainActivity",//pic
            "cn.com.unionman.umtvsetting.sound.SoundMainActivity", //sound
            "cn.com.unionman.umtvsetting.appmanage.AppManageMainActivity",//appmanage
            "com.unionman.netsetup.MainActivity",//net
            "cn.com.unionman.umtvsetting.powersave.PowerMainActivity",//powersaving
            "cn.com.unionman.umtvsetting.systeminfo.MainActivity",//sysinfo
    };

    private Context mContext;
    private Handler mhandler;
    
	public MainLayout(Context context,Handler handler) {
		super(context);
        this.mContext =  context;
        this.mhandler =  handler;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.main_page, this);
	     initView(parent);  	
	}
    private void initView(View parent) {
        imgView = new View[] {
                parent.findViewById(R.id.set_item_net),
                parent.findViewById(R.id.set_item_pic),
                parent.findViewById(R.id.set_item_sound),
             /*   parent.findViewById(R.id.set_item_appmanage),*/
                parent.findViewById(R.id.set_item_advanced),
                parent.findViewById(R.id.set_item_powersaving),
                parent.findViewById(R.id.set_item_systeminfo)//,
                //parent.findViewById(R.id.set_item_help)
        };
        
        imgFocusView = new View[] {
                findViewById(R.id.set_net_img_focus),
                findViewById(R.id.set_pic_img_focus),
                 findViewById(R.id.set_sound_img_focus),
             /*   findViewById(R.id.set_appmanage_img_focus),*/
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
        	/*	(ImageView)findViewById(R.id.set_appmanage_img_reflect),*/
        		(ImageView)findViewById(R.id.set_advanced_img_reflect),
        		(ImageView)findViewById(R.id.set_powersaving_img_reflect),
        		(ImageView)findViewById(R.id.set_systeminfo_img_reflect),	
        };
        
        mTextView = new TextView[]{
                (TextView)parent.findViewById(R.id.set_net_txt),
                (TextView)parent.findViewById(R.id.set_pic_txt),
                (TextView)parent.findViewById(R.id.set_sound_txt),
/*                (TextView)parent.findViewById(R.id.set_appmanage_txt),*/
                (TextView)parent.findViewById(R.id.set_advance_txt),
                (TextView)parent.findViewById(R.id.set_powersaving_txt),
                (TextView)parent.findViewById(R.id.set_systeminfo_txt)
        };
        
        for (int i = 0; i < mTextView.length; i++)
        {
        	mTextView[i].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0)); 
        }
    }
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "onFocusChange---");
        }
        if (hasFocus) {
            /*v.bringToFront();
            v.animate().scaleX(1.0f).scaleY(1.0f)
                    .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            v.getBackground().setAlpha(255);*/
            // Set the flag and focus related position
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
                /*case R.id.set_item_appmanage:
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
        } else {
            /*v.getBackground().setAlpha(0);
            v.animate().scaleX(1.0f).scaleY(1.0f)
                    .setDuration(Constant.SCARE_ANIMATION_DURATION).start();*/
            
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
        }
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
   	 for (int i = 0; i < imgView.length; i++) {
         if (imgView[i] == v) {
             try {
                 String pkg = thridPkg[i].trim();
                 String cls = thridCls[i].trim();
                 ComponentName componentName = new ComponentName(pkg, cls);
                 Intent mIntent = new Intent();
                 mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 mIntent.setComponent(componentName);
                 mContext.startActivity(mIntent);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }
	}
	
/*	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.set_item_net:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn = new ComponentName("com.unionman.netsetup", "com.unionman.netsetup.MainActivity");            
			intent.setComponent(cn);			
			mContext.startActivity(intent);
			setVisibility(View.INVISIBLE);
			break;
		case R.id.set_item_pic:
//			Intent intent_pic = new Intent(Intent.ACTION_MAIN);
//			intent_pic.addCategory(Intent.CATEGORY_LAUNCHER);            
//			ComponentName cn_pic = new ComponentName("cn.com.unionman.umtvsetting.picture", "cn.com.unionman.umtvsetting.picture.PicMainActivity");            
//			intent_pic.setComponent(cn_pic);
//			startActivity(intent_pic);
			Intent intent_pic = new Intent("cn.com.unionman.umtvsetting.picture.service.ACTION");
			intent_pic.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startService(intent_pic);
			break;
		case R.id.set_item_sound:
//			Intent intent_voice = new Intent(Intent.ACTION_MAIN);
//			intent_voice.addCategory(Intent.CATEGORY_LAUNCHER);
//			intent_voice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			ComponentName cn_voice = new ComponentName("cn.com.unionman.umtvsetting.sound", "cn.com.unionman.umtvsetting.sound.SoundMainActivity");            
//			intent_voice.setComponent(cn_voice);
//			mContext.startActivity(intent_voice);
//			setVisibility(View.INVISIBLE);
			Intent intent_voice = new Intent("cn.com.unionman.umtvsetting.sound.service.ACTION");
			intent_voice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startService(intent_voice);	
			break;
		case R.id.set_item_powersaving:
			Intent intent_energy = new Intent(Intent.ACTION_MAIN);
			intent_energy.addCategory(Intent.CATEGORY_LAUNCHER);  
			intent_energy.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn_energy = new ComponentName("cn.com.unionman.umtvsetting.powersave", "cn.com.unionman.umtvsetting.powersave.PowerMainActivity");            
			intent_energy.setComponent(cn_energy);
			mContext.startActivity(intent_energy);
				Message msg = new Message();
				msg.what=0;
				mhandler.sendMessage(msg);
			break;
		case R.id.set_item_advanced:
			Intent intent_settings = new Intent(Intent.ACTION_MAIN);
			intent_settings.addCategory(Intent.CATEGORY_LAUNCHER); 
			intent_settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn_settings = new ComponentName("cn.com.unionman.umtvsetting.system", "cn.com.unionman.umtvsetting.system.SysSettingMainActivity");            
			intent_settings.setComponent(cn_settings);
			mContext.startActivity(intent_settings);
				Message msg_advanced = new Message();
				msg_advanced.what=0;
				mhandler.sendMessage(msg_advanced);
//			Intent intent_settings = new Intent("cn.com.unionman.umtvsetting.system.SysSettingService");
//			intent_settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			mContext.startService(intent_settings);
			break;
		case R.id.set_item_systeminfo:
			Intent intent_aboutme = new Intent(Intent.ACTION_MAIN);
			intent_aboutme.addCategory(Intent.CATEGORY_LAUNCHER); 
			intent_aboutme.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn_aboutme = new ComponentName("cn.com.unionman.umtvsetting.systeminfo", "cn.com.unionman.umtvsetting.systeminfo.MainActivity");            
			intent_aboutme.setComponent(cn_aboutme);
			mContext.startActivity(intent_aboutme);
				Message msg_info = new Message();
				msg_info.what=0;
				mhandler.sendMessage(msg_info);
//			Intent intent_aboutme = new Intent("cn.com.unionman.umtvsetting.systeminfo.netsetting");
//			intent_aboutme.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			mContext.startService(intent_aboutme);
			break;
		case R.id.set_item_appmanage:
			Intent intent_apk = new Intent(Intent.ACTION_MAIN);
			intent_apk.addCategory(Intent.CATEGORY_LAUNCHER); 
			intent_apk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn_apk = new ComponentName("cn.com.unionman.umtvsetting.appmanage", "cn.com.unionman.umtvsetting.appmanage.AppManageMainActivity");            
			intent_apk.setComponent(cn_apk);
			mContext.startActivity(intent_apk);
			setVisibility(View.INVISIBLE);
			break;
		}
		
	}*/
	
}
