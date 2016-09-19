package com.um.ui;

import com.um.dvb.R;
import com.um.dvbstack.Prog;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hisilicon.android.HiDisplayManager;

public class PlayerMenuContrl extends PopupWindow implements
		OnFocusChangeListener, OnClickListener{
    private final String PREFERENCES_TAG = "com.um.dvb";
    private final String PREFERENCES_OFTEN_WATCH_EN = "ofenwatch.enable";
	private Context context;
	private Handler handler = null;
	private AudioManager audioManager = null;
	private LinearLayout scalor = null;
	private ImageView scalorLeft, scalorRight;
	private TextView mAddFav, mRemoveFav, mLeftVoice, mRightVoice;
	private TextView mStereoVoice, mOriginScale, mSecondScale, mThirdScale, mOftenWatchOPen, mOftenWatchClose;
	private int autoDismiss = 5000;
	private ImageView whiteBorder;// 白色框
	private DvbPlayService.DvbServerBinder mPlayService = null;
	private Prog mProg = null;
//	private SharedPreferences mPreferences = null;

	public PlayerMenuContrl(Context paramContext, Handler paramHandler, 
				DvbPlayService.DvbServerBinder playService) {
		this.context = paramContext;
		this.handler = paramHandler;
		this.mPlayService = playService;
		init();
	}

	/**
	 * 设置背景
	 */
	private void init() {
		setBackgroundDrawable(this.context.getResources().getDrawable(
				android.R.color.transparent));
		setFocusable(true);
		setWindowLayoutMode(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		View localView = ((LayoutInflater) this.context
				.getSystemService("layout_inflater")).inflate(
				R.layout.player_menu_control, null);
		mAddFav = (TextView) localView.findViewById(R.id.menu_add_fav);// 添加节目到收藏
		mRemoveFav = (TextView) localView.findViewById(R.id.menu_remove_fav);// 移除节目
		mLeftVoice = (TextView) localView.findViewById(R.id.menu_left_voice);// 左声道
		mRightVoice = (TextView) localView.findViewById(R.id.menu_right_voice);// 右声道
        mStereoVoice = (TextView) localView.findViewById(R.id.menu_stereo_voice);// 立体声
		mOriginScale = (TextView) localView
				.findViewById(R.id.menu_origin_scale);// 视频原有比例
		mSecondScale = (TextView) localView
				.findViewById(R.id.menu_second_scale);// 视频比例为4:3
        //mThirdScale = (TextView) localView.findViewById(R.id.menu_third_scale);// 视频比例为16:9
        mOftenWatchOPen= (TextView) localView.findViewById(R.id.menu_offenwatch_open);
        mOftenWatchClose= (TextView) localView.findViewById(R.id.menu_offenwatch_close);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics( );
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        int WidthPixels = mDisplayMetrics.widthPixels;
        int HeightPixels = mDisplayMetrics.heightPixels;
		this.whiteBorder = ((ImageView) localView.findViewById(R.id.white_boder));
		//初始化高亮框
		RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(180*WidthPixels/1280,80*HeightPixels/720);
//        mPreferences = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
//		layoutparams.leftMargin = 168;
//		layoutparams.topMargin = 20;
		whiteBorder.setLayoutParams(layoutparams);
		setContentView(localView);
		mAddFav.setOnFocusChangeListener(this);
		mRemoveFav.setOnFocusChangeListener(this);
		mLeftVoice.setOnFocusChangeListener(this);
		mRightVoice.setOnFocusChangeListener(this);
        mStereoVoice.setOnFocusChangeListener(this);
		mOriginScale.setOnFocusChangeListener(this);
		mSecondScale.setOnFocusChangeListener(this);
		//mThirdScale.setOnFocusChangeListener(this);
        mOftenWatchOPen.setOnFocusChangeListener(this);
        mOftenWatchClose.setOnFocusChangeListener(this);

		mAddFav.setOnClickListener(this);
		mRemoveFav.setOnClickListener(this);
		mLeftVoice.setOnClickListener(this);
		mRightVoice.setOnClickListener(this);
        mStereoVoice.setOnClickListener(this);
		mOriginScale.setOnClickListener(this);
		mSecondScale.setOnClickListener(this);
		//mThirdScale.setOnClickListener(this);
        mOftenWatchOPen.setOnClickListener(this);
        mOftenWatchClose.setOnClickListener(this);
	}

	private float oldX,oldY;
	private float newX,newY;
	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
        DisplayMetrics mDisplayMetrics = new DisplayMetrics( );
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        int WidthPixels = mDisplayMetrics.widthPixels;
        int HeightPixels = mDisplayMetrics.heightPixels;
		
		if(arg1 == false)
		{
			int[] location = new  int[2] ;
			arg0.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
		}else
		{
			int[] location = new  int[2] ;
			arg0.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
			flyWhiteBorder(180*WidthPixels/1280,80*HeightPixels/720,location [0] ,location [1]);
		}
		this.handler.removeCallbacks(this.autoHide);
		this.handler.postDelayed(this.autoHide, this.autoDismiss);	
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
//        SharedPreferences.Editor editor = mPreferences.edit();
		switch (id) {
		case R.id.menu_add_fav:// 添加到收藏
			if (mProg != null) {
				mProg.setCollectFlag(true);
			}
			break;
		case R.id.menu_left_voice:// 设置左声道
			break;
		case R.id.menu_right_voice:// 设置右声道			
			break;
		case R.id.menu_origin_scale:// 设置视频 满屏显示
			setAspectCvrs(DvbPlayService.DvbServerBinder.ASPECT_CVRS_IGNORE);
			break;
		case R.id.menu_second_scale:// 设置视频  黑边
			setAspectCvrs(DvbPlayService.DvbServerBinder.ASPECT_CVRS_LETTERBOX);
			break;
/*
		case R.id.menu_third_scale:// 设置视频  无黑边
			setAspectCvrs(DvbPlayService.DvbServerBinder.ASPECT_CVRS_PANSCAN);
			break;
*/
		case R.id.menu_stereo_voice:// 设置立体声
			break;
		case R.id.menu_remove_fav:// 从收藏中删除该节目
			if (mProg != null) {
				mProg.setCollectFlag(false);
			}
			break;
        case R.id.menu_offenwatch_open:
            SharedPreferencesUtils.setPreferencesBoolean(context,PREFERENCES_TAG, PREFERENCES_OFTEN_WATCH_EN, true, Context.MODE_PRIVATE);
            break;
        case R.id.menu_offenwatch_close:
            SharedPreferencesUtils.setPreferencesBoolean(context,PREFERENCES_TAG, PREFERENCES_OFTEN_WATCH_EN, false, Context.MODE_PRIVATE);
            break;
		default:
			break;
		}
		initMenuControl(mProg);
		this.handler.removeCallbacks(this.autoHide);
		this.handler.postDelayed(this.autoHide, this.autoDismiss);	
	}

	public void showAtLocation(View paramView, int paramInt1, int paramInt2,
			int paramInt3) {
		this.handler.removeCallbacks(this.autoHide);
		this.handler.postDelayed(this.autoHide, this.autoDismiss);		
		super.showAtLocation(paramView, paramInt1, paramInt2, paramInt3);
	}

	public void dismiss() {
		this.handler.removeCallbacks(this.autoHide);
		super.dismiss();
	}

	/**
	 * 自动隐藏菜单栏
	 */
	Runnable autoHide = new Runnable() {
		public void run() {
			PlayerMenuContrl.this.dismiss();
		}
	};
	
	/**
	 * 白色焦点框飞动、移动、变大
	 * 
	 * @param width
	 *                白色框的宽(非放大后的)
	 * @param height
	 *                白色框的高(非放大后的)
	 * @param paramFloat1
	 *                x坐标偏移量，相对于初始的白色框的中心点
	 * @param paramFloat2
	 *                y坐标偏移量，相对于初始的白色框的中心点
	 * */
	private void flyWhiteBorder(int width, int height, float paramFloat1, float paramFloat2) {
		if ((this.whiteBorder != null)) {
			this.whiteBorder.setVisibility(View.VISIBLE);
			int mWidth = this.whiteBorder.getWidth();
			int mHeight = this.whiteBorder.getHeight();
			if (mWidth == 0 || mHeight == 0) {
				mWidth = 1;
				mHeight = 1;
			}
			ViewPropertyAnimator localViewPropertyAnimator = this.whiteBorder.animate();
			localViewPropertyAnimator.setDuration(150L);
			localViewPropertyAnimator.scaleX((float) (width * 1) / (float) mWidth);
			localViewPropertyAnimator.scaleY((float) (height * 1) / (float) mHeight);
			localViewPropertyAnimator.x(paramFloat1);
			localViewPropertyAnimator.y(paramFloat2);
			localViewPropertyAnimator.start();
		}
	}
	
	
	
    /* update ration value */
    private boolean setAspectCvrs(int cvrs) {
    	if (mPlayService != null) {
    		return mPlayService.setAspectCvrs(cvrs);
    	}
    	return false;
    }
    
    private int getAspectCvrs() {
    	if (mPlayService != null) {
    		return mPlayService.getAspectCvrs();
    	}
    	return -1;
    }
    
    public void initMenuControl(Prog prog)
    {
		int ratio = getAspectCvrs();
		Log.i("Dvb_Activity", ratio + "");
		if (prog == null) {
			return ;
		}
		switch(ratio)
		{
			case 0:
				Log.i("Dvb_Activity", "满屏");				
				mSecondScale.setTextColor(context.getResources().getColor(R.color.my_white));
				//mThirdScale.setTextColor(context.getResources().getColor(R.color.my_white));
				mOriginScale.setTextColor(context.getResources().getColor(R.color.my_yellow));
			 break;
			case 1:
				Log.i("Dvb_Activity", "原始比例");
				mSecondScale.setTextColor(context.getResources().getColor(R.color.my_yellow));
				//mThirdScale.setTextColor(context.getResources().getColor(R.color.my_white));
				mOriginScale.setTextColor(context.getResources().getColor(R.color.my_white));
				 break;
		    default:
				mSecondScale.setTextColor(context.getResources().getColor(R.color.my_white));
				//mThirdScale.setTextColor(context.getResources().getColor(R.color.my_white));
				mOriginScale.setTextColor(context.getResources().getColor(R.color.my_white));		    	
		    	break;
		}

		if (prog.getCollectFlag()) {
			mAddFav.setTextColor(context.getResources().getColor(R.color.my_yellow));
			mRemoveFav.setTextColor(context.getResources().getColor(R.color.my_white));
		} else {
			mRemoveFav.setTextColor(context.getResources().getColor(R.color.my_yellow));
			mAddFav.setTextColor(context.getResources().getColor(R.color.my_white));			
		}

        if (SharedPreferencesUtils.getPreferencesBoolean(context, PREFERENCES_TAG, PREFERENCES_OFTEN_WATCH_EN, false, Context.MODE_PRIVATE)) {
            mOftenWatchOPen.setTextColor(context.getResources().getColor(R.color.my_yellow));
            mOftenWatchClose.setTextColor(context.getResources().getColor(R.color.my_white));
        } else {
            mOftenWatchOPen.setTextColor(context.getResources().getColor(R.color.my_white));
            mOftenWatchClose.setTextColor(context.getResources().getColor(R.color.my_yellow));
        }
		mProg = prog;
    }
    
}
