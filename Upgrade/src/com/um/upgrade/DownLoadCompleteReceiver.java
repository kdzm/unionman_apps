package com.um.upgrade;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.um.upgrade.base.MyApp;
import com.um.upgrade.data.DeviceInfo;
import com.um.upgrade.data.UpgradeInfoBean;
import com.um.upgrade.util.DownloadManagerPro;
import com.um.upgrade.util.LogUtils;
import com.um.upgrade.util.PreferencesUtils;
import com.um.upgrade.util.UpgradeUtil;

import java.io.File;

/**
 * Created by hjian on 2015/3/13.
 */
public class DownLoadCompleteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = PreferencesUtils.getLong(context, DefaultParameter.KEY_NAME_DOWNLOAD_ID, 0);
        /**
         * get the id of download which have download success, if the id is my id and it's status is successful,
         * then install it
         **/
        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        LogUtils.d("completeDownloadId: " + completeDownloadId + ", downloadId: " + downloadId);
        if (completeDownloadId == downloadId) {
            // if download successful, install it
            DownloadManagerPro downloadManagerPro = new DownloadManagerPro(
                    (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE));
            if (downloadManagerPro.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                String filePath = DefaultParameter.DOWNLOAD_FOLDER_NAME + File.separator + DefaultParameter.DOWNLOAD_FILE_NAME;
                DeviceInfo deviceInfo = MyApp.getDeviceInfo();
                String description = PreferencesUtils.getString(context, DefaultParameter.KEY_NAME_UPGRADE_DESCRIPTION, "");
                String fileType = PreferencesUtils.getString(context, DefaultParameter.KEY_NAME_UPGRADE_FILE_TYPE, "");
                String upgradeMode = PreferencesUtils.getString(context, DefaultParameter.KEY_NAME_UPGRADE_MODE, "");
                String upgradeVersion = PreferencesUtils.getString(context, DefaultParameter.KEY_NAME_UPGRADE_SOFTWARE_VERSION, "");
                String deviceVersion = deviceInfo.getSoftwareVersion();

                if (upgradeMode.equals(UpgradeInfoBean.RECOVERY_PARTITION_MODE)) {
                    UpgradeUtil.startRecoveryUpgrade(filePath, DefaultParameter.STB_UNZIP_RECOVERY_DIR,
                            DefaultParameter.STB_RECOVERY_PARTITION, DefaultParameter.STB_RECOVERY_IMG_NAME,
                            DefaultParameter.STB_LODAERDB_PARTITION, DefaultParameter.STB_LOADERDB_IMG_NAME);
                } else if (fileType.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
                    UpgradeUtil.startZipUpgradeActivity(upgradeMode, filePath, fileType, deviceVersion, description, upgradeVersion);
                }
            }
        }
    }
}
