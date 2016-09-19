package com.um.upgrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.um.upgrade.base.BaseActivity;

public class CableUserManualUpgrade extends BaseActivity implements OnClickListener{
    private final String TAG = CableUserManualUpgrade.class.getSimpleName();
    private EditText mFreqEditText = null;
    private EditText mSymbolEditText = null;
    private EditText mPidEditTex = null;
    private Spinner mQamSpinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cable_user_manual_upgrade);

        mFreqEditText = (EditText) findViewById(R.id.freq_edit_text);
        mSymbolEditText = (EditText) findViewById(R.id.symbol_edit_text);
        mPidEditTex = (EditText) findViewById(R.id.pid_edit_text);
        mQamSpinner = (Spinner) findViewById(R.id.qam_spinner);
	    
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(   
	  	      this, R.array.qam_array, R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(R.layout.simple_spinner_item);
	    mQamSpinner.setAdapter(adapter);
        mFreqEditText.setText("419");
        mSymbolEditText.setText("6875");
        mPidEditTex.setText("1332");
	    mQamSpinner.setSelection(2);
      
        findViewById(R.id.cable_user_upgrade_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.cable_user_upgrade_button:
	    		if (mFreqEditText.getText().toString().equals("")||
	        		mSymbolEditText.getText().toString().equals("")||
	        		mPidEditTex.getText().toString().equals("")) {
	        		Toast.makeText(CableUserManualUpgrade.this, getString(R.string.information_not_complete), Toast.LENGTH_LONG).show();
	    			return;
	    		} 

	            Bundle bundle = new Bundle();
	            bundle.putInt("upgradeType", 2);
	            bundle.putInt("upgradeFreq", Integer.parseInt(mFreqEditText.getText().toString()));
	            bundle.putInt("upgradeSymbol", Integer.parseInt(mSymbolEditText.getText().toString()));
	            bundle.putInt("upgradePid", Integer.parseInt(mPidEditTex.getText().toString()));
	            bundle.putInt("upgradeQam", (int)mQamSpinner.getSelectedItemId()+1);
	            Intent intent = new Intent(CableUserManualUpgrade.this, CableUpgradeActivity.class);
	            intent.putExtras(bundle);
	            startActivity(intent);
			break;
		default:
			break;
		}
    }
}
