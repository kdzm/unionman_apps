
package com.unionman.netsetup.view.setting;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unionman.netsetup.R;

/**
 * Network settings menu two
 *
 * @author huyq
 */
public class NetSetting extends LinearLayout implements
        View.OnFocusChangeListener {

    private Context mContext;
    // Control NetSettingDialog display content
    private Handler mHandler;
    // private LogicFactory mLogicFactory;

/*    // text of Ethernet
    private TextView mEtherText;
    // text of wifi
    private TextView mWifiText;
    // text of state
    private TextView mStateText;
    // text of test
    private TextView mTestText;
    // list of text
    private TextView[] mTextList;*/
    
    private LinearLayout mEtherText;
    private LinearLayout mWifiText;
    private LinearLayout mStateText;
    private LinearLayout mTestText;
    private LinearLayout mAPLayout;
    private LinearLayout[] mTextList;

    public NetSetting(Context context, Handler handler, int focus) {
        super(context);
        mContext = context;
        mHandler = handler;
        // mLogicFactory = new LogicFactory(mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        View parent = inflater.inflate(R.layout.setting_net, this);
        initView(parent, focus);
    }

    /**
     * The initialization of view
     *
     * @param parent
     * @param focus
     */
    private void initView(View parent, int focus) {
        mEtherText = (LinearLayout) parent.findViewById(R.id.ether_setting_ll);
        mEtherText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message message = mHandler.obtainMessage();
                message.what = NetSettingDialog.FLAG_ETHER;
                mHandler.sendMessageDelayed(message, 100);
            }
        });
        mEtherText.setOnFocusChangeListener(this);

        mWifiText = (LinearLayout) parent.findViewById(R.id.wifi_setting_ll);
        mWifiText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message message = mHandler.obtainMessage();
                message.what = NetSettingDialog.FLAG_WIFI;
                mHandler.sendMessageDelayed(message, 100);
            }
        });
        mWifiText.setOnFocusChangeListener(this);

        mStateText = (LinearLayout) parent.findViewById(R.id.net_state_ll);
        mStateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message message = mHandler.obtainMessage();
                message.what = NetSettingDialog.FLAG_STATE;
                mHandler.sendMessageDelayed(message, 100);
            }
        });
        mStateText.setOnFocusChangeListener(this);

        
        mTestText = (LinearLayout) parent.findViewById(R.id.net_test_ll);
        mTestText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message message = mHandler.obtainMessage();
                message.what = NetSettingDialog.FLAG_TEST;
                mHandler.sendMessageDelayed(message, 100);
            }
        });
        mTestText.setOnFocusChangeListener(this);
        
        mAPLayout = (LinearLayout)parent.findViewById(R.id.create_ap_ll);
        mAPLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = Message.obtain();
				msg.what = NetSettingDialog.FLAG_AP;
				mHandler.sendMessageDelayed(msg, 100);
			}
		});
        
        mTextList = new LinearLayout[] {
                mEtherText, mWifiText, mStateText, mTestText, mAPLayout
        };
        mTextList[focus].requestFocus();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {/*
        if (hasFocus) {
            v.setBackgroundResource(R.drawable.launcher_set_focus);
            ((TextView) v).setTextColor(Color.parseColor(mContext
                    .getResources().getStringArray(R.array.text_color)[3]));
        } else {
            v.setBackgroundResource(R.drawable.button_transparent);
            ((TextView) v).setTextColor(Color.parseColor(mContext
                    .getResources().getStringArray(R.array.text_color)[0]));

        }
    */}

}
