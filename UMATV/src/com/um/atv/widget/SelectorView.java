package com.um.atv.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
//import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.um.atv.R;
import com.um.atv.model.WidgetType;
import com.um.atv.model.WidgetType.Refreshable;
import com.um.atv.util.Constant;

/**
 * The SelectorView WidgetView in second menu.
 *  
 * @author tang_shengchang HiSI.ltd <br>
 *         Radio interface listview
 */
public class SelectorView extends RelativeLayout implements Refreshable,
        View.OnKeyListener, View.OnFocusChangeListener {

    private static final String TAG = "SelectorView";
    private Context mContext;
    // listView of menu
    public ListView menuListView;
    // text of menu
    private TextView menuTxt;
    // type of widget
    private WidgetType mWidgetType;
    // menu of button
    private TextView menuBtn;
    // menu container
    private CustomSettingView mCustomSettingView;
    // list of WidgetType
    private List<WidgetType> mWidgetTypeList = null;
    // is has focus or not
    private boolean ishasFocus = false;
    // length of data
    private int[] mDataLength;
    // index of item
    private int mItemIndex = 0;
    
    public SelectorView(Context context, SelectorView mselectorView) {
        super(context);
        mContext = context;

    }

    public SelectorView(CustomSettingView customSettingView, Context context,
            WidgetType widgetType, List<WidgetType> widgetTypelist) {
        super(context);
        mContext = context;
        this.mCustomSettingView = customSettingView;
        mWidgetType = widgetType;
        mWidgetTypeList = widgetTypelist;
        if (mWidgetType == null
                || mWidgetType.getmAccessSysValueInterface() == null) {
            throw new NullPointerException(
                    "mWidgetType is null || mWidgetType.getmAccessDataInterface() is null");
        }
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mLinflater.inflate(R.layout.selector_view, this);

        // The initialization of selector
        initView();

    }

    /**
     * The initialization of selector
     */
    public void initView() {
        menuTxt = (TextView) findViewById(R.id.selector_name_txt);
        menuTxt.setText(mWidgetType.getName());
        menuTxt.setTextColor(Color.parseColor(mContext.getResources()
                .getStringArray(R.array.text_colorchange)[1]));
        if (Constant.LOG_TAG) {
            Log.i(TAG, mWidgetType.getName() + "=length="
                    + mWidgetType.getName().length());
        }
        menuBtn = (TextView) findViewById(R.id.menu_btn);
        menuBtn.setFocusable(mWidgetType.isEnable());
        ChangeSrsState(mWidgetType.isEnable());
        //findViewById(R.id.button_layout).setBackgroundResource(
                //R.drawable.setting_focus_on);
        //findViewById(R.id.button_layout).setBackgroundResource(
                //R.drawable.setting_bg);
        mItemIndex = mWidgetType.getmAccessSysValueInterface().getSysValue();
        mDataLength = mWidgetType.getData();
        if (Constant.LOG_TAG) {
            Log.d("mSystem", mItemIndex + "");
        }
        if (mWidgetType.isVGAstate()) {
            menuBtn.setText(mDataLength[mItemIndex]);
        } else {
            //menuBtn.setBackgroundResource(R.drawable.vga_arrow);
            menuBtn.setHeight(10);
        }
        menuBtn.setTextColor(Color.parseColor(mContext.getResources()
                .getStringArray(R.array.text_colorchange)[1]));
        menuBtn.setOnKeyListener(this);
        menuBtn.setOnFocusChangeListener(this);
        if (mWidgetType.isIshaveArrow()) {
            findViewById(R.id.left_arrow_img).setVisibility(View.VISIBLE);
            findViewById(R.id.right_arrow_img).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.left_arrow_img).setVisibility(View.INVISIBLE);
            findViewById(R.id.right_arrow_img).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * move to previous option
     *
     * @return
     */
    public boolean moveToPreviousOption() {
        if (ishasFocus == true) {
            mItemIndex--;
            if (mItemIndex < 1) {
            }
            if (mItemIndex < 0) {
                mItemIndex = mDataLength.length - 1;
            }
            if (mWidgetType.isVGAstate()) {
                menuBtn.setText(mDataLength[mItemIndex]);
            } else {
                //menuBtn.setBackgroundResource(R.drawable.vga_arrow);
                menuBtn.setHeight(30);
            }
            mWidgetType.getmAccessSysValueInterface().setSysValue(mItemIndex);
            return true;
        } else {
            return false;
        }
    }

    /**
     * move to next option
     *
     * @return
     */
    public boolean moveToNextOption() {
        if (ishasFocus == true) {
            mItemIndex++;
            if (mItemIndex >= mDataLength.length - 1) {
            }
            if (mItemIndex >= mDataLength.length) {
                mItemIndex = 0;
            }
            if (mWidgetType.isVGAstate()) {
                menuBtn.setText(mDataLength[mItemIndex]);
            } else {
                //menuBtn.setBackgroundResource(R.drawable.vga_arrow);
                menuBtn.setHeight(30);
            }
            mWidgetType.getmAccessSysValueInterface().setSysValue(mItemIndex);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void refreshUI() {
        mItemIndex = mWidgetType.getmAccessSysValueInterface().getSysValue();
        menuBtn.setText(mDataLength[mItemIndex]);
        menuBtn.setFocusable(mWidgetType.isEnable());
        ChangeSrsState(mWidgetType.isEnable());
    }

    /**
     * SRS is open, SRS down arrow for highlighting.SRS off, SRS below the arrow
     * is gray
     *
     * @param isEnable
     */
    private void ChangeSrsState(boolean isEnable) {
        if (isEnable) {
            findViewById(R.id.left_arrow_img).setBackgroundResource(
                    R.drawable.selector_arrow_left);
            findViewById(R.id.right_arrow_img).setBackgroundResource(
                    R.drawable.selector_arrow_right);
        } else {
            findViewById(R.id.left_arrow_img).setBackgroundResource(
                    R.drawable.selector_arrow_left);
            findViewById(R.id.right_arrow_img).setBackgroundResource(
                    R.drawable.selector_arrow_right);
        }
    }

    @Override
    public WidgetType getWidgetType() {
        return mWidgetType;
    }

    @Override
    public boolean getIsFocus() {
        return ishasFocus;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
        case R.id.menu_btn:
            ishasFocus = hasFocus;
            if (ishasFocus) {
                if (mWidgetTypeList.size() > 1) {
                    //findViewById(R.id.button_layout).setBackgroundResource(
                            //R.drawable.setting_focus_on);
                    menuBtn.setTextColor(Color.parseColor(mContext
                            .getResources().getStringArray(
                                    R.array.text_colorchange)[0]));
                } else {
                    findViewById(R.id.menu_btn).setBackgroundResource(
                            R.drawable.setting_bg);
                    menuBtn.setTextColor(getResources().getColor(R.color.mypurple));
                }
                menuTxt.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[0]));
            } else {
                menuTxt.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[1]));
                menuBtn.setTextColor(Color.parseColor(mContext.getResources()
                        .getStringArray(R.array.text_colorchange)[1]));
                //findViewById(R.id.button_layout).setBackgroundResource(
                        //R.drawable.button_transparent);
            }
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onFocusChange flag = " + ishasFocus);
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
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    // On the left, the left shift data
                    return moveToPreviousOption();

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    // According to the right, the right data
                    return moveToNextOption();

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
