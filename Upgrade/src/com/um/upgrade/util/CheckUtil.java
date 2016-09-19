package com.um.upgrade.util;

import android.content.Context;
import android.util.Log;

import com.um.upgrade.data.UpgradeInfoBean;
import com.um.upgrade.data.DeviceInfo;

import java.util.List;

/**
 * Created by ziliang.nong on 14-6-17.
 */
public class CheckUtil {
    private final static String TAG = CheckUtil.class.getSimpleName()+"----U668";
    private final static boolean LOG_EN = true;

    private CheckUtil() {
    }

    public static UpgradeInfoBean getUpgrageInfo(List<UpgradeInfoBean> upgradeInfoList, DeviceInfo deviceInfo) {
        UpgradeInfoBean upgradeInfo = null;
        for (UpgradeInfoBean upgradeInfoBean : upgradeInfoList) {
            /* 校验产品型号 */
            if (!deviceInfo.getProduceModel().equals(upgradeInfoBean.getProductModel())) {
                if (LOG_EN) Log.e(TAG, "the product model is not match");
                if (LOG_EN) Log.e(TAG, "the local product model: " + deviceInfo.getProduceModel());
                if (LOG_EN)
                    Log.e(TAG, "the remote product model: " + upgradeInfoBean.getProductModel());
                continue;
            }

            /* 校验厂商 */
            if (!deviceInfo.getVendor().equals(upgradeInfoBean.getVendor())) {
                if (LOG_EN) Log.e(TAG, "the vendor is not match");
                if (LOG_EN) Log.e(TAG, "the local stb vendor: " + deviceInfo.getVendor());
                if (LOG_EN) Log.e(TAG, "the remote stb vendor: " + upgradeInfoBean.getVendor());
                continue;
            }

            /* 校验硬件版本号 */
            if (!deviceInfo.getHardwareVersion().equals(upgradeInfoBean.getHardVersion())) {
                if (LOG_EN) Log.e(TAG, "the hardware version is not match");
                if (LOG_EN) Log.e(TAG, "the local stb hardware version: " + deviceInfo.getHardwareVersion());
                if (LOG_EN) Log.e(TAG, "the remote stb hardware version:" + upgradeInfoBean.getHardVersion());
                continue;
            }

            /* 校验软件版本号 */
            if (!checkStbVersion(deviceInfo.getSoftwareVersion(), upgradeInfoBean.getVersion(), upgradeInfoBean.getSoftMinVersion(), upgradeInfoBean.getSoftMaxVersion(), upgradeInfoBean.getSoftwares())) {
                if (LOG_EN) Log.e(TAG, "software version is not match,the local stb software version: " + deviceInfo.getSoftwareVersion());
                continue;
            }

            /* 校验序列号 */
            if (!checkStbSerial(deviceInfo.getSerial(), upgradeInfoBean.getSerials())) {
                if (LOG_EN) Log.e(TAG, "serial number is not match,the local stb serial number: " + deviceInfo.getSerial());
                break;
            }

            upgradeInfo = upgradeInfoBean;
        }

        return upgradeInfo;
    }

    public static boolean checkStbVersion(String stbVersion, String packageVersion, String minVersion, String maxVersion, List<UpgradeInfoBean.Software> softwareSegments)
    {
        long stbSoft = parseSoftware(stbVersion);
        long minSoft = parseSoftware(minVersion);
        long maxSoft = parseSoftware(maxVersion);

        if (LOG_EN) Log.v(TAG, "softwareSegments.size(): " + softwareSegments.size());
        if (LOG_EN) Log.v(TAG, "check stb software version, local stbSoft: " + stbSoft);
        for (UpgradeInfoBean.Software software : softwareSegments)
        {
            long remoteMinSoft = parseSoftware(software.getMin());
            long remoteMaxSoft = parseSoftware(software.getMax());

            if (LOG_EN) Log.v(TAG, "check stb software version, remoteMinSof: "+remoteMinSoft);
            if (LOG_EN) Log.v(TAG, "check stb software version, remoteMaxSoft:  "+remoteMaxSoft);

            if (remoteMinSoft < minSoft || remoteMaxSoft > maxSoft) {
                if (LOG_EN) Log.e(TAG, "software segment is out of the limited");
                return false;
            }
        }

        for (UpgradeInfoBean.Software software : softwareSegments)
        {
            long remoteMinSoft = parseSoftware(software.getMin());
            long remoteMaxSoft = parseSoftware(software.getMax());

            if (LOG_EN) Log.v(TAG, "check stb software version, remoteMinSoft2: "+remoteMinSoft);
            if (LOG_EN) Log.v(TAG, "check stb software version, remoteMaxSoft2:"+remoteMaxSoft);

            if (stbSoft > remoteMinSoft && stbSoft < remoteMaxSoft) {
                if (LOG_EN) Log.v(TAG, "packageVersion:"+packageVersion);
                if (parseSoftware(packageVersion) > stbSoft) {
                    return true;
                }
            }
        }

        return false;
    }

    public static long parseSoftware(String origSoft)
    {
        if (LOG_EN) Log.v(TAG, "parseSoftware origSoft:"+origSoft);
        long result;
        String[] parts = origSoft.split("\\.");
        String trimSoft = "";
        for (String part : parts) {
            trimSoft += part;
        }
        if (LOG_EN) Log.v(TAG, "parseSoftware trimSoft:"+trimSoft);
        if (!StringUtils.isBlank(trimSoft)) {
            result = Long.parseLong(trimSoft, 16);
        } else {
            result = 0;
        }
        return result;
    }

    public static boolean checkStbSerial(String stbSerial, List<UpgradeInfoBean.Serial> serialSegments)
    {
        boolean greater = false;
        boolean less = false;

        if (StringUtils.isBlank(stbSerial)) {
            return true;
        }

        if (LOG_EN) {
            Log.v(TAG, "stbSerial: " + stbSerial);

            for (UpgradeInfoBean.Serial serial : serialSegments) {
                Log.v(TAG, "serial from: "+serial.getSerialFrom());
                Log.v(TAG, "serial to: "+serial.getSerialTo());
            }
        }

        int[] stbSerialInts = parseSerial(stbSerial);

        for (UpgradeInfoBean.Serial serial : serialSegments) {
            int[] fromSerialInts = parseSerial(serial.getSerialFrom());
            int[] toSerialInts = parseSerial(serial.getSerialTo());
            int serialLength = stbSerial.length();
            for (int i = 0; i < serialLength; i++) {
                if (stbSerialInts[i] > fromSerialInts[i]) {
                    if (LOG_EN) Log.v(TAG, "stb serial greater than fromSerial: yes");
                    greater = true;
                    break;
                } else if (stbSerialInts[i] < fromSerialInts[i]){
                    if (LOG_EN) Log.v(TAG, "stb serial greater than fromSerial: no");
                    greater = false;
                    break;
                }
                if (stbSerialInts.length - 1 == i) {
                    if (LOG_EN) Log.v(TAG, "stb serial equal fromSerial");
                    greater = true;
                }
            }

            for (int i = 0; i < toSerialInts.length; i++) {
                if (stbSerialInts[i] < toSerialInts[i]) {
                    if (LOG_EN) Log.v(TAG, "stb serial less than toSerial: yes");
                    less = true;
                    break;
                } else if (stbSerialInts[i] > toSerialInts[i]) {
                    if (LOG_EN) Log.v(TAG, "stb serial less than toSerial: no");
                    less = false;
                    break;
                }
                if (stbSerialInts.length - 1 == i) {
                    if (LOG_EN) Log.v(TAG, "stb serial equal toSerial");
                    less = true;
                }
            }

            if (greater && less)
            {
                if (LOG_EN) Log.v(TAG, "stb serial match the limited");
                return true;
            }
        }

        if (LOG_EN) Log.v(TAG, "stb serial does not match the limited");
        return false;
    }

    public static int[] parseSerial(String serial)
    {
        int[] result = new int[serial.length()];
        for (int i = 0; i < serial.length(); i++) {
            result[i] = Integer.parseInt(serial.substring(i, i + 1));
        }

        return result;
    }

    public static boolean checkApk(Context context, String filePath, boolean diffVerInstall) {
        PackageInfoUtil.AppInfo downedAppInfo = PackageInfoUtil.getUnstalledApp(context, filePath);
        List<PackageInfoUtil.AppInfo> installedAppsInfo = PackageInfoUtil.getInstalledApps(context);

        for (PackageInfoUtil.AppInfo appInfo : installedAppsInfo) {
            if (downedAppInfo.getPackageName().equals(appInfo.getPackageName())) {
                Log.v(TAG, "*************************************************************");
                if (LOG_EN) Log.v(TAG, "应用已安装，包名: "+downedAppInfo.getPackageName());
                if (LOG_EN) Log.v(TAG, "应用名称："+downedAppInfo.getPackageLabel());
                if (diffVerInstall) {
                    if (downedAppInfo.getVerCode() != appInfo.getVerCode()) {
                        if (LOG_EN) Log.v(TAG, "应用有更新版本");
                        if (LOG_EN) Log.v(TAG, "新版本软件版本名称："+downedAppInfo.getPackageLabel());
                        if (LOG_EN) Log.v(TAG, "新版本软件版本号: "+downedAppInfo.getVerCode());
                        if (LOG_EN) Log.v(TAG, "旧版本软件版本名称："+appInfo.getPackageLabel());
                        if (LOG_EN) Log.v(TAG, "旧版本软件版本号: "+appInfo.getVerCode());
                        Log.v(TAG, "*************************************************************");
                        return true;
                    }
                } else {
                    if (downedAppInfo.getVerCode() > appInfo.getVerCode()) {
                        if (LOG_EN) Log.v(TAG, "应用有更新版本");
                        if (LOG_EN) Log.v(TAG, "新版本软件版本名称："+downedAppInfo.getPackageLabel());
                        if (LOG_EN) Log.v(TAG, "新版本软件版本号: "+downedAppInfo.getVerCode());
                        if (LOG_EN) Log.v(TAG, "旧版本软件版本名称："+appInfo.getPackageLabel());
                        if (LOG_EN) Log.v(TAG, "旧版本软件版本号: "+appInfo.getVerCode());
                        Log.v(TAG, "*************************************************************");
                        return true;
                    }
                }

                if (LOG_EN) Log.v(TAG, "当前应用已是最新版本");
                if (LOG_EN) Log.v(TAG, "当前软件版本号: "+appInfo.getVerCode());
                if (LOG_EN) Log.v(TAG, "当前版本软件版本号: "+appInfo.getVerCode());
                Log.v(TAG, "*************************************************************");
                return false;
            }
        }

        Log.v(TAG, "*************************************************************");
        if (LOG_EN) Log.v(TAG, "应用未安装，包名: "+downedAppInfo.getPackageName());
        if (LOG_EN) Log.v(TAG, "应用名称："+downedAppInfo.getPackageLabel());
        if (LOG_EN) Log.v(TAG, "apk的软件版本号: "+downedAppInfo.getVerCode());
        Log.v(TAG, "*************************************************************");
        return true;
    }

    public static int[] checkApks(Context context, UpgradeInfoBean upgradeInfoBean, boolean difVerInstall) {
        List<UpgradeInfoBean.Packet> packets = upgradeInfoBean.getPacketList();
        String type = null;
        String url = null;
        String packageName = null;
        int verCode = 0;

        int length = upgradeInfoBean.getPacketList().size();
        int[] result =  new int[length];
        int j = 0;

        for (int i = 0; i < length; i++) {
            result[i] = -1;
        }

        for (int i = 0; i < length; i++) {
            if (!packets.get(i).getPacketType().equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
                continue;
            }
            type = packets.get(i).getPacketType();
            url = packets.get(i).getPacketUrl();
            packageName = packets.get(i).getPackageName();
            verCode = Integer.parseInt(packets.get(i).getPackageVersion());

            if (!url.endsWith(type)) {
                if (LOG_EN) Log.v(TAG, "Config.xml指定的升级文件类型与实际文件类型不一致");
                return null;
            }

            if (checkApk(context, packageName, verCode, difVerInstall)) {
                result[j++] = i;
                if (LOG_EN) Log.v(TAG, "应用【"+(i+1)+"】"+"有更新");
            }
        }

        return (result[0] >= 0) ? result : null;
    }

    public static boolean checkApk(Context context, String packageName, int verCode, boolean difVerInstall) {
        List<PackageInfoUtil.AppInfo> installedAppsInfo = PackageInfoUtil.getInstalledApps(context);

        for (PackageInfoUtil.AppInfo appInfo : installedAppsInfo) {
            if (appInfo.getPackageName().equals(packageName)) {
                Log.v(TAG, "*************************************************************");
                if (LOG_EN) Log.v(TAG, "应用已安装，包名: " + appInfo.getPackageName());
                if (difVerInstall) {
                    if (verCode != appInfo.getVerCode()) {
                        if (LOG_EN) Log.v(TAG, "应用有更新版本");
                        if (LOG_EN) Log.v(TAG, "新版本软件版本号: " + verCode);
                        if (LOG_EN) Log.v(TAG, "旧版本软件版本号: "+appInfo.getVerCode());
                        Log.v(TAG, "*************************************************************");
                        return true;
                    }
                } else {
                    if (verCode > appInfo.getVerCode()) {
                        if (LOG_EN) Log.v(TAG, "应用有更新版本");
                        if (LOG_EN) Log.v(TAG, "新版本软件版本号: " + verCode);
                        if (LOG_EN) Log.v(TAG, "旧版本软件版本号: "+appInfo.getVerCode());
                        Log.v(TAG, "*************************************************************");
                        return true;
                    }
                }

                if (LOG_EN) Log.v(TAG, "当前应用已是最新版本");
                if (LOG_EN) Log.v(TAG, "当前软件版本号: "+appInfo.getVerCode());
                Log.v(TAG, "*************************************************************");
                return false;

                /*
                if (verCode <= appInfo.getVerCode()) {
                    if (LOG_EN) Log.v(TAG, "当前应用已是最新版本");
                    if (LOG_EN) Log.v(TAG, "当前软件版本号: "+appInfo.getVerCode());
                    Log.v(TAG, "*************************************************************");
                    return false;
                } else {
                    if (LOG_EN) Log.v(TAG, "应用有更新版本");
                    if (LOG_EN) Log.v(TAG, "新版本软件版本号: " + verCode);
                    if (LOG_EN) Log.v(TAG, "旧版本软件版本号: "+appInfo.getVerCode());
                    Log.v(TAG, "*************************************************************");
                    return true;
                }
                */
            }
        }
        Log.v(TAG, "*************************************************************");
        if (LOG_EN) Log.v(TAG, "应用未安装，包名: "+packageName);
        if (LOG_EN) Log.v(TAG, "apk的软件版本号: "+verCode);
        Log.v(TAG, "*************************************************************");
        return true;
    }
}
