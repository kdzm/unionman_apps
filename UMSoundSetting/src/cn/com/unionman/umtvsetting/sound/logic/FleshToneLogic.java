package cn.com.unionman.umtvsetting.sound.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import cn.com.unionman.umtvsetting.sound.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.sound.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.sound.util.Util;

import cn.com.unionman.umtvsetting.sound.R;

/**
 * FleshToneLogic
 *
 * @author wangchuanjian
 *
 */
public class FleshToneLogic implements InterfaceLogic {
    private Context mContext;

    // private WidgetType mFleshTone;// FleshTone
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mFleshToneValue = InterfaceValueMaps.flesh_tone;

    public FleshToneLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // FleshTone
        WidgetType mFleshTone = new WidgetType();
        // set name for FleshTone
        mFleshTone.setName(res.getStringArray(R.array.pic_setting)[5]);
        // set type for FleshTone
        mFleshTone.setType(WidgetType.TYPE_SELECTOR);
        mFleshTone.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                return PictureInterface
                        .setFleshTone(InterfaceValueMaps.flesh_tone[i][0]);
            }

            @Override
            public int getSysValue() {
                int mode = PictureInterface.getFleshTone();
                return Util.getIndexFromArray(mode,
                        InterfaceValueMaps.flesh_tone);
            }
        });
        // set data for FleshTone
        mFleshTone.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.flesh_tone));
        mWidgetList.add(mFleshTone);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
