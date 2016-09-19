package com.um.controller.frequentprog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hjan on 2014/4/18.
 */
public class FrequentProg implements Parcelable {

    private int progId;
    private String progName;
    private int watchDuration;
    private int watchTimes;
    private int progMode;

    public FrequentProg() {
    }

    public FrequentProg(int progId, String progName, int watchDuration, int watchTimes, int mode) {
        this.progId = progId;
        this.progName = progName;
        this.watchDuration = watchDuration;
        this.watchTimes = watchTimes;
        this.progMode = mode;
    }

    public int getProgId() {
        return progId;
    }

    public void setProgId(int progId) {
        this.progId = progId;
    }

    public String getProgName() {
        return progName;
    }

    public void setProgName(String progName) {
        this.progName = progName;
    }

    public int getWatchDuration() {
        return watchDuration;
    }

    public void setWatchDuration(int watchDuration) {
        this.watchDuration = watchDuration;
    }

    public int getWatchTimes() {
        return watchTimes;
    }

    public void setWatchTimes(int watchTimes) {
        this.watchTimes = watchTimes;
    }

    public int getProgMode() {
        return progMode;
    }

    public void setProgMode(int progMode) {
        this.progMode = progMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(progId);
        parcel.writeString(progName);
        parcel.writeInt(watchDuration);
        parcel.writeInt(watchTimes);
        parcel.writeInt(progMode);
    }

    //该静态域是必须要有的，而且名字必须是CREATOR，否则会出错
    public static final Creator<FrequentProg> CREATOR =
            new Creator<FrequentProg>() {

                @Override
                public FrequentProg createFromParcel(Parcel source) {

                    //从Parcel读取通过writeToParcel方法写入的Person的相关成员信息
                    int id = source.readInt();
                    String name = source.readString();
                    int duration = source.readInt();
                    int times = source.readInt();
                    int mode = source.readInt();

                    //更加读取到的信息，创建返回Person对象
                    return new FrequentProg(id, name, duration, times, mode);
                }

                @Override
                public FrequentProg[] newArray(int size) {

                    //返回Person对象数组
                    return new FrequentProg[size];
                }
            };
}