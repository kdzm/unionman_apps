package com.um.ui;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.WindowManager;

import com.um.dvbstack.DVB;
import com.um.dvbstack.Event;
import com.um.dvbstack.Prog;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;
import com.unionman.dvbstorage.ProgStorageUtil;

import java.lang.Override;
import java.util.ArrayList;

/**
 * Created by Administrator on 14-3-28.
 */
public class EpgTimerProc extends Service{
    ArrayList<Event> timers = new ArrayList<Event>();
    public static final String KEY_EVENTS = "events";
    public static final String KEY_PROG_ID = "prog_id";
    EpgTimerThread epgTimerProcThread = new EpgTimerThread();
    int progid = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
    	// TODO Auto-generated method stub
    	super.onCreate();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if (!DVB.isServerAlive()) {
    		Log.i("EpgTimerProc", "onStartCommand:abort start EpgTimerProc");
    		stopSelf();
    		return START_NOT_STICKY;
    	}
        epgTimerProcThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        epgTimerProcThread.flag = false;
        super.onDestroy();
    }

    private Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<ProgInfo> progInfosList = null;
            ProgStorage ps = new ProgStorage(getContentResolver());
            ProgStorageUtil psu = new ProgStorageUtil(ps);
            progInfosList = psu.getProgsWithBook();

            super.handleMessage(msg);
            int what = msg.what;
            int i = msg.arg1;
            int j = msg.arg2;
            progid = progInfosList.get(i).getProgId();

            Log.v("EpgTimerProc", "wsl#####recive msg progid==!!" + progid + "what=" +what );
            if (what == 0)
            {
                epgTimerProcThread.flag = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(EpgTimerProc.this);
                builder.setNegativeButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //确定按钮事件
                        int id  = 0;
                        id = progid;
                        Log.v("epgtimerproc", "wsl#####play the book prog!!" + id  );

                        PackageManager manager = getPackageManager();
                        Intent intent = manager.getLaunchIntentForPackage("com.um.dvb");

                        intent.putExtra("prog_id", id);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.v("epgtimerproc", "wsl#####canncel the timer!!" );
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.setTitle("预约提醒");
                dialog.setMessage(progInfosList.get(i).bookList.get(j).getEventName() + " 即将播放，是否收看?");
                dialog.show();
            }
        }
    };


    class EpgTimerThread extends Thread
    {
        private boolean flag = true;
        @Override
        public void run()
        {
            ArrayList<ProgInfo> progInfosList = null;
            ProgStorage ps = new ProgStorage(getContentResolver());
            ProgStorageUtil psu = new ProgStorageUtil(ps);
            Prog prog = new Prog(DVB.getInstance());
            Prog.Epg_LocalTime localTime = new Prog.Epg_LocalTime();
            Time curTime = new Time();
            Time starttimer = new Time();

            while (true)
            {
                progInfosList = psu.getProgsWithBook();
                if(progInfosList != null)
                {
                    prog.prog_get_localtime(localTime);
                    curTime.set(localTime.sec, localTime.min, localTime.hour, localTime.day, localTime.month, localTime.year);

                    //Log.v("epgtimerproc", "wsl#####curtime:" + curTime.year + "-" +  curTime.month  + "-" +  curTime.monthDay + " " + curTime.hour + ":" + curTime.minute  + ":" + curTime.second);
                    //Log.v("epgtimerproc", "wsl#####progInfosList.size()=" + progInfosList.size()  );
                    for(int i = 0; i < progInfosList.size(); i++)
                    {
                        for(int j = 0; j < progInfosList.get(i).bookList.size(); j++)
                        {
                            starttimer = progInfosList.get(i).bookList.get(j).getStartTime();
                            if(compareTimer(curTime, starttimer) >= 0)
                            {
                                //定时时间到
                                Log.v("epgtimerproc", "wsl#####timer timeout now!!" );
                                Message msg = new Message();
                                msg.what = 0;
                                msg.arg1 = i;
                                msg.arg2 = j;//progInfosList.get(i).getProgId();
                                handler.sendMessage(msg);
                                progInfosList.get(i).bookList.remove(j);
                                ps.addProg(progInfosList.get(i), true);
                                break;
                            }
                        }
                    }
                }

                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
         }
    }



    private int compareTimer(Time timer1, Time timer2)
    {
//        Log.v("epgtimerproc", "wsl#######timer1:" + timer1.year + "-" +  timer1.month  + "-" +  timer1.monthDay + " " + timer1.hour + ":" + timer1.minute  + ":" + timer1.second);
        Log.v("epgtimerproc", "wsl#######timer2:" + timer2.year + "-" +  timer2.month  + "-" +  timer2.monthDay + " " + timer2.hour + ":" + timer2.minute  + ":" + timer2.second);
         if(timer1.year > timer2.year)
            return 1;
        else if(timer1.year < timer2.year)
            return -1;

        if(timer1.month > timer2.month)
            return 1;
        else if(timer1.month < timer2.month)
            return -1;

        if(timer1.monthDay > timer2.monthDay)
            return 1;
        else if(timer1.monthDay < timer2.monthDay)
            return -1;

        if(timer1.hour > timer2.hour)
            return 1;
        else if(timer1.hour < timer2.hour)
            return -1;

        if(timer1.minute > timer2.minute)
            return 1;
        else if(timer1.minute < timer2.minute)
            return -1;

        if (timer1.second > timer2.second)
            return 1;
        else if(timer1.second < timer2.second)
            return -1;

        return 0;
    }
}
