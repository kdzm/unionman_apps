package com.um.launcher;

import java.util.ArrayList;

import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

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
