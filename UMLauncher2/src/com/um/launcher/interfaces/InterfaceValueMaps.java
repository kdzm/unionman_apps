
package com.um.launcher.interfaces;

import com.hisilicon.android.tvapi.constant.EnumSoundMode;
import com.um.launcher.R;

/**
 * Parameter of interface
 *
 * @author huyq
 */
public class InterfaceValueMaps {
    /**
     * all configuration settings
     */
    public static int app_item_values[][] = {
            {
                    R.id.set_item_net, R.string.net_setting
            },
            {
                    R.id.set_item_pic, R.string.pic_setting
            },
            {
                    R.id.set_item_sound, R.string.voice_setting
            },
            {
                    R.id.set_item_powersaving, R.string.system
            },
            {
                    R.id.set_item_advanced, R.string.advance_setting
            },
            {
                    R.id.set_item_appmanage, R.string.recover_setting
            },
            {
                    R.id.set_item_systeminfo, R.string.system_info
            }/*,
            {
                    R.id.set_item_help, R.string.help
            }*/
    };

    /**
     * sound settings
     */
    public static int voice_mode_logic[][] = {
            {
                    EnumSoundMode.SNDMODE_STANDARD, R.string.sndmode_standard_string
            },
            {
                    EnumSoundMode.SNDMODE_NEWS, R.string.sndmode_news_string
            },
            {
                    EnumSoundMode.SNDMODE_MUSIC, R.string.sndmode_music_string
            },
            {
                    EnumSoundMode.SNDMODE_MOVIE, R.string.sndmode_movie_string
            },
            {
                    EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite1_string
            },
            {
                    EnumSoundMode.SNDMODE_DESK, R.string.sndmode_onsite2_string
            }
    };

    /**
     * The language switching
     */
    public static int language_change[][] = {
            {
                    0, R.string.chinese
            },
            {
                    1, R.string.english
            }
    };

    /**
     * Sleep time
     */
    public static int system_sleep[][] = {
            {
                    0, R.string.sleep_close
            },
            {
                    1, R.string.five_min
            }, {
                    2, R.string.thirty_min
            },
            {
                    3, R.string.sixty_min
            }, {
                    4, R.string.ninety_min
            },
            {
                    5, R.string.sixscore_min
            }
    };

    public static int key_sound[][] = {
            {
                    0, R.string.key_sound_off
            },
            {
                    1, R.string.key_sound_on
            }
    };

    public static int power_on_mode[][] = {
            {
                    0, R.string.power_on_mode_tv
            },
            {
                    1, R.string.power_on_mode_launcher
            }
    };

    public static int blue_screen[][] = {
            {
                    0, R.string.blue_screen_off
            },
            {
                    1, R.string.blue_screen_on
            }
    };
}
