package com.cvte.tv.at.api.tvapi.hisilicon;


import com.cvte.tv.at.util.Utils.EnumChannelType;
import com.cvte.tv.at.util.Utils.EnumInputSourceCategory;

/**
 * Created by User on 2015/11/24.
 */
public class EntityChannel {

    public int channelId;
    public int LCN;
    public String serviceName;
    public EnumChannelType serviceType;
    public EnumInputSourceCategory tvCategory;
    public boolean channelLocked;
    public boolean channelSkiped;
    public boolean channelScrambled;
    public int channelFavoriteGroup;

    public EntityChannel() { /* compiled code */ }

}
