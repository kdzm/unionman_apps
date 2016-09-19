package com.um.music.util;

import com.um.music.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;


public class CustomToast extends Dialog implements Runnable
{
	private Context mContext;
	private Dialog mDialog;

	private TextView mTipsText;
	public TextView getmTipsText() {
		return mTipsText;
	}

	public void setmTipsText(TextView mTipsText) {
		this.mTipsText = mTipsText;
	}

	private int mShowTime;
	
	private boolean backDismiss=false;
	
	public boolean isBackDismiss() {
		return backDismiss;
	}

	public void setBackDismiss(boolean backDismiss) {
		this.backDismiss = backDismiss;
	}

	public Dialog getmDialog() {
		return mDialog;
	}

	public void setmDialog(Dialog mDialog) {
		this.mDialog = mDialog;
	}

    public CustomToast(Context context)
    {
        super(context,R.style.dialog);
        this.mContext = context;
        mDialog = new Dialog(mContext,R.style.dialog);
        mDialog.setOnKeyListener(new OnKeyListener() 
        {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
			{
				if(event.getAction() == KeyEvent.ACTION_DOWN)
				{
					switch(keyCode)
					{
					case KeyEvent.KEYCODE_BACK:
						dismiss();
						backDismiss=true;
						break;
					}
				}
				return false;
			}
		});
        initView();
    }
    
    private void initView() 
	{
    	mDialog.setContentView(R.layout.custom_toast);
		mTipsText = (TextView) mDialog.findViewById(R.id.toastText);
	}

	
	@Override
	public void run() 
	{
		while(true)
		{
			try
			{
				Thread.sleep(mShowTime);//睡眠时间
				sendMessage(0);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void sendMessage(int msg)
	{
		Message message = new Message();
        message.what = msg;
        mHandler.sendMessage(message);
	}
	
	private Handler mHandler = new Handler()
	{
	    public void handleMessage(Message msg) 
	    {
	    	switch(msg.what)
	    	{
	    	case 0:
    			dismiss();
    			backDismiss=false;
	    		break;
	    	}
	        super.handleMessage(msg);
	    }
	};
	
	public void setMessage(int msg)
    {
		mTipsText.setText(msg);
    }
	
	public void setMessage(CharSequence msg)
    {
		mTipsText.setText(msg);
    }
	
	public void showTime(int time)
	{
		mShowTime = time;
	}
	
    public void show()
    {
    	mDialog.show();
    	new Thread(this).start();
    }
    
    public void dismiss()
    {
    	if(mDialog.isShowing())
    	{
    		mDialog.dismiss();
    	}
    }
}

