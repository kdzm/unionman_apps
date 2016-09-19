package cn.com.unionman.umtvsystemserver;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSystemSetting;

public class PowerDownEventReciver extends BroadcastReceiver{
	private static final String RESET_MONITORS = "cn.com.unionman.umtvsystemserver.RESET_MONITORS";
	private static final String NOSIGNAL_PD_BROADCAST = "android.intent.action.NOSIGNAL_POWERDOWN_BROADCAST";
	private static final String SLEEP_NO_BROADCAST = "android.intent.action.SLEEP_NO_BROADCAST";
	private static String TAG = "PowerDownEventReciver";
	public static final int POWERDOWN_TYPE_SLEEPON = 1;
	public static final int POWERDOWN_TYPE_NOSIGNAL_OR_NOHANDLE = 2;
	private Context mContext;
	private Dialog mSleepOnDialog;
	private static PowerDownDialog mDialog;
	private static boolean mDialogOpenFlag = false;
	private static CusSystemSetting  sysSetting;
	
	private CusFactory mFactory = UmtvManager.getInstance().getFactory();
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			mContext = arg0;
			Log.i(TAG,"PowerDownEventReciver "+intent.getAction());
			
			if (mFactory.isMModeEnable() || mFactory.isAgingModeEnable()){
				return;
			}
			
			if (intent.getAction().equals(NOSIGNAL_PD_BROADCAST)) {
				String modeString = intent.getStringExtra("mode");
				Log.i(TAG,"PowerDownEventReciver :"+modeString);
				if ((modeString != null) && (modeString.equals("close"))){
					closePowerDownDialog();
				}else if ((modeString != null) && (modeString.equals("direct"))){
					  Log.i(TAG,"Power Down direct now");
					  Intent intent1 = new Intent(RESET_MONITORS);
					  intent1.putExtra("mode", "powerdown");
					  mContext.sendBroadcast(intent1);

  					  sysSetting = UmtvManager.getInstance().getSystemSetting();
  					  sysSetting.suspend();
				  	  Intent intent2 = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
				  	  intent2.putExtra("android.intent.extra.KEY_CONFIRM", false);
				  	  intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  	  mContext.startActivity(intent2);
				}else{
					if (checkSourceStatus() && (checkSignalStatus() == false)){
						openPowerDownDialogWithTimeout(30, intent);
					}
				}				
			}else if (intent.getAction().equals(SLEEP_NO_BROADCAST)){
				openSleepOnPowerDownDialogWithTimeout(30, intent);
			}else{
				String modeString = intent.getStringExtra("mode");
				
				if ((modeString != null) && (modeString.equals("close"))){
					closePowerDownDialog();
				}else{
					openPowerDownDialogWithTimeout(30, intent);
				}
			}
		}
	
		private void openSleepOnPowerDownDialogWithTimeout(int tminsecond,Intent intent){
			Log.i(TAG,"openSleepOnPowerDownDialogWithTimeout");
			
			mSleepOnDialog = new PowerDownDialog(mContext, tminsecond, POWERDOWN_TYPE_SLEEPON);
			mSleepOnDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					try{
				        Context otherContext = mContext.createPackageContext(
				                "cn.com.unionman.umtvsetting.system", Context.CONTEXT_IGNORE_SECURITY);
				        Editor sharedata = otherContext.getSharedPreferences(
				                  "itemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
				                  + Context.MODE_MULTI_PROCESS).edit();
			 		    sharedata.putInt("sleeponState",0); 
			 		    sharedata.commit();
					}catch (NameNotFoundException e) {
			        	e.printStackTrace();
			        }
					
					Intent intent = new Intent(RESET_MONITORS);
					mContext.sendBroadcast(intent);
				}
			});
			mSleepOnDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			mSleepOnDialog.show();
		}
		
		private void openPowerDownDialogWithTimeout(int tminsecond,Intent intent){
			Log.i(TAG,"openPowerDownDialogWithTimeout");
			
			if (mDialogOpenFlag == false){
				mDialog = new PowerDownDialog(mContext, tminsecond, POWERDOWN_TYPE_NOSIGNAL_OR_NOHANDLE);
				mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						mDialogOpenFlag = false;
						savePowerDownDialogFlag();
						Intent intent = new Intent(RESET_MONITORS);
						mContext.sendBroadcast(intent);
					}
				});
				mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				mDialogOpenFlag = true;
				savePowerDownDialogFlag();
				Log.i(TAG,"openPowerDownDialogWithTimeout savePowerDownDialogFlag true");
				mDialog.show();
			}	
		}
	
		private void closePowerDownDialog(){
			Log.i(TAG,"closePowerDownDialog mDialogOpenFlag:"+ mDialogOpenFlag);
			if (mDialogOpenFlag == true){
				mDialogOpenFlag = false;
				savePowerDownDialogFlag();
				mDialog.myCancel();
			}
		}
	
		private void savePowerDownDialogFlag(){
			 Editor sharedata = mContext.getSharedPreferences("powerDownDialog", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                 + Context.MODE_MULTI_PROCESS).edit();  
		      sharedata.putBoolean("powerDownDialogFlag",mDialogOpenFlag); 
		      sharedata.commit(); 
		}
		
		private boolean checkSourceStatus(){
			int currSourceId = SourceManagerInterface.getSelectSourceId();
			Log.i(TAG, "checkSourceStatus currSourceId"+currSourceId);
			boolean b = ((currSourceId==EnumSourceIndex.SOURCE_VGA||currSourceId==EnumSourceIndex.SOURCE_DVBC||
		     		   currSourceId==EnumSourceIndex.SOURCE_DTMB||currSourceId==EnumSourceIndex.SOURCE_ATV));
			Log.i(TAG,"checkSourceStatus "+b);
			return true;
		}
	
		private boolean checkSignalStatus(){
			int currSourceId = SourceManagerInterface.getSelectSourceId();
			if ((currSourceId == EnumSourceIndex.SOURCE_VGA) || (currSourceId == EnumSourceIndex.SOURCE_ATV)){
				int sigstat = SourceManagerInterface.getSignalStatus();
				if (sigstat==EnumSignalStat.SIGSTAT_NOSIGNAL) {
					Log.i(TAG, "checkSignalStatus SIGSTAT_NOSIGNAL");
					return false;
				} else if(sigstat == EnumSignalStat.SIGSTAT_SUPPORT){
					Log.i(TAG, "checkSignalStatus SIGSTAT_SUPPORT");
					return true;
				}
				Log.i(TAG,"checkSignalStatus false");
			    	return false;
			}
			
			return false;
		}

	}
