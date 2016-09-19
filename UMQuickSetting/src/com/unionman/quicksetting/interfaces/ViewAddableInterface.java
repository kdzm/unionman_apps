package com.unionman.quicksetting.interfaces;

/**
 * The interface of ViewAddable
 * 
 * @author huyq
 * 
 */
public interface ViewAddableInterface {
    /**
     * is add to windowManager
     * 
     * @return
     */
    boolean isAddedToWmanager();

    /**
     * set boolean value of is add to windowManager
     * 
     * @param isAddedToWmanager
     */
    void setAddedToWmanager(boolean isAddedToWmanager);

    /**
     * remove message from handler
     */
    void removeMsg();

    /**
     * get x-axis offset
     * 
     * @return
     */
    int getViewAddableX();

    /**
     * get y-axis offset
     * 
     * @return
     */
    int getViewAddableY();

    /**
     * get width
     * 
     * @return
     */
    int getViewAddableWidth();

    /**
     * get height
     * 
     * @return
     */
    int getViewAddableHeight();

    /**
     * send disappear message to handler
     */
    void sendDisappearMsg();
}
