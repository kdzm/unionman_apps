package com.um.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.um.controller.frequentprog.FrequentProg;
import com.um.controller.frequentprog.FrequentProgManager;
import com.um.dvb.R;
import com.um.dvbstack.Prog;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.ProgManage;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class ExitPopLayout extends PopupWindow implements OnClickListener,OnFocusChangeListener{

	private Context mContext;
	private int ids[] = {R.id.text1,R.id.text2,R.id.text3,R.id.text4,R.id.text5};
	private TextView mTextViews[] = new TextView[5];
	private ImageView mImageViews[] = new ImageView[5];
	private int imgIds[] = {R.id.reflect1,R.id.reflect2,R.id.reflect3,R.id.reflect4,R.id.reflect5};
	private Button mExitBtn;
	private ImageView whiteBorder;
	private float x = 0.0F;
	private float y = 0.0F;
	private int width = 0;// 放大前的宽
	private int height = 0;// 放大前的高
	private FrequentProg mPlayedPorg = null;

	private int CLICK_EXIT_BTN = 1;
	private int CLICK_ITEM_BTN = 2;
	private int NORMAL_EXIT = 0;
	private int EXIT_MODE = NORMAL_EXIT;
	
	private FrequentProgManager manager = null;
	
	public ExitPopLayout(Context context)
	{
		mContext = context;
		manager = new FrequentProgManager(mContext);
		init();
	}
	
	private void init()
	{
		setBackgroundDrawable(mContext.getResources().getDrawable(android.R.color.transparent));
		setFocusable(true);
		setWindowLayoutMode(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		View v = LayoutInflater.from(mContext).inflate(R.layout.exit_pop_layout, null);
		for(int i = 0; i < ids.length; i++)
		{
			mTextViews[i] = (TextView)v.findViewById(ids[i]);
			mTextViews[i].setOnClickListener(this);
			mTextViews[i].setOnFocusChangeListener(this);
			mImageViews[i]=(ImageView)v.findViewById(imgIds[i]);
			mImageViews[i].setImageBitmap(ImageReflect.createCutReflectedImage(ImageReflect.convertViewToBitmap(mTextViews[i]), 0));
		}
		whiteBorder = (ImageView)v.findViewById(R.id.white_boder1);
		this.animEffect = new ScaleAnimEffect();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        int WidthPixels = mDisplayMetrics.widthPixels;
        int HeightPixels = mDisplayMetrics.heightPixels;
		RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(180*WidthPixels/1280, 186*HeightPixels/720);
		whiteBorder.setLayoutParams(layoutparams);
		mExitBtn = (Button)v.findViewById(R.id.exit_onlive);
		mExitBtn.setOnClickListener(this);
		
		mExitBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1)
				{
					showOnFocusAnimation(-1);
				}else
				{
					showLooseFocusAinimation(-1);
				}
			}
		});
		mExitBtn.requestFocus();
		setContentView(v);
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		int i = -1;
		switch (arg0.getId()) {
		case R.id.text1:
			i = 0;
			break;
		case R.id.text2:
			i = 1;
			break;
		case R.id.text3:
			i = 2;
			break;
		case R.id.text4:
			i = 3;
			break;
		case R.id.text5:
			i = 4;
			break;
		}
		if (arg1) {
			if(i != -1)
			{
		        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
		        int WidthPixels = mDisplayMetrics.widthPixels;
		        int HeightPixels = mDisplayMetrics.heightPixels;
				showOnFocusAnimation(i);
				int[] location = new  int[2] ;
				arg0.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标			
				flyWhiteBorder(180*WidthPixels/1280,186*HeightPixels/720,location [0] ,location [1] + 6);//加6是偏移量
				Log.i("adfadf", "location0:" + location[0] + "location1:" + location[1]);
			}
			
		} else {
			showLooseFocusAinimation(i);
			whiteBorder.setVisibility(View.INVISIBLE);
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			flyWhiteBorder(width, height, x, y);
		}
	};
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
			case R.id.text1:
			case R.id.text2:
			case R.id.text3:
			case R.id.text4:
			case R.id.text5:
				TextView tv = (TextView)arg0; 
				mPlayedPorg = (FrequentProg)tv.getTag();
				EXIT_MODE = CLICK_ITEM_BTN;
				break;
			case R.id.exit_onlive:
				mPlayedPorg = null;
				EXIT_MODE = CLICK_EXIT_BTN;
				break;
			default:
				mPlayedPorg = null;
				break;			
		}
		dismiss();
	}

	public int getExitMode()
	{
		return EXIT_MODE;
	}
	
    public static int SORT_BY_DURATION = 1;
	
	/**
	 * 设置最长看的几个节目信息
	 */
	private void initExitData(FrequentProg lastFreProg) {
		// TODO Auto-generated method stub
//		ArrayList<Prog> list = getRecentProgList();
		if(lastFreProg == null)
		{
			mTextViews[0].setVisibility(View.VISIBLE);
			mImageViews[0].setVisibility(View.VISIBLE);
			mTextViews[0].setText("暂无节目");
		}else
		{
			mTextViews[0].setVisibility(View.VISIBLE);
			mTextViews[0].setText(lastFreProg.getProgName());
			mTextViews[0].setTag(lastFreProg);
			mImageViews[0].setVisibility(View.VISIBLE);
		}
		int count = -1;
		if(mFreProgList == null)
		{
			Toast.makeText(mContext, "当前无节目！", Toast.LENGTH_SHORT).show();
		}else
		{
			Log.i("ExitPopLayout" , "mFreProgList.size():" + mFreProgList.size());
			if(mFreProgList.size() > 4)
			{
				count = 4;
			}else
			{
				count = mFreProgList.size();
			}
			for(int i = 1; i <= count; i++)
			{
				FrequentProg prog = mFreProgList.get(i-1);
				mTextViews[i].setText(prog.getProgName());
				mTextViews[i].setVisibility(View.VISIBLE);
				mTextViews[i].setTag(prog);
				mImageViews[i].setVisibility(View.VISIBLE);
			}				
		}
		
	}
    private List<FrequentProg> mFreProgList = null;
	public void setDataFromActivity(FrequentProg lastFreProg)
	{
		mExitBtn.requestFocus();
		EXIT_MODE = NORMAL_EXIT;
		if(mFreProgList != null)
		{
			mFreProgList.clear();
		}
		mFreProgList = manager.getAll(SORT_BY_DURATION, ProgManage.TVPROG);
//		mProgList = list;
		initExitData(lastFreProg);
		mPlayedPorg = null;
	}
	public FrequentProg getPlayedProg() {
		return mPlayedPorg;
	}
	
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
			this.whiteBorder.bringToFront();
			this.whiteBorder.setVisibility(View.VISIBLE);
			int mWidth = this.whiteBorder.getWidth();
			int mHeight = this.whiteBorder.getHeight();
			if (mWidth == 0 || mHeight == 0) {
				mWidth = 1;
				mHeight = 1;
			}
			ViewPropertyAnimator localViewPropertyAnimator = this.whiteBorder.animate();
			localViewPropertyAnimator.setDuration(150L);
			localViewPropertyAnimator.scaleX((float) (width * 1.105) / (float) mWidth);
			localViewPropertyAnimator.scaleY((float) (height * 1.105) / (float) mHeight);
			localViewPropertyAnimator.x(paramFloat1);
			localViewPropertyAnimator.y(paramFloat2);
			localViewPropertyAnimator.start();
		}
	}
	private ScaleAnimEffect animEffect;
	/**
	 * 失去焦点的的动画动作
	 * 
	 * @param paramInt
	 *                失去焦点的item
	 * */
	private void showLooseFocusAinimation(int paramInt) {
		if(paramInt == -1)
		{
			this.animEffect.setAttributs(1.305F, 1.0F, 1.305F, 1.0F, 100L);
			Animation localAnimation = this.animEffect.createAnimation();
			mExitBtn.bringToFront();
			mExitBtn.startAnimation(localAnimation);
		}else
		{
			this.animEffect.setAttributs(1.105F, 1.0F, 1.105F, 1.0F, 100L);
			Animation localAnimation = this.animEffect.createAnimation();
			this.mTextViews[paramInt].bringToFront();			
			mImageViews[paramInt].startAnimation(localAnimation);
			this.mTextViews[paramInt].startAnimation(localAnimation);
		}
	}

	/**
	 * 获得焦点的item的动画动作
	 * 
	 * @param paramInt
	 *                获得焦点的item
	 * */
	private void showOnFocusAnimation(final int paramInt) {

		if(paramInt == -1)
		{
			this.animEffect.setAttributs(1.0F, 1.305F, 1.0F, 1.305F, 100L);
			Animation localAnimation = this.animEffect.createAnimation();
			mExitBtn.bringToFront();
			mExitBtn.startAnimation(localAnimation);
		}else
		{
			this.animEffect.setAttributs(1.0F, 1.105F, 1.0F, 1.105F, 100L);
			Animation localAnimation = this.animEffect.createAnimation();
			this.mTextViews[paramInt].bringToFront();			
			mImageViews[paramInt].startAnimation(localAnimation);
			this.mTextViews[paramInt].startAnimation(localAnimation);
		}
	}
/*	
	class SortByWatch implements Comparator<Prog> {
		public int compare(Prog p1, Prog p2) {
			return p2.getWatchTime() - p1.getWatchTime();
		}
	}	 
	
	
	private ProgList mProgList = null;
	public void setDataFromActivity(ProgList list)
	{
		mExitBtn.requestFocus();
		EXIT_MODE = NORMAL_EXIT;
		mProgList = list;
		initExitData();
		mPlayedPorg = null;
	}

	private ArrayList<Prog> getRecentProgList() {
		
		if (mProgList == null || mProgList.list.isEmpty()) {
			return null;
		}
		int progCount = mProgList.list.size();
		ArrayList<Prog> sortList = new ArrayList<Prog>();
		for (int i=0; i<progCount; i++) {
			int	progindex = Integer.valueOf(mProgList.list.get(i).get(ProgManage.PROG_VIEWINDEX));
			Prog pg = ProviderProgManage.GetInstance().getProg(progindex);
			sortList.add(pg);
		}
		Collections.sort(sortList, new SortByWatch());
		return sortList;
	}
	
	public Prog getPlayedProg() {
		return mPlayedPorg;
	}
	*/
}
