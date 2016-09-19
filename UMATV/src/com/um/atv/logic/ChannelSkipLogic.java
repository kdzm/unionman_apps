package com.um.atv.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import com.hisilicon.android.tvapi.vo.TvChannelAttr;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.um.atv.R;
import com.um.atv.interfaces.ATVChannelInterface;
import com.um.atv.interfaces.InterfaceValueMaps;
import com.um.atv.logic.factory.InterfaceLogic;
import com.um.atv.model.WidgetType;
import com.um.atv.model.WidgetType.AccessSysValueInterface;
import com.um.atv.util.Util;

/**
 * ChannelSkipLogic
 *
 * @author wangchuanjian
 *
 */
public class ChannelSkipLogic implements InterfaceLogic {
    private Context mContext;

    // private WidgetType mSkips;// ChannelSkip
    // private List<WidgetType> mWidgetList = null;
    // private int mCurProg = 0;
    // private int[][] mSkipOnOffValue = InterfaceValueMaps.on_off;

    public ChannelSkipLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        final int mCurProg = ATVChannelInterface.getCurProgNumber();

        // ChannelSkip
        WidgetType mSkips = new WidgetType();
        // set name for ChannelSkip
        mSkips.setName(res.getStringArray(R.array.channel_setting)[7]);
        // set type for ChannelSkip
        mSkips.setType(WidgetType.TYPE_SELECTOR);
        mSkips.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (i == 1) {
                    ATVChannelInterface.skip(mCurProg, true);
                } else {
                    ATVChannelInterface.skip(mCurProg, false);
                }
                return i;
            }

            @Override
            public int getSysValue() {
                TvProgram tvpro = ATVChannelInterface.getProgInfo(mCurProg);
                TvChannelAttr channelattr = tvpro.getStChannelAttr();
                boolean res = channelattr.isbSkip();
                int i = 0;
                if (res)
                    i = 1;
                else
                    i = 0;

                return i;
            }
        });
        // set data for ChannelSkip
        mSkips.setData(Util.createArrayOfParameters(InterfaceValueMaps.on_off));
        mWidgetList.add(mSkips);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
