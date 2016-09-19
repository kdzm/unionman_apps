package tv.cmcc.vendor;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * com.hisilicon.android.gallery3d/com.hisilicon.android.gallery3d.app.Gallery
 * com.hisilicon.android.videoplayer/com.hisilicon.android.videoplayer.activity.VideoActivity
 *
 * Created by hjian on 2015/7/10.
 */
public class ActionTypeApp {
    private static Map<String, String> map = new HashMap<String, String>();
    static {
        map.put("Settings", "com.unionman.settings");
        map.put("Apps", "com.unionman.applist.app/com.unionman.applist.app.AllAppActivity");
//        map.put("FileBrowser", "com.unionman.filebrowser");
        map.put("FileBrowser", "com.unionman.filebrowser/com.unionman.filebrowser.MyMediaActivity");
        map.put("Gallery", "android.intent.action.localpicture");
        map.put("VideoPlayer", "android.intent.action.localvideo");
		map.put("MusicPlayer", "android.intent.action.localmusic");
//        map.put("Homemedia", "com.unionman.filebrowser");
		map.put("Homemedia", "com.unionman.filebrowser/com.unionman.filebrowser.MyMediaActivity");
    }

    public static Map<String, String> getActionTypeApps() {
        return map;
    }
}
