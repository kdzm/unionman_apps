package com.um.upgrade.util;

import android.content.Context;
import android.content.Intent;

import com.um.upgrade.DefaultParameter;
import com.um.upgrade.NetworkForceUpgradePromptActivity;
import com.um.upgrade.NetworkUpgradePromptActivity;
import com.unionman.SystemUpgrade.Upgrade;
import com.um.upgrade.RecoveryUpgradeActivity;
import com.um.upgrade.RecoveryUpgrader;
import com.um.upgrade.data.UpgradeInfoBean;
import com.um.upgrade.base.ExitAppUtil;
import com.um.upgrade.base.MyApp;
import com.um.upgrade.data.DeviceInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziliang.nong on 14-6-18.
 */
public class UpgradeUtil {
    private UpgradeUtil() {
    }

    public static void systemRecovery(Context context, String filePath) {
        new Upgrade(context).start(filePath);
    }

    public static void apkInstall(Context context, String filePath) {
        RuntimeCmdUtil.chmod("777", filePath);
        PackageInstaller installer = new PackageInstaller(context);
        installer.instatllBatch(filePath);
    }

    public static void upgradeDevPart(Context context, String filePath, int partType) {
        new Upgrade(context).startDevPart(filePath, partType);
    }

    /**
     * 启动Recovery分区升级
     * @param srcZipFilePath
     * @param unZipDirPath
     * @param recoveryPartPath
     * @param recoveryImgName
     */
    public static void startRecoveryUpgrade(String srcZipFilePath, String unZipDirPath,
                                      String recoveryPartPath, String recoveryImgName,
                                      String loaderdbPartParh, String loaderdbImgName) {
        MyApp.setKeyDisable();
        Intent intent = new Intent(MyApp.getContext(), RecoveryUpgradeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApp.getContext().startActivity(intent);
        RecoveryUpgrader mRecoveryUpgrader = new RecoveryUpgrader();
        mRecoveryUpgrader.setContext(MyApp.getContext());
        mRecoveryUpgrader.setSrcZipPath(srcZipFilePath);
        mRecoveryUpgrader.setDstUnzipDirPath(unZipDirPath);
        mRecoveryUpgrader.setRecoveryPartPath(recoveryPartPath);
        mRecoveryUpgrader.setRecoveryImgName(recoveryImgName);
        mRecoveryUpgrader.setLoaderdbPartPath(loaderdbPartParh);
        mRecoveryUpgrader.setLoaderdbImgName(loaderdbImgName);
        mRecoveryUpgrader.setUpgradeCompletedListener(new RecoveryUpgrader.OnUpgradeCompletedListener() {
            @Override
            public void onUpgradeCompleted(String srcDirPath, String srcFileName, String dstPartDirPath, String dstPartFilePath) {
                if (dstPartFilePath.contains("recovery")) {
                    MyApp.setKeyEnable();
                    ExitAppUtil.getInstance().exit();  //退出系统升级应用
                }

            }
        });
        mRecoveryUpgrader.upgradeRecovery();
    }

    /**
     * 启动apk升级提示界面
     * @param upgradeMode
     * @param filePaths
     * @param fileType
     * @param verBeforeUpgrade
     * @param description
     * @param verAfterUpgrade
     * @param details
     */
    public static void startApkUpgradeActivity(String upgradeMode, String[] filePaths, String fileType, String verBeforeUpgrade,
                                         String description, String verAfterUpgrade, String[] details) {
        List<String> tmpfilePaths = new ArrayList<String>();
        List<String> tmpDetails = new ArrayList<String>();

        for (int i = 0; i < filePaths.length; i++) {
            if (CheckUtil.checkApk(MyApp.getContext(), filePaths[i], true)) {
                tmpfilePaths.add(filePaths[i]);
                tmpDetails.add(details[i]);
            }
        }

        if (tmpfilePaths.size() <= 0) {

            return;
        }

        int length = tmpfilePaths.size();
        String[] upgradeFilePaths = new String[length];
        String[] upgradeDetails = new String[length];

        for (int i = 0; i < length; i++) {
            upgradeFilePaths[i] = tmpfilePaths.get(i);
            upgradeDetails[i] = tmpDetails.get(i);
        }

        Intent intent = null;
        if (upgradeMode.equalsIgnoreCase(UpgradeInfoBean.MANUAL_MODE)) {
            intent = new Intent(MyApp.getContext(), NetworkUpgradePromptActivity.class);
//        } else if (upgradeMode.equalsIgnoreCase(UpgradeInfoBean.FORCE_MODE)) {
//            intent = new Intent(mContext, NetworkForceUpgradePromptActivity.class);
        } else if (upgradeMode.equalsIgnoreCase(UpgradeInfoBean.SILENT_MODE) || upgradeMode.equalsIgnoreCase(UpgradeInfoBean.FORCE_MODE)) {
            for (int i = 0; i < filePaths.length; i++) {
                UpgradeUtil.apkInstall(MyApp.getContext(), filePaths[i]);
            }
            return;
        } else {
            return;
        }

        intent.putExtra("filePaths", upgradeFilePaths);
        intent.putExtra("fileType", fileType);
        intent.putExtra("verBeforeUpgrade", verBeforeUpgrade);
        intent.putExtra("description", description);
        intent.putExtra("verAfterUpgrade", verAfterUpgrade);
        intent.putExtra("details", upgradeDetails);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApp.getContext().startActivity(intent);
    }

    /**
     * 启动系统升级界面(跟apk升级使用同一个Activity，但是需要绑定的数据类型和个数不一致，所以分在两个方法中)
     * @param upgradeMode
     * @param filePath
     * @param fileType
     * @param verBeforeUpgrade
     * @param description
     * @param verAfterUpgrade
     */
    public static void startZipUpgradeActivity(String upgradeMode, String filePath, String fileType,
                                         String verBeforeUpgrade, String description, String verAfterUpgrade) {

        Intent intent = null;
        if (upgradeMode.equalsIgnoreCase(UpgradeInfoBean.MANUAL_MODE)) {
            intent = new Intent(MyApp.getContext(), NetworkUpgradePromptActivity.class);
        } else if (upgradeMode.equalsIgnoreCase(UpgradeInfoBean.FORCE_MODE)) {
            intent = new Intent(MyApp.getContext(), NetworkForceUpgradePromptActivity.class);
        } else if (upgradeMode.equalsIgnoreCase(UpgradeInfoBean.SILENT_MODE)) {
            UpgradeUtil.systemRecovery(MyApp.getContext(), filePath);
            return;
        } else {
            return;
        }

        intent.putExtra("filePath", filePath);
        intent.putExtra("fileType", fileType);
        intent.putExtra("verBeforeUpgrade", verBeforeUpgrade);
        intent.putExtra("description", description);
        intent.putExtra("verAfterUpgrade", verAfterUpgrade);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApp.getContext().startActivity(intent);
    }

    public static void upgrade(UpgradeInfoBean upgradeInfo, DeviceInfo deviceInfo) {
        List<UpgradeInfoBean.Packet> packets = upgradeInfo.getPacketList();
        String fileUrl = null;
        String fileName = null;
        String filePath = null;
        String detail = null;
        String xmlPackageName = null;
        String fileType = packets.get(0).getPacketType();
        int length = packets.size();
        String[] details = new String[length];
        String[] filePaths = new String[length];
        String[] packageNames = new String[length];

        for (int i = 0; i < length; i++) {
            detail = packets.get(i).getDetail();
            details[i] = detail;

            fileUrl = packets.get(i).getPacketUrl();
            fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            xmlPackageName = packets.get(i).getPackageName();
            if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
                filePath = DefaultParameter.DOWNLOAD_FOLDER_NAME + File.separator + DefaultParameter.DOWNLOAD_FILE_NAME;
            } else if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
                filePath = "/sdcard/"+fileName;
                filePaths[i] = filePath;
                packageNames[i] = xmlPackageName;
            }
        }
        if (upgradeInfo.getUpdateMode().equals(UpgradeInfoBean.RECOVERY_PARTITION_MODE)) {
            UpgradeUtil.startRecoveryUpgrade(filePath, DefaultParameter.STB_UNZIP_RECOVERY_DIR,
                    DefaultParameter.STB_RECOVERY_PARTITION, DefaultParameter.STB_RECOVERY_IMG_NAME,
                    DefaultParameter.STB_LODAERDB_PARTITION, DefaultParameter.STB_LOADERDB_IMG_NAME);
        } else if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
            UpgradeUtil.startZipUpgradeActivity(upgradeInfo.getUpdateMode(),
                    filePath, fileType, deviceInfo.getSoftwareVersion(), upgradeInfo.getDescription(), upgradeInfo.getVersion());
        }
    }
}
