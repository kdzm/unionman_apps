package com.um.launcher.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class VideoLayout extends FrameLayout
        implements
        FocusedRelativeLayout.ViewTypeInterface{

    private String text = "";
    private String name = "";
    private Drawable mDrawable;

    public VideoLayout(Context context) {
        super(context);
    }

    public VideoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);
/*		
        Rect dst = new Rect();// 屏幕裁剪区域
		dst.left = 2;
		dst.top = 280;
		dst.right = getWidth() - 2;
		dst.bottom = 382;
		mDrawable.draw(canvas);
		mDrawable.setBounds(dst);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setShadowLayer(1, 2, 2, Color.BLACK);
		paint.setTextSize(24);
		int hight = 0;
		int width = 0;
		int textNameW = (int) paint.measureText(getVideoName());
		
		width = this.getWidth();
		hight = this.getHeight() - 100;
	
		canvas.drawText(getTitle(), 30, hight, paint);		
		canvas.drawText(getVideoName(), width - textNameW - 50 , hight, paint);
*/

    }

    public void setTitle(String text) {
        this.text = text;
    }

    public String getTitle() {
        return text;
    }

    public void setVideoName(String name) {
        this.name = name;
    }

    public String getVideoName() {
        return name;
    }

/*    @Override
    public boolean getIfScale() {
        return false;
    }

    @Override
    public Rect getScaledRect(float arg0, float arg1, boolean arg2) {
        Rect rect = new Rect();
        this.getGlobalVisibleRect(rect);
        int padding = 0;
		rect.bottom = rect.bottom + padding;
        rect.top = rect.top - padding;
        rect.left = rect.left - padding;
        rect.right = rect.right + padding;

        return rect;
    }
*/
    @Override
    public int getViewType() {
        return FocusedRelativeLayout.ViewTypeInterface.TYPE_VIDEO;
    }
}
