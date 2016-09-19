package com.cvte.tv.at.api;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.cvte.tv.at.util.Utils;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 7366M6 UART AT Common API for Android System Code
 *
 * @author Leajen_Ren
 * @version V1.1 2014-06-24 更新了变量命名
 * @Package com.cvte.tv.at.api.mtk.api
 * @Description Common API限定用Android原生代码是实现API， 不允许有任何平台方案级的API出现，保证代码和功能的纯洁性。
 * 这样的移植的时候这份文件可以不修改就能用。
 */
public class CommonAPI {

    private static CommonAPI instance;
    private static Context sContext;

    private CommonAPI(Context context) {
        setsContext(context);
    }

    public static CommonAPI getInstance(Context context) {
        if (instance == null) {
            instance = new CommonAPI(context);
        }
        return instance;
    }

    public Context getsContext() {
        return sContext;
    }

    public void setsContext(Context context) {
        CommonAPI.sContext = context;
    }


    private static List<String> sArrayAdapter = new ArrayList<String>();
    private static ArrayList<ScanResult> sWifiScanResultList = new ArrayList<ScanResult>();

    private static WifiManager sWifiManager = null;

    private static final int WIFI_SCAN_START = 100;
    private static final int WIFI_SCAN_SUCCESS = 101;
    private static final int WIFI_SCAN_FAILURE = 102;
    private static final int WIFI_SCAN_BREAK_LOOP = 103;
    private static final int BT_SCAN_START = 104;

    private static final int BT_DETECT_TIME = 8000;
    private static final int WIFI_SCAN_BREAK_LOOP_TIME = 9000;

    private static boolean breakloopflag = false;
    private static String wifistatus = Utils.FAIL;
    private static String bluetoothstatus = Utils.FAIL;

    private static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_SCAN_START:
                    Utils.LOG("WIFITestStart-WIFI_SCAN_START");
                    // before scan wifi we must turn on wifi first
                    wifistatus = Utils.DOING;
                    WifiStatus(Utils.DOING);
                    new WifiScanThread().start();
                    break;
                case WIFI_SCAN_SUCCESS:
                    Utils.LOG("WIFITestStart-WIFI_SCAN_SUCCESS");
                    WIFI_OFF();
                    break;
                case WIFI_SCAN_FAILURE:
                    Utils.LOG("WIFITestStart-WIFI_SCAN_FAILURE");
                    WIFI_OFF();
                    break;
                case WIFI_SCAN_BREAK_LOOP:
                    breakloopflag = true;
                    break;
                case BT_SCAN_START:
                    Utils.LOG("Reset-Bluetooth time");
//                    if (bluetoothstatus.equals(Utils.DOING))
                    BlueToothTestResult(false);
                    break;
            }
        }
    };

    public static void WifiStatus(String stat) {
        try {
            if (stat.equals(Utils.OK))
                new File(Utils.WIFIOK).createNewFile();
            else if (stat.equals(Utils.FAIL))
                new File(Utils.WIFIFail).createNewFile();
            else {
                new File(Utils.WIFIFail).exists();
                new File(Utils.WIFIOK).exists();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String CheckWifiTestStatus() {
        if (new File(Utils.WIFIOK).exists())
            return Utils.OK;
        else if (new File(Utils.WIFIFail).delete())
            return Utils.FAIL;
        else
            return Utils.DOING;
    }

    public String CheckBluetoothTestState() {
        return bluetoothstatus;
    }

    public static String GetTime(String str) {
        long msec = SystemClock.uptimeMillis();
        long sec = msec / 1000;
        long min = sec / 60;

        long sec_show = sec % 60;
        long min_show = min % 60;

        Formatter formatter = new Formatter();
        String Ltime = formatter.format("%02d:%02d",
                min_show, sec_show).toString().toLowerCase();

        Utils.LOG("<AT> Ltime = " + Ltime + " str=" + str);
        return Ltime;
    }

    private static class WifiScanThread extends Thread {
        @Override
        public void run() {
            if (sWifiManager != null) {
                sWifiManager.startScan();
                Utils.LOG("WIFITestStart-WifiScanThread sWifiManager != null");

                if (sWifiScanResultList != null)
                    sWifiScanResultList.clear();

                Utils.LOG("WIFITestStart-WifiScanThread sWifiScanResultList != null");
                breakloopflag = false;
                GetTime("Start Scan");
//                mHandler.sendEmptyMessageDelayed(WIFI_SCAN_BREAK_LOOP, WIFI_SCAN_BREAK_LOOP_TIME);
                Timer wifitime = new Timer();
                wifitime.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Utils.LOG("<CVTE-AT> WifiTest breakloopflag");
                        breakloopflag = true;
                    }
                }, WIFI_SCAN_BREAK_LOOP_TIME);

                while (true) {
                    sWifiScanResultList = (ArrayList<ScanResult>) sWifiManager.getScanResults();
                    if ((sWifiScanResultList.size() > 0) || (breakloopflag)) {
//                        if (mHandler.hasMessages(WIFI_SCAN_BREAK_LOOP))
//                            mHandler.removeMessages(WIFI_SCAN_BREAK_LOOP);
                        wifitime.purge();
                        wifitime.cancel();
                        Utils.LOG("Leave WIFI Test Loop breakloopflag=" + breakloopflag);
                        break;
                    }
                }

                int wifisize = sWifiScanResultList.size();
                Utils.LOG("WIFITestStart-WifiScanThread wifisize=" + wifisize);
                GetTime("Finish Scan");
                if (wifisize == 0) {
                    Utils.LOG("WifiScanThread NULL");
//                    mHandler.sendEmptyMessage(WIFI_SCAN_FAILURE);
                    WIFI_OFF();
                    wifistatus = Utils.FAIL;
                    WifiStatus(Utils.FAIL);
                    try {
                        new File(Utils.WIFIFail).createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Utils.LOG("WifiScanThread OK");
//                    mHandler.sendEmptyMessage(WIFI_SCAN_SUCCESS);
                    WIFI_OFF();
                    wifistatus = Utils.OK;
                    WifiStatus(Utils.OK);
                    try {
                        new File(Utils.WIFIOK).createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Utils.LOG("No WifiManager, Wifi Scan Failure!!!");
//                mHandler.sendEmptyMessage(WIFI_SCAN_FAILURE);
                WIFI_OFF();
                wifistatus = Utils.FAIL;
                WifiStatus(Utils.FAIL);
            }
        }
    }

    private static void WIFI_ON() {
//        SysAPI.Ethernet_SetEnable(false);
        Utils.LOG("WIFI_ON ");
        if (sWifiManager != null)
            sWifiManager.setWifiEnabled(true);
    }

    private static void WIFI_OFF() {
//        SysAPI.Ethernet_SetEnable(true);
        Utils.LOG("WIFI_OFF No Action");
//        if (sWifiManager != null)
//            sWifiManager.setWifiEnabled(false);
    }


    public boolean WIFI_TEST() {
        Utils.LOG("WIFITestStart-Start");
        wifistatus = Utils.DOING;
        WifiStatus(Utils.DOING);

        sWifiManager = (WifiManager) getsContext().getSystemService(Context.WIFI_SERVICE);
        if (sWifiManager == null) {
            Utils.LOG("sWifiManager == nullm return false");
            return false;
        }
//        SysAPI.Ethernet_Init(getsContext());
        WIFI_ON();
//        mHandler.sendEmptyMessage(WIFI_SCAN_START);
        new WifiScanThread().start();
        Utils.LOG("WIFITestStart-2");
        return true;//must set true for start ones
    }


    public boolean BlueTooth_TEST() {
        bluetoothstatus = Utils.DOING;
        sArrayAdapter.clear();
        Utils.LOG("Start Blue Tooth Discovery, wait 8sec");
        mHandler.sendEmptyMessageDelayed(BT_SCAN_START, BT_DETECT_TIME);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        // BluetoothAdapter is null, no devices
        if ((adapter != null)) {
            if (adapter.getName() == null) {
                Utils.LOG("adapter = null, return");
                adapter = null;
                bluetoothstatus = Utils.FAIL;
                if (mHandler.hasMessages(BT_SCAN_START)) {
                    Utils.LOG("removeMessages-BT_SCAN_START");
                    mHandler.removeMessages(BT_SCAN_START);
                }
                return true;//must use true for test start ones
            }
            Utils.LOG("BluetoothAdapter = " + adapter.getName());
            Utils.LOG("Have Local BlueTooth device");
            {
                if (adapter.isDiscovering())
                    adapter.cancelDiscovery();
                Utils.LOG("Open BlueTooth device");
                adapter.enable();
            }
            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            // Don't forget to unregister during onDestroy
            getsContext().getApplicationContext().registerReceiver(mReceiver, filter);
            adapter.startDiscovery();
        }
        return true;
    }

    public static final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context sContext, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a
                // ListView
                sArrayAdapter.add(device.getName() + device.getAddress());
                Utils.LOG("<CVTE-AT> Blue address = " + device.getName() + device.getAddress());
                BlueToothTestResult(sArrayAdapter.isEmpty() ? (false) : (true));
            }
        }
    };

    public static void BlueToothTestResult(boolean rel) {
        bluetoothstatus = rel ? Utils.OK : Utils.FAIL;
        if (mHandler.hasMessages(BT_SCAN_START)) {
            Utils.LOG("removeMessages-BT_SCAN_START");
            mHandler.removeMessages(BT_SCAN_START);
        }
    }

    public void startActivity(Context context, String startpath) {
        Intent intent = new Intent(startpath);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
        }
    }

    public void startActivityNew(Context context, String startpath) {
        Intent intent = new Intent(startpath);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
        }
    }

    public void SendBroadCast(String key) {
        getsContext().sendBroadcast(new Intent(key));
    }

    public int SDCardDetect() {
        File sdcard = new File(Utils.SDCARD_DEVICES_PATH);

        Utils.LOG("sdcard.exists=" + sdcard.exists() +
                " sdcard.canExecute=" + sdcard.canExecute() +
                " sdcard.canRead=" + sdcard.canRead() +
                " sdcard.canWrite=" + sdcard.canWrite() +
                " sdcard.lastModified=" + sdcard.lastModified() +
                " sdcard.isAbsolute=" + sdcard.isAbsolute());
        return (sdcard.exists() && sdcard.canExecute() && sdcard.canRead() && sdcard.canWrite()) ? 1 : 0;
    }

    public int USBBusCount() {
        //Step1 USB-Disk Channel Table
        int sdcard = SDCardDetect();
        File usbbus = new File(Utils.USB_BUS_PATH);
        if (usbbus != null && usbbus.exists()) {
            File[] usbbusitems = usbbus.listFiles();
            Utils.LOG("usbbusitems.size = " + usbbusitems.length);
            return usbbusitems.length + sdcard;
        }
        return 0 + sdcard;
    }

    public List<String> USBDeviceList() {

        UsbManager sUsbManager = (UsbManager) getsContext().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = sUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice device = null;
        List<String> data = new ArrayList<String>();
        while (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            data.add("Name:" + device.getDeviceName());
            Utils.LOG("<CVTE-AT> get USB Name: = " + device.getDeviceName());
        }
        return data;
    }

    public int FindUBSCountByFileSystem() {
        File usbroot = new File(Utils.USB_DEVICES_PATH);
        int count = 0;
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            for (int i = 0; i < usbitems.length; i++) {

                if ((usbitems[i].canExecute() == true) &&
                        (usbitems[i].canRead() == true) &&
                        (usbitems[i].canWrite() == true)) {
                    Utils.LOG("FindUBSCountByFileSystem getPath = " + usbitems[i].getPath() +
                            " canExecute = " + usbitems[i].canExecute() +
                            " canRead=" + usbitems[i].canRead() +
                            " canWrite=" + usbitems[i].canWrite());

                    if (Utils.USB_DEVICES_PASS_INTERSDCARD.equals(usbitems[i].getPath()) ||
                            Utils.USB_DEVICES_PASS_SDCARD.equals(usbitems[i].getPath()) ||
                            Utils.USB_DEVICES_PASS_tmp.equals(usbitems[i].getPath())) {
                        Utils.LOG("FindUBSCountByFileSystem = continue");
                        continue;
                    }
                    count++;
                }
            }
        }
        Utils.LOG("FindUBSCountByFileSystem = " + count);
        return count;
    }


    /**
     * 从二进制文件读取字节数组
     *
     * @param sourceFile
     * @return
     * @throws IOException
     */
    public byte[] readBytes(File sourceFile) {

        long fileLength = sourceFile.length();
        if (fileLength > 0) {
            try {
                BufferedInputStream fis = new BufferedInputStream(
                        new FileInputStream(sourceFile));
                byte[] b = new byte[(int) fileLength];

                while (fis.read(b) != -1) {
                }

                fis.close();
                fis = null;

                return b;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 将字节数组读入二进制文件
     *
     * @param targetFile
     * @param content
     * @return
     */
    public boolean writeBytes(File targetFile, byte[] content) {

        try {
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(targetFile));

            for (int i = 0; i < content.length - 1; i++) {
                fos.write(content[i]);
            }

            fos.write(content[content.length - 1]); // 写入最后一个字节

            fos.flush();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public byte[] CatchData(String path) {
        File data = new File(path);
        if (data.exists()) {
            byte[] ByteData = readBytes(data);
            int datalen = ByteData.length;
            Utils.LOG("CatchData buffer.length = " + datalen);
            return ByteData;
        } else
            return null;
    }

    public String GetEthernetIP(Context ctx) {
        String IP = getLocalIpAddressIPV4();
        if ((IP == null) || (IP.equals(null)) || (IP == "") || (IP.equals("")))
            IP = "0.0.0.0";
        Utils.LOG("<IP> getLocalIpAddressIPV4 = " + IP);
        return IP;
    }

    public String getLocalIpAddressIPV4() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                String netname = ni.getName();
                Utils.LOG("<IP> getLocalIpAddressIPV4 netname= " + netname);
                if (netname.equals("eth0")) {
                    Utils.LOG("<IP> getLocalIpAddressIPV4 = eth0");
                    ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                    for (InetAddress address : ialist) {
                        if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())) {
                            return ipv4;
                        }
                    }
                }
            }

        } catch (SocketException ex) {
            Utils.LOG(ex.toString());
        }
        return "0.0.0.0";
    }

    private static void writeStringToFile(String text, String path) {
        FileOutputStream sysFile;
        try {
            sysFile = new FileOutputStream(path);
            sysFile.write(text.getBytes(), 0, text.length());
            sysFile.close();
        } catch (Exception e) {
            Log.e("writeSysFile", "Can not write sys file!" + e);
        }
    }

    private static String getStringFromFile(String str) {
        File file = new File(str);

        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file), "gbk");
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            if ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void ThreadSleep(int val) {
        try {
            Thread.sleep(val);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ShowKeyData(int[] key) {
        int i = 0, temp = 0;
        int len = key.length;
        String str = "";
        for (i = 0; i < len; i++) {
            temp = key[i] & 0xff;
            if (temp >= 0x10)
                str += Integer.toHexString(temp) + " ";
            else
                str += "0" + Integer.toHexString(temp) + " ";
            if (i % 16 == 15) {
                Utils.LOG(str);
                str = "";
            }

            if (i == (len - 1))
                Utils.LOG(str);
        }
    }
}
