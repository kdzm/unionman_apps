package com.cvte.tv.at.api.tvapi;

import android.content.Context;

import com.cvte.tv.at.api.CommonAPI;
import com.cvte.tv.at.api.SysProp;
import com.cvte.tv.at.util.Utils;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.impl.AtvChannelImpl;

import java.io.File;
import java.io.IOException;

//import com.cvte.tv.api.hisilicon.TvManagerHelper;

//import com.cvte.tv.api.at.api.EditChannel;


/**
 * ICvteFacAPI Factory API 统一接口文件
 *
 * @author Leajen_Ren
 * @version V1.1 2014-06-24 更新了变量命名
 * @Package cvte.factory.api.mtk.api
 * @Description mtk的API，这的所有功能都是基于MTK的源码和API编写，用来实现需要用MTK才能完成的功能，
 */
public class HisiFunAPI {
    // It is for log.
    public static final boolean DEBUG_FLAG = true;
    //    private TvManager mTvManager;
    private int ATVChannelSize = 0;
    private int DTVChannelSize = 0;
    private int DTVRouteType = 0;

    private static CommonAPI sComapi = null;
    private static Context sContext;

    public HisiFunAPI(Context sContext) {
        setsContext(sContext);
        sComapi = CommonAPI.getInstance(getsContext());
    }

    private static Context getsContext() {
        return sContext;
    }

    private static void setsContext(Context sContext) {
        HisiFunAPI.sContext = sContext;
    }

    public void CleanChannelData() {
        cleanATVChannelList();
        cleanDTVChannelList();
    }

    public void cleanATVChannelList() {
        //Clean All Channel Data and change source to ATV
        AtvChannelImpl.getInstance().clearAll();//ATV Clean All
    }


    public void cleanDTVChannelList() {
        //Clean All Channel Data and change source to ATV
    }

    public boolean importTVProg(String folderpath) {
        Utils.LOG("<Hisi> importTVProg folderpath=" + folderpath);
        boolean rel = UmtvManager.getInstance().getFactory().importTVProg(folderpath) == 0;
        try {
            Runtime.getRuntime().exec("sync");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rel;
    }

    public boolean exportTVProg(String folderpath) {
        Utils.LOG("<Hisi> exportTVProg folderpath=" + folderpath);
        boolean rel = UmtvManager.getInstance().getFactory().exportTVProg(folderpath) == 0;
        try {
            Runtime.getRuntime().exec("sync");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rel;
    }

    public boolean boardFactoryReset() {
        Utils.LOG("<Hisi> boardFactoryReset");
        boolean rel = UmtvManager.getInstance().getFactory().boardFactoryReset() == 0;
        try {
            Runtime.getRuntime().exec("sync");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rel;
    }


    public boolean dtvEvnInit() {
        Utils.LOG("<Hisi> dtvEvnInit");
        return UmtvManager.getInstance().getFactory().dtvEvnInit() == 0;
    }

    public boolean dtvEvnDeinit() {
        Utils.LOG("<Hisi> dtvEvnDeinit");
        return UmtvManager.getInstance().getFactory().dtvEvnDeinit() == 0;
    }

    public boolean dtvPlay(int chid) {
        Utils.LOG("<Hisi> dtvPlay chid=" + chid);
        return UmtvManager.getInstance().getFactory().dtvPlay(chid) == 0;
    }

    //卡插入返回true，否则返回false
    public boolean CiIsInserted() {
        Utils.LOG("<Hisi> CiIsInserted rel=Start");
        boolean rel = UmtvManager.getInstance().getFactory().dvbGetCardStatus();
        Utils.LOG("<Hisi> CiIsInserted rel=" + rel);
        return rel;
    }

    public boolean CiCardDataCheck() {
        Utils.LOG("<Hisi> CiCardDataCheck chid= Start");
        File ci = new File(Utils.CICardStateInfo);
        ci.delete();

        SystemCmd("cicarddetect");
        Sleep(500);

        Utils.LOG("<Hisi> CiCardDataCheck ci.exists()=" + ci.exists());
        if (ci.exists()) {
            String cistae = new String(sComapi.readBytes(ci));
            Utils.LOG("<Hisi> cistae:\n" + cistae);
            if (cistae.contains(Utils.CICardState_READY) ||
                    cistae.contains(Utils.CICardState_RX))
                return true;
            else
                return false;
        } else
            return false;
//        String str = UmtvManager.getInstance().getFactory().dvbGetCardNo();
//        Utils.LOG("<Hisi> CiCardDataCheck chid=" + str);
//        if ((str == null) || ("".equals(str)))
//            return false;
//        else
//            return true;
    }

    private static void SystemCmd(String Str) {
        SysProp.set("ctl.start", "sys_ctl:" + Str);
        Sleep(300);
    }

    private static void Sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
