package com.cvte.tv.at.util;

import android.util.Log;

/**
 * 用来定义常量和公共枚举的地方
 */
public class Utils {
    // Log TAG
    public static String TAG = "CvteATServiceAT";

    public static final String CVTEFG_INITOK = "/data/CVTEFG_INITOK.flag";

    public static final String DeviceName_Remote = "Hi keyboard";
    public static final String DeviceName_Keypad = "Hi Keypad";//"Hi Keypad";
    public static final String CVTE_PRESS_KEY = "persist.sys.persskey";
    public static final String CVTE_BROADCAST_FACKEY = "cvte.at.FactoryKey";
    public static final String CVTE_BROADCAST_RESETKEY = "cvte.at.ResetKey";
    public static final String CVTE_BROADCAST_FACKEYF_FLAG = "CvteFacKey";
    public static final String CVTE_BROADCAST_SCANEND = "cvte.at.PerCHEnd";
    public static final String CVTE_BROADCAST_USB_MOUNT = "android.intent.action.MEDIA_MOUNTED_CVTE";
    public static final String CUS_BROADCAST_FACKEY = "NewOp.at.FactoryKey";
    public static final String CUS_BROADCAST_FACKEYF_FLAG = "NewOpFacKey";
    public static final String CUS_BROADCAST_FACKEYCODE_FLAG = "NewOpFacKeyCode";
    public static final String Persist_MODEL_NAME = "ro.product.model";//"ro.build.model";
    public static final String Persist_WY_FAC_MODE = "fts.fac.factory_mode";
    public static final String FTS_AGINGMODE = "fts.fac.aging_test";
    public static final String Persist_BUILD_VERSION = "ro.umtv.sw.version";
    public static final String Persist_WY_BUILD_VERSION = "ro.build.version.incremental";
    public static final String Persist_RO_CVTE_AT_READ = "ro.boot.AT_Read";

    //Action
    public static final String UMBOOT_COMPLETED = "android.intent.action.UMBOOT_COMPLETED";
    public static final String ACT_ATShow = "cvte.factory.intent.action.ATScreenActivity";
    public static final String ACT_FactoryMenu = "cvte.factory.intent.action.FactoryMenu";
    public static final String ACT_DialogMenuScan = "cvte.factory.intent.action.DialogMenuScan";
    public static final String ACT_DialogmenutypeE = "cvte.factory.intent.action.DialogMenuTypeE";
    public static final String ACT_DialogMenuReset = "cvte.factory.intent.action.DialogMenuReset";
    public static final String ACT_DialogMenuUpgrade = "cvte.factory.intent.action.DialogMenuUpgrade";
    public static final String ACT_SWinformation = "cvte.factory.intent.action.SWinformation";
    public static final String ACT_WBAdjust = "cvte.factory.intent.action.WBAdjust";
    public static final String ACT_AgingMode = "cvte.factory.intent.action.AgingMode";

    public static final String ATPackageName = "com.cvte.tv.fac.autotest";
    public static final String ATActivityName = "com.cvte.tv.at.job.ATActivity";

    public static final String ETHERNET_MAC_ADDRESS_FILE_PATH = "/sys/class/net/eth0/address";

    public static final String CICardState_READY = "Sci State           :READY";
    public static final String CICardState_RX = "Sci State           :RX";
    public static final String CICardState_NOCARD = "Sci State           :NOCARD";
    public static final String CICardState_INACTIVECARD = "Sci State           :INACTIVECARD";

    public static final String CICardStateInfo = "/mnt/cicarddetect.txt";
    public static final String AUTO_START_FILE_NAME = "FAC_BOOT_HIS.cvt";
    public static final String USB_BUS_PATH = "/dev/bus/usb";
    public static final String USB_DEVICES_PATH = "/mnt/";
    public static final String USB_DEVICES_PASS_INTERSDCARD = "/mnt/internal_sdcard";
    public static final String USB_DEVICES_PASS_SDCARD = "/mnt/sdcard";
    public static final String USB_DEVICES_PASS_SD_HEAD = "/mnt/sd";
    public static final String USB_DEVICES_PASS_tmp = "/mnt/tmp";
    public static final String USB_DEVICES_WIFI1 = "/sys/devices/platform/hiusb-ehci.0/usb1/1-1/1-1:1.0";// WIFI设备
    public static final String USB_DEVICES_WIFI2 = "/sys/devices/platform/hiusb-ohci.0/usb2/2-1/2-1:1.0/";// WIFI设备
    public static final String USB_DEVICES_BT = "/sys/devices/platform/hiusb-ehci.0/usb1/1-2/1-2.1";// BT设备
    public static final String USB_DEVICES_USB20 = "/sys/devices/platform/hiusb-ehci.0/usb1/1-2";// USB2.0设备
    public static final String USB_DEVICES_USB30_20 = "/sys/devices/platform/hiusb-xhci.0/usb3/3-1";// USB3.0接口插入USB2.0设备
    public static final String USB_DEVICES_USB30_30 = "/sys/devices/platform/hiusb-xhci.0/usb4/4-1";// USB3.0接口插入USB3.0设备
    public static final String SDCARD_DEVICES_DEV = "/dev/block/mmcblk1";
    public static final String SDCARD_DEVICES_PATH = "/mnt/mmcblk1p1";
    public static final String CONFIG_FILE_DIR = "/msd828_config";
    public static final String FILE_NewOp_WBMode = "/sdcard/isNewOpWBMode";
    public static final String FILE_LeaveATFlag = "/sdcard/LeaveATFlag";
    public static final String FILE_WBIRFlag = "/sdcard/WBIRFlag";//link with policy jni
    public static final String FILE_NewOp_AT_START = "/sdcard/isNewOpATStart";//link with policy jni

    public static final String HISI_ATV_TABLE_NAME = "HISI_ATV_CHANNEL_TABLE.json";
    public static final String HISI_DTV_CHANNEL_DB = "HISI_DTV_CHANNEL_DB.db";
    public static final String CVTE_COMMON_CHDB_PATH = "/system/etc/CVTE_COMMOM_CHANNEL_TABLE/";
    public static final String CHDB_FOLDER = "UMCHTab_HISI";

    public static final String EndBin = ".bin";
    public static final String HDCP_FOLDER_NAME = "hdcp_key";
    public static final String HDCP_FILE_NAME = "hisHDCPkey00000001.bin";
    public static final String HDCP_FILE_HEAD = "hisHDCPkey";
    public static final String HDCP_NUM_BAR = "00000000.bin";

    public static final String HDCP2_FOLDER_NAME = "hdcp2_key";
    public static final String HDCP2_FILE_NAME = "hisHDCP22G00000001.bin";
    public static final String HDCP2_FILE_HEAD = "hisHDCP2";//include HDCP20 and HDCP22
    public static final String HDCP2G_HEAD = "2G";//include HDCP20 and HDCP22
    public static final String HDCP0G_HEAD = "0G";//include HDCP20 and HDCP22
    public static final String HDCP2_NUM_BAR = "00000000.bin";

    public static final String MAC_FOLDER_NAME = "MAC";
    public static final String MAC_FILE_NAME = "MAC0000.bin";
    public static final String MAC_FILE_HEAD = "MAC";

    public static final String MAC_CLEAN_ENV = "";
    public static final String MAC_DEFAULT = "a0:b0:c0:00:00:00";
    public static final String MAC_FULL = "ff:ff:ff:ff:ff:ff";
    public static final String MAC_EMPTY = "ff:ff:ff:ff:ff:f0";
    public static final String MAC_CHECKSUM_ERROR = "ff:ff:ff:ff:ff:f1";
    public static final String MAC_LENGTH_NG = "ff:ff:ff:ff:ff:f2";
    public static final String MAC_NETCARD_DIFF_DTORAGE = "ff:ff:ff:ff:ff:f3";

    public static final int[] MAC_DEFAULT_INT = {0xa0, 0xb0, 0xc0, 0x00, 0x00, 0x00};
    public static final int[] MAC_FULL_INT = {0xff, 0xff, 0xff, 0xff, 0xff, 0xff};
    public static final int[] MAC_EMPTY_INT = {0xff, 0xff, 0xff, 0xff, 0xff, 0xf0};
    public static final int[] MAC_CHECKSUM_ERROR_INT = {0xff, 0xff, 0xff, 0xff, 0xff, 0xf1};
    public static final int[] MAC_LENGTH_NG_INT = {0xff, 0xff, 0xff, 0xff, 0xff, 0xf2};
    public static final int[] MAC_NETCARD_DIFF_DTORAGE_INT = {0xff, 0xff, 0xff, 0xff, 0xff, 0xf3};

    public static final int KeyPad_Error = 0x0F;
    public static final int HDCP_KSV_Space = 5;
    public static final int MACLen = 6;
    public static final byte HDCP_KSV_def[] = {0, 0, 0, 0, 0};//burn Flag

    public static final String CENV_AT_B = "CEnv_AgingMode";
    public static final String CENV_AT_B_2 = "CEnv_AgingModeStage2";
    public static final String CENV_POWER_MODE = "CEnv_PowerMode";
    public static final String MACENV_Ethernet = "macaddr";//828 is macaddr

    public static final String POWER_MODE_DIRECT = "0";
    public static final String POWER_MODE_MEMORY = "1";
    public static final String POWER_MODE_SECONDARY = "2";

    public static final String NoFun = "NoFun";
    public static final String NoName = "NoName";
    public static final String Nofile = "NoFile";
    public static final String NoDisk = "NoDisk";
    public static final String NoFunction = "NoFunction";
    public static final String NoStr = "NoStr";
    public static final String NoMode = "NoMode";
    public static final String CENV_ON = "1";
    public static final String CENV_OFF = "0";

    public static final int nodata = -1;
    public static final String BurnKey = "burnkey";
    public static final int BurnByUSBFlag[] = {0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0xFF};//burn Flag

    public static final String NoUUID = "----";
    public static final String UUIDFile = "/tmp/uuid.txt";
    public static final String WIFIOK = "/mnt/cvte_wifiok.flag";
    public static final String WIFIFail = "/mnt/cvte_wififail.flag";

    public static final String OK = "OK";
    public static final String FAIL = "FAIL";
    public static final String DOING = "DOING";

    public static final String RelBool = ":0";
    public static final String RelOnlyOne = ":1";
    public static final String RelSameDef = ":2";

    public static final int DevCount = 10;
    public static final int USB20_OK = 0;
    public static final int USB20_NG = 1;
    public static final int NO_DEVICE = 2;
    public static final int SDCard_OK = 3;
    public static final int SDCard_NG = 4;
    public static final int USB30_OK = 5;
    public static final int USB30_NG = 6;

    public static void LOG(String info) {
        Log.e(TAG, info);
    }

    public enum EnumLedStatus {
        LED_STATUS_OFF,
        LED_STATUS_GREEN_ON,
        LED_STATUS_RED_ON,
        LED_STATUS_FULL_ON
    }

    public enum FacType {
        TPV,
        TCL,

        MAX,
    }

//    public enum SetType {
//        W40F,
//        W43F,//TPV
//        W49F,//TCL
//        W50J,
//        W50A,
//        W50T,
//        WTV55K1T,
//        WTV55K1X,
//        WTV55K1G,
//    }

    public enum HDCP_E {
        HDCP1X,
        HDCP2X
    }

    public enum EnumUpgradeStatus {
        // status fail
        E_UPGRADE_FAIL("Fail"),
        // status success
        E_UPGRADE_SUCCESS("Sucess"),
        // file not found
        E_UPGRADE_FILE_NOT_FOUND("File Not Found!"),
        // file not found
        E_UPGRADE_PLEASE_PLUG_USB("Please Plug USB!"),
        // delete fail
        E_UPGRADE_FILE_DELETE_FAIL("Can't delete file!");

        private String name;

        private EnumUpgradeStatus(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum HDCPMAC_E {
        Option_HDCPKey("Burn HDCP"),
        Option_HDCPKey20G("Burn HDCP2.0"),
        Option_MACAddr("Burn MAC");

        private String name;

        private HDCPMAC_E(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum EnumKeyPad {
        KEY_PAD_K0,
        KEY_PAD_K1,
        KEY_PAD_K2,
        KEY_PAD_K3,
        KEY_PAD_K4,
        KEY_PAD_K5,
        KEY_PAD_K6,
        KEY_PAD_K7,
    }

    public enum EnumChannelType {
        CHANNEL_TYPE_VIDEO, CHANNEL_TYPE_AUDIO, CHANNEL_TYPE_DATA,
    }

    public enum EnumBurningKeyStatus {
        BURNING_KEY_STATUS_SUCCESS, BURNING_KEY_STATUS_KEY_INVAILD, BURNING_KEY_STATUS_KEY_DATA_NULL,
    }

    public enum EnumFactoryDataType {
        FACTORY_DATA_TYPE_SN, FACTORY_DATA_TYPE_UUID, FACTORY_DATA_TYPE_DEVICEID,
    }

    public enum EnumInputStatus {
        INPUT_STATUS_CONNECTED, INPUT_STATUS_DISCONNECT, INPUT_STATUS_LOCKED, INPUT_STATUS_HIDE
    }

    public enum EnumAntennaType {
        ANTENNA_TYPE_AIR,
        ANTENNA_TYPE_CABLE,
        ANTENNA_TYPE_SATELLITE,
        ANTENNA_TYPE_ALL,
    }

    public enum EnumDataImportsAndExports {
        DATA_FACTORY_CHANNELS,
        DATA_CUSTOMER_CHANNELS,
        DATA_PQ,
        DATA_LOGO,
        DATA_MUSIC,
        DATA_PANEL_CONFIG,
        DATA_SYSTEM_UPGRADE_IMAGE,
    }

    public enum EnumInputSourceCategory {
        INPUTSOURCE_TV,
        INPUTSOURCE_ATV,
        INPUTSOURCE_DVBT,
        INPUTSOURCE_DTMB,
        INPUTSOURCE_DVBC,
        INPUTSOURCE_DVBS,
        INPUTSOURCE_ATSC,
        INPUTSOURCE_HDMI,
        INPUTSOURCE_AV,
        INPUTSOURCE_SVIDEO,
        INPUTSOURCE_YPBPR,
        INPUTSOURCE_PC,
        INPUTSOURCE_SCART,
        INPUTSOURCE_APP,
    }

    public enum LED_E {
        RED,
        GREEN,
    }

    public enum UART_E {
        Init,
        Final,
    }

    public enum ATSC_E {
        AIR,
        CABLE,
    }

    public enum BurnData_E {
        EN_NONE_KEY,
        EN_HDCP_KEY,
        EN_CI_PLUS_KEY,
        EN_HDCP_20G,
        EN_HDCP_22G,
        EN_CUS_1,
        EN_CUS_2,
        EN_CUS_3,
        EN_CUS_4,
        EN_CUS_5,
        EN_KEY_MAX,
    }

    public enum KEYDATA_E {
        Option_HDCPKey("Burn HDCP"),
        Option_HDCPKey20G("Burn HDCP2.0"),
        Option_HDCPKey22G("Burn HDCP2.2"),
        Option_CIPlus("Burn CI+"),
        Option_Cus1("Burn Cus1"),
        Option_Cus2("Burn Cus2"),
        Option_Cus3("Burn Cus3"),
        Option_Cus4("Burn Cus4"),
        Option_Cus5("Burn Cus5"),
        Option_Max("Burn Cus5");

        private String name;

        private KEYDATA_E(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum UART_DEBUG {
        UART_CVTE,
        UART_CUS,
        UART_CVTEWB,
        UART_FINISH,
    }


    public enum ActionType {
        Act_NULL,
        Act_AgingMode,
        Act_CvteUARTAT,
        Act_CustAT,
        Act_ImportCUSCH,
        Act_ImportCH,
        Act_ExportCH,
        Act_WBAdjust,
    }

    // all project use this function
    public static enum SourceEnum {
        EN_AT_ATV("atv"),
        EN_AT_DTV("dtv"),
        EN_AT_DVBS("dvbs"),
        EN_AT_DVBC("dvbc"),
        EN_AT_DVBT1("dvbt1"),
        EN_AT_DVBT2("dvbt2"),
        EN_AT_VGA1("vga1"),
        EN_AT_VGA2("vga2"),
        EN_AT_HDMI1("hdmi1"),
        EN_AT_HDMI2("hdmi2"),
        EN_AT_HDMI3("hdmi3"),
        EN_AT_HDMI4("hdmi4"),
        EN_AT_HDMI5("hdmi5"),
        EN_AT_SCART1("scart1"),
        EN_AT_SCART2("scart2"),
        EN_AT_AV1("av1"),
        EN_AT_AV2("av2"),
        EN_AT_AV3("av3"),
        EN_AT_AV4("av4"),
        EN_AT_YPBPR1("ypbpr1"),
        EN_AT_YPBPR2("ypbpr2"),
        EN_AT_YPBPR3("ypbpr3"),
        EN_AT_YPBPR4("ypbpr4"),
        EN_AT_USB1("usb1"),
        EN_AT_USB2("usb2"),
        EN_AT_USB3("usb3"),
        EN_AT_USB4("usb4"),
        EN_AT_SVIDEO1("svideo1"),
        EN_AT_SVIDEO2("svideo2"),
        EN_AT_DVD("dvd"),
        EN_AT_OTHER("other"),
        EN_AT_VGA3("vga3"),
        EN_AT_VGA4("vga4"),
        EN_AT_SOURCE_MAX("source_max");

        private String name;

        private SourceEnum(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static int getSize() {
            return SourceEnum.EN_AT_SOURCE_MAX.ordinal();
        }

        public static boolean isATV(SourceEnum src) {
            return (src == EN_AT_ATV);
        }

        public static boolean isDTV(SourceEnum src) {
            return ((src == EN_AT_DTV) ||
                    (src == EN_AT_DVBS) ||
                    (src == EN_AT_DVBC) ||
                    (src == EN_AT_DVBT1) || (src == EN_AT_DVBT2));
        }

        public static boolean isVGA(SourceEnum src) {
            return ((src == EN_AT_VGA1) || (src == EN_AT_VGA2));
        }

        public static boolean isAV(SourceEnum src) {
            return ((src == EN_AT_AV1) ||
                    (src == EN_AT_AV2) ||
                    (src == EN_AT_AV3) || (src == EN_AT_AV4));
        }

        public static boolean isHDMI(SourceEnum src) {
            return ((src == EN_AT_HDMI1) ||
                    (src == EN_AT_HDMI2) ||
                    (src == EN_AT_HDMI3) ||
                    (src == EN_AT_HDMI4) || (src == EN_AT_HDMI5));
        }

        public static boolean isSCART(SourceEnum src) {
            return ((src == EN_AT_SCART1) || (src == EN_AT_SCART2));
        }

        public static boolean isYPBPR(SourceEnum src) {
            return ((src == EN_AT_YPBPR1) ||
                    (src == EN_AT_YPBPR2) ||
                    (src == EN_AT_YPBPR3) || (src == EN_AT_YPBPR4));
        }

        public static boolean isUSB(SourceEnum src) {
            return ((src == EN_AT_USB1) ||
                    (src == EN_AT_USB2) ||
                    (src == EN_AT_USB3) || (src == EN_AT_USB4));
        }

        public static boolean isSVIDEO(SourceEnum src) {
            return ((src == EN_AT_SVIDEO1) || (src == EN_AT_SVIDEO2));
        }

        public static boolean isDVD(SourceEnum src) {
            return (src == EN_AT_DVD);
        }

        public static boolean isOther(SourceEnum src) {
            return (src == EN_AT_OTHER);
        }
    }

    public static enum ENUM_KEYPAD_KEYCODE {
        EN_KEYPAD_KEYCODE_K0, // CVTKEY_CHANNEL_MINUS_SUPERNOVA
        EN_KEYPAD_KEYCODE_K1, // CVTKEY_VOLUME_PLUS_SUPERNOVA
        EN_KEYPAD_KEYCODE_K2, // CVTKEY_VOLUME_MINUS_SUPERNOVA
        EN_KEYPAD_KEYCODE_K3, // CVTKEY_MENU_SUPERNOVA
        EN_KEYPAD_KEYCODE_K4, // CVTKEY_CHANNEL_PLUS_SUPERNOVA
        EN_KEYPAD_KEYCODE_K5, // CVTKEY_INPUT_SOURCE_SUPERNOVA
        EN_KEYPAD_KEYCODE_K6, // CVTKEY_POWER_SUPERNOVA
        EN_KEYPAD_KEYCODE_K7, // CVTKEY_NONE_SUPERNOVA
    }

    ;

    public enum AgingColor {
        white,
        Red,
        Green,
        blue,
        black,
    }

    public enum DialogPage {
        id_facmenu_channel_export,
        id_facmenu_channel_import,
    }

    public enum Transdirection {
        CHExport,
        CHImport,
    }

    /**
     * 工厂遥控器的IR Code，也就是遥控器的物理键值
     */
    public enum CvteIRRawE {
        IRCODE_KEY_CVT_FAC_ATV(0x80),
        IRCODE_KEY_CVT_FAC_DTV(0x81),
        IRCODE_KEY_CVT_FAC_VGA(0x82),
        IRCODE_KEY_CVT_FAC_MEDIA(0x83),
        IRCODE_KEY_CVT_FAC_AV1(0x84),
        IRCODE_KEY_CVT_FAC_YPBPR1(0x85),
        IRCODE_KEY_CVT_FAC_HDMI1(0x86),
        IRCODE_KEY_CVT_FAC_SCART1(0x87),
        IRCODE_KEY_CVT_FAC_SVIDEO1(0x88),
        IRCODE_KEY_CVT_FAC_DVD(0x89),
        IRCODE_KEY_CVT_FAC_HDMI2(0x8A),
        IRCODE_KEY_CVT_FAC_HDMI3(0x8B),
        IRCODE_KEY_CVT_FAC_HDMI4(0x8C),
        IRCODE_KEY_CVT_FAC_AV2(0x8D),
        IRCODE_KEY_CVT_FAC_AV3(0x8E),
        IRCODE_KEY_CVT_FAC_YPBPR2(0x8F),
        IRCODE_KEY_CVT_FAC_SCART2(0x90),
        IRCODE_KEY_CVT_FAC_IO_TEST_ON(0x91),
        IRCODE_KEY_CVT_FAC_IO_TEST_OFF(0x92),
        IRCODE_KEY_CVT_FAC_TEST_CI_PLUS(0x96),
        IRCODE_KEY_CVT_FAC_DVBS2(0x99),

        IRCODE_KEY_CVT_FAC_AT_SHOW(0xa0),
        IRCODE_KEY_CVT_FAC_F1(0x08),
        IRCODE_KEY_CVT_FAC_AGING(0x0b),
        IRCODE_KEY_CVT_FAC_MENU_SHOW(0x18);

        private int val;

        CvteIRRawE(int val) {
            this.val = val;
        }

        public int toInt() {
            return this.val;
        }
    }

    /**
     * 工厂遥控器的IR Code，也就是遥控器的物理键值
     */
    public static final int CvteIRRaw_ATV = 0x80;
    public static final int CvteIRRaw_DTV = 0x81;
    public static final int CvteIRRaw_VGA = 0x82;
    public static final int CvteIRRaw_MEDIA = 0x83;
    public static final int CvteIRRaw_AV1 = 0x84;
    public static final int CvteIRRaw_YPBPR1 = 0x85;
    public static final int CvteIRRaw_HDMI1 = 0x86;
    public static final int CvteIRRaw_SCART1 = 0x87;
    public static final int CvteIRRaw_SVIDEO1 = 0x88;
    public static final int CvteIRRaw_DVD = 0x89;
    public static final int CvteIRRaw_HDMI2 = 0x8A;
    public static final int CvteIRRaw_HDMI3 = 0x8B;
    public static final int CvteIRRaw_HDMI4 = 0x8C;
    public static final int CvteIRRaw_AV2 = 0x8D;
    public static final int CvteIRRaw_AV3 = 0x8E;
    public static final int CvteIRRaw_YPBPR2 = 0x8F;
    public static final int CvteIRRaw_SCART2 = 0x90;
    public static final int CvteIRRaw_IO_TEST_ON = 0x91;
    public static final int CvteIRRaw_IO_TEST_OFF = 0x92;
    public static final int CvteIRRaw_TEST_CI_PLUS = 0x96;
    public static final int CvteIRRaw_DVBS2 = 0x99;

    public static final int CvteIRRaw_AT_SHOW = 0xa0;
    public static final int CvteIRRaw_F1 = 0x08;
    public static final int CvteIRRaw_AGING = 0x0b;
    public static final int CvteIRRaw_MENU_SHOW = 0x18;
}
