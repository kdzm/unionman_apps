package com.cvte.tv.at.util;

import com.cvte.tv.at.api.SysProp;

/**
 * Created by User on 2016/2/18.
 */
public class CVTEBURN {

    public static void create() {
        SysProp.set(Utils.FTS_AGINGMODE, Utils.CENV_ON);
    }

    public static void delete() {
        SysProp.set(Utils.FTS_AGINGMODE, Utils.CENV_OFF);
    }

    public static boolean exist() {
        return Utils.CENV_ON.equals(SysProp.get(Utils.FTS_AGINGMODE, Utils.CENV_OFF));
    }
}
