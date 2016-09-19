package com.um.launcher;

import android.app.Activity;
import android.os.Bundle;

public class MyMultiScreenActivity extends Activity{
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_multiscreen_activity);
	}
	
    public void doExit(){
        finish();
    }
}
