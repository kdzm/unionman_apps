package com.unionman.netsetup.view.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.unionman.netsetup.R;
import com.unionman.netsetup.interfaces.WifiAdmin;

class WifiDialog extends AlertDialog
{
    static final int BUTTON_SUBMIT = DialogInterface.BUTTON_POSITIVE;
    static final int BUTTON_FORGET = DialogInterface.BUTTON_NEUTRAL;
	private static final String TAG = "WifiDialog";

    private final boolean mEdit;
    private final DialogInterface.OnClickListener mListener;
    private final AccessPoint mAccessPoint;

    private Context mContext;
    private View mView;
    private WifiConfigController mController;

    public WifiDialog(Context context, DialogInterface.OnClickListener listener,
            AccessPoint accessPoint, boolean edit) {
        super(context,R.style.CustomAlertDialogBackground);
//    	  super(context);
        mContext = context;
        mEdit = edit;
        mListener = listener;
        mAccessPoint = accessPoint;
        
    }

    public WifiConfigController getController() {
        return mController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.wifi_dialog, null);
        setView(mView);       

        mController = new WifiConfigController(this, mView, mAccessPoint, mEdit);

        super.onCreate(savedInstanceState);
        /* During creation, the submit button can be unavailable to determine
         * visibility. Right after creation, update button visibility */
        mController.enableSubmitIfAppropriate();
    }

    public Button getSubmitButton() {
    	
    	 return getButton(BUTTON_SUBMIT);

    }

    public Button getForgetButton() {
        return getButton(BUTTON_FORGET);
    }

    public Button getCancelButton() {
        return getButton(BUTTON_NEGATIVE);
    }

    public void setSubmitButton(CharSequence text) {
        setButton(BUTTON_SUBMIT, text, mListener);
    }

    public void setForgetButton(CharSequence text) {
        setButton(BUTTON_FORGET, text, mListener);
    }

    public void setCancelButton(CharSequence text) {
        setButton(BUTTON_NEGATIVE, text, mListener);
    }

}
