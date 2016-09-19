package com.um.tv.menu.app;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.UmtvManager;
import com.um.tv.menu.R;
import com.um.tv.menu.model.ADBConsoleModel;
import com.um.tv.menu.model.ADCAdjustModel;
import com.um.tv.menu.model.AndroidSettingModel;
import com.um.tv.menu.model.CombinatedModel;
import com.um.tv.menu.model.FactorySingleKeyModel;
import com.um.tv.menu.model.InfoModel;
import com.um.tv.menu.model.Model;
import com.um.tv.menu.model.NextPageModel;
import com.um.tv.menu.model.NonLinearModel;
import com.um.tv.menu.model.NonStandardModel;
import com.um.tv.menu.model.OtherOptionsModel;
import com.um.tv.menu.model.OverscanModel;
import com.um.tv.menu.model.PictureModel;
import com.um.tv.menu.model.SSCAdjustModel;
import com.um.tv.menu.model.UMNetUpgradeModel;
import com.um.tv.menu.model.WhiteBlanceModel;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;

@SuppressLint("HandlerLeak")
public class FactoryWindowManager {
	private static final String TAG = "UMFACTORYMENU";
	private static final String SUBTAG = "FactoryWindowManager:";
    private static FactoryWindowManager mManager = null;
    private static Context mContext = null;
    private FactoryWindow mFactoryWindow = null;
    private CusFactory mFactory = UmtvManager.getInstance().getFactory();

    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mFactoryLayoutParams = null;

    private MyListAdapter mAdapter = null;
    private CombinatedModel mRootModel = null;

    private FactoryWindowManager() {
        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);

        mFactoryLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mFactoryLayoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);

        mFactoryLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mFactoryLayoutParams.type = LayoutParams.TYPE_PHONE;
        //      mFactoryLayoutParams.width = LayoutParams.MATCH_PARENT;
        //      mFactoryLayoutParams.height = LayoutParams.MATCH_PARENT;
        initViews();

    }

    public static FactoryWindowManager from(Context context) {
        mContext = context;
        if (mManager == null) {
            mManager = new FactoryWindowManager();
        }
        return mManager;
    }

    public void changeFactoryMenuStatus() {
		Log.d(TAG, SUBTAG+"mFactoryWindow.isShowing() "+mFactoryWindow.isShowing());
        if (mFactoryWindow.isShowing()) {
            dismissFactory();
			
            mHandler.sendMessage(mHandler.obtainMessage(MsgUpdateItems, mRootModel));
        } else {
            initViews();
            showFactory();
            if (Utils.DEBUG) {
                DisplayMetrics dm = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(dm);
                Log.d(TAG, SUBTAG+"current display--->ScreenWidth:" + dm.widthPixels + "	ScreenHeight:" + dm.heightPixels + "\n-------->WindowWidth:" + mFactoryWindow.getWidth() + "	WindowHeight:" + mFactoryWindow.getHeight());
                //              mFactoryWindow.printWindowSize();
            }
        }
    }

    public boolean isFactoryShow(){
    	return mFactoryWindow.isShowing();
    }
    
    private void showFactory() {
    	Log.d(TAG,SUBTAG+"showFactory()--->");
        mWindowManager.addView(mFactoryWindow, mFactoryLayoutParams);
        mFactoryWindow.setShowing(true);
    }

    private void dismissFactory() {
    	Log.d(TAG,SUBTAG+"dismissFactory()--->");
        mWindowManager.removeView(mFactoryWindow);
        mFactoryWindow.setShowing(false);
        mFactoryWindow.init();
    }

    private FactoryWindow.WindowCallback mFactoryCallback = new FactoryWindow.WindowCallback() {

        @Override
        public void show() {
            // TODO Auto-generated method stub

        }

        @Override
        public void dismiss() {
            // TODO Auto-generated method stub
            dismissFactory();
        }

        @Override
        public void update(Model model) {
            // TODO Auto-generated method stub
            mHandler.sendMessage(mHandler.obtainMessage(MsgUpdateItems, model));
        }

        @Override
        public void changeValue(int direct) {
            // TODO Auto-generated method stub
            mHandler.sendMessage(mHandler.obtainMessage(MsgChangeValue, direct));
        }

        @Override
        public void gotoUpperLevel() {
            // TODO Auto-generated method stub
            if (mAdapter.getCurrentModel().mParent != null) {
                mHandler.sendEmptyMessage(MsgGoUpperLevel);
            } else {
                dismissFactory();
            }
        }
    };

    private void initViews() {
        mFactoryWindow = new FactoryWindow(mContext);

        initItems();
        CombinatedModel blankModel = new CombinatedModel(mContext,
                mFactoryWindow, mFactory, CombinatedModel.TypeRoot);
        mAdapter = new MyListAdapter(mContext, blankModel);
        mFactoryWindow.setAdapter(mAdapter);
        mFactoryWindow.setOnItemClick(mOnItemClickListener);
        mFactoryWindow.setCallback(mFactoryCallback);

        mFactoryLayoutParams.width = (int)mContext.getResources().getDimension(R.dimen.window_width);
        mFactoryLayoutParams.height = (int)mContext.getResources().getDimension(R.dimen.window_height);
    }

    private void initItems() {
        new AsyncTask<Void, Void, CombinatedModel>() {

            @Override
            protected CombinatedModel doInBackground(Void... arg0) {
                // TODO Auto-generated method stub
                CombinatedModel mRootModel = new CombinatedModel(mContext, mFactoryWindow, mFactory, CombinatedModel.TypeRoot);

                if (Utils.EnableADC) {
                    ADCAdjustModel adc = new ADCAdjustModel(mContext, mFactoryWindow, mFactory);
                    mRootModel.addChild(adc);
                }

                if (Utils.EnableWhiteBlance) {
                    WhiteBlanceModel wb = new WhiteBlanceModel(mContext, mFactoryWindow, mFactory);
                    mRootModel.addChild(wb);
                }


                if (Utils.EnableOverscan) {
                    OverscanModel os = new OverscanModel(mContext, mFactoryWindow, mFactory);
                    mRootModel.addChild(os);
                }

                if (Utils.EnablePictureMode) {
                    PictureModel pm = new PictureModel(mContext, mFactoryWindow, mFactory);
                    mRootModel.addChild(pm);
                }

                if (Utils.EnableNonLinear) {
                    NonLinearModel ml = new NonLinearModel(mContext, mFactoryWindow, mFactory);
                    mRootModel.addChild(ml);
                }

                if (Utils.EnableNonStandard) {
                    NonStandardModel ns = new NonStandardModel(mContext, mFactoryWindow, mFactory);
                    mRootModel.addChild(ns);
                }
                
                if (Utils.EnableSSCAdjust){
                	SSCAdjustModel ssc = new SSCAdjustModel(mContext, mFactoryWindow, mFactory);
                	mRootModel.addChild(ssc);
                }

                if (Utils.EnableOtherOptions) {
                    OtherOptionsModel oo = new OtherOptionsModel(mContext, mFactoryWindow, mFactory);
                    mRootModel.addChild(oo);
                }

                if (Utils.EnableInfo) {
                    InfoModel info = new InfoModel(mContext, mFactoryWindow, mFactory);
                    mRootModel.addChild(info);
                }
                
                if(Utils.EnableFactorySingleKey){
                	FactorySingleKeyModel factory = new FactorySingleKeyModel(mContext, mFactoryWindow, mFactory);
                	mRootModel.addChild(factory);
                }
                
                if(Utils.EnablePage2){
                	NextPageModel page2 = new NextPageModel(mContext, mFactoryWindow, mFactory);
                	mRootModel.addChild(page2);
                }

                if (Utils.EnableUMNetUpgrade){
                	UMNetUpgradeModel netUpgrade = new UMNetUpgradeModel(mContext, mFactoryWindow, mFactory);
                	mRootModel.addChild(netUpgrade);
                }
                
                if (Utils.EnableAndroidSetting){
                	AndroidSettingModel androidSetting = new AndroidSettingModel(mContext, mFactoryWindow, mFactory);
                	mRootModel.addChild(androidSetting);
                }
                if (Utils.EnableADBConsole){
                	ADBConsoleModel aDBConsoleModel = new ADBConsoleModel(mContext, mFactoryWindow, mFactory);
                	mRootModel.addChild(aDBConsoleModel);
                }
                
                return mRootModel;
            }

            @Override
            protected void onPostExecute(CombinatedModel result) {
                mRootModel = result;
                mHandler.sendMessage(mHandler.obtainMessage(MsgUpdateItems, result));
            };

        } .execute();
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
        long id) {
            // TODO Auto-generated method stub
            Model m = mAdapter.getCurrentModel();
            m.onItemClick(parent, view, position, id);
            mFactoryWindow.setSelection(0);
        }
    };

    private static final int MsgUpdateItems = 1;
    private static final int MsgChangeValue = 2;
    private static final int MsgGoUpperLevel = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgUpdateItems:
				Model model = (Model) msg.obj;
				if (model != null) {
					mAdapter.updateItems(model);
					mFactoryWindow.updateTitle(model.mName == null ? Utils.DisplayNameFactoryMenu
							: model.mName);
				}
				break;
			case MsgChangeValue:
				Object direct = msg.obj;
				if (direct != null) {
					((Model) mAdapter.getCurrentModel()).changeValue(
							(Integer) direct,
							mFactoryWindow.getSelectedItemPosition(),
							mFactoryWindow.getSelectedView());
				}
				break;
			case MsgGoUpperLevel:
				Model m = mAdapter.getCurrentModel().mParent;
				mAdapter.updateItems(m);
				mFactoryWindow.setSelection(0);
				mFactoryWindow.updateTitle(m.mName == null ? Utils.DisplayNameFactoryMenu
						: m.mName);
				break;
			default:
				break;
			}
        };
    };
}
