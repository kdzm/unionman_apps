package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import cn.com.unionman.umtvsetting.picture.interfaces.ATVChannelInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.picture.util.Util;

import com.hisilicon.android.tvapi.vo.TvChannelAttr;
import com.hisilicon.android.tvapi.vo.TvProgram;
import cn.com.unionman.umtvsetting.picture.R;

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
        mSkips.setName(res.getStringArray(R.array.channel_setting)[0]);
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
    
    private static final String TAG = "PictureModeLogic";
    public boolean isHueMode() {
    	// TODO Auto-generated method stub
    	if(TAG.equals("HueModeLogic")){
    		return true;
    	}else{
    		return false;
    	}
    	
    }

}
