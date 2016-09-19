package com.cvte.tv.at.api.tvapi;


import com.cvte.tv.at.util.Utils.EnumInputSourceCategory;
import com.cvte.tv.at.util.Utils.EnumInputStatus;

/**
 * Created by User on 2015/11/24.
 */
public class EntityInputSource {
    public int id;
    public String name;
    public EnumInputStatus status;
    public EnumInputSourceCategory category;

    public EntityInputSource(int id, String name, EnumInputStatus status, EnumInputSourceCategory category) { /* compiled code */
        this.id = id;
        this.name = name;
        this.status = status;
        this.category = category;
    }


}
