/**
 * File name : ChannelInfo.java
 * <p>
 * Author information : pan_zhijun
 * <p>
 * Create time : 2012-10-8, 上午10:55:52
 * <p>
 * Copyright statement : Copyright (c) 2009-2012 CIeNET Ltd. All rights reserved
 * <p>
 * Review records :
 * <p>
 */

package com.um.atv.bean;

import java.io.Serializable;

/**
 * The entity class channel
 */
public class ChannelInfo implements Serializable {
    /** sId */
    private static final long serialVersionUID = 1L;
    /** The name of the database table */
    public static final String CHANNEL_TABLE = "channel";
    /** Database table fields channelid */
    public static final String TAB_ID = "channelInfo_id";
    /** Database table fields _ channel name */
    public static final String TAB_NAME = "channelInfo_name";
    /** Database table fields _ channel name */
    public static final String TAB_PINYIN = "channelInfo_pinyin";
    /** Database table fields _ categoriesid */
    public static final String TAB_TYPE_ID = "channelInfo_typeId";
    /** Database table fields _ index */
    public static final String TAB_INDEX = "channelInfo_index";
    /** Database table fields _ whether collection */
    public static final String TAB_IS_COLLECT = "channelInfo_isCollect";
    /** Collection time */
    public static final String TAB_COLLECT_TIME = "collect_time";
    /** Database table fields _ logo url */
    public static final String TAB_ICON_URL = "channelInfo_iconUrl";
    /** Database table fields _ playback source address */
    public static final String TAB_PLAY_URL = "channelInfo_playUrl";
    public static final String TAB_LOCAL_ICON_URL = "channelInfo_localIcon";
    public static final String TAB_CHANNEL_PROGRAM = "channelInfo_program";
    public static final String TAB_CHANNEL_LAST_SOURCE = "channe_last_source";
    /** Channelid */
    public String mChannelId = "id";
    /** Channel name */
    public String mChannelName = "name";
    /** Type id */
    public String mTypeId = "id";
    /** The index number */
    public String mChannelIndex = "";
    /** Whether the collection */
    public boolean isCollect = false;
    /** Collection time */
    public String mCollectTime = "0";
    /**
     * logo url
     */
    public String mIconUrl = "";
    /** The playback source address */
    public String mPlayUrl = "";
    /** The local station path */
    public String mLocalChannelIconPath = null;
    public String mChannelPromgram = null;
    public String mLastSourceIndex = "0";

    public ChannelInfo(String id, String name) {
        super();
        this.mChannelId = id;
        this.mChannelName = name;
    }

    @Override
    public String toString() {
        return "ChannelInfo [id=" + mChannelId + ", name=" + mChannelName
                + ", typeId=" + mTypeId + ", index=" + mChannelIndex
                + ", isCollect=" + isCollect + ", iconUrl=" + mIconUrl
                + ", playUrl=" + mPlayUrl + ", channelPromgram="
                + mChannelPromgram + "]";
    }

    public String getId() {
        return mChannelId;
    }

    public void setId(String id) {
        this.mChannelId = id;
    }

    public String getName() {
        return mChannelName;
    }

    public void setName(String name) {
        this.mChannelName = name;
    }

}
