package com.unionman.settings.layoutmanager;

import java.util.HashMap;

public class ConstantList{
	//管理应用程序
	public static final int FRAME_APP_ALL = 100000 + 1;
	public static final int FRAME_APP_DETAIL = FRAME_APP_ALL+1;
	public static final int FRAME_APP_DOWNLOAD = FRAME_APP_DETAIL + 1;
	public static final int FRAME_APP_RUN = FRAME_APP_DOWNLOAD + 1;
	
	//日期和时间
	public static final int FRAME_DATE = FRAME_APP_RUN + 1;
	public static final int FRAME_DATE_FORMAT = FRAME_DATE + 1;
	public static final int FRAME_DATE_NTP = FRAME_DATE_FORMAT + 1;
	public static final int FRAME_DATE_NTP_2 = FRAME_DATE_NTP + 1;
	public static final int FRAME_DATE_TIMEZONE = FRAME_DATE_NTP_2 + 1;
	
	//语言设置
	public static final int FRAME_LANGUAGE_SET = FRAME_DATE_TIMEZONE + 1;
	
	public static final int FRAME_MEMORY_UNMOUNT = FRAME_LANGUAGE_SET + 1;
	
	//网络设置和网络信息
	public static final int FRAME_NETWORK = FRAME_MEMORY_UNMOUNT + 1;
	public static final int FRAME_NETWORK_ETH = FRAME_NETWORK + 1;	
	public static final int FRAME_WIFI_AP=FRAME_NETWORK_ETH+1;
	public static final int FRAME_NETWORK_ETH_DHCP = FRAME_WIFI_AP + 1;
	public static final int FRAME_NETWORK_ETH_PPPOE = FRAME_NETWORK_ETH_DHCP + 1;
	public static final int FRAME_NETWORK_ETH_STATIC = FRAME_NETWORK_ETH_PPPOE + 1;
	public static final int FRAME_NETWORK_NETWORKINFO = FRAME_NETWORK_ETH_STATIC + 1;
	public static final int FRAME_NETWORK_WIFI = FRAME_NETWORK_NETWORKINFO + 1;
	public static final int FRAME_NETWORK_WIFI_SET = FRAME_NETWORK_WIFI + 1;
	public static final int FRAME_NETWORK_WIFI_SET_STATIC = FRAME_NETWORK_WIFI_SET + 1;
	
	//声音设置
	public static final int FRAME_VOICE_HDMI = FRAME_NETWORK_WIFI_SET_STATIC + 1;
	public static final int FRAME_VOICE_SPDIF = FRAME_VOICE_HDMI + 1;
	
	//显示器设置
	public static final int FRAME_DISPLAY_PIC = FRAME_VOICE_SPDIF + 1;
	public static final int FRAME_DISPLAY_VIDEO = FRAME_DISPLAY_PIC + 1;
	public static final int FRAME_DISPLAY_VIDEO_COMPARE = FRAME_DISPLAY_VIDEO + 1;
	public static final int FRAME_DISPLAY_VIDEO_MODE = FRAME_DISPLAY_VIDEO_COMPARE + 1;
	
	//节能自动待机设置
	public static final int SET_TIME_TO_SLEEP = 12345;
	
	//网络检测
	public static final int NET_CHECK = 12344;

	// 壁纸设置
	public static final int FRAME_STATIC_WALLPAPER = FRAME_DISPLAY_VIDEO_MODE + 1;
	public static final int FRAME_TRENDS_WALLPAPER = FRAME_STATIC_WALLPAPER + 1;

	
	public static final int FRAME_OPERATION_DEVICE = FRAME_TRENDS_WALLPAPER + 1;
	public static final int FRAME_SERVICE_INFO = FRAME_OPERATION_DEVICE + 1;
	public static final String PACKAGENAME = "com.unionman.settings.content.";
	
	public static HashMap<Integer, String> classname;
	public static int start = 100000;
	static{
		classname = new HashMap();
		
		classname.put(Integer.valueOf(0), PACKAGENAME+"AboutActivity");
		classname.put(Integer.valueOf(1), PACKAGENAME+"NetworkActivity");
		classname.put(Integer.valueOf(2), PACKAGENAME+"NetworkInfoActivity");
		classname.put(Integer.valueOf(3), PACKAGENAME+"DateActivity");
		classname.put(Integer.valueOf(4), PACKAGENAME+"PowerSaveActivity");
		classname.put(Integer.valueOf(5), PACKAGENAME+"SysSettingActivity");
		classname.put(Integer.valueOf(6), PACKAGENAME+"NetworkCheck");
		classname.put(Integer.valueOf(7), PACKAGENAME+"PwdProtectedActivity");
		classname.put(Integer.valueOf(8), PACKAGENAME+"UpgradeActivity");
		classname.put(Integer.valueOf(9), PACKAGENAME+"AppActivity");
		classname.put(Integer.valueOf(10), PACKAGENAME+"ResetActivity");
	//	classname.put(Integer.valueOf(11), PACKAGENAME+"WallpaperActivity");
		classname.put(Integer.valueOf(11), PACKAGENAME+"ShutdownActivity");
	//	classname.put(Integer.valueOf(13), PACKAGENAME+"LanguageActivity");
		classname.put(Integer.valueOf(12), PACKAGENAME+"ServiceInfoActivity");

		//日期时间模块
		classname.put(Integer.valueOf(FRAME_DATE_TIMEZONE), PACKAGENAME+"DateTimezone");
		classname.put(Integer.valueOf(FRAME_DATE_FORMAT), PACKAGENAME+"DateFormat");
		classname.put(Integer.valueOf(FRAME_DATE_NTP), PACKAGENAME+"DateNtp");
		classname.put(Integer.valueOf(FRAME_DATE_NTP_2), PACKAGENAME+"DateNtp2");

		//网络模块
		classname.put(Integer.valueOf(FRAME_NETWORK_ETH), PACKAGENAME+"EthNetwork");
		classname.put(Integer.valueOf(FRAME_WIFI_AP), PACKAGENAME+"WifiAp");
		classname.put(Integer.valueOf(FRAME_NETWORK_ETH_DHCP), PACKAGENAME+"EthDhcp");
		classname.put(Integer.valueOf(FRAME_NETWORK_ETH_STATIC), PACKAGENAME+"EthStatic");
		classname.put(Integer.valueOf(FRAME_NETWORK_ETH_PPPOE), PACKAGENAME+"EthPppoe");
		classname.put(Integer.valueOf(FRAME_NETWORK_WIFI), PACKAGENAME+"WifiSettings");
		classname.put(Integer.valueOf(FRAME_NETWORK_WIFI_SET), PACKAGENAME+"WifiNetwork");
		classname.put(Integer.valueOf(FRAME_NETWORK_WIFI_SET_STATIC), PACKAGENAME+"WifiStatic");
		
		
		classname.put(Integer.valueOf(FRAME_LANGUAGE_SET), PACKAGENAME+"LanguagePick");

		//应用管理模块
		classname.put(Integer.valueOf(FRAME_APP_DOWNLOAD), PACKAGENAME+"AppDownloaded");
		classname.put(Integer.valueOf(FRAME_APP_ALL), PACKAGENAME+"AppAll");
		classname.put(Integer.valueOf(FRAME_APP_RUN), PACKAGENAME+"AppRun");
		classname.put(Integer.valueOf(FRAME_APP_DETAIL), PACKAGENAME+"AppDetail");
		
		//显示模块（显示设置、播放设置、声音设置）
		classname.put(Integer.valueOf(FRAME_DISPLAY_PIC), PACKAGENAME+"DisplayPic");
		classname.put(Integer.valueOf(FRAME_DISPLAY_VIDEO), PACKAGENAME+"DisplayVideo");
		classname.put(Integer.valueOf(FRAME_DISPLAY_VIDEO_COMPARE), PACKAGENAME+"DisplayVideoCompare");
		classname.put(Integer.valueOf(FRAME_DISPLAY_VIDEO_MODE), PACKAGENAME+"DisplayVideoMode");
		classname.put(Integer.valueOf(FRAME_VOICE_HDMI), PACKAGENAME+"VoiceHdmi");
		classname.put(Integer.valueOf(FRAME_VOICE_SPDIF), PACKAGENAME+"VoiceSpdif");
		classname.put(Integer.valueOf(SET_TIME_TO_SLEEP), PACKAGENAME+"SetTimeToSleep");
		classname.put(Integer.valueOf(NET_CHECK), PACKAGENAME+"FrameNetworkOneKey");

		classname.put(Integer.valueOf(FRAME_STATIC_WALLPAPER), PACKAGENAME+"StaticWallPaper");
		classname.put(Integer.valueOf(FRAME_TRENDS_WALLPAPER), PACKAGENAME+"TrendsWallPaper");
		classname.put(FRAME_OPERATION_DEVICE, PACKAGENAME+"OperationDevice");
		classname.put(FRAME_SERVICE_INFO, PACKAGENAME+"ServiceInfoActivity");
	}
}