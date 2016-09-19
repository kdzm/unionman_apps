package com.cvte.tv.at.util;

import com.cvte.tv.at.util.Utils.SourceEnum;

/**
 * Created by User on 2014/12/4.
 */
public class SourceItem {
    private SourceEnum SrcItem;
    private int SrcID;

    public SourceItem(SourceEnum src, int id) {
        SrcItem = src;
        SrcID = id;
    }

    public SourceEnum GetSrcItem() {
        return SrcItem;
    }

    public int GetSrcID() {
        return SrcID;
    }
}
