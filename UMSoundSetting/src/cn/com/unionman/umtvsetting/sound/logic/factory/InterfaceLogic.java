package cn.com.unionman.umtvsetting.sound.logic.factory;

import java.util.List;

import android.os.Handler;

import cn.com.unionman.umtvsetting.sound.model.WidgetType;

/**
 * interface of Logic
 *
 * @author wangchuanjian
 *
 */
public interface InterfaceLogic {
    /**
     * get list of WidgetType
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
}
