package com.um.controller.frequentprog;
import com.um.controller.frequentprog.FrequentProg;

/**
 * @author Trinea 2012-9-25
 */
interface FrequentCounterInterface {

     int getWatchDuration(int progId);
     FrequentProg getFrequentProg(int progId);
     List<FrequentProg> getTheMost(int sortType, int count, int mode);
     void reset();
}