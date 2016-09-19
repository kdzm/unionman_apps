
package com.cvte.tv.at.util;
/**
 * 工厂遥控器的Keyevent Code，也就是经过转化之后的Android Keyevent键值
 * 这份表格继承于818的UART AT，其中的Panda和BOE是818上用到的，在M6上没用到
 */
public class CvteKey {

    public static final int KEYCODE_KEY_NULL                        = 0x0;
    //Renlijia.20140522 move from M6 for Cvte All Factory Key

    //Cvte Renlijia.20141117 add for All Factory Key
    public static final int KEYCODE_KEY_CVT_FAC_ATV                 = 0x300;
    public static final int KEYCODE_KEY_CVT_FAC_DTV                 = 0x301;
    public static final int KEYCODE_KEY_CVT_FAC_VGA                 = 0x302;
    public static final int KEYCODE_KEY_CVT_FAC_MEDIA               = 0x303;
    public static final int KEYCODE_KEY_CVT_FAC_AV1                 = 0x304;
    public static final int KEYCODE_KEY_CVT_FAC_YPBPR1              = 0x305;
    public static final int KEYCODE_KEY_CVT_FAC_HDMI1               = 0x306;
    public static final int KEYCODE_KEY_CVT_FAC_SCART1              = 0x307;
    public static final int KEYCODE_KEY_CVT_FAC_SVIDEO1             = 0x308;
    public static final int KEYCODE_KEY_CVT_FAC_DVD                 = 0x309;
    public static final int KEYCODE_KEY_CVT_FAC_HDMI2               = 0x30A;
    public static final int KEYCODE_KEY_CVT_FAC_HDMI3               = 0x30B;
    public static final int KEYCODE_KEY_CVT_FAC_HDMI4               = 0x30C;
    public static final int KEYCODE_KEY_CVT_FAC_AV2                 = 0x30D;
    public static final int KEYCODE_KEY_CVT_FAC_AV3                 = 0x30E;
    public static final int KEYCODE_KEY_CVT_FAC_YPBPR2              = 0x30F;
    public static final int KEYCODE_KEY_CVT_FAC_SCART2              = 0x310;
    public static final int KEYCODE_KEY_CVT_FAC_IO_TEST_ON          = 0x311;
    public static final int KEYCODE_KEY_CVT_FAC_IO_TEST_OFF         = 0x312;
    public static final int KEYCODE_KEY_CVT_FAC_TEST_CI_PLUS        = 0x313;
    public static final int KEYCODE_KEY_CVT_FAC_SVIDEO2             = 0x314;
    public static final int KEYCODE_KEY_CVT_FAC_WIFI_TEST           = 0x315;
    public static final int KEYCODE_KEY_CVT_FAC_EHTER_TEST          = 0x316;
    public static final int KEYCODE_KEY_CVT_FAC_MAC_H_TEST          = 0x317;
    public static final int KEYCODE_KEY_CVT_FAC_MAC_L_TEST          = 0x318;
    public static final int KEYCODE_KEY_CVT_FAC_AT_SHOW             = 0x319;
    public static final int KEYCODE_KEY_CVT_FAC_F1                  = 0x31A;
    public static final int KEYCODE_KEY_CVT_FAC_AGING               = 0x31B;
    public static final int KEYCODE_KEY_CVT_FAC_VERSION             = 0x31C;
    public static final int KEYCODE_KEY_CVT_FAC_AUTO_TUNING         = 0x31D;
    public static final int KEYCODE_KEY_CVT_FAC_FACTORY_RESET       = 0x31E;
    public static final int KEYCODE_KEY_CVT_FAC_ERASE_HDCP          = 0x31F;
    public static final int KEYCODE_KEY_CVT_FAC_ERASE_MAC           = 0x320;
    public static final int KEYCODE_KEY_CVT_FAC_ERASE_CI_PLUS       = 0x321;
    public static final int KEYCODE_KEY_CVT_FAC_MENU_SHOW           = 0x322;
    public static final int KEYCODE_KEY_CVT_FAC_ADC_ADJUST          = 0x323;
    public static final int KEYCODE_KEY_CVT_FAC_VGA_SYNC            = 0x324;
    public static final int KEYCODE_KEY_CVT_FAC_ONEKEYVOLUME        = 0x325;
    public static final int KEYCODE_KEY_CVT_FAC_COLTEMP_LOOP        = 0x326;
    public static final int KEYCODE_KEY_CVT_FAC_PICMODE_LOOP        = 0x327;
    public static final int KEYCODE_KEY_CVT_FAC_CUS_DEFCHTAB        = 0x328;
    public static final int KEYCODE_KEY_CVT_FAC_CUS_ENTERTEST       = 0x329;// Enter Customer Factory Test Use in BOE, Panda
    public static final int KEYCODE_KEY_CVT_FAC_BLUETEST            = 0x32A;// Enter Customer Factory Test Use in BOE, Panda
    public static final int KEYCODE_KEY_CVT_FAC_CUS_FINALRESET      = 0x32B;// CVTE Renlijia.20140307 modify for Customer Final Reset
    public static final int KEYCODE_KEY_CVT_FAC_RESET_MENU_SHOW     = 0x32C;
    public static final int KEYCODE_KEY_CVT_FAC_CALIBRATION1        = 0x32D;
    public static final int KEYCODE_KEY_CVT_FAC_CALIBRATION2        = 0x32E;
    public static final int KEYCODE_KEY_CVT_FAC_F3                  = 0x32F;
    public static final int KEYCODE_KEY_CVT_FAC_10_P                = 0x330;// -/-- KEYCODE_KEY
    public static final int KEYCODE_KEY_CVT_FAC_DOT                 = 0x331;// . KEYCODE_KEY, in Fac remote point KEYCODE_KEY
    public static final int KEYCODE_KEY_CVT_FAC_AUTO_ADJUST         = 0x332;// Auto Adjust VGA
    public static final int KEYCODE_KEY_CVT_FAC_NICAM               = 0x334;// NICAM
    public static final int KEYCODE_KEY_CVT_FAC_ZOOM                = 0x335;
    public static final int KEYCODE_KEY_CVT_FAC_PVR                 = 0x336;
    public static final int KEYCODE_KEY_CVT_FAC_EPG                 = 0x337;
    public static final int KEYCODE_KEY_CVT_FAC_UPGRADE_PAGE        = 0x338;
    public static final int KEYCODE_KEY_CVT_CUS_FAC_OPEN_UART       = 0x339;
    public static final int KEYCODE_KEY_CVT_CUS_FAC_OPEN_ADJUST     = 0x33A;
    public static final int KEYCODE_KEY_CVT_CUS_FAC_VOL_40          = 0x33B;
    public static final int KEYCODE_KEY_CVT_CUS_FAC_VOL_60          = 0x33C;
    public static final int KEYCODE_KEY_CVT_CUS_FAC_VOL_80          = 0x33D;
    public static final int KEYCODE_KEY_CVT_CUS_FAC_EXIT_AGING      = 0x33E;
    public static final int KEYCODE_KEY_CVT_CUS_FAC_NETSETTING      = 0x33F;

    //used in 818 BOE Factory Key Group Start  = 0x300, now can remove them
    public static final int KEYCODE_KEY_BOE_FAC_OSDRESET            = 0x3f1;
    public static final int KEYCODE_KEY_BOE_FAC_FASTSCAN_ATV        = 0x3f2;
    public static final int KEYCODE_KEY_BOE_FAC_FASTSCAN_DTV        = 0x3f3;
    public static final int KEYCODE_KEY_BOE_FAC_ATVDTVLOOP          = 0x3f4;

    //used in 818 Panda Factory Key Group Start  = 0x320, now can remove them
    public static final int KEYCODE_KEY_PANDA_FAC_EXIT_AGING        = 0x3fA;
    public static final int KEYCODE_KEY_PANDA_FAC_NETSETTING        = 0x3fB;

    //customer define
    public static final int KEYCODE_KEY_CVT_FAC_CH_REFRESH          = 0x500;
    //Keypad Group
    public static final int KEYCODE_KEY_CVT_FAC_KEYPAD_POWER        = 0xff0;
    public static final int KEYCODE_KEY_CVT_FAC_KEYPAD_TV_INPUT     = 0xff1;
    public static final int KEYCODE_KEY_CVT_FAC_KEYPAD_DPAD_DOWN    = 0xff2;
    public static final int KEYCODE_KEY_CVT_FAC_KEYPAD_DPAD_UP      = 0xff3;
    public static final int KEYCODE_KEY_CVT_FAC_KEYPAD_DPAD_LEFT    = 0xff4;
    public static final int KEYCODE_KEY_CVT_FAC_KEYPAD_DPAD_RIGHT   = 0xff5;
    public static final int KEYCODE_KEY_CVT_FAC_KEYPAD_MENU         = 0xff6;


    public static final int KEYCODE_KEY_CVT_FAC_END = KEYCODE_KEY_CVT_CUS_FAC_NETSETTING + 1;

    public static final int KEYCODE_KEY_CVT_FAC_F1_UM               = 0x258;

    //TPV Row Remote Key Data
    public static final int IRKEY_POWER                 = 0xFF;
    public static final int IRKEY_TPV_FAC_1             = 0x0F;
    public static final int IRKEY_TPV_FAC_2             = 0x12;
    public static final int IRKEY_TPV_FAC_3             = 0x4A;
    public static final int IRKEY_TPV_FAC_4             = 0x30;
    public static final int IRKEY_TPV_FAC_5             = 0x10;
    public static final int IRKEY_TPV_FAC_6             = 0x11;
    public static final int IRKEY_TPV_FAC_7             = 0x32;
    public static final int IRKEY_TPV_FAC_8             = 0x35;
    public static final int IRKEY_TPV_FAC_9             = 0x36;
    public static final int IRKEY_TPV_FAC_0             = 0x33;
    public static final int IRKEY_MENU                  = 0x21;
    public static final int IRKEY_TPV_FAC_ChannelUp     = 0x56;
    public static final int IRKEY_TPV_FAC_ChannelDown   = 0x55;
    public static final int IRKEY_UP                    = 0x26;
    public static final int IRKEY_DOWN                  = 0x27;
    public static final int IRKEY_TPV_FAC_MUTE          = 0x37;
    public static final int IRKEY_TPV_FAC_PIC           = 0x46;
    public static final int IRKEY_TPV_FAC_VGA           = 0x7B;
    public static final int IRKEY_TPV_FAC_TV            = 0x3F;
    public static final int IRKEY_TPV_FAC_USB           = 0x70;
    public static final int IRKEY_TPV_FAC_CVBS          = 0x00;
    public static final int IRKEY_TPV_FAC_HDMI          = 0x02;
    public static final int IRKEY_TPV_FAC_YPBPR_SCART   = 0x01;
    public static final int IRKEY_SELECT                = 0x0E;
    public static final int IRKEY_TPV_FAC_TEST          = 0x6D;
    public static final int IRKEY_TPV_FAC_FAC           = 0x3E;
    public static final int IRKEY_TPV_FAC_CTC           = 0x03;
    public static final int IRKEY_TPV_FAC_CSM           = 0x50;
    public static final int IRKEY_TPV_FAC_VIRGIN        = 0x51;
    public static final int IRKEY_TPV_FAC_RST           = 0x5A;
    public static final int IRKEY_LEFT                  = 0x5D;
    public static final int IRKEY_RIGHT                 = 0x5E;
    public static final int IRKEY_TPV_FAC_VOL_MAX       = 0x5F;
    public static final int IRKEY_TPV_FAC_VOL_BUZZ      = 0x60;
    public static final int IRKEY_TPV_FAC_ADC           = 0x31;
    public static final int IRKEY_TPV_FAC_PATTERN       = 0x62;
    public static final int IRKEY_TPV_FAC_EDID_WP       = 0x69;
    public static final int IRKEY_TPV_FAC_PRE_CH        = 0x6B;
    public static final int IRKEY_TPV_FAC_BLK           = 0x0D;
    public static final int IRKEY_TPV_FAC_DCR           = 0x34;
    public static final int IRKEY_TPV_FAC_CC_TT         = 0x4B;
    public static final int IRKEY_TPV_FAC_AUDIO         = 0x53;
    public static final int IRKEY_TPV_FAC_CLONE         = 0x72;
    public static final int IRKEY_TPV_FAC_CLK           = 0x73;
    public static final int IRKEY_TPV_FAC_REGIONID      = 0x74;
    public static final int IRKEY_APP_BACK              = 0x75;
    public static final int IRKEY_TPV_FAC_3D_2D         = 0x22;
    public static final int IRKEY_TPV_FAC_ARC           = 0x23;
    public static final int IRKEY_TPV_FAC_CI_IRKEY      = 0x24;
    public static final int IRKEY_TPV_FAC_LIGHT_SENSOR  = 0x25;
    public static final int IRKEY_TPV_FAC_RS232         = 0x28;
    public static final int IRKEY_TPV_FAC_AGINGMODE     = 0x54;
    public static final int IRKEY_TPV_FAC_CH_SCAN       = 0x57;
    public static final int IRKEY_TPV_FAC_WIFI_TEST     = 0x29;
    public static final int IRKEY_TPV_FAC_ETHERNET_RJ45 = 0x71;

    //Cvte Fac Remote Raw Key
    public static final int CVTE_IRKEY_1                = 0x02;
    public static final int CVTE_IRKEY_2                = 0x12;
    public static final int CVTE_IRKEY_3                = 0x06;
    public static final int CVTE_IRKEY_4                = 0x16;
    public static final int CVTE_IRKEY_5                = 0x03;
    public static final int CVTE_IRKEY_6                = 0x13;
    public static final int CVTE_IRKEY_7                = 0x07;
    public static final int CVTE_IRKEY_8                = 0x17;
    public static final int CVTE_IRKEY_9                = 0x00;
    public static final int CVTE_IRKEY_0                = 0x10;
    public static final int CVTE_IRKEY_POWER            = 0x01;
    public static final int CVTE_IRKEY_INFO             = 0x05;
    public static final int CVTE_IRKEY_AT_F1_RESET      = 0x08;
    public static final int CVTE_IRKEY_AT_USB           = 0x09;
    public static final int CVTE_IRKEY_AT_AUTO_TUNING   = 0x0a;
    public static final int CVTE_IRKEY_AT_AGING_MODE    = 0x0b;
    public static final int CVTE_IRKEY_AT_VERSION       = 0x0e;
    public static final int CVTE_IRKEY_AT_WINDOW_SHOW   = 0x0f;
    public static final int CVTE_IRKEY_MUTE             = 0x11;
    public static final int CVTE_IRKEY_AT_F3            = 0x14;
    public static final int CVTE_IRKEY_AT_F2            = 0x18;
    public static final int CVTE_IRKEY_BLUE             = 0x1c;
    public static final int CVTE_IRKEY_RED              = 0x1d;
    public static final int CVTE_IRKEY_GREEN            = 0x1e;
    public static final int CVTE_IRKEY_YELLOW           = 0x1f;
    public static final int CVTE_IRKEY_AT_WIFI_TEST     = 0x3c;
    public static final int CVTE_IRKEY_SCREEN_SHOT      = 0x3f;
    public static final int CVTE_IRKEY_INPUT_SOURCE     = 0x40;
    public static final int CVTE_IRKEY_TT_TTX           = 0x41;
    public static final int CVTE_IRKEY_VOLUME_PLUS      = 0x44;
    public static final int CVTE_IRKEY_VOLUME_MINUS     = 0x45;
    public static final int CVTE_IRKEY_MENU             = 0x46;
    public static final int CVTE_IRKEY_LEFT             = 0x47;
    public static final int CVTE_IRKEY_CHANNEL_PLUS     = 0x48;
    public static final int CVTE_IRKEY_CHANNEL_MINUS    = 0x49;
    public static final int CVTE_IRKEY_APP_BACK         = 0x4a;
    public static final int CVTE_IRKEY_RIGHT            = 0x4b;
    public static final int CVTE_IRKEY_EPG              = 0x4d;
    public static final int CVTE_IRKEY_DOWN             = 0x50;
    public static final int CVTE_IRKEY_ASPECT           = 0x51;
    public static final int CVTE_IRKEY_NICAM_MTS        = 0x55;
    public static final int CVTE_IRKEY_UP               = 0x56;
    public static final int CVTE_IRKEY_SELECT           = 0x57;
    public static final int CVTE_IRKEY_HOME             = 0x59;

}
