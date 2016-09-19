
package com.unionman.netsetup.view.setting;

import java.io.IOException;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.netsetup.R;

/**
 * Network settings menu two
 *
 * @author huyq
 */
public class NetSettingTest extends LinearLayout implements
        View.OnFocusChangeListener {

    private Context mContext;
    // Control NetSettingDialog display content
    private Handler mHandler;
    // private LogicFactory mLogicFactory;

    // text of Ethernet
    private TextView mGWTestTxt;
    // text of wifi
    private TextView mDNSTestTxt;
    // list of text
    private TextView[] mTextList;

    public NetSettingTest(Context context, Handler handler) {
        super(context);
        mContext = context;
        mHandler = handler;
        // mLogicFactory = new LogicFactory(mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        View parent = inflater.inflate(R.layout.setting_net_test, this);
        initView(parent, 0);
    }

    /**
     * The initialization of view
     *
     * @param parent
     * @param focus
     */
    private void initView(View parent, int focus) {
        mGWTestTxt = (TextView) parent.findViewById(R.id.gw_testing_txt);
        mGWTestTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//            	String resault = "failure!";
//            	if(GWTest()){
//            		resault = "success!";
//            	}
//            	Toast	toast = Toast.makeText(mContext,
//            			resault, Toast.LENGTH_LONG);
//                		   toast.setGravity(Gravity.CENTER, 0, 0);
//                		   toast.show();
            }
        });
        mGWTestTxt.setOnFocusChangeListener(this);

        mDNSTestTxt = (TextView) parent.findViewById(R.id.dns_testing_txt);
        mDNSTestTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//            	String resault = "failure!";
//            	if(DNSTest()){
//            		resault = "success!";
//            	}
//            	Toast	toast = Toast.makeText(mContext,
//            			resault, Toast.LENGTH_LONG);
//                		   toast.setGravity(Gravity.CENTER, 0, 0);
//                		   toast.show();
            }
        });
        mDNSTestTxt.setOnFocusChangeListener(this);

        mTextList = new TextView[] {
                mGWTestTxt, mDNSTestTxt
        };
        mTextList[focus].requestFocus();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.setBackgroundResource(R.drawable.launcher_set_focus);
            ((TextView) v).setTextColor(Color.parseColor(mContext
                    .getResources().getStringArray(R.array.text_color)[3]));
        } else {
            v.setBackgroundResource(R.drawable.button_transparent);
            ((TextView) v).setTextColor(Color.parseColor(mContext
                    .getResources().getStringArray(R.array.text_color)[0]));

        }
    }
    

}
