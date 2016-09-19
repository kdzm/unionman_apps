package com.um.launcher.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.um.launcher.MainActivity;
import com.um.launcher.R;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.interfaces.SourceManagerInterface;
import com.um.launcher.model.SourceObj;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;
import com.um.launcher.util.UtilDtv;

/**
 * The first big view
 */
@SuppressLint("ResourceAsColor")
public class MainPageFirst extends RelativeLayout implements ShowAbleInterface,
        View.OnClickListener, OnFocusChangeListener{

    private static final String TAG = "MainPageFirst";
    private static final String DTV_NO_SIGNAL_ACTION = "unionman.intent.action.STATUS_TUNER";
    private static final int DIRECT_LEFT = 1;
    private static final int DIRECT_RIGHT = 2;
    private static final int DIRECT_UP = 3;
    private static final int DIRECT_DOWN = 4;
    public final static int PAGENUM = 0;
    private static final int SHOW_INFO = 1008;
    private static final int NOSIGNAL_CHECK_MAXCNT = 5;
    private static final int SIGNAL_NOSUPPORT_CHECK_MAXCNT = 3;
    private MainActivity mContext;
    // The actual background
    public View[] mImgView;
    // when the Source is not used,set the background ashing
    public View mAdView;
    public View[] mInnerView;
    public TextView[] mTextView;
    public TextView mSignalTextView;
    public ImageView[] mImageViewReflect;
    private View  mViewNextFocuse;
    private View  mViewFocuseMove;
    private View  mImgViewFocuseMove;
    // show the current Source
    private TextView mCurSourceText;
    private Scroller mScroller;
    private ViewGroup root;
    private View parent;
    private float scaleNum = 1.0f;
    private Timer checkATVSignalStat = null;
    private TimerTask timerTask = null;
    private int mNoSignalCnt = 0;
    private int mSignalNoSupport = 0;
   //The first interface package name
    private String[] firstPkg = new String[] {
            "",//
            "com.sina.news",//news
            "com.thtf.myhouse",//housekeeper
            "com.source",//source
            "com.um.launcher",//multiscreen  com.hisilicon.dlna.mediacenter
            "com.umexplorer",//mediacenter
            "com.example.newthtfcemarket",//appcenter
            "com.thtf.twocode",//qrcode
            "cn.com.unionman.umtvsetting.umsettingmenu"//photo com.lfzd.sinagallery

    };

    // The main Activity in the first interface
    private String[] firstCls = new String[] {
            "",
            "com.sina.news.ui.PowerOnScreen",//news
            "com.thtf.myhouse.MyHouseKeeper",//housekeeper
            "com.source.SourceSelect2Activity", //source
            "com.um.launcher.MyMultiScreenActivity ",//multiscreen com.hisilicon.dlna.mediacenter.MediaCenterActivity
            "com.umexplorer.activity.SelectFileType",//mediacenter
            "com.example.newthtfcemarket.MainActivity",//appcenter
            "com.thtf.twocode.TwoCodeActivity",//qrcode
            "cn.com.unionman.umtvsetting.umsettingmenu.MainActivity"//photo com.lfzd.sinagallery.Activity.LoginActivity

    };
    
    public MainPageFirst(Context context) {
        super(context);
    }

    public MainPageFirst(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        parent = inflater.inflate(R.layout.main_page_first, this);
        initView(parent);
        mScroller = new Scroller(mContext);
    }

    private void initView(View parent) {
        SurfaceView surface = (SurfaceView)parent.findViewById(R.id.first_minvideo);
        SurfaceHolder sh = surface.getHolder();
        sh.setType(SurfaceHolder.SURFACE_TYPE_HISI_TRANSPARENT);
        Util.setSurfaceView(surface);
        
        mImgView = new View[] {
                parent.findViewById(R.id.first_item_window),
                parent.findViewById(R.id.first_item_news),
                parent.findViewById(R.id.first_item_housekeeper),
                parent.findViewById(R.id.first_item_source),
                parent.findViewById(R.id.first_item_multiscreen),
                parent.findViewById(R.id.first_item_mediacenter),
                parent.findViewById(R.id.first_item_appcenter),
                parent.findViewById(R.id.first_item_qrcode),
                parent.findViewById(R.id.first_item_setting),
        };
        mInnerView = new View[] {
                parent.findViewById(R.id.first_window_pic),
                parent.findViewById(R.id.view_news),
                parent.findViewById(R.id.view_housekeeper),
                parent.findViewById(R.id.view_source),
                parent.findViewById(R.id.view_multiscreen),
                parent.findViewById(R.id.view_mediacenter),
                parent.findViewById(R.id.view_appcenter),
                parent.findViewById(R.id.view_qrcode),
                parent.findViewById(R.id.view_setting),
        };
        mAdView = parent.findViewById(R.id.first_item_ad);
        mSignalTextView = (TextView)parent.findViewById(R.id.first_window_signal_txt);
        mImageViewReflect = new ImageView[]{
        		(ImageView)parent.findViewById(R.id.first_ad_img_reflect),
        		(ImageView)parent.findViewById(R.id.first_mediacenter_img_reflect),
        		(ImageView)parent.findViewById(R.id.first_appcenter_img_reflect),
        		(ImageView)parent.findViewById(R.id.first_qrcode_img_reflect),
        		(ImageView)parent.findViewById(R.id.first_setting_img_reflect),
        };
        
        for (int i = 0; i < mImgView.length; i++) {
            mImgView[i].setOnClickListener(this);
            mImgView[i].getBackground().setAlpha(0);
            mImgView[i].setOnFocusChangeListener(this);
        }
        mCurSourceText = (TextView) parent.findViewById(R.id.first_window_txt);
        
        mTextView = new TextView[]{
                (TextView)parent.findViewById(R.id.first_window_txt),
                (TextView)parent.findViewById(R.id.first_news_txt),
                (TextView)parent.findViewById(R.id.first_housekeeper_txt),
                (TextView)parent.findViewById(R.id.first_source_txt),
                (TextView)parent.findViewById(R.id.first_multiscreen_txt),
                (TextView)parent.findViewById(R.id.first_mediacenter_txt),
                (TextView)parent.findViewById(R.id.first_appcenter_txt),
                (TextView)parent.findViewById(R.id.first_qrcode_txt),	
                (TextView)parent.findViewById(R.id.first_setting_txt),

        };
        
        for (int i = 0; i < mTextView.length; i++)
        {
        	mTextView[i].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0)); 
        }
        
        mViewFocuseMove = parent.findViewById(R.id.first_focuse_item);
        mImgViewFocuseMove = parent.findViewById(R.id.first_focuse_img);
        root = (ViewGroup)parent.findViewById(R.id.first_page_id);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mImgView[Constant.NUMBER_0].hasFocus()) {
                mContext.snapToPreScreen();
                return true;
            }
            else{
            	//foucesStartMove(DIRECT_LEFT);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if ((mImgView[Constant.NUMBER_4].hasFocus() || mImgView[Constant.NUMBER_8]
                    .hasFocus())) {
                mContext.snapToNextScreen();
                return true;
            }
            else{
            	//foucesStartMove(DIRECT_RIGHT);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mImgView[Constant.NUMBER_0].hasFocus()
                    || mImgView[Constant.NUMBER_1].hasFocus()
                    || mImgView[Constant.NUMBER_2].hasFocus()
                    || mImgView[Constant.NUMBER_3].hasFocus()
                    || mImgView[Constant.NUMBER_4].hasFocus()) {
                return true;
            }
            else
            {
            	//foucesStartMove(DIRECT_UP);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mImgView[Constant.NUMBER_5].hasFocus()
                    || mImgView[Constant.NUMBER_6].hasFocus()
                    || mImgView[Constant.NUMBER_7].hasFocus()
                    || mImgView[Constant.NUMBER_8].hasFocus()) {
                RelativeLayout[] tagList = mContext.getTagView().getTagList();
                tagList[mContext.getFocusedPage()].requestFocus();
                return true;
            }
            else{
            	//foucesStartMove(DIRECT_DOWN);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void isShow() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "Now isShow--->" + PAGENUM);
            Log.i(TAG, "MainActivity.isSnapLeftOrRight--->"
                    + MainActivity.isSnapLeftOrRight);
        }
        
        reflectImageShow();
        
        if (MainActivity.isSnapLeftOrRight) {
            if (!mContext.getTagView().hasFocus()) {
                if (mContext.isSnapLeft()) {
                    if (mContext.isFocusUp()) {
                        mImgView[Constant.NUMBER_4].requestFocus();
                    } else {
                        mImgView[Constant.NUMBER_8].requestFocus();
                    }
                } else {
                    if (mContext.isFocusUp()) {
                        mImgView[Constant.NUMBER_0].requestFocus();
                    } else {
                        mImgView[Constant.NUMBER_0].requestFocus();
                    }
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
        return mImgView;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mImgView.length; i++) {
            if (i == 0 && mImgView[i] == v) {
            	mContext.enterToPlay();
            }else if (i == 3 && mImgView[i] == v){
     		   Intent service = new Intent();
               service.setClassName("com.source", "com.source.SourceService");
               mContext.startService(service);
            }else if (mImgView[i] == v) {
                try {
                    String pkg = firstPkg[i].trim();
                    String cls = firstCls[i].trim();
                    ComponentName componentName = new ComponentName(pkg, cls);
                    Intent mIntent = new Intent();
                    mIntent.setComponent(componentName);
                    mContext.startToActivity(mIntent);
                    startActivityDo(v);
                } catch (Exception e) {
                    if (i != 0) {
                    	Util.appNoSupportPrompt(mContext, R.string.app_no_support);
                    }
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
        	//if (!mScroller.computeScrollOffset())
        	{
        		v.getBackground().setAlpha(255);
	            if (v.getId() == R.id.first_item_window){
	                v.animate().scaleX(1.00f).scaleY(1.00f)
	                .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
	            }else{
	            	 v.animate().scaleX(1.2f).scaleY(1.2f)
	                 .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
	            }
        	}
            // Set the flag and focus related position
            switch (v.getId()) {
                case R.id.first_item_window:
                    mContext.setFocusedView(Constant.NUMBER_0);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_0].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    mSignalTextView.setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_news:
                    mContext.setFocusedView(Constant.NUMBER_1);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_1].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_housekeeper:
                    mContext.setFocusedView(Constant.NUMBER_2);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_2].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_source:
                    mContext.setFocusedView(Constant.NUMBER_3);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_multiscreen:
                    mContext.setFocusedView(Constant.NUMBER_4);
                    mContext.setFocusUp(true);
                    mTextView[Constant.NUMBER_4].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_mediacenter:
                    mContext.setFocusedView(Constant.NUMBER_5);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_5].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_appcenter:
                    mContext.setFocusedView(Constant.NUMBER_6);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_6].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_qrcode:
                    mContext.setFocusedView(Constant.NUMBER_7);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_7].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;     
                case R.id.first_item_setting:
                    mContext.setFocusedView(Constant.NUMBER_8);
                    mContext.setFocusUp(false);
                    mTextView[Constant.NUMBER_8].setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                    break;

                default:
                    break;
            }
        } else {
            v.getBackground().setAlpha(0);
            	v.animate().scaleX(1.0f).scaleY(1.0f)
                .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                switch (v.getId()) {
                case R.id.first_item_window:
                    mTextView[Constant.NUMBER_0].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    mSignalTextView.setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_news:
                    mTextView[Constant.NUMBER_1].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_housekeeper:
                    mTextView[Constant.NUMBER_2].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_source:
                    mTextView[Constant.NUMBER_3].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_multiscreen:
                    mTextView[Constant.NUMBER_4].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_mediacenter:
                    mTextView[Constant.NUMBER_5].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_appcenter:
                    mTextView[Constant.NUMBER_6].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_qrcode:
                    mTextView[Constant.NUMBER_7].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                case R.id.first_item_setting:
                    mTextView[Constant.NUMBER_8].setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * set current source name
     */
    public void setTextValue(int curId) {
        // TODO Auto-generated method stub
        if (null == mCurSourceText) {
            return;
        }
        // int curId = SourceManagerInterface.getCurSourceId();
        switch (curId) {
            case EnumSourceIndex.SOURCE_DVBC:
                mCurSourceText.setText(R.string.DVB);
                break;
            case EnumSourceIndex.SOURCE_DTMB:// DTV
                mCurSourceText.setText(R.string.DTV);
                break;
            case EnumSourceIndex.SOURCE_ATV:
                mCurSourceText.setText(R.string.ATV);
                break;
            case EnumSourceIndex.SOURCE_CVBS1:// AV1
                mCurSourceText.setText(R.string.AV);
                break;
            case EnumSourceIndex.SOURCE_CVBS2:// AV2
                mCurSourceText.setText(R.string.AV2);
                break;
            case EnumSourceIndex.SOURCE_YPBPR1:// YPBPR
                mCurSourceText.setText(R.string.YPbPr);
                break;
            case EnumSourceIndex.SOURCE_HDMI1:
                mCurSourceText.setText(R.string.HDMI1);
                break;
            case EnumSourceIndex.SOURCE_HDMI2:
                mCurSourceText.setText(R.string.HDMI2);
                break;
            case EnumSourceIndex.SOURCE_HDMI3:
                mCurSourceText.setText(R.string.HDMI3);
                break;
            case EnumSourceIndex.SOURCE_VGA:
                mCurSourceText.setText(R.string.VGA);
                break;
            default:
                mCurSourceText.setText(R.string.ATV);
                break;
        }
    }
    
    public void reflectImageShow(){
    	
        if (mImageViewReflect[0].getDrawable() == null){
        	Bitmap resizeBitmap1 = Util.createReflectedImage(mContext, mAdView, 3);
        	if (resizeBitmap1 != null)
        		mImageViewReflect[0].setImageBitmap(resizeBitmap1);
        }
    	
        if ((mImageViewReflect[1].getDrawable() == null) && (!mImgView[5].isFocused())){
        	Bitmap resizeBitmap2 = Util.createReflectedImage(mContext, mImgView[5], 3);
        	if (resizeBitmap2 != null)
        		mImageViewReflect[1].setImageBitmap(resizeBitmap2);
        }
        
        if ((mImageViewReflect[2].getDrawable() == null) && (!mImgView[6].isFocused())){
            Bitmap resizeBitmap3 = Util.createReflectedImage(mContext, mImgView[6], 3);
            if (resizeBitmap3 != null)
            	mImageViewReflect[2].setImageBitmap(resizeBitmap3);
        }
        
        if ((mImageViewReflect[3].getDrawable() == null) && (!mImgView[7].isFocused())){
            Bitmap resizeBitmap4 = Util.createReflectedImage(mContext, mImgView[7], 3);
            if (resizeBitmap4 != null)
            	mImageViewReflect[3].setImageBitmap(resizeBitmap4);
        }
        
        if ((mImageViewReflect[4].getDrawable() == null) && (!mImgView[8].isFocused())){
            Bitmap resizeBitmap5 = Util.createReflectedImage(mContext, mImgView[8], 3);
            if (resizeBitmap5 != null)
            	mImageViewReflect[4].setImageBitmap(resizeBitmap5);
        }
    }

	@Override
	public void computeScroll() {
		super.computeScroll();
		Log.d(TAG,"leon: come to computeScroll");
		reflectImageShow();
        if (mScroller.computeScrollOffset()) {
        	mViewFocuseMove.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        	if (mViewNextFocuse != null){
        		if (mViewNextFocuse.getId() == R.id.first_item_window)
        		{
            		//mImgViewFocuseMove.animate().scaleX(scaleNum + 1.5f).scaleY(scaleNum + 0.5f)
                    //.setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            		scaleNum += 0.001f;
        		}else
        		{
	        		scaleNum += 0.01f;
        		}
            	mViewNextFocuse.animate().scaleX(scaleNum).scaleY(scaleNum)
                .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
        	}
        	mViewFocuseMove.bringToFront();
            postInvalidate();
            Log.d(TAG,"leon: come to computeScroll:move");
        }else{
        	/*
        	mImgViewFocuseMove.setVisibility(View.INVISIBLE);
        	if (mViewNextFocuse != null)
        	{
        		if (mViewNextFocuse.hasFocus())
        		{
		        	mViewNextFocuse.bringToFront();
		        	mViewNextFocuse.getBackground().setAlpha(255);
		            if (mViewNextFocuse.getId() == R.id.first_item_window){
		            	mViewNextFocuse.animate().scaleX(1.00f).scaleY(1.00f)
		                .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
		            }else{
		            	mViewNextFocuse.animate().scaleX(1.2f).scaleY(1.2f)
		                 .setDuration(50).start();
		            }
        		}
        		else
        		{
	            	mViewNextFocuse.animate().scaleX(1.0f).scaleY(1.0f)
	                 .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
        		}
	            mViewNextFocuse = null;
        	}
        	*/
        }
	}
	
	private void foucesStartMove(int direct){
		int forwardid = 0;
		View focuseView = root.getFocusedChild();
		Log.d(TAG,"leon: come to foucesStartMove");
		if (focuseView != null)
		{
			Log.d(TAG,"leon: come to foucesStartMove:find focus child");
			switch (direct)
			{
				case DIRECT_LEFT:
					forwardid = focuseView.getNextFocusLeftId();
					break;
				case DIRECT_RIGHT:
					forwardid = focuseView.getNextFocusRightId();
					break;
				case DIRECT_UP:
					forwardid = focuseView.getNextFocusUpId();
					break;
				case DIRECT_DOWN:
					forwardid = focuseView.getNextFocusDownId();
					break;
				default:
					break;
			}
			Log.d(TAG,"leon: come to foucesStartMove:find forwardid:"+forwardid);
			View nextView = parent.findViewById(forwardid);
			if (nextView != null)
			{
	        	mViewNextFocuse = nextView;
				int startx = focuseView.getLeft();
				int starty = focuseView.getTop();
				int dx = startx - nextView.getLeft();
				int dy = starty - nextView.getTop();
				RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mImgViewFocuseMove.getLayoutParams();
				relativeParams.height = nextView.getHeight();//(focuseView.getHeight() > nextView.getHeight()) ? nextView.getHeight(): focuseView.getHeight();
				relativeParams.width = nextView.getWidth();//(focuseView.getWidth() > nextView.getWidth()) ? nextView.getWidth(): focuseView.getWidth();
				relativeParams.leftMargin = startx;
				relativeParams.topMargin = starty;
				mImgViewFocuseMove.setLayoutParams(relativeParams);
        		mImgViewFocuseMove.animate().scaleX(1.0f).scaleY(1.0f)
                .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
				mImgViewFocuseMove.setVisibility(View.VISIBLE);
				scaleNum = 1.0f;
				Log.d(TAG,"leon: [startx:"+startx+"][starty:"+starty+"][dx:"+dx+"][dy:"+dy+"]");
				mScroller.startScroll(0, 0, dx, dy, 400);
				Log.d(TAG,"leon: startScroll");
			}
		}
	}
	
	private void startActivityDo(View v){
		int flag = MainActivity.ENTER_ACTIVITY_BY_CLICK;
		if (v.getId() == R.id.first_item_source){
			Log.d(TAG,"leon: startActivityDo");
			flag = flag | MainActivity.ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE | MainActivity.ENTER_ACTIVITY_WITHOUT_SCALE_WINDOW;
		}else if (v.getId() == R.id.first_item_window){
			flag = flag | MainActivity.ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE;
		}
		
		mContext.setEnterActivityFlag(flag);
	}
	
	 /**
     * TV play listener
     */
    OnPlayerListener onPlayerListener = new OnPlayerListener() {

        @Override
        public void onPCAutoAdjustStatus(int arg0) {
            Log.d(TAG, "  onPCAutoAdjustStatus  arg0: " + arg0);
        }

        @Override
        public void onSignalStatus(int arg0) {
            //if (Constant.LOG_TAG) {
                Log.d(TAG, "onSignalStatus  arg0: " + arg0);
            //}
                handleSignalStat(arg0);
        }

        @Override
        public void onTimmingChanged(TimingInfo arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onTimmingChanged  arg0: " + arg0);
            }
        }

        @Override
        public void onSrcDetectPlugin(ArrayList<Integer> arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugin  arg0: " + arg0);
            }
        }

        @Override
        public void onSrcDetectPlugout(ArrayList<Integer> arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugout  arg0: " + arg0);
            }
        }
        @Override
        public void onSelectSourceComplete(int  arg0,int arg1,int arg2) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSelectSourceComplete  arg0: " + arg0);
            }
            Log.d(TAG, "===yiyonghui=== ");
        }
    };

	/**
     * register all listeners
     */
    private void registerListener() {
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SIGNAL_STATUS, onPlayerListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_TIMMING_CHANGED, onPlayerListener);
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGIN,
                onPlayerListener);
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGOUT,
                onPlayerListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_PC_ADJ_STATUS, onPlayerListener);
    }
    
    private void unRegisterListener(){
    	UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SIGNAL_STATUS, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_TIMMING_CHANGED, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PLUGIN, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PLUGOUT, onPlayerListener);

        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PC_ADJ_STATUS, onPlayerListener);
    }
    
    private void handleSignalStat(int sigstat){
    	int sourceid = SourceManagerInterface.getLastSourceId();
        Log.d(TAG,"leon... firstpage showSignalStat");
        if ((sourceid != EnumSourceIndex.SOURCE_DVBC) && (sourceid != EnumSourceIndex.SOURCE_DTMB) && (sourceid != EnumSourceIndex.SOURCE_MEDIA)){
            if (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL){
            	mSignalNoSupport = 0;
            	if (mNoSignalCnt >= NOSIGNAL_CHECK_MAXCNT){
            		mSignalTextView.setText(R.string.no_signal);
            		if (mImgView[Constant.NUMBER_0].isFocused()){
            			mSignalTextView.setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
            		}else{
            			mSignalTextView.setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            		}
            		mSignalTextView.setVisibility(View.VISIBLE);
            		mNoSignalCnt = 0;
            	}else{
                	mNoSignalCnt++;
                	checkATVSignalStat.schedule(checkSignalStat(),500);
            	}
            }else if(sigstat == EnumSignalStat.SIGSTAT_SUPPORT){
            	mSignalTextView.setVisibility(View.INVISIBLE);
            	mNoSignalCnt = 0;
            	mSignalNoSupport = 0;
            }else if (sigstat == EnumSignalStat.SIGSTAT_SUPPORT){
            	mNoSignalCnt = 0;
            	if (mSignalNoSupport >= SIGNAL_NOSUPPORT_CHECK_MAXCNT){
            		mSignalTextView.setText(R.string.signal_nosupport);
            		if (mImgView[Constant.NUMBER_0].isFocused()){
            			mSignalTextView.setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
            		}else{
            			mSignalTextView.setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            		}
            		mSignalTextView.setVisibility(View.VISIBLE);
            		mSignalNoSupport = 0;
            	}else{
            		mSignalNoSupport++;
            		checkATVSignalStat.schedule(checkSignalStat(),500);
            	}
            }
        }
    }
    
    Handler checkSignalHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SHOW_INFO:
            	int sigstat = SourceManagerInterface.getSignalStatus();
            	handleSignalStat(sigstat);
                break;
            default:
                break;
            }
        }
    };
    
    public TimerTask checkSignalStat(){
        timerTask = new TimerTask(){
            @Override
            public void run(){
                checkSignalHandler.sendEmptyMessage(SHOW_INFO);
            }
        };
        return timerTask;
    }
    
    private void registDtvNoSignalBroadCast(){
    	IntentFilter filter = new IntentFilter(DTV_NO_SIGNAL_ACTION);
    	mContext.registerReceiver(dtvNoSignalReceiver, filter);
    }
    
    private void unregistDtvNoSignalBroadCast(){
    	mContext.unregisterReceiver(dtvNoSignalReceiver);
    }
    
    private BroadcastReceiver dtvNoSignalReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "dtvNoSignalReceiver DTV_NO_SIGNAL_ACTION");
            String action = intent.getAction();
            if (action.equals(DTV_NO_SIGNAL_ACTION)){
            	int sourceid = SourceManagerInterface.getLastSourceId();
            	if ((sourceid == EnumSourceIndex.SOURCE_DVBC) || (sourceid == EnumSourceIndex.SOURCE_DTMB)){
            		boolean noSignalFlag = intent.getBooleanExtra("no_signal", true);
            		Log.e(TAG, "dtvNoSignalReceiver DTV_NO_SIGNAL_ACTION,noSignalFlag:"+noSignalFlag);
                	if (noSignalFlag){
					if(UtilDtv.getInstance().isDTVProgEmpty(mContext)==true)
					{
						mSignalTextView.setText(R.string.no_program);
					}
					else
					{
						mSignalTextView.setText(R.string.no_signal);	
					}
              		if (mImgView[Constant.NUMBER_0].isFocused()){
                  			mSignalTextView.setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                		}else{
                			mSignalTextView.setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                		}
                		mSignalTextView.setVisibility(View.VISIBLE);
                	}else{
                		mSignalTextView.setVisibility(View.INVISIBLE);
                	}
            	}        	
            }
        }
    };
    
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
    	super.onWindowFocusChanged(hasWindowFocus);
    	Log.d(TAG,"leon... firstpage onWindowFocusChanged, hasWindowFocus:"+hasWindowFocus);
    	if (hasWindowFocus){
    		mNoSignalCnt = 0;
    		mSignalTextView.setVisibility(View.INVISIBLE);
    		registerListener();
    		registDtvNoSignalBroadCast();
            if (checkATVSignalStat != null){
                checkATVSignalStat.cancel();
            }
            checkATVSignalStat = new Timer();
            checkATVSignalStat.schedule(checkSignalStat(),500);
    	}else{
    		checkSignalHandler.removeMessages(SHOW_INFO);
    		unRegisterListener();
    		unregistDtvNoSignalBroadCast();
            if (checkATVSignalStat != null){
                checkATVSignalStat.cancel();
            }
    	}
    }
    
    public void resetSignalCheckStatus(){
       int sourceid = SourceManagerInterface.getLastSourceId();
       if (mSignalTextView != null){
           mSignalTextView.setVisibility(View.INVISIBLE);
       }
       
       if ((sourceid != EnumSourceIndex.SOURCE_DVBC) && (sourceid != EnumSourceIndex.SOURCE_DTMB) && (sourceid != EnumSourceIndex.SOURCE_MEDIA)){
    	   mNoSignalCnt = 0;
            if (checkATVSignalStat != null){
                checkATVSignalStat.cancel();
            }
            checkATVSignalStat = new Timer();
            checkATVSignalStat.schedule(checkSignalStat(),500);
       }
	}
    
    public void hideVideoWindow(){
    	mInnerView[Constant.NUMBER_0].setVisibility(View.INVISIBLE);
    	mInnerView[Constant.NUMBER_0].invalidate();
    }
    
    public void showVideoWindow(){
    	mInnerView[Constant.NUMBER_0].setVisibility(View.VISIBLE);
    	mInnerView[Constant.NUMBER_0].invalidate();
    }
    
}
