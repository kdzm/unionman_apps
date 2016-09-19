package com.um.launcher.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.um.launcher.R;
import com.um.launcher.util.LogUtils;
import com.um.launcher.weather.StringUtils;

/**
 * Created by hjian on 2015/3/19.
 */
public class AppView extends RelativeLayout implements FocusedRelativeLayout.ScalePostionInterface, FocusedRelativeLayout.ViewTypeInterface {
    public static int ICON_TYPE_ICON = 0;
    public static int ICON_TYPE_PIC = 1;

    private RoundedImageView appBgImageView;
    private RoundedImageView appIconImageView;
    private TextView mainTitleView;
    private View titleBgView;
    private int mReflectHight = 0;
    public int reflectionGap = 2;
    private int appTitleBg = -1;
    private int appIcon = -1;
    private int appBg = -1;
    private int iconType = ICON_TYPE_ICON;
    private String mainTitle = "";
    private int mainTitleColor = 0x80F0F0F0;
    private int mainTitleSize = 20;

    public AppView(Context context) {
        super(context);
        init(context, null);
    }

    public AppView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AppView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lf.inflate(R.layout.app_item, null);
        appBgImageView = (RoundedImageView) view.findViewById(R.id.imageView1);
        mainTitleView = (TextView) view.findViewById(R.id.textView1);
        titleBgView = view.findViewById(R.id.ly_title);
        if (attrs != null) {
            parseAttributes(context, attrs);
        }

        if (iconType == ICON_TYPE_PIC) {
            appIconImageView = (RoundedImageView) view.findViewById(R.id.imageView2);
            view.findViewById(R.id.imageView3).setVisibility(GONE);
        } else {
            appIconImageView = (RoundedImageView) view.findViewById(R.id.imageView3);
            view.findViewById(R.id.imageView2).setVisibility(GONE);
        }

        setAttributes();
        addView(view);
    }

    public int getIconViewWidth() {
    	if (appIconImageView != null) {
    		return appIconImageView.getWidth();
    	}
    	return 0; 
    }
    
    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppView);

        iconType = a.getInt(R.styleable.AppView_av_icon_type, iconType);
        appBg = a.getResourceId(R.styleable.AppView_av_icon_bg, appBg);
        appIcon = a.getResourceId(R.styleable.AppView_av_icon, appIcon);
        appTitleBg = a.getResourceId(R.styleable.AppView_av_title_bg, appTitleBg);
        if (a.hasValue(R.styleable.AppView_av_title)) {
            mainTitle = a.getString(R.styleable.AppView_av_title);
        }

        a.recycle();
    }

    private void setAttributes() {
        mainTitleView.setTextColor(mainTitleColor);
        mainTitleView.setTextSize(mainTitleSize);
        if (appBg != -1) {
            setAppBg(appBg);
        }
        if (appIcon != -1) {
            setAppIcon(appIcon);
        }
        if (appTitleBg != -1) {
            setTitleBg(appTitleBg);
        }
        if (!StringUtils.isBlank(mainTitle)) {
            setTitle(mainTitle);
        }
    }

    @Override
    public Rect getScaledRect(float scaleXValue, float scaleYValue, boolean isScaled) {
        Rect firstRect = new Rect();

        this.getGlobalVisibleRect(firstRect);

        LogUtils.d("scaleXValue=" + scaleXValue
                + ",bottom=" + firstRect.bottom
                + ",top=" + firstRect.top
                + ",left" + firstRect.left);

        return firstRect;
    }

    @Override
    public boolean getIfScale() {
        return true;
    }

    public void setAppIconDrawable(Drawable drawable) {
        appIconImageView.setImageDrawable(drawable);
    }

    public void setAppIconBitmap(Bitmap bm) {
        appIconImageView.setImageBitmap(bm);
    }

    public void setAppIconResource(int resId) {
        appIconImageView.setImageResource(resId);
    }

    public String getTitle() {
        return mainTitle;
    }

    public void setTitle(String title) {
        this.mainTitle = title;
        mainTitleView.setText(title);
    }

    public void setAppBg(int resId) {
        appBgImageView.setImageResource(resId);
    }

    public void setAppBg(Drawable drawable) {
        appBgImageView.setImageDrawable(drawable);
    }

    public void setAppBg(Bitmap bitmap) {
        appBgImageView.setImageBitmap(bitmap);
    }

    public void setAppIcon(int resId) {
        appIconImageView.setImageResource(resId);
    }

    public void setAppIcon(Drawable drawable) {
        appIconImageView.setImageDrawable(drawable);
    }

    public void setAppIcon(Bitmap bitmap) {
        appIconImageView.setImageBitmap(bitmap);
    }

    public void setAppIcon(String url) {
        appIconImageView.setImageUrl(url);
    }

    public void setTitleBg(int resId) {
        titleBgView.setBackgroundResource(resId);
    }

    public void setTitleBg(Drawable drawable) {
        titleBgView.setBackgroundDrawable(drawable);
    }

    public void setTitleBg(Bitmap bitmap) {
        titleBgView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public int getViewType() {
        return FocusedRelativeLayout.ViewTypeInterface.TYPE_APP;
    }
}
