package com.unionman.cityfeature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2014/11/6.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, FeatureInitService.class);
        startService(intent);

    }
}
