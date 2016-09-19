package com.um.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ErrorReport {
	
	public static void sendErrorReport(Context context,String what){
		Intent mIntent=new Intent("android.unionman.action.ERROR_REPORT");
		if("invalid_request".equals(what)){
			mIntent.putExtra("code_type","29001");
			Log.i("ERROR_REPORT", "sendErrorReport 29001");
		}else if("invalid_client".equals(what)){
			mIntent.putExtra("code_type","29002");
			Log.i("ERROR_REPORT", "sendErrorReport 29002");
		}else if("invalid_grant".equals(what)){
			mIntent.putExtra("code_type","29003");
			Log.i("ERROR_REPORT", "sendErrorReport 29003");
		}else if("unauthorized_client".equals(what)){
			mIntent.putExtra("code_type","29004");
			Log.i("ERROR_REPORT", "sendErrorReport 29004");
		}else if("unsupported_grant_type".equals(what)){
			mIntent.putExtra("code_type","29005");
			Log.i("ERROR_REPORT", "sendErrorReport 29005");
		}else if("invalid_scope".equals(what)){
			mIntent.putExtra("code_type","29006");
			Log.i("ERROR_REPORT", "sendErrorReport 29006");
		}else if("access_denied".equals(what)){
			mIntent.putExtra("code_type","29007");
			Log.i("ERROR_REPORT", "sendErrorReport 29007");
		}else if("unsupported_response_type".equals(what)){
			mIntent.putExtra("code_type","29008");
			Log.i("ERROR_REPORT", "sendErrorReport 29008");
		}else if("invalid_certificate".equals(what)){
			mIntent.putExtra("code_type","29009");
			Log.i("ERROR_REPORT", "sendErrorReport 29009");
		}else if("userstate_suspend".equals(what)){
			mIntent.putExtra("code_type","29010");
			Log.i("ERROR_REPORT", "sendErrorReport 29010");
		}else if("userstate_stopbyself".equals(what)){
			mIntent.putExtra("code_type","29011");
			Log.i("ERROR_REPORT", "sendErrorReport 29011");
		}else if("userstate_stopbyfee".equals(what)){
			mIntent.putExtra("code_type","29012");
			Log.i("ERROR_REPORT", "sendErrorReport 29012");
		}else if("user_notexist".equals(what)){
			mIntent.putExtra("code_type","29013");
			Log.i("ERROR_REPORT", "sendErrorReport 29013");
		}else if("invalid_checkcode".equals(what)){
			mIntent.putExtra("code_type","29014");
			Log.i("ERROR_REPORT", "sendErrorReport 29014");
		}else if("checkcode_expire".equals(what)){
			mIntent.putExtra("code_type","29015");
			Log.i("ERROR_REPORT", "sendErrorReport 29015");
		}else if("account_nobind".equals(what)){
			mIntent.putExtra("code_type","29016");
			Log.i("ERROR_REPORT", "sendErrorReport 29016");
		}else if("invalid_authtype".equals(what)){
			mIntent.putExtra("code_type","29017");
			Log.i("ERROR_REPORT", "sendErrorReport 29017");
		}else if("invalid_password".equals(what)){
			mIntent.putExtra("code_type","29018");
			Log.i("ERROR_REPORT", "sendErrorReport 29018");
		}else if("account_loced".equals(what)){
			mIntent.putExtra("code_type","29019");
			Log.i("ERROR_REPORT", "sendErrorReport 29019");
		}else if("invalid_mac".equals(what)){
			mIntent.putExtra("code_type","29020");
			Log.i("ERROR_REPORT", "sendErrorReport 29020");
		}else if("mac_occupied".equals(what)){
			mIntent.putExtra("code_type","29021");
			Log.i("ERROR_REPORT", "sendErrorReport 29021");
		}else{
			return;
		}
		context.sendBroadcast(mIntent);
	}
}
