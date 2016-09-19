package com.um.tv.menu.model;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.impl.FactoryImpl;
import com.um.tv.menu.R;
import com.um.tv.menu.ui.FactoryWindow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

public class CombinatedModel extends Model {
    private static final String TAG = "CombinatedModel";

    public static final int TypeRoot = -1;
    public static final int TypeADC = 0;
    public static final int TypeWhiteBlance = 1;
    public static final int TypeOverscan = 2;
    public static final int TypePictureMode = 3;
    public static final int TypeNonLinear = 4;
    public static final int TypeNonStandard = 5;
    public static final int TypeSSC = 6;
    public static final int TypePEQ = 7;
    public static final int TypeOtherOpthion = 8;
    public static final int TypeInfo = 9;
    public static final int TypeVIF = 10;
    public static final int TypeVD = 11;
    public static final int TypeAudio = 12;
    public static final int TypeDemod = 13;
    public static final int TypeFactorySingleKey = 14;
    public static final int TypeVByOne = 15;
	public static final int TypePage2 = 16;
	public static final int TypePage3 = 17;
	private Context mContext;
	private FactoryWindow mFactorywindow;
    protected int mType = TypeRoot;

    enum ModeType {
        Combinated,
        Choice,
        Command,
        Range
    }

    public CombinatedModel(Context context, FactoryWindow window,
                           CusFactory factory, int type) {
        super(context, window, factory);
        // TODO Auto-generated constructor stub
        mType = type;
        mContext = context;
        mFactorywindow = window;
    }

    @Override
    public View getView(Context context, int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null || !convertView.getTag().equals(ViewTagMain)) {
            convertView = LayoutInflater.from(context).inflate(R.layout.factory_menu_list_item, null);
            convertView.setTag(ViewTagMain);
        }
        TextView main = (TextView)convertView;
        main.setText(mChildrenList.get(position).mName);
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
    	switch (mType) {
		case TypeVIF:
		case TypeVD:
		case TypeDemod:
			return;

		default:
			break;
		}
    	
    	if(mChildrenList.get(position) instanceof FactorySingleKeyModel){
    		showFactoryCofirmDialog();
    		return;
    	}else if (mChildrenList.get(position) instanceof UMNetUpgradeModel){
    		Intent intent = new Intent();
    		intent.setClassName("cn.com.unionman.umtvsetting.umsysteminfo","cn.com.unionman.umtvsetting.umsysteminfo.UpgradeMainActivity");
    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		mContext.startActivity(intent);
    		mFactorywindow.dismiss();
    		Log.d(TAG,"UMNetUpgradeModel");
    		return;
    	}else if (mChildrenList.get(position) instanceof AndroidSettingModel){
    		Intent intent = new Intent();
    		intent.setAction("android.settings.SETTINGS");
    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		mContext.startActivity(intent);
    		mFactorywindow.dismiss();
    		Log.d(TAG,"AndroidSettingModel");
    		return;
    	}else if (mChildrenList.get(position) instanceof ADBConsoleModel){
    		Intent intent = new Intent();
    		intent.setClassName("com.um.console","com.um.console.MainActivity");
    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   		
    		mContext.startActivity(intent);
    		mFactorywindow.dismiss();
    		Log.d(TAG,"ADBConsoleModel");
    		return;
    	}
    	
        mWindow.updateItems(mChildrenList.get(position));
    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub

    }
    
    private void showFactoryCofirmDialog(){
    	final boolean isFactoryOn = false;//FactoryImpl.getInstance().isFacSingleKeyEnable();
    	Log.d(TAG,"showFactoryCofirmDialog()--->isFacotryOn:" + isFactoryOn);
		Dialog dlg = new AlertDialog.Builder(mContext)
		.setTitle(mContext.getString(R.string.dlg_title_factory_key))
		.setMessage(isFactoryOn ? mContext.getString(R.string.dlg_message_turn_off_factory_key) : mContext.getString(R.string.dlg_message_turn_on_factory_key))
		.setPositiveButton(mContext.getString(R.string.btn_submit), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//FactoryImpl.getInstance().enableFacSingleKey(!isFactoryOn);
			//	Log.d(TAG,"showFactoryCofirmDialog()--->setFactory:" + !isFactoryOn + "		FactoryCurrentStatus:" + FactoryImpl.getInstance().isFacSingleKeyEnable());
			}
		})
		.setNegativeButton(mContext.getString(R.string.btn_cancel), null).create();
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dlg.show();
    }
}
