package com.um.controller.frequentprog;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.um.dvbstack.Prog;
import com.um.dvbstack.ProviderProgManage;

/**
 * Created by hjan on 2014/4/19.
 */
public class FrequentCounter extends Service {

    private String TAG = "FrequentCounter";
    private int mProgId = 0;
    private int mDurationTemp = 0;
    private int mCurPlayDuration = 0;

    private int INTERVAL = 1000;
    private int MIN_VALID_TIME = 5;
    private int TIME_TO_SAVE = 10;

    private FrequentProgManager mFrequentProgManager;

    private Handler mCounterHandler = new Handler();
    private Runnable mCounterRunnable = new Runnable() {
        @Override
        public void run() {
            mCounterHandler.postDelayed(this, INTERVAL);
            ++mCurPlayDuration;
            ++mDurationTemp;

            // 大于一定间隔保存一次，不频繁操作数据库，也不至于程序异常而丢失数据
            if (mDurationTemp > TIME_TO_SAVE) {
                saveWatchDurationToDB(false);
            }
        }
    };

    @Override
    public void onCreate() {
        mFrequentProgManager = new FrequentProgManager(this);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "onBind", Toast.LENGTH_SHORT).show();
        return new CounterBinder();
    }

    @Override
    public void onDestroy() {
        mFrequentProgManager.closeDB();
        super.onDestroy();
    }

    /**
     * 开始为当前节目计时，如果上一个节目正在计时，则先停止、保存
     * @param progId
     */
    public void startCounter(int progId) {
        if (mProgId != 0) {
            stopCounter();
        }

//        mCurPlayDuration = 0;
//        mDurationTemp = 0;
        Toast.makeText(this, "startCounter", Toast.LENGTH_SHORT).show();
        mProgId = progId;
        mCounterHandler.removeCallbacks(mCounterRunnable);
        mCounterHandler.postDelayed(mCounterRunnable, INTERVAL);
    }

    public void stopCounter() {
        Toast.makeText(this, "stopCounter", Toast.LENGTH_SHORT).show();

        mCounterHandler.removeCallbacks(mCounterRunnable);

        // 小于最小有效时间的数据认为在切台，不保存；否则切台时会频繁操作数据库；
        // （最后一次的数据可能会丢失掉，但是可以不care）
        if (mCurPlayDuration > MIN_VALID_TIME) {
            saveWatchDurationToDB(true);
        }

        mCurPlayDuration = 0;
    }

    /**
     * 保存数据到数据库，更新或新增
     * @param needIncreaseTimes 是否增加观看次数
     */
    private void saveWatchDurationToDB(boolean needIncreaseTimes) {
        FrequentProg frequentProg = new FrequentProg();
        FrequentProg frequentProgInDB = getFrequentProgInDB();
        if (frequentProgInDB != null) {
            frequentProg.setProgId(mProgId);
            frequentProg.setProgName(getProgName(mProgId));
            frequentProg.setWatchDuration(frequentProgInDB.getWatchDuration() + mDurationTemp);
            frequentProg.setWatchTimes(frequentProgInDB.getWatchTimes() + (needIncreaseTimes ? 1 : 0));
//            mFrequentProgManager.update(frequentProg);
        } else {
            frequentProg.setProgId(mProgId);
            frequentProg.setProgName(getProgName(mProgId));
            frequentProg.setWatchDuration(mDurationTemp);
            frequentProg.setWatchTimes(needIncreaseTimes ? 1 : 0);
            mFrequentProgManager.add(frequentProg);
        }

        mDurationTemp = 0;
    }

    private String getProgName(int progId) {
        Prog prog = ProviderProgManage.GetInstance(this).getProgById(progId);
        if (prog != null) {
            return prog.getName();
        }
        return "";
    }

    private FrequentProg getFrequentProgInDB() {
        return mFrequentProgManager.get(mProgId);
    }

    public int getDurationTemp() {
        FrequentProg frequentProgInDB = getFrequentProgInDB();
        if (frequentProgInDB != null) {
            return frequentProgInDB.getWatchDuration();
        }

        return 0;
    }

    public class CounterBinder extends Binder {
        public FrequentCounter getService() {
            return FrequentCounter.this;
        }
    }
}
