package com.unionman.umerrorinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @Description Receive broadcasts and start DialogActivity to show a reminder dialog.
 * @author weiyi.nong
 * @email weiyi.nong@unionman.com.cn
 * @date 2016-6-20
 */
public class ErrorInfoRecevier extends BroadcastReceiver{
	private Context context=BaseApplication.getContext();
	private SharedPreferences sharedPreferences; 
	private Editor editor; 

	@Override
	public void onReceive(Context arg0, Intent paramIntent) {
		String strAction = paramIntent.getAction();
		if (strAction.equals(DataManager.REPORT_ACTION)) {
			String strCodeType = paramIntent.getStringExtra("code_type");
			sharedPreferences=context.getSharedPreferences(DataManager.SharedPreferences_name, Context.MODE_PRIVATE);
			editor= sharedPreferences.edit();
			Boolean sp_isDialogShow=sharedPreferences.getBoolean(DataManager.SP_FLAG_SHOW, false);
			String sp_strCodeType=sharedPreferences.getString(DataManager.SP_CODE_TYPE, "");
			
			if(sp_isDialogShow){

				if (!strCodeType.equals(sp_strCodeType)) {
					Intent intent = new Intent();
					intent.setAction(DataManager.DATA_UPDAPT_ACTION);
					intent.putExtra("code_type", strCodeType);
					context.sendBroadcast(intent);
				}
			} else {
				startDialogActivity(strCodeType);
			}
			editor.putString(DataManager.SP_CODE_TYPE, strCodeType);
			editor.commit();
			
		}
	}

	private void startDialogActivity(String strCodeType) {
		Intent intent=new Intent(context,DialogActivity.class);
		intent.putExtra("code_type", strCodeType);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);	
	}
}
