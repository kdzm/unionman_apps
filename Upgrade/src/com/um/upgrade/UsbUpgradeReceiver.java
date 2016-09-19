package com.um.upgrade;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.util.Log;

import com.um.upgrade.base.MyApp;
import com.um.upgrade.data.DeviceInfo;
import com.um.upgrade.util.CheckUtil;
import com.unionman.SystemUpgrade.UpgradeSocketClient;

public class UsbUpgradeReceiver extends BroadcastReceiver {
	private final String TAG = UsbUpgradeReceiver.class.getSimpleName();
	private final boolean LOGE = true;

	private final String UPGRADE_ZIP_FILE = "update.zip";
    private final String UPGRADE_FLAG_FILE = "update.txt";
	private final String UPGRADE_INFO_FILE = "META-INF/com/google/android/update-info";
    private final String UPGRADE_DEFAULT_MOUNTED_PATH = "/mnt/sda/sda1/";

	private Context mContext = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (LOGE) Log.v(TAG, "UsbUpgradeReceiver is called,intent action = " + intent.getAction());
		mContext = context;

		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
            /*获取存在正确升级文件的路径*/
			String path = getUpgradeFilePath();
            if(LOGE) Log.v(TAG, "mounted path: "+path);

            if (path == null) {
                /*如果不存在正确的升级文件路径，到默认的目录检测再检测一次*/
                if (LOGE) Log.v(TAG, "start to check default zip file path...");
                boolean result = false;
                if (result = checkUpgrade(UPGRADE_DEFAULT_MOUNTED_PATH)) {
                    startUpgrade(context, UPGRADE_DEFAULT_MOUNTED_PATH);
                }
                if (LOGE) Log.v(TAG, "check default mounted: "+UPGRADE_DEFAULT_MOUNTED_PATH+" is "+result);
            } else {
                /*获得正确升级文件的路径，直接升级*/
                if (LOGE) Log.v(TAG, "start recovery,reboot system!");
                startUpgrade(context, UPGRADE_DEFAULT_MOUNTED_PATH);
            }
            if (LOGE) Log.v(TAG, "upgrade check progress is over!");
		}
	}

    /**
     * 查找存在升级文件的挂载设备目录
     * @return
     * 存在升级文件的挂载设备目录，如果返回null则没有发存在升级文件挂载设备目录
     */
	private String getUpgradeFilePath() {
		try {
			IBinder service = ServiceManager.getService("mount");
			if (service != null) {
				IMountService mountService = IMountService.Stub
						.asInterface(service);
				List<android.os.storage.ExtraInfo> mountList = mountService
						.getAllExtraInfos();
				int deviceCount = mountList.size();
				for (int i = 0; i < deviceCount; i++) {
                    if(LOGE) Log.v(TAG, "mounted point: "+mountList.get(i).mMountPoint);
					if (checkUpgrade(mountList.get(i).mMountPoint)) {
						return mountList.get(i).mMountPoint;
					}
				}
			}
		} catch (RemoteException e) {
            e.printStackTrace();
            return null;
		}

		return null;
	}

    /**
     * 检测指定目录中的升级文件是否存在
     * @param path
     * @return
     */
	private boolean zipFileExists(String path) {
		File file = new File(path + "/" + UPGRADE_ZIP_FILE);
		if (LOGE) Log.v(TAG, "zip file path: " + file.getAbsolutePath()
				+ ",file.exists() = " + file.exists());

		try {
			if (file.exists()) {
                return true;
			}
		} catch (Exception e) {
            e.printStackTrace();
			return false;
		}

		return false;
	}

    /**
     * 检查升级文件是否存在并比较升级信息
     * @param mountedPath
     * @return
     */
    private boolean checkUpgrade(String mountedPath) {
        boolean zipFileExists = zipFileExists(mountedPath);
        String zipFilePath = mountedPath+"/"+UPGRADE_ZIP_FILE;
        String flagFilePath = mountedPath+"/"+UPGRADE_FLAG_FILE;
        if (zipFileExists) {
            return checkUpgradeInfo(zipFilePath, flagFilePath);
        } else {
            return false;
        }
    }

    /**
     * 比较软件版本号
     * @param stbVersion
     * @param newVersion
     * @return
     */
    private boolean checkSoftwareVer(String stbVersion, String newVersion)
    {

        long stbVer = CheckUtil.parseSoftware(stbVersion);
        long newVer = CheckUtil.parseSoftware(newVersion);

        if (LOGE) Log.v(TAG, "newSoftwareVersionInt = " + stbVer);
        if (LOGE) Log.v(TAG, "curSoftwareVersionInt = " + newVer);

        return stbVer < newVer;
    }

    /**
     * 比较软硬件版本号等升级信息
     * @param zipFilePath
     * @param flagFilePath
     * @return
     */
    private boolean checkUpgradeInfo(String zipFilePath, String flagFilePath) {
        DeviceInfo deviceInfo = MyApp.getDeviceInfo();
        File zipFile  = new File(zipFilePath);
        File flagFile = new File(flagFilePath);

        try {
            ZipFile zipFile1 = new ZipFile(zipFilePath);
            ZipEntry zipEntry = zipFile1.getEntry(UPGRADE_INFO_FILE);
            if (zipEntry != null) {
                InputStream inputStream = zipFile1.getInputStream(zipEntry);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                Map<String, String> map = new HashMap<String, String>();

                String line = br.readLine();
                while (line != null) {
                    String[] items = line.split("=");
                    if (items.length == 2) {
                        map.put(items[0], items[1]);
                    }
                    line = br.readLine();
                }

                br.close();

                String productModel = deviceInfo.getProduceModel();
                String pm = map.get("Product-Model");

                Log.v(TAG, "productModel = " + productModel + ", pm = " + pm);
                if (flagFile.exists()) {
                    if (LOGE)
                        Log.v(TAG, "flagFile.exists() = " + flagFile.exists() + ", ignore to check product model");
                } else {
                    if (LOGE)
                        Log.v(TAG, "flagFile.exists() = " + flagFile.exists() + ", start to check product model");
                    if (pm == null || (!pm.equals(productModel))) {
                        return false;
                    }
                }

                String vd = map.get("Vendor");
                if (vd == null || (!vd.equals(DefaultParameter.STB_VENDOR))) {
                    return false;
                }

                String curHardwareVer = null;
                String curSoftwareVer = null;
                curHardwareVer = deviceInfo.getHardwareVersion();
                curSoftwareVer = deviceInfo.getSoftwareVersion();

                if (LOGE) Log.v(TAG, "curHardwareVer = " + curHardwareVer);
                if (LOGE) Log.v(TAG, "curSoftwareVer = " + curSoftwareVer);

                String hv = map.get("Hardware-Version");
                if (hv == null || (!hv.equals(curHardwareVer))) {
                    return false;
                }

                String sv = map.get("Software-Version");
                if (sv == null) {
                    return false;
                }

                if (LOGE) Log.v(TAG, "newSoftwareVersion = " + sv);
                if (LOGE) Log.v(TAG, "curSoftwareVersion = " + curSoftwareVer);
                if (checkSoftwareVer(curSoftwareVer, sv)) {
                    zipFile1.close();
                    return true;
                }
            }

            zipFile1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 启动Recovery，进行升级
     * @param context
     * @param mountPoint
     */
	private void startUpgrade(Context context, String mountPoint) {
		UpgradeSocketClient socketClient = null;
        socketClient = new UpgradeSocketClient();
        socketClient.writeMess("upgrade " + mountPoint);
        socketClient.readNetResponseSync();
        Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
        intent.putExtra("mount_point", mountPoint);
        context.sendBroadcast(intent);
	}
}
