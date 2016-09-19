package com.um.console;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.os.SystemProperties;
import android.widget.Button;
import android.widget.TextView;

import com.um.console.R;

public class MainActivity extends Activity {
    private Button mbut;
    private TextView tv_zhuantai;
    private String flags;
    private String SysProfrty = "persist.sys.debugenable";
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        init();
        checkstatus();
        mButtonListener();
	}

    private void mButtonListener() {
        mbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flags.equals("0")) {
                    SystemProperties.set(SysProfrty, "1");
                }else if (flags.equals("1")) {
                    SystemProperties.set(SysProfrty, "0");
                } else {
                    System.out.println("dong "+SysProfrty+" = "+flags);
                }
                checkstatus_click();
            }
        });
    }

    private void checkstatus() {
        flags = SystemProperties.get(SysProfrty);
        if (flags.equals("1"))  {
            tv_zhuantai.setText("状态：已打开");
            mbut.setText("关闭");
        }else {
             tv_zhuantai.setText("状态：已关闭");
            mbut.setText("打开");
        }
    }
    private void checkstatus_click() {
        flags = SystemProperties.get(SysProfrty);
        if (flags.equals("1"))  {
            tv_zhuantai.setText("状态：已打开");
            mbut.setText("关闭");
        }else {
            tv_zhuantai.setText("状态：已关闭,要重启才有效哦");
            mbut.setText("打开");
        }
    }
    private void init() {
        mbut = (Button) findViewById(R.id.mbut);
        tv_zhuantai = (TextView) findViewById(R.id.mtv_zhuangtai);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
