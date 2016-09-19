package com.cvte.tv.at.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cvte.tv.at.R;
import com.cvte.tv.at.api.tvapi.EntityInputSource;
import com.cvte.tv.at.api.tvapi.HisiFunAPI;
import com.cvte.tv.at.api.tvapi.TVAPI;
import com.cvte.tv.at.api.tvapi.hisilicon.EntityChannel;
import com.cvte.tv.at.serial.SerialDevice;
import com.cvte.tv.at.util.CVTEBURN;
import com.cvte.tv.at.util.SourceItem;
import com.cvte.tv.at.util.Utils;
import com.cvte.tv.at.util.Utils.EnumAntennaType;
import com.cvte.tv.at.util.Utils.EnumFactoryDataType;
import com.cvte.tv.at.util.Utils.EnumInputSourceCategory;
import com.cvte.tv.at.util.Utils.EnumKeyPad;
import com.cvte.tv.at.util.Utils.EnumLedStatus;
import com.cvte.tv.at.util.Utils.KEYDATA_E;
import com.cvte.tv.at.util.Utils.SourceEnum;
import com.cvte.tv.at.util.Utils.UART_DEBUG;
import com.cvte.tv.at.util.Utils.UART_E;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Factory API Group，API组件
 *
 * @author Leajen_Ren
 * @version V1.1 2014-10-13
 * @Package com.cvte.tv.at.api
 * @Description
 */
public class CvteFacAPI {

    private final Object mutex = new Object();
    private static Context sContext;
    private static CvteFacAPI sCvtefacapi = null;
    private static CommonAPI sComapi = null;
    private static SerialDevice sSerialdev = null;
    private static HisiFunAPI sHisiapi = null;
    private static List<SourceItem> SourceTable = new ArrayList<SourceItem>();

    private static EnumAntennaType perantennatype = EnumAntennaType.ANTENNA_TYPE_ALL;
    private static final int HANDLER_DTV_CHANGE_BEFORE_TIME = 1000;
    private static final int HANDLER_DTV_CHANGE_AFTER_TIME = 2000;

    public static CvteFacAPI getInstance(Context context) {

        if (sCvtefacapi == null) {
            Utils.LOG("CvteFacAPI.getInstance = " + context.getClass());
            sCvtefacapi = new CvteFacAPI(context);
            sComapi = CommonAPI.getInstance(getmContext());
            sHisiapi = new HisiFunAPI(getmContext());
            sSerialdev = SerialDevice.getInstance(getmContext());
        }
        return sCvtefacapi;
    }

    public CvteFacAPI(Context context) {
        setmContext(context);
        TvApiInit(context);
    }

    public static Context getmContext() {
        return sContext;
    }

    public static void setmContext(Context context) {
        CvteFacAPI.sContext = context;
    }

    public boolean ApiInitStatus() {
        return true;
    }

    public void Finalize() {
        this.Finalize();
    }

    private static TVAPI TvApi = null;

    public void TvApiInit(Context context) {
        GetTime("TvApiInit:Link-Start");
        TvApi = new TVAPI(context);
    }

    public String GetTime(String str) {
        long msec = SystemClock.uptimeMillis();
        long sec = msec / 1000;
        long min = sec / 60;

        long sec_show = sec % 60;
        long min_show = min % 60;

        Formatter formatter = new Formatter();
        String Ltime = formatter.format("%02d:%02d:%04d",
                min_show, sec_show, msec).toString().toLowerCase();

        Utils.LOG("<AT> Ltime = " + Ltime + " str=" + str);
        return Ltime;
    }


    public String GetSERIAL_DEVICES() {
        return "/dev/ttyAMA0";
    }

    /**
     * Serial Mode的启动控制
     *
     * @param uartCvte
     */
    public void SetUART_DEBUG(UART_DEBUG uartCvte) {
        switch (uartCvte) {
            case UART_CVTE:
                sSerialdev.onStart(uartCvte);
                break;
            case UART_FINISH:
                sSerialdev.onDestory();
                break;
        }
    }

    /**
     * AT启动之后的系统配置，设备权限赋予，系统权限开放，关闭Log等功能要在这里面实现
     * 注意在这API中要实现：SetLVDSOFF，setBlueScreen的功能
     *
     * @param state
     */
    public boolean UARTDebugEnable(UART_E state) {
        boolean rel = false;
        Utils.LOG("<ATScreen> UARTDebugEnable state=" + state);
        rel = TvApi.eventSystemDebugUartSetEnable(state.equals(UART_E.Init));
        return rel;
    }

    /**
     * AT启动之后应该是要出现整个TV画面的，这个API的作用就是打画面出来
     *
     * @param key
     */
    public void setATScreen(boolean key) {
    }

    public void ATResetFunction() {
        Utils.LOG("ATResetFunction in CvteFacAPI");
//        if (sSerialdev != null)
//            sSerialdev.ATReset2JNI();
        CVTEBURN.delete();
        CleanChannelData();
        //Set Volume to 20 or default
        //change to ATV
        Reset4CustomerFactory();
    }

    public void Reset4CustomerFactory() {
        sHisiapi.boardFactoryReset();
    }

    public void ThreadSleep(int i) {
        sComapi.ThreadSleep(i);
    }

    /**
     * 系统目前支持的频道列表
     *
     * @return
     */
    public List<SourceEnum> SupportSourceList() {

        List<SourceEnum> EnumList = new ArrayList<SourceEnum>();
        EnumList.clear();
        Utils.LOG("SupportSourceList Enter");
        List<EntityInputSource> SrcList = new ArrayList<EntityInputSource>();
        SrcList.clear();
        SrcList = TvApi.eventSystemInputSourceGetList();
        Utils.LOG("Init SrcList.size()=" + SrcList.size());
        Utils.LOG("SupportSourceList SrcList.size()=" + SrcList.size());
        if (SrcList.size() == 0)
            return EnumList;

        SourceEnum Src = SourceEnum.EN_AT_SOURCE_MAX;
        EnumInputSourceCategory OrgSrc = null;
        EnumInputSourceCategory PerSrc = null;
        int Srcitem = 0, size = SrcList.size();
        for (int i = 0; i < size; i++) {
            OrgSrc = SrcList.get(i).category;
            if (OrgSrc != PerSrc) {
                PerSrc = OrgSrc;
                Srcitem = 0;
            }
            Srcitem += 1;
            Utils.LOG("OrgSrc = " + OrgSrc);
            switch (OrgSrc) {
                case INPUTSOURCE_TV:
                    Utils.LOG("OrgSrc = Mutile ATV/DTV Source Set DTV Source");
                    Src = SourceEnum.valueOf("EN_AT_DVBT1");// this solution use Air/Cable control function
                    break;
                case INPUTSOURCE_ATV:
                    Src = SourceEnum.valueOf("EN_AT_ATV");
                    break;
                case INPUTSOURCE_DVBT:
                    Src = SourceEnum.valueOf("EN_AT_DTV");//默认只有一个DTV
                    break;
                case INPUTSOURCE_DTMB:
                    Src = SourceEnum.valueOf("EN_AT_DTV");//默认只有一个DTV
                    break;
                case INPUTSOURCE_DVBC:
                    Src = SourceEnum.valueOf("EN_AT_DTV");//默认只有一个DTV
                    break;
                case INPUTSOURCE_DVBS:
                    Src = SourceEnum.valueOf("EN_AT_DTV");//默认只有一个DTV
                    break;
                case INPUTSOURCE_ATSC:
                    Src = SourceEnum.valueOf("EN_AT_DTV");
                    break;
                case INPUTSOURCE_HDMI:
                    Src = SourceEnum.valueOf("EN_AT_HDMI" + Srcitem);
                    break;
                case INPUTSOURCE_AV:
                    Src = SourceEnum.valueOf("EN_AT_AV" + Srcitem);
                    break;
                case INPUTSOURCE_SVIDEO:
                    Src = SourceEnum.valueOf("EN_AT_SVIDEO" + Srcitem);
                    break;
                case INPUTSOURCE_YPBPR:
                    Src = SourceEnum.valueOf("EN_AT_YPBPR" + Srcitem);
                    break;
                case INPUTSOURCE_PC:
                    Src = SourceEnum.valueOf("EN_AT_VGA" + Srcitem);
                    break;
                case INPUTSOURCE_SCART:
                    Src = SourceEnum.valueOf("EN_AT_SCART" + Srcitem);
                    break;
                case INPUTSOURCE_APP:
                    Src = SourceEnum.valueOf("EN_AT_USB" + Srcitem);
                    break;
            }
            Utils.LOG("OrgSrc = " + OrgSrc + " SupportSourceList <" + i + "> = " + Src + " SrcID=" + SrcList.get(i).id);
            SourceTable.add(new SourceItem(Src, SrcList.get(i).id));//this item is source id
            EnumList.add(Src);//this item is source id
        }
        return EnumList;
    }

//==================AT API Start=====================

    public String GetSystemCheckSum() {
        return TvApi.eventSystemInformationGetFirmwareVersion();
    }

    //Renlijia.20151208 add for catch fixed checksum
    public String GetSystemFixedCheckSum() {
        return GetSystemCheckSum();
    }

    private SourceEnum perSrc = SourceEnum.EN_AT_SOURCE_MAX;
    private int dtvtimer = 0;

    private void DelayChangeDTV(SourceEnum perSrc, SourceEnum src, int time) {
        if (!SourceEnum.isDTV(perSrc) && SourceEnum.isDTV(src)) {
            Utils.LOG("DTV Delay Timer=" + dtvtimer);
            Sleep(time);
        }
    }

    public void Sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean SetInputSource(SourceEnum src) {
        synchronized (mutex) {
            Utils.LOG("SetInputSource = " + src);
            boolean rel = false;
            SourceTable = new ArrayList<SourceItem>();
            SourceTable.clear();
            SupportSourceList();

            Utils.LOG("SourceTable.size() = " + SourceTable.size());
            if (SourceTable.size() == 0)
                return rel;

            for (int id = 0; id < SourceTable.size(); id++) {
                Utils.LOG("SourceTable.get(" + id + ").GetSrcItem() = " + SourceTable.get(id).GetSrcItem() + " src=" + src);
                if (SourceTable.get(id).GetSrcItem() == src) {
                    Utils.LOG("SetInputSourceID = " + id);
//                            if (perSrc == src)
//                                return true;
                    DelayChangeDTV(perSrc, src, HANDLER_DTV_CHANGE_BEFORE_TIME);
                    Utils.LOG("<AT> SetInputSrc = " + SourceTable.get(id).GetSrcItem());
                    rel = TvApi.eventSystemInputSourceSetInputSource(SourceTable.get(id).GetSrcID());
                    DelayChangeDTV(perSrc, src, HANDLER_DTV_CHANGE_AFTER_TIME);
                    if (perSrc != src)
                        perSrc = src;
                    return rel;
                }
            }
            return rel;
        }
    }


    public SourceEnum GetInputSource() {
        if (SourceTable.size() == 0) {
            SupportSourceList();
        }

//        Utils.LOG("GetInputSource SourceTable.size()=" + SourceTable.size());
        if (SourceTable.size() == 0)
            return SourceEnum.EN_AT_SOURCE_MAX;

        EntityInputSource src = null;
        src = TvApi.eventSystemInputSourceGetInputSource();

        int Srcid = 0;
        if (src == null)
            Utils.LOG("GetInputSource EntityInputSource=null");
        else
            Srcid = src.id;

        for (int id = 0; id < SourceTable.size(); id++) {
            if (SourceTable.get(id).GetSrcID() == Srcid) {
//                Utils.LOG("GetInputSource GetSrcItem=" + SourceTable.get(id).GetSrcItem());
                return SourceTable.get(id).GetSrcItem();
            }
        }
        return SourceEnum.EN_AT_SOURCE_MAX;
    }


    public boolean SetVolume(int val) {
        if (val < 0 || val > 100) return false;
        return TvApi.eventSoundSpeakerOutSetVolume(val);
    }


    public boolean state = true;

    public boolean ChangeChannelByID(int val) {

        SourceEnum NowSrc = GetInputSource();
        Utils.LOG("<AT> ChangeChannelByID-1=" + val);
        if (SourceEnum.isATV(NowSrc) || SourceEnum.isDTV(NowSrc))
            ;
        else
            return false;
        List<EntityChannel> chdata = null;

        if (SourceEnum.isATV(NowSrc)) {
            chdata = TvApi.eventTVChannelsGetChannelList(EnumInputSourceCategory.INPUTSOURCE_ATV);
            Utils.LOG("<AT> isATV=" + val + " atv.size()=" + chdata.size());
            if (val > chdata.size())
                return false;
            if (val <= 1)
                val = 1;
        } else if (SourceEnum.isDTV(NowSrc)) {
            Utils.LOG("<AT> isDTV=" + val);
            if (val <= 1)
                val = 1;
            return sHisiapi.dtvPlay(val);
        }

        Utils.LOG("<AT> ChangeChannelByID-2=" + val);
        TvApi.eventTVChannelsTuneChannelById(NowSrc, val, chdata);

        return false;
    }

    public EnumAntennaType GetNowAntennaAirCableMode() {
        return EnumAntennaType.ANTENNA_TYPE_AIR;
    }

    public int[] GetEthernetMacAddress() {
        byte[] orgMAC = new byte[Utils.MACLen];
        int[] intMAC = new int[Utils.MACLen];

        orgMAC = TvApi.eventSystemMacGetValue();
        for (int i = 0; i < Utils.MACLen; i++)
            intMAC[i] = orgMAC[i] & 0xFF;

        if (intMAC == null)
            return Utils.MAC_EMPTY_INT;

        if (intMAC.length != 6)
            return Utils.MAC_LENGTH_NG_INT;

        String MAC = "";
        for (int i = 0; i < intMAC.length; i++) {
            if (intMAC[i] < 0x10)
                MAC += "0" + Integer.toHexString(intMAC[i]) + ":";
            else
                MAC += Integer.toHexString(intMAC[i]) + ":";
        }
        MAC = MAC.substring(0, MAC.length() - 1);
        Utils.LOG("GetEthernetMacAddress MAC From TvApi()= " + MAC);
        // MAC_FULL = "ff:ff:ff:ff:ff:ff";
        // MAC_EMPTY = "ff:ff:ff:ff:ff:f0";
        // MAC_CHECKSUM_ERROR = "ff:ff:ff:ff:ff:f1";
        // MAC_LENGTH_NG = "ff:ff:ff:ff:ff:f2";
        // MAC_NETCARD_DIFF_DTORAGE = "ff:ff:ff:ff:ff:f3";
        if (MAC.equals(Utils.MAC_FULL)) {
            return Utils.MAC_FULL_INT;
        } else if (MAC.equals(Utils.MAC_EMPTY)) {
            return Utils.MAC_DEFAULT_INT;
        } else if (MAC.equals(Utils.MAC_CHECKSUM_ERROR)) {
            return Utils.MAC_DEFAULT_INT;
        } else if (MAC.equals(Utils.MAC_LENGTH_NG)) {
            return Utils.MAC_DEFAULT_INT;
        } else if (MAC.equals(Utils.MAC_NETCARD_DIFF_DTORAGE)) {
            return Utils.MAC_DEFAULT_INT;
        } else {//right value
            return intMAC;
        }
    }

    public int[] GetConfigMacAddress(int type) {

        Utils.LOG("GetConfigMacAddress Type:" + type);
        return GetEthernetMacAddress();
    }

    public long GetTVKEY_ID(KEYDATA_E type) {
        long data = 0;
        String ID = "0";
        switch (type) {
            case Option_HDCPKey:
                ID = TvApi.eventSystemHdcp1xKeyGetKeyName();
                if (ID != null)
                    data = Long.parseLong(ID);
                Utils.LOG("HDCP ID:" + data);
                break;
            case Option_HDCPKey20G:
            case Option_HDCPKey22G:
                ID = TvApi.eventSystemHdcp2xKeyGetKeyName();
                if (ID != null)
                    data = Long.parseLong(ID);
                Utils.LOG("HDCP2XG ID:" + data);
                break;
            case Option_CIPlus:
                ID = TvApi.eventSystemCiPlusKeyGetName();
                if (ID != null)
                    data = Long.parseLong(ID);
                Utils.LOG("CI+ ID:" + data);
                break;
            case Option_Cus1:
            case Option_Cus2:
            case Option_Cus3:
            case Option_Cus4:
            case Option_Cus5:
                break;
        }
        return data;
    }

    public boolean SaveDataToSystem(KEYDATA_E type, int[] key, int fileId) {
        sComapi.ShowKeyData(key);
        String ID = String.valueOf(fileId);
        int len = key.length;
        Utils.LOG("SaveDataToSystem len=" + len + " fileId=" + fileId + " type=" + type);
        if (len == 0)
            return false;

        byte[] Data = new byte[len];
        switch (type) {
            case Option_HDCPKey:
            case Option_HDCPKey20G:
            case Option_HDCPKey22G:
            case Option_CIPlus:
            case Option_Cus1:
            case Option_Cus2:
            case Option_Cus3:
            case Option_Cus4:
            case Option_Cus5:
                for (int i = 0; i < len; i++)
                    Data[i] = (new Integer(key[i])).byteValue();
                break;
            default:
                return false;
        }
        boolean rel = false;
        switch (type) {
            case Option_HDCPKey:
//                rel = TvApi.eventSystemHdcp1xKeyBurning(ID, Data, len);
                rel = TvApi.eventSystemHdcpKeyBurningByWY(type, Data, len);
                Utils.LOG("HDCP fileId = " + fileId);
                break;
            case Option_HDCPKey20G:
            case Option_HDCPKey22G:
//                rel = TvApi.eventSystemHdcp2xKeyBurning(ID, Data, len);
                rel = TvApi.eventSystemHdcpKeyBurningByWY(type, Data, len);
                Utils.LOG("HDCP20G fileId = " + fileId);
                break;
            case Option_CIPlus:
                rel = TvApi.eventSystemCiPlusKeyBurning(ID, Data, len);
                Utils.LOG("CI+ fileId = " + fileId);
                break;
            case Option_Cus1:
            case Option_Cus2:
            case Option_Cus3:
            case Option_Cus4:
            case Option_Cus5:
                break;
        }
        return rel;
    }

    public boolean SaveMACAddr(int[] macAddr) {

        int i, length = macAddr.length;
        byte[] MAC = new byte[length];
        if (macAddr.length != 6)
            return false;

        Utils.LOG("Write MAC:");
        for (i = 0; i < length; i++) {
            MAC[i] = (byte) (macAddr[i] & 0xFF);
            Utils.LOG("macbyte[" + i + "]:" + MAC[i] + " macint[" + i + "]:" + macAddr[i]);
        }
        Utils.LOG("Write MAC End");
        return TvApi.eventSystemMacSetValue(MAC);
    }

    public boolean SaveConfigMACAddr(int type, int[] macAddr) {
        Utils.LOG("Burn Type:" + type + " Write MAC:");
        return SaveMACAddr(macAddr);
    }

    /**
     * AT控制端口电压的API，不过由于没有明确的设计对象，因此在这里不做设计
     *
     * @param pin
     * @param level
     * @return
     */
    public boolean CtrlIOLevel(int pin, int level) {
        return false;
    }

    public int GetCurrentKeyPadStatus() {
        int item = Utils.KeyPad_Error;
        item = TvApi.eventSystemKeyPadGetPressingKey().ordinal();
        Utils.LOG("GetCurrentKeyPadStatus item = " + item);
        if (item == EnumKeyPad.KEY_PAD_K7.ordinal())
            return Utils.KeyPad_Error;
        return item;
    }

    public boolean SaveBarcode(String barcode) {
        boolean rel = false;
        Utils.LOG("Save Barcode = " + barcode + " Len = " + barcode.length());
//        rel = TvApi.eventSystemSetFactoryData(EnumFactoryDataType.FACTORY_DATA_TYPE_SN, barcode);
        Utils.LOG("Save Barcode Finish");
        return rel;
    }

    public String GetBarcode() {
        Utils.LOG("Enter Barcodetest");
//        return TvApi.eventSystemGetFactoryData(EnumFactoryDataType.FACTORY_DATA_TYPE_SN);
        return "No Need Burn Barcode";
    }

    public int[] GetHDCP_KSV() {
        int[] KSVData = new int[Utils.HDCP_KSV_Space];
        byte[] OrData = new byte[Utils.HDCP_KSV_Space];
        OrData = TvApi.eventSystemHdcp1xKeyGetKSV();
        if ((OrData == null) || (OrData.length == 0))
            return KSVData;

        for (int i = 0; i < Utils.HDCP_KSV_Space; i++)
            KSVData[i] = ((int) OrData[i]) & 0xFF;

        return KSVData;
    }

    //true = Air
    // false = Cable
    public boolean SetAntennaAirCableMode(int type) {
        return false;
    }

    public boolean eventDtvCiIsInserted() {
        if (sHisiapi == null)
            sHisiapi = new HisiFunAPI(getmContext());
        return sHisiapi.CiIsInserted();
    }

    public boolean eventSystemCiPlusKeyCheck() {
        if (sHisiapi == null)
            sHisiapi = new HisiFunAPI(getmContext());
        return sHisiapi.CiCardDataCheck();
    }

    public int GetCIFunctionStatus() {
        Utils.LOG("GetCIFunctionStatus  = start");
        if (!SourceEnum.isDTV(GetInputSource()))//must in DTV can do detect CI Card
            return 1;//error

        if (eventDtvCiIsInserted()) {
            Utils.LOG("eventDtvCiIsInserted = IsInserted");
            if (eventSystemCiPlusKeyCheck()) {
                Utils.LOG("eventSystemCiPlusKeyCheck = OK");
                return 0;//OK
            } else {
                Utils.LOG("eventSystemCiPlusKeyCheck = NG");
                return 2;//Fail
            }
        } else {
            Utils.LOG("eventDtvCiIsInserted = No Inserted");
            return 1;// No Insertrd
        }
    }

    public int[] GetIP() {
        String TVIP = sComapi.GetEthernetIP(getmContext());
        if ((TVIP != null) || (!TVIP.equals("")) || (!TVIP.equals(null))) {
            String IPStr[] = TVIP.replace('.', '-').split("-");
            Utils.LOG("IP Adress:" + TVIP + " IPStr.length=" + IPStr.length);
            int[] IP = new int[]{0, 0, 0, 0};
            for (int i = 0; i < IPStr.length; i++) {
                IP[i] = Integer.parseInt(IPStr[i]);
                Utils.LOG("IP[" + i + "]:" + IP[i]);
            }

            return IP;
        } else
            return new int[]{0, 0, 0, 0};
    }

    public boolean StartWifiTestResult() {
        return TvApi.eventCheckWifiAutoTest();
    }

    public int GetUSBCount() {
        int count = TvApi.eventUsbGetStorageDeviceInsertedCount();
        Utils.LOG("USB mFacapi count1 = " + count);
        return count;
    }

    public int USBBusCount() {
        //Step1 USB-Disk Channel Table
        File usbbus = new File(Utils.USB_BUS_PATH);
        if (usbbus != null && usbbus.exists()) {
            File[] usbbusitems = usbbus.listFiles();
            Utils.LOG("usbbusitems.size = " + usbbusitems.length);
            return usbbusitems.length;
        }
        return 0;
    }

    public List<String> USBDeviceList() {

        UsbManager sUsbManager = (UsbManager) getmContext().getSystemService(Context.USB_SERVICE);
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

    public int[] GetUSBGroupsStatus() {
        Utils.LOG("GetUSBGroupsStatus");

        int devlist[] = new int[Utils.DevCount];
        //Step1. init to NG
        for (int x = 0; x < Utils.DevCount; x++)
            devlist[x] = Utils.NO_DEVICE;

        devlist[0] = (FileFlag.Exist(Utils.USB_DEVICES_USB20)) ? Utils.USB20_OK : Utils.USB20_NG;
        devlist[1] = Utils.USB30_NG;
        if (FileFlag.Exist(Utils.USB_DEVICES_USB30_30))
            devlist[1] = Utils.USB30_OK;
        else {
            if (FileFlag.Exist(Utils.USB_DEVICES_USB30_20))
                devlist[1] = Utils.USB20_OK;
        }
//        devlist[2] = FileFlag.Exist(Utils.USB_DEVICES_BT) ? Utils.USB20_OK : Utils.USB20_NG;
//        devlist[3] = (FileFlag.Exist(Utils.USB_DEVICES_WIFI1) || FileFlag.Exist(Utils.USB_DEVICES_WIFI2)) ? Utils.USB20_OK : Utils.USB20_NG;
//        devlist[4] = FileFlag.Exist(Utils.SDCARD_DEVICES_DEV) ? Utils.SDCard_OK : Utils.SDCard_NG;

        return devlist;
    }

    boolean cmdrel = false;

    public boolean SendCommonDataToSystem(int cmdtype, int[] cmddata) {
        Utils.LOG("SendCommonDataToSystem cmdtype=" + cmdtype + " cmddata=" + ArrayInt2String(cmddata));
        cmdrel = false;
        String rec = "SendData";
        Utils.LOG("TvApi != null");
        String CMD = "CMD:" + rec + ":" + cmdtype + ":" + ArrayInt2String(cmddata);
        cmdrel = TvApi.eventSystemSetFactoryData(EnumFactoryDataType.FACTORY_DATA_TYPE_UUID, CMD);
        Utils.LOG("SendCommonDataToSystem Finish");
        return true;
    }

    // 0x00 boolean
    // 0x01 is only one
    // 0x02 same as default
    public int[] SendCommonSystemToJNI() {
        if (!cmdrel)
            return new int[]{0x01, 'n', 'u', 'l', 'l'};

        String rec = "SendData";
        Utils.LOG("TvApi != null");
        rec = TvApi.eventSystemGetFactoryData(EnumFactoryDataType.FACTORY_DATA_TYPE_UUID);

        Utils.LOG("rec == " + rec);
        // rec data is = REC:type:String:reltype
        String recstrarray[] = rec.split(":");

        int type = -1;
        int reltype = 0;
        if (recstrarray[0].equals("REC") && (recstrarray.length == 4)) {
            type = Integer.parseInt(recstrarray[1]);
            rec = recstrarray[2];
            reltype = Integer.parseInt(recstrarray[3]);
        }

        int recarray[] = new int[0];
        if ((!"SendData".equals(rec)))
            recarray = String2ArrayInt(rec);

        int reldata[] = new int[recarray.length + 1];
        reldata[0] = reltype;
        for (int i = 0; i < recarray.length; i++) {
            reldata[1 + i] = recarray[i];
        }

        Utils.LOG("RecData type=" + type + " reltype=" + reltype + " rec=" + rec);
        return reldata;
    }

    public boolean sendKeyEvent(int key) {
        Utils.LOG("OnCVTEFACIRCtrl = " + key);
        switch (key) {
            case Utils.CvteIRRaw_F1:
                getmContext().sendBroadcast(new Intent(Utils.CVTE_BROADCAST_RESETKEY));
                return true;
        }
        return false;
    }

    public boolean StartBluetoothTest() {
        return TvApi.eventCheckBluetoothAT();
    }

    /**
     * The following 2 functions implement for the video surfaceview to play video
     */
    public SurfaceView createSurfaceView(Activity at) {
        boolean enablesuferview = TvApi.eventSystemDebugPermissionEnable();
        if (enablesuferview) {
            Utils.LOG("createSurfaceView");
            SurfaceView v = (SurfaceView) at.findViewById(R.id.tranplentview);
            if (v != null) {
                Utils.LOG("createSurfaceView 2");
                v.getHolder().addCallback(new SurfaceHolder.Callback() {
                    public void surfaceChanged(SurfaceHolder holder, int format, int w,
                                               int h) {
                        Utils.LOG("surfaceChanged");
                        initSurface(holder);
                    }

                    public void surfaceCreated(SurfaceHolder holder) {
                        Utils.LOG("surfaceCreated");
                        initSurface(holder);
                    }

                    public void surfaceDestroyed(SurfaceHolder holder) {
                        Utils.LOG("surfaceDestroyed");
                    }

                    private void initSurface(SurfaceHolder h) {
                        Canvas c = null;
                        try {
                            Utils.LOG("initSurface");
                            c = h.lockCanvas();
                        } finally {
                            if (c != null)
                                h.unlockCanvasAndPost(c);
                        }
                    }
                });
                int VIDEO_HOLE_REAL = 258;
                v.getHolder().setFormat(VIDEO_HOLE_REAL);
            }
            return v;
        } else
            Utils.LOG("Not createSurfaceView");
        return null;
    }

    public static int[] String2ArrayInt(String str) {
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

    public static String ArrayInt2String(int[] data) {
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

    public boolean ExportAllProgramTable() {
        boolean rel = false;
        String folderpath = FindExportChannelUBSDiskFolderPath();
        if (Utils.Nofile.equals(folderpath))
            return false;

        Utils.LOG("<TvApi>  ExportAllProgramTable folderpath:" + folderpath);
        //Create Channel Table Folder
        File fp = new File(folderpath + Utils.CHDB_FOLDER);
        if (fp.exists())
            Utils.LOG("<TvApi>  fp.delete():" + fp.delete());
        Utils.LOG("<TvApi>  fp.mkdirs():" + fp.mkdirs());

        folderpath = fp.getPath() + "/";
        Utils.LOG("<TvApi> ExportAllProgramTable folderpath:" + folderpath);

        rel = sHisiapi.exportTVProg(folderpath);
        try {
            Runtime.getRuntime().exec("sync");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.LOG("<CVTE-AT> ExportAllProgramTable - rel=" + rel);
        return rel;
    }

    public boolean ImportAllProgramTable() {
        String path = FindImportChannelFilePath();
        Utils.LOG("<CVTE-AT> ImportAllProgramTable path=" + path);
        if (!Utils.CVTE_COMMON_CHDB_PATH.equals(path)) {
            File fp = new File(path + Utils.CHDB_FOLDER);
            if (!fp.exists()) {
                return false;
            }
            path = fp.getPath() + "/";
        }

        Utils.LOG("<CVTE-AT> final path=" + path);
        if (sHisiapi.importTVProg(path)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Utils.LOG("<CVTE-AT> Import Channel Finish Change Channel to 1");
                    SetInputSource(SourceEnum.EN_AT_ATV);
                    Sleep(1000);
                    ChangeChannelByID(1);
                }
            }, 0);
            return true;
        } else
            return false;
    }

    private String FindExportChannelUBSDiskFolderPath() {
        File usbroot = new File(Utils.USB_DEVICES_PATH);
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            Utils.LOG("FindExport FindATFilePath usbitems.size = " + usbitems.length);
            for (int i = 0; i < usbitems.length; i++) {
                if (!usbitems[i].getPath().startsWith(Utils.USB_DEVICES_PASS_SD_HEAD) ||
                        Utils.USB_DEVICES_PASS_SDCARD.equals(usbitems[i].getPath())) {
                    Utils.LOG("FindUBSCountByFileSystem = continue");
                    continue;
                }
//                Utils.LOG("FindATFilePath getPath = " + usbitems[i].getPath() + "/" + Utils.HISI_ATV_TABLE_NAME);
//                Utils.LOG("FindATFilePath canExecute = " + usbitems[i].canExecute() + " canRead=" + usbitems[i].canRead() + " canWrite=" + usbitems[i].canWrite());
                if ((usbitems[i].canExecute() == true) && (usbitems[i].canRead() == true) && (usbitems[i].canWrite() == true)) {
                    String getPath = usbitems[i].getPath() + "/";
                    Utils.LOG("FindATFilePath getPath = " + getPath);
                    return getPath;
                }
            }
        }
        return Utils.Nofile;
    }


    private String FindImportChannelFilePath() {

        //Step1 USB-Disk Channel Table
        File usbroot = new File(Utils.USB_DEVICES_PATH);
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            Utils.LOG("FindImport FindATFilePath usbitems.size = " + usbitems.length);
            for (int i = 0; i < usbitems.length; i++) {
                if (!usbitems[i].getPath().startsWith(Utils.USB_DEVICES_PASS_SD_HEAD) ||
                        Utils.USB_DEVICES_PASS_SDCARD.equals(usbitems[i].getPath())) {
                    Utils.LOG("FindUBSCountByFileSystem = continue, path=" + usbitems[i].getPath());
                    continue;
                }
                File ATFile = new File(usbitems[i].getPath());
                if (ATFile.exists()) {
                    Utils.LOG("FindATFilePath getPath = " + usbitems[i].getPath() + "/");
                    return usbitems[i].getPath() + "/";
                }
            }
        }
        Utils.LOG("FindImport Step2 use system default=" + Utils.CVTE_COMMON_CHDB_PATH);
        //Step2 FW Common Channel Table
        return Utils.CVTE_COMMON_CHDB_PATH;
    }

    public boolean GetMute() {
        return TvApi.eventSoundSpeakerOutIsMute();
    }

    public boolean SetMuteOff() {
        Utils.LOG("SetMute = false");
        TvApi.eventSoundSpeakerOutSetMute(false);
        return true;
    }

    public void CleanChannelData() {
        sHisiapi.CleanChannelData();
    }

    public void SetLEDLight(boolean b) {
        TvApi.eventSystemLedLightSetStatus(b ? EnumLedStatus.LED_STATUS_FULL_ON : EnumLedStatus.LED_STATUS_OFF);
    }

}
