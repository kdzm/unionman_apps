package com.cvte.tv.at.api.tvapi.hisilicon;

/**
 * Created by User on 2015/6/26.
 */
/**
 * Common static final values which will be used in different activities.
 *
 * @author y00164887
 *
 */
public class CommonValue {
    /**
     * Specify .so plug_in name used in application.<br>
     * Input parameter of method DTV.getInstance(String value)
     */
    // Old HiDTV apk.
    //public static final String DTV_PLUGIN_NAME = "dtv-live://plugin.libhidtv_multiroom_plugin.libhidtv_plugin.libhipvr_plugin";
    // New HiDTVPlayer.
    public static final String DTV_PLUGIN_NAME = "dtv-live://plugin.libhidtv_plugin.libhipvr_plugin.libhidtv_multiroom_plugin";
    /**
     * Shared preference's file name. <br>
     * Currently, content in preference file decide whether start activity when
     * system reboot.
     */
    public static final String DTV_PREFERCE_NAME = "dtv_preference";
    /**
     * Intent name. Used for DTVService notice DTVPlayerActivity that
     * initialization has finished.
     */
    public static final String DTV_INTENT_INIT_FINISH = "com.hisilicon.init.finish";
    /**
     * Intent name. Used for EPGActivity notice DTVPlayerActivity to dismiss
     * CA/No signal tip and Radio Background.
     */
    public static final String DTV_INTENT_DISMISS_TIP = "com.hisilicon.dismiss.tip";
    /**
     * Intent name. Used for Activities which transparent to notice
     * DTVPlayerActivity show/dismiss No signal tip.
     */
    public static final String DTV_INTENT_SIGNAL_STATU = "com.hisilicon.signal.statu";
    /**
     * Intent name. Used for DTVService to show BookAlarmActivity.
     */
    public static final String DTV_BOOK_ALARM_REMINDE = "com.hisilicon.book.alarm.reminde";
    /**
     * Intent name. Used for DTVService to start book task.
     */
    public static final String DTV_BOOK_ALARM_ARRIVE = "com.hisilicon.book.alarm.arrive";
    public static final String DTV_BOOK_ID = "Id";
    public static final String DTV_BOOK_DURATION = "Duration";
    public static final String DTV_BOOK_CHANNEL_ID = "ChannelId";
    public static final String DTV_BOOK_TYPE = "Type";
    /**
     * Extra value tag of DTV_INTENT_SIGNAL_STATU intent.
     */
    // These two only action in DTV.
    public static final int VALUE_NO_SIGNAL = 0;
    public static final int VALUE_HAVE_SIGNAL = 1;
    public static final int VALUE_NOT_CA = 0;
    public static final int VALUE_IS_CA = 1;

    /**
     * Extra value tag in intent. <br>
     * Used for DTVPlayerActivity notice ChannelListActivity to show Favorite
     * list when creating.
     */
    public static final String FAV_TAG = "FavType";
    public static final int VALUE_OPEN_FAV = 1;
    /**
     * the Tag of the TDT Lock in the configure file.
     */
    public static final String TDT_LOCK = "s32EnableSystemTimeUpdate";
    /**
     * the TDT Lock is open.
     */
    public static final int TDT_LOCK_OPEN = 1;
    /**
     * the TDT Lock is close.
     */
    public static final int TDT_LOCK_CLOSE = 0;
    /**
     * the tag of the Program Lock in the configure file.
     */
    public static final String PROGRAM_LOCK = "bEnableProgramLock";
    /**
     * the Program Lock is open.
     */
    public static final int PROGRAM_LOCK_OPEN = 1;
    /**
     * the Program Lock is close.
     */
    public static final int PROGRAM_LOCK_CLOSE = 0;
    /**
     * the Menu Lock is closed in the configure file.
     */
    public static final String MENU_LOCK = "bEnableMenuLock";
    /**
     * the Menu Lock is open.
     */
    public static final int MENU_LOCK_OPEN = 1;
    /**
     * the Menu Lock is close.
     */
    public static final int MENU_LOCK_CLOSE = 0;
    /**
     * the tag of the user password in the configure file.
     */
    public static final String USER_PASSWORD = "au8UserPW";
    /**
     * the tag of the inforbar show time in the configure file.
     */
    public static final String INFOBAR_SHOW_TIME = "u32InfoBarInsistTime";
    /**
     * the default show time of the inforbar.
     */
    public static final int DEFAULT_INFORBAR_SHOW_TIME = 5;
    /**
     * the tag of the timeshift to record switch in the configure file.
     */
    public static final String TIMESHIFT_TO_RECORD_SWITCH = "bEnableTimeShiftToPvr";
    /**
     * the timeshift to record switch is open.
     */
    public static final int TIMESHIFT_TO_RECORD_SWITCH_OPEN = 1;
    /**
     * the timeshift to record switch is close.
     */
    public static final int TIMESHIFT_TO_RECORD_SWITCH_CLOSE = 0;
    /**
     * the tag of the record path switch in the configure file.
     */
    public static final String RECORD_PATH = "au8RecordFilePath";
    /**
     * the default path for recording.
     */
    public static final String DEFAULT_RECORD_PATH = "/mnt/nand";
    /**
     * the tag of the timeshift duration.
     */
    public static final String TIMESHIFT_TIME = "u64TimeShiftDuration";
    /**
     * the default time of timeshift.
     */
    public static final String DEFAULT_TIMESHIFT_TIME = "1h";
    /**
     * the tag of the super password.
     */
    public static final String SUPER_PASSWORD = "au8SuperPW";
    /**
     * the default user password.
     */
    public static final String DEFAULT_USER_PASSWORD = "14474E4033AC29CC";
    /**
     * the default super password.
     */
    public static final String DEFAULT_SUPER_PASSWORD = "31F7C8A627E8E287";
    /**
     * the length of the password.
     */
    public static final int PASSWORD_LENGTH = 4;
    /**
     * The name of the TV group in stack
     */
    public static final String TVLIST_NAME = "Tv";
    /**
     * The name of the Radio group in stack
     */
    public static final String RADIOLIST_NAME = "Radio";
    /**
     * Format string.
     */
    public final static String FORMAT_STR = "0000";
    /**
     * Default pop item number
     */
    public static final int DEFAULT_POP_ITEM_NUMBER = 4;
    /**
     * Send DTVPlayerActivity to close PIP
     */
    // public static final String BROADCAST_CLOSE_PIP =
    // "com.hisilicon.dtvui.play.DTVPlayerActivity.ClosePIP";

    /**
     * the flag of select country in the configure file.
     */
    public static final String SELECT_COUNTRYCODE = "bEnableSetCountryCode";

    public static final String COUNTRY_CODE_KEY = "au8CountryCode";

    public static final String FIRST_EPG_LANGUAGE = "au8FirstEPGLanguage";
}
