package com.um.controller.frequentprog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.um.dvbstack.ProgManage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjan on 2014/4/19.
 */
public class FrequentProgProvider extends Service {

    private FrequentCounterInterface.Stub myBinder = new FrequentCounterInterface.Stub() {
        @Override
        public int getWatchDuration(int progId) throws RemoteException {
            return getLastWatchDurationInDB(progId);
        }

        @Override
        public FrequentProg getFrequentProg(int progId) throws RemoteException {
            return getFrequentProgInDB(progId);
        }

        @Override
        public List<FrequentProg> getTheMost(int sortType, int count, int mode) throws RemoteException {
            ArrayList<FrequentProg> frequentProgs = new FrequentProgManager(
                    FrequentProgProvider.this).getAll(sortType, ProgManage.TVPROG);
            if (frequentProgs != null) {
                if (frequentProgs.size() <= count) {
                    return frequentProgs;
                } else {
                    return frequentProgs.subList(0, count);
                }
            }
            return null;
        }
        
        @Override
        public void reset() {
        	new FrequentProgManager(FrequentProgProvider.this).deleteAll();
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
//        Toast.makeText(this, "Service onBind", Toast.LENGTH_SHORT).show();
        return myBinder;
    }

    private int getLastWatchDurationInDB(int progId) {
        FrequentProg frequentProg = getFrequentProgInDB(progId);
        if (frequentProg != null) {
            return frequentProg.getWatchDuration();
        }
        return 0;
    }

    private FrequentProg getFrequentProgInDB(int progId) {
        return new FrequentProgManager(this).get(progId);
    }
}
