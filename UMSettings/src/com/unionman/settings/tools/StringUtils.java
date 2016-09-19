package com.unionman.settings.tools;

import android.text.Editable;
import android.widget.EditText;

public class StringUtils
{
  public static void addIndex(EditText paramEditText)
  {
    int i = paramEditText.getSelectionStart();
    if (i == paramEditText.getText().toString().length())
      return;
    paramEditText.setSelection(i + 1);
  }

  public static void addText(EditText paramEditText, String paramString)
  {
    int i = paramEditText.getSelectionStart();
    paramEditText.getEditableText().insert(i, paramString);
  }

  public static void decIndex(EditText paramEditText)
  {
    int i = paramEditText.getSelectionStart();
    if (i == 0)
      return;
    paramEditText.setSelection(i - 1);
  }

  public static void delText(EditText paramEditText)
  {
    int i = paramEditText.getSelectionStart();
    Editable localEditable = paramEditText.getEditableText();
    if (i == 0)
      return;
    localEditable.delete(i - 1, i);
  }
}