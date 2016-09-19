package com.um.tv.menu.utils;

import android.content.Context;

import com.hisilicon.android.tvapi.constant.EnumFactoryNlaitem;
import com.hisilicon.android.tvapi.constant.EnumFactoryPattern;
import com.hisilicon.android.tvapi.constant.EnumPanelMirror;
import com.hisilicon.android.tvapi.constant.EnumPictureClrtmp;
import com.hisilicon.android.tvapi.constant.EnumPictureMode;
import com.hisilicon.android.tvapi.constant.EnumSoundHidev;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.um.tv.menu.UMFactoryMenu;

public class Utils {
    public static final boolean DEBUG = true;

    public static Context mContext = UMFactoryMenu.getContext();

    public static final int ID_MAIN_MENU = 0x00001;

    public static final int ADCRGainMin = 0;
    public static final int ADCRGainMax = 2097151;

    public static final int ADCGGainMin = 0;
    public static final int ADCGGainMax = 2097151;

    public static final int ADCBGainMin = 0;
    public static final int ADCBGainMax = 2097151;

    public static final int ADCROffsetMin = 0;
    public static final int ADCROffsetMax = 255;

    public static final int ADCGOffsetMin = 0;
    public static final int ADCGOffsetMax = 255;

    public static final int ADCBOffsetMin = 0;
    public static final int ADCBOffsetMax = 255;

    public static final int WhiteBlanceRGainMin = 0;
    public static final int WhiteBlanceRGainMax = 511;

    public static final int WhiteBlanceGGainMin = 0;
    public static final int WhiteBlanceGGainMax = 511;

    public static final int WhiteBlanceBGainMin = 0;
    public static final int WhiteBlanceBGainMax = 512;

    public static final int WhiteBlanceROffsetMin = 0;
    public static final int WhiteBlanceROffsetMax = 1023;

    public static final int WhiteBlanceGOffsetMin = 0;
    public static final int WhiteBlanceGOffsetMax = 1023;

    public static final int WhiteBlanceBOffsetMin = 0;
    public static final int WhiteBlanceBOffsetMax = 1023;

    //fixed by unionman for Overscan adjust
    public static final int OverscanHSizeMin = 0;
    public static final int OverscanHSizeMax = 1000;

    public static final int OverscanHPositionMin = 0;
    public static final int OverscanHPositionMax = 1000;

    public static final int OverscanVSizeMin = 0;
    public static final int OverscanVSizeMax = 1000;

    public static final int OverscanVPositionMin = 0;
    public static final int OverscanVPositionMax = 1000;

    public static final int PictureModeBrightnessMin = 0;
    public static final int PictureModeBrightnessMax = 100;

    public static final int PictureModeContrastMin = 0;
    public static final int PictureModeContrastMax = 100;

    public static final int PictureModeSaturationMin = 0;
    public static final int PictureModeSaturationMax = 100;

    public static final int PictureModeHueMin = 0;
    public static final int PictureModeHueMax = 100;

    public static final int PictureModeSharpnessMin = 0;
    public static final int PictureModeSharpnessMax = 100;

    public static final int PictureModeBacklightMin = 0;
    public static final int PictureModeBacklightMax = 100;

    public static final int NLOSD0Min = 0;
    public static final int NLOSD0Max = 100;

    public static final int NLOSD25Min = 0;
    public static final int NLOSD25Max = 100;

    public static final int NLOSD50Min = 0;
    public static final int NLOSD50Max = 100;

    public static final int NLOSD75Min = 0;
    public static final int NLOSD75Max = 100;

    public static final int NLOSD100Min = 0;
    public static final int NLOSD100Max = 100;

    public static final int SSCDDRModulationMin = 20;
    public static final int SSCDDRModulationMax = 40;

    public static final int SSCDDRPercentageMin = 0;
    public static final int SSCDDRPercentageMax = 10;

    public static final int SSCLVDSModulationMin = 20;
    public static final int SSCLVDSModulationMax = 40;

    public static final int SSCLVDSPercentageMin = 0;
    public static final int SSCLVDSPercentageMax = 10;

    public static final int SSCPanelSwingMin = 0;
    public static final int SSCPanelSwingMax = 100;

    public static final int AVCThresholdMin = 1;
    public static final int AVCThresholdMax = 100;

    public static final int LvbsSpreadRatioMin = 0;
    public static final int LvbsSpreadRatioMax = 31;

    public static final int LvdsSpreadFreqMin = 2;
    public static final int LvdsSpreadFreqMax = 5;

    public static final int LvdsSpreadDrvCurrentMin = 0;
    public static final int LvdsSpreadDrvCurrentMax = 7;

    public static final int LvdsSpreadComVoltageMin = 0;
    public static final int LvdsSpreadComVoltageMax = 7;

    public static final int DdrSpreadRatioMin = 0;
    public static final int DdrSpreadRatioMax = 31;

    public static final int DdrSpreadFreqMin = 2;
    public static final int DdrSpreadFreqMax = 5;

    public static final int VBOSpreadRatioMin = 0;
    public static final int VBOSpreadRatioMax = 31;

    public static final int VBOSpreadFreqMin = 1;
    public static final int VBOSpreadFreqMax = 2;

    public static final int VBODrvCurrentMin = 0;
    public static final int VBODrvCurrentMax = 4;

    public static final int GMACSpreadRatioMin = 0;
    public static final int GMACSpreadRatioMax = 31;

    public static final int GMACSpreadFreqMin = 2;
    public static final int GMACSpreadFreqMax = 5;

    public static final int GMACDrvCurrentMin = 1;
    public static final int GMACDrvCurrentMax = 50;

    public static final int USB3SpreadRatioMin = 0;
    public static final int USB3SpreadRatioMax = 31;

    public static final int USB3SpreadFreqMin = 2;
    public static final int USB3SpreadFreqMax = 15;

    public static final int USB3DrvCurrentMin = 1;
    public static final int USB3DrvCurrentMax = 50;

//    public static final String DisplayNameFactoryMenu  = HiFactoryMenu.getContext().getString(R.string.display_name_factory_menu);
//    public static final String DisplayNameADC          = HiFactoryMenu.getContext().getString(R.string.display_name_adc);
//    public static final String DisplayNameWhiteBalance = HiFactoryMenu.getContext().getString(R.string.display_name_white_balance);
//    public static final String DisplayNameOverscan     = HiFactoryMenu.getContext().getString(R.string.display_name_overscan);
//    public static final String DisplayNameNonLinear    = HiFactoryMenu.getContext().getString(R.string.display_name_non_linear);
//    public static final String DisplayNameNonStandard  = HiFactoryMenu.getContext().getString(R.string.display_name_non_standard);
//    public static final String DisplayNameFacSingleKey = HiFactoryMenu.getContext().getString(R.string.display_name_fac_single_key);
//    public static final String DisplayNameOtherOptions = HiFactoryMenu.getContext().getString(R.string.display_name_other_options);

    public static final String DisplayNameFactoryMenu  = "工厂菜单";
    public static final String DisplayNameADC          = "ADC Adjust";
    public static final String DisplayNameWhiteBalance = "White Balance";
    public static final String DisplayNameOverscan     = "Overscan";
    public static final String DisplayNameNonLinear    = "NonLinear";
    public static final String DisplayNameNonStandard  = "NonStandard";
    public static final String DisplayNameSSCAdjust    = "SSC Adjust";
    public static final String DisplayNameFacSingleKey = "FactorySingleKey";
    public static final String DisplayNameOtherOptions = "Other Options";
    public static final String DisplayNameNext = "Next";
    public static final String DisplayNamePage2 = "Page2";
    public static final String DisplayNameUMNetUpgrade = "UM Upgrade";
    public static final String DisplayNameAndroidSetting = "Android Setting";
    public static final String DisplayNameADBConsole = "ADB Console";
    
    public static final String[] ItemsADC = new String[] { "Source",
            "ADC Tune", "R Gain", "G Gain", "B Gain", "R offset", "G offset",
            "B offset", "Phase" };
    public static final String[] ItemsWhiteBlance = new String[] { "Source",
            "R Gain", "G Gain", "B Gain", "R Offset", "G Offset", "B Offset"
                                                                 };
    public static final String[] ItemsOverscan = new String[] { "Source",
            "H size", "H position", "V size", "V position" };

    public static final String[] ItemsPictureMode = new String[] {
            "Source", "Pictuer mode", "Brightness", "Contrast", "Saturation",
            "Hue", "Sharpness", "Backlight" };

    public static final String[] ItemsNonLinear = new String[] { "Curve Type",
            "OSD_0", "OSD_25", "OSD_50", "OSD_75", "OSD_100"
                                                               };
    public static final String[] ItemsNonStandard = new String[] { "VIF", "VD",
            "AUDIO", "DEMOD"
                                                                 };
    public static final String[] ItemsSSCAdjust = new String[] { "VbyOne",
            "LVDS", "DDR","GMAC", "USB", "PLL", "CI", "VDAC" };

    public static final String[] ItemsLVDS = new String[] { "Lvds", "Spread",
            "SpreadRatio", "SpreadFreq", "DrvCurrent", "ComVoltage" };

    public static final String[] ItemsDDR = new String[] { "DdrSpread",
            "SpreadRatio", "SpreadFreq" };

    public static final String[] ItemsVBO = new String[] { "VByOne", "Spread",
            "SpreadRatio", "SpreadFreq", "DrvCurrent", "VBOEmphasis" };

    public static final String[] ItemsGMAC = new String[] { "GMAC", "Spread",
        "SpreadRatio", "SpreadFreq", "DrvCurrent" };

    public static final String[] ItemsUSB = new String[] { "USBSpread",
        "SpreadRatio", "SpreadFreq", "DrvCurrent" };

    public static final String[] ItemsPLL = new String[] { "PLL" };

    public static final String[] ItemsCI = new String[] { "CI" };

    public static final String[] ItemsVDAC = new String[] { "VDAC" };

    public static final String[] ItemsOtherOptions = new String[] { "TunerLNA", "WatchDog",
            "Reset", "PowerOnMode", "Uart", "UartDebug", "TestPattern",
            "AgingMode", "PanelSelect", "Mirror", "PowerMusic", "USBUpdateDeviceInfo","PWMOffset", "Reset EEPROM","USBUpdateCustomInfo","UpdatePQNumAndAQ","DdrRefresh"};
    
    public static final String[] ItemsPage2 = new String[] { "PqUpdate", "AqUpdate",
    	"LogoUpdate", "MachineTypeUpdate", "ScreenParamUpdate", "SalePicUpdate" };
    
    public static final String[] ItemsPage3 = new String[] { "FacReset", "SystemInit",
    	"AgingModeSwitch", "StartupMode", "StartUpMusic"};
    
    public static final String[] ItemsInfo = new String[] { "SW Version",
            "Board", "panel", "main PQ Version", "Sub PQ Version", "Date",
            "Time" };
    public static final String[] ItemsAudio = new String[] { "NRThreshold",
            "AVCThreshold", "HiDEVType", "OverModulation", "FrequencyOffset"};

    public static final String[] SourceADC = new String[] { "YPBPR1", "YPBPR2", "VGA" };
    public static final int[] SourceADCValue = new int[] {
            EnumSourceIndex.SOURCE_YPBPR1, EnumSourceIndex.SOURCE_YPBPR2,
            EnumSourceIndex.SOURCE_VGA };

    public static final String[] SourceWhiteBlance = new String[] { "DTV",
            "ATV", "AV", "YPbPr1", "VGA", "HDMI", };
    public static final int[] SourceWBValue = new int[] {
            EnumSourceIndex.SOURCE_DVBC, EnumSourceIndex.SOURCE_ATV,
            EnumSourceIndex.SOURCE_CVBS1, EnumSourceIndex.SOURCE_YPBPR1,
            EnumSourceIndex.SOURCE_VGA, EnumSourceIndex.SOURCE_HDMI1, };

    public static final String[] SourceOverscan = new String[] { "ATV",
            "DVBC", "DTMB", "CVBS", "CVBS", "VGA", "YPBPR", "HDMI", "HDMI",
            "HDMI" };

    public static final int[] SourceOverscanValue = new int[]  {
            EnumSourceIndex.SOURCE_ATV, EnumSourceIndex.SOURCE_DVBC,
            EnumSourceIndex.SOURCE_DTMB, EnumSourceIndex.SOURCE_CVBS1,
            EnumSourceIndex.SOURCE_CVBS2, EnumSourceIndex.SOURCE_VGA,
            EnumSourceIndex.SOURCE_YPBPR1, EnumSourceIndex.SOURCE_HDMI1,
            EnumSourceIndex.SOURCE_HDMI2, EnumSourceIndex.SOURCE_HDMI3 };


    public static final String[] SourcePicModeTvSource = new String[] { "ATV",
            "DVBC", "DTMB", "CVBS1", "CVBS2", "VGA", "YPBPR1", "HDMI1", "HDMI2",
            "HDMI3" };

    public static final int[] SourcePicModeTvSourceValue = new int[] {
            EnumSourceIndex.SOURCE_ATV, EnumSourceIndex.SOURCE_DVBC,
            EnumSourceIndex.SOURCE_DTMB, EnumSourceIndex.SOURCE_CVBS1,
            EnumSourceIndex.SOURCE_CVBS2, EnumSourceIndex.SOURCE_VGA,
            EnumSourceIndex.SOURCE_YPBPR1, EnumSourceIndex.SOURCE_HDMI1,
            EnumSourceIndex.SOURCE_HDMI2, EnumSourceIndex.SOURCE_HDMI3 };

    public static final String[] SourcePictureMode = new String[] { "Standard",
            "VIVID", "SOFT", "USER" };

    public static final int[] SourcePictureModeValue = new int[] { EnumPictureMode.PICMODE_STANDARD,
         EnumPictureMode.PICTURE_VIVID, EnumPictureMode.PICMODE_SOFTNESS, EnumPictureMode.PICMODE_USER };

    public static final String[] SourceNonLinear = new String[] { "Volume",
            "Brightness", "Contrast", "Saturation", "Sharpness", "Hue",
            "Backlight", };
    public static final int[] SourceNonLinearValue = new int[] {
            EnumFactoryNlaitem.NLAITEM_VOLUME,
            EnumFactoryNlaitem.NLAITEM_BRIGHTNESS,
            EnumFactoryNlaitem.NLAITEM_CONTRAST,
            EnumFactoryNlaitem.NLAITEM_SATURATION,
            EnumFactoryNlaitem.NLAITEM_SHARPNESS,
            EnumFactoryNlaitem.NLAITEM_HUE,
            EnumFactoryNlaitem.NLAITEM_BACKLIGHT };

    public static final String[] SourceWhiteBalanceColorTemp = new String[] {
            "STANDARD", "COOL", "WARM", "USER" };
    public static final int[] SourceWhiteBalanceColorTempValue = new int[] {
            EnumPictureClrtmp.CLRTMP_NATURE, EnumPictureClrtmp.CLRTMP_COOL,
            EnumPictureClrtmp.CLRTMP_WARM, EnumPictureClrtmp.CLRTMP_USER };

    public static final String[] SourceNRThreshold = new String[] { "0", "1",
            "2", "3", };
    public static final int[] SourceNRThresholdValue = new int[] { 0, 1, 2, 3 };

    public static final String[] SourceAVCThreshold = new String[] { "0", "1",
            "2", "3", };
    public static final int[] SourceAVCThresholdvalue = new int[] { 0, 1, 2, 3 };

    public static final String[] SourceHiDEVType = new String[] { "OFF",
            "BW_LV1", "BW_LV2", "BW_LV3", "BW_MAX", };
    public static final int[] SourceHiDEVTypeValue = new int[] {
            EnumSoundHidev.SOUND_HIDEV_OFF, EnumSoundHidev.SOUND_HIDEV_BW_LV1,
            EnumSoundHidev.SOUND_HIDEV_BW_LV2,
            EnumSoundHidev.SOUND_HIDEV_BW_LV3,
            EnumSoundHidev.SOUND_HIDEV_BW_MAX };

    public static final String[] SourceWatchDog = new String[] { "OFF", "ON", };
    public static final boolean[] SourceWatchDogValue = new boolean[] { false, true };

    public static final String[] SourceUart = new String[] { "OFF", "ON", };
    public static final boolean[] SourceUartValue = new boolean[] { false, true, };

    public static final String[] SourceUartDebug = new String[] { "OFF", "ON", };
    public static final boolean[] SourceUartDebugValue = new boolean[] { false, true, };

    public static final String[] SourceAgingMode = new String[] { "OFF", "ON", };
    public static final boolean[] SourceAgingModeValue = new boolean[] { false, true, };

    public static final String[] SourcePanelIndex = new String[] {"0", "1", "2",
            "3", "4", "5","6","7","8","9","10","11","12","13","14"};
    public static final int[] SourcePanelIndexValue = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

    public static  String[] SourcePWMOffset = null;
    public static  int[] SourcePWMOffsetValue = null;
    public static void intallPWMOffset()
    {
        if( SourcePWMOffset == null || SourcePWMOffsetValue == null)
        {
          SourcePWMOffset = new String[511];
          SourcePWMOffsetValue = new int[511];
          for(int i = -255; i < 256 ; i++)
          {
            SourcePWMOffset[i + 255] = i+"";
            SourcePWMOffsetValue[i + 255] = i;
          }
        }
    }
    public static final String[] SourcePanelFlipMirror = new String[] {"NORMAL","MIRROR","FLIP","MAF",};
    public static final int[] SourcePanelFlipMirrorValue = new int[] {
            EnumPanelMirror.MIRROR_FLIP_OFF,
            EnumPanelMirror.MIRROR_FLIP_MIRROR,
            EnumPanelMirror.MIRROR_FLIP_FLIP, EnumPanelMirror.MIRROR_FLIP_MIRROR_FLIP, };

    public static final String[] SourcePowerMusic = new String[] { "OFF", "ON", };
    public static final boolean[] SourcePowerMusicValue = new boolean[] { false, true, };

    public static final String[] SourcePowerOnMode = new String[] { "POWERON",
            "STANDBY", "MEMORY", };
    public static final int[] SourcePowerOnModeValue = new int[] { 0, 1, 2 };

    public static final String[] Source3DDetectLevel = new String[] { "LOW",
            "MIDDLE", "HIGH" };

    public static final String[] SourceTestPattern = new String[] { "OFF",
            "BLACK", "WHITE", "RED", "GREEN", "BLUE", "GRAY", };
    public static final int[] SourceTestPatternValue = new int[] {
            EnumFactoryPattern.PATTERN_OFF, EnumFactoryPattern.PATTERN_BLACK,
            EnumFactoryPattern.PATTERN_WHITE, EnumFactoryPattern.PATTERN_RED,
            EnumFactoryPattern.PATTERN_GREEN, EnumFactoryPattern.PATTERN_BLUE,
            EnumFactoryPattern.PATTERN_GRAY, };

    public static final String[] SourceInitialChannel = new String[] { "DTV",
            "ATV"
                                                                     };
    public static final String[] SourceName = new String[] { "ATV", "DVBC",
            "DTMB", "CVBS1", "CVBS2", "CVBS3", "VGA", "YPBPR1", "YPBPR2",
            "HDMI1", "HDMI2", "HDMI3", "HDMI4", "MEDIA", "SCART1", "SCART2", "BUTT" };

    public static final String[] SourceLvbsEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceLvbsEnableValue = new boolean[] { false, true, };

    public static final String[] SourceLvbsSpreadEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceLvbsSpreadEnableValue = new boolean[] { false, true, };

    public static final String[] SourceDdrSpreadEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceDdrSpreadEnableValue = new boolean[] { false, true, };

    public static final String[] SourceVBOEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceVBOEnableValue = new boolean[] { false, true, };

    public static final String[] SourceVBOSpreadEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceVBOSpreadEnableValue = new boolean[] { false, true, };

    public static final String[] SourceVBOEmphasis = new String[] { "0DB", "3.5DB","6DB" };
    public static final int[] SourceVBOEmphasisValue = new int[] {0,1,2};

    public static final String[] SourceGMACEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceGMACEnableValue = new boolean[] { false, true, };

    public static final String[] SourceGMACSpreadEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceGMACSpreadEnableValue = new boolean[] { false, true, };

    public static final String[] SourceUSBSpreadEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceUSBSpreadEnableValue = new boolean[] { false, true, };

    public static final String[] SourcePLLEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourcePLLEnableValue = new boolean[] { false, true, };

    public static final String[] SourceCIEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceCIEnableValue = new boolean[] { false, true, };

    public static final String[] SourceVDACEnable = new String[] { "OFF", "ON", };
    public static final boolean[] SourceVDACEnableValue = new boolean[] { false, true, };

    public static final String[] SourceOverModulation = new String[] { "50K", "100K", "200K", "540K", "384K"};
    public static final int[] SourceOverModulationValue = new int[] { 0, 1, 2, 3, 4};
    
    public static final boolean[] TunerLNASwitch = new boolean[] { false, true, };
    
    public static final String[] SourceDdrRefresh = new String[] { "OFF", "ON"};
    public static final int[] SourceDdrRefreshValue = new int[] { 0, 1};
    
    public static final boolean EnableADC = true;
    public static final boolean EnableWhiteBlance = true;
    public static final boolean EnableOverscan = true;
    public static final boolean EnablePictureMode = true;
    public static final boolean EnableNonLinear = true;
    public static final boolean EnableNonStandard = true;
    public static final boolean EnableSSCAdjust = true;
    public static final boolean EnableOtherOptions = true;
    public static final boolean EnableInfo = true;
    public static final boolean EnableFactorySingleKey = true;
    public static final boolean EnablePage2 = false;
    public static final boolean EnableUMNetUpgrade = true;
    public static final boolean EnableAndroidSetting = true;
    public static final boolean EnableADBConsole = true;
}
