package com.cvte.tv.at.api.tvapi;

import android.content.Context;
import android.content.Intent;

import com.cvte.tv.at.api.CommonAPI;
import com.cvte.tv.at.api.SysProp;
import com.cvte.tv.at.util.Utils;
import com.cvte.tv.at.util.Utils.HDCPMAC_E;

import java.io.File;
import java.io.IOException;

/**
 * Factory API Group，API组件
 *
 * @author Leajen_Ren
 * @version V1.0 2014-10-23 首版
 * @Package cvte.factory.api
 * @Description
 */
public class FacAPI {
    // Create a Static API
    private static Context sContext;
    private static FacAPI sfacapi = null;
    private static CommonAPI sComapi = null;
//    private static SerialDevice sSerialdev = null;

    public static FacAPI getInstance(Context sContext) {

        if (sfacapi == null) {
            Utils.LOG("CvteFacAPI.getInstance = " + sContext.getClass());
            sfacapi = new FacAPI(sContext);
            sComapi = CommonAPI.getInstance(getmContext());
        }
        return sfacapi;
    }

    public FacAPI(Context sContext) {
        setmContext(sContext);
    }

    private static Context getmContext() {
        return sContext;
    }

    private void setmContext(Context sContext) {
        FacAPI.sContext = sContext;
    }

    public static void SystemCmd(String Str) {
        SysProp.set("ctl.start", "sys_ctl:" + Str);
        Sleep(300);
    }

    public static void SysCmd(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean StartWifiTest() {
        return sComapi.WIFI_TEST();
    }

    public String CheckWifiTestStatus() {
        return sComapi.CheckWifiTestStatus();
    }

    public boolean StartBluetoothTest() {
        return sComapi.BlueTooth_TEST();
    }

    public String CheckBluetoothTestState() {
        return sComapi.CheckBluetoothTestState();
    }

    public static int USBCount() {
        int count = 0;
//        count = sComapi.USBDeviceList().size() - 1;
        if (sComapi == null)
            sComapi = CommonAPI.getInstance(getmContext());
        count = sComapi.FindUBSCountByFileSystem();
        Utils.LOG("get USB ListSize = " + count);
        return count;
    }

    public boolean FileExist(String file) {
        File fp = new File(file);
        return fp.exists();
    }

    public String USBStatus() {

        String devstr = "";

        int devlist[] = new int[Utils.DevCount];
        //Step1. init to NG
        for (int x = 0; x < Utils.DevCount; x++)
            devlist[x] = Utils.NO_DEVICE;

        devlist[0] = FileExist(Utils.USB_DEVICES_USB20) ? Utils.USB20_OK : Utils.USB20_NG;
        devlist[1] = FileExist(Utils.USB_DEVICES_BT) ? Utils.USB20_OK : Utils.USB20_NG;
        devlist[2] = Utils.USB30_NG;
        if (FileExist(Utils.USB_DEVICES_USB30_30))
            devlist[2] = Utils.USB30_OK;
        else {
            if (FileExist(Utils.USB_DEVICES_USB30_20))
                devlist[2] = Utils.USB20_OK;
        }

        String rec = ArrayInt2String(devlist);
        Utils.LOG("get USBStatus = " + rec);
        return rec;
    }

    private String RelBool(boolean stat) {
        return (stat ? "1" : "0") + Utils.RelBool;
    }

    private String RelOnlyOne(String str) {
        return str + Utils.RelOnlyOne;
    }

    private String RelSameDef(String str) {
        return str + Utils.RelSameDef;
    }

    public String SendCmd(int cmdtyppe_global, String cmdstr_global) {
        String str = Utils.NoFun;
        return str;
    }

    private void SendKeycode(int key) {
        Intent intent = new Intent(Utils.CUS_BROADCAST_FACKEYCODE_FLAG);
        intent.putExtra(Utils.CUS_BROADCAST_FACKEYCODE_FLAG, key);
        getmContext().sendBroadcast(intent);
    }

    public static String GetCheckSum() {
        String val = SysProp.get(Utils.Persist_BUILD_VERSION, "no verison");
        Utils.LOG("<CVTE-AT> GetCheckSum=" + val);
        return val;
    }

    private static int val = 0;

    public boolean CheckUsbIsExist() {
        return (sComapi.FindUBSCountByFileSystem() > 0);
    }

    public byte[] CatchData(String path) {
        return sComapi.CatchData(path);
    }

    public String FindBurnFilePath(HDCPMAC_E type) {

        String FolderName = "";
        String Headname = "";
        switch (type) {
            case Option_HDCPKey:
                FolderName = Utils.HDCP_FOLDER_NAME;
                Headname = Utils.HDCP_FILE_HEAD;
                break;
            case Option_HDCPKey20G:
                FolderName = Utils.HDCP2_FOLDER_NAME;
                Headname = Utils.HDCP2_FILE_HEAD;
                break;
            case Option_MACAddr:
                FolderName = Utils.MAC_FOLDER_NAME;
                Headname = Utils.MAC_FILE_HEAD;
                break;
            default:
                return Utils.Nofile;
        }

        //Step1. Find Folder
        File usbroot = new File(Utils.USB_DEVICES_PATH);
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            Utils.LOG("FindATFilePath usbitems.size = " + usbitems.length);
            for (int i = 0; i < usbitems.length; i++) {
//                Utils.LOG("FindATFilePath getPath = " + usbitems[i].getPath() + "/" + Utils.HISI_ATV_TABLE_NAME);
//                Utils.LOG("FindATFilePath canExecute = " + usbitems[i].canExecute() + " canRead=" + usbitems[i].canRead() + " canWrite=" + usbitems[i].canWrite());
                if (FileExit(usbitems[i])) {
                    String getFolderPath = usbitems[i].getPath() + "/" + FolderName;//get folder name
                    Utils.LOG("getFolderPath = " + getFolderPath);
                    File folderpath = new File(getFolderPath);

                    if ((folderpath != null) && (folderpath.exists())) {
                        File[] mutilsfile = folderpath.listFiles();
                        Utils.LOG("File Count = " + mutilsfile.length);

                        for (int x = 0; x < mutilsfile.length; x++) {
                            if (/*FileExit(mutilsfile[x])*/true) {
                                String getFilePath = mutilsfile[x].getPath();
                                String Name = mutilsfile[x].getName();
                                Utils.LOG("getBurnFilePath = " + getFilePath + " HeadName = " + Name);
                                if (Name.startsWith(Headname)) {
                                    Utils.LOG("final return = " + getFilePath);
                                    return getFilePath;
                                }
                            }
                        }
                    }
                }
            }
        }
        return Utils.Nofile;
    }

    private boolean FileExit(File f) {
        return ((f.canExecute() == true) && (f.canRead() == true) && (f.canWrite() == true));
    }

    public int FindKeyCountOnUSB(String foldername) {
        int number = Utils.nodata;
        File usbroot = new File(Utils.USB_DEVICES_PATH);
        File targetfile;

        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            int sdx = 0;
            for (; sdx < usbitems.length; sdx++) {
                if (usbitems[sdx].isDirectory() && FileExit(usbitems[sdx])) {
                    targetfile = new File(usbitems[sdx].getPath() + "/" + foldername);
                    if (!targetfile.exists())
                        return Utils.nodata;
                    number = targetfile.listFiles().length;
                    break;
                }
            }
        }
        Utils.LOG("FindKeyCountOnUSB Count = " + number);
        return number;
    }

    private static int[] String2ArrayInt(String str) {
        if (str != null) {
            char cSNCode[] = str.toCharArray();
            int len = cSNCode.length;
            int iSNCode[] = new int[len];
            for (int i = 0; i < len; i++) {
                iSNCode[i] = (int) cSNCode[i];
            }
            return iSNCode;
        }
        int acknull[] = {};
        return acknull;
    }

    private static String ArrayInt2String(int[] data) {
        if (data != null) {
            int len = data.length;
            char cSNCode[] = new char[len];
            for (int i = 0; i < len; i++) {
                cSNCode[i] = (char) data[i];
            }
            return new String(cSNCode);
        }
        return "";
    }

    private boolean ReCreateFile(File f) {
        try {
            if (f.exists())
                f.delete();
            f.createNewFile();
            if (!f.exists()) {
                Utils.LOG("<TvApi-API> file can't create");
                return false;
            }
        } catch (IOException e) {
            Utils.LOG("<TvApi-API> ReCreateFile fail:" + e.toString());
            e.printStackTrace();
        }
        return true;
    }

}
