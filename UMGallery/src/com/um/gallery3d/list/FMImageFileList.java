
package com.um.gallery3d.list;

import java.io.File;
import java.util.List;

import com.um.gallery3d.app.Gallery;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;



public class FMImageFileList extends ImageFileList {
    private String currPath = null;
    private String currName = null;

    private ImageFileListService imageFileListService = null;


    public FMImageFileList(String path, String name) {
        this.currPath = path;
        this.currName = name;
        setId(Common.FROMFILEM);
    }

    @Override
    public ImageModel getCurrImageInfo() {
        File file = new File(this.currPath);
        ImageModel model = new ImageModel();
        model.setPath(currPath);
        model.setTitle(currName);
        model.setSize(file.length());
        return model;
    }

/*    @Override
    public ImageModel getNextImageInfo(List < ImageModel > list) {
        checkService();
        return imageFileListService.getImageInfo(1);
    }

    @Override
    public ImageModel getNextImageInfo_NoCycle(List < ImageModel > list) {
        checkService();
        return imageFileListService.getImageInfo(4);
    }

    @Override
    public ImageModel getPreImageInfo(List < ImageModel > list) {
        checkService();
        return imageFileListService.getImageInfo(2);
    }

    @Override
    public ImageModel getRandomImageInfo(List < ImageModel > list) {
        checkService();
        return imageFileListService.getImageInfo(3);
    }*/

    private void checkService() {
        if (imageFileListService == null) {
            imageFileListService = Gallery.imageFileListService;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(currPath);
        arg0.writeString(currName);
    }

    public static final Parcelable.Creator <FMImageFileList> CREATOR = new Creator <FMImageFileList>() {
        public FMImageFileList createFromParcel(Parcel arg0) {
            String path = arg0.readString();
            String name = arg0.readString();
            FMImageFileList r = new FMImageFileList(path,name);
            return r;
        }
        public FMImageFileList[] newArray(int arg0) {
            return new FMImageFileList[arg0];
        }
    };

    
}
