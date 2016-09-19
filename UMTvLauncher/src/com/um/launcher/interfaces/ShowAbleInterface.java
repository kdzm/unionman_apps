
package com.um.launcher.interfaces;

import android.view.View;

/**
 * The interface of show view
 *
 * @author huyq
 */
public interface ShowAbleInterface {

    /**
     * when current page is show, set focus or do something you want
     */
    public void isShow();

    /**
     * get id of current screen
     *
     * @return
     */
    public int getId();

    /**
     * get imageViews arrays of current screen
     *
     * @return
     */
    public View[] getImgViews();
}
