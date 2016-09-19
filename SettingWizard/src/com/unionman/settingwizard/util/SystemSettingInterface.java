package com.unionman.settingwizard.util;

import android.util.Log;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusPicture;

/**
 * interface of system setting
 *
 * @author huyq
 *
 */
public class SystemSettingInterface {
    public static final String TAG = "SystemSettingInterface";

    
	 /**
     * get instance of Picture
     *
     * @return
     */
    public static CusPicture getPictureManager() {
        return UmtvManager.getInstance().getPicture();
    }     
    
    public static boolean getStoreMode() {
    	boolean value = getPictureManager().getStoreMode(); 
    	return value;
    }
    
    public static int setStoreMode(int storeMode) {
    	int value = getPictureManager().setStoreMode(storeMode==1?true:false); 
    	return value;
    }
}
