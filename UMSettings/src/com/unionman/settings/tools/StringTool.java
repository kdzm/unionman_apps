package com.unionman.settings.tools;

import java.io.ByteArrayOutputStream;

public class StringTool
{
  public static String null2Empty(String paramString)
  {
    if (paramString == null)
      paramString = "";
    return paramString;
  }

  public static final byte[] toByte(String paramString)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int j;
    for (int i = 0; ; i = j)
    {
      if (i >= paramString.length())
        return localByteArrayOutputStream.toByteArray();
      j = i + 2;
      localByteArrayOutputStream.write(0xFF & Integer.parseInt(paramString.substring(i, j), 16));
    }
  }

  public static final String toHex(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer(2 * paramArrayOfByte.length);
    for (int i = 0; ; i++)
    {
      if (i >= paramArrayOfByte.length)
        return localStringBuffer.toString().toUpperCase();
      if ((0xFF & paramArrayOfByte[i]) < 16)
        localStringBuffer.append("0");
      localStringBuffer.append(Long.toString(0xFF & paramArrayOfByte[i], 16));
    }
  }
}