package com.um.launcher.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.um.launcher.MainActivity;
import com.um.launcher.R;
import com.um.launcher.constant.SettingsConstants;

/**
 * Created by hjian on 2015/3/25.
 */
public class PopupMenu extends PopupWindow implements View.OnKeyListener, View.OnClickListener{
	
	private static final String TAG = "PopupMenu";
	private BluetoothAdapter  bluetoothAdapter;
    private MainActivity mContext;
    
    private View divider_01;
    private  Button menu02;
    
    public PopupMenu(MainActivity context) {
        super(context);
        mContext = context;

        init();
    }

    private void init() {
        setBackgroundDrawable(mContext.getResources().getDrawable(R.color.popup_menu_bg_color));
        setFocusable(true);
        setWindowLayoutMode(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        View view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.popup_menu, null);
        Button menu01 = (Button) view.findViewById(R.id.menu01);
        menu02 = (Button) view.findViewById(R.id.menu02);
        Button menu03 = (Button) view.findViewById(R.id.menu03);
        divider_01 = (View) view.findViewById(R.id.divider_01);
        menu01.setOnKeyListener(this);
        menu02.setOnKeyListener(this);
        menu03.setOnKeyListener(this);
        menu01.setOnClickListener(this);
        menu02.setOnClickListener(this);
        menu03.setOnClickListener(this);
        setContentView(view);
    }

    private void startWifiSetting(){
        ComponentName componentName = new ComponentName("com.unionman.netsetup", "com.unionman.netsetup.MainActivity");
        Intent mIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(SettingsConstants.NetSetup.START_FLAG, SettingsConstants.NetSetup.FLAG_WIFI);
        mIntent.setComponent(componentName);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startToActivity(mIntent);
		mContext.setEnterActivityFlag(MainActivity.ENTER_ACTIVITY_BY_CLICK);
    }

    private void startBluetoothSetting(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter  
                .getDefaultAdapter();  
        Log.i("GAGA","mBluetoothAdapter="+mBluetoothAdapter);
        if (mBluetoothAdapter == null) {  
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_find_bluetooth_driver), Toast.LENGTH_SHORT).show();  
            return;  
        } 
        
        mContext.startToActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
		mContext.setEnterActivityFlag(MainActivity.ENTER_ACTIVITY_BY_CLICK);
       // mContext.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        if (!mBluetoothAdapter.isEnabled()) {  
        	mBluetoothAdapter.enable();  
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        mContext.delay();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                dismiss();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu01:
                startWifiSetting();
                break;
            case R.id.menu02:
                startBluetoothSetting();
                break;
            case R.id.menu03:
                break;
            default:
                break;
        }
        dismiss();
    }
    
    public void setBluetoothViewInvisiable(){
    	Log.i(TAG, "===setBluetoothViewInvisiable===");
		divider_01.setVisibility(View.INVISIBLE);
		menu02.setVisibility(View.INVISIBLE);
    }
}
