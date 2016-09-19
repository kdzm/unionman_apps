package com.um.tv.menu.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
//import com.hisilicon.tv.menu.model.ChoiceModel;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class OtherOptionsModel extends Model {
    private static final String TAG = "OtherOptionsModel";
    public String[] mItemNames = Utils.ItemsOtherOptions;

    public OtherOptionsModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = Utils.DisplayNameOtherOptions;

        initChildren();
    }

    private void initChildren() {
    	ChoiceModel tunerLNA = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeTunerLNA);
    	tunerLNA.mName = mItemNames[0];
        addChild(tunerLNA);
        
        ChoiceModel watchdog = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeWatchDog);
        watchdog.mName = mItemNames[1];
        addChild(watchdog);

        CommandModel restore = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeRestore);
        restore.mName = mItemNames[2];
        addChild(restore);

        ChoiceModel poweron = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypePowerOnMode);
        poweron.mName = mItemNames[3];
        addChild(poweron);

        ChoiceModel uartenable = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeUartEnable);
        uartenable.mName = mItemNames[4];
        addChild(uartenable);

        ChoiceModel uartdebug = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeUartDebug);
        uartdebug.mName = mItemNames[5];
        addChild(uartdebug);

        ChoiceModel testpattern = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeTestPattern);
        testpattern.mName = mItemNames[6];
        addChild(testpattern);

        ChoiceModel agingmode = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeAgingMode);
        agingmode.mName = mItemNames[7];
        addChild(agingmode);

        ChoiceModel panelindex = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypePanelIndex);
        panelindex.mName = mItemNames[8];
        addChild(panelindex);

        ChoiceModel flipmirror = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypePanelFlipMirror);
        flipmirror.mName = mItemNames[9];
        addChild(flipmirror);

        ChoiceModel powermusic = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypePowerMusic);
        powermusic.mName = mItemNames[10];
        addChild(powermusic);

        
        CommandModel usbupdatemac = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeUSBUpdateMac);
        usbupdatemac.mName = mItemNames[11];
        addChild(usbupdatemac);

         ChoiceModel PWMOffset = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypePWMOffset);
         PWMOffset.mName = mItemNames[12];
         addChild(PWMOffset);

		CommandModel resetEeprom = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeResetEeprom);
        resetEeprom.mName = mItemNames[13];
        addChild(resetEeprom);
        

		CommandModel usbupdatecustom = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeUSBUpdateCustom);
		usbupdatecustom.mName = mItemNames[14];
        addChild(usbupdatecustom);
        
        CommandModel updatepqnum = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeUpdatePQNum);
        updatepqnum.mName = mItemNames[15];
        addChild(updatepqnum);
        
        ChoiceModel ddrRefresh = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeDdrRefresh);
        ddrRefresh.mName = mItemNames[16];
        addChild(ddrRefresh);
    }

    @Override
    public View getView(Context context, int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "getView---->position:" + position);
        return mChildrenList.get(position).getView(context, position, convertView, parent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).onItemClick(parent, view, position, id);
    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).changeValue(direct, position, view);
    }
}
