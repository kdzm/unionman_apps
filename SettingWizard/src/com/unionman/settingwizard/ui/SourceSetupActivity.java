package com.unionman.settingwizard.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.ui.ScreenSetupActivity.MyClickListener;
import com.unionman.settingwizard.util.PreferencesUtils;
import com.unionman.settingwizard.util.Util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumPictureMode;
import com.unionman.settingwizard.util.Constant;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;

public class SourceSetupActivity extends Activity implements View.OnClickListener, OnFocusChangeListener {
    private static final String TAG = "SourceSetupActivity";
    private static final int DIRECT_LEFT = 1;
    private static final int DIRECT_RIGHT = 2;
    private static final int DIRECT_UP = 3;
    private static final int DIRECT_DOWN = 4;  
 	private static final int ACTIVITY_FINISH = 0x00000001;
 	private static final int ACTIVITY_REFLASH_VIEW = 0x00000002;    
 	public static final int Context_ACTIVITY_TYPE = 0;	
    private Scroller mScroller;
 	public static boolean isexist = false;
	private int mContextType = 0;	
    public View[] mImgView;
    public View[] mImgIconView;
    public TextView[] mInnerView; 
    private ViewGroup root;
    private View  mViewFocuseMove;
    private View  mImgViewFocuseMove;
    private View  mViewNextFocuse;    
    private ArrayList<Integer> mSourceList;    
    private Button NextStepBtn;
    private Map<Integer, Integer> sourceMap = new HashMap<Integer, Integer>(); 
    private ArrayList<Integer> mFrontSourceList;
    private ArrayList<Integer> mAllSourceList;
    private ArrayList<Integer> mOtherSourceList;
    private int hdmiint;
    private int cvbsint;
    private int ypbprint;
   
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sourceselect2_view);
		hdmiint = 0;
        cvbsint = 0;
        ypbprint = 0;
        
		 mFrontSourceList = new ArrayList<Integer>();     
	     mFrontSourceList.add(EnumSourceIndex.SOURCE_ATV);
	     mFrontSourceList.add(EnumSourceIndex.SOURCE_DVBC);
	     mFrontSourceList.add(EnumSourceIndex.SOURCE_DTMB);
	        
	     mAllSourceList = new ArrayList<Integer>();
	        //获取所有端口，端口不能超过10个
	     mAllSourceList = UmtvManager.getInstance().getSourceManager().getSourceList(); 
	     mAllSourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA2); 
	     mAllSourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA);

	     mOtherSourceList = new ArrayList<Integer>();
	     mOtherSourceList = UmtvManager.getInstance().getSourceManager().getSourceList();
	     mOtherSourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA);
	     mOtherSourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA2);
	     mOtherSourceList.removeAll(mFrontSourceList);
	        
        initView();
        mScroller = new Scroller(SourceSetupActivity.this);
        isexist = true;
        doShow();
	}
	 private void initView() {
	        NextStepBtn = (Button) findViewById(R.id.btn_next_step);
	        Button LastStepBtn = (Button) findViewById(R.id.btn_last_step);
	        NextStepBtn.setOnClickListener(this);
	        LastStepBtn.setOnClickListener(this);
	        
	       mImgView = new View[] {
	               //parent.findViewById(R.id.tv_item_window),
	               findViewById(R.id.tv_item_atv),
	               findViewById(R.id.tv_item_dvbc),
	               findViewById(R.id.tv_item_dtv),
	               findViewById(R.id.tv_item_port1),
	               //parent.findViewById(R.id.tv_item_av2),
	               findViewById(R.id.tv_item_port2),
	               findViewById(R.id.tv_item_port3),
	               findViewById(R.id.tv_item_port4),
	               findViewById(R.id.tv_item_port5),
	               findViewById(R.id.tv_item_port6),
	               findViewById(R.id.tv_item_port7)
	       };
	       
	       mImgIconView = new View[] {
	    		   findViewById(R.id.view_atv),
	    		   findViewById(R.id.view_dvbc),
	    		   findViewById(R.id.view_dtv),
	    		   findViewById(R.id.view_port1),
	    		   findViewById(R.id.view_port2),
	    		   findViewById(R.id.view_port3),
	    		   findViewById(R.id.view_port4),
	    		   findViewById(R.id.view_port5),
	    		   findViewById(R.id.view_port6),
	    		   findViewById(R.id.view_port7)
	       };
	       
	       mInnerView = new TextView[] {
	               //parent.findViewById(R.id.view_window_pic),
	               (TextView)findViewById(R.id.tv_atv_txt),
	               (TextView)findViewById(R.id.tv_dvbc_txt),
	               (TextView)findViewById(R.id.tv_dtv_txt),
	               (TextView)findViewById(R.id.tv_port1_txt),
	               //(TextView)parent.findViewById(R.id.tv_av2_txt),
	               (TextView)findViewById(R.id.tv_port2_txt),
	               (TextView)findViewById(R.id.tv_port3_txt),
	               (TextView)findViewById(R.id.tv_port4_txt),
	               (TextView)findViewById(R.id.tv_port5_txt),
	               (TextView)findViewById(R.id.tv_port6_txt),
	               (TextView)findViewById(R.id.tv_port7_txt)

	       };
	       
	       ArrayList<Integer> sourceList = UmtvManager.getInstance().getSourceManager().getSourceList();
	       sourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA2);
	       sourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA);   
	       Log.i("hehe","i=size====="+(sourceList.size()));
	        for(int i=0;i<10-sourceList.size();i++){
	     	   Log.i("hehe","i======"+(9-i));
	     	   mImgView[9-i].setVisibility(View.INVISIBLE);
	        }

	        for(int i=0;i<mOtherSourceList.size();i++){
	     	   if(mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS1||
	     		  mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS2||
	     		  mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS3){
	     		  mImgIconView[i+3].setBackgroundResource(R.drawable.source_av_background);
	     	   }else if(mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR1||
	     			    mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR2){
	     		   		mImgIconView[i+3].setBackgroundResource(R.drawable.source_ypbpr_background);
	     	   }else if(mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI1||
	     			    mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI2||
	     			    mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI3||
	     			    mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI4){
	     		        mImgIconView[i+3].setBackgroundResource(R.drawable.source_hdmi_background);
	     	   }else if(mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_VGA){
	     		   mImgIconView[i+3].setBackgroundResource(R.drawable.source_vga_background);
	     	   }
	     	   
	        }
	       
	        for(int i=0;i<mOtherSourceList.size();i++){
	     	   if(mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS1||
	     		  mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS2||
	     		  mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS3){
	     		   if(getCVBSNum()==1){
	     			   mInnerView[i+3].setText(R.string.CVBS);  
	     		   }else{
	     			   if(cvbsint%3 == 0){
	         			   mInnerView[i+3].setText(R.string.CVBS1); 
	         		   }else if(cvbsint%3 == 1){
	         			   mInnerView[i+3].setText(R.string.CVBS2); 
	         		   }else if(cvbsint%3 == 2){
	         			   mInnerView[i+3].setText(R.string.CVBS3); 
	         		   }
	     			   cvbsint++;
	     		   }
	     		   
	     	   }else if(mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR1||
	     			   mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR2){
	     		   if(getYPBPRNum()==1){
	     			   mInnerView[i+3].setText(R.string.YPBPR);
	     		   }else{
	     			   if(ypbprint%2== 0){
	         			   mInnerView[i+3].setText(R.string.YPBPR1); 
	         		   }else if(ypbprint%2 == 1){
	         			   mInnerView[i+3].setText(R.string.YPBPR2); 
	         		   }
	     			   ypbprint++;
	     		   }
	     		   
	     	   }else if(mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI1||
	     			    mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI2||
	     			    mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI3||
	     			    mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI4){
	     		   if(hdmiint%4 == 0){
	     			   mInnerView[i+3].setText(R.string.HDMI1); 
	     		   }else if(hdmiint%4 == 1){
	     			   mInnerView[i+3].setText(R.string.HDMI2); 
	     		   }else if(hdmiint%4 == 2){
	     			   mInnerView[i+3].setText(R.string.HDMI3); 
	     		   }else if(hdmiint%4 == 3){
	     			   mInnerView[i+3].setText(R.string.HDMI4); 
	     		   }
	     		   hdmiint++;
	     	   }else if(mOtherSourceList.get(i)==EnumSourceIndex.SOURCE_VGA){
	     		   mInnerView[i+3].setText(R.string.VGA);
	     	   }
	        }
	        
	        
	       for (int i = 0; i < mImgView.length; i++) {
	           mImgView[i].setOnClickListener(this);
	           mImgView[i].getBackground().setAlpha(0);
	           mImgView[i].setOnFocusChangeListener(this);
	       }
	       mSourceList = new ArrayList<Integer>();
	       mSourceList.add(EnumSourceIndex.SOURCE_ATV);
	       mSourceList.add(EnumSourceIndex.SOURCE_DVBC);
	       mSourceList.add(EnumSourceIndex.SOURCE_DTMB);
	       
	       for(int j=0;j<mOtherSourceList.size();j++){
	    	   mSourceList.add(mOtherSourceList.get(j)); 
	       }
	       
	       lastSourceIdShow();
	       
	       root = (ViewGroup)findViewById(R.id.source_select2_id);
	       mViewFocuseMove = findViewById(R.id.focuse_item);
	       mImgViewFocuseMove = findViewById(R.id.focuse_img);
	       
	    }
	    private void lastSourceIdShow(){
	        sourceMap.put(0, EnumSourceIndex.SOURCE_ATV);
	        sourceMap.put(1, EnumSourceIndex.SOURCE_DVBC);
	        sourceMap.put(2, EnumSourceIndex.SOURCE_DTMB);
	        for(int j=0;j<mOtherSourceList.size();j++){
	        	sourceMap.put(j+3, mOtherSourceList.get(j));
	        }
	        
	        int pos = 0;
	        int lastid = UmtvManager.getInstance().getSourceManager().getLastSourceId();	
	        for(Integer obj : sourceMap.keySet()){
	        	Integer value = sourceMap.get(obj);
	        	if (value == lastid)
	        	{
	        		break;
	        	}
	        	pos++;
	        }
	        if (pos >= mImgView.length)
	        {
	        	pos = 0;
	        }
	        
	        mInnerView[pos].setTextColor(SourceSetupActivity.this.getResources().getColor(R.color.blue));
	    }
	    
	    public void doShow() {
	    	NextStepBtn.requestFocus();
	 
	        reflashSourceIcon();
	      }

	    private void reflashSourceIcon(){
	    	boolean bfind = false;
	    	ArrayList<Integer> sourceList = UmtvManager.getInstance().getSourceManager().getAvailSourceList();
	    	Log.d(TAG, "sourceList cnt:"+sourceList.size());
	    	for (int i = 3; i < mSourceList.size(); i++){
	    		bfind = false;
	    		//Log.d(TAG, "mSourceList.get(i):"+mSourceList.get(i));
	    		for (int j = 0; j < sourceList.size(); j++){
	    			//Log.d(TAG, "sourceList.get(j):"+sourceList.get(j));
	    			if (mSourceList.get(i).intValue() == sourceList.get(j).intValue()){
	    				Log.d(TAG, "mSourceList match i:"+i);
	    				bfind = true;
	    			}
	    		}
	    		
	    		if (bfind){
					mImgView[i].setSelected(true);
	    		}else{
	    			mImgView[i].setSelected(false);
	    		}
	    	}
	    	
	    //	invalidate();
	    }
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {

	    	Log.d(TAG,"leon---- hasFocus:"+hasFocus+",v.getId():"+v.getId());
	        if (hasFocus) {
	            v.bringToFront();
	            if (!mScroller.computeScrollOffset())
	            {
	            	v.getBackground().setAlpha(255);
	            }
	            v.animate().scaleX(1.0f).scaleY(1.0f)
	            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
	        } else {
	            v.getBackground().setAlpha(0);
	            v.animate().scaleX(1.0f).scaleY(1.0f)
	                 .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
	        }
	    }
		@Override
		public void onClick(View v) {
			Intent intent;
			 int destid = 0;
			 int[] mViewId = new int[] {
		              R.id.tv_item_atv,
		              R.id.tv_item_dvbc,
		              R.id.tv_item_dtv,
		              R.id.tv_item_port1,
		              R.id.tv_item_port2,
		              R.id.tv_item_port3,
		              R.id.tv_item_port4,
		              R.id.tv_item_port5,
		              R.id.tv_item_port6,
		              R.id.tv_item_port7
		        };
			 if(v.getId() == R.id.btn_last_step){
				 intent = new Intent(SourceSetupActivity.this, NetworkSetupActivity.class);
	                startActivity(intent);
	                finish();
			 }else if(v.getId() == R.id.btn_next_step){
				 boolean hasPreInstalled = PreferencesUtils.getBoolean(SourceSetupActivity.this, "has_pre_install", false);
					intent = new Intent();
	                if (hasPreInstalled && PreferencesUtils.getBoolean(SourceSetupActivity.this, "boot_by_provision", false)) {
	                    intent.setClass(this, SetupFinishActivity.class);
	                } else {
	                    intent.putExtra("isInSettingWizard", true);
	                    intent.setClassName("com.unionman.dvbcitysetting", "com.unionman.dvbcitysetting.CitySettingActivity");
	                }
			    	startActivity(intent);
			 }else{
				 for(int j=0;j<mAllSourceList.size();j++){
		        		if(v.getId() == mViewId[j]){
		        			setChangeForClick(j);
		 	                destid = mAllSourceList.get(j);
		 	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
		        		}
		        	}
			 }
			/*switch (v.getId()) {	
			case R.id.btn_last_step:
                intent = new Intent(SourceSetupActivity.this, NetworkSetupActivity.class);
                startActivity(intent);
                finish();
				break;
			case R.id.btn_next_step:
	                boolean hasPreInstalled = PreferencesUtils.getBoolean(SourceSetupActivity.this, "has_pre_install", false);
					intent = new Intent();
	                if (hasPreInstalled && PreferencesUtils.getBoolean(SourceSetupActivity.this, "boot_by_provision", false)) {
	                    intent.setClass(this, SetupFinishActivity.class);
	                } else {
	                    intent.putExtra("isInSettingWizard", true);
	                    intent.setClassName("com.unionman.dvbcitysetting", "com.unionman.dvbcitysetting.CitySettingActivity");
	                }
			    	startActivity(intent);
			    	finish();		          
				break;
	           case R.id.tv_item_dvbc:
	                setChangeForClick(1);
	                destid = EnumSourceIndex.SOURCE_DVBC;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;
	            case R.id.tv_item_dtv:
	                setChangeForClick(2);
	                destid = EnumSourceIndex.SOURCE_DTMB;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;
	            case R.id.tv_item_atv:
	                setChangeForClick(0);
	                destid = EnumSourceIndex.SOURCE_ATV;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;
	            case R.id.tv_item_av1:
	                setChangeForClick(3);
	                destid = EnumSourceIndex.SOURCE_CVBS1;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;
	            case R.id.tv_item_ypbpr:
	                setChangeForClick(4);
	                destid = EnumSourceIndex.SOURCE_YPBPR1;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;
	            case R.id.tv_item_hdmi1:
	                setChangeForClick(5);
	                destid = EnumSourceIndex.SOURCE_HDMI1;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;
	            case R.id.tv_item_hdmi2:
	                setChangeForClick(6);
	                destid = EnumSourceIndex.SOURCE_HDMI2;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;
	            case R.id.tv_item_hdmi3:
	                setChangeForClick(7);
	                destid = EnumSourceIndex.SOURCE_HDMI3;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;
	            case R.id.tv_item_vga:
	                setChangeForClick(8);
	                destid = EnumSourceIndex.SOURCE_VGA;
	                UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	                break;	
			}*/
			
		}
		
	    private void foucesStartMove(int direct){
			int forwardid = 0;
			View focuseView = root.getFocusedChild();
			//focuseView.setVisibility(View.INVISIBLE);
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
				View nextView = findViewById(forwardid);
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
					mImgViewFocuseMove.setVisibility(View.VISIBLE);
					Log.d(TAG,"leon: [startx:"+startx+"][starty:"+starty+"][dx:"+dx+"][dy:"+dy+"]");
					mScroller.startScroll(0, 0, dx, dy, 200);
					Log.d(TAG,"leon: startScroll");
				}
			}
		}
   
	    private void enterToPlay(View v){
	        int curId = UmtvManager.getInstance().getSourceManager().getCurSourceId(0);
	        if (Constant.LOG_TAG) {
	            Log.d(TAG, "getCurSourceId curId = " + curId);
	        }
	        Intent intent = new Intent();
	        int destid = 0;
	        
	        int[] mViewId = new int[] {
	                R.id.tv_item_atv,
	                R.id.tv_item_dvbc,
	                R.id.tv_item_dtv,
	                R.id.tv_item_port1,
	                R.id.tv_item_port2,
	                R.id.tv_item_port3,
	                R.id.tv_item_port4,
	                R.id.tv_item_port5,
	                R.id.tv_item_port6,
	                R.id.tv_item_port7
	          };
	        
	        for(int j=0;j<mAllSourceList.size();j++){
        		if(v.getId() == mViewId[j]){
        			intent.putExtra("SourceName", mAllSourceList.get(j));
        			//intent.putExtra("SourceNameStr", mInnerView[j].getText());
        			Log.i("txt", "==txt="+mInnerView[j].getText());
                    destid = mAllSourceList.get(j);
        		}
        	}
	        /*switch (v.getId()) {
	            case R.id.tv_item_dvbc:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DVBC);
	                destid = EnumSourceIndex.SOURCE_DVBC;
	                break;
	            case R.id.tv_item_dtv:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DTMB);
	                destid = EnumSourceIndex.SOURCE_DTMB;
	                break;
	            case R.id.tv_item_atv:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_ATV);
	                destid = EnumSourceIndex.SOURCE_ATV;
	                break;
	            case R.id.tv_item_av1:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS1);
	                destid = EnumSourceIndex.SOURCE_CVBS1;
	                break;
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS2);
	                destid = EnumSourceIndex.SOURCE_CVBS2;
	                break;
	            case R.id.tv_item_ypbpr:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_YPBPR1);
	                destid = EnumSourceIndex.SOURCE_YPBPR1;
	                break;
	            case R.id.tv_item_hdmi1:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI1);
	                destid = EnumSourceIndex.SOURCE_HDMI1;
	                break;
	            case R.id.tv_item_hdmi2:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI2);
	                destid = EnumSourceIndex.SOURCE_HDMI2;
	                break;
	            case R.id.tv_item_hdmi3:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI3);
	                destid = EnumSourceIndex.SOURCE_HDMI3;
	                break;
	            case R.id.tv_item_vga:
	                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_VGA);
	                destid = EnumSourceIndex.SOURCE_VGA;
	                break;
	            default:
	                break;
	        }*/
	        if (Constant.LOG_TAG) {
	            Log.d(TAG, "selectSource start,set destid = " + destid);
	        }
	        RectInfo rect = new RectInfo();
	        if ((destid == EnumSourceIndex.SOURCE_ATV)
	                || (destid >= EnumSourceIndex.SOURCE_CVBS1 && destid <= EnumSourceIndex.SOURCE_HDMI4)) {

	            if (curId == EnumSourceIndex.SOURCE_DVBC
	                 || curId == EnumSourceIndex.SOURCE_DTMB) {
	                //Util.notifyDTVStopPlay(mContext);
	            }
	            Log.d(TAG, "Scaler ATV Windows to full");
	            rect.setX(0);
	            rect.setY(0);
	            rect.setW(1920);
	            rect.setH(1080);
	            UmtvManager.getInstance().getSourceManager().setWindowRect(rect, 0);
	        }
	        else if (destid == EnumSourceIndex.SOURCE_DVBC || destid == EnumSourceIndex.SOURCE_DTMB) {
	            rect.setX(0);
	            rect.setY(0);
	            rect.setW(1920);
	            rect.setH(1080);
	            UmtvManager.getInstance().getSourceManager().setWindowRect(rect, 0);
	            Log.d(TAG,"--12222--mainpage tv current ID ="+curId);

	            UmtvManager.getInstance().getSourceManager().deselectSource(curId, true);
	            UmtvManager.getInstance().getSourceManager().selectSource(destid, 0);
	            //Util.notifyDTVStartPlay(mContext, true);
	            if (Constant.LOG_TAG) {
	                Log.d(TAG, "selectSource done,now getCurSourceId = "
	                        + UmtvManager.getInstance().getSourceManager().getCurSourceId(0));
	            }
	        }
	        
	        if (destid == EnumSourceIndex.SOURCE_DVBC || destid == EnumSourceIndex.SOURCE_DTMB){

	            intent.setAction(Constant.INTENT_DTV);
	        }
	        else if (destid == EnumSourceIndex.SOURCE_ATV)
	        {
	        	intent.setAction(Constant.INTENT_ATV);
	        }
	        else
	        {
	        	intent.setAction(Constant.INTENT_PORT);
	        }
	        
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
	        
	    }  

	   /**
	    * 鏍规嵁position鍊艰缃瓧浣撻鑹�
	    * @param position
	    */
	 private void setChangeForClick(int position){
		 for(int m=0;m<mInnerView.length;m++){
			 if(m==position){
				 mInnerView[m].setTextColor(SourceSetupActivity.this.getResources().getColor(R.color.blue));
			 }else{
				 mInnerView[m].setTextColor(SourceSetupActivity.this.getResources().getColor(R.color.white));
			 }			 
		 }

	 }
	 
	    private long mExitTime = 0;

	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {  
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_VOLUME_UP :
        case KeyEvent.KEYCODE_VOLUME_DOWN :
        	Log.i(TAG,"click keyCode="+keyCode);
        	break;
        default:
        	Log.i(TAG,"click keyCode="+keyCode+" return true");
        	return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
	    
	    
	    public int getCVBSNum(){
	    	int j=0;
	    	ArrayList<Integer> SourceListTmp = new ArrayList<Integer>();
	    	SourceListTmp = UmtvManager.getInstance().getSourceManager().getSourceList();
	    	for(int i=0;i<SourceListTmp.size();i++){
	    		if(SourceListTmp.get(i)==EnumSourceIndex.SOURCE_CVBS1||
	    		   SourceListTmp.get(i)==EnumSourceIndex.SOURCE_CVBS2||
	    		   SourceListTmp.get(i)==EnumSourceIndex.SOURCE_CVBS3){
	    			j++;
	    		}
	    	}
	    	return j;
	    }
	    public int getYPBPRNum(){
	    	int j=0;
	    	ArrayList<Integer> SourceListTmp = new ArrayList<Integer>();
	    	SourceListTmp = UmtvManager.getInstance().getSourceManager().getSourceList();
	    	for(int i=0;i<SourceListTmp.size();i++){
	    		if(SourceListTmp.get(i)==EnumSourceIndex.SOURCE_YPBPR1||
	    		   SourceListTmp.get(i)==EnumSourceIndex.SOURCE_YPBPR2){
	    			j++;
	    		}
	    	}
	    	return j;
	    }
		public int getHDMINum(){
	    	int j=0;
	     	ArrayList<Integer> SourceListTmp = new ArrayList<Integer>();
	    	SourceListTmp = UmtvManager.getInstance().getSourceManager().getSourceList();
	    	for(int i=0;i<SourceListTmp.size();i++){
	    		if(SourceListTmp.get(i)==EnumSourceIndex.SOURCE_HDMI1||
	    		   SourceListTmp.get(i)==EnumSourceIndex.SOURCE_HDMI2||
	    	       SourceListTmp.get(i)==EnumSourceIndex.SOURCE_HDMI3||
	    		   SourceListTmp.get(i)==EnumSourceIndex.SOURCE_HDMI4){
	    			j++;
	    		}
	    	}
	    	return j;
	    }
}
