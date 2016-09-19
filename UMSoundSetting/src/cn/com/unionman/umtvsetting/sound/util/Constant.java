package cn.com.unionman.umtvsetting.sound.util;

/**
 *
 * @author tang_shengchang HiSi.ltd <br>
 *
 *         Procedure in constant
 */
public class Constant {

    /**
     * Log tag
     */
    public static final boolean LOG_TAG = false;
    /**
     * Lost 5s time
     */
    public static final int DISPEAR_TIME = 30000;
	public static final int DISPEAR_TIME_LONG = 30000;
    /**
     * Lost 10s time
     */
    public static final int DISPEAR_TIME_10s = 10000;
    /**
     * The volume of the length
     */
    // public static final int BAR_LENGTH = 100;
    /**
     * Set interface refresh message event
     */
    public static final int SETTING_UI_REFRESH_VIEWS = 0;
    public static final int EXIT_MUNE = 1;
    /**
     * Action of start scan
     */
    public static final String ACTION_START_RF_SCAN = "com.hisilicon.atv.scan.start";
    /**
     * Action of scan finish
     */
    public static final String ACTION_FINISH_RF_SCAN = "com.hisilicon.atv.scan.finish";
    
	public static final int DIALOG_ITEM_DISMISS_BYTIME = 4;
	public static final int DIALOG_DISMISS_BYTIME = 5;
	public static final int DIALOG_DISMISS_NOW = 6;
	
	public static final String DeafaultPwd = "123456";
	public static final String RestorePwd = "RestorePwd";
	public static final int DIALOG_PWD_DISMISS_BYTIME = 7;
	public static final int SOUND_DIALOG_ITEM_SHOW = 2;
	public static final int SOUND_DIALOG_ITEM_DISMISS = 3;
	public static final int DIALOG_SURROUND_DISMISS_BYTIME = 8;
	
	public static final String UMDefaultPwd = "UMDefaultPwd";
	public static final String UMDefaultPwdValue = "000000";
	public static final String UMSuperPwd = "UMSuperPwd";
	public static final String UMSuperPwdValue = "123456";	
}
