package com.unionman.settings.content.display;

import com.hisilicon.android.tvapi.constant.EnumAtvAudsys;
import com.hisilicon.android.tvapi.constant.EnumAtvClrsys;
import com.hisilicon.android.tvapi.constant.EnumPictureAspect;
import com.hisilicon.android.tvapi.constant.EnumPictureClrtmp;
import com.hisilicon.android.tvapi.constant.EnumPictureMode;
import com.hisilicon.android.tvapi.constant.EnumSoundMode;
import com.hisilicon.android.tvapi.constant.EnumSoundSpdif;
import com.hisilicon.android.tvapi.constant.EnumSoundfield;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.unionman.settings.R;

public class InterfaceValueMaps {

    /**
     * ON or OFF
     */
    public static int[][] on_off = { { 0, R.string.off }, { 1, R.string.on } };

    /**
     * Auto_Standby
     */
    public static int[][] Auto_Standby = { { 0, R.string.off }, { 1, R.string.onehrs } 
    , { 2, R.string.twohrs } 
    , { 3, R.string.threehrs } 
    , { 4, R.string.fourhrs } };
   
}
