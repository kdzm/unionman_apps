
package com.um.launcher.logic.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.um.launcher.R;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.model.WidgetType;
import com.um.launcher.model.WidgetType.AccessOnClickInterface;
import com.um.launcher.util.Constant;
import com.um.launcher.view.setting.SystemUpdateDialog;

/**
 * local update
 *
 * @author wangchuanjian
 */
public class SystemLocalUpdateLogic implements InterfaceLogic {

    private final static String TAG = "SystemLocalUpdateLogic";
    private final static String MOUNT_LABLE = "mountLable";
    private final static String MOUNT_TYPE = "mountType";
    private final static String MOUNT_PATH = "mountPath";
    private final static String MOUNT_NAME = "mountName";
    private final static String FILE_NAME = "/update.zip";
    private Context mContext;
    private SystemUpdateDialog mSystemUpdateDialog;

    public SystemLocalUpdateLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        final List<Map<String, String>> li = getMountEquipmentList();
        if (Constant.LOG_TAG) {
            for (int i = 0; i < li.size(); i++) {
                Log.d(TAG, li.get(i).get(MOUNT_NAME));
            }
        }
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        // Resources res = mContext.getResources();
        WidgetType mSysLocUpdate = null;
        for (int i = 0; i < li.size(); i++) {
            // SystemLocalUpdate
            mSysLocUpdate = new WidgetType();
            // set name for SystemLocalUpdate
            mSysLocUpdate.setOnlySelectorName(li.get(i).get(MOUNT_NAME));
            // set type for SystemLocalUpdate
            mSysLocUpdate.setType(WidgetType.TYPE_ONLYSELECTOR);
            // set tag for SystemLocalUpdate
            mSysLocUpdate.setTag(i);
            mSysLocUpdate
                    .setmAccessOnClickInterface(new AccessOnClickInterface() {

                        @Override
                        public void onClickEvent(View v) {
                            // f the letter is the upgrade package
                            int i = Integer.valueOf(v.getTag().toString());
                            if (Constant.LOG_TAG) {
                                Log.d(TAG,
                                        "Integer.valueOf(v.getTag().toString())"
                                                + Integer.valueOf(v.getTag()
                                                        .toString()));
                            }
                            if (isFileExist(li.get(i).get(MOUNT_PATH),
                                    FILE_NAME)) {
                                createDialog(350, 400,
                                        SystemUpdateDialog.LOCAL_UPDATE_HAVE,
                                        (li.get(i).get(MOUNT_PATH)));
                            } else {
                                // If the letter does not upgrade package
                                createDialog(350, 400,
                                        SystemUpdateDialog.LOCAL_UPDATE_NONE,
                                        null);
                            }
                        }
                    });
            mWidgetList.add(mSysLocUpdate);
        }
        mSysLocUpdate = new WidgetType();
        // set name
        mSysLocUpdate.setOnlySelectorName("");
        // set type for SystemLocalUpdate
        mSysLocUpdate.setType(WidgetType.TYPE_TEXTVIEW);
        mWidgetList.add(mSysLocUpdate);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
    }

    /**
     * create a dialog
     *
     * @param height
     * @param width
     * @param save
     * @param path
     */
    public void createDialog(int height, int width, int save, String path) {
        mSystemUpdateDialog = new SystemUpdateDialog(mContext, save, path);
        mSystemUpdateDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        Window window = mSystemUpdateDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = height;
        lp.width = width;
        window.setAttributes(lp);
        mSystemUpdateDialog.show();

    }

    /**
     * To determine whether a file exists
     *
     * @param path
     * @param fileName
     * @return
     */
    public boolean isFileExist(String path, String fileName) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "path=" + path + "fileName" + fileName);
        }
        File file = new File(path + fileName);
        if (Constant.LOG_TAG) {
            Log.d(TAG, "file=" + file);
        }
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * get the list of mount equipment
     *
     * @return
     */
    private List<Map<String, String>> getMountEquipmentList() {
        String[] mountType = mContext.getResources().getStringArray(
                R.array.mount_type);
        MountInfo info = new MountInfo(mContext);
        List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
        for (int j = 0; j < mountType.length; j++) {
            for (int i = 0; i < info.index; i++) {
                if (info.type[i] == j) {
                    if (info.path[i] != null && (info.path[i].contains("/mnt") || info.path[i].contains("/storage"))) {
                        Map<String, String> map = new HashMap<String, String>();
                        // map.put(MOUNT_DEV, info.dev[i]);
                        map.put(MOUNT_TYPE, String.valueOf(info.type[i]));
                        map.put(MOUNT_PATH, info.path[i]);
                        // map.put(MOUNT_LABLE, info.label[i]);
                        map.put(MOUNT_LABLE, "");
                        map.put(MOUNT_NAME, info.partition[i]);
                        childList.add(map);
                    }
                }
            }
        }
        return childList;
    }

    @Override
    public void dismissDialog() {
        // TODO Auto-generated method stub
        if (null != mSystemUpdateDialog && mSystemUpdateDialog.isShowing())
            mSystemUpdateDialog.dismiss();
    }

}
