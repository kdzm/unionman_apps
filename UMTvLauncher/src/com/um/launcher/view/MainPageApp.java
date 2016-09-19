
package com.um.launcher.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.um.launcher.MainActivity;
import com.um.launcher.MyAppActivity;
import com.um.launcher.R;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;

/**
 * The first big view
 */
public class MainPageApp extends RelativeLayout implements ShowAbleInterface,
        View.OnClickListener, OnFocusChangeListener {

    public final static int PAGENUM = 3;
    private MainActivity mContext;
    public View[] imgView;
    public TextView[] mTextView;
    public ImageView[] mImageViewReflect;
    
    // The second interface package name
    private String[] secondPkg = new String[] {
            "",//all app
            "com.seeyou.tv",//video com.togic.livevideo
            "st.com.xiami",//music
            "com.tencent.pad.qq",// tvQQ
            "com.android.browser", // browser
            "com.um.filemanager",// filemanage
            "com.manle.phone.android.yaodian",// housenurse
            "com.chaoliu.TaoCookForPad"// healthydiet
    };
    // the packages name of App showing in the front
    private String[] secondCls = new String[] {
            "", //all app
            "com.zte.ucs.ui.login.WelcomeGuideActivity",//video com.togic.launcher.SplashActivity
            "st.com.xiami.activity.LoadingActivity",//music
            "com.tencent.pad.qq.QQLoginActivity", //tvQQ 
            "com.android.browser.BrowserActivity",//browser
            "com.um.filemanager.activity.TabBarExample",//filemanage
            "com.manle.phone.android.yaodian.Welcome",//housenurse
            "com.chaoliu.TaoCookForPad.ActivityFirst"//healthydiet
    };

    public MainPageApp(Context context) {
        super(context);
    }

    public MainPageApp(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.main_page_myapp, this);
        initView();
    }

    /**
     * Initialize current views
     */
    private void initView() {
        imgView = new View[] {
                findViewById(R.id.app_item_allapp),
                findViewById(R.id.app_item_video),
                findViewById(R.id.app_item_music),
                findViewById(R.id.app_item_tvqq),
                findViewById(R.id.app_item_browser),
                findViewById(R.id.app_item_filemanage),
                findViewById(R.id.app_item_housenurse),
                findViewById(R.id.app_item_healthydiet)

        };
        for (int i = 0; i < imgView.length; i++) {
            imgView[i].setOnClickListener(this);
            imgView[i].getBackground().setAlpha(0);
            imgView[i].setOnFocusChangeListener(this);
        }
        
        mImageViewReflect = new ImageView[]{
        		(ImageView)findViewById(R.id.app_browser_img_reflect),
        		(ImageView)findViewById(R.id.app_filemanage_img_reflect),
        		(ImageView)findViewById(R.id.app_housenurse_img_reflect),
        		(ImageView)findViewById(R.id.app_healthydiet_img_reflect),	
        };
        
        mTextView = new TextView[]{
                (TextView)findViewById(R.id.app_allapp_txt),
                (TextView)findViewById(R.id.app_video_txt),
                (TextView)findViewById(R.id.app_music_txt),
                (TextView)findViewById(R.id.app_tvqq_txt),
                (TextView)findViewById(R.id.app_browser_txt),
                (TextView)findViewById(R.id.app_filemanage_txt),
                (TextView)findViewById(R.id.app_housenurse_txt),
                (TextView)findViewById(R.id.app_healthydiet_txt)
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
            if (imgView[Constant.NUMBER_3].hasFocus() || imgView[Constant.NUMBER_7].hasFocus()) {
                mContext.snapToNextScreen();
                return true;
            }
        }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (imgView[Constant.NUMBER_4].hasFocus()
            		|| imgView[Constant.NUMBER_5].hasFocus()
            		|| imgView[Constant.NUMBER_6].hasFocus()
            		|| imgView[Constant.NUMBER_7].hasFocus()) {
                RelativeLayout[] tagList = mContext.getTagView().getTagList();
                tagList[mContext.getFocusedPage()].requestFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void isShow() {
        if (Constant.LOG_TAG) {
            Log.i("TestForPage", "Now isShow--->" + PAGENUM);
        }
        
        reflectImageShow();
        
        if (!mContext.getTagView().hasFocus()) {
            if (mContext.isSnapLeft()) {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_3].requestFocus();
                } else {
                    imgView[Constant.NUMBER_7].requestFocus();
                }
            } else {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_0].requestFocus();
                } else {
                    imgView[Constant.NUMBER_4].requestFocus();
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
            if (i != 0 && imgView[i] == v) {
                try {
                    String pkg = secondPkg[i].trim();
                    String cls = secondCls[i].trim();
                    ComponentName componentName = new ComponentName(pkg, cls);
                    Intent mIntent = new Intent();
                    mIntent.setComponent(componentName);
                    mContext.startToActivity(mIntent);
                    startActivityDo(v);
                    break;
                } catch (Exception e) {
                    if (i != 0) {
                    	Util.appNoSupportPrompt(mContext, R.string.app_no_support);
                    }
                    e.printStackTrace();
                }
            } else if (i == 0 && imgView[i] == v) {
                Intent intent = new Intent(mContext, MyAppActivity.class);
                mContext.startActivity(intent);
                startActivityDo(v);
                break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.bringToFront();
            v.getBackground().setAlpha(255);
            v.animate().scaleX(1.2f).scaleY(1.2f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();

            // Set the flag and focus related position
            switch (v.getId()) {
                case R.id.app_item_allapp:
                    mContext.setFocusedView(Constant.NUMBER_0);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_0].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_video:
                    mContext.setFocusedView(Constant.NUMBER_1);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_1].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_music:
                    mContext.setFocusedView(Constant.NUMBER_2);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_2].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_tvqq:
                    mContext.setFocusedView(Constant.NUMBER_3);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_browser:
                    mContext.setFocusedView(Constant.NUMBER_4);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_4].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_filemanage:
                    mContext.setFocusedView(Constant.NUMBER_5);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_5].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_housenurse:
                    mContext.setFocusedView(Constant.NUMBER_6);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_6].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_healthydiet:
                    mContext.setFocusedView(Constant.NUMBER_7);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_7].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;                    
                default:
                    break;
            }
        } else {
            v.getBackground().setAlpha(0);
                v.animate().scaleX(1.0f).scaleY(1.0f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            
            switch (v.getId()) {
                case R.id.app_item_allapp:
                    mTextView[Constant.NUMBER_0].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_video:
                    mTextView[Constant.NUMBER_1].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_music:
                    mTextView[Constant.NUMBER_2].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_tvqq:
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_browser:
                    mTextView[Constant.NUMBER_4].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_filemanage:
                    mTextView[Constant.NUMBER_5].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_housenurse:
                    mTextView[Constant.NUMBER_6].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.app_item_healthydiet:
                    mTextView[Constant.NUMBER_7].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;                    
                default:
                    break;
            }
        }
    }

    public void reflectImageShow(){
    	if (mImageViewReflect[0].getDrawable() == null)
    	{
        	Bitmap resizeBitmap1 = Util.createReflectedImage(mContext, imgView[4], 3);
        	if (resizeBitmap1 != null)
        		mImageViewReflect[0].setImageBitmap(resizeBitmap1);
    	}
    	
    	if (mImageViewReflect[1].getDrawable() == null)
    	{
            Bitmap resizeBitmap2 = Util.createReflectedImage(mContext, imgView[5], 3);
            if (resizeBitmap2 != null)
            	mImageViewReflect[1].setImageBitmap(resizeBitmap2);
    	}
    	
    	if (mImageViewReflect[2].getDrawable() == null)
    	{
            Bitmap resizeBitmap3 = Util.createReflectedImage(mContext, imgView[6], 3);
            if (resizeBitmap3 != null)
            	mImageViewReflect[2].setImageBitmap(resizeBitmap3);
    	}
    	
    	if (mImageViewReflect[3].getDrawable() == null)
    	{
            Bitmap resizeBitmap4 = Util.createReflectedImage(mContext, imgView[7], 3);
            if (resizeBitmap4 != null)
            	mImageViewReflect[3].setImageBitmap(resizeBitmap4);
    	}
    }
    
    private void startActivityDo(View v){
    	int flag = MainActivity.ENTER_ACTIVITY_BY_CLICK;
		
		mContext.setEnterActivityFlag(flag);
	}
}
