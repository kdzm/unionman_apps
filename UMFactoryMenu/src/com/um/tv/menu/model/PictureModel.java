package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class PictureModel extends Model {
    public String mDisplayName = "Picture Mode";
    public String[] mItemNames = Utils.ItemsPictureMode;

    public PictureModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = mDisplayName;

        initChildren();
    }

    private void initChildren() {
        ChoiceModel sourceModel = new ChoiceModel(mContext, mWindow, mFactory,
                ChoiceModel.TypePicModeTvSource);
        sourceModel.mName = mItemNames[0];
        addChild(sourceModel);

        ChoiceModel picMode = new ChoiceModel(mContext, mWindow, mFactory,
                ChoiceModel.TypePictureModeSource);
        picMode.mName = mItemNames[1];
        addChild(picMode);

        RangeModel brightness = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypePictureBrightness);
        brightness.mName = mItemNames[2];
        picMode.registeSourceChangeListener(brightness.getSourceChangeListener());
        addChild(brightness);

        RangeModel contrast = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypePictureContrast);
        contrast.mName = mItemNames[3];
        picMode.registeSourceChangeListener(contrast.getSourceChangeListener());
        addChild(contrast);

        RangeModel saturation = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypePictureSaturation);
        saturation.mName = mItemNames[4];
        picMode.registeSourceChangeListener(saturation.getSourceChangeListener());
        addChild(saturation);

        RangeModel hue = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypePictureHue);
        hue.mName = mItemNames[5];
        picMode.registeSourceChangeListener(hue.getSourceChangeListener());
        addChild(hue);

        RangeModel sharpness = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypePictureSharpness);
        sharpness.mName = mItemNames[6];
        picMode.registeSourceChangeListener(sharpness.getSourceChangeListener());
        addChild(sharpness);

        RangeModel backlight = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypePictureBacklight);
        backlight.mName = mItemNames[7];
        picMode.registeSourceChangeListener(backlight.getSourceChangeListener());
        addChild(backlight);
    }

    @Override
    public View getView(Context context, int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        return mChildrenList.get(position).getView(context, position,
                convertView, parent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).changeValue(direct, position, view);
    }
}
