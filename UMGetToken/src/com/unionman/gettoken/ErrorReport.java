package com.unionman.gettoken;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ErrorReport {
	
	public static void sendErrorReport(Context context,String what){
		Intent mIntent=new Intent("android.unionman.action.ERROR_REPORT");
		if("70116001".equals(what)||"70116101".equals(what)||"70116203".equals(what)||"70116108".equals(what)){
			mIntent.putExtra("code_type","29013");
			Log.i("ERROR_REPORT", "sendErrorReport 29013");
		}else if("70116002".equals(what)||"70116206".equals(what)){
			mIntent.putExtra("code_type","29018");
			Log.i("ERROR_REPORT", "sendErrorReport 29018");
		}else if("70116003".equals(what)||"70116104".equals(what)){
			mIntent.putExtra("code_type","29019");
			Log.i("ERROR_REPORT", "sendErrorReport 29019");
		}else if("70106010".equals(what)||"70106100".equals(what)||"70106200".equals(what)||"80106100".equals(what)||"80106200".equals(what)){
			mIntent.putExtra("code_type","29022");
			Log.i("ERROR_REPORT", "sendErrorReport 29022");
		}else if("70116102".equals(what)){
			mIntent.putExtra("code_type","29010");
			Log.i("ERROR_REPORT", "sendErrorReport 29010");
		}else if("70116103".equals(what)||"70116106".equals(what)){
			mIntent.putExtra("code_type","29024");
			Log.i("ERROR_REPORT", "sendErrorReport 29024");
		}else if("70116105".equals(what)){
			mIntent.putExtra("code_type","29025");
			Log.i("ERROR_REPORT", "sendErrorReport 29025");
		}else if("70116107".equals(what)){
			mIntent.putExtra("code_type","29011");
			Log.i("ERROR_REPORT", "sendErrorReport 29011");
		}else if("70116109".equals(what)){
			mIntent.putExtra("code_type","29012");
			Log.i("ERROR_REPORT", "sendErrorReport 29012");
		}else if("70116115".equals(what)){
			mIntent.putExtra("code_type","29026");
			Log.i("ERROR_REPORT", "sendErrorReport 29026");
		}else if("70116199".equals(what)||"70116299".equals(what)){
			mIntent.putExtra("code_type","29027");
			Log.i("ERROR_REPORT", "sendErrorReport 29027");
		}else if("50991008".equals(what)||"50019998".equals(what)){
			mIntent.putExtra("code_type","29028");
			Log.i("ERROR_REPORT", "sendErrorReport 29028");
		}else if("80106101".equals(what)){
			mIntent.putExtra("code_type","29005");
			Log.i("ERROR_REPORT", "sendErrorReport 29005");
		}else if("80106104".equals(what)){
			mIntent.putExtra("code_type","29029");
			Log.i("ERROR_REPORT", "sendErrorReport 29029");
		}else if("70116201".equals(what)){
			mIntent.putExtra("code_type","29009");
			Log.i("ERROR_REPORT", "sendErrorReport 29009");
		}else if("70116202".equals(what)){
			mIntent.putExtra("code_type","29002");
			Log.i("ERROR_REPORT", "sendErrorReport 29002");
		}else if("70116204".equals(what)||"70116209".equals(what)){
			mIntent.putExtra("code_type","29023");
			Log.i("ERROR_REPORT", "sendErrorReport 29023");
		}else if("70116207".equals(what)){
			mIntent.putExtra("code_type","");
			Log.i("ERROR_REPORT", "sendErrorReport undifine");
		}else if("70116214".equals(what)){
			mIntent.putExtra("code_type","29021");
			Log.i("ERROR_REPORT", "sendErrorReport 29021");
		}else if("80106208".equals(what)){
			mIntent.putExtra("code_type","29030");
			Log.i("ERROR_REPORT", "sendErrorReport 29030");
		}else if("80106207".equals(what)){
			mIntent.putExtra("code_type","29031");
			Log.i("ERROR_REPORT", "sendErrorReport 29031");
		}else if("50991001".equals(what)){
			mIntent.putExtra("code_type","29032");
			Log.i("ERROR_REPORT", "sendErrorReport 29032");
		}else if("50991002".equals(what)){
			mIntent.putExtra("code_type","29033");
			Log.i("ERROR_REPORT", "sendErrorReport 29033");
		}else if("50991003".equals(what)){
			mIntent.putExtra("code_type","29034");
			Log.i("ERROR_REPORT", "sendErrorReport 29034");
		}else if("50991004".equals(what)){
			mIntent.putExtra("code_type","29035");
			Log.i("ERROR_REPORT", "sendErrorReport 29035");
		}else if("50991006".equals(what)){
			mIntent.putExtra("code_type","29036");
			Log.i("ERROR_REPORT", "sendErrorReport 29036");
		}else if("50119216".equals(what)){
			mIntent.putExtra("code_type","29037");
			Log.i("ERROR_REPORT", "sendErrorReport 29037");
		}else if("50119220".equals(what)){
			mIntent.putExtra("code_type","29038");
			Log.i("ERROR_REPORT", "sendErrorReport 29038");
		}else if("50119222".equals(what)||"50119224".equals(what)){
			mIntent.putExtra("code_type","29039");
			Log.i("ERROR_REPORT", "sendErrorReport 29039");
		}else if("50119223".equals(what)){
			mIntent.putExtra("code_type","29040");
			Log.i("ERROR_REPORT", "sendErrorReport 29040");
		}else if("50011014".equals(what)||"50119217".equals(what)){
			mIntent.putExtra("code_type","29001");
			Log.i("ERROR_REPORT", "sendErrorReport 29001");
		}else{
			return;
		}
		context.sendBroadcast(mIntent);
		
	}
}
