package com.source.widget;

import com.source.R;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.source.SourceSelect2Activity;
import com.source.SourceService;
import com.source.util.Constant;
import com.source.util.Util;

import android.R.color;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.source.interfaces.SourceManagerInterface;
import android.os.SystemProperties;
import com.source.interfaces.PictureInterface;
import android.os.SystemProperties;

public class SourceSelectLayout2 extends RelativeLayout implements View.OnClickListener, OnFocusChangeListener, OnKeyListener{
 	private static final String TAG = "SourceSelectLayout2";
    private static final int DIRECT_LEFT = 1;
    private static final int DIRECT_RIGHT = 2;
    private static final int DIRECT_UP = 3;
    private static final int DIRECT_DOWN = 4;
 	private static final int ACTIVITY_FINISH = 0x00000001;
 	private static final int ACTIVITY_REFLASH_VIEW = 0x00000002;
 	public static final int Context_ACTIVITY_TYPE = 0;
 	public static final int Context_SERVICE_TYPE = 1;
 	private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
 	public static boolean isexist = false;
	private int mContextType = 0;
    public View[] mImgView;
    public ImageView[] mImgIconView;
    public View[] mImgFocusView;
    public TextView[] mInnerView;
    private Context mContext;
    private ViewGroup root;
    private View  mViewFocuseMove;
    private View  mImgViewFocuseMove;
    private View  mViewNextFocuse;
    private View parent;
    private Scroller mScroller;
    private ArrayList<Integer> mSourceList;
    private Map<Integer, Integer> sourceMap = new HashMap<Integer, Integer>();
    private ArrayList<Integer> mFrontSourceList;
    private ArrayList<Integer> mAllSourceList;
    private ArrayList<Integer> mOtherSourceList;
    private int hdmiint;
    private int cvbsint;
    private int ypbprint;
    
    public SourceSelectLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        hdmiint = 0;
        cvbsint = 0;
        ypbprint = 0;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        parent = inflater.inflate(R.layout.sourceselect2_view, this);
        
        mFrontSourceList = new ArrayList<Integer>();
        mFrontSourceList.add(EnumSourceIndex.SOURCE_MEDIA);       
        mFrontSourceList.add(EnumSourceIndex.SOURCE_ATV);
        mFrontSourceList.add(EnumSourceIndex.SOURCE_DVBC);
        mFrontSourceList.add(EnumSourceIndex.SOURCE_DTMB);
        
        mAllSourceList = new ArrayList<Integer>();
        mAllSourceList = SourceManagerInterface.getSourceList();
        mAllSourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA2);
        mOtherSourceList = new ArrayList<Integer>();
        mOtherSourceList = SourceManagerInterface.getSourceList();
        mOtherSourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA2);
        mOtherSourceList.removeAll(mFrontSourceList);
        
        initView(parent);
        mScroller = new Scroller(mContext);
        isexist = true;
        
        registerListener();
        registerSystemDialogCloseReceiver();
        
        doShow();
    }

    public SourceSelectLayout2(Context context) {
        super(context);
    }
    
    private void initView(View parent) {
        
       mImgView = new View[] {
               //parent.findViewById(R.id.tv_item_window),
               parent.findViewById(R.id.tv_item_cmcc),
               parent.findViewById(R.id.tv_item_atv),
//               parent.findViewById(R.id.tv_item_dvbc),yyf
               parent.findViewById(R.id.tv_item_dtv),
               parent.findViewById(R.id.tv_item_port1),
               parent.findViewById(R.id.tv_item_port2),
               parent.findViewById(R.id.tv_item_port3),
               parent.findViewById(R.id.tv_item_port4),
               parent.findViewById(R.id.tv_item_port5),
               parent.findViewById(R.id.tv_item_port6)

       };
   
       mImgIconView = new ImageView[] {
               //parent.findViewById(R.id.tv_item_window),
    		   (ImageView)parent.findViewById(R.id.view_cmcc),
    		   (ImageView)parent.findViewById(R.id.view_atv),
//    		   (ImageView)parent.findViewById(R.id.view_dvbc),yyf
    		   (ImageView)parent.findViewById(R.id.view_dtv),
    		   (ImageView)parent.findViewById(R.id.view_port1),
    		   (ImageView)parent.findViewById(R.id.view_port2),
    		   (ImageView)parent.findViewById(R.id.view_port3),
    		   (ImageView)parent.findViewById(R.id.view_port4),
    		   (ImageView)parent.findViewById(R.id.view_port5),
    		   (ImageView)parent.findViewById(R.id.view_port6)
       };
       mImgFocusView = new View[] {
               //parent.findViewById(R.id.tv_item_window),
               parent.findViewById(R.id.view_cmcc_focus),
               parent.findViewById(R.id.view_atv_focus),
//               parent.findViewById(R.id.view_dvbc_focus),yyf
               parent.findViewById(R.id.view_dtv_focus),
               parent.findViewById(R.id.view_port1_focus),
               parent.findViewById(R.id.view_port2_focus),
               parent.findViewById(R.id.view_port3_focus),
               parent.findViewById(R.id.view_port4_focus),
               parent.findViewById(R.id.view_port5_focus),
               parent.findViewById(R.id.view_port6_focus)
   
       };
       mInnerView = new TextView[] {
               //parent.findViewById(R.id.view_window_pic),
               (TextView)parent.findViewById(R.id.tv_cmcc_txt),
               (TextView)parent.findViewById(R.id.tv_atv_txt),
//               (TextView)parent.findViewById(R.id.tv_dvbc_txt),yyf
               (TextView)parent.findViewById(R.id.tv_dtv_txt),
               (TextView)parent.findViewById(R.id.tv_port1_txt),
               (TextView)parent.findViewById(R.id.tv_port2_txt),
               (TextView)parent.findViewById(R.id.tv_port3_txt),
               (TextView)parent.findViewById(R.id.tv_port4_txt),
               (TextView)parent.findViewById(R.id.tv_port5_txt),
               (TextView)parent.findViewById(R.id.tv_port6_txt)
       };
       ArrayList<Integer> sourceList = SourceManagerInterface.getSourceList();
       sourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA2); 
       sourceList.remove((Integer)EnumSourceIndex.SOURCE_DVBC);
       Log.i(TAG,"=============sourceList.size()="+sourceList.size()+" mOtherSourceList.size()="+mOtherSourceList.size());
       //othersourceList中包含了视频、HDMI1、HDMI2。
       for(int i=0;i<9-sourceList.size();i++){
    	   mImgView[8-i].setVisibility(View.INVISIBLE);
    	   Log.i(TAG,"mImgViewt["+(8-i)+"].setVisibility(View.INVISIBLE)");
       }
	
       for(int i=0;i<mOtherSourceList.size();i++){
    	   mInnerView[i+3].setText(getSourceStrById(mOtherSourceList.get(i)));  
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
       for (int i = 0; i < mImgView.length; i++) {
    	   mImgView[i].setOnKeyListener(this);
           mImgView[i].setOnClickListener(this);
           mImgFocusView[i].getBackground().setAlpha(0);
           mImgView[i].setOnFocusChangeListener(this);
       }
       mSourceList = new ArrayList<Integer>();
       mSourceList.add(EnumSourceIndex.SOURCE_MEDIA);       
       mSourceList.add(EnumSourceIndex.SOURCE_ATV);
//       mSourceList.add(EnumSourceIndex.SOURCE_DVBC);
       mSourceList.add(EnumSourceIndex.SOURCE_DTMB);

       for(int j=0;j<mOtherSourceList.size();j++){
    	   mSourceList.add(mOtherSourceList.get(j)); 
       }

       lastSourceIdShow();
       
       root = (ViewGroup)parent.findViewById(R.id.source_select2_id);
       mViewFocuseMove = parent.findViewById(R.id.focuse_item);
       mImgViewFocuseMove = parent.findViewById(R.id.focuse_img);
    }
    
    private void lastSourceIdShow(){
        sourceMap.put(0, EnumSourceIndex.SOURCE_MEDIA);
        sourceMap.put(1, EnumSourceIndex.SOURCE_ATV);
        sourceMap.put(2, EnumSourceIndex.SOURCE_DTMB);
        for(int j=0;j<mOtherSourceList.size();j++){
        	sourceMap.put(j+3,mOtherSourceList.get(j)); 
        }
       
        int pos = 0;
        String fullScreen_Source = SystemProperties.get("persist.sys.fullScreen_Source",""+EnumSourceIndex.SOURCE_MEDIA); 
        int lastid = Integer.parseInt(fullScreen_Source); 
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
        mInnerView[pos].setTextColor(mContext.getResources().getColor(R.color.blue));
    }
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ACTIVITY_FINISH:
            	Log.d(TAG,"leon---- ACTIVITY_FINISH");
            	exitActivity();
            	break;
            case ACTIVITY_REFLASH_VIEW:
            	Log.d(TAG,"leon---- ACTIVITY_REFLASH_VIEW");
            	reflashSourceIcon();
            	break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };
    
 
    
    @Override  
    public boolean onKey(View v, int keyCode, KeyEvent event) {
    	
    	Log.d(TAG,"leon---- keyCode:"+keyCode+",v.getId():"+v.getId()+",event.getAction:"+event.getAction());
    	if (event.getAction() == KeyEvent.ACTION_DOWN){
    		delay();
    	}
    	
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
        	if (keyCode == KeyEvent.KEYCODE_MENU
            		|| keyCode == KeyEvent.KEYCODE_TVSETUP
            		||keyCode == KeyEvent.KEYCODE_BACK){
        		Log.d(TAG," keyCode:"+keyCode+",v.getId():"+v.getId());
            	doExit();
            	return true;
            }else if (keyCode == KeyEvent.KEY_SOURCEENTER){
            	enterToPlay(v);
                doExit();
            	return true;
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
            	//foucesStartMove(DIRECT_LEFT);
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            	//foucesStartMove(DIRECT_RIGHT);
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
            	//foucesStartMove(DIRECT_UP);
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            	//foucesStartMove(DIRECT_DOWN);
            }
        } 
        else if (event.getAction() == KeyEvent.ACTION_UP){
        }
        return false;  
    } 
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    	Log.d(TAG,"leon---- hasFocus:"+hasFocus+",v.getId():"+v.getId());
        if (hasFocus) {
            /*v.bringToFront();
            //if (!mScroller.computeScrollOffset())
            {
            	v.getBackground().setAlpha(255);
            }
            v.animate().scaleX(1.0f).scaleY(1.0f)
            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();*/
        	if( v.equals(mImgView[0])){
        		mImgFocusView[0].getBackground().setAlpha(255);
        	}else if(v.equals(mImgView[1])){
        		mImgFocusView[1].getBackground().setAlpha(255);
        	}else if(v.equals(mImgView[2])){
        		mImgFocusView[2].getBackground().setAlpha(255);
        	}else if( v.equals(mImgView[3])){
        		mImgFocusView[3].getBackground().setAlpha(255);
        	}else if( v.equals(mImgView[4])){
        		mImgFocusView[4].getBackground().setAlpha(255);
        	}else if( v.equals(mImgView[5])){
        		mImgFocusView[5].getBackground().setAlpha(255);
        	}else if( v.equals(mImgView[6])){
        		mImgFocusView[6].getBackground().setAlpha(255);
        	}else if( v.equals(mImgView[7])){
        		mImgFocusView[7].getBackground().setAlpha(255);
        	}else if( v.equals(mImgView[8])){
        		mImgFocusView[8].getBackground().setAlpha(255);
        	}/*else if( v.equals(mImgView[9])){
        		mImgFocusView[9].getBackground().setAlpha(255);
        	}*/
        	
        } else {
           /* v.getBackground().setAlpha(0);
            v.animate().scaleX(1.0f).scaleY(1.0f)
                 .setDuration(Constant.SCARE_ANIMATION_DURATION).start();*/
        	if( v.equals(mImgView[0])){
        		mImgFocusView[0].getBackground().setAlpha(0);
        	}else if(v.equals(mImgView[1])){
        		mImgFocusView[1].getBackground().setAlpha(0);
        	}else if(v.equals(mImgView[2])){
        		mImgFocusView[2].getBackground().setAlpha(0);
        	}else if( v.equals(mImgView[3])){
        		mImgFocusView[3].getBackground().setAlpha(0);
        	}else if( v.equals(mImgView[4])){
        		mImgFocusView[4].getBackground().setAlpha(0);
        	}else if( v.equals(mImgView[5])){
        		mImgFocusView[5].getBackground().setAlpha(0);
        	}else if( v.equals(mImgView[6])){
        		mImgFocusView[6].getBackground().setAlpha(0);
        	}else if( v.equals(mImgView[7])){
        		mImgFocusView[7].getBackground().setAlpha(0);
        	}else if( v.equals(mImgView[8])){
        		mImgFocusView[8].getBackground().setAlpha(0);
        	}/*else if( v.equals(mImgView[9])){
        		mImgFocusView[9].getBackground().setAlpha(0);
        	}*/
        	
        }
    }
    
    @Override
    public void onClick(View v) {

    	Log.d(TAG,"leon---- onClick:"+"v.getId():"+v.getId());
    	enterToPlay(v);
        doExit();
    }
    
    public void doShow() {
       
      requestFocusBySourceID();
      reflashSourceIcon();
    }

    /**
     * On the basis of the reference, or select the default
     */
    private void requestFocusBySourceID() {
    	String fullScreen_Source = SystemProperties.get("persist.sys.fullScreen_Source",""+EnumSourceIndex.SOURCE_MEDIA); 
        int curId = Integer.parseInt(fullScreen_Source);
        if(curId == EnumSourceIndex.SOURCE_MEDIA)  {
    		mImgView[Constant.NUMBER_0].requestFocus();
    	}else if(curId == EnumSourceIndex.SOURCE_ATV){
    		mImgView[Constant.NUMBER_1].requestFocus();
    	}else if(curId == EnumSourceIndex.SOURCE_DVBC){
//    		mImgView[Constant.NUMBER_2].requestFocus();
    	}else if(curId == EnumSourceIndex.SOURCE_DTMB){
//    		mImgView[Constant.NUMBER_3].requestFocus();
    		mImgView[Constant.NUMBER_2].requestFocus();
    	}
    	else{
    		
    		for(int i=0;i<mOtherSourceList.size();i++){
    			if(curId==mOtherSourceList.get(i)){
    				mImgView[i+3].requestFocus();
    			}
    			
    		}
    		
    	}
    
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
    	Log.d(TAG,"onWindowFocusChanged hasWindowFocus:"+hasWindowFocus);
        if (hasWindowFocus) {
            delay();
            //registerListener();
            //registerSystemDialogCloseReceiver();
        } else {
        	//mHandler.removeMessages(ACTIVITY_FINISH);
        	//unregisterListener();
        	//unregisterSystemDialogCloseReceiver();
        }

    }
    
    public void setContextType(int type){
    	
    	mContextType = type;
    }
    
    public int getmContextType(){
    	return mContextType;
    }
    
    public void delay() {
    	Log.d(TAG, "leon... delay");
    	mHandler.removeMessages(ACTIVITY_FINISH);
        Message message = new Message();
        message.what = ACTIVITY_FINISH;
        //mHandler.sendMessageDelayed(message, 30000);
        mHandler.sendEmptyMessageDelayed(ACTIVITY_FINISH, 30000);
    }
    
    public void doExit()
    {
    	Log.d(TAG, "doExit");
    	
    	unregisterListener();
    	unregisterSystemDialogCloseReceiver();
    	
    	mHandler.removeMessages(ACTIVITY_FINISH);

    	if (mContextType == Context_ACTIVITY_TYPE){
	    	((SourceSelect2Activity) mContext).doExit();
    	}
    	else{
	    	((SourceService) mContext).doExit();
    	}
    	
    	isexist = false;
    }
    
    private void reflashSourceIcon(){
    	boolean bfind = false;
    	ArrayList<Integer> sourceList = SourceManagerInterface.getAvailSourceList();
    	Log.d(TAG, "sourceList cnt:"+sourceList.size());
    	for (int i = 3; i < mSourceList.size(); i++){
    		bfind = false;
    		for (int j = 0; j < sourceList.size(); j++){
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
    	
    	invalidate();
    }
    
    private void registerListener() {
    	Log.d(TAG, "  registerListener ");
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGIN,
                onPlayerListener);
        
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGOUT,
                onPlayerListener);
    }
    
    private void unregisterListener() {
    	Log.d(TAG, "  unregisterListener ");
        UmtvManager.getInstance().unregisterListener(TVMessage.HI_TV_EVT_PLUGIN,
                onPlayerListener);
        
        UmtvManager.getInstance().unregisterListener(TVMessage.HI_TV_EVT_PLUGOUT,
                onPlayerListener);
    }
    
    OnPlayerListener onPlayerListener = new OnPlayerListener() {

        @Override
        public void onPCAutoAdjustStatus(int arg0) {
            Log.d(TAG, "  onPCAutoAdjustStatus  arg0: " + arg0);
        }
        
        @Override
        public void onSignalStatus(int arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSignalStatus  arg0: " + arg0);
            }
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
            mHandler.sendEmptyMessageDelayed(ACTIVITY_REFLASH_VIEW, 3000);
        }

        @Override
        public void onSrcDetectPlugout(ArrayList<Integer> arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugout  arg0: " + arg0);
            }
            
            reflashSourceIcon();
        }

		@Override
        public void onSelectSource(int  arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSelectSource  arg0: " + arg0);
            }
        }
	@Override
        public void onSelectSourceComplete(int  arg0,int arg1,int arg2) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSelectSourceComplete  arg0: " + arg0);
            }
            Log.d(TAG, "===yiyonghui=== ");
        }
		
		@Override
        public void onPlayLock(ArrayList<Integer> list) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onPlayLock  arg0: " + list);
            }
        }
    };
    
    @Override
	public void computeScroll() {
		super.computeScroll();
		Log.d(TAG,"leon: come to computeScroll");
		
        if (mScroller.computeScrollOffset()) {
        	mViewFocuseMove.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        	mViewFocuseMove.bringToFront();
            postInvalidate();
            Log.d(TAG,"leon: come to computeScroll:move");
        }else{
        	mImgViewFocuseMove.setVisibility(View.INVISIBLE);
        	if (mViewNextFocuse != null)
        		mViewNextFocuse.getBackground().setAlpha(255);
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
				RelativeLayout.LayoutParams relativeParams = 
						(RelativeLayout.LayoutParams) mImgViewFocuseMove.getLayoutParams();
				relativeParams.height = nextView.getHeight();
				relativeParams.width = nextView.getWidth();
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

    private void clearAllRecentTask() {
    	ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
    	List<RecentTaskInfo> list = am.getRecentTasks(4096, 0);
    	if (list != null) {
	    	for (RecentTaskInfo ti : list) {
	    		if (ti != null) {
	    			am.removeTask(ti.id, ActivityManager.REMOVE_TASK_KILL_PROCESS);
	    		}
	    	}
    	}
    }
    
    private void enterToPlay(View v){
        int curId = SourceManagerInterface.getCurSourceId();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "=====================getCurSourceId curId = " + curId);
        }
        if (false) {
        	clearAllRecentTask();
        } else {
        	Log.d(TAG, "Not clear Recent Task.");
        }
        
        Intent intent = new Intent();
        int destid = 0;
        
        int []mViewId = new int[] {
              R.id.tv_item_atv,
              -1/*之前的DVBC*/,
              R.id.tv_item_dtv,
              R.id.tv_item_port1,
              R.id.tv_item_port2,
              R.id.tv_item_port3,
              R.id.tv_item_port4,
              R.id.tv_item_port5,
              R.id.tv_item_port6,
        };
		if (v.getId() == R.id.tv_item_cmcc) {
			destid = EnumSourceIndex.SOURCE_MEDIA;
		} else {
			for (int j = 0; j < mAllSourceList.size(); j++) {
				if (v.getId() == mViewId[j]) {
					intent.putExtra("SourceName", mAllSourceList.get(j));
					destid = mAllSourceList.get(j);
					break;
				}
			}
		}

       Log.d(TAG, "===========================selectSource start,set destid = " + destid);
       boolean iscmccRunning = Util.isCmccApkRunningAtTop(mContext);
       Log.d(TAG, "iscmccRunning = " + iscmccRunning);
      if( !iscmccRunning && curId == destid && curId !=EnumSourceIndex.SOURCE_MEDIA   ||  iscmccRunning &&curId == destid){
       	 Log.d(TAG, "*************curId == destid,no need to change source********************") ;
        }else{
        RectInfo rect = new RectInfo();
        if ((destid == EnumSourceIndex.SOURCE_ATV)
                || (destid >= EnumSourceIndex.SOURCE_CVBS1 && destid <= EnumSourceIndex.SOURCE_HDMI4)) {

            if (curId == EnumSourceIndex.SOURCE_DVBC
                 || curId == EnumSourceIndex.SOURCE_DTMB) {
            }
            Log.d(TAG, "Scaler ATV Windows to full");
            rect.setX(0);
            rect.setY(0);
            rect.setW(1920);
            rect.setH(1080);
            SourceManagerInterface.setWindowRect(rect, 0);
	      int aspect = PictureInterface.getAspect();
            PictureInterface.setAspect(aspect,false);		
        }
        else if (destid == EnumSourceIndex.SOURCE_DVBC || destid == EnumSourceIndex.SOURCE_DTMB) {
            Log.d(TAG,"--12222--mainpage tv current ID ="+curId);

            if (Constant.LOG_TAG) {
                Log.d(TAG, "selectSource done,now getCurSourceId = "
                        + SourceManagerInterface.getCurSourceId());
            }
        }
        
        if (destid == EnumSourceIndex.SOURCE_DVBC || destid == EnumSourceIndex.SOURCE_DTMB){
            intent.setAction(Constant.INTENT_DTV);
        }
        else if (destid == EnumSourceIndex.SOURCE_ATV)
        {
        	intent.setAction(Constant.INTENT_ATV);
        }
        else if(destid == EnumSourceIndex.SOURCE_MEDIA){
    		SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
            String pkg1 = "cn.gd.snm.snmcm";
            String cls1 = "cn.gd.snm.snmcm.LauncherActivity";
            ComponentName componentName1 = new ComponentName(pkg1, cls1);
            intent.setComponent(componentName1);           
        }
        else
        {
        	intent.setAction(Constant.INTENT_PORT);
        }
        if(destid == EnumSourceIndex.SOURCE_MEDIA){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
        }else{
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
        }
        try{
            if(destid == EnumSourceIndex.SOURCE_MEDIA){
           	 SystemProperties.set("persist.sys.fullScreen_Source", ""+EnumSourceIndex.SOURCE_MEDIA);
        	 SystemProperties.set("dev.unionman.ignore", "0");
           }       	
            mContext.startActivity(intent);
        }catch(Exception e){
        	e.printStackTrace();
        }
     }   
        if (mContextType == Context_SERVICE_TYPE){
        	sendsystemDialogCloseBroadCast();
        }
        sendSourceChangedBroadcast(destid);
    }
    
    private void sendSourceChangedBroadcast(int id){
    	Intent intent = new Intent();
    	intent.setAction("com.um.sourcechanged");
    	intent.putExtra("source_id", id);
    	mContext.sendBroadcast(intent);
    	Log.d(TAG, "source changed broadcast was sended."+id);
    }
    
    private void exitActivity(){
    	View focuseView = root.getFocusedChild();
    	enterToPlay(focuseView);
    	doExit();
    }
    
	public void setExitFlag(){
		isexist = false;
	}
	
	private BroadcastReceiver systemDialogCloseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "systemDialogCloseReceiver UM_CLOSE_SYSTEM_DIALOG_ACTION");
            String action = intent.getAction();
            if (action.equals(UM_CLOSE_SYSTEM_DIALOG_ACTION)){
            	String reason = intent.getStringExtra("reason");
            	Log.e(TAG, "systemDialogCloseReceiver reason:"+reason+";mContextType:"+mContextType);
            	if ((reason != null) && (!reason.equals("SelectSource"))){
            		if (mContextType == Context_SERVICE_TYPE){
            			 doExit();
            		}
            	}
            }
        }
    };
    
    private void registerSystemDialogCloseReceiver(){
    	IntentFilter filter = new IntentFilter(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	mContext.registerReceiver(systemDialogCloseReceiver, filter);
    }
    
    private void unregisterSystemDialogCloseReceiver(){
    	mContext.unregisterReceiver(systemDialogCloseReceiver);
    }
    
    private void sendsystemDialogCloseBroadCast(){
    	Intent intent = new Intent(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	intent.putExtra("reason", "SelectSource");
    	mContext.sendBroadcast(intent);
    }
	
	private String getSourceStrById(int sourceIndex){
		int strid = 0;
		ArrayList<Integer> mAllSourceList = new ArrayList<Integer>();
		ArrayList<Integer> mCVBSList = new ArrayList<Integer>();
		ArrayList<Integer> mYPBRList = new ArrayList<Integer>();
		ArrayList<Integer> mHDMIList = new ArrayList<Integer>();
		
        mAllSourceList = SourceManagerInterface.getSourceList(); 
        mAllSourceList.remove((Integer)EnumSourceIndex.SOURCE_MEDIA2); 
		 for(int i=0;i<mAllSourceList.size();i++){
	    	   if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS1||
	    		  mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS2||
	    		  mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS3){
	    		   mCVBSList.add(mAllSourceList.get(i));
	    		   
	    	   }else if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR1||
	    			   mAllSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR2){
	    		   mYPBRList.add(mAllSourceList.get(i));
	    	   }else if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI1||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI2||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI3||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI4){
	    		   mHDMIList.add(mAllSourceList.get(i));
	    	   }
	       }
		 
		 Log.i("heh", "==mCVBSList="+mCVBSList.size());
		 Log.i("heh", "==mYPBRList="+mYPBRList.size());
		 Log.i("heh", "==mHDMIList="+mHDMIList.size());
		switch (sourceIndex){
			case EnumSourceIndex.SOURCE_CVBS1:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int index=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS1);
					if(index==0){
						strid = R.string.CVBS1;
					}else if(index==1){
						strid = R.string.CVBS2;
					}else if(index==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_CVBS2:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int indexcvbs2=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS2);
					if(indexcvbs2==0){
						strid = R.string.CVBS1;
					}else if(indexcvbs2==1){
						strid = R.string.CVBS2;
					}else if(indexcvbs2==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_CVBS3:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int indexcvbs3=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS3);
					if(indexcvbs3==0){
						strid = R.string.CVBS1;
					}else if(indexcvbs3==1){
						strid = R.string.CVBS2;
					}else if(indexcvbs3==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_YPBPR1:
				
				
					if(mYPBRList.size()==1){
						strid = R.string.YPBPR;
					}else{
						int indexypbpr=mYPBRList.indexOf(EnumSourceIndex.SOURCE_YPBPR1);
						if(indexypbpr==0){
							strid = R.string.YPBPR1;
						}else if(indexypbpr==1){
							strid = R.string.YPBPR2;
						}
					}
				
				break;
				
			case EnumSourceIndex.SOURCE_YPBPR2:
			
					if(mYPBRList.size()==1){
						strid = R.string.YPBPR;
					}else{
						int indexypbpr1=mYPBRList.indexOf(EnumSourceIndex.SOURCE_YPBPR2);
						if(indexypbpr1==0){
							strid = R.string.YPBPR1;
						}else if(indexypbpr1==1){
							strid = R.string.YPBPR2;
						}
					}
					Log.i("fk", "===SOURCE_YPBPR2===++++");
					Log.i("fk", "===SOURCE_YPBPR2==="+mContext.getString(strid));
				break;
			case EnumSourceIndex.SOURCE_HDMI1:
				
				int indexhdmi=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI1);
				if(indexhdmi==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi==3){
					strid = R.string.HDMI4;
				}
				
				break;
			case EnumSourceIndex.SOURCE_HDMI2:
				int indexhdmi2=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI2);
				if(indexhdmi2==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi2==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi2==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi2==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_HDMI3:
				int indexhdmi3=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI3);
				if(indexhdmi3==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi3==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi3==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi3==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_HDMI4:
				int indexhdmi4=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI4);
				if(indexhdmi4==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi4==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi4==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi4==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_VGA:
				strid = R.string.VGA;
				break;
			default:
				strid = R.string.CVBS;
				break;
				
		}

		return getResources().getString(strid);
	}
}
