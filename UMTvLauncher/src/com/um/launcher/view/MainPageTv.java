
package com.um.launcher.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.um.launcher.MainActivity;
import com.um.launcher.R;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.interfaces.SourceManagerInterface;
import com.um.launcher.model.SourceObj;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;

/**
 * The first big view
 */
@SuppressLint("ResourceAsColor")
public class MainPageTv extends RelativeLayout implements ShowAbleInterface,
        View.OnClickListener, OnFocusChangeListener {

    private static final String TAG = "MainPageTv";
    public final static int PAGENUM = 0;
    private MainActivity mContext;
    // The actual background
    public View[] mImgView;
    // when the Source is not used,set the background ashing
    public View[] mInnerView;
    // show the current Source
    private TextView mCurSourceText;

    public MainPageTv(Context context) {
        super(context);
    }

    public MainPageTv(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.main_page_tv, this);
        initView(parent);
    }

    private void initView(View parent) {
         SurfaceView surface = (SurfaceView)parent.findViewById(R.id.minvideo);
         SurfaceHolder sh = surface.getHolder();
         sh.setType(SurfaceHolder.SURFACE_TYPE_HISI_TRANSPARENT);
         Util.setSurfaceView(surface);
         
        mImgView = new View[] {
                parent.findViewById(R.id.tv_item_window),
                parent.findViewById(R.id.tv_item_dvbc),
                parent.findViewById(R.id.tv_item_av1),
                parent.findViewById(R.id.tv_item_dtv),
                parent.findViewById(R.id.tv_item_av2),
                parent.findViewById(R.id.tv_item_atv),
                parent.findViewById(R.id.tv_item_hdmi1),
                parent.findViewById(R.id.tv_item_hdmi2),
                parent.findViewById(R.id.tv_item_hdmi3),
                parent.findViewById(R.id.tv_item_vga),
                parent.findViewById(R.id.tv_item_ypbpr)
        };
        mInnerView = new View[] {
                parent.findViewById(R.id.view_window_pic),
                parent.findViewById(R.id.view_dvbc),
                parent.findViewById(R.id.view_av1),
                parent.findViewById(R.id.view_dtv),
                parent.findViewById(R.id.view_av2),
                parent.findViewById(R.id.view_atv),
                parent.findViewById(R.id.view_hdmi1),
                parent.findViewById(R.id.view_hdmi2),
                parent.findViewById(R.id.view_hdmi3),
                parent.findViewById(R.id.view_vga),
                parent.findViewById(R.id.view_ypbpr)
        };

        for (int i = 0; i < mImgView.length; i++) {
            mImgView[i].setOnClickListener(this);
            mImgView[i].getBackground().setAlpha(0);
            mImgView[i].setOnFocusChangeListener(this);
        }
        mCurSourceText = (TextView) parent.findViewById(R.id.tv_window_txt);
        setSourceBackground();
    }

    /**
     * set Source Background enabled according to the source isavailable
     */
    private void setSourceBackground() {
        List<SourceObj> sourcelist = getSouceList();
        SourceObj model;
        for (int i = 0; i < sourcelist.size(); i++) {
            model = sourcelist.get(i);
            if (!model.isAvail()) {
                if ((model.getSourceId() == Constant.NUMBER_0)
                        || model.getSourceId() == Constant.NUMBER_2
                        || model.getSourceId() == Constant.NUMBER_3
                        || model.getSourceId() == Constant.NUMBER_4) {
                    mInnerView[model.getSourceId()]
                            .setBackgroundResource(R.drawable.tv_grey_small);
                } else if ((model.getSourceId() == Constant.NUMBER_5)
                        || model.getSourceId() == Constant.NUMBER_6
                        || model.getSourceId() == Constant.NUMBER_7
                        || model.getSourceId() == Constant.NUMBER_8
                        || model.getSourceId() == Constant.NUMBER_9) {
                    mInnerView[model.getSourceId()]
                            .setBackgroundResource(R.drawable.tv_grey_middle);
                } else {
                    mInnerView[model.getSourceId()]
                            .setBackgroundResource(R.drawable.tv_grey_high);
                }

                mImgView[model.getSourceId()].setFocusable(false);
                mImgView[model.getSourceId()].setEnabled(false);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mImgView[Constant.NUMBER_0].hasFocus()
                    || mImgView[Constant.NUMBER_5].hasFocus()) {
                mContext.snapToPreScreen();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if ((mImgView[Constant.NUMBER_10].hasFocus() || mImgView[Constant.NUMBER_9]
                    .hasFocus())) {
                mContext.snapToNextScreen();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mImgView[Constant.NUMBER_0].hasFocus()
                    || mImgView[Constant.NUMBER_1].hasFocus()
                    || mImgView[Constant.NUMBER_2].hasFocus()
                    || mImgView[Constant.NUMBER_10].hasFocus()) {
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mImgView[Constant.NUMBER_5].hasFocus()
                    || mImgView[Constant.NUMBER_6].hasFocus()
                    || mImgView[Constant.NUMBER_7].hasFocus()
                    || mImgView[Constant.NUMBER_8].hasFocus()
                    || mImgView[Constant.NUMBER_9].hasFocus()) {
                RelativeLayout[] tagList = mContext.getTagView().getTagList();
                tagList[mContext.getFocusedPage()].requestFocus();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void isShow() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "Now isShow--->" + PAGENUM);
            Log.i(TAG, "MainActivity.isSnapLeftOrRight--->"
                    + MainActivity.isSnapLeftOrRight);
        }
        if (MainActivity.isSnapLeftOrRight) {
            if (!mContext.getTagView().hasFocus()) {
                if (mContext.isSnapLeft()) {
                    if (mContext.isFocusUp()) {
                        mImgView[Constant.NUMBER_10].requestFocus();
                    } else {
                        mImgView[Constant.NUMBER_9].requestFocus();
                    }
                } else {
                    if (mContext.isFocusUp()) {
                        mImgView[Constant.NUMBER_0].requestFocus();
                    } else {
                        mImgView[Constant.NUMBER_5].requestFocus();
                    }
                }
            }
        } else if (MainActivity.isNeedResetTvFocus) {
            requestFocusBySourceID();
        }
        // setTextValue();
        MainActivity.isSnapLeftOrRight = false;
        MainActivity.isNeedResetTvFocus = false;
        mContext.getTagView().setViewOnSelectChange(PAGENUM);
    }

    /**
     * On the basis of the reference, or select the default
     */
    private void requestFocusBySourceID() {
        int curId = SourceManagerInterface.getCurSourceId();
        switch (curId) {
            case EnumSourceIndex.SOURCE_DVBC:
                mImgView[Constant.NUMBER_1].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_DTMB:// DTV
                mImgView[Constant.NUMBER_3].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_ATV:
                mImgView[Constant.NUMBER_5].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_CVBS1:// AV1
                mImgView[Constant.NUMBER_2].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_CVBS2:// AV2
                mImgView[Constant.NUMBER_4].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_YPBPR1:// YPBPR
                mImgView[Constant.NUMBER_10].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_HDMI1:
                mImgView[Constant.NUMBER_6].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_HDMI2:
                mImgView[Constant.NUMBER_7].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_HDMI3:
                mImgView[Constant.NUMBER_8].requestFocus();
                break;
            case EnumSourceIndex.SOURCE_VGA:
                mImgView[Constant.NUMBER_9].requestFocus();
                break;
            default:
                mImgView[Constant.NUMBER_5].requestFocus();
                break;
        }
    }

    /**
     * set current source name
     */
    public void setTextValue(int curId) {
        // TODO Auto-generated method stub
        if (null == mCurSourceText) {
            return;
        }
        // int curId = SourceManagerInterface.getCurSourceId();
        switch (curId) {
            case EnumSourceIndex.SOURCE_DVBC:
                mCurSourceText.setText(R.string.DVB);
                break;
            case EnumSourceIndex.SOURCE_DTMB:// DTV
                mCurSourceText.setText(R.string.DTV);
                break;
            case EnumSourceIndex.SOURCE_ATV:
                mCurSourceText.setText(R.string.ATV);
                break;
            case EnumSourceIndex.SOURCE_CVBS1:// AV1
                mCurSourceText.setText(R.string.AV1);
                break;
            case EnumSourceIndex.SOURCE_CVBS2:// AV2
                mCurSourceText.setText(R.string.AV2);
                break;
            case EnumSourceIndex.SOURCE_YPBPR1:// YPBPR
                mCurSourceText.setText(R.string.YPbPr);
                break;
            case EnumSourceIndex.SOURCE_HDMI1:
                mCurSourceText.setText(R.string.HDMI1);
                break;
            case EnumSourceIndex.SOURCE_HDMI2:
                mCurSourceText.setText(R.string.HDMI2);
                break;
            case EnumSourceIndex.SOURCE_HDMI3:
                mCurSourceText.setText(R.string.HDMI3);
                break;
            case EnumSourceIndex.SOURCE_VGA:
                mCurSourceText.setText(R.string.VGA);
                break;
            default:
                mCurSourceText.setText(R.string.ATV);
                break;
        }
    }

    public int getId() {
        return PAGENUM;
    }

    @Override
    public View[] getImgViews() {
        return mImgView;
    }

    @Override
    public void onClick(View v) {
        MainActivity.isSnapLeftOrRight = false;
        MainActivity.isNeedResetTvFocus = true;
        int curId = SourceManagerInterface.getCurSourceId();
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getCurSourceId curId = " + curId);
        }
        Intent intent = new Intent();
        int destid = 0;
        intent.setAction(Constant.INTENT_ATV);
        switch (v.getId()) {
            case R.id.tv_item_window: {
                switch (curId) {
                    case EnumSourceIndex.SOURCE_DVBC:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DVBC);
                        intent.setAction(Constant.INTENT_DTV);
                        break;
                    case EnumSourceIndex.SOURCE_DTMB:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DTMB);
                        intent.setAction(Constant.INTENT_DTV);
                        break;
                    case EnumSourceIndex.SOURCE_ATV:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_ATV);
                        break;
                    case EnumSourceIndex.SOURCE_CVBS1:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS1);
                        break;
                    case EnumSourceIndex.SOURCE_CVBS2:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS2);
                        break;
                    case EnumSourceIndex.SOURCE_YPBPR1:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_YPBPR1);
                        break;
                    case EnumSourceIndex.SOURCE_HDMI1:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI1);
                        break;
                    case EnumSourceIndex.SOURCE_HDMI2:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI2);
                        break;
                    case EnumSourceIndex.SOURCE_HDMI3:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI3);
                        break;
                    case EnumSourceIndex.SOURCE_VGA:
                        intent.putExtra("SourceName", EnumSourceIndex.SOURCE_VGA);
                        break;
                    default:
                        break;
                }
                if ((curId == EnumSourceIndex.SOURCE_ATV)
                        || (curId >= EnumSourceIndex.SOURCE_CVBS1 && curId <= EnumSourceIndex.SOURCE_HDMI4)) {

                    Log.d(TAG, "Scaler ATV Windows to full");
                    RectInfo rect = new RectInfo();
                    rect.setX(0);
                    rect.setY(0);
                    rect.setW(1920);
                    rect.setH(1080);
                    SourceManagerInterface.setWindowRect(rect, 0);
                }else{
                    Log.d(TAG,"Scaler DTV Window to full");
                    Util.notifyDTVStartPlay(mContext, true);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
                return;
            case R.id.tv_item_dvbc:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DVBC);
                intent.setAction(Constant.INTENT_DTV);
                destid = EnumSourceIndex.SOURCE_DVBC;
                break;
            case R.id.tv_item_dtv:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_DTMB);
                intent.setAction(Constant.INTENT_DTV);
                destid = EnumSourceIndex.SOURCE_DTMB;
                break;
            case R.id.tv_item_atv:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_ATV);
                destid = EnumSourceIndex.SOURCE_ATV;
                break;
            case R.id.tv_item_av1:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS1);
                destid = EnumSourceIndex.SOURCE_CVBS1;
                break;
            case R.id.tv_item_av2:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_CVBS2);
                destid = EnumSourceIndex.SOURCE_CVBS2;
                break;
            case R.id.tv_item_ypbpr:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_YPBPR1);
                destid = EnumSourceIndex.SOURCE_YPBPR1;
                break;
            case R.id.tv_item_hdmi1:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI1);
                destid = EnumSourceIndex.SOURCE_HDMI1;
                break;
            case R.id.tv_item_hdmi2:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI2);
                destid = EnumSourceIndex.SOURCE_HDMI2;
                break;
            case R.id.tv_item_hdmi3:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_HDMI3);
                destid = EnumSourceIndex.SOURCE_HDMI3;
                break;
            case R.id.tv_item_vga:
                intent.putExtra("SourceName", EnumSourceIndex.SOURCE_VGA);
                destid = EnumSourceIndex.SOURCE_VGA;
                break;
            default:
                break;
        }
        if (Constant.LOG_TAG) {
            Log.d(TAG, "selectSource start,set destid = " + destid);
        }
        RectInfo rect = new RectInfo();
        if ((destid == EnumSourceIndex.SOURCE_ATV)
                || (destid >= EnumSourceIndex.SOURCE_CVBS1 && destid <= EnumSourceIndex.SOURCE_HDMI4)) {

            if (curId == EnumSourceIndex.SOURCE_DVBC
                 || curId == EnumSourceIndex.SOURCE_DTMB) {
                Util.notifyDTVStopPlay(mContext);
            }
            Log.d(TAG, "Scaler ATV Windows to full");
            rect.setX(0);
            rect.setY(0);
            rect.setW(1920);
            rect.setH(1080);
            SourceManagerInterface.setWindowRect(rect, 0);
        }
        else if (destid == EnumSourceIndex.SOURCE_DVBC || destid == EnumSourceIndex.SOURCE_DTMB) {
            rect.setX(0);
            rect.setY(0);
            rect.setW(1920);
            rect.setH(1080);
            SourceManagerInterface.setWindowRect(rect, 0);
            Log.d(TAG,"--12222--mainpage tv current ID ="+curId);

            SourceManagerInterface.deselectSource(curId, true);
            SourceManagerInterface.selectSource(destid, 0);
            Util.notifyDTVStartPlay(mContext, true);
            if (Constant.LOG_TAG) {
                Log.d(TAG, "selectSource done,now getCurSourceId = "
                        + SourceManagerInterface.getCurSourceId());
            }
        }
        Util.saveCurSourceToPrefer(mContext, destid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "onFocusChange---");
        }
        if (hasFocus) {
            v.bringToFront();
            v.getBackground().setAlpha(255);
            // Set the flag and focus related position
            switch (v.getId()) {
                case R.id.tv_item_window:
                    mContext.setFocusedView(Constant.NUMBER_0);
                    mContext.setFocusUp(true);
                    break;
                case R.id.tv_item_dvbc:
                    mContext.setFocusedView(Constant.NUMBER_1);
                    v.animate().scaleX(0.935f).scaleY(0.935f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusUp(true);
                    break;
                case R.id.tv_item_av1:
                    v.animate().scaleX(0.935f).scaleY(0.935f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_2);
                    mContext.setFocusUp(true);
                    break;
                case R.id.tv_item_dtv:
                    v.animate().scaleX(0.935f).scaleY(0.935f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_3);
                    mContext.setFocusUp(true);
                    break;
                case R.id.tv_item_av2:
                    v.animate().scaleX(0.935f).scaleY(0.935f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_4);
                    mContext.setFocusUp(true);
                    break;
                case R.id.tv_item_atv:
                    v.animate().scaleX(0.908f).scaleY(0.924f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_5);
                    mContext.setFocusUp(false);
                    break;
                case R.id.tv_item_hdmi1:
                    v.animate().scaleX(0.908f).scaleY(0.924f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_6);
                    mContext.setFocusUp(false);
                    break;
                case R.id.tv_item_hdmi2:
                    v.animate().scaleX(0.908f).scaleY(0.924f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_7);
                    mContext.setFocusUp(false);
                    break;
                case R.id.tv_item_hdmi3:
                    v.animate().scaleX(0.908f).scaleY(0.924f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_8);
                    mContext.setFocusUp(false);
                    break;
                case R.id.tv_item_vga:
                    v.animate().scaleX(0.908f).scaleY(0.924f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_9);
                    mContext.setFocusUp(false);
                    break;
                case R.id.tv_item_ypbpr:
                    v.animate().scaleX(0.957f).scaleY(0.957f)
                            .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
                    mContext.setFocusedView(Constant.NUMBER_10);
                    mContext.setFocusUp(true);
                    break;
                default:
                    break;
            }
        } else {
            v.getBackground().setAlpha(0);
            if (v.getId() == R.id.tv_item_window) {
            } else if (v.getId() == R.id.tv_item_dvbc
                    || v.getId() == R.id.tv_item_av1
                    || v.getId() == R.id.tv_item_dtv
                    || v.getId() == R.id.tv_item_av2) {
                v.animate().scaleX(0.85f).scaleY(0.85f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            } else if (v.getId() == R.id.tv_item_ypbpr) {
                v.animate().scaleX(0.87f).scaleY(0.87f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            } else {
                v.animate().scaleX(0.825f).scaleY(0.84f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            }
        }
    }

    public List<Integer> getAvailbleIndex() {
        List<Integer> list = new ArrayList<Integer>();
        return list;
    }

    public static List<SourceObj> getSouceList() {
        List<SourceObj> list = new ArrayList<SourceObj>();
        List<Integer> sourcelist = getTestSourceList();
        List<Integer> availablelist = getAvailableList();

        for (int i = 0; i < sourcelist.size(); i++) {
            SourceObj mSourceObj = new SourceObj();

            mSourceObj.setSourceId(i + 1);

            int id = sourcelist.get(i);
            for (int j = 0; j < availablelist.size(); j++) {
                int aviid = availablelist.get(j);
                if (id == aviid) {
                    mSourceObj.setAvail(true);
                    break;
                } else {
                    mSourceObj.setAvail(false);
                }
            }
            list.add(mSourceObj);
        }
        // Put slist into list
        return list;
    }

    /**
     * get all source
     *
     * @return all source list
     */
    public static List<Integer> getTestSourceList() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(Constant.NUMBER_1);
        list.add(Constant.NUMBER_2);
        list.add(Constant.NUMBER_3);
        list.add(Constant.NUMBER_4);
        list.add(Constant.NUMBER_5);
        list.add(Constant.NUMBER_6);
        list.add(Constant.NUMBER_7);
        list.add(Constant.NUMBER_8);
        list.add(Constant.NUMBER_9);
        list.add(Constant.NUMBER_10);
        return list;
    }

    /**
     * get current available source list test data
     *
     * @return list
     */
    public static List<Integer> getAvailableList() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(Constant.NUMBER_1);
        list.add(Constant.NUMBER_2);
        list.add(Constant.NUMBER_3);
        list.add(Constant.NUMBER_4);
        list.add(Constant.NUMBER_5);
        list.add(Constant.NUMBER_6);
        list.add(Constant.NUMBER_7);
        list.add(Constant.NUMBER_8);
        list.add(Constant.NUMBER_9);
        list.add(Constant.NUMBER_10);
        return list;
    }
}
