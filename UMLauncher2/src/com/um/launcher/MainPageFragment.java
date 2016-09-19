package com.um.launcher;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.listener.OnPlayerListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.um.launcher.constant.PosterConstants;
import com.um.launcher.data.MainPageAppInfo;
import com.um.launcher.data.PosterInfo;
import com.um.launcher.db.MainPageAppManager;
import com.um.launcher.interfaces.SourceManagerInterface;
import com.um.launcher.util.Constant;
import com.um.launcher.util.LogUtils;
import com.um.launcher.util.ScaleAnimEffect;
import com.um.launcher.util.Util;
import com.um.launcher.util.UtilDtv;
import com.um.launcher.view.PopupMenu;
import com.um.launcher.widget.AppView;
import com.um.launcher.widget.FocusedBasePositionManager;
import com.um.launcher.widget.FocusedRelativeLayout;
import com.um.launcher.widget.HomeRelativeLayout;
import com.um.launcher.widget.PosterView;
import com.um.launcher.widget.VideoLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The first big view
 */
@SuppressLint("ResourceAsColor")
public class MainPageFragment extends Fragment implements View.OnClickListener, OnFocusChangeListener{

    private static final String TAG = "MainPageFirst";
    private static final String DTV_NO_SIGNAL_ACTION = "unionman.intent.action.STATUS_TUNER";
    public final static int PAGENUM = 0;
    private static final int SHOW_INFO = 1008;
    private static final int SHOW_REF = 11;
    private static final int NOSIGNAL_CHECK_MAXCNT = 5;
    private static final int SIGNAL_NOSUPPORT_CHECK_MAXCNT = 3;
    private MainActivity mContext;
    // The actual background
    public View[] mImgView;
    public AppView[] mCustomAppViews;
    // when the Source is not used,set the background ashing
    public TextView mSignalTextView;
    // show the current Source
    private TextView mCurSourceText;
    private View parent;
    private float scaleNum = 1.0f;
    private Timer checkATVSignalStat = null;
    private TimerTask timerTask = null;
    private int mNoSignalCnt = 0;
    private int mSignalNoSupport = 0;
    private HomeRelativeLayout mContentLayout;
    private PopupMenu mPopupMenu;
    private MainPageAppManager mMainPageAppManager;
    private ScaleAnimEffect animEffect;
    private  BluetoothAdapter bluetoothAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.main_page_first, container, false);
        initView(parent);
    	animEffect = new ScaleAnimEffect();
        return parent;
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.d("hjian...");
        mNoSignalCnt = 0;
        mSignalTextView.setVisibility(View.INVISIBLE);
        registerListener();
        registDtvNoSignalBroadCast();
        if (checkATVSignalStat != null){
            checkATVSignalStat.cancel();
        }
        checkATVSignalStat = new Timer();
        checkATVSignalStat.schedule(checkSignalStat(), 500);

/*        if (video.equals(mContentLayout.getSelectedView())) {
            video.posterScaleBig();
        } else if (appStore.equals(mContentLayout.getSelectedView())) {
            appStore.posterScaleBig();
        }*/

//        checkFocus(true);
//
//        reflectImageShow();

        fixFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
        checkSignalHandler.removeMessages(SHOW_INFO);
        unRegisterListener();
        unregistDtvNoSignalBroadCast();
        if (checkATVSignalStat != null){
            checkATVSignalStat.cancel();
        }

        if (mPopupMenu != null && mPopupMenu.isShowing()) {
            mPopupMenu.dismiss();
        }
//        checkFocus(false);
    }

    private void initView(View parent) {
    	final VideoLayout first_item_window = (VideoLayout) parent.findViewById(R.id.first_item_window);
    	first_item_window.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					first_item_window.bringToFront();        
					first_item_window.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start();					
				}else{
				    first_item_window.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();					
				}
			}
		});

        mMainPageAppManager = new MainPageAppManager(mContext);
        mReflectImageViews = new ImageView[] {
                (ImageView)parent.findViewById(R.id.imgv_reflect01),
                (ImageView)parent.findViewById(R.id.imgv_reflect04),
                (ImageView)parent.findViewById(R.id.imgv_reflect05),
                (ImageView)parent.findViewById(R.id.custome_app_imgv_reflect01),
                (ImageView)parent.findViewById(R.id.custome_app_imgv_reflect02),
                (ImageView)parent.findViewById(R.id.custome_app_imgv_reflect03),
        };
        mImgView = new View[] {
                parent.findViewById(R.id.first_item_window), //电视
                parent.findViewById(R.id.first_item_ad), //广告
                parent.findViewById(R.id.app02), //设置
                parent.findViewById(R.id.app03), //多屏互动
                parent.findViewById(R.id.app04), //媒体
                parent.findViewById(R.id.app05), //信号源
        };

        mCustomAppViews = new AppView[] {
        		(AppView)parent.findViewById(R.id.custome_app01),
                (AppView)parent.findViewById(R.id.custome_app02),
                (AppView)parent.findViewById(R.id.custome_app03),
                (AppView)parent.findViewById(R.id.custome_app04),
                (AppView)parent.findViewById(R.id.custome_app05),
        };

        mSignalTextView = (TextView)parent.findViewById(R.id.first_window_signal_txt);

        for (int i = 0; i < mImgView.length; i++) {
            mImgView[i].setOnClickListener(this);
            mImgView[i].setTag("" + i);
        }

        mCurSourceText = (TextView) parent.findViewById(R.id.first_window_txt);

        ViewGroup root = (ViewGroup) parent.findViewById(R.id.first_page_id);

        mContentLayout = (HomeRelativeLayout) root.findViewById(R.id.ly_content);
        mContentLayout.setFocusMode(FocusedBasePositionManager.FOCUS_ASYNC_DRAW);
        mContentLayout.setScale(true);
        mContentLayout.setItemScaleValue(1.1f, 1.1f);
        mContentLayout.setFocusResId(R.drawable.launcher_item_border);
        mContentLayout.setFocusShadowResId(R.drawable.launcher_item_border);
        mContentLayout.setViewRight((int) getResources().getDimension(R.dimen.launcher_first_item_margin));
        mContentLayout.setViewLeft((int) getResources().getDimension(R.dimen.launcher_first_item_margin));
//        mContentLayout.setHorizontalMode(FocusedRelativeLayout.HORIZONTAL_SINGEL);

        mContentLayout.setFrameRate(5);

        mPopupMenu = new PopupMenu(mContext);
    	bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContentLayout.setOnEdgeListener(new FocusedRelativeLayout.EdgeListener() {
            @Override
            public void onEdge(int direction) {
                if (direction == View.FOCUS_UP) {
            		boolean isEnable =bluetoothAdapter.isEnabled();
            		Log.i(TAG, "=== mContentLayout View.FOCUS_UP Bluetooth isEnable="+isEnable);
            		if(!isEnable){
            			mPopupMenu.setBluetoothViewInvisiable();
            		}
            		
                    mPopupMenu.showAtLocation(mImgView[0], Gravity.NO_GRAVITY, 0, 0);
                    mPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    });
                }
            }
        });

        mImgView[0].setOnFocusChangeListener(this);
        updatePoster();
        updateCustomApp();
    }

    public void changeFocus(int i) {
        mContentLayout.changeFocus(i);
    }

    public void fixFocus() {
        LogUtils.d("hjian..." + mContentLayout.mIndex);
        View selectedView = mContentLayout.getSelectedView();
        if (selectedView == null) {
            selectedView = mContentLayout.getChildAt(0);
        }
        mContentLayout.setFocusable(true);
        mContentLayout.setFocusableInTouchMode(true);
        selectedView.setFocusable(true);
        selectedView.setFocusableInTouchMode(true);
        mContentLayout.requestFocus();
        selectedView.requestFocus();
        mContentLayout.changeFocus(mContentLayout.mIndex);
        mContentLayout.requestFocus();
        mContentLayout.initState();
    }

    public View checkFocus(boolean hasWindowFocus) {
//        PosterView video = (PosterView)mImgView[2];  yyf.0621
//        PosterView appStore = (PosterView)mImgView[3];
//        if (hasWindowFocus) {
//            if (video.equals(mContentLayout.getSelectedView())) {
//                video.posterScaleBig();
//            } else if (appStore.equals(mContentLayout.getSelectedView())) {
//                appStore.posterScaleBig();
//            }
//        } else {
//            if (video.equals(mContentLayout.getSelectedView())) {
//                video.posterScaleSmall();
//            } else if (appStore.equals(mContentLayout.getSelectedView())) {
//                appStore.posterScaleSmall();
//            }
//        }

        return mContentLayout.getSelectedView();
    }

    public void updatePoster(HashMap<String, List<PosterInfo>> posterInfoMap) {
//        PosterView video = (PosterView)mImgView[2];
//        PosterView appStore = (PosterView)mImgView[3];
//        AppView gameView = (AppView) mImgView[4];
//        AppView eduView = (AppView) mImgView[9];

        LogUtils.d("posterInfoMap: " + posterInfoMap);
//        if (posterInfoMap.containsKey(PosterConstants.POSTER_APP_STORE)) {
//            List<PosterInfo> appStorePosterInfos = posterInfoMap.get(PosterConstants.POSTER_APP_STORE);
//            if (appStorePosterInfos.size() > 0) {
//                String mainTitle = appStorePosterInfos.get(0).getName();
//                String picUrl = appStorePosterInfos.get(0).getImageUrl();
////                appStore.setMainTitle(mainTitle);
////                appStore.setPosterUrl(picUrl);
//            }
//        }

//        if (posterInfoMap.containsKey(PosterConstants.POSTER_EDUCATION)) {
//            List<PosterInfo> eduPosterInfos = posterInfoMap.get(PosterConstants.POSTER_EDUCATION);
//            if (!eduPosterInfos.isEmpty()) {
////                eduView.setAppIcon(eduPosterInfos.get(0).getImageUrl());
//            }
//        }

//        if (posterInfoMap.containsKey(PosterConstants.POSTER_GAME)) {
//            List<PosterInfo> gamePosterInfos = posterInfoMap.get(PosterConstants.POSTER_GAME);
//            if (!gamePosterInfos.isEmpty()) {
////                gameView.setAppIcon(gamePosterInfos.get(0).getImageUrl());
//            }
//        }
    }

    public void updatePoster() {
//        PosterView video = (PosterView)mImgView[2];  yyf.160621
//        PosterView appStore = (PosterView)mImgView[3];yyf
//        final AppView gameView = (AppView) mImgView[4];
//        AppView eduView = (AppView) mImgView[9];
//        video.setPosterResource(R.drawable.poster01);  yyf.160621,next 5 same.
//        video.setMainTitle(getResources().getString(R.string.AV));
//        video.setSubTitle(getResources().getString(R.string.AV));
//        appStore.setPosterResource(R.drawable.poster02);
//        appStore.setMainTitle(getResources().getString(R.string.appstore_normal));
//        appStore.setSubTitle(getResources().getString(R.string.appstore_normal));
    }
    
    private Bitmap drawableToBitmap(Drawable draw) {
        int width = draw.getIntrinsicWidth();
        int height = draw.getIntrinsicHeight();
        Bitmap.Config config = draw.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        draw.setBounds(0, 0, width, height);
        draw.draw(canvas);
        return bitmap;
    }

    public void updateCustomApp() {
        final PackageManager packageManager = mContext.getPackageManager();
        ArrayList<MainPageAppInfo> appInfos = mMainPageAppManager.getAll();
        Collections.sort(appInfos, MainPageAppManager.MAIN_PAGE_APP_COMPARATOR);
        int size = appInfos.size() > MainPageAppManager.CUSTOM_APP_COUNT ? 5 : appInfos.size();
        int index = 0;
        PackageInfo packageInfo = null;
        for (int i = 0; i < size; i++) {
            try {
                packageInfo = packageManager.getPackageInfo(appInfos.get(i).
                		getPackageName(), 0);
//                LogUtils.d("tag" + packageInfo.packageName + 
//                		packageInfo.applicationInfo.loadLabel(packageManager));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packageInfo != null) {
                index++;
                mCustomAppViews[i].setVisibility(View.VISIBLE);
                
                // Load Icon
                Bitmap iconBitmap = null;
                Drawable draw = packageInfo.applicationInfo.loadIcon(packageManager);
                if (draw != null) {
                	int newWidth = mCustomAppViews[i].getIconViewWidth();
                	if (newWidth <= 0) {
                		newWidth = 200;
                	}
                    int width = draw.getIntrinsicHeight();
                    int height = draw.getIntrinsicWidth();
                    
                    Bitmap bitmap = drawableToBitmap(draw);
                    if (width*height > newWidth*newWidth) {
                    	Log.v(TAG, "icon is too large, need to scale it. width="+width+", height="+height);
                    	iconBitmap = Bitmap.createScaledBitmap(bitmap, newWidth,
                        		newWidth, true);
                    } else {
                    	iconBitmap = bitmap;
                    }
                }
                mCustomAppViews[i].setAppIcon(iconBitmap);
                mCustomAppViews[i].setTitle((String) packageInfo.applicationInfo.loadLabel(packageManager));
                final PackageInfo finalPackageInfo = packageInfo;
                mCustomAppViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = packageManager.getLaunchIntentForPackage(
                        		finalPackageInfo.packageName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (intent != null) {
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
        } // for  --  end.

        // Jump to more app application.
        mCustomAppViews[index].setVisibility(View.VISIBLE);
        mCustomAppViews[index].setAppIcon(R.drawable.page_allapp);
        mCustomAppViews[index].setTitle(getResources().getString(R.string.more_app));
        mCustomAppViews[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyAppActivity.class);
                mContext.startActivity(intent);
            }
        });

//        for (int i = index + 1; i < 4; i++) {  yyf.160621,没必要存在的代码。
//            mCustomAppViews[i].setVisibility(View.INVISIBLE);
//        }

        if (mContentLayout.getSelectedView() != null
                && mContentLayout.getSelectedView().getVisibility() == View.INVISIBLE) {
            mContentLayout.setFocusViewId(mCustomAppViews[index].getId());
        }

        reflectImageShow();
    }

    private ImageView[] mReflectImageViews;
    public void reflectImageShow(){
        int height = 100;
        Bitmap resizeBitmap;
        if (mReflectImageViews[0].getDrawable() == null){
            resizeBitmap = Util.createReflectedImage(mImgView[0], height);
            if (resizeBitmap != null)
                mReflectImageViews[0].setImageBitmap(resizeBitmap);
        }

        if ((mReflectImageViews[1].getDrawable() == null) && (!mImgView[2].isFocused())){
            resizeBitmap = Util.createReflectedImage(mImgView[2], height);
            if (resizeBitmap != null)
                mReflectImageViews[1].setImageBitmap(resizeBitmap);
        }

        if ((mReflectImageViews[2].getDrawable() == null) && (!mImgView[3].isFocused())){
            resizeBitmap = Util.createReflectedImage(mImgView[3], height);
            if (resizeBitmap != null)
                mReflectImageViews[2].setImageBitmap(resizeBitmap);
        }
//  --------------------------  考虑更改样式  ------------------------------
//        if ((mReflectImageViews[3].getDrawable() == null) && (!mImgView[5].isFocused())){
//            resizeBitmap = Util.createReflectedImage(mImgView[5], height);
//            if (resizeBitmap != null)
//                mReflectImageViews[3].setImageBitmap(resizeBitmap);
//        }
        mReflectImageViews[3].setVisibility(View.INVISIBLE);
    }

    private PopupWindow mSourceListPopuwnd;
    private PopupWindow createSourceSwitchPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.source_switch_list, null);
        ListView sourceListView = (ListView) view.findViewById(R.id.lv_source_switch_list);
        String[] names =  mContext.getResources().getStringArray(R.array.sources_name);
        sourceListView.setAdapter(
                new ArrayAdapter<String>(
                        mContext,
                        R.layout.source_switch_item,
                        R.id.source_name,
                        names));
        PopupWindow sourceListPopuwnd = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        sourceListPopuwnd.setBackgroundDrawable(new ColorDrawable(0));
        sourceListPopuwnd.setFocusable(true);
//        sourceListPopuwnd.showAsDropDown(parent);


        return sourceListPopuwnd;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.first_item_window:
//                mContext.enterToPlay();
            	SourceManagerInterface.selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
                String pkg1 = Constant.Default_Start_Apk_Package_Name;
                String cls1 = Constant.Default_Start_Apk_MainClass_Name;
                ComponentName componentName1 = new ComponentName(pkg1, cls1);
                intent = new Intent();
                intent.setComponent(componentName1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
                SystemProperties.set("persist.sys.fullScreen_Source", ""+EnumSourceIndex.SOURCE_MEDIA);
                break;
            case R.id.first_item_ad:
                break;
//            case R.id.poster01:  yyf.160621
//                intent = mContext.getPackageManager().getLaunchIntentForPackage("cn.cibntv.ott");
//                if (intent != null) {
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                break;
//            case R.id.poster02: //yyf.0621
//                intent = mContext.getPackageManager().getLaunchIntentForPackage("com.huan.appstore");
//                if (intent != null) {
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                break;
//            case R.id.app01:    //tv.huan.le.Mugen  -- 游戏应用
//                intent = mContext.getPackageManager().getLaunchIntentForPackage("tv.huan.le.Mugen");
//                if (intent != null) {
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                break;      cn.com.unionman.umtvsetting.umsettingmenu
            case R.id.app02:
                String pkg = "cn.com.unionman.umtvsetting.umsettingmenu";
                String cls = "cn.com.unionman.umtvsetting.umsettingmenu.MainActivity";
                ComponentName componentName = new ComponentName(pkg, cls);
                intent = new Intent();
                intent.setComponent(componentName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case R.id.app03:
                intent = new Intent(mContext, MyMultiScreenActivity.class);
                break;
            case R.id.app04:
                intent = mContext.getPackageManager().getLaunchIntentForPackage("com.umexplorer");
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                break;
            case R.id.app05:  //  signal source app.
                intent = mContext.getPackageManager().getLaunchIntentForPackage("com.source");
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                break;
//            case R.id.app06:    //com.huan.edu.lexue.frontend   -- 教育应用。
//                intent = mContext.getPackageManager().getLaunchIntentForPackage("com.huan.edu.lexue.frontend");
//                if (intent != null) {
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                break;
            default:
                break;
        }
        if (intent != null) {
            mContext.startToActivity(intent);
            startActivityDo(v);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
         Log.i(TAG, "=======================================hasFocus==========================================="+hasFocus);
        if (hasFocus) {
            // Set the flag and focus related position
            switch (v.getId()) {
                case R.id.first_item_window:
                    mSignalTextView.setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
//                    mSourceListPopuwnd.showAsDropDown(v);
                    break;

                default:
                    break;
            }
        } else {
                switch (v.getId()) {
                case R.id.first_item_window:
                    mSignalTextView.setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
//                    mSourceListPopuwnd.dismiss();
                    break;
                default:
                    break;
            }
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
                mCurSourceText.setText(R.string.AV);
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

	private void startActivityDo(View v){
		int flag = MainActivity.ENTER_ACTIVITY_BY_CLICK;
		if (v.getId() == R.id.first_item_window){
			flag = flag | MainActivity.ENTER_ACTIVITY_WITHOUT_SELECT_SOURCE;
		}
		
		mContext.setEnterActivityFlag(flag);
	}
	
	 /**
     * TV play listener
     */
    OnPlayerListener onPlayerListener = new OnPlayerListener() {

        @Override
        public void onPCAutoAdjustStatus(int arg0) {
            Log.d(TAG, "  onPCAutoAdjustStatus  arg0: " + arg0);
        }

        @Override
        public void onSignalStatus(int arg0) {
            //if (Constant.LOG_TAG) {
                Log.d(TAG, "onSignalStatus  arg0: " + arg0);
            //}
                handleSignalStat(arg0);
        }

        @Override
        public void onTimmingChanged(TimingInfo arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onTimmingChanged  arg0: " + arg0);
            }
        }

        @Override
        public void onSrcDetectPlugin(ArrayList<Integer> arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugin  arg0: " + arg0);
            }
        }

        @Override
        public void onSrcDetectPlugout(ArrayList<Integer> arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugout  arg0: " + arg0);
            }
        }
        
        @Override
        public void onSelectSource(int arg0) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSrcDetectPlugout  arg0: " + arg0);
            }
        }
        @Override
        public void onSelectSourceComplete(int  arg0,int arg1,int arg2) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onSelectSourceComplete  arg0: " + arg0);
            }
            Log.d(TAG, "===yiyonghui=== ");
        }
		
	    @Override
        public void onPlayLock(ArrayList<Integer> list) {
            // TODO Auto-generated method stub
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onPlayLock  arg0: " + list);
            }
        }
    };

	/**
     * register all listeners
     */
    private void registerListener() {
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_SIGNAL_STATUS, onPlayerListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_TIMMING_CHANGED, onPlayerListener);
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGIN,
                onPlayerListener);
        UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_PLUGOUT,
                onPlayerListener);
        UmtvManager.getInstance().registerListener(
                TVMessage.HI_TV_EVT_PC_ADJ_STATUS, onPlayerListener);
    }
    
    private void unRegisterListener(){
    	UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_SIGNAL_STATUS, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_TIMMING_CHANGED, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PLUGIN, onPlayerListener);
        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PLUGOUT, onPlayerListener);

        UmtvManager.getInstance().unregisterListener(
                TVMessage.HI_TV_EVT_PC_ADJ_STATUS, onPlayerListener);
    }
    
    private void handleSignalStat(int sigstat){
    	int sourceid = SourceManagerInterface.getLastSourceId();
        Log.d(TAG,"leon... firstpage showSignalStat");
        if ((sourceid != EnumSourceIndex.SOURCE_DVBC) && (sourceid != EnumSourceIndex.SOURCE_DTMB) && (sourceid != EnumSourceIndex.SOURCE_MEDIA)){
            if (sigstat == EnumSignalStat.SIGSTAT_NOSIGNAL){
            	mSignalNoSupport = 0;
            	if (mNoSignalCnt >= NOSIGNAL_CHECK_MAXCNT){
            		mSignalTextView.setText(R.string.no_signal);
            		if (mImgView[Constant.NUMBER_0].isFocused()){
            			mSignalTextView.setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
            		}else{
            			mSignalTextView.setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            		}
            		mSignalTextView.setVisibility(View.VISIBLE);
            		mNoSignalCnt = 0;
            	}else{
                	mNoSignalCnt++;
                	checkATVSignalStat.schedule(checkSignalStat(),500);
            	}
            }else if(sigstat == EnumSignalStat.SIGSTAT_SUPPORT){
            	mSignalTextView.setVisibility(View.INVISIBLE);
            	mNoSignalCnt = 0;
            	mSignalNoSupport = 0;
            }else if (sigstat == EnumSignalStat.SIGSTAT_SUPPORT){
            	mNoSignalCnt = 0;
            	if (mSignalNoSupport >= SIGNAL_NOSUPPORT_CHECK_MAXCNT){
            		mSignalTextView.setText(R.string.signal_nosupport);
            		if (mImgView[Constant.NUMBER_0].isFocused()){
            			mSignalTextView.setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
            		}else{
            			mSignalTextView.setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
            		}
            		mSignalTextView.setVisibility(View.VISIBLE);
            		mSignalNoSupport = 0;
            	}else{
            		mSignalNoSupport++;
            		checkATVSignalStat.schedule(checkSignalStat(),500);
            	}
            }
        }
    }

    Handler checkSignalHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SHOW_INFO:
            	int sigstat = SourceManagerInterface.getSignalStatus();
            	handleSignalStat(sigstat);
                break;
            default:
                break;
            }
        }
    };
    
    public TimerTask checkSignalStat(){
        timerTask = new TimerTask(){
            @Override
            public void run(){
                checkSignalHandler.sendEmptyMessage(SHOW_INFO);
            }
        };
        return timerTask;
    }
    
    private void registDtvNoSignalBroadCast(){
    	IntentFilter filter = new IntentFilter(DTV_NO_SIGNAL_ACTION);
    	mContext.registerReceiver(dtvNoSignalReceiver, filter);
    }
    
    private void unregistDtvNoSignalBroadCast(){
    	mContext.unregisterReceiver(dtvNoSignalReceiver);
    }
    
    private BroadcastReceiver dtvNoSignalReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "dtvNoSignalReceiver DTV_NO_SIGNAL_ACTION");
            String action = intent.getAction();
            if (action.equals(DTV_NO_SIGNAL_ACTION)){
            	int sourceid = SourceManagerInterface.getLastSourceId();
            	if ((sourceid == EnumSourceIndex.SOURCE_DVBC) || (sourceid == EnumSourceIndex.SOURCE_DTMB)){
            		boolean noSignalFlag = intent.getBooleanExtra("no_signal", true);
            		Log.e(TAG, "dtvNoSignalReceiver DTV_NO_SIGNAL_ACTION,noSignalFlag:"+noSignalFlag);
                	if (noSignalFlag){
					if(UtilDtv.getInstance().isDTVProgEmpty(mContext)==true)
					{
						mSignalTextView.setText(R.string.no_program);
					}
					else
					{
						mSignalTextView.setText(R.string.no_signal);	
					}
              		if (mImgView[Constant.NUMBER_0].isFocused()){
                  			mSignalTextView.setTextColor(Color.argb(0xff, 0xf0, 0xf0, 0xf0));
                		}else{
                			mSignalTextView.setTextColor(Color.argb(153, 0xf0, 0xf0, 0xf0));
                		}
                		mSignalTextView.setVisibility(View.VISIBLE);
                	}else{
                		mSignalTextView.setVisibility(View.INVISIBLE);
                	}
            	}        	
            }
        }
    };

    public void setOnDirectionKeyListener(FocusedRelativeLayout.DirectionKeyListener directionKeyListener) {
        mContentLayout.setOnDirectionKeyListener(directionKeyListener);
    }

    public void setOnSourceChangeListener(FocusedRelativeLayout.SourceChangeListener sourceChangeListener) {
        mContentLayout.setOnSourceChangeListener(sourceChangeListener);
    }

    public void resetSignalCheckStatus(){
       int sourceid = SourceManagerInterface.getLastSourceId();
       if (mSignalTextView != null){
           mSignalTextView.setVisibility(View.INVISIBLE);
       }
       
       if ((sourceid != EnumSourceIndex.SOURCE_DVBC) && (sourceid != EnumSourceIndex.SOURCE_DTMB) && (sourceid != EnumSourceIndex.SOURCE_MEDIA)){
    	   mNoSignalCnt = 0;
            if (checkATVSignalStat != null){
                checkATVSignalStat.cancel();
            }
            checkATVSignalStat = new Timer();
            checkATVSignalStat.schedule(checkSignalStat(),500);
       }
	}
}
