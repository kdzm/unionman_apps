
package com.unionman.netsetup.logic.factory;

import java.util.List;

import android.os.Handler;

import com.unionman.netsetup.model.WidgetType;

/**
 * the interface of logic
 *
 * @author huyq
 */
public interface InterfaceLogic {

    /**
     * get WidgetType list
     *
     * @return
     */
    public List<WidgetType> getWidgetTypeList();

    /**
     * set handler
     *
     * @param handler
     */
    public void setHandler(Handler handler);

    /**
     * dismiss dialog
     */
    public void dismissDialog();
}
