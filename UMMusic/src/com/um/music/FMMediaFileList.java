package com.um.music;

import java.io.File;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.um.music.Constants;
import com.um.music.MediaFileListService;
import com.um.music.MusicModel;

/**
 * play list control child class
 * @author
 */
public class FMMediaFileList extends MediaFileList {
    private String currPath = null;
    private MediaFileListService mediaFileListService = null;

    public FMMediaFileList(String currPath) {
        this.currPath = currPath;
        setId(Constants.FROMFILEM);
    }

    @Override
    public MusicModel getCurrMusicInfo() {
        File file = new File(this.currPath);
        MusicModel model = new MusicModel();
        model.setPath(currPath);
        model.setTitle(file.getName());
        model.setSize(file.length());
        return model;
    }

    @Override
    public MusicModel getNextMusicInfo(List<MusicModel> list) {
        checkService();
        return mediaFileListService.getMusicInfo(1);
    }

    @Override
    public MusicModel getNextMusicInfo_NoCycle(List<MusicModel> list) {
        checkService();
        return mediaFileListService.getMusicInfo(4);
    }

    @Override
    public MusicModel getPreMusicInfo(List<MusicModel> list) {
        checkService();
        return mediaFileListService.getMusicInfo(2);
    }

    @Override
    public MusicModel getPreMusicInfo_NoCycle(List<MusicModel> list) {
        checkService();
        return mediaFileListService.getMusicInfo(0);
    }
    @Override
    public MusicModel getPreRandomMusicInfo(List<MusicModel> list) {
         checkService();
         return mediaFileListService.getMusicInfo(5);
    }
    @Override
    public MusicModel getNextRandomMusicInfo(List<MusicModel> list) {
        checkService();
        return mediaFileListService.getMusicInfo(3);
    }
    @Override
    public void setSignal() {
        checkService();
        mediaFileListService.setSignal();
    }
    /**
     * check whether the service is null
     * @author
     */
    private void checkService() {
        if (mediaFileListService == null) {
            mediaFileListService = MediaPlaybackActivity.mediaFileListService;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(currPath);
    }

    public static final Parcelable.Creator<FMMediaFileList> CREATOR = new Creator<FMMediaFileList>() {
        public FMMediaFileList createFromParcel(Parcel arg0) {
            String path = arg0.readString();
            FMMediaFileList r = new FMMediaFileList(path);
            return r;
        }
        public FMMediaFileList[] newArray(int arg0) {
            return new FMMediaFileList[arg0];
        }
    };
}
