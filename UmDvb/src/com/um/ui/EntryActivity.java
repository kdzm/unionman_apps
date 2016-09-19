package com.um.ui;

import java.io.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.RemoteException;
import android.util.Xml;

import com.um.util.PropertyUtils;
import com.um.util.SystemUtils;
import com.um.controller.ParamSave;
import com.um.dvbstack.DVB;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.ProviderProgManage;
import com.um.dvbstack.Tuner;




import android.app.Activity;
import android.app.ActivityManager;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.dvbserver.DvbServerManager;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;
import com.unionman.jazzlib.*;
import com.um.dvb.R;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.SourceManager;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.hisilicon.android.tvapi.Picture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntryActivity extends Activity {
	private DvbServerReceiver mDvbServerReceiver = null;
	private final static String TAG = "EntryActivity";
	private Thread mThread = null;
	private boolean mSwitchingFlag = false;
	private int mDestSourceId = -1;
	private Context mContext;
	private Object mSwitchLock;
	private boolean mCardChangedFlag = false;
	private boolean dvn_reboot_flag = false;
	private boolean dvn_dp_reboot_flag = false;
	private DvbServerManager mServerManager = DvbServerManager.getInstance();
	private static boolean mRestartingService = false;
	
	public class DvbServerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.v(TAG, "receive: " + action);
			if (action != null
					&&action
							.equals("com.unionman.dvb.ACTION_DVBPLAYER_SERVER_READY")) {
				Log.v(TAG, "ready enter full screen play.");
				if(getSyncProgState()==0)
				{
					TextView txt = (TextView)findViewById(R.id.textView1);
					txt.setText(R.string.wait_for_sync_prog);
					startProgramSync();	
				}
				else
				{
					startPlayerActivity();
				}
			}else if(action.equals("com.unionman.dvb.ACTION_DVB_SYNC_PROG_READY")&&DVB.isServerAlive())
			{
				Log.v(TAG, "SYNC PROG OK");
				startPlayerActivity();	
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			break;
		default:
			break;
		}
	}

	private boolean isProgEmpty() {
		int progTypeId;
		if (mDestSourceId == EnumSourceIndex.SOURCE_DTMB) {
			progTypeId = ProviderProgManage.DTMB_ID;
		} else {
			progTypeId = ProviderProgManage.DVBC_ID;
		}

		int modeTypeId = ProviderProgManage.TVPROG;

		Intent intent = getIntent();
		String modeStr = intent.getStringExtra("mode");
		if (modeStr != null && modeStr.equals("radio")) {
			modeTypeId = ProviderProgManage.RADIO_ID;
		}

		ProgStorage progStorage = new ProgStorage(getContentResolver());
		ArrayList<ProgInfo> progList = progStorage.getProgOrderBy(new int[] { progTypeId, modeTypeId }, null, false);
		if (progList != null && progList.size() > 0) {
			for (ProgInfo pi : progList) {
				if (!pi.hiden && pi.valid) {
					return false;
				}
			}
		}
		return true;

	}

	private void startPlayerActivity() {
		if (isProgEmpty()) {
			Intent itent = getPackageManager().getLaunchIntentForPackage(
					"com.um.dvbsearch");
			if (itent != null) {
				itent.putExtra("launchReason", "noProg");
				Log.v(TAG, "ready to start search activity");
				startActivity(itent);
			}
			Log.v(TAG, "prog is empty");
			finish();
			return;
		}

		Intent intent = new Intent();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			intent.putExtras(extras);
		}
		intent.putExtra("SourceName", mDestSourceId);
		intent.setClass(this, Dvbplayer_Activity.class);
		Log.v(TAG, "ready to start dvb activity");
		startActivity(intent);
		finish();
		setFullScreen();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (SystemProperties.getInt("sys.dvbentry.booted", 0) == 0) {
			SystemProperties.set("sys.dvbentry.booted", "1");
		}
		
		mContext = this;
		mSwitchLock = new Object();
		
		Log.v(TAG, "EntryActiviy onCreate, service status:" + mServerManager.getServiceStatus());
		if (SystemProperties.get("persist.sys.dvb.installed", "0").equals("0")) {
			Toast.makeText(this, R.string.dvb_service_not_installed, 3000)
					.show();
			finish();
			return;
		}
		
		mCardChangedFlag = false;
		dvn_reboot_flag = false;
		dvn_dp_reboot_flag = false;
		mSwitchingFlag = false;
		int destSourceId = -1;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			destSourceId = extras.getInt("SourceName", -1);
			//mCardChangedFlag = extras.getBoolean("cardChanged", false);
			Log.v(TAG, "cardChanged=" + extras.getBoolean("cardChanged", false));
		}
		
		Log.v(TAG, "Extras: SourceName=" + destSourceId);
		
		if (SystemProperties.getInt("sys.dvb.cas.cardchanged", 0) == 1) {
			mCardChangedFlag = true;
			Log.v(TAG, "switching ca card...");
		}
		if (SystemProperties.getInt("sys.dvn.cas.pair.reboot", 0) == 1) {
			dvn_reboot_flag = true;
			Log.v(TAG, "switching ca card...");
		}
		if (SystemProperties.getInt("sys.dvn.cas.depair.reboot", 0) == 1) {
			dvn_dp_reboot_flag = true;
			Log.v(TAG, "switching ca card...");
		}
		int curSourceId = getSourceManager().getCurSourceId(0);
		Log.v(TAG, "curSourceId=" + curSourceId + ", destSourceId=" + destSourceId);
		/*
		 * Intent婵炴垶鎼╅崢浠嬫儉閸涙潙瀚夊璺侯儏閻﹀綊鎮楃憴鍕憘ourceName闂佹寧绋戦懟顖炲垂椤栫偛绀嗛柕鍫濇閻掔晫鎲搁悧鍫熷碍濠⒀呭殲ource闂佸搫瀚烽崹浼村箚娑撳弧V闂佹寧绋戦懟顖炪�瑜斿绋款煥閸曨儷锕傛煥濞戞鐏辨繛韫嵆瀵粙宕抽幉鍍絩ce婵炴垶鎸告鍝ョ礊鐎ｎ喖绀堢�鍕棦urce婵炴埊鎷�	 * 闁跨喕妫勫В锟�	 */
		if (destSourceId == -1) {
			if (curSourceId == EnumSourceIndex.SOURCE_DVBC
					|| curSourceId == EnumSourceIndex.SOURCE_DTMB) {
				destSourceId = curSourceId;
			}
		}

		Log.v(TAG, "Dest source id: " + destSourceId);

		if (destSourceId != EnumSourceIndex.SOURCE_DVBC
				&& destSourceId != EnumSourceIndex.SOURCE_DTMB) {
			Log.v(TAG, "destSourceId is invalid: " + destSourceId);
			Toast.makeText(this, "Target source is invalid. Pls select DTMB or DVBC.", 3000).show();
			finish();
			return;
		}

		mDestSourceId = destSourceId;

		Log.v(TAG, "Cur source id: " + curSourceId);
		if (mCardChangedFlag) {
			Log.v(TAG, "card is changed, ready to restart server...");
			restartService();
			preSyncProc();
			if(dvn_reboot_flag)
			{
				TextView txt = (TextView)findViewById(R.id.textView1);
				txt.setText(R.string.dvn_reboot);
			}
			else if(dvn_dp_reboot_flag)
			{
				TextView txt = (TextView)findViewById(R.id.textView1);
				txt.setText(R.string.dvn_dp_reboot);
			}
			else
			{
				TextView txt = (TextView)findViewById(R.id.textView1);
				txt.setText(R.string.switching_ca_card);
			}
		} else if (curSourceId != destSourceId) {
			mSwitchingFlag = true;
			
			switchSource(curSourceId, destSourceId);
			preSyncProc();
		} else if (DVB.isServerAlive()){
			if (getSyncProgState()== 0) {
				preSyncProc();
				TextView txt = (TextView)findViewById(R.id.textView1);
				txt.setText(R.string.wait_for_sync_prog);
				startProgramSync();  
			} else {
				Log.v(TAG, "Service is alive.");
				// createTestProg(Tuner.UM_TRANS_SYS_TYPE_CAB);
				startPlayerActivity();
				return;
			}
		}  else {
			Log.v(TAG, "Service is not alive, wait for it.");
			preSyncProc();
		}
		
		int selectedID = getSourceManager().getSelectSourceId();
		//int tunerLNA = PropertyUtils.getInt("persist.sys.tunerLNA", 0);
		int tunerLNA = UmtvManager.getInstance().getFactory().getTunerLNA();
		int bitval = 1 << destSourceId;
		if((bitval & tunerLNA) != 0){
        	SystemUtils.shellExecute("echo LNA on > /proc/msp/tuner");
        }else{
        	SystemUtils.shellExecute("echo LNA off > /proc/msp/tuner");
        }
		
		Log.d(TAG, "tunerLNA " + tunerLNA + " (1 << destSourceId) " + bitval);
	}
	
	private boolean restartService() {
		mServerManager.restartService();
		return true;
	}
	
	private void preSyncProc()
	{
		unregistReciver();
		mDvbServerReceiver = new DvbServerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.unionman.dvb.ACTION_DVBPLAYER_SERVER_READY");
		filter.addAction("com.unionman.dvb.ACTION_DVB_SYNC_PROG_READY");
		registerReceiver(mDvbServerReceiver, filter);
		setContentView(R.layout.entry_page);
	}
	
	private void unregistReciver() {
		if (mDvbServerReceiver != null) {
			unregisterReceiver(mDvbServerReceiver);
			mDvbServerReceiver = null;
		}	
	}
	
	private void resetTestProg() {
		ProgManage.GetInstance().resetTestProgList();
	}

	private void createTestProg(int tunerType) {
		Log.d(TAG, "==========createTestProg");
		ProgManage progManage = ProgManage.GetInstance();
		progManage.createTestProgList(tunerType);

		progManage.refreshProgList();
		progManage.SetCurMode(ProgManage.ALLPROG);

		ArrayList<HashMap<String, String>> list = progManage.getAllProgList().list;
		Log.d(TAG, "createTestProg, size: " + list.size());
		for (HashMap<String, String> item : list) {
			String name = item.get(ProgManage.PROG_NAME);
			Log.d(TAG, "createTestProg: " + name);
		}
	}

	private void startProgramSync() {
		Intent it = new Intent("com.unionman.intent.SERVICE_SYNC_PROGRAM");
		it.setPackage("com.um.dvbsearch");
		startService(it);
	}
	
	private static SourceManager getSourceManager() {
		return UmtvManager.getInstance().getSourceManager();
	}

	private void setFullScreen() {
		RectInfo rect = new RectInfo();
		rect.setX(0);
		rect.setY(0);
		rect.setW(1920);
		rect.setH(1080);
		getSourceManager().setWindowRect(rect, 0);
		Picture picture = UmtvManager.getInstance().getPicture(); 
		int aspect= picture.getAspect(); 
		picture.setAspect(aspect, false);
	}
	
	private void switchSource(final int curSourceId, final int destSourceId) {
		mThread = new Thread() {
			public void run() {
				Log.v(TAG, "ready to switch source (" + curSourceId + "->"
						+ destSourceId + ")");
				try {
					getSourceManager().deselectSource(curSourceId, true);
					//KillAppsBeforeSwitchSource();
					KillBlackListApps();
					KillNonsystemApps();
					//waitForSwitchLock();
					getSourceManager().selectSource(destSourceId, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.v(TAG, "switch source completed");
				mSwitchingFlag = false;
			}
		};
		mThread.start();

	}

	@Override
	protected void onPause() {
		Log.v(TAG, "EntryActivity onPause");
		unregistReciver();
		finish();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
        SystemProperties.set("persist.sys.fullScreen_Source", ""+mDestSourceId);		
		super.onResume();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "EntryActivity onDestroy");

		super.onDestroy();
	}

	private int getSyncProgState()
	{
		int curSourceId = getSourceManager().getCurSourceId(0);
		int state = 1;
		if(curSourceId==EnumSourceIndex.SOURCE_DVBC)
		{
			state = ParamSave.getProgDVBCSyncStatus(mContext);
		}
		else
		{
			state = ParamSave.getProgDTMBSyncStatus(mContext);	
		}
		return state;
	}

    private void KillAppsBeforeSwitchSource()
    {
    	synchronized (mSwitchLock) {
	        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
	        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
	    	for (int i=0; i<processes.size(); i++)
	    	{
	    		ActivityManager.RunningAppProcessInfo actRSI = processes.get(i);
	    		if(!actRSI.processName.equals("com.um.launcher")
	                && !actRSI.processName.equals("com.android.musicfx")
	                && !actRSI.processName.equals("com.um.umreceiver")
	                && !actRSI.processName.equals("com.unionman.netsetup")
	                && !actRSI.processName.equals("android.process.acore")
	                && !actRSI.processName.equals("cn.com.unionman.umtvsetting.umsettingmenu")
					&& !actRSI.processName.equals("com.um.dvb.entry")
	                && !actRSI.processName.equals("com.um.dvb")
	                && !actRSI.processName.equals("com.um.dvbsearch")
	                && !actRSI.processName.equals("com.unionman.dvbprovider")
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
	                && !actRSI.processName.equals("com.unionman.remoteserver")
	                && !actRSI.processName.equals("com.unionman.factorytestassist")
	                && !actRSI.processName.equals("com.unionman.factorytestassist.PrepareActivity")
					&& !actRSI.processName.contains("inputmethod"))	   
	    		{
	    			Log.v(TAG, "umdvb >>>> Kill process: " + actRSI.processName);
	    			am.forceStopPackage(actRSI.processName);
	    		}
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
  

  private void KillBlackListApps() {
		File file = new File("/vendor/etc/blacklist.xml");
		Log.d(TAG, "KillBlackListApps Enter ");
		if(file.exists() && file.isFile()){
	    	try {
				
		    	InputStream xml = new FileInputStream(file);
		    	ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		    	XmlPullParser pullParser = Xml.newPullParser();
		        pullParser.setInput(xml, "UTF-8"); //濞戞挾鐦籾ll閻熸瑱缍侀崳鎾闯閵婎煈鍟庣紓鍐惧枦椤懐鎲撮敐鍡欙拷闁汇劌鍨昅L闁轰胶澧楀畵锟�      
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
	    		Toast.makeText(mContext, "ERROR: blacklist.xml not found.", 
						Toast.LENGTH_LONG).show();
	    		return;
	    	} catch (XmlPullParserException e) {
	    		e.printStackTrace();
	    		Toast.makeText(mContext, "ERROR: parse blacklist.xml failed.", 
						Toast.LENGTH_LONG).show();
	        	return;
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		Toast.makeText(mContext, "ERROR: read blacklist.xml failed.", 
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
		ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (int i=0; i<processes.size(); i++){
			ActivityManager.RunningAppProcessInfo infor = processes.get(i);
			String[] pkgNameList =  infor.pkgList;; // 閼惧嘲绶辨潻鎰攽閸︺劏顕氭潻娑氣柤闁插瞼娈戦幍锟芥箒鎼存梻鏁ょ粙瀣碍閸栵拷
	        // 鏉堟挸鍤幍锟芥箒鎼存梻鏁ょ粙瀣碍閻ㄥ嫬瀵橀崥锟�
	        for (int j = 0; j < pkgNameList.length; j++) {  
	            String pkgName = pkgNameList[j];  
	            Log.i(TAG, "packageName " + pkgName + " at index " + j); 	
	            if(pkgName.equals("com.huan.appstore")){
	            	continue;
	            }
				if(!isSystemApplication(mContext.getPackageManager(), pkgName)){
					Log.i(TAG, "forceStopPackage " + pkgName + " at index " + j); 		
					am.forceStopPackage(pkgName);
				} 
	        }
		}
	}
		
}
