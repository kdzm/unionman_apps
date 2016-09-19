package com.um.upgrade.base;

import android.app.Activity;

public class ExitAppUtil {

//    private List<Activity> mActivityList = new LinkedList<Activity>();
    private static ExitAppUtil instance = new ExitAppUtil();
    private ExitAppUtil(){};
    public static ExitAppUtil getInstance(){
        return instance;
    }
    public void addActivity(Activity activity){
//        mActivityList.add(activity);
        MyApp.addActivity(activity);
    }

    public void delActivity(Activity activity){
//        mActivityList.remove(activity);
        MyApp.delActivity(activity);
    }

    public void exit(){
//        for(Activity activity : mActivityList){
//            activity.finish();
//        }
        MyApp.exitAllAtivities();
//        System.exit(0);
    }
}