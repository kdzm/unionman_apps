package com.source.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

/**
 * including the selector view control operation data transform second level
 * menu
 *
 * @author wangchuanjian
 *
 */
public class Util {

    private static final String TAG = "Util";

	/**
     * get index of Parameters from array
     *
     * @param mode
     * @param arrays
     * @return index of Parameters
     */
    public static int getIndexFromArray(int mode, int[][] arrays) {
        int num = 0;
        if (Constant.LOG_TAG) {
            Log.i("getIndexFromArray", "getIndexFromArray");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i("getIndexFromArray", "getIndexFromArray=" + i);
            }
            if (arrays[i][0] == mode) {
                num = i;
                return num;
            }
        }
        return num;
    }

    /**
     * create array of parameters
     *
     * @param arrays
     * @return array of Parameters
     */
    public static int[] createArrayOfParameters(int[][] arrays) {
        int[] num = new int[arrays.length];
        if (Constant.LOG_TAG) {
            Log.i("createArrayOfParameters", "createArrayOfParameters");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i("createArrayOfParameters", "createArrayOfParameters=" + i);
            }
            num[i] = arrays[i][1];
        }
        return num;
    }
    
	/**
	 * 判断应用是否正在运行,包括是否在后台运行
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */ 
   public static boolean isRunning(Context context, String packageName) { 
	    ActivityManager am = (ActivityManager) context 
	            .getSystemService(Context.ACTIVITY_SERVICE); 
	    List<RunningAppProcessInfo> list = am.getRunningAppProcesses(); 
	    for (RunningAppProcessInfo appProcess : list) { 
	        String processName = appProcess.processName; 
	        if (processName != null && processName.equals(packageName)) { 
	            return true; 
	        } 
	    } 
	    return false; 
	}
   
		/**
		 * 判断应用是否在栈顶运行
		 * 
		 * @param context
		 * @param subPackageName 部分包名
		 * @return
		 */ 
	public static boolean isRunningAtTop(Context context, String subPackageName) { 
		    ActivityManager am = (ActivityManager) context 
		            .getSystemService(Context.ACTIVITY_SERVICE); 
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			String topPackageName = cn.getPackageName();
			Log.i(TAG,"=== isRunningAtTop()  topPackageName="+topPackageName);
			if(topPackageName != null && topPackageName.contains(subPackageName)){
				return true;
			}
		    return false; 
		}
	/**
	 * 判断是否有7大牌照方的apk在栈顶运行   
	 * @param context
	 * @return
	 */
	public static boolean isCmccApkRunningAtTop(Context context){
		//baishitong pagName: com.bestv.ott.baseservices
	       boolean isBestTVRunning = isRunningAtTop(context, "com.bestv");
		//CIBN  pagName:cn.cibntv.ott86
	       boolean isCIBNRunning = isRunningAtTop(context, "cn.cibntv");
		//GITV  pagName:com.gitv.tv.launcher || com.gitv.tv.live
	       boolean isGITVRunning = isRunningAtTop(context, "com.gitv");
		//huashu  pagName:net.sunniwell.app.ott.chinamobile
	       boolean isHuashuRunning = isRunningAtTop(context, "net.sunniwell.app.ott.chinamobile");
		//mangguoTV pagName:com.starcor.hunan
	       boolean isMangguoTVRunning = isRunningAtTop(context, "com.starcor.hunan");
		//weilaiTV pagName:tv.icntv.ott
	       boolean isWeilaiTVRunning = isRunningAtTop(context, "tv.icntv");
		//youpeng  pagName:com.voole.webepg || com.live.webepglive
	       boolean isYoupengRunning = isRunningAtTop(context, "com.voole.webepg") || isRunningAtTop(context, "com.live.webepglive");
		//guangdong cmcc    pagName:cn.gd.snm.snmcm
		 boolean isGdCmccRunning = isRunningAtTop(context, "cn.gd.snm.snmcm") ;   
		return (isBestTVRunning || isCIBNRunning || isGITVRunning || isHuashuRunning || isMangguoTVRunning || isWeilaiTVRunning || isYoupengRunning || isGdCmccRunning);	
	}
}
