package com.unionman.settings.content;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.Logger;

public class LanguageActivity extends RightWindowBase
{
  private LinearLayout lay_content;
  private List<CheckRadioButton> crbs;
  private LinearLayout lay_div;
  private CheckRadioButton crb_input;
  private CheckRadioButton crb_language;
  UMLogger log;
  private boolean mHaveHardKeyboard;
  private List<InputMethodInfo> mInputMethodProperties;
  private String strLastInputMethodId;
  private static final String TAG="com.unionman.settings.content.public--LanguageActivity--";

  CheckRadioButton.OnCheckedChangeListener mOnCheckedChangeListener = new CheckRadioButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton, boolean paramAnonymousBoolean)
    {
      Iterator localIterator = null;
      if (paramAnonymousBoolean)
        localIterator = LanguageActivity.this.crbs.iterator();
	  else
	  	return ;
      while (true)
      {
     	 UMDebug.umdebug_trace();
        if (!localIterator.hasNext())
        {
          paramAnonymousCheckRadioButton.setCheckedState(true);
          Settings.Secure.putString(LanguageActivity.this.context.getContentResolver(), "default_input_method", (String)paramAnonymousCheckRadioButton.getTag());
          return;
        }
        ((CheckRadioButton)localIterator.next()).setCheckedState(false);
		UMDebug.umdebug_trace();
      }
    }
  };

  public LanguageActivity(Context paramContext)
  {
    super(paramContext);
  }

  private void onCreateIMM()
  {
    Logger.i(TAG,"onCreateIMM()--");
    this.mInputMethodProperties = ((InputMethodManager)this.context.getSystemService("input_method")).getInputMethodList();
    this.strLastInputMethodId = Settings.Secure.getString(this.context.getContentResolver(), "default_input_method");
    int i = -1;
    if (this.mInputMethodProperties == null)
    {
      i = 0;
      this.log.d("-----------------------N=" + i);
    }
    for (int j = 0; ; j++)
    {
      if (j >= i)
      {
       // return;
        i = this.mInputMethodProperties.size();
        break;
      }
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mInputMethodProperties.get(j);
      String str = localInputMethodInfo.getId();
      this.log.d("-----------------------prefKey=" + str + "        i=" + j);
      CharSequence localCharSequence = localInputMethodInfo.loadLabel(this.context.getPackageManager());
      if (this.mHaveHardKeyboard)
      {
        CheckRadioButton localCheckRadioButton = new CheckRadioButton(this.context);
        this.log.d("-----------------------crb=" + localCheckRadioButton + "        i=" + j);
        localCheckRadioButton.setNextFocusLeftId(2131099677);
        localCheckRadioButton.setCheckModel(0);
        if (j == i - 1)
        {
          localCheckRadioButton.setId(j + 1000);
          localCheckRadioButton.setNextFocusDownId(localCheckRadioButton.getId());
          this.log.d("XXXXXXXXXXXXXXXXXXXX");
        }
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, 54);
        this.lay_content.addView(localCheckRadioButton, localLayoutParams);
        this.crbs.add(localCheckRadioButton);
        if (j < i - 1)
          this.lay_content.addView(this.lay_div);
        localCheckRadioButton.setTag(str);
        localCheckRadioButton.setText1(localCharSequence.toString());
        if (str.toString().equals(this.strLastInputMethodId))
        {
          localCheckRadioButton.setCheckedState(true);
          this.crb_input = localCheckRadioButton;
        }
        localCheckRadioButton.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
      }
    }
  }

  public void initData()
  {
    Logger.i(TAG,"initData()--");
    if (getResources().getConfiguration().keyboard != 2);
    for (this.mHaveHardKeyboard = true; ; this.mHaveHardKeyboard = false)
    {
      onCreateIMM();
      return;
    }
  }

  public void onInvisible()
  {
  }

  public void onResume()
  {
    Logger.i(TAG,"onResume()--");
    Configuration localConfiguration = getResources().getConfiguration();
    String str1 = localConfiguration.locale.getDisplayName(localConfiguration.locale);
    if ((str1 != null) && (str1.length() > 1))
    {
      String str2 = Character.toUpperCase(str1.charAt(0)) + str1.substring(1);
      this.crb_language.setText2(str2);
    }
  }

  public void setId()
  {
    Logger.i(TAG,"setId()--");
    this.levelId = 1001;
    this.log = UMLogger.getLogger(getClass());
  }

  public void setView()
  {
    Logger.i(TAG,"setView()--");
    this.layoutInflater.inflate(R.layout.language, this);
    this.lay_content = ((LinearLayout)findViewById(R.id.content));
    this.lay_div = ((LinearLayout)this.layoutInflater.inflate(R.layout.divider, null));
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -2);
    this.lay_div.setLayoutParams(localLayoutParams);
    this.crbs = new ArrayList();
    this.crb_language = ((CheckRadioButton)findViewById(R.id.crb_language_set));
    this.crb_language.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton, boolean paramAnonymousBoolean)
      {
        try
        {
          LanguageActivity.this.layoutManager.showLayout(ConstantList.FRAME_LANGUAGE_SET);
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }
}
