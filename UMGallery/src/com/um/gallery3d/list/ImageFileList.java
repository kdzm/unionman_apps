
package com.um.gallery3d.list;

import java.util.List;

import android.os.Parcelable;



public abstract class ImageFileList implements Parcelable {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

  /*  public abstract ImageModel getPreImageInfo(List < ImageModel > list);

    public abstract ImageModel getNextImageInfo(List < ImageModel > list);

    public abstract ImageModel getNextImageInfo_NoCycle(List < ImageModel > list);
    
    public abstract ImageModel getPreImageInfo_NoCycle(List < ImageModel > list);
*/

    public abstract ImageModel getCurrImageInfo();
    

}
