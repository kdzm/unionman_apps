package tv.cmcc.vendor.bootstuff;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

//import com.hisilicon.android.hisysmanager.HiSysManager;

import java.io.File;

import tv.cmcc.vendor.utils.FileUtils;
import tv.cmcc.vendor.utils.PreferencesUtils;

/**
 * Created by hjian on 2015/7/13.
 */
public class UpdateUtil {
    private static final String BOOT_VIDEO_PATH = Environment.getExternalStorageDirectory().getPath() + "/jscmccgg/sp";
    private static final String BOOT_ANIMATION_PATH = Environment.getExternalStorageDirectory().getPath() + "/jscmccgg/dt";
    private static final String BOOT_LOGO_PATH = Environment.getExternalStorageDirectory().getPath() + "/jscmccgg/jt";

    /**
     *
     * @param filePath like /mnt/sdcard/image.jpeg"
     * @return
     */
    public static boolean updateBootlogo(String filePath) {
    	//note for build passed
/*        File[] files = new File(BOOT_LOGO_PATH).listFiles();
        if (files != null && files.length > 0) {
            Log.d("hjian---", "updateBootlogo");
            HiSysManager sysmanager = new HiSysManager();
            boolean result = FileUtils.copyFile(files[0].getAbsolutePath(), "/data/local/logo.jpg");
            if(result){
            	FileUtils.changeFileMod("644", "/data/local/logo.jpg");
            }else{
            	return false;
            }
            return sysmanager.updateLogo("/data/local/logo.jpg") != -1;
        }*/

        return false;
    }

    public static boolean updateBootAnimation(String filePath) {
        File[] files = new File(BOOT_ANIMATION_PATH).listFiles();
        if (files != null && files.length > 0) {
            Log.d("hjian---", "updateBootAnimation");
            boolean result = FileUtils.copyFile(files[0].getAbsolutePath(), "/data/local/bootanimation.zip");
			FileUtils.changeFileMod("644", "/data/local/bootanimation.zip");
			return result;
        }

        return false;
    }

    public static boolean updateBootVideo(String filePath) {
        File[] files = new File(BOOT_VIDEO_PATH).listFiles();
        if (files != null && files.length > 0) {
            Log.d("hjian---", "updateBootVideo");
            boolean result = FileUtils.copyFile(files[0].getAbsolutePath(), "/data/local/boot.ts");
            FileUtils.changeFileMod("644", "/data/local/boot.ts");
			return result;
        }

        return false;
    }

    private static boolean setCurVersionId(Context context, long version) {
        return PreferencesUtils.putLong(context, "ad_cur_version", version);
    }

    private static long getCurVersionId(Context context, long defVal) {
        return PreferencesUtils.getLong(context, "ad_cur_version", defVal);
    }

    public static boolean updateBootStuff(Context context, int downloadId, long versionId) {
        if (getCurVersionId(context, 0l) >= versionId) {
            return false;
        }

        boolean ret = updateBootlogo(BOOT_LOGO_PATH);
        ret &= updateBootVideo(BOOT_VIDEO_PATH);
        ret &= updateBootAnimation(BOOT_ANIMATION_PATH);

        if (!ret) {
            return false;
        } else {
            setCurVersionId(context, versionId);
            return true;
        }
    }
}
