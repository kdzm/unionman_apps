package cn.com.unionman.umtvsetting.system.logic;

/**
 * logic of signal
 *
 * @author huyq
 *
 */
public class SignalLogic {
    public interface SignalListener {
        /**
         * is have signal or not
         *
         * @param isHaveSignal
         */
        public void onNoSignal(boolean isHaveSignal);

        /**
         * doing sometion when signal changed
         *
         * @param str
         */
        public void SignalChange(String str);
    }

}
