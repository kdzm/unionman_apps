package com.um.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.unionman.jazzlib.*;
import android.util.Log;

import com.um.controller.ParamSave;
import com.um.dvb.R;

/**
 * Created by Administrator on 14-4-16.
 */
public class DvbMonitorAlertDialog extends Activity {
    private int ver = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dvbdialog);

        Intent intent = getIntent();
        ver = intent.getIntExtra("version", 0);

        SystemProperties.set("runtime.um.monitor.searchflag", "0");
        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle(R.string.altert_dialog_title).
                setMessage(R.string.nit_version_change_tips).
                //setIcon(R.drawable.ic_launcher).
                setPositiveButton(R.string.dvt_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).

                setNegativeButton(R.string.dvt_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int nitver = 0;
                        int fre = ParamSave.GetMainFreq();
                        int symbl = 6875;
                        int qam = 3;
                        int type = 0;
                        nitver = ver;

                        //SystemProperties.set("runtime.um.monitor.nitver", "3");
                        Log.v("DvbMonitorAlertDialog", "wsl#####yes save nitver=" + nitver);
                        ComponentName componentName = new ComponentName("com.um.dvbsearch", "com.um.ui.Search");
                        Intent it = new Intent();
                        it.setComponent(componentName);
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        it.setAction("com.um.dvbsearch.START_SEARCH");

                        Bundle bundle = new Bundle();
                        bundle.putInt("type", type);
                        bundle.putInt("fre", fre);
                        bundle.putInt("sym", symbl);
                        bundle.putInt("qam", qam);
                        it.putExtras(bundle);

                        startActivity(it);
                        //
                        dialog.dismiss();
                        finish();
                    }
                }).create();

        alertDialog.show();
    }


}
