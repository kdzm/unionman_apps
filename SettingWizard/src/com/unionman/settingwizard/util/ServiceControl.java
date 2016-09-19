package com.unionman.settingwizard.util;

import android.os.Handler;

/**
 * Created by hjian on 2014/11/10.
 */
public class ServiceControl {
    private final String SERVICE_STOPPED = "stopped";
    private final String SERVICE_RUNNING = "running";
    private final int CHECK_INTERVAL = 1000;

    private String serviceName;
    private OnServiceStopListener onServiceStopListener;
    private OnServiceStartListener onServiceStartListener;
    private Handler handler = new Handler();
    private Runnable checkStopRunnable = new Runnable() {
        @Override
        public void run() {
            waitServiceStopped();
        }
    };
    private Runnable checkStartRunnable = new Runnable() {
        @Override
        public void run() {
            waitServiceStart();
        }
    };

    public ServiceControl(String serviceName) {
        this.serviceName = serviceName;
    }

    public void stopService(OnServiceStopListener onServiceStopListener) {
        this.onServiceStopListener = onServiceStopListener;
        String command = "setprop ctl.stop " + serviceName;
        ShellUtils.execCommand(command, false);
        handler.postDelayed(checkStopRunnable, CHECK_INTERVAL);
    }

    public void startService(OnServiceStartListener onServiceStartListener) {
        this.onServiceStartListener = onServiceStartListener;
        String command = "setprop ctl.start " + serviceName;
        ShellUtils.execCommand(command, false);
        handler.postDelayed(checkStartRunnable, CHECK_INTERVAL);
    }

    public void stopServiceByProperty(String property, OnServiceStopListener onServiceStopListener) {
        this.onServiceStopListener = onServiceStopListener;
        PropertyUtils.setInt(property, 0);
        handler.postDelayed(checkStopRunnable, CHECK_INTERVAL);
    }

    public void startServiceByProperty(String property, OnServiceStartListener onServiceStartListener) {
        this.onServiceStartListener = onServiceStartListener;
        PropertyUtils.setInt(property, 1);
        handler.postDelayed(checkStartRunnable, CHECK_INTERVAL);
    }

    private void waitServiceStopped() {
        String status = getServiceStatus();
        if (SERVICE_STOPPED.equals(status)) {
            if (onServiceStopListener != null) {
                onServiceStopListener.onServiceStop();
            }
        } else {
            handler.postDelayed(checkStopRunnable, CHECK_INTERVAL);
        }
    }

    private void waitServiceStart() {
        String status = getServiceStatus();
        if (SERVICE_RUNNING.equals(status)) {
            if (onServiceStartListener != null) {
                onServiceStartListener.onServiceStart();
            }
        } else {
            handler.postDelayed(checkStartRunnable, CHECK_INTERVAL);
        }
    }

    private String getServiceStatus() {
        String key = "init.svc." + serviceName;
        return PropertyUtils.getString(key, "");
    }

    public interface OnServiceStopListener{
        public void onServiceStop();
    }

    public interface OnServiceStartListener{
        public void onServiceStart();
    }
}
