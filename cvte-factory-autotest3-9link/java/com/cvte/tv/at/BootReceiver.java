package com.cvte.tv.at;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cvte.tv.at.api.SysProp;
import com.cvte.tv.at.util.CVTEBURN;
import com.cvte.tv.at.util.FacBootEntity;
import com.cvte.tv.at.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Renlijia on 2014/10/23
 */
public class BootReceiver extends BroadcastReceiver {

    private Context mcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String dat = intent.getDataString();
        Utils.LOG("CVTE AT onReceive:" + action + " dat=" + dat);
        mcontext = context;
        if (action.equals(Utils.UMBOOT_COMPLETED)) {
            StartCVTEAT();
        }
    }

    private void StartCVTEAT() {
        String AT_Read = SysProp.get(Utils.Persist_RO_CVTE_AT_READ, "0");
        Utils.LOG("CVTE AT_Read:" + AT_Read);
        if ("1".equals(AT_Read)) {
            if (!isFactoryATTest())
                startActivity(Utils.ACT_ATShow);
        }
    }

    private boolean isBootfile_exist(String Path) {
        if (Path.equals(""))
            return false;

        File logexists = new File(Path);
        if (!logexists.exists()) {
            return false;
        }
        return true;
    }

    public String FindATFilePath() {
        File usbroot = new File(Utils.USB_DEVICES_PATH);
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            Utils.LOG("FindATFilePath usbitems.size = " + usbitems.length);
            for (int i = 0; i < usbitems.length; i++) {
                Utils.LOG("FindATFilePath getPath = " + usbitems[i].getPath() + "/" + Utils.AUTO_START_FILE_NAME);
                File ATFile = new File(usbitems[i].getPath() + "/" + Utils.AUTO_START_FILE_NAME);
                if (ATFile.exists())
                    return ATFile.getPath();
            }
        }
        return "";
    }


    public FacBootEntity decodeBootFile(String path) {
        BufferedReader bufferedReader = null;
        String line = null;
        FacBootEntity facBootEntity = new FacBootEntity();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            facBootEntity = new FacBootEntity();
            do {
                String strs[] = null;

                line = bufferedReader.readLine().trim();

                if (line != null) {
                    // decode
                    strs = line.split(":");

                    if (strs[0].toUpperCase().equalsIgnoreCase("P")) {// Panel
                        facBootEntity.setP_BIT(Integer.parseInt(strs[1]));
                    } else if (strs[0].toUpperCase().equalsIgnoreCase("M")) {
                        facBootEntity.setM_BIT(Integer.parseInt(strs[1]));
                    } else if (strs[0].toUpperCase().equalsIgnoreCase("E")) {
                        facBootEntity.setE_BIT(Integer.parseInt(strs[1]));
                    } else if (strs[0].toUpperCase().equalsIgnoreCase("V")) {
                        facBootEntity.setV_BIT(Integer.parseInt(strs[1]));
                    } else if (strs[0].toUpperCase().equalsIgnoreCase("F")) {
                        facBootEntity.setF_BIT(Integer.parseInt(strs[1]));
                    } else if (strs[0].toUpperCase().equalsIgnoreCase("R")) {
                        facBootEntity.setR_BIT(Integer.parseInt(strs[1]));
                    } else if (strs[0].toUpperCase().equalsIgnoreCase("B")) {
                        facBootEntity.setB_BIT(Integer.parseInt(strs[1]));
                    } else if (strs[0].toUpperCase().equalsIgnoreCase("T")) {
                        facBootEntity.setT_BIT(Integer.parseInt(strs[1]));
                    }
                }

                if (line != null && line.toUpperCase().equals("Z")) {
                    break;
                }
            } while (line != null);
        } catch (IOException e) {

        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
        }
        return facBootEntity;
    }

    private void startActivity(String startpath) {
        Intent intent = new Intent(startpath);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mcontext.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
        }
    }


    public boolean isFactoryATTest() {

        ActivityManager manager = (ActivityManager) mcontext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infoList = manager.getRunningTasks(2);
        if (infoList.size() > 0) {
            ActivityManager.RunningTaskInfo info = infoList.get(0);
            String packageName = info.topActivity.getPackageName();
            String className = info.topActivity.getClassName();

            //must by AT Activity
            if (packageName.equals(Utils.ATPackageName) &&
                    className.equals(Utils.ATActivityName)) {
                Utils.LOG("isFactoryATTest = TRUE");
                return true;
            }
        }
        return false;
    }

    private boolean DetectBurnFile(FacBootEntity bootfile) {
        if ((bootfile.getB_BIT() == 1) && (bootfile.getF_BIT() == 1)) {
            //Start AgingMode
            if (CVTEBURN.exist())
                return false;
            else
                startActivity(Utils.ACT_AgingMode);
        } else if ((bootfile.getB_BIT() == 0) && (bootfile.getF_BIT() == 1)) {
            //Start CVTE AT Test Mode
            Utils.LOG("<Cvte-AT> Leave AgingMode and Start AT");
            int leavetime = 0;
            if (CVTEBURN.exist()) {
                CVTEBURN.delete();
                leavetime = 1200;
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Utils.LOG("<Cvte-AT> Start AT");
                    startActivity(Utils.ACT_ATShow);
                }
            }, leavetime);
        }
        return false;
    }


}
