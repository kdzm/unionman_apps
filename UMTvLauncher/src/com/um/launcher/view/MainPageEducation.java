package com.um.launcher.view;

import java.io.File;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.um.launcher.R;
import com.um.launcher.MainActivity;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;

/**
 * The first big view
 */
public class MainPageEducation extends RelativeLayout implements ShowAbleInterface,
        View.OnClickListener, OnFocusChangeListener {

    private static final String TAG = "MainPageEducation";
    public final static int PAGENUM = 1;
    private MainActivity mContext;
    public View[] imgView;
    public TextView[] mTextView;
    public ImageView[] mImageViewReflect;
    
    // The first interface package name
    private String[] firstPkg = new String[] {
            "com.chuanke.tv",//integrated
            "com.ruyou.tiantianqinzi",// children 
            "com.cpsoft.game.paopaole3d",// pple
            "com.appshare.android.ilisten.tv",// reading
            "com.mb.mayboon",// healthy
            "com.netease.vopen.tablet",// society
            "com.thtfce.web"// school
    };

    // The main Activity in the first interface
    private String[] firstCls = new String[] {
            "com.chuanke.tv.SplashActivity",//integrated
            "com.ruyou.tiantianqinzi.WelcomeActivity",//children
            "com.cpsoft.game.paopaole3d.PaopaoleActivity",//pple
            "com.appshare.android.ilisten.tv.WelcomeActivity",//reading
            "com.mb.mayboon.WelcomeVideo", //healthy
            "com.netease.vopen.tablet.activity.WelcomeActivity",//society
            "com.thtfce.web.LoadingView"//school
    };

    public MainPageEducation(Context context) {
        super(context);
    }

    public MainPageEducation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.main_page_education, this);
        initView(parent);
    }

    /**
     * Initialize current views
     */
    private void initView(View parent) {
        imgView = new View[] {
                parent.findViewById(R.id.education_item_integrated),
                parent.findViewById(R.id.education_item_children),
                parent.findViewById(R.id.education_item_pple),
                parent.findViewById(R.id.education_item_reading),
                parent.findViewById(R.id.education_item_healthy),
                parent.findViewById(R.id.education_item_society),
                parent.findViewById(R.id.education_item_tsqinghua_school)
        };

        for (int i = 0; i < imgView.length; i++) {
            imgView[i].setOnClickListener(this);
            imgView[i].getBackground().setAlpha(0);
            imgView[i].setOnFocusChangeListener(this);
        }
        
        mImageViewReflect = new ImageView[]{
        		(ImageView)parent.findViewById(R.id.education_reading_img_reflect),
        		(ImageView)parent.findViewById(R.id.education_healthy_img_reflect),
        		(ImageView)parent.findViewById(R.id.education_society_img_reflect),
        		(ImageView)parent.findViewById(R.id.education_tsqinghua_school_img_reflect),	
        };
        
        mTextView = new TextView[]{
                (TextView)parent.findViewById(R.id.education_integrated_txt),
                (TextView)parent.findViewById(R.id.education_children_txt),
                (TextView)parent.findViewById(R.id.education_pple_txt),
                (TextView)parent.findViewById(R.id.education_reading_txt),
                (TextView)parent.findViewById(R.id.education_healthy_txt),
                (TextView)parent.findViewById(R.id.education_society_txt),
                (TextView)parent.findViewById(R.id.education_tsqinghua_school_txt),	
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
                    || imgView[Constant.NUMBER_3].hasFocus()) {
                mContext.snapToPreScreen();
                super.onKeyDown(keyCode, event);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if ((imgView[Constant.NUMBER_2].hasFocus() || imgView[Constant.NUMBER_6]
                    .hasFocus())) {
                mContext.snapToNextScreen();
                super.onKeyDown(keyCode, event);
                return true;
            }
        }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (imgView[Constant.NUMBER_3].hasFocus()
            		|| imgView[Constant.NUMBER_4].hasFocus()
            		|| imgView[Constant.NUMBER_5].hasFocus()
            		|| imgView[Constant.NUMBER_6].hasFocus()) {
                RelativeLayout[] tagList = mContext.getTagView().getTagList();
                tagList[mContext.getFocusedPage()].requestFocus();
                super.onKeyDown(keyCode, event);
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
        
        if (!mContext.getTagView().hasFocus()) {
            if (mContext.isSnapLeft()) {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_2].requestFocus();
                } else {
                    imgView[Constant.NUMBER_6].requestFocus();
                }
            } else {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_0].requestFocus();
                } else {
                    imgView[Constant.NUMBER_3].requestFocus();
                }
            }
        }
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
                    String pkg = firstPkg[i].trim();
                    String cls = firstCls[i].trim();
                    ComponentName componentName = new ComponentName(pkg, cls);
                    Intent mIntent = new Intent();
                    mIntent.setComponent(componentName);
                    mContext.startToActivity(mIntent);
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
            v.getBackground().setAlpha(255);
            v.animate().scaleX(1.2f).scaleY(1.2f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            // Set the flag and focus related position
            switch (v.getId()) {
                case R.id.education_item_integrated:
                    mContext.setFocusedView(Constant.NUMBER_0);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_0].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.education_item_children:
                    mContext.setFocusedView(Constant.NUMBER_1);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_1].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.education_item_pple:
                    mContext.setFocusedView(Constant.NUMBER_2);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_2].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;   
                case R.id.education_item_reading:
                    mContext.setFocusedView(Constant.NUMBER_3);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.education_item_healthy:
                    mContext.setFocusedView(Constant.NUMBER_4);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_4].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;               
                case R.id.education_item_society:
                    mContext.setFocusedView(Constant.NUMBER_5);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_5].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.education_item_tsqinghua_school:
                    mContext.setFocusedView(Constant.NUMBER_6);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_6].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                default:
                    break;
            }
        } else {
           v.animate().scaleX(1.0f).scaleY(1.0f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();

            v.getBackground().setAlpha(0);
            
         switch (v.getId()) {
            case R.id.education_item_integrated:
                mTextView[Constant.NUMBER_0].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.education_item_children:
                mTextView[Constant.NUMBER_1].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.education_item_pple:
                mTextView[Constant.NUMBER_2].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.education_item_reading:
                mTextView[Constant.NUMBER_3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.education_item_healthy:
                mTextView[Constant.NUMBER_4].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.education_item_society:
                mTextView[Constant.NUMBER_5].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.education_item_tsqinghua_school:
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
        	Bitmap resizeBitmap1 = Util.createReflectedImage(mContext, imgView[3], 3);
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
    
    private void startActivityDo(View v){
    	int flag = MainActivity.ENTER_ACTIVITY_BY_CLICK;
		
		mContext.setEnterActivityFlag(flag);
	}
    
}