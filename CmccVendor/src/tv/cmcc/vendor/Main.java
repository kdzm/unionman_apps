package tv.cmcc.vendor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by hjian on 2015/7/10.
 *
 * Intent intent = new Intent();
 * intent.setClassName("tv.cmcc.vendor", "tv.cmcc.vendor.Main");
 * intent.putExtra("actiontype", strActionType);
 * startActivity(intent);
 *
 * ps:
 * am start -n tv.cmcc.vendor/tv.cmcc.vendor.Main -e actiontype Settings
 *
 */

public class Main extends Activity{
    private static final String TAG = "Main";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String actiontype = intent.getStringExtra("actiontype");
        if (actiontype != null && !actiontype.isEmpty()) {
            String action = ActionTypeApp.getActionTypeApps().get(actiontype);
            if (action != null && !action.isEmpty()) {
                Intent actionIntent = getActionInten(this, action);
                if (actionIntent != null) {
                    if (actiontype.equals("VideoPlayer")) {
                        actionIntent.putExtra("path", "/mnt");
                    }
                    actionIntent.putExtra("actiontype", actiontype);
                    startActivity(actionIntent);
                } else {
                    Toast.makeText(this, R.string.app_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        }
        finish();
    }

    /**
     * @param action 支持两种方式：
     * 1、com.unionman.iptv/com.unionman.iptv.MyAppActivity,
     * 2、com.unionman.iptv
     *
     */
    private Intent getActionInten(Context context, String action) {
        Intent intent;
        PackageManager pkmanager = context.getPackageManager();
        if (action.contains("/")) {
            String[] names = action.split("/");
            if (names == null || names.length != 2) {
                return null;
            }
            intent = new Intent();
            ComponentName comp = new ComponentName(names[0], names[1]);
            intent.setComponent(comp);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else if(action.contains("android.intent.action")){
			intent=new Intent(action);
		}else{
            intent = pkmanager.getLaunchIntentForPackage(action);
        }
        return intent;
    }
}
