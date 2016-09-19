
package com.um.launcher.view;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.um.launcher.MainActivity;
import com.um.launcher.R;
import com.um.launcher.interfaces.InterfaceValueMaps;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.logic.factory.LogicFactory;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;
import com.um.launcher.util.UtilLauncher;
import com.um.launcher.view.setting.CustomSettingView;
import com.um.launcher.view.setting.NetSettingDialog;

public class MainPageSetting extends RelativeLayout implements
        ShowAbleInterface, View.OnClickListener, OnFocusChangeListener {

    private static final String TAG = "MainPageSetting";
    public final static int PAGENUM = 4;
    private MainActivity mContext;
    private View[] imgView;
    // This variable is used to solve the pop-up window when
    // the MainActivity focusedView is unable to effectively control the focus
    // problem
    private int mFocusedView = 0;
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
    
    public MainPageSetting(Context context) {
        super(context);
    }

    public MainPageSetting(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.main_page_setting, this);
        initView(parent);
    }

    private void initView(View parent) {
        imgView = new View[] {
                parent.findViewById(R.id.set_item_advanced),
                parent.findViewById(R.id.set_item_pic),
                parent.findViewById(R.id.set_item_sound),
                parent.findViewById(R.id.set_item_appmanage),
                parent.findViewById(R.id.set_item_net),
                parent.findViewById(R.id.set_item_powersaving),
                parent.findViewById(R.id.set_item_systeminfo)//,
                //parent.findViewById(R.id.set_item_help)
        };

        for (int i = 0; i < imgView.length; i++) {
            imgView[i].setOnClickListener(this);
            imgView[i].getBackground().setAlpha(0);
            imgView[i].setOnFocusChangeListener(this);
        }
        
        mImageViewReflect = new ImageView[]{
        		(ImageView)findViewById(R.id.set_appmanage_img_reflect),
        		(ImageView)findViewById(R.id.set_advanced_img_reflect),
        		(ImageView)findViewById(R.id.set_powersaving_img_reflect),
        		(ImageView)findViewById(R.id.set_systeminfo_img_reflect),	
        };
        
        mTextView = new TextView[]{
                (TextView)parent.findViewById(R.id.set_advance_txt),
                (TextView)parent.findViewById(R.id.set_pic_txt),
                (TextView)parent.findViewById(R.id.set_sound_txt),
                (TextView)parent.findViewById(R.id.set_appmanage_txt),
                (TextView)parent.findViewById(R.id.set_net_txt),
                (TextView)parent.findViewById(R.id.set_powersaving_txt),
                (TextView)parent.findViewById(R.id.set_systeminfo_txt)
        };
        
        for (int i = 0; i < mTextView.length; i++)
        {
        	mTextView[i].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0)); 
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (imgView[Constant.NUMBER_0].hasFocus()
                    || imgView[Constant.NUMBER_4].hasFocus()) {
                mContext.snapToPreScreen();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (imgView[Constant.NUMBER_3].hasFocus()) {
                mContext.snapToNextScreen();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (imgView[Constant.NUMBER_3].hasFocus()
                    || imgView[Constant.NUMBER_4].hasFocus()
                    || imgView[Constant.NUMBER_5].hasFocus()
                    || imgView[Constant.NUMBER_6].hasFocus()) {
                RelativeLayout[] tagList = mContext.getTagView().getTagList();
                tagList[mContext.getFocusedPage()].requestFocus();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (imgView[0].hasFocus() || imgView[1].hasFocus()
                    || imgView[Constant.NUMBER_2].hasFocus()
                    || imgView[Constant.NUMBER_3].hasFocus()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void isShow() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "Now isShow--->" + PAGENUM);
        }
        reflectImageShow();
        
        if (MainActivity.isChangeLocale) {
            imgView[Constant.NUMBER_4].requestFocus();
        } else if (!mContext.getTagView().hasFocus()) {
            if (mContext.isSnapLeft()) {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_3].requestFocus();
                } else {
                    imgView[Constant.NUMBER_3].requestFocus();
                }
            } else {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_0].requestFocus();
                } else {
                    imgView[Constant.NUMBER_4].requestFocus();
                }
            }
        }
        MainActivity.isChangeLocale = false;
        mContext.getTagView().setViewOnSelectChange(PAGENUM);
    }

    public int getId() {
        return PAGENUM;
    }

    @Override
    public View[] getImgViews() {
        return imgView;
    }

    @Override
    public void onClick(View v) {
    	
    	 for (int i = 0; i < imgView.length; i++) {
             if (imgView[i] == v) {
                 try {
                     String pkg = thridPkg[i].trim();
                     String cls = thridCls[i].trim();
                     ComponentName componentName = new ComponentName(pkg, cls);
                     Intent mIntent = new Intent();
                     mIntent.setComponent(componentName);
                     mContext.startActivity(mIntent);
                     startActivityDo(v);
                 } catch (Exception e) {
                     Util.appNoSupportPrompt(mContext, R.string.app_no_support);
                     e.printStackTrace();
                 }
             }
         }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "onFocusChange---");
        }
        if (hasFocus) {
            v.bringToFront();
            v.animate().scaleX(1.2f).scaleY(1.2f)
                    .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            v.getBackground().setAlpha(255);
            // Set the flag and focus related position
            switch (v.getId()) {
                case R.id.set_item_advanced:
                    mFocusedView = Constant.NUMBER_0;
                    mContext.setFocusedView(Constant.NUMBER_0);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_0].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.set_item_pic:
                    mFocusedView = Constant.NUMBER_1;
                    mContext.setFocusedView(Constant.NUMBER_1);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_1].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.set_item_sound:
                    mFocusedView = Constant.NUMBER_2;
                    mContext.setFocusedView(Constant.NUMBER_2);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_2].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.set_item_appmanage:
                    mFocusedView = Constant.NUMBER_3;
                    mContext.setFocusedView(Constant.NUMBER_3);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.set_item_net:
                    mFocusedView = Constant.NUMBER_4;
                    mContext.setFocusedView(Constant.NUMBER_4);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_4].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.set_item_powersaving:
                    mFocusedView = Constant.NUMBER_5;
                    mContext.setFocusedView(Constant.NUMBER_5);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_5].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.set_item_systeminfo:
                    mFocusedView = Constant.NUMBER_6;
                    mContext.setFocusedView(Constant.NUMBER_6);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_6].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                default:
                    break;
            }
        } else {
            v.getBackground().setAlpha(0);
            v.animate().scaleX(1.0f).scaleY(1.0f)
                    .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            
            switch (v.getId()) {
            case R.id.set_item_advanced:
                mTextView[Constant.NUMBER_0].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.set_item_pic:
            	mTextView[Constant.NUMBER_1].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.set_item_sound:
            	mTextView[Constant.NUMBER_2].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.set_item_appmanage:
            	mTextView[Constant.NUMBER_3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.set_item_net:
            	mTextView[Constant.NUMBER_4].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.set_item_powersaving:
            	mTextView[Constant.NUMBER_5].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.set_item_systeminfo:
            	mTextView[Constant.NUMBER_6].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            default:
                break;
            }
        }
    }
    
    public void reflectImageShow(){
    	if (mImageViewReflect[0].getDrawable() == null)
    	{
        	Bitmap resizeBitmap1 = Util.createReflectedImage(mContext, imgView[3], 6);
        	if (resizeBitmap1 != null)
        		mImageViewReflect[0].setImageBitmap(resizeBitmap1);
    	}
    	
    	if (mImageViewReflect[1].getDrawable() == null)
    	{
            Bitmap resizeBitmap2 = Util.createReflectedImage(mContext, imgView[4], 3);
            if (resizeBitmap2 != null)
            	mImageViewReflect[1].setImageBitmap(resizeBitmap2);
    	}
    	
    	if (mImageViewReflect[2].getDrawable() == null)
    	{
            Bitmap resizeBitmap3 = Util.createReflectedImage(mContext, imgView[5], 3);
            if (resizeBitmap3 != null)
            	mImageViewReflect[2].setImageBitmap(resizeBitmap3);
    	}
    	
    	if (mImageViewReflect[3].getDrawable() == null)
    	{
            Bitmap resizeBitmap4 = Util.createReflectedImage(mContext, imgView[6], 3);
            if (resizeBitmap4 != null)
            	mImageViewReflect[3].setImageBitmap(resizeBitmap4);
    	}
    }

	@Override
	public void computeScroll() {
		reflectImageShow();
	}
	
	private void startActivityDo(View v){
		if (v.getId() == R.id.set_item_pic){
			Log.d(TAG,"leon: startActivityDo");
			int flag = MainActivity.ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE | MainActivity.ENTER_ACTIVITY_INVISIBLE_VIEW;
			mContext.setEnterActivityFlag(flag);
		}
	}
}
