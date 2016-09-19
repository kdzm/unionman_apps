package com.unionman.settings.content;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Identity;
import java.util.Locale;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.UMLogger;
import com.unionman.settings.tools.Logger;

public class LanguagePick extends RightWindowBase
{
  private CheckRadioButton crb_en;
  private CheckRadioButton crb_zh;
  UMLogger mlog;
  private static final String TAG="com.unionman.settings.content.public--LanguageActivity--";

  public LanguagePick(Context paramContext)
  {
    super(paramContext);
  }

  public static void setLanguage(Locale locale) {
    Logger.i(TAG,"setLanguage()--");
      try {
          IActivityManager am = ActivityManagerNative.getDefault();
          Configuration config = am.getConfiguration();
          config.locale = locale;
          am.updateConfiguration(config);
          BackupManager.dataChanged("com.android.providers.settings");
      } catch (Exception e) {
    	  e.printStackTrace();
      }
  }
  
  private void setSystemLanguage(String paramString)
  {
    Logger.i(TAG,"setSystemLanguage()--");
    try
    {
      if (paramString.contains("-"));
      Locale localLocale;
      for (Object localObject1 = new Locale(paramString.split("-")[0], paramString.split("-")[1]); ; localObject1 = localLocale)
      {
        Class localClass1 = Class.forName("android.app.ActivityManagerNative");
        Object localObject2 = localClass1.getDeclaredMethod("getDefault", new Class[0]).invoke(localClass1, new Object[0]);
        Configuration localConfiguration = (Configuration)localObject2.getClass().getDeclaredMethod("getConfiguration", new Class[0]).invoke(localObject2, new Object[0]);
        Class localClass2 = Class.forName("android.content.res.Configuration");
        localClass2.getDeclaredField("locale").set(localConfiguration, localObject1);
        localClass2.getDeclaredField("userSetLocale").set(localConfiguration, Boolean.valueOf(true));
        localObject2.getClass().getDeclaredMethod("updateConfiguration", new Class[] { Configuration.class }).invoke(localObject2, new Object[] { localConfiguration });
        Class localClass3 = Class.forName("android.app.backup.BackupManager");
        localClass3.getDeclaredMethod("dataChanged", new Class[] { String.class }).invoke(localClass3, new Object[] { "com.android.providers.settings" });
        
        localLocale = new Locale(paramString);
		return;
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      this.mlog.e("========" + localException.getMessage());
    }
  }

  public void initData()
  {
  }

  public void onInvisible()
  {
  }

  public void onResume()
  {
    this.crb_en.requestFocus();
  }

  public void setId()
  {
    Logger.i(TAG,"setId()--");
    this.frameId = ConstantList.FRAME_LANGUAGE_SET;
    this.levelId = 1002;
    this.mlog = UMLogger.getLogger(getClass());
  }

  public void setView()
  {
    Logger.i(TAG,"setView()--");
    this.layoutInflater.inflate(R.layout.language_pick, this);
    this.crb_en = ((CheckRadioButton)findViewById(R.id.crb_language_en));
    this.crb_zh =  ((CheckRadioButton)findViewById(R.id.crb_language_zh));
    this.crb_zh.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton, boolean paramAnonymousBoolean)
      {
        LanguagePick.this.setSystemLanguage("zh-cn");
        LanguagePick.this.layoutManager.backShowView();
      }
    });
    this.crb_en.setOnCheckedChangeListener(new CheckRadioButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CheckRadioButton paramAnonymousCheckRadioButton, boolean paramAnonymousBoolean)
      {
        LanguagePick.this.setSystemLanguage("en-us");
        LanguagePick.this.layoutManager.backShowView();
      }
    });
  }
}