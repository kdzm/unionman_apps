package com.unionman.quicksetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import java.io.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.ActivityNotFoundException;
import android.os.RemoteException;
import android.util.Xml;
import android.widget.Toast;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
//import com.um.launcher.interfaces.SourceManagerInterface;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

import com.hisilicon.android.tvapi.vo.RectInfo;
import com.unionman.quicksetting.interfaces.SourceManagerInterface;
import com.unionman.quicksetting.interfaces.ViewAddableInterface;
import com.unionman.quicksetting.util.Constant;
import com.unionman.quicksetting.util.Util;

import android.content.Context;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;

import android.os.SystemProperties;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusFactory;
/**
 * The service of UI
 * 
 * @author tang_shengchang hisi.ltd <br>
 *         MainService
 */
public class UIService extends Service {

    // mWindowManager Add a view in the above
	private static final String TAG = "UIService";
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    // whole setting layout
    private RelativeLayout mWholeSettingLay;
    // menu container layout
    private LinearLayout mMenuContainerLay;
    // the index of first
    private int mFirstIndex = 0;
    // the list of ViewAddableInterface
    private List<ViewAddableInterface> mViewList = null;
    
    private Object mSwitchLock;
    private int TvSourceIdx = EnumSourceIndex.SOURCE_ATV;
    private int mCurrentSourceIdx = EnumSourceIndex.SOURCE_ATV;

    
	private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
	private static final String ATPackageName = "com.cvte.tv.fac.autotest";
	private static final String ATActivityName = "com.cvte.tv.at.job.ATActivity";
	
    /**
     * Add a view and returns its index
     */
    public int addList(ViewAddableInterface viewAddable) {
        mViewList.add(viewAddable);
        return mViewList.size() - 1;
    }

    /**
     * The specified list view removed
     * 
     * @param location
     */
    public void removeList(int location) {
        mViewList.remove(location);
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    public List<ViewAddableInterface> getViewAddable() {
        return mViewList;
    }

    @Override
    public void onCreate() {
        mViewList = new ArrayList<ViewAddableInterface>();
        super.onCreate();
		mSwitchLock = new Object();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * For pass over the button value
         */
        if (intent == null) {
            return Log.d(TAG, "intent is null");
        }
        int keycode = intent.getIntExtra("hotkey", -1);
        if (Constant.LOG_TAG) {
            Log.d(TAG, "MainService action = " + intent.getAction()
                    + "  keycode = " + keycode);
        }
		
        /**
         * Through value judgment calls which createView ()
         */
        switch (keycode) {
        case KeyEvent.KEYCODE_SOURCE:
            if (Constant.LOG_TAG) {
                Log.d(TAG, "--" + KeyEvent.KEYCODE_SOURCE);
            }
            try {
                Intent newIntent = new Intent();
                newIntent.setClassName("com.hisilicon.launcher",
                        "com.hisilicon.launcher.MainActivity");
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // The pageNum control to jump to the page of launcher concrete
                intent.putExtra("pageNum", 0);
                startActivity(newIntent);
            } catch (Exception e) {
                if (Constant.LOG_TAG) {
                    Log.e(TAG, e.toString());
                }
            }
            break;
/*        case KeyEvent.KEY_PICTUREMODE:     //deal in ATV , DTV and Port
            if (Constant.LOG_TAG) {
                Log.d(TAG, "--" + KeyEvent.KEY_PICTUREMODE);
            }
            
             * For listview incoming image settings menu options
             
            if (mPictureMenuView == null) {
                mPictureMenuView = new MenuView(
                        UIService.this,
                        mLogicFactory
                                .createLogicQuickly(Constant.QUICK_PICTURE_MODE));
                mViewList.add(mPictureMenuView);
            }
            if (!mPictureMenuView.isAddedToWmanager()) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, " removeAllViews()");
                }
                removeAllViews();
                createView(mPictureMenuView);
            } else {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "removeView(mPictureMenuView)");
                }
                removeView(mPictureMenuView);
            }
            break;*/
/*        case KeyEvent.KEY_SOUNDMODE:   //deal in ATV , DTV and Port
            if (Constant.LOG_TAG) {
                Log.d(TAG, "--" + KeyEvent.KEY_SOUNDMODE);
            }
            
             * For listview incoming sound settings menu options
             

            if (mSoundMenuView == null) {
                mSoundMenuView = new MenuView(UIService.this,
                        mLogicFactory
                                .createLogicQuickly(Constant.QUICK_SOUND_MODE));
                mViewList.add(mSoundMenuView);
            }
            if (!mSoundMenuView.isAddedToWmanager()) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, " removeAllViews()");
                }
                removeAllViews();
                createView(mSoundMenuView);
            } else {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "removeView(mSoundMenuView)");
                }
                removeView(mSoundMenuView);
            }
            break;*/
/*        case KeyEvent.KEY_ZOOM:    //deal in ATV , DTV and Port
            if (Constant.LOG_TAG) {
                Log.d(TAG, "--" + KeyEvent.KEY_ZOOM);
            }
            
             * To the listview afferent menu options proportion
             
            if (mZoomMenuView == null) {
                mZoomMenuView = new MenuView(UIService.this,
                        mLogicFactory
                                .createLogicQuickly(Constant.QUICK_ASPECT_MODE));
                mViewList.add(mZoomMenuView);
            }
            if (!mZoomMenuView.isAddedToWmanager()) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "removeAllViews()");
                }
                removeAllViews();
                createView(mZoomMenuView);
            } else {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "removeView(mZoomMenuView)");
                }
                removeView(mZoomMenuView);
            }

            break;*/
        case KeyEvent.KEYCODE_TV:
        	Log.i(TAG,"KEYCODE_TV is clicked,but it won't be acknowledged");
/*
  		   String topActivityClassName=  getTopActivityName(UIService.this);
  		   Log.i(TAG,"topActivityClassName="+topActivityClassName);
		 
  		 if(!topActivityClassName.equals("com.um.ui.Dvbplayer_Activity")){
  			 Log.i(TAG,"not in DVB-C or DTMB ,start DVB-C");
			 //KillAppsBeforeSwitchSource();
  			KillAppsBeforeSwitch();
			 doDeleteMediaSource();
			 waitForSwitchLock();
			Intent tv_intent = new Intent();
			tv_intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DVBC);
			tv_intent.setAction("com.unionman.intent.ACTION_PLAY_DVB");
			tv_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			tv_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(tv_intent);  			 
  		 }else if(SystemProperties.get("sys.dvb.frontend.type","").equals("3")){  //DTMB
  			 Log.i(TAG," in DTMB ,start DVB-C");
			 //KillAppsBeforeSwitchSource();
  			KillAppsBeforeSwitch();
 			Intent tv_intent = new Intent();
 			tv_intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DVBC);
 			tv_intent.setAction("com.unionman.intent.ACTION_PLAY_DVB");
 			tv_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
 			tv_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 			startActivity(tv_intent);   			 
  		 }else if(SystemProperties.get("sys.dvb.frontend.type","").equals("2")){  //DVB-C
  			 Log.i(TAG,"in DVB-C , not need to start DVB-C");   
  		 }
		 */
        	break;
        case KeyEvent.KEYCODE_GUIDE:  //Ӧ��
        	Log.i(TAG,"KEYCODE_GUIDE is clicked");
        	break;
        case  KeyEvent.KEY_TRACK:  //���   
        	Log.i(TAG,"KEY_TRACK is clicked");
        break;
        case KeyEvent.KEY_RADIO:  // �㲥
        	Log.i(TAG,"KEY_RADIO is clicked");
        	break;     
        case KeyEvent.KEYCODE_AUDIO:   // ����
        	Log.i(TAG,"KEYCODE_AUDIO is clicked");
        	break;        	
        case KeyEvent.KEY_EPG:    // ָ��
        	Log.i(TAG,"KEY_EPG is clicked");
        	break;
        case KeyEvent.KEYCODE_FAVORITES:  //ϲ��
        	Log.i(TAG,"KEY_FAVORITES is clicked");
        	break;     
        case KeyEvent.KEYCODE_RECALL:   // �ؿ�
        	Log.i(TAG,"KEYCODE_RECALL is clicked");
        	break; 
		case KeyEvent.KEY_USB:
        	Log.i(TAG,"KEY_USB is clicked");
			//KillBlackListApps();
			//KillNonsystemApps();
			//doSelectMediaSource();
        	KillAppsBeforeSwitch();
			openMediaCenter();
			break;
		case KeyEvent.KEY_APPLICATION:
        	Log.i(TAG,"KEY_APPLICATION is clicked,but it won't be acknowledged.");
			//KillBlackListApps();
			//KillNonsystemApps();
			//doSelectMediaSource();
        	//KillAppsBeforeSwitch();
			//openMyApps();
			break;
		case KeyEvent.KEY_CVT_FAC_AT_SHOW:
			if (isCVTEATTest() == true)
			{
				break;
			}
			
			Intent cvte1intent = new Intent("cvte.factory.intent.action.ATScreenActivity");
			cvte1intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				startActivity(cvte1intent);
			} catch (ActivityNotFoundException ex) {
				
			}
			break;
		case KeyEvent.KEY_CVT_FAC_F1:
			CusFactory mFactory = UmtvManager.getInstance().getFactory();
			mFactory.boardFactoryReset();
			break;
		case KeyEvent.KEY_CVT_FAC_MENU_SHOW:
			ComponentName componentName = new ComponentName("cn.com.unionman.umtvsetting.umsysteminfo", "cn.com.unionman.umtvsetting.umsysteminfo.SysInfoActivity");
			Intent mIntent = new Intent();
			mIntent.setComponent(componentName);
			mIntent.putExtra("dialog_mode", 2);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mIntent);
			break;
		case KeyEvent.KEY_CVT_FAC_AUTO_TUNING:
			Intent cvte2intent = new Intent("cvte.factory.intent.action.DialogMenuScan");
			cvte2intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				startActivity(cvte2intent);
			} catch (ActivityNotFoundException ex) {
				
			}
			break;  			
        default:
            break;
        }
        return super.onStartCommand(intent, flags, startId);
    }
	
	private boolean isCVTEATTest() 
	{    
		ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);      
		List<ActivityManager.RunningTaskInfo> infoList = manager.getRunningTasks(2);    
		if (infoList.size() > 0) 
		{        
			ActivityManager.RunningTaskInfo info = infoList.get(0);        
			String packageName = info.topActivity.getPackageName();        
			String className = info.topActivity.getClassName();        
			//must by AT Activity        
			if (packageName.equals(ATPackageName) &&               
			className.equals(ATActivityName)) 
			{            
				Log.i(TAG,"isCVTEATTest = TRUE");            
				return true;        
			}    

		}    
		return false;
	}

    private void openMediaCenter(){
//        ComponentName componentName = new ComponentName("com.umexplorer", "com.umexplorer.activity.SelectFileType");
        Intent mIntent = new Intent();
//        mIntent.setComponent(componentName);
        mIntent.setComponent(new ComponentName("com.unionman.settings",
        		"com.unionman.settings.UMSettingsActivity"));
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mIntent);
    }

	    private void openMyApps(){
		String topActivityClassName=  getTopActivityName(UIService.this);
  		Log.i(TAG,"topActivityClassName="+topActivityClassName);	
		if(!topActivityClassName.equals("com.um.launcher.MyAppActivity")){
			ComponentName componentName = new ComponentName("com.um.launcher", "com.um.launcher.MyAppActivity");
			Intent mIntent = new Intent();
			mIntent.setComponent(componentName);
	//        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(mIntent);
		}else{
		  Log.i(TAG,"in com.um.launcher.MyAppActivity , not need to start MyAppActivity");		
		}
    }

	private void KillBlackListApps() {
	  File file = new File("/vendor/etc/blacklist.xml");
	  Log.d(TAG, "KillBlackListApps Enter ");
	  if(file.exists() && file.isFile()){
		  try {
			  
			  InputStream xml = new FileInputStream(file);
			  ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
			  XmlPullParser pullParser = Xml.newPullParser();
			  pullParser.setInput(xml, "UTF-8"); //为Pull解释器设置要解析的XML数据		  
			  int event = pullParser.getEventType();
			  
			  while (event != XmlPullParser.END_DOCUMENT) {
				  
				  switch (event) {
				  
				  case XmlPullParser.START_DOCUMENT:
					  break;	
				  case XmlPullParser.START_TAG:   
					  String name = pullParser.getName();
					  if ("listname".equals(name)) {
						  String value = pullParser.getAttributeValue(0);
						  Log.d(TAG, "KillBlackListApps value is "+value);
						  am.forceStopPackage(value);
					  }
					  break;
					  
				  case XmlPullParser.END_TAG:
					  break;
				  }
				  
				  event = pullParser.next();
				  }
			  } catch (FileNotFoundException e) {
				  e.printStackTrace();
				  Toast.makeText(this, "ERROR: blacklist.xml not found.", 
						  Toast.LENGTH_LONG).show();
				  return;
			  } catch (XmlPullParserException e) {
				  e.printStackTrace();
				  Toast.makeText(this, "ERROR: parse blacklist.xml failed.", 
						  Toast.LENGTH_LONG).show();
				  return;
			  } catch (IOException e) {
				  e.printStackTrace();
				  Toast.makeText(this, "ERROR: read blacklist.xml failed.", 
						  Toast.LENGTH_LONG).show();
				  return;
			  }
		  }
	
	  }
	
		
	  private boolean isSystemApplication(PackageManager packageManager, String packageName) {
		  if (packageManager == null || packageName == null || packageName.length() == 0) {
			  return false;
		  }
	
		  try {
			  ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
			  return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
		  } catch (NameNotFoundException e) {
			  e.printStackTrace();
		  }
		  return false;
	  }
	  
	private void KillNonsystemApps(){
		ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (int i=0; i<processes.size(); i++){
			ActivityManager.RunningAppProcessInfo infor = processes.get(i);
			String[] pkgNameList =	infor.pkgList;; // 鑾峰緱杩愯鍦ㄨ杩涚▼閲岀殑鎵€鏈夊簲鐢ㄧ▼搴忓寘 
			// 杈撳嚭鎵€鏈夊簲鐢ㄧ▼搴忕殑鍖呭悕	
			for (int j = 0; j < pkgNameList.length; j++) {	
				String pkgName = pkgNameList[j];  
				Log.i(TAG, "packageName " + pkgName + " at index " + j);			
				if(!isSystemApplication(this.getPackageManager(), pkgName)){
					Log.i(TAG, "forceStopPackage " + pkgName + " at index " + j);		
					am.forceStopPackage(pkgName);
				} 
			}
		}
	}

    private void KillAppsBeforeSwitchSource()
    {
        ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
    	for (int i=0; i<processes.size(); i++)
    	{
    		ActivityManager.RunningAppProcessInfo actRSI = processes.get(i);
    		if(!actRSI.processName.equals("com.um.launcher")
                && !actRSI.processName.equals("com.android.musicfx")
                && !actRSI.processName.equals("com.um.umreceiver")
                && !actRSI.processName.equals("com.um.dvb")
                && !actRSI.processName.equals("com.um.dvbsearch")
                && !actRSI.processName.equals("com.unionman.caprovider")
                && !actRSI.processName.equals("com.um.upgrade")
                && !actRSI.processName.equals("com.unionman.dvbcitysetting")
                && !actRSI.processName.equals("com.unionman.settingwizard")
                && !actRSI.processName.equals("com.android.exchange")
                && !actRSI.processName.equals("com.android.settings")
                && !actRSI.processName.equals("android.process.media")
                && !actRSI.processName.equals("com.android.onetimeinitializer")
                && !actRSI.processName.equals("com.um.tv.menu")
                && !actRSI.processName.equals("com.android.smspush")
                && !actRSI.processName.equals("cn.com.unionman.umtvsystemserver")
                && !actRSI.processName.equals("com.hisilicon.android.hiRMService")
                && !actRSI.processName.equals("com.android.phone")
                && !actRSI.processName.equals("com.hisilicon.android.inputmethod.remote")
                && !actRSI.processName.equals("com.android.systemui")
                && !actRSI.processName.equals("system")
                && !actRSI.processName.equals("com.um.atv")
                && !actRSI.processName.equals("com.portplayer")
                && !actRSI.processName.equals("com.unionman.quicksetting"))
    		{
    			am.forceStopPackage(actRSI.processName);
    		}
    	}
    }
    
    private void KillAppsBeforeSwitch()
    {
        ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
    	for (int i=0; i<processes.size(); i++)
    	{
    		ActivityManager.RunningAppProcessInfo actRSI = processes.get(i);
    		if(actRSI.processName.equals("cn.com.unionman.umtvsetting.picture")||actRSI.processName.equals("cn.com.unionman.umtvsetting.sound"))
    		{
    		    	Intent intent = new Intent(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    		    	intent.putExtra("reason", "UIService");
    		    	sendBroadcast(intent);
    		    
    		}
    		
    	}
    }

    /**
     * Create a Dialog interface
     */
    public void createDialogView() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.ANIMATION_CHANGED,
                PixelFormat.TRANSLUCENT);
        mParams.y = 0;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    /**
     * remove DialogView
     */
    public void removeDialogView() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // mSettingViewDialog.removeMsg();
       // mWindowManager.removeView(mSettingViewDialog);
        // mSettingViewDialog.setShowDialog(false);
    }

    /**
     * remove All Views
     */
    public void removeAllViews() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        for (int i = 0; i < mViewList.size(); i++) {
            if (mViewList.get(i).isAddedToWmanager()) {
                mViewList.get(i).setAddedToWmanager(false);
                mWindowManager.removeView((View) mViewList.get(i));
            }
        }
    }

    /**
     * remove view by ViewAddable
     * 
     * @param viewAddable
     */
    public void removeView(ViewAddableInterface viewAddable) {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (viewAddable != null && viewAddable.isAddedToWmanager()) {
            viewAddable.removeMsg();
            mWindowManager.removeView((View) viewAddable);
            viewAddable.setAddedToWmanager(false);
        }
    }

    /**
     * create view by viewAddable
     * 
     * @param viewAddable
     */
    public void createView(ViewAddableInterface viewAddable) {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        if (viewAddable != null) {
            mParams.x = viewAddable.getViewAddableX();
            mParams.y = viewAddable.getViewAddableY();
            mParams.width = viewAddable.getViewAddableWidth();
            mParams.height = viewAddable.getViewAddableHeight();
            if (viewAddable != null && !viewAddable.isAddedToWmanager()) {
                mWindowManager.addView((View) viewAddable, mParams);
                viewAddable.sendDisappearMsg();
                viewAddable.setAddedToWmanager(true);
            }
        }
    }
	public  String getTopActivityName(Context context){
		String topActivityClassName=null;
		 ActivityManager activityManager =
		(ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE )) ;
	     List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;
	     if(runningTaskInfos != null){
	    	 ComponentName f=runningTaskInfos.get(0).topActivity;
	    	 topActivityClassName=f.getClassName();
	     }
	     return topActivityClassName;
	}

    public void setVideoWindowRect(boolean isFullScreen) {
    	int sourceIdx = SourceManagerInterface.getSelectSourceId();
        RectInfo rect = new RectInfo();
        if ((sourceIdx == EnumSourceIndex.SOURCE_MEDIA) || (sourceIdx == EnumSourceIndex.SOURCE_ATV)
                || (sourceIdx >= EnumSourceIndex.SOURCE_CVBS1 && sourceIdx <= EnumSourceIndex.SOURCE_HDMI4)) {
             if(isFullScreen){
                 rect.setX(0);
                 rect.setY(0);
                 rect.setW(1920);
                 rect.setH(1080);
                 Log.d(TAG, "Scaler TV Windows to Full");
             }else{
                 rect.setX(120);
                 rect.setY(205);
                 rect.setW(720);
                 rect.setH(410);
                 Log.d(TAG, "Scaler TV Windows to Small");
             }
            SourceManagerInterface.setWindowRect(rect, 0);
        }
    }

    private void doDeleteMediaSource(){
    	
    	synchronized (mSwitchLock) {
    		mCurrentSourceIdx = SourceManagerInterface.getSelectSourceId();
    		TvSourceIdx = SourceManagerInterface.getLastSourceId();
    		Log.d(TAG, "doDeleteMediaSource is called: mCurrentSourceIdx=" + mCurrentSourceIdx 
    					+ ", TvSourceIdx="+TvSourceIdx);
    		
        	if (mCurrentSourceIdx == EnumSourceIndex.SOURCE_MEDIA){
	    		Log.d(TAG, "doDelectTVSource");
            	setVideoWindowRect(true);
                SourceManagerInterface.deselectSource(EnumSourceIndex.SOURCE_MEDIA, true);
            }
    	}
	}

    private void doSelectMediaSource(){
    	
    	synchronized (mSwitchLock) {
    		mCurrentSourceIdx = SourceManagerInterface.getCurSourceId();
    		TvSourceIdx = SourceManagerInterface.getLastSourceId();
    		Log.d(TAG, "doDeleteMediaSource is called: mCurrentSourceIdx=" + mCurrentSourceIdx 
    					+ ", TvSourceIdx="+TvSourceIdx);
    		if(TvSourceIdx != EnumSourceIndex.SOURCE_MEDIA){
				SourceManagerInterface.deselectSource(TvSourceIdx, true);
			}
			
        	if (mCurrentSourceIdx != EnumSourceIndex.SOURCE_MEDIA){
	    		Log.d(TAG, "doDelectTVSource");
            	setVideoWindowRect(true);
                SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
            }
    	}
	}

	private void waitForSwitchLock(){
		Log.d(TAG,"waitForSwitchLock waiting");
		synchronized (mSwitchLock){
			Log.d(TAG,"waitForSwitchLock in");
		}
		Log.d(TAG,"waitForSwitchLock out");
	}
}
