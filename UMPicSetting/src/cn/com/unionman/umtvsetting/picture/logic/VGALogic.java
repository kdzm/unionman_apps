package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.interfaces.PCSettingInterface;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.picture.util.Constant;
import cn.com.unionman.umtvsetting.picture.util.Util;
import cn.com.unionman.umtvsetting.picture.widget.VGAAdjustingDialog;

import cn.com.unionman.umtvsetting.picture.R;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.CusSourceManager;
/**
 * VGA
 *
 * @author wangchuanjian
 *
 */
public class VGALogic implements InterfaceLogic {

    private static final String TAG = "VGALogic";
    private Context mContext;
    private Dialog mSystemUpdateDialog;
    public static Handler RefreshHandler = null;
    private final int registerVGAResultListener = 0;
    private final int unregisterVGAResultListener = 1;
    private final int VGAautoSetSuccess = 2;
    private final int VGAautoSetting = 3;
    
    // ADJUSTING
    public final static int ADJUSTING = 0;
    // adjust failed
    public final static int ADJUST_FAILED = 1;
    // adjust success
    public final static int ADJUST_SUCCESS = 2;
    
    private WidgetType mHPosition = new WidgetType();
    private WidgetType mVPosition = new WidgetType();
    private WidgetType mClock = new WidgetType();
    private WidgetType mPhase = new WidgetType();
    
    public VGALogic(Context mContext) {
        super();
        this.mContext = mContext;
        registerListener();
    }
    @Override
    
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
         
        if(!isVGAAvilable()){
        	mHPosition.setEnable(false);
        	mVPosition.setEnable(false);
        	mClock.setEnable(false);
        	mPhase.setEnable(false);
        }
    		


        // AutoAdjust
        WidgetType mAutoAdjust = new WidgetType();
        // set name for AutoAdjust
        mAutoAdjust.setName(res.getStringArray(R.array.Auto_Adjust)[0]);
        // set type for AutoAdjust
        mAutoAdjust.setType(WidgetType.TYPE_VGA);
        // set is have arrow for AutoAdjust
        mAutoAdjust.setIshaveArrow(false);
        // set state of VGA
        mAutoAdjust.setVGAstate(false);
        
        mAutoAdjust.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {            	
                PCSettingInterface.autoAdjust();
    
                ShoeVgaAdjustDialog(ADJUSTING);

                return 0;
            }
            @Override
            public int getSysValue() {
                return 0;
            }
        });
        // set data for AutoAdjust
        mAutoAdjust.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.auto_adjust));
        mWidgetList.add(mAutoAdjust);

        // HPosition
       // WidgetType mHPosition = new WidgetType();
        // set name for HPosition
        mHPosition.setName(res.getStringArray(R.array.VGA_setting)[1]);
        // set type for HPosition
        mHPosition.setType(WidgetType.TYPE_PROGRESS);
        mHPosition.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                //return PCSettingInterface.setHPosition(i);
                PCSettingInterface.setHPosition(i);
                return i;
            }
            @Override
            public int getProgress() {
                return PCSettingInterface.getHPosition();
            }
        });
       
        //mHPosition.setEnable(true);
        mWidgetList.add(mHPosition);

        // VPosition
        //WidgetType mVPosition = new WidgetType();
        // set name for VPosition
        mVPosition.setName(res.getStringArray(R.array.VGA_setting)[2]);
        // set type for VPosition
        mVPosition.setType(WidgetType.TYPE_PROGRESS);
        mVPosition.setmAccessProgressInterface(new AccessProgressInterface() {
            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                //return PCSettingInterface.setVPosition(i);
                PCSettingInterface.setVPosition(i);
                return i;
            }
            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                    Log.d("PCSettingInterface.getVPosition()", ""
                            + PCSettingInterface.getVPosition());
                }
                return PCSettingInterface.getVPosition();
            }
        });
        //mVPosition.setEnable(true);
        mWidgetList.add(mVPosition);
        // Clock
        //WidgetType mClock = new WidgetType();
        // set name for Clock
        mClock.setName(res.getStringArray(R.array.VGA_setting)[3]);
        // set type for Clock
        mClock.setType(WidgetType.TYPE_PROGRESS);
        mClock.setmAccessProgressInterface(new AccessProgressInterface() {
            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                //return PCSettingInterface.setClock(i);
                PCSettingInterface.setClock(i);
                return i;
            }
            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                    Log.d("PCSettingInterface.getClock()", ""
                            + PCSettingInterface.getClock());
                }
                return PCSettingInterface.getClock();
            }
        });
        //mClock.setEnable(true);
        mWidgetList.add(mClock);

        // Phase
        //WidgetType mPhase = new WidgetType();
        // set name for Phase
        mPhase.setName(res.getStringArray(R.array.VGA_setting)[4]);
        // set type for Phase
        mPhase.setType(WidgetType.TYPE_PROGRESS);
        mPhase.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }
                //return PCSettingInterface.setPhase(i);
                PCSettingInterface.setPhase(i);
                return i;
            }
            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                    Log.d("PCSettingInterface.getPhase()", ""
                            + PCSettingInterface.getPhase());
                }
                return PCSettingInterface.getPhase();
            }
        });
        //mPhase.setEnable(true);
        mWidgetList.add(mPhase);

        return mWidgetList;
    }
    
    
    public void ShoeVgaAdjustDialog(int flag)
    {
        if(mSystemUpdateDialog != null && mSystemUpdateDialog.isShowing())
            mSystemUpdateDialog.dismiss();
        mSystemUpdateDialog = new VGAAdjustingDialog( mContext, flag);
        mSystemUpdateDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
        Window window = mSystemUpdateDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.width = LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        mSystemUpdateDialog.show();
    }
    
    public boolean isHueMode() {
    	// TODO Auto-generated method stub
    	if(TAG.equals("HueModeLogic")){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
    public boolean isVGAAvilable(){
    	boolean bfind = false;
        ArrayList<Integer> sourceList = UmtvManager.getInstance().getSourceManager().getAvailSourceList();;

		for (int j = 0; j < sourceList.size(); j++){

			if (EnumSourceIndex.SOURCE_VGA == sourceList.get(j).intValue()){
		        bfind = true;
			}
		}
		
		return bfind;
    }
    
    
    /**
     * refresh selector of Picture
     */
    private void RefreshPictureSelector() {
    	Log.i(TAG, "refresh==========");
        Message msg = new Message();
        msg.what = Constant.SETTING_UI_REFRESH_VIEWS;
        List<String> Stringlist = new ArrayList<String>();
        Stringlist.add(mContext.getResources().getString(R.string.VGA));
        msg.obj = Stringlist;
        RefreshHandler.sendMessage(msg);
    }
    
    private void registerListener() {
    	Log.d(TAG, "  registerListener ");
    	  UmtvManager.getInstance().registerListener(
                  TVMessage.HI_TV_EVT_PC_ADJ_STATUS, onVGAPlayerListener);
    }
    
    private void unregisterListener() {
    	Log.d(TAG, "  unregisterListener ");
    	  UmtvManager.getInstance().unregisterListener(
                  TVMessage.HI_TV_EVT_PC_ADJ_STATUS, onVGAPlayerListener);
    }
    
    OnPlayerListener onVGAPlayerListener = new OnPlayerListener() {

        @Override
        public void onSignalStatus(int arg0) {
        	
        }
        @Override
        public void onTimmingChanged(TimingInfo arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onTimmingChanged  arg0: " + arg0);
            }
        }

        @Override
        public void onSrcDetectPlugin(ArrayList<Integer> arg0) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugin  arg0: " + arg0);
            }
        }

        @Override
        public void onSrcDetectPlugout(ArrayList<Integer> arg0) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugout  arg0: " + arg0);
            }
        }

        @Override
        public void onPCAutoAdjustStatus(int arg0) {
            Log.d(TAG, "  onPCAutoAdjustStatus  arg0: " + arg0);
            if(arg0 == 1)
            {
            	RefreshPictureSelector();
            }
        }

		@Override
        public void onSelectSource(int  arg0) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSelectSource  arg0: " + arg0);
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

		@Override
        public void onPlayLock(ArrayList<Integer> list) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onPlayLock  arg0: " + list);
            }
        }
    };

    @Override
    public void setHandler(Handler handler) {
        RefreshHandler = handler;

    }
}
