package com.cvte.tv.at.serial;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.cvte.tv.at.api.CvteFacAPI;
import com.cvte.tv.at.listener.IUartDeviceListener;
import com.cvte.tv.at.listener.UartDevice;
import com.cvte.tv.at.util.Utils;
import com.cvte.tv.at.util.Utils.BurnData_E;
import com.cvte.tv.at.util.Utils.KEYDATA_E;
import com.cvte.tv.at.util.Utils.UART_DEBUG;
import com.cvte.tv.at.util.Utils.UART_E;

import java.util.Timer;
import java.util.TimerTask;

/**
 * UART AT Serial Device
 *
 * @author Leajen_Ren
 * @version V1.1 2014-06-24 更新了变量命名
 * @Package com.cvte.tv.at.api.mtk.api
 * @Description SerialDevice设计的理念是将UART的功能的封装在一个组建内，这里的组建提供监听器的接口，具体的每个API实现在其他地方
 * ， 这样做的好处是，能够将控制过程和实现过程进行分离，在移植的时候控制过程可以不变，变化的只有实现方法，
 * 也就是在CvteFacAPI_M6中实现的那些功能。 所以能在这里看到CvteFacAPI facapi的实例化。
 */
public class SerialDevice {

    private static CvteFacAPI sFacapi = null;
    private static Context sContext = null;
    private static SerialDevice instance;

    private static UartDevice mUartdevice = null;

    private static final String TAG = "-->SerialDevice";

    private static final int FACTORY_TEST_AT_RESET = 99;
    private static final int FACTORY_TEST_AT_SCAN_ACTION = 100;
    private static final int COMMAND_ACTION = 101;
    private static final int FAC_DELAYTIME = 800;
    private static final int FAC_DELAYTIME_0 = 0;

    private static enum CMD {
        getchecksum,
        getfixedchecksum,
        changesource,
        getsource,
        changechannel,
        setvolume,
        setbarcode,
        getbarcode,
        getkeypad,
        getusbstatus,
        getusbcount,
        getip,
        gethdcpksv,
        sendcommandcmd,
        returncommandcmd,
        savemac,
        saveconfigmac,
        getmac,
        getconfigmac,
        wifi,
        bt,
        cistatus,
        aircable,
        hdcp1xid,
        hdcp2xid,
        cipluskeyid,
        cus1id,
        cus2id,
        cus3id,
        cus4id,
        cus5id,
        hdcp1xburn,
        hdcp2xburn,
        cipluskeyburn,
        cus1burn,
        cus2burn,
        cus3burn,
        cus4burn,
        cus5burn,
        facircode
    }


    private static int[] getarray = null;
    private static int getint = -1;
    private static long getlong = -1;
    private static int getbool = -1;

    private static void GetArray(int[] d) {
        getarray = d;
    }

    private static void GetInt(int d) {
        getint = d;
    }

    private static void GetLong(long d) {
        getlong = d;
    }

    private static void GetBool(boolean d) {
        getbool = d ? 1 : 0;
    }

    private static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Utils.LOG("<AT> handleMessage msg.what=" + msg.what);
            switch (msg.what) {
                case FACTORY_TEST_AT_RESET:
                    if (mUartdevice != null)
                        mUartdevice.ATResetFunction();
                    break;
                case FACTORY_TEST_AT_SCAN_ACTION:
                    mUartdevice = new UartDevice(getsContext());
                    mUartdevice.UartListener(new UARTListenter());
                    mUartdevice.StartService(getsContext(), (UART_DEBUG) msg.obj);
                    break;
                case COMMAND_ACTION:
                    CMD cmd = (CMD) msg.obj;
                    Utils.LOG("<AT> cmd=" + cmd + " arg1=" + msg.arg1);

                    switch (cmd) {
                        case changesource:
                            GetBool(sFacapi.SetInputSource(Utils.SourceEnum.values()[msg.arg1]));
                            break;
                        case getchecksum:
                            GetArray(sFacapi.String2ArrayInt(sFacapi.GetSystemCheckSum()));
                            break;
                        case getfixedchecksum:
                            GetArray(sFacapi.String2ArrayInt(sFacapi.GetSystemFixedCheckSum()));
                            break;
                        case changechannel:
                            GetBool(sFacapi.ChangeChannelByID(msg.arg1));
                            break;
                        case getsource:
                            GetInt(sFacapi.GetInputSource().ordinal());
                            break;
                        case setvolume:
                            GetBool(sFacapi.SetVolume(msg.arg1));
                            break;
                        case setbarcode:
                            GetBool(sFacapi.SaveBarcode(sFacapi.ArrayInt2String(getarray)));
                            break;
                        case getbarcode:
                            GetArray(sFacapi.String2ArrayInt(sFacapi.GetBarcode()));
                            break;
                        case getkeypad:
                            GetInt(sFacapi.GetCurrentKeyPadStatus());
                            break;
                        case getusbstatus:
                            GetArray(sFacapi.GetUSBGroupsStatus());
                            break;
                        case getusbcount:
                            GetInt(sFacapi.GetUSBCount());
                            break;
                        case getip:
                            GetArray(sFacapi.GetIP());
                            break;
                        case gethdcpksv:
                            GetArray(sFacapi.GetHDCP_KSV());
                            break;
                        case sendcommandcmd:
                            GetBool(sFacapi.SendCommonDataToSystem(msg.arg1, getarray));
                            break;
                        case returncommandcmd:
                            GetArray(sFacapi.SendCommonSystemToJNI());
                            break;
                        case savemac:
                            GetBool(sFacapi.SaveMACAddr(getarray));
                            break;
                        case saveconfigmac:
                            GetBool(sFacapi.SaveConfigMACAddr(msg.arg1, getarray));
                            break;
                        case getmac:
                            GetArray(sFacapi.GetEthernetMacAddress());
                            break;
                        case getconfigmac:
                            GetArray(sFacapi.GetConfigMacAddress(msg.arg1));
                            break;
                        case wifi:
                            GetBool(sFacapi.StartWifiTestResult());
                            break;
                        case bt:
                            GetBool(sFacapi.StartBluetoothTest());
                            break;
                        case cistatus:
                            GetInt(sFacapi.GetCIFunctionStatus());
                            break;
                        case aircable:
                            GetBool(sFacapi.SetAntennaAirCableMode(msg.arg1));
                            break;
                        case hdcp1xid:
                            GetLong(sFacapi.GetTVKEY_ID(KEYDATA_E.Option_HDCPKey));
                            break;
                        case hdcp2xid:
                            GetLong(sFacapi.GetTVKEY_ID(KEYDATA_E.Option_HDCPKey20G));
                            break;
                        case cipluskeyid:
                            GetLong(sFacapi.GetTVKEY_ID(KEYDATA_E.Option_CIPlus));
                            break;
                        case cus1id:
                            GetLong(sFacapi.GetTVKEY_ID(KEYDATA_E.Option_Cus1));
                            break;
                        case cus2id:
                            GetLong(sFacapi.GetTVKEY_ID(KEYDATA_E.Option_Cus2));
                            break;
                        case cus3id:
                            GetLong(sFacapi.GetTVKEY_ID(KEYDATA_E.Option_Cus3));
                            break;
                        case cus4id:
                            GetLong(sFacapi.GetTVKEY_ID(KEYDATA_E.Option_Cus4));
                            break;
                        case cus5id:
                            GetLong(sFacapi.GetTVKEY_ID(KEYDATA_E.Option_Cus5));
                            break;
                        case hdcp1xburn:
                            GetBool(sFacapi.SaveDataToSystem(KEYDATA_E.Option_HDCPKey, getarray, msg.arg1));
                            break;
                        case hdcp2xburn:
                            GetBool(sFacapi.SaveDataToSystem(KEYDATA_E.Option_HDCPKey20G, getarray, msg.arg1));
                            break;
                        case cipluskeyburn:
                            GetBool(sFacapi.SaveDataToSystem(KEYDATA_E.Option_CIPlus, getarray, msg.arg1));
                            break;
                        case cus1burn:
                            GetBool(sFacapi.SaveDataToSystem(KEYDATA_E.Option_Cus1, getarray, msg.arg1));
                            break;
                        case cus2burn:
                            GetBool(sFacapi.SaveDataToSystem(KEYDATA_E.Option_Cus2, getarray, msg.arg1));
                            break;
                        case cus3burn:
                            GetBool(sFacapi.SaveDataToSystem(KEYDATA_E.Option_Cus3, getarray, msg.arg1));
                            break;
                        case cus4burn:
                            GetBool(sFacapi.SaveDataToSystem(KEYDATA_E.Option_Cus4, getarray, msg.arg1));
                            break;
                        case cus5burn:
                            GetBool(sFacapi.SaveDataToSystem(KEYDATA_E.Option_Cus5, getarray, msg.arg1));
                            break;
                        case facircode:
                            GetBool(sFacapi.sendKeyEvent(msg.arg1));
                            break;
                    }

                    break;
            }
        }
    };

    private SerialDevice(Context context) {
        setsContext(context);
    }

    public static SerialDevice getInstance(Context context) {
        if (instance == null) {
            instance = new SerialDevice(context);
        }
        return instance;
    }

    public void onStart(final UART_DEBUG type) {
        Utils.LOG("SerialDevice onStart-Start");
        sFacapi = CvteFacAPI.getInstance(getsContext());
        sFacapi.UARTDebugEnable(UART_E.Init);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = FACTORY_TEST_AT_SCAN_ACTION;
                msg.obj = type;
                mHandler.sendMessageDelayed(msg, FAC_DELAYTIME);
            }
        }, 500);
        Utils.LOG("SerialDevice onStart-End");
    }

    public void ATReset2JNI() {
        Utils.LOG("SerialDevice ATReset2JNI-Start");
        Message msg = new Message();
        msg.what = FACTORY_TEST_AT_RESET;
        mHandler.sendMessageDelayed(msg, FAC_DELAYTIME_0);
        Utils.LOG("SerialDevice ATReset2JNI-End");
    }

    public void onDestory() {
        Utils.LOG("<ATScreen> onPause-start");
        sFacapi.UARTDebugEnable(UART_E.Final);
        if (mUartdevice != null) {
            try {
                mUartdevice.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Utils.LOG("<ATScreen> onPause-End");
    }

    // must base on Activity Content
    private static Context getsContext() {
        return sContext;
    }

    private static void setsContext(Context context) {
        SerialDevice.sContext = context;
    }

    /**
     * @Title UARTListenter
     * @Description 使用监听器的最大好处是，UARTDevie的可移植性非常好，将所有UART功能封装成一个模块，防止代码污染和更多bug出现
     */
    private static class UARTListenter implements IUartDeviceListener {

        private boolean StartSendAct(CMD tar, int val) {
            getbool = -1;
            Message msg = new Message();
            msg.what = COMMAND_ACTION;
            msg.obj = tar;
            msg.arg1 = val;
            SerialDevice.mHandler.sendMessage(msg);
            while (getbool == -1) {
                ;
            }
            return (getbool == 1) ? true : false;
        }

        private int StartCatchval(CMD tar, int val) {
            getint = -1;
            Message msg = new Message();
            msg.what = COMMAND_ACTION;
            msg.obj = tar;
            msg.arg1 = val;
            SerialDevice.mHandler.sendMessage(msg);
            while (getint == -1) {
                ;
            }
            return getint;
        }

        private long StartCatchlong(CMD tar, int val) {
            getlong = -1;
            Message msg = new Message();
            msg.what = COMMAND_ACTION;
            msg.obj = tar;
            msg.arg1 = val;
            SerialDevice.mHandler.sendMessage(msg);
            while (getlong == -1) {
                ;
            }
            return getlong;
        }

        private int[] StartCatchIntArray(CMD tar, int val) {
            getarray = null;
            Message msg = new Message();
            msg.what = COMMAND_ACTION;
            msg.obj = tar;
            msg.arg1 = val;
            SerialDevice.mHandler.sendMessage(msg);
            while (getarray == null) {
                ;
            }
            return getarray;
        }

        private boolean StartSendIntArray(CMD tar, int val, int[] valarray) {
            getbool = -1;
            getarray = valarray;
            Message msg = new Message();
            msg.what = COMMAND_ACTION;
            msg.obj = tar;
            msg.arg1 = val;
            SerialDevice.mHandler.sendMessage(msg);
            while (getbool == -1) {
                ;
            }
            return (getbool == 1) ? true : false;
        }

        @Override
        public int[] OnGetTVChecksumListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchIntArray(CMD.getchecksum, 0);
        }

        @Override
        public int[] OnGetTVFixedChecksumListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchIntArray(CMD.getfixedchecksum, 0);
        }

        @Override
        public boolean OnSetTVSourceListener(int source) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartSendAct(CMD.changesource, source);
        }

        @Override
        public int OnGetTVSourceListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            Utils.LOG("<UART> OnGetTVSourceListener Start");
            return StartCatchval(CMD.getsource, 0);
        }

        @Override
        public boolean OnSetVolumeListener(int volume) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            Utils.LOG("Set Volume = " + volume);
            return StartSendAct(CMD.setvolume, volume);
        }

        @Override
        public boolean OnChangeTVNumListener(int num) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            Utils.LOG("On Change TV Num = " + num);
            return StartSendAct(CMD.changechannel, num);
        }

        @Override
        public int[] OnGetTVMacAddrListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchIntArray(CMD.getmac, 0);
        }

        @Override
        public int[] OnGetTVConfigMacAddrListener(int type) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchIntArray(CMD.getconfigmac, type);
        }

        @Override
        public long OnGetLongData_IDListener(int type) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            switch (BurnData_E.values()[type]) {
                case EN_HDCP_KEY:
                    return StartCatchlong(CMD.hdcp1xid, 0);
                case EN_CI_PLUS_KEY:
                    return StartCatchlong(CMD.cipluskeyid, 0);
                case EN_HDCP_20G:
                case EN_HDCP_22G:
                    return StartCatchlong(CMD.hdcp2xid, 0);
                case EN_CUS_1:
                    return StartCatchlong(CMD.cus1id, 0);
                case EN_CUS_2:
                    return StartCatchlong(CMD.cus2id, 0);
                case EN_CUS_3:
                    return StartCatchlong(CMD.cus3id, 0);
                case EN_CUS_4:
                    return StartCatchlong(CMD.cus4id, 0);
                case EN_CUS_5:
                    return StartCatchlong(CMD.cus5id, 0);
                case EN_KEY_MAX:
                    break;
            }
            return 0;
        }

        @Override
        public boolean OnSaveLongDataToSystemListener(int type, int[] key, int len, int fileId) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            Utils.LOG("<UART-AT> Save Type=" + BurnData_E.values()[type]);
            switch (BurnData_E.values()[type]) {
                case EN_HDCP_KEY:
                    return StartSendIntArray(CMD.hdcp1xburn, fileId, key);
                case EN_CI_PLUS_KEY:
                    return StartSendIntArray(CMD.cipluskeyburn, fileId, key);
                case EN_HDCP_20G:
                case EN_HDCP_22G:
                    return StartSendIntArray(CMD.hdcp2xburn, fileId, key);
                case EN_CUS_1:
                    return StartSendIntArray(CMD.cus1burn, fileId, key);
                case EN_CUS_2:
                    return StartSendIntArray(CMD.cus2burn, fileId, key);
                case EN_CUS_3:
                    return StartSendIntArray(CMD.cus3burn, fileId, key);
                case EN_CUS_4:
                    return StartSendIntArray(CMD.cus4burn, fileId, key);
                case EN_CUS_5:
                    return StartSendIntArray(CMD.cus5burn, fileId, key);
                case EN_KEY_MAX:
                    break;
            }
            return false;
        }

        @Override
        public boolean OnSaveMACAddrToTVListener(int mac0, int mac1, int mac2, int mac3, int mac4, int mac5) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartSendIntArray(CMD.savemac, 0, new int[]{mac0, mac1, mac2, mac3, mac4, mac5});
        }

        @Override
        public boolean OnSaveConfigMACAddrToTVListener(int type, int mac0, int mac1, int mac2, int mac3, int mac4, int mac5) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartSendIntArray(CMD.saveconfigmac, type, new int[]{mac0, mac1, mac2, mac3, mac4, mac5});
        }

        @Override
        public boolean OnCtrlIOLevelListener(int pin, int level) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return true;//sFacapi.CtrlIOLevel(pin, level);
        }

        @Override
        public int OnGetCurrentKeyPadStatusListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchval(CMD.getkeypad, 0);
        }

        @Override
        public boolean OnSaveBarcodeToFlashListener(int[] barcode) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartSendIntArray(CMD.setbarcode, 0, barcode);
        }

        @Override
        public int[] OnGetBarcodeFromFlashListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchIntArray(CMD.getbarcode, 0);
        }

        @Override
        public int[] OnGetHDCPKSVCodeListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchIntArray(CMD.gethdcpksv, 0);
        }

        @Override
        public boolean OnSetAntennaAirCableListener(int type) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartSendAct(CMD.aircable, type);
        }

        @Override
        public boolean OnSetATSCProNumberListener(int major, int minor) {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return false;//sFacapi.SetATSCProNumber(major, minor);
        }

        @Override
        public int OnGetCIFunctionStatusListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchval(CMD.cistatus, 0);
        }

        @Override
        public int[] OnGetIPAddrListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchIntArray(CMD.getip, 0);
        }

        @Override
        public boolean OnGetWifiTestResultStartListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartSendAct(CMD.wifi, 0);
        }

        @Override
        public int OnGetUSBConnectCountListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartCatchval(CMD.getusbcount, 0);
        }

        @Override
        public int[] OnGetUSBConnectStatusListener() {
            return StartCatchIntArray(CMD.getusbstatus, 0);
        }

        @Override
        public boolean OnSendCVTEFACIRKeyListener(int key_code) {
            return StartSendAct(CMD.facircode, key_code);
        }

        @Override
        public boolean OnCheckBluetoothStartListener() {
            Utils.LOG(new Exception().getStackTrace()[0].getMethodName() + TAG);
            return StartSendAct(CMD.bt, 0);
        }

        @Override
        public void OnSendCommonJNIToSystemListener(int cmdtype, int[] cmddata) {
            StartSendIntArray(CMD.sendcommandcmd, cmdtype, cmddata);
        }

        @Override
        public int[] OnSendCommonSystemToJNIListener() {
            return StartCatchIntArray(CMD.returncommandcmd, 0);
        }
    }
}
