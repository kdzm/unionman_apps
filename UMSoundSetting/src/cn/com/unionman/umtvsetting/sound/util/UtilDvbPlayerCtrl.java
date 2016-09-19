package cn.com.unionman.umtvsetting.sound.util;

import android.util.Log;
import com.unionman.dvbplayer.IDvbPlayerService;
import android.os.IBinder;
import android.os.ServiceManager;

public class UtilDvbPlayerCtrl {
	private static final String TAG = "UtilDvbPlayerCtrl";
	
	private static IDvbPlayerService getDvbPlayerService() {
		IBinder binder = ServiceManager.getService("dvbplayer");
		
        if (binder == null) {
            Log.d(TAG, "binder is null");
            return null;
        }
        
        return IDvbPlayerService.Stub.asInterface(binder);  
	}
	
    public static int getAudioTrackCount() {
        Log.w(TAG, "getAudioTrackCount");
        try {
        	IDvbPlayerService service = getDvbPlayerService();
        	if (service != null) {
        		return service.getAudioTrackCount();
        	}
        } catch(Exception ex) {
            
        }
        
        return -1;
	}
	
    public static int getAudioCurTrack() {
        Log.w(TAG, "getAudioCurTrack");
        try {
        	IDvbPlayerService service = getDvbPlayerService();
        	if (service != null) {
        		return service.getAudioCurTrack();
        	}
        } catch(Exception ex) {
            
        }
        return -1;
	}
	
    public static int setAudioTrack(int index) {
        Log.w(TAG, "setSubLanguage");
        try {
        	IDvbPlayerService service = getDvbPlayerService();
        	if (service != null) {
        		return service.setAudioTrack(index);
        	}
        } catch(Exception ex) {
            
        }
        return -1;
	}
}
