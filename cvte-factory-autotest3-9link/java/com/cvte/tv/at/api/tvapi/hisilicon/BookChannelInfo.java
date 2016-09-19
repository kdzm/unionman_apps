package com.cvte.tv.at.api.tvapi.hisilicon;
import java.util.Date;

/**
 * 预约节目信息，临时存储，有真实环境后即可删除
 *
 */
public class BookChannelInfo {
    private String channelName = "";
    private Date mStartDate = null;
    private String eventName = "";
    private int index = 0;
    private boolean isDeleteState = false;

    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    public Date getStartDate() {
        return mStartDate;
    }
    public void setStartDate(Date startDate) {
        this.mStartDate = startDate;
    }
    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public boolean isDeleteState() {
        return isDeleteState;
    }
    public void setDeleteState(boolean deleteState) {
        this.isDeleteState = deleteState;
    }
}