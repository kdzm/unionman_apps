package cn.com.unionman.umtvsetting.picture.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.Refreshable;
import cn.com.unionman.umtvsetting.picture.util.Constant;

import cn.com.unionman.umtvsetting.picture.R;

/**
 * a type of widget used in adjust VGA
 *
 * @author tang_shengchang HiSI.ltd <br>
 *         Radio interface listview
 */
public class VGASelectorView extends RelativeLayout implements Refreshable,
        View.OnKeyListener, View.OnFocusChangeListener {
    private static final String TAG = "VGASelectorView";
    // listView of menu
    public ListView menuListView;
    // text of menu
    private TextView menuTxt;
    // button of menu
    private Button menuBtn;
    private Context mContext;
    // menu container
    private CustomSettingView mCustomSettingView;
    // type of widget
    private WidgetType mWidgetType;
    // private int mSystem = 0;
    // is has focus or not
    private boolean isHasFocus = false;

    public VGASelectorView(Context context, VGASelectorView mselectorView) {
        super(context);
        mContext = context;

    }

    public VGASelectorView(CustomSettingView customSettingView,
            Context context, WidgetType widgetType,
            List<WidgetType> widgetTypeList) {
        super(context);
        mContext = context;
        this.mCustomSettingView = customSettingView;
        mWidgetType = widgetType;
        if (mWidgetType == null
                || mWidgetType.getmAccessSysValueInterface() == null) {
            throw new NullPointerException(
                    "mWidgetType is null || mWidgetType.getmAccessDataInterface() is null");
        }
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mLinflater.inflate(R.layout.vga_view, this);
        initView();

    }

    /**
     * The initialization of selector
     */
    private void initView() {
        menuTxt = (TextView) findViewById(R.id.menu_name_txt);
        menuTxt.setText(mWidgetType.getName());
        menuTxt.setTextColor(Color.parseColor(mContext.getResources()
                .getStringArray(R.array.text_colorchange)[1]));
        if (Constant.LOG_TAG) {
            Log.i(TAG, mWidgetType.getName() + "=length="
                    + mWidgetType.getName().length());
        }
        menuBtn = (Button) findViewById(R.id.menu_btn);
        menuBtn.setOnKeyListener(this);
        menuBtn.setOnFocusChangeListener(this);
        findViewById(R.id.vga_selector).setBackgroundResource(
                R.drawable.setting_select2);
        findViewById(R.id.vga_selector).setBackgroundResource(
                R.drawable.button_transparent);
        // mSystem = mWidgetType.getmAccessSysValueInterface().getSysValue();

    }

    @Override
    public WidgetType getWidgetType() {
        return mWidgetType;
    }

    @Override
    public boolean getIsFocus() {
        return isHasFocus;
    }

    @Override
    public void refreshUI() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
        case R.id.menu_btn:
            isHasFocus = hasFocus;
            if (isHasFocus) {
                menuBtn.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[2]));

                menuTxt.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[0]));
                findViewById(R.id.vga_selector).setBackgroundResource(
                        R.drawable.setting_select2);
            } else {
                menuTxt.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[1]));
                menuBtn.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[1]));
                findViewById(R.id.vga_selector).setBackgroundResource(
                        R.drawable.button_transparent);
            }
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onFocusChange flag = " + isHasFocus);
            }

            break;

        default:
            break;
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
        case R.id.menu_btn:
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (mCustomSettingView.onKey(v, keyCode, event)) {
                    return true;
                }
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "keycode = " + keyCode + "; event = " + event);
                }
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (mWidgetType.isIshaveArrow() == false) {
                        mWidgetType.getmAccessSysValueInterface()
                                .setSysValue(0);
                        return true;
                    }
                case KeyEvent.KEYCODE_BACK:
                    if (Constant.LOG_TAG) {
                        Log.d(TAG, "back !!!");
                    }
                    break;
                default:
                    break;
                }
            }

            break;

        default:
            break;
        }
        return false;
    }
}
