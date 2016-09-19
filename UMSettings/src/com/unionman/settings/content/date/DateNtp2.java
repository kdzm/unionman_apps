package com.unionman.settings.content;

import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.StringUtils;
import com.unionman.settings.tools.Logger;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class DateNtp2 extends RightWindowBase
{
  private EditText mEditText;
  private UMLogger log;
  private static final String TAG="com.unionman.settings.content.date--DateNtp2--";
  View.OnKeyListener mEditKeyListener = new View.OnKeyListener()
  {
    public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if ((paramAnonymousKeyEvent.getAction() == 0) && (paramAnonymousInt == 4))
        if (((EditText)paramAnonymousView).getText().length() > 0);
      while ((paramAnonymousKeyEvent.getAction() != 1) || (paramAnonymousInt != 4))
      {
        return false;
      }
      StringUtils.delText((EditText)paramAnonymousView);
      return true;
    }
  };
  private Button btn_save;

  public DateNtp2(Context paramContext)
  {
    super(paramContext);
  }

  public void initData()
  {
    this.mEditText.setOnKeyListener(this.mEditKeyListener);
  }

  public void onInvisible()
  {
  }

  public void onResume()
  {
    Logger.i(TAG,"onResume()--");
    String str = Settings.Secure.getString(this.context.getContentResolver(), "ntp_server2");
    if (str == null)
      str = "";
    this.mEditText.setText(str);
    this.mEditText.requestFocus();
    this.mEditText.setSelection(str.length());
  }

  public void setId()
  {
    this.frameId = ConstantList.FRAME_DATE_NTP_2;
    this.levelId = 1002;
    this.log = UMLogger.getLogger(getClass());
  }

  public void setView()
  {
    Logger.i(TAG,"setView()--");
    this.layoutInflater.inflate(2130903051, this);
    this.btn_save = ((Button)findViewById(2131099712));
    this.btn_save.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Settings.Secure.putString(DateNtp2.this.context.getContentResolver(), "ntp_server2", DateNtp2.this.mEditText.getText().toString());
        DateNtp2.this.layoutManager.backShowView();
      }
    });
    this.mEditText = ((EditText)findViewById(2131099711));
  }
}
