package com.um.tv.menu.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.UmtvManager;

public class TvMenuWindowManagerService extends Service {

	private static final String TAG = "UMFACTORYMENU";
	private static final String SUBTAG = "TvMenuWindowManagerService:";
    public static final String COMMAND = "com.um.tv.menu.commmand";
    public static final String KEY = "com.um.tv.menu.key";
    public static final String EXTRA = "com.um.tv.menu.extra";
    public static final String COMMAND_START_SERVICE = "com.um.tv.menu.commmand.start_service";
    public static final String COMMAND_FACTORY_MENU = "com.um.tv.menu.commmand.factory_menu";
    public static final String COMMAND_AGING_MENU = "com.um.tv.menu.commmand.aging_menu";
    public static final String COMMAND_MMODE_KEY = "com.um.tv.menu.commmand.mmode_key";
    
    private FactoryWindowManager mFactoryManager = null;
    private AgingWindowManager mAgingManager = null;
    private MmodeKeyManager mMmodeKeyManager = null;
    private CusFactory mFactory = UmtvManager.getInstance().getFactory();
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        
        if (mFactoryManager == null) {
            mFactoryManager = FactoryWindowManager.from(this);
        }
        
	   	 if (mAgingManager == null) {
			 mAgingManager = AgingWindowManager.from(this);
	     }
	   	 
     	if (mMmodeKeyManager == null){
    		mMmodeKeyManager = MmodeKeyManager.from(this);
    	}    	
     	
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        String command = intent == null ? null : intent.getStringExtra(COMMAND);
        Log.d(TAG,SUBTAG+"onStartCommand command "+command+" equals "+command.equals(COMMAND_FACTORY_MENU));
        if (command != null) {
            if (command.equals(COMMAND_FACTORY_MENU)) {

				Log.d(TAG,SUBTAG+"mFactoryManager.isFactoryShow() "+mFactoryManager.isFactoryShow());
                if (mFactoryManager.isFactoryShow()){
                	mFactoryManager.changeFactoryMenuStatus();
                }else{
                	String extraString = intent == null ? null : intent.getStringExtra(EXTRA);
                	if (extraString != null){
						Log.d(TAG,SUBTAG+"mFactoryManager(extraString != null) extraString "+extraString);
                		mFactoryManager.changeFactoryMenuStatus();
                	}else{
                    	if (mFactory.isMModeEnable() && (mAgingManager.isShowAging() == false)){
                    		mFactoryManager.changeFactoryMenuStatus();
                    	}
                	}
                }
            }else if (command.equals(COMMAND_AGING_MENU)){
            	 if (mAgingManager.isShowAging()){
            		 mAgingManager.changeFactoryMenuStatus();
            	 }else{
            		 if (mFactory.isMModeEnable() && (mFactoryManager.isFactoryShow() == false)){
            			 mMmodeKeyManager.mModeDismissNote();
            			 mAgingManager.changeFactoryMenuStatus();
            			 mMmodeKeyManager.mModeKeyProcess(false);
            		 }
            		 
            		 String bootup = intent.getStringExtra("bootup");
            		 if (bootup != null){
            			 mAgingManager.changeFactoryMenuStatus();
            		 }
            	 }
            }else if (command.equals(COMMAND_MMODE_KEY)){
            	int key = 0;
            	key = intent.getIntExtra(KEY, 0);
            	if ((key == KeyEvent.KEY_MMODE) || (key == KeyEvent.KEY_TESMMODE)){
            		Log.d(TAG,SUBTAG+"KeyEvent.KEY_MMODE---> ismModeNoteShow "+mMmodeKeyManager.ismModeNoteShow());
            		if (mMmodeKeyManager.ismModeNoteShow() == false){
            			boolean enable = true; 
            			String extraString = intent == null ? null : intent.getStringExtra(EXTRA);
            			if (extraString != null){
            				if (extraString.equals("open")){
            					enable = true;
            				}else{
            					enable = true;
            				}
            			}
						Log.d(TAG,SUBTAG+"extraString "+extraString);
            			Log.d(TAG,SUBTAG+"KeyEvent.KEY_MMODE--->enable:"+enable);
            			mMmodeKeyManager.mModeKeyProcess(enable);
            		}else if(mMmodeKeyManager.ismModeNoteShow() == true){
            			boolean enable = false; 
						Log.d(TAG,SUBTAG+"KeyEvent.KEY_MMODE--->enable:"+enable);
            			mMmodeKeyManager.mModeKeyProcess(enable);
            		}
            	}else{
            		 if (mFactory.isMModeEnable() && (mFactoryManager.isFactoryShow() == false)
            				 && (mAgingManager.isShowAging() == false)){
            			 mMmodeKeyManager.mModeQuickKeyProcess(key);
            		 }
            	}
            }else if (command.equals(COMMAND_START_SERVICE)){
            	mMmodeKeyManager.mModeBootProcess();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
