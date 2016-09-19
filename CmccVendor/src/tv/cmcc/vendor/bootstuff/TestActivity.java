package tv.cmcc.vendor.bootstuff;

import android.app.Activity;
import android.os.Bundle;

import tv.cmcc.vendor.R;

/**
 * Created by Administrator on 2015/7/13.
 */
public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UpdateUtil.updateBootStuff(this, 15, 1);
    }
}
