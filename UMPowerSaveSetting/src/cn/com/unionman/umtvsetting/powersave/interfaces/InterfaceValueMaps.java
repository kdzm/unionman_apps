package cn.com.unionman.umtvsetting.powersave.interfaces;

import com.hisilicon.android.tvapi.constant.EnumAtvAudsys;
import com.hisilicon.android.tvapi.constant.EnumAtvClrsys;
import com.hisilicon.android.tvapi.constant.EnumPictureAspect;
import com.hisilicon.android.tvapi.constant.EnumPictureClrtmp;
import com.hisilicon.android.tvapi.constant.EnumPictureMode;
import com.hisilicon.android.tvapi.constant.EnumSoundMode;
import com.hisilicon.android.tvapi.constant.EnumSoundSpdif;
import com.hisilicon.android.tvapi.constant.EnumSoundfield;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import cn.com.unionman.umtvsetting.powersave.R;

/**
 * Parameter of interface
 *
 * @author huyq
 *
 */
public class InterfaceValueMaps {

    /**
     * PictureMode
     */
    public static int picture_mode[][] = {
            { EnumPictureMode.PICMODE_STANDARD,
                    R.string.picmode_standard_string },
            { EnumPictureMode.PICMODE_DYNAMIC, R.string.picmode_dynamic_string },
            { EnumPictureMode.PICMODE_SOFTNESS,
                    R.string.picmode_softness_string },
            { EnumPictureMode.PICMODE_USER, R.string.picmode_user_string } };

    /**
     * PictureClrtmp
     */
    public static int picture_clrtmp[][] = {
            { EnumPictureClrtmp.CLRTMP_NATURE, R.string.clrtmp_nature_string },
            { EnumPictureClrtmp.CLRTMP_COOL, R.string.clrtmp_cool_string },
            { EnumPictureClrtmp.CLRTMP_WARM, R.string.clrtmp_warm_string } };

    /**
     * PictureAspect
     */
    public static int[][] picture_aspect = {
            { EnumPictureAspect.ASPECT_AUTO, R.string.aspect_auto_string },
            { EnumPictureAspect.ASPECT_16_9, R.string.aspect_16_9_string },
            { EnumPictureAspect.ASPECT_4_3, R.string.aspect_4_3_string },
            { EnumPictureAspect.ASPECT_SUBTITLE,R.string.aspect_subtitle_string },
            { EnumPictureAspect.ASPECT_CINEMA, R.string.aspect_cinema_string },
            { EnumPictureAspect.ASPECT_ZOOM, R.string.aspect_zoom_string },
            { EnumPictureAspect.ASPECT_ZOOM1, R.string.aspect_zoom1_string },
            { EnumPictureAspect.ASPECT_ZOOM2, R.string.aspect_zoom2_string },
//            { EnumPictureAspect.ASPECT_POINT2POINT,R.string.aspect_point2point_string },
            { EnumPictureAspect.ASPECT_PANORAMA,R.string.aspect_panorama_string }
    };


    /**
     * PicturehdAspect
     */
    public static int[][] picturehd_aspect = {
            { EnumPictureAspect.ASPECT_AUTO, R.string.aspect_auto_string },
            { EnumPictureAspect.ASPECT_16_9, R.string.aspect_16_9_string },
            { EnumPictureAspect.ASPECT_4_3, R.string.aspect_4_3_string },
            { EnumPictureAspect.ASPECT_SUBTITLE,R.string.aspect_subtitle_string },
            { EnumPictureAspect.ASPECT_CINEMA, R.string.aspect_cinema_string },
            { EnumPictureAspect.ASPECT_ZOOM, R.string.aspect_zoom_string },
            { EnumPictureAspect.ASPECT_ZOOM1, R.string.aspect_zoom1_string },
            { EnumPictureAspect.ASPECT_ZOOM2, R.string.aspect_zoom2_string }//,
//            { EnumPictureAspect.ASPECT_POINT2POINT,R.string.aspect_point2point_string }
    };

    /**
     * VGAAspect
     */
    public static int[][] vga_aspect = {
            { EnumPictureAspect.ASPECT_16_9, R.string.aspect_16_9_string },
            { EnumPictureAspect.ASPECT_4_3, R.string.aspect_4_3_string }
    };

    /**
     * MEMCLevel
     */
    public static int[][] MEMC_level = { { 0, R.string.memc_off },
            { 1, R.string.memc_low }, { 2, R.string.memc_mid },
            { 3, R.string.memc_high },{4,R.string.memc_auto} };

    /**
     * FleshTone
     */
    public static int[][] flesh_tone = { { 0, R.string.weak_string },
            { 1, R.string.low_string }, { 2, R.string.middle_string },
            { 3, R.string.high_string } };
    /**
     * ON or OFF
     */
    public static int[][] on_off = { { 0, R.string.off }, { 1, R.string.on } };

    /**
     * NR
     */
    public static int[][] NR = { { 0, R.string.off },
            { 1, R.string.low_string }, { 2, R.string.middle_string },
            { 3, R.string.high_string } };

    /**
     * SoundMode
     */
    public static int[][] sound_mode = {
            { EnumSoundMode.SNDMODE_STANDARD, R.string.sndmode_standard_string },
            { EnumSoundMode.SNDMODE_NEWS, R.string.sndmode_dialog_string },
            { EnumSoundMode.SNDMODE_MUSIC, R.string.sndmode_music_string },
            { EnumSoundMode.SNDMODE_MOVIE, R.string.sndmode_movie_string },
            { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite1_string },
            { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite2_string },
            { EnumSoundMode.SNDMODE_USER, R.string.sndmode_user_string } };
    /**
     * AutoAdjust
     */
    public static int[][] auto_adjust = { { 0, R.string.auto_adjust } };

    /**
     * SPDIFOutput
     */
    public static int[][] SPDIF_output = {
            { EnumSoundSpdif.SPDIF_OFF, R.string.off },
            { EnumSoundSpdif.SPDIF_PCM, R.string.spdif_pcm_string },
            { EnumSoundSpdif.SPDIF_RAWDATA, R.string.spdif_rawdata_string } };

    /**
     * HangMode
     */
    public static int[][] hang_mode = {
            { EnumSoundfield.SNDFIELD_HANG, R.string.on },
            { EnumSoundfield.SNDFIELD_DESKTOP, R.string.off } };

    /**
     * 3DMode
     */
    public static int[][] menuof_3D_mode = { { 0, R.string.off },
            { 1, R.string.menuof_2dto3d_string },
            { 2, R.string.menuof3d_leftright_string },
            { 3, R.string.menuof3d_updown_string },
            { 4, R.string.menuof3d_auto_string },
            { 5, R.string.menuof3d_frame_string },
            { 6, R.string.menuof3d_checkbox_string },
            { 7, R.string.menuof3d_line_string },
            { 8, R.string.menuof3d_field_string },
            { 9, R.string.menuof3d_frame2_string } };

    /**
     * DEMO_SR
     */
    public static int[][] demo_SR = { { 0, R.string.off }, { 1, R.string.on },
            { 2, R.string.demo_string } };

    /**
     * Power_Demo
     */
    public static int[][] Power_demo = { { 0, R.string.off }, { 1, R.string.on } };
    
    /**
     * Auto_Powerdown
     */
    public static int[][] Auto_Powerdown = { { 0, R.string.off }, { 1, R.string.on } };
    /**
     * Auto_Standby
     */
    public static int[][] Auto_Standby = { { 0, R.string.off }, { 1, R.string.onehrs } 
    , { 2, R.string.twohrs } 
    , { 3, R.string.threehrs } 
    , { 4, R.string.fourhrs } };
    /**
     * ColorSystem
     */
    public static int[][] color_system = {
            { EnumAtvClrsys.CLRSYS_NTSC, R.string.clrsys_ntsc_string },
            { EnumAtvClrsys.CLRSYS_PAL, R.string.clrsys_pal_string } };
    /**
     * AudioSystem
     */
    public static int[][] audio_system = {
            { EnumAtvAudsys.AUDSYS_DK, R.string.audsys_DK_string },
            { EnumAtvAudsys.AUDSYS_I, R.string.audsys_I_string },
            { EnumAtvAudsys.AUDSYS_BG, R.string.audsys_BG_string },
            { EnumAtvAudsys.AUDSYS_M, R.string.audsys_M_string } };
    /**
     * ChangeModeEnable
     */
    public static int[][] change_mode_enable = {
            { 0, R.string.ModeEnable_string },
            { 1, R.string.DisModeEnable_string } };

}
