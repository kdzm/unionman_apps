
package com.um.launcher.util;

import java.util.List;

import android.os.IBinder;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.util.Log;

public class UtilLauncher {

    private static final String TAG = "UtilLauncher";

    /**
     * get index of Parameters from array
     *
     * @param mode
     * @param arrays
     * @return index of Parameters
     */
    public static int getIndexFromArray(int mode, int[][] arrays) {
        int n = 0;
        if (Constant.LOG_TAG) {
            Log.i(TAG, "getIndexFromArray");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i(TAG, "getIndexFromArray=" + i);
            }
            if (arrays[i][0] == mode) {
                n = i;
                return n;
            }
        }
        return n;
    }

    /**
     * create array of parameters
     *
     * @param arrays
     * @return array of Parameters
     */
    public static int[] createArrayOfParameters(int[][] arrays) {
        int[] n = new int[arrays.length];
        if (Constant.LOG_TAG) {
            Log.i(TAG, "createArrayOfParameters");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i(TAG, "createArrayOfParameters=" + i);
            }
            n[i] = arrays[i][1];
        }
        return n;
    }

    /**
     * get value of Parameters from array
     *
     * @param mode
     * @param arrays
     * @return value of Parameters
     */
    public static int getValueFromArray(int mode, int[][] arrays) {
        int n = 0;
        if (Constant.LOG_TAG) {
            Log.i(TAG, "getValueFromArray");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i(TAG, "getValueFromArray=" + i);
            }
            if (arrays[i][0] == mode) {
                n = arrays[i][1];
                return n;
            }
        }
        return n;
    }

    /**
     * Gets the getMountService
     *
     * @return
     */
    public static synchronized IMountService getMountService() {
        IMountService mMountService = null;
        if (mMountService == null) {
            IBinder service = ServiceManager.getService("mount");
            if (service != null) {
                mMountService = IMountService.Stub.asInterface(service);
            } else {
                if (Constant.LOG_TAG) {
                    Log.e(TAG, "Can't get munt service");
                }
            }
        }
        return mMountService;
    }
    
    public static boolean isExtralDevicesMount() {
        String[] path = new String[64];
        int cnt = 0, count = 0;
        try {
            // support for DevType
            IBinder service = ServiceManager.getService("mount");

            if (service != null) {
                IMountService mountService = IMountService.Stub
                                             .asInterface(service);
                List<android.os.storage.ExtraInfo> mountList = mountService
                                                               .getAllExtraInfos();
                cnt = mountList.size();
                Log.d("leon","leon... cnt="+cnt);
                for (int i = 0; i < cnt; i++) {
                    path[i] = mountList.get(i).mMountPoint;

                    String typeStr = mountList.get(i).mDevType;
                    Log.d("leon","leon... typeStr="+typeStr);
                    if (path[i].contains("/mnt/nand")) {
                    	count++;
                    }
                    else if (typeStr.equals("SDCARD") && !(path[i].contains("/storage/emulated"))) {
                    	count++;
                    }
                    else if (typeStr.equals("SATA")) {
                    	count++;
                    }
                    else if (typeStr.equals("USB")) {
                    	count++;
                    }
                    else if (typeStr.equals("USB2.0")) {
                    	count++;
                    }
                    else if (typeStr.equals("USB3.0")) {
                    	count++;
                    }
                    else if (typeStr.equals("UNKOWN")) {
                    	count++;
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        
        Log.d("leon","leon... count="+count);
        if (count > 0){
        	return true;
        }else{
        	return false;
        }
    }
}
