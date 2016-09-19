package com.unionman.settings.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class CustomDialog extends Dialog
{
  private static int default_height = 120;
  private static int default_width = 160;

  public CustomDialog(Context paramContext, int paramInt1, int paramInt2)
  {
    this(paramContext, default_width, default_height, paramInt1, paramInt2);
  }

  public CustomDialog(Context paramContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super(paramContext, paramInt4);
    setContentView(paramInt3);
    Window localWindow = getWindow();
    WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
    localLayoutParams.width = paramInt1;
    localLayoutParams.height = paramInt2;
    localLayoutParams.gravity = 17;
    localWindow.setAttributes(localLayoutParams);
  }
}
