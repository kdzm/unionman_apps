
package com.um.launcher.view;

import java.io.File;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.um.launcher.MainActivity;
import com.um.launcher.R;
import com.um.launcher.interfaces.ShowAbleInterface;
import com.um.launcher.util.Constant;

/**
 * The first big view
 */
public class MainPageMovie extends RelativeLayout implements ShowAbleInterface,
        View.OnClickListener, OnFocusChangeListener {

    private static final String TAG = "MainPageMovie";
    public final static int PAGENUM = 1;
    private MainActivity mContext;
    public View[] imgView;
    // The first interface package name
    private String[] firstPkg = new String[] {
            "",//
            "com.skyworth.onlinemovie.letv.csapp",// letv
            "com.sohutv.tv",// sohutv
            "com.tencent.qqlivehd",// QQLive
            "com.qiyi.video",// qiyi
            "com.youku.tv",// youku
            "com.togic.livevideo"// togic
    };

    // The main Activity in the first interface
    private String[] firstCls = new String[] {
            "",
            "com.letv.tv.activity.WelcomeActivity",
            "com.sohutv.tv.HomePageActivity",
            "com.tencent.qqlivehd.MainActivity",
            "com.qiyi.video.WelcomeActivity", "com.youku.tv.WelcomeActivity",
            "com.togic.launcher.MainActivity"
    };

    public MainPageMovie(Context context) {
        super(context);
    }

    public MainPageMovie(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.main_page_movie, this);
        initView(parent);
    }

    /**
     * Initialize current views
     */
    private void initView(View parent) {
        imgView = new View[] {
                parent.findViewById(R.id.movie_item_netvideo),
                parent.findViewById(R.id.movie_item_letv),
                parent.findViewById(R.id.movie_item_sohu),
                parent.findViewById(R.id.movie_item_qqlive),
                parent.findViewById(R.id.movie_item_iqy),
                parent.findViewById(R.id.movie_item_youku),
                parent.findViewById(R.id.movie_item_taijie)
        };

        for (int i = 0; i < imgView.length; i++) {
            imgView[i].setOnClickListener(this);
            imgView[i].getBackground().setAlpha(0);
            imgView[i].setOnFocusChangeListener(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (imgView[Constant.NUMBER_0].hasFocus()
                    || imgView[Constant.NUMBER_2].hasFocus()) {
                mContext.snapToPreScreen();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if ((imgView[Constant.NUMBER_1].hasFocus() || imgView[Constant.NUMBER_6]
                    .hasFocus())) {
                mContext.snapToNextScreen();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (imgView[Constant.NUMBER_2].hasFocus()
                    || imgView[Constant.NUMBER_3].hasFocus()
                    || imgView[Constant.NUMBER_4].hasFocus()) {
                imgView[Constant.NUMBER_0].requestFocus();
                return true;
            } else if (imgView[Constant.NUMBER_5].hasFocus()
                    || imgView[Constant.NUMBER_6].hasFocus()) {
                imgView[Constant.NUMBER_1].requestFocus();
                return true;
            } else if (imgView[Constant.NUMBER_0].hasFocus()
                    || imgView[Constant.NUMBER_1].hasFocus()) {
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (imgView[Constant.NUMBER_0].hasFocus()) {
                imgView[Constant.NUMBER_2].requestFocus();
                return true;
            } else if (imgView[Constant.NUMBER_1].hasFocus()) {
                imgView[Constant.NUMBER_6].requestFocus();
                return true;
            } else {
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
        }
        if (!mContext.getTagView().hasFocus()) {
            if (mContext.isSnapLeft()) {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_1].requestFocus();
                } else {
                    imgView[Constant.NUMBER_6].requestFocus();
                }
            } else {
                if (mContext.isFocusUp()) {
                    imgView[Constant.NUMBER_0].requestFocus();
                } else {
                    imgView[Constant.NUMBER_2].requestFocus();
                }
            }
        }
        mContext.getTagView().setViewOnSelectChange(PAGENUM);
    }

    public int getId() {
        return PAGENUM;
    }

    @Override
    public View[] getImgViews() {
        return imgView;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < imgView.length; i++) {
            if (i == 0 && imgView[i] == v) {
                Intent intent = new Intent();
                intent.setClassName("com.hisilicon.android.videoplayer",
                        "com.hisilicon.android.videoplayer.activity.MediaFileListService");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(
                        Uri.fromFile(new File(Constant.TEST_VEDIO_PATH)),
                        "video/*");
                intent.putExtra("sortCount", 0);
                // mContext.startService(intent);
                break;
            } else if (imgView[i] == v) {
                try {
                    String pkg = firstPkg[i].trim();
                    String cls = firstCls[i].trim();
                    ComponentName componentName = new ComponentName(pkg, cls);
                    Intent mIntent = new Intent();
                    mIntent.setComponent(componentName);
                    mContext.startActivity(mIntent);
                } catch (Exception e) {
                    if (i != 0) {
                        Toast.makeText(mContext, "Failed to start" + i,
                                Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "onFocusChange---");
        }
        if (hasFocus) {
            v.bringToFront();
            v.getBackground().setAlpha(255);
            if (v.getId() == R.id.movie_item_netvideo) {
                v.animate().scaleX(1.1f).scaleY(1.1f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            } else {
                v.animate().scaleX(0.924f).scaleY(0.924f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            }
            // Set the flag and focus related position
            switch (v.getId()) {
                case R.id.movie_item_netvideo:
                    mContext.setFocusedView(Constant.NUMBER_0);
                    mContext.setFocusUp(true);
                    break;
                case R.id.movie_item_letv:
                    mContext.setFocusedView(Constant.NUMBER_1);
                    mContext.setFocusUp(true);
                    break;
                case R.id.movie_item_sohu:
                    mContext.setFocusedView(Constant.NUMBER_2);
                    mContext.setFocusUp(false);
                    break;
                case R.id.movie_item_qqlive:
                    mContext.setFocusedView(Constant.NUMBER_3);
                    mContext.setFocusUp(false);
                    break;
                case R.id.movie_item_iqy:
                    mContext.setFocusedView(Constant.NUMBER_4);
                    mContext.setFocusUp(false);
                    break;
                case R.id.movie_item_youku:
                    mContext.setFocusedView(Constant.NUMBER_5);
                    mContext.setFocusUp(false);
                    break;
                case R.id.movie_item_taijie:
                    mContext.setFocusedView(Constant.NUMBER_6);
                    mContext.setFocusUp(false);
                    break;
                default:
                    break;
            }
        } else {
            if (v.getId() == R.id.movie_item_netvideo) {
                v.animate().scaleX(1f).scaleY(1f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            } else if (v.getId() == R.id.movie_item_letv) {
                v.animate().scaleX(0.8335f).scaleY(0.8386f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            } else {
                v.animate().scaleX(0.83f).scaleY(0.83f)
                        .setDuration(Constant.SCARE_ANIMATION_DURATION).start();
            }
            v.getBackground().setAlpha(0);
        }
    }
}
