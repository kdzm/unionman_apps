
package com.um.launcher.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.um.launcher.MainActivity;
import com.um.launcher.R;
import com.um.launcher.util.Constant;

/**
 * The bottom tag is used to quickly Switching the screen and show the current
 * page outstanding
 *
 * @author qian_shengwei
 */
public class TagView extends LinearLayout implements View.OnKeyListener,
        View.OnFocusChangeListener {

    private static final String TAG = "TagView";
    private MainActivity mContext;
    // focused page number
    private int mFocusedPage;
    // Click the record button, don't need to deal with button down as -1
    private int mKeyCode = -1;

    // layout of First
    private RelativeLayout mTagFirst;
    // layout of TV
    private RelativeLayout mTagTv;
    // layout of Movie
    private RelativeLayout mTagMovie;
    // layout of Education
    private RelativeLayout mTagEducation;
    // layout of App
    private RelativeLayout mTagApp;
    // layout of Game
    private RelativeLayout mTagGame;
    // layout of Setting
    private RelativeLayout mTagSetting;
    // list of all tag layout
    private RelativeLayout[] mTagList;

    // text of First
    private TextView firstText;
    // text of TV
    private TextView tvText;
    // text of movie
    private TextView movieText;
    // text of education
    private TextView educationText;
    // text of app
    private TextView appText;
    // text of game
    private TextView gameText;
    // text of setting
    private TextView settingText;
    // list of all tag text
    private TextView[] mTextList;

    // image of First
    private ImageView firstImg;
    // image of TV
    private ImageView tvImg;
    // image of movie
    private ImageView movieImg;
    // image of education
    private ImageView educationImg;
    // image of app
    private ImageView appImg;
    // image of game
    private ImageView gameImg;
    // image of setting
    private ImageView settingImg;
    // list of all tag image
    private ImageView[] mImgList;

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.tag_view, this);
        initView(parent);
        mFocusedPage = mContext.getFocusedPage();
        setViewOnSelectChange(mFocusedPage);
    }

    /**
     * The initialization of view
     *
     * @param parent
     */
    private void initView(View parent) {
    	mTagFirst = (RelativeLayout) parent.findViewById(R.id.tag_first);
        //mTagTv = (RelativeLayout) parent.findViewById(R.id.tag_tv);
        //mTagMovie = (RelativeLayout) parent.findViewById(R.id.tag_movie);
        mTagEducation = (RelativeLayout) parent.findViewById(R.id.tag_education);
        mTagApp = (RelativeLayout) parent.findViewById(R.id.tag_app);
        mTagGame = (RelativeLayout) parent.findViewById(R.id.tag_game);
        //mTagSetting = (RelativeLayout) parent.findViewById(R.id.tag_setting);
        mTagFirst.setOnKeyListener(this);
        //mTagTv.setOnKeyListener(this);
        //mTagMovie.setOnKeyListener(this);
        mTagEducation.setOnKeyListener(this);
        mTagApp.setOnKeyListener(this);
        mTagGame.setOnKeyListener(this);
        //mTagSetting.setOnKeyListener(this);
        mTagFirst.setOnFocusChangeListener(this);
        //mTagTv.setOnFocusChangeListener(this);
        //mTagMovie.setOnFocusChangeListener(this);
        mTagEducation.setOnFocusChangeListener(this);
        mTagApp.setOnFocusChangeListener(this);
        mTagGame.setOnFocusChangeListener(this);
        //mTagSetting.setOnFocusChangeListener(this);
        mTagList = new RelativeLayout[] {
        		mTagFirst, mTagEducation, mTagGame, mTagApp
        };

        firstText = (TextView) mTagFirst.findViewById(R.id.tag_first_txt);
        //tvText = (TextView) mTagTv.findViewById(R.id.tag_tv_txt);
        //movieText = (TextView) mTagMovie.findViewById(R.id.tag_movie_txt);
        educationText = (TextView) mTagEducation.findViewById(R.id.tag_education_txt);
        appText = (TextView) mTagApp.findViewById(R.id.tag_app_txt);
        gameText = (TextView) mTagGame.findViewById(R.id.tag_game_txt);
        //settingText = (TextView) mTagSetting.findViewById(R.id.tag_setting_txt);
        mTextList = new TextView[] {
        		firstText, educationText, gameText, appText
        };

        firstImg = (ImageView) mTagFirst.findViewById(R.id.tag_first_img);
        //tvImg = (ImageView) mTagTv.findViewById(R.id.tag_tv_img);
        //movieImg = (ImageView) mTagMovie.findViewById(R.id.tag_movie_img);
        educationImg = (ImageView) mTagEducation.findViewById(R.id.tag_education_img);
        appImg = (ImageView) mTagApp.findViewById(R.id.tag_app_img);
        gameImg = (ImageView) mTagGame.findViewById(R.id.tag_game_img);
        //settingImg = (ImageView) mTagSetting.findViewById(R.id.tag_setting_img);
        mImgList = new ImageView[] {
        		firstImg, educationImg, gameImg, appImg
        };
        
    }

    /**
     * set view when select change
     *
     * @param focusedPage
     */
    public void setViewOnSelectChange(int focusedPage) {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "setViewOnSelectChange" + focusedPage);
        }
        mContext.setFocusePage(focusedPage);
        for (int i = 0; i < mTagList.length; i++) {
            if (i == focusedPage) {
                mTextList[i].setTextSize(34F);
                mTextList[i].setTextColor(Color.WHITE);
                if (this.hasFocus()) {
                    mImgList[i].setVisibility(View.VISIBLE);
                } else {
                    mImgList[i].setVisibility(View.INVISIBLE);
                }
            } else {
                mTextList[i].setTextSize(33F);
                mTextList[i].setTextColor(mContext.getResources().getColor(
                        R.color.tagunfocus));
                mImgList[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * get list of all tag layout
     *
     * @return
     */
    public RelativeLayout[] getTagList() {
        return mTagList;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mKeyCode = KeyEvent.KEYCODE_DPAD_LEFT;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mKeyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                mKeyCode = -1;
                View[] views = mContext.getRoot().getCurScreen().getImgViews();
                mFocusedPage = mContext.getFocusedPage();
                switch (mFocusedPage) {
                    case MainPageFirst.PAGENUM:
                        if (views.length == 9) {
                            if (Constant.LOG_TAG) {
                                Log.i(TAG, "views requestFocus");
                            }
                            views[6].requestFocus();
                        }
                        break;
                    case MainPageEducation.PAGENUM:
                        if (views.length == 7) {
                            views[4].requestFocus();
                        }
                        break;
                    case MainPageApp.PAGENUM:
                        if (views.length == 8) {
                            views[5].requestFocus();
                        }
                        break;
                    case MainPageGame.PAGENUM:
                        if (views.length == 7) {
                            views[1].requestFocus();
                        }
                        break;
                    case MainPageSetting.PAGENUM:
                        if (views.length == 7) {
                            views[6].requestFocus();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                // Focus disable keys in tagView, in order to prevent the loss
                // of
                // focus
                mKeyCode = -1;
                return true;
            default:
                mKeyCode = -1;
                break;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (mKeyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    mContext.snapToPreScreen();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    mContext.snapToNextScreen();
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return;
                default:
                    break;
            }
            mKeyCode = -1;
        }
        mFocusedPage = mContext.getFocusedPage();
        setViewOnSelectChange(mFocusedPage);
        if (!hasFocus) {
            mImgList[mFocusedPage].setVisibility(View.INVISIBLE);
        }
    }

}
