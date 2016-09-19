package com.source;

import com.source.widget.SourceSelectLayout2;
import android.os.Bundle;
import android.app.Activity;

public class SourceSelect2Activity extends Activity {
     private String TAG = "SourceSelectActivity";
	 private SourceSelectLayout2 mAllLayout;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_source_select2);
		initView();
	}
	
	 private void initView() {
		 mAllLayout = (SourceSelectLayout2) findViewById(R.id.sourceSelectLayout2);
		 mAllLayout.setContextType(SourceSelectLayout2.Context_ACTIVITY_TYPE);
	 }
    
    public void doExit(){
        finish();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	mAllLayout.setExitFlag();
    }
}
