
package com.um.launcher.view;

import android.annotation.SuppressLint;
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
import com.um.launcher.R;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;

/**
 * The first big view
 */
@SuppressLint("HandlerLeak")
public class MainPageGame extends RelativeLayout implements ShowAbleInterface,
        View.OnClickListener, OnFocusChangeListener {

    private static final String TAG = "MainPageGame";
    public final static int PAGENUM = 2;
    private MainActivity mContext;
    public View[] imgView;
    public TextView[] mTextView;
    public ImageView[] mImageViewReflect;
    private ComFlipView mComFlipView;

    private final int mFlipDraw[] = {
            R.drawable.game_item_bird,
            R.drawable.game_item_corpse,
            R.drawable.game_item_cat, R.drawable.game_item_race
    };

    // The third interface package name
    private String[] thridPkg = new String[] {
            "com.trans.gamehall",//game center
            "com.seleuco.kof97",//boxingking
            "com.jiajia.club_main",// jiajia
            "com.vectorunit.redcmgeplaycn",// gp  thtf.cpsoft.fly3d
            "com.dotemu.rtype2",// war
            "com.chuanyun.box.game",// feizhi
            "app.android.applicationxc"// xiaochong
    };
    // The main Activity third interface
    private String[] thridCls = new String[] {
            "com.trans.gamehall.AndroidGameHallActivity",//game center
            "com.putaolab.ptsdk.activity.PTMainActivity",//boxingking
            "com.jiajia.club_main.Start",// jiajia
            "com.vectorunit.redcmgeplaycn.Red",//gp  thtf.cpsoft.fly3d.Fly3dActivity
            "com.dotemu.rtype2.RType2LaunchActivity",//war
            "com.chuanyun.box.game.activity.MainGameActivity",//feizhi
            "com.xiaocong.android.recommend.TvLauncherActivity"//xiaochong
    };

    public MainPageGame(Context context) {
        super(context);
    }

    public MainPageGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.main_page_mygame, this);
        initView(parent);
    }

    /**
     * Initialize current views
     */
    private void initView(View parent) {
        imgView = new View[] {
                parent.findViewById(R.id.game_item_hall),
                parent.findViewById(R.id.game_item_boxingking),
                parent.findViewById(R.id.game_item_jiajia),
                parent.findViewById(R.id.game_item_gp),
                parent.findViewById(R.id.game_item_helicopter_war),
                parent.findViewById(R.id.game_item_feizhi),
                parent.findViewById(R.id.game_item_xiaochong),
        };
        for (int i = 0; i < imgView.length; i++) {
            if (i == 0) {
                /*mComFlipView = new ComFlipView(mContext, mFlipDraw);
                mComFlipView.setId(1);
                View titleText = findViewById(R.id.game_title_txt);
                RelativeLayout.LayoutParams txtParams = (LayoutParams) titleText
                        .getLayoutParams();
                ((RelativeLayout) imgView[i].findViewById(R.id.game_child))
                        .removeAllViews();
                RelativeLayout.LayoutParams params = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mComFlipView.setLayoutParams(params);
                ((RelativeLayout) imgView[i].findViewById(R.id.game_child))
                        .addView(mComFlipView);
                // txtParams.topMargin=370;
                txtParams.addRule(RelativeLayout.ALIGN_BOTTOM, 1);
                ((RelativeLayout) imgView[i].findViewById(R.id.game_child))
                        .addView(titleText, txtParams);*/

            }
            imgView[i].setOnClickListener(this);
            imgView[i].getBackground().setAlpha(0);
            imgView[i].setOnFocusChangeListener(this);
        }
        
        mImageViewReflect = new ImageView[]{
        		(ImageView)parent.findViewById(R.id.game_boxingking_img_reflect),
        		(ImageView)parent.findViewById(R.id.game_war_img_reflect),
        		(ImageView)parent.findViewById(R.id.game_feizhi_img_reflect),
        		(ImageView)parent.findViewById(R.id.game_xiaochong_img_reflect),	
        };
        
        mTextView = new TextView[]{
                (TextView)parent.findViewById(R.id.game_hall_txt),
                (TextView)parent.findViewById(R.id.game_boxingking_txt),
                (TextView)parent.findViewById(R.id.game_jiajia_txt),
                (TextView)parent.findViewById(R.id.game_gp_txt),
                (TextView)parent.findViewById(R.id.game_helicopter_war_txt),
                (TextView)parent.findViewById(R.id.game_feizhi_txt),
                (TextView)parent.findViewById(R.id.game_xiaochong_txt),	
        };
        
        for (int i = 0; i < mTextView.length; i++)
        {
        	mTextView[i].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0)); 
        }
        
    }

    /**
     * show next view
     */
    public void showNext() {
        if (mComFlipView != null) {
            mComFlipView.showNext();
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
            if ((imgView[Constant.NUMBER_3].hasFocus() || imgView[Constant.NUMBER_6]
                    .hasFocus())) {
                mContext.snapToNextScreen();
                return true;
            }
        }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (imgView[Constant.NUMBER_0].hasFocus()) {
                //imgView[Constant.NUMBER_4].requestFocus();
                //return true;
            }else if(imgView[Constant.NUMBER_2].hasFocus()){
                //imgView[Constant.NUMBER_5].requestFocus();
                //return true;
            }else if (imgView[Constant.NUMBER_3].hasFocus()){
                //imgView[Constant.NUMBER_6].requestFocus();
                //return true;
            } else {
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
            Log.i(TAG, "Now isShow--->" + PAGENUM);
        }
        
        reflectImageShow();
        
        if (!mContext.getTagView().hasFocus()) {
            if (mContext.isSnapLeft()) {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_3].requestFocus();
                } else {
                    imgView[Constant.NUMBER_6].requestFocus();
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
            if (imgView[i] == v) {
                try {
                    String pkg = thridPkg[i].trim();
                    String cls = thridCls[i].trim();
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
            v.animate().scaleX(1.2f).scaleY(1.2f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();

            v.getBackground().setAlpha(255);
            // Set the flag and focus related position
            switch (v.getId()) {
                case R.id.game_item_hall:
                    mContext.setFocusedView(Constant.NUMBER_0);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_0].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.game_item_boxingking:
                    mContext.setFocusedView(Constant.NUMBER_1);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_1].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.game_item_jiajia:
                    mContext.setFocusedView(Constant.NUMBER_2);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_2].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.game_item_gp:
                    mContext.setFocusedView(Constant.NUMBER_3);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;                   
                case R.id.game_item_helicopter_war:
                    mContext.setFocusedView(Constant.NUMBER_4);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_4].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.game_item_feizhi:
                    mContext.setFocusedView(Constant.NUMBER_5);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_5].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.game_item_xiaochong:
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
            case R.id.game_item_hall:
                mTextView[Constant.NUMBER_0].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.game_item_boxingking:
                mTextView[Constant.NUMBER_1].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.game_item_jiajia:
                mTextView[Constant.NUMBER_2].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.game_item_gp:
                mTextView[Constant.NUMBER_3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;                   
            case R.id.game_item_helicopter_war:
                mTextView[Constant.NUMBER_4].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.game_item_feizhi:
                mTextView[Constant.NUMBER_5].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                break;
            case R.id.game_item_xiaochong:
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
        	Bitmap resizeBitmap1 = Util.createReflectedImage(mContext, imgView[1], 6);
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
