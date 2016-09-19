package com.um.interfaces;

import com.hisilicon.android.tvapi.constant.EnumAtvAudsys;
import com.hisilicon.android.tvapi.constant.EnumAtvClrsys;
import com.hisilicon.android.tvapi.constant.EnumPictureAspect;
import com.hisilicon.android.tvapi.constant.EnumPictureClrtmp;
import com.hisilicon.android.tvapi.constant.EnumPictureMode;
import com.hisilicon.android.tvapi.constant.EnumSoundMode;
import com.hisilicon.android.tvapi.constant.EnumSoundSpdif;
import com.hisilicon.android.tvapi.constant.EnumSoundfield;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.constant.EnumSoundTrack;
import com.um.dvb.R;

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
            { EnumPictureMode.PICTURE_VIVID, R.string.picmode_vivid_string },
            { EnumPictureMode.PICMODE_SOFTNESS,
                    R.string.picmode_softness_string },
            { EnumPictureMode.PICMODE_USER, R.string.picmode_user_string } };
    /**
     * SoundMode
     */
    public static int[][] sound_mode = {
            { EnumSoundMode.SNDMODE_STANDARD, R.string.sndmode_standard_string },
            { EnumSoundMode.SNDMODE_NEWS, R.string.sndmode_dialog_string },
            { EnumSoundMode.SNDMODE_MUSIC, R.string.sndmode_music_string },
            { EnumSoundMode.SNDMODE_MOVIE, R.string.sndmode_movie_string },
//            { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite1_string },
//            { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite2_string },
            { EnumSoundMode.SNDMODE_USER, R.string.sndmode_user_string } };

    /**
     * PictureAspect
     */
    public static int[][] picture_aspect = {
//            { EnumPictureAspect.ASPECT_AUTO, R.string.aspect_auto_string },
            { EnumPictureAspect.ASPECT_16_9, R.string.aspect_16_9_string },
            { EnumPictureAspect.ASPECT_4_3, R.string.aspect_4_3_string },
            { EnumPictureAspect.ASPECT_SUBTITLE,R.string.aspect_subtitle_string },
            { EnumPictureAspect.ASPECT_CINEMA, R.string.aspect_cinema_string },
//            { EnumPictureAspect.ASPECT_ZOOM, R.string.aspect_zoom_string },
//            { EnumPictureAspect.ASPECT_ZOOM1, R.string.aspect_zoom1_string },
//            { EnumPictureAspect.ASPECT_ZOOM2, R.string.aspect_zoom2_string },
//            { EnumPictureAspect.ASPECT_POINT2POINT,R.string.aspect_point2point_string },
            { EnumPictureAspect.ASPECT_PANORAMA,R.string.aspect_panorama_string }
    };
    public static int[][] track_mode = {
            { EnumSoundTrack.TRACK_STEREO, R.string.stereo },
            { EnumSoundTrack.TRACK_DOUBLE_MONO, R.string.joint_stereo },
            { EnumSoundTrack.TRACK_DOUBLE_LEFT, R.string.left_track },
            { EnumSoundTrack.TRACK_DOUBLE_RIGHT, R.string.right_track },
            { EnumSoundTrack.TRACK_EXCHANGE, R.string.stereo },
            { EnumSoundTrack.TRACK_ONLY_RIGHT, R.string.stereo },
            { EnumSoundTrack.TRACK_ONLY_LEFT, R.string.stereo } };
}
