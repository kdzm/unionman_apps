package com.unionman.quicksetting.interfaces;

import com.hisilicon.android.tvapi.constant.EnumPictureAspect;
import com.hisilicon.android.tvapi.constant.EnumPictureMode;
import com.hisilicon.android.tvapi.constant.EnumSoundMode;
import com.hisilicon.android.tvapi.constant.EnumSoundSpdif;
import com.unionman.quicksetting.R;

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
     * MEMCLevel
     */
    public static int[][] MEMC_LEVEL = { { 0, R.string.repeat_string },
            { 1, R.string.MEMC1_string }, { 2, R.string.MEMC2_string },
            { 3, R.string.MEMC3_string } };

    /**
     * PictureAspect
     */
    public static int[][] picture_aspect = {
            { EnumPictureAspect.ASPECT_AUTO, R.string.aspect_auto_string },
            { EnumPictureAspect.ASPECT_16_9, R.string.aspect_16_9_string },
            { EnumPictureAspect.ASPECT_4_3, R.string.aspect_4_3_string },
            { EnumPictureAspect.ASPECT_PANORAMA, R.string.aspect_panorama_string },
            { EnumPictureAspect.ASPECT_SUBTITLE, R.string.aspect_subtitle_string },
            { EnumPictureAspect.ASPECT_CINEMA, R.string.aspect_film_string },
            { EnumPictureAspect.ASPECT_ZOOM, R.string.aspect_zoom_string },
            { EnumPictureAspect.ASPECT_ZOOM1, R.string.aspect_zoom1_string },
            { EnumPictureAspect.ASPECT_ZOOM2, R.string.aspect_zoom2_string },
            { EnumPictureAspect.ASPECT_POINT2POINT, R.string.aspect_pointtopoint_string } };

    /**
     * CloseOrOpen
     */
    public static int[][] on_off = { 
	        { 0, R.string.off },
            { 1, R.string.on } };
    /**
     * 3D
     */
    public static int[][] menuof3d_mode = {
	        { 0, R.string.off }, 
	        { 1, R.string.menuof3d_2dto3d_string },
            { 2, R.string.menuof3d_leftright_string }, 
			{ 3, R.string.menuof3d_updown_string }, 
			{ 4, R.string.menuof3d_auto_string },
            { 5, R.string.menuof3d_frame_string },
			{ 6, R.string.menuof3d_checkbox_string },
			{ 7, R.string.menuof3d_line_string },
            { 8, R.string.menuof3d_field_string }, 
			{ 9, R.string.menuof3d_framealter_string } };
  
    /**
     * MetSoundMode
     */
    public static int[][] sound_mode = {
            { EnumSoundMode.SNDMODE_STANDARD, R.string.sndmode_standard_string },
            { EnumSoundMode.SNDMODE_NEWS, R.string.sndmode_news_string },
            { EnumSoundMode.SNDMODE_MUSIC, R.string.sndmode_music_string },
            { EnumSoundMode.SNDMODE_MOVIE, R.string.sndmode_movie_string },
            { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite1_string },
            { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite2_string },
            { EnumSoundMode.SNDMODE_USER, R.string.sndmode_user_string } };
    /**
     * SPDIF output
     */
    public static int[][] SPDIF_output = {
            { EnumSoundSpdif.SPDIF_OFF, R.string.off },
            { EnumSoundSpdif.SPDIF_PCM, R.string.spdif_pcm_string },
            { EnumSoundSpdif.SPDIF_RAWDATA, R.string.spdif_rawdata_string } };

    /**
     * demo_SR
     */
    public static int[][] demo_SR = { 
	        { 0, R.string.off },
            { 1, R.string.on }, 
			{ 2, R.string.split_screen_string } };

}
