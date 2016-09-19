package com.um.launcher.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.um.launcher.R;
import com.um.launcher.data.PosterInfo;
import com.um.launcher.util.LogUtils;
import com.um.launcher.widget.salvage.RecyclingPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjian on 2015/3/19.
 */
public class PosterView extends RelativeLayout
        implements FocusedRelativeLayout.ScalePostionInterface,
        FocusedRelativeLayout.ViewTypeInterface{
    private RoundedImageView posterBgImageView;
    private RoundedImageView posterImageView;
    private AutoScrollViewPager viewPager;
    private View titleBackgroundView;
    private TextView mainTitleView;
    private TextView subTitleView;
    private View mainView;
    private int posterBg = -1;
    private int poster = -1;
    private int titleBackground = -1;
    private String mainTitle = "";
    private String subTitle = "";
    private int mainTitleColor = 0xFFF0F0F0;
    private int subTitleColor = 0x80F0F0F0;
    private int mainTitleSize = 32;
    private int subTitleSize = 20;
    private boolean isTop = false;
    private int mViewType = FocusedRelativeLayout.ViewTypeInterface.TYPE_POSTER;
    private List<PosterInfo> posterInfos = null;

    private Animation scaleBigAnim;
    private Animation scaleSmallAnim;
    
    public PosterView(Context context) {
        super(context);
        init(context, null);
    }

    public PosterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PosterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setChildrenDrawingOrderEnabled(true);
        LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = lf.inflate(R.layout.poster_item, null);
        posterBgImageView = (RoundedImageView) mainView.findViewById(R.id.imageView1);
        posterImageView = (RoundedImageView) mainView.findViewById(R.id.imageView2);
        viewPager = (AutoScrollViewPager) mainView.findViewById(R.id.image_pager);
        mainTitleView = (TextView) mainView.findViewById(R.id.textView2);
        subTitleView = (TextView) mainView.findViewById(R.id.textView1);
        titleBackgroundView = mainView.findViewById(R.id.ly_title);

        scaleBigAnim = AnimationUtils.loadAnimation(getContext(), R.anim.poster_scale_big);
        scaleSmallAnim = AnimationUtils.loadAnimation(getContext(), R.anim.poster_scale_small);
        
        if (attrs != null) {
            parseAttributes(context, attrs);
        }

        setAttributes();
        addView(mainView);

    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PosterView);
        posterBg = a.getResourceId(R.styleable.PosterView_pv_poster_bg, posterBg);
        poster = a.getResourceId(R.styleable.PosterView_pv_poster, poster);
        titleBackground = a.getResourceId(R.styleable.PosterView_pv_title_bg, titleBackground);
//        mainTitleColor = 0xFFFFFFFF;
//        subTitleColor = 0xFFFFFFFF;
//        mainTitleSize = 32;
//        subTitleSize = 26;

        if (poster != -1) {
            posterImageView.setImageResource(poster);
        }
        if (posterBg != -1) {
            posterBgImageView.setImageResource(posterBg);
        }
        if (titleBackground != -1) {
            titleBackgroundView.setBackgroundResource(titleBackground);
        }
        if (a.hasValue(R.styleable.PosterView_pv_main_title)) {
            mainTitle = a.getString(R.styleable.PosterView_pv_main_title);
            mainTitleView.setText(mainTitle);
        }
        if (a.hasValue(R.styleable.PosterView_pv_sub_title)) {
            subTitle = a.getString(R.styleable.PosterView_pv_sub_title);
            subTitleView.setText(subTitle);
        }

        a.recycle();
    }

    private void setAttributes() {
        mainTitleView.setTextColor(mainTitleColor);
        mainTitleView.setTextSize(mainTitleSize);
        subTitleView.setTextColor(subTitleColor);
        subTitleView.setTextSize(subTitleSize);
    }

    public void setPosterInfos(List<PosterInfo> posterInfos) {
        this.posterInfos = posterInfos;
        posterImageView.setVisibility(GONE);
        viewPager.setVisibility(VISIBLE);

        List<String> imageUrlList = new ArrayList<String>();
        for (PosterInfo posterInfo : posterInfos) {
            imageUrlList.add(posterInfo.getImageUrl());
        }
        viewPager.setAdapter(new ImagePagerAdapter(getContext(), imageUrlList).setInfiniteLoop(true));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        viewPager.setInterval(2000);
        viewPager.startAutoScroll();
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % imageUrlList.size());
    }

    public void setPosterUrl(String imgUrl) {
        posterImageView.setImageUrl(imgUrl);
    }

    public void setPosterBitmap(Bitmap bitmap) {
        posterImageView.setImageBitmap(bitmap);
    }

    public void setPosterDrawable(Drawable drawable) {
        posterImageView.setImageDrawable(drawable);
    }

    public void setPosterResource(int resId) {
        posterImageView.setImageResource(resId);
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
        mainTitleView.setText(mainTitle);
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        subTitleView.setText(subTitle);
    }

    @Override
    public Rect getScaledRect(float scaleXValue, float scaleYValue, boolean isScaled) {
        Rect firstRect = new Rect();

        this.getGlobalVisibleRect(firstRect);
        //firstRect.top += 60;

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


    public void posterScaleBig() {
//        posterImageView.startAnimation(scaleBigAnim);
    	mainView.bringToFront();	
    	mainView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start();
    }

    public void posterScaleSmall() {
//        posterImageView.startAnimation(scaleSmallAnim);
    	mainView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
    }

    @Override
    public int getViewType() {
        return mViewType;
    }

    public void setViewType(int viewType) {
        mViewType = viewType;
    }

    class ImagePagerAdapter extends RecyclingPagerAdapter {

        private Context context;
        private List<String> imageUrlList;

        private int size;
        private boolean isInfiniteLoop;

        public ImagePagerAdapter(Context context, List<String> imageUrlList) {
            this.context = context;
            this.imageUrlList = imageUrlList;
            this.size = imageUrlList == null ? 0 : imageUrlList.size();
            isInfiniteLoop = false;
        }

        @Override
        public int getCount() {
            // Infinite loop
            return isInfiniteLoop ? Integer.MAX_VALUE : imageUrlList == null ? 0 : imageUrlList.size();
        }

        /**
         * get really position
         *
         * @param position
         * @return
         */
        private int getPosition(int position) {
            return isInfiniteLoop ? position % size : position;
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = holder.imageView = new RoundedImageView(context);
                holder.imageView.setCornerRadius(13);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.imageView.setImageUrl(imageUrlList.get(getPosition(position)));
            return view;
        }

        private class ViewHolder {

            RoundedImageView imageView;
        }

        /**
         * @return the isInfiniteLoop
         */
        public boolean isInfiniteLoop() {
            return isInfiniteLoop;
        }

        /**
         * @param isInfiniteLoop the isInfiniteLoop to set
         */
        public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
            this.isInfiniteLoop = isInfiniteLoop;
            return this;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    }
    
}
