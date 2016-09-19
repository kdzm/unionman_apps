package com.cvte.tv.at.listener;

import android.content.Context;

import com.cvte.tv.at.api.CvteFacAPI;
import com.cvte.tv.at.util.Utils;
import com.cvte.tv.at.util.Utils.UART_DEBUG;

/**
 * UART Device Code Link to JNI Code
 *
 * @author Leajen_Ren
 * @version V1.1 2014-06-24 更新了变量命名
 * @Package com.cvte.tv.at.listener
 * @Description UartDevice的设计理念是，实例化JNI功能，因为UART设备是用C语言写出来的，而需要在JAVA这边实例化，进行例如启动，
 * 关闭，回调等功能。因此这里设计了一个监听器IUartDeviceListener，将控制过程抽象成监听器，
 * 而功能的实现完全不这里实现， 保证在移植过程中最小的代码改动。
 * 因此在这里是没有API实现的，因为实现的地方在监听器实例化的地方实现。
 */
public class UartDevice {
    private static UartDevice instance = null;
    private Context mContext;

    static {
        System.loadLibrary("atserialjni");
    }

    public UartDevice(Context context) {
        Utils.LOG("SerialDevice UartDevice init");
        this.mContext = context;
    }

    public static synchronized UartDevice instance(Context context) {
        Utils.LOG("SerialDevice UartDevice instance");
        if (instance == null) {
            instance = new UartDevice(context);
        }
        return instance;
    }

    static IUartDeviceListener listener;

    public void UartListener(IUartDeviceListener e) {
        listener = e;
    }

    public void StartService(Context context, UART_DEBUG mCusType) {
        Utils.LOG("UartPartJob StartService mCusType define = " + mCusType);
        mainThread(mCusType.ordinal(), CvteFacAPI.getInstance(context).GetSERIAL_DEVICES());
    }

    public void StartService(Context context) {
        Utils.LOG("UartPartJob StartService mCusType default = CVTE");
        mainThread(UART_DEBUG.UART_CVTE.ordinal(), CvteFacAPI.getInstance(context).GetSERIAL_DEVICES());
    }

    public void ATResetFunction() {
        Utils.LOG("UartPartJob ATResetFunction");
        ATReset();
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        Utils.LOG("UartDevice finalize-1");
        ThreadFinish();
        instance = null;
        listener = null;
        Utils.LOG("UartDevice finalize-2");
    }

    public static int[] OnGetTVChecksumListener() {
        return listener.OnGetTVChecksumListener();
    }

    public static int[] OnGetTVFixedChecksumListener() {
        return listener.OnGetTVFixedChecksumListener();
    }


    public static boolean OnSetTVSourceListener(int source) {
        return listener.OnSetTVSourceListener(source);
    }

    public static int OnGetTVSourceListener() {
        return listener.OnGetTVSourceListener();
    }

    public static boolean OnSetVolumeListener(int volume) {
        return listener.OnSetVolumeListener(volume);
    }

    public static boolean OnChangeTVNumListener(int num) {
        return listener.OnChangeTVNumListener(num);
    }

    public static int[] OnGetTVMacAddrListener() {
        return listener.OnGetTVMacAddrListener();
    }

    public static int[] OnGetTVConfigMacAddrListener(int type) {
        return listener.OnGetTVConfigMacAddrListener(type);
    }

    public static long OnGetLongData_IDListener(int type) {
        return listener.OnGetLongData_IDListener(type);
    }

    public static boolean OnSaveLongDataToSystemListener(int type, int[] key, int len, int fileId) {
        return listener.OnSaveLongDataToSystemListener(type, key, len, fileId);
    }

    public static boolean OnSaveMACAddrToTVListener(int mac0, int mac1, int mac2, int mac3, int mac4, int mac5) {
        return listener.OnSaveMACAddrToTVListener(mac0, mac1, mac2, mac3, mac4, mac5);
    }
    public static boolean OnSaveConfigMACAddrToTVListener(int type, int mac0, int mac1, int mac2, int mac3, int mac4, int mac5) {
        return listener.OnSaveConfigMACAddrToTVListener(type, mac0, mac1, mac2, mac3, mac4, mac5);
    }

    public static boolean OnCtrlIOLevelListener(int pin, int level) {
        return listener.OnCtrlIOLevelListener(pin, level);
    }

    public static int OnGetCurrentKeyPadStatusListener() {
        return listener.OnGetCurrentKeyPadStatusListener();
    }

    public static boolean OnSaveBarcodeToFlashListener(int[] barcode) {
        return listener.OnSaveBarcodeToFlashListener(barcode);
    }

    public static int[] OnGetBarcodeFromFlashListener() {
        return listener.OnGetBarcodeFromFlashListener();
    }

    public static int[] OnGetHDCPKSVCodeListener() {
        return listener.OnGetHDCPKSVCodeListener();
    }

    //this function have issue, but I don't know why
    public static boolean OnSetAntennaAirCableListener(int type) {
        return listener.OnSetAntennaAirCableListener(type);
    }

    public static boolean OnSetATSCProNumberListener(int major, int minor) {
        Utils.LOG("OnSetATSCProNumberListener minor = " + minor);
        if (minor == 0xFFF)
            return listener.OnSetAntennaAirCableListener(major);
        else
            return listener.OnSetATSCProNumberListener(major, minor);
    }

    public static int OnGetCIFunctionStatusListener() {
        return listener.OnGetCIFunctionStatusListener();
    }

    public static int[] OnGetIPAddrListener() {
        return listener.OnGetIPAddrListener();
    }

    public static boolean OnGetWifiTestResultStartListener() {
        return listener.OnGetWifiTestResultStartListener();
    }

    public static int OnGetUSBConnectCountListener() {
        return listener.OnGetUSBConnectCountListener();
    }

    public static int[] OnGetUSBConnectStatusListener() {
        return listener.OnGetUSBConnectStatusListener();
    }

    public static boolean OnSendCVTEFACIRKeyListener(int key_code) {
        return listener.OnSendCVTEFACIRKeyListener(key_code);
    }

    public static boolean OnCheckBluetoothStartListener() {
        return listener.OnCheckBluetoothStartListener();
    }

    public static void OnSendCommonJNIToSystemListener(int cmdtype, int[] cmddata) {
        listener.OnSendCommonJNIToSystemListener(cmdtype, cmddata);
    }

    public static int[] OnSendCommonSystemToJNIListener() {
        return listener.OnSendCommonSystemToJNIListener();
    }

    public native static void mainThread(int i, String devicepath);

    public native static void ThreadFinish();

    public native static void ATReset();
}
