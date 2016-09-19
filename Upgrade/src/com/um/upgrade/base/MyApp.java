package com.um.upgrade.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.um.upgrade.DefaultParameter;
import com.um.upgrade.data.DeviceInfo;
import com.um.upgrade.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import android.os.SystemProperties;
import android.util.Log;
import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.impl.CusFactoryImpl;

/**
 * Created by ziliang.nong on 14-6-16.
 */
public class MyApp extends Application{
    private final String TAG = "MyApp_Upgrade";
    private final boolean LOG_EN = true;

    private static String machineModel = null;
    private static String productModel = null;
    private static String vendor = null;
    private static String softVersion = null;
    private static String hardVersion = null;
    private static String serialAndMac = null;
    private static String dvbFlag = null;

    private static Context mContext;
    private static SharedPreferences mSharedPerferences = null;
    private static List<Activity> mActivities = null;
    private static DeviceInfo mDeviceInfo;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mSharedPerferences = getSharedPreferences("com.um.upgrade", Context.MODE_PRIVATE);
        mActivities = new LinkedList<Activity>();

        String sw_version = getDisplaySoftVersion();
        if (!StringUtils.isBlank(sw_version)) {
			int modelLen = DefaultParameter.STB_PRODUCT_MODEL.length();
			Log.v(TAG, "modelLen="+modelLen);
			if (sw_version.length() > modelLen+2) {
				productModel = sw_version.substring(0, modelLen);
				softVersion = sw_version.substring(modelLen, modelLen+2) + "." + sw_version.substring(modelLen+2);
				hardVersion = CusFactoryImpl.getInstance().getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_HARDWARE_VER);
				Log.v(TAG, "productModel=" + productModel
						+ ", softVersion=" + softVersion + ", hardVersion="
						+ hardVersion);
			}
		}

        mDeviceInfo = initDeviceInfo();
    }

    public static Context getContext () {
        return mContext;
    }

    private DeviceInfo initDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setHardwareVersion(getHardVersion());
        deviceInfo.setProduceModel(getProductModel());
        deviceInfo.setSerial(getSerialNo());
        deviceInfo.setSoftwareVersion(getSoftVersion());
        deviceInfo.setVendor(getVendor());
        deviceInfo.setDisplaySerial(getShowedSerialNo());
        deviceInfo.setMac(getMac());
        deviceInfo.setDisplaySoftVersion(getDisplaySoftVersion());
        deviceInfo.setMachineModel(getMachineModel());

        return deviceInfo;
    }

    public static DeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }
    public static boolean hasDvb() {
        if (StringUtils.isBlank(dvbFlag)) {
            dvbFlag = SystemProperties.get("ro.product.umdvb", DefaultParameter.STB_DVB_FLAG);
        }

        if (dvbFlag.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    private static String getMachineModel() {
        if (StringUtils.isBlank(machineModel)) {
            machineModel = SystemProperties.get("ro.product.model", DefaultParameter.STB_MACHINE_MODEL);
        }
        return machineModel;
    }

    private static String getProductModel() {
        if (StringUtils.isBlank(productModel)) {
            productModel = DefaultParameter.STB_PRODUCT_MODEL;
        }

        return productModel;
    }

    private static String getVendor() {
        if (StringUtils.isBlank(vendor)) {
            vendor = SystemProperties.get("ro.product.manufacturer", DefaultParameter.STB_VENDOR);
        }

        if (vendor.equalsIgnoreCase("unknown") || StringUtils.isBlank(vendor)) {
            vendor = DefaultParameter.STB_VENDOR;
        }

        return vendor;
    }

    private static String getSoftVersion() {
        if (StringUtils.isBlank(softVersion)) {
            softVersion = DefaultParameter.STB_SOFTWARE_VERSION;
        }
        return softVersion;
    }

    private static String getDisplaySoftVersion() {
        return SystemProperties.get("ro.umtv.sw.version", DefaultParameter.STB_SOFTWARE_VERSION);
    }

    private static String getHardVersion() {
        if (StringUtils.isBlank(hardVersion)) {
            hardVersion = DefaultParameter.STB_HARDWARE_VERSION;
        }
        return hardVersion;
    }

    private static String getSerialNo() {
        if (serialAndMac == null) {
            serialAndMac = SystemProperties.get("ro.serialno", DefaultParameter.STB_SERIAL_NO);
        }

        String serialNo;
        if (StringUtils.isBlank(serialAndMac)) {
            serialNo = "";
        } else {
            serialNo = serialAndMac.substring(0, DefaultParameter.STB_SERIAL_LEN);
        }
        return serialNo;
    }

    private static String getShowedSerialNo() {
        if (serialAndMac == null) {
            serialAndMac = SystemProperties.get("ro.serialno", DefaultParameter.STB_SERIAL_NO);
        }
        int showedSerialEnd = DefaultParameter.STB_SHOWED_SERIAL_START + DefaultParameter.STB_SHOWED_SERIAL_LEN;
        String showedSerialNo;
        if (StringUtils.isBlank(serialAndMac)) {
            showedSerialNo = "";
        } else {
            showedSerialNo = serialAndMac.substring(DefaultParameter.STB_SHOWED_SERIAL_START, showedSerialEnd);
        }
        return showedSerialNo;
    }

    private static String getMac() {
        if (serialAndMac == null) {
            serialAndMac = SystemProperties.get("ro.serialno", DefaultParameter.STB_SERIAL_NO);
        }

        String mac;
        if (serialAndMac == null || serialAndMac.trim().length() == 0) {
            mac = "";
        } else {
            mac = serialAndMac.substring(DefaultParameter.STB_SERIAL_LEN);
        }
        return mac;
    }

    public static void setServerUrl(String url) {
        SharedPreferences.Editor editor = mSharedPerferences.edit();
        editor.putString("serverUrl", url);
        editor.commit();
    }

    public static String getServerUrl() {
        return mSharedPerferences.getString("serverUrl", DefaultParameter.STB_SERVER_URL);
    }

    public static void setBackupServerUrl(String url) {
        SharedPreferences.Editor editor = mSharedPerferences.edit();
        editor.putString("backupServerUrl", url);
        editor.commit();
    }

    public static String getBackupServerUrl() {
        return mSharedPerferences.getString("backupServerUrl", DefaultParameter.STB_BACKUP_SERVER_URL);
    }

    public static void setKeyDisable() {
        SystemProperties.set("runtime.unionman.disablekey", "1");
    }

    public static void setKeyEnable(){
        SystemProperties.set("runtime.unionman.disablekey", "0");
    }

    public static void setCableUpgradeManual() {
        SystemProperties.set("runtime.ota.cable.check.manual","0");
    }

    public static void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    public static void delActivity(Activity activity) {
        mActivities.remove(activity);
    }

    public static void exitAllAtivities() {
        for (Activity activity : mActivities) {
            activity.finish();
        }
    }
}
