
package com.hisilicon.higallery.core;

import android.graphics.Point;
import android.os.Looper;
import android.os.Parcel;
import android.view.Surface;
import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class GalleryCore {
    /**
     * 初始化完成时回调的命令，obj:boolean 初始化的结果
     * 
     * @see Callback#onReceiveCMD
     */
    public static final int CMD_INIT_COMPLETED = 0;

    /**
     * 查看一张图片完成时回调的命令，obj:boolean 显示完成的结�?     * 
     * @see Callback#onReceiveCMD
     */
    public static final int CMD_VIEW_COMPLETED = 1;

    public static final int CMD_SHOWN_FRAME_CHANGED = 2;

    /** 图片移动的方�?*/
    public enum Direction {
        LEFT, UP, RIGHT, DOWN
    };

    /** 图片旋转角度 */
    public enum Rotation {
        ROTATION_0(0),
        ROTATION_90(90),
        ROTATION_180(180),
        ROTATION_270(270);

        int degree;

        private Rotation(int d) {
            degree = d;
        }
    }

    /** 幻灯片使用的动画类型 */
    public enum AnimType {

        /** 无动�?*/
        ANIM_NONE(0),
        /** 缩放 */
        ANIM_SCALE(1),
        /** 滑动 */
        ANIM_SLIDE(2),
        /** 淡入淡出 */
        ANIM_FADE(3),
        /** 随机 */
        ANIM_RANDOM(4);

        int type;

        private AnimType(int t) {
            type = t;
        }
    }

    public enum ViewMode {
        ORIGINAL_MODE(0),
        FULLSCREEN_MODE(1),
        AUTO_MODE(2),
        SCALE_MODE(3);

        int mode;

        private ViewMode(int m) {
            mode = m;
        }
    }

    /**
     * 接收底层回调回来的命�?     */
    public interface Callback {
        /**
         * @param cmd 底层回调回来的命�?         * @param obj 与命令相关的状�?         * @see GalleryCore#CMD_INIT_COMPLETED
         * @see GalleryCore#CMD_VIEW_COMPLETED
         */
        public void onReceiveCMD(int cmd, Object obj);
    }

    /**
     * 接收底层回调回来的命�?包含图片Url
     */
    public interface CallbackWithUrl {
        /**
         * @param cmd 底层回调回来的命�?         * @param obj 与命令相关的状�    
         * @param parcel 包含图片显示状态、图片url
         * @see GalleryCore#CMD_INIT_COMPLETED
         * @see GalleryCore#CMD_VIEW_COMPLETED
         */
        public void onReceiveCMDWithUrl(int cmd, Object obj,Parcel parcel);
    }

    public interface Sliding{
        public void showNext();
    }

    static GalleryImpl sGalleryCore;
    /** 获得GalleryCore的实�?*/
    public static GalleryCore getGallery(Looper looper) {
        if (sGalleryCore == null) {
            sGalleryCore = new GalleryImpl(looper);
        } else {
            sGalleryCore.setLooper(looper);
        }
        return sGalleryCore;
    }

    /**
     * 设置底层状态回调的接收
     *
     * @param callback
     * @see Callback
     */
    public abstract void setCallback(Callback callback);

    /**
     * 设置底层状态回调的接收，并带图片url
     *
     * @param callback
     * @see CallbackWithUrl
     */
    public abstract void setCallbackWithUrl(CallbackWithUrl callback);

    /**
     * 初始化GalleryCore，一般在 {@link android.app.Activity#onCreate} 里调�?     * 
     * @param width 视频层的宽度
     * @param height 视频层的高度
     */
    public abstract void init(int width, int height);

    /**
     * 初始化GalleryCore，一般在 {@link android.app.Activity#onCreate} 里调�?     * 
     * @param width 视频层的宽度
     * @param height 视频层的高度
     * @param maxUsedMemSize 解码可以使用的内存大小，单位M
     */
    public abstract void init(int width, int height, int maxUsedMemSize);

    /**
     * 反初始化GalleryCore�?释放内部资源一般在 {@link android.app.Activity#onStop} 里调�?     * 
     * @return 反初始化结果
     */
    public abstract boolean deinit();

    /**
     * 设置动画开�?     * 
     * @param enable true 打开动画；false 关闭动画
     * @return 动画设置的结�?     */
    public abstract void enableAnimation(boolean enable);

    /**
     * 查看图片 图片显示完成�?{@link Callback#onReceiveCMD} 会收�?     * {@link #CMD_INIT_COMPLETED} 命令
     * 默认为全屏显�?     * 
     * @param path 图片所在目�?     */
    public abstract void viewImage(String path);

    /**
     * 查看图片 图片显示完成�?{@link Callback#onReceiveCMD} 会收�?     * {@link #CMD_INIT_COMPLETED} 命令
     * 
     * @param path 图片所在目�?     * @param fullScreen, true 图片全屏显示�?false 图片按原始尺寸显�?     */
    public abstract void viewImage(String path, boolean fullScreen);

    public abstract void viewImage(final String path, final ViewMode viewmode);

    /**
     * 查看图片 图片显示完成�?{@link Callback#onReceiveCMD} 会收�?     * {@link #CMD_INIT_COMPLETED} 命令
     * 
     * @param path 图片所在目�?     * @param scale 图片显示时的拉伸比例
     */
    public abstract void viewImage(String path, float scale);

    /**
     * 获得当前显示的图片的真实尺寸
     * 
     * @param size 保存图片的尺�?     */
    public abstract void getImageSize(Point size);

    /**
     * 放大当前图片
     * 
     * @return true 图片被放大， false 图片不能继续放大
     */
    public abstract boolean zoomIn();

    /**
     * 缩小当前图片
     * 
     * @return true 图片被缩小， false 图片不能继续缩小
     */
    public abstract boolean zoomOut();

    /**
     * 放大、缩小当前图�?     * 
     * @param scale 缩放的倍数�?.125 �?0.8 之间�?     * @return true 缩放成功�?缩放失败
     */
    public abstract boolean zoom(float scale);

    /**
     * 朝指定方向移动当前图�?     * 
     * @param r 图片移动的方�?     * @param step 移动的大小（像素�?     * @return true 图片移动成功，false 图片不能像该方向移动
     * @see Direction
     */
    public abstract boolean move(Direction r, int step);

    /**
     * 旋转当前图片
     * 
     * @param r 旋转的角�?     * @return true 旋转成功，false 旋转失败
     * @see Rotation
     */
    public abstract boolean rotate(Rotation r);

    /**
     * 重置所有变�?     *
     * @return true 重置成功，false 重置失败
     */
    public abstract boolean reset();

    /**
     * 启动幻灯片浏览模�?     * 
     * @param a 幻灯片切换的动画
     * @param interval 幻灯片切换的间隔（秒�?     * @return true 幻灯片模式已经启�?     * @see AnimType
     */
    public abstract boolean startSliding(Sliding s, AnimType a, long interval);

    public abstract boolean startSliding(Sliding s, AnimType a, AnimType[] randomSeeds, long interval);

    /**
     * 停止幻灯片浏览模�?     * 
     * @return true 幻灯片模式已经停�?     */
    public abstract boolean stopSliding();
    
    public abstract void enablePQ(boolean enable);

    // Maybe remove
    public abstract void initWithSurface(Surface surface, int width, int height);

    public abstract void setFailBitmap(Bitmap bitmap);
    public abstract String getCurrentPath();
    public abstract void getDisplaySize(Point size);
    public abstract boolean decodeSizeEvaluate(String path, int width, int height, int sampleSize ,int size);
    public abstract Rect getShownFrame();
}
