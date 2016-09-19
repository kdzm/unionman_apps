package com.unionman.settings.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.unionman.settings.tools.UMDebug;
public class MD5sum
{
  public static String md5sum(File paramFile)
  {
    DLBLog.d("");
    try
    {
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(paramFile));
      byte[] arrayOfByte = new byte[10240];
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      while (true)
      {
        int i = localBufferedInputStream.read(arrayOfByte);
        if (i == -1)
        {
          localBufferedInputStream.close();
          String str = StringTool.toHex(localMessageDigest.digest());
          DLBLog.d("md5=" + str);
          return str;
        }
        localMessageDigest.update(arrayOfByte, 0, i);
		UMDebug.umdebug_trace();
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      DLBLog.w(localFileNotFoundException);
      return null;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      DLBLog.w(localNoSuchAlgorithmException);
      return null;
    }
    catch (IOException localIOException)
    {
      DLBLog.w(localIOException);
      return null;
    }
    finally
    {
      DLBLog.d("md5=" + null);
    }
  }

  public static String md5sum(String paramString)
  {
    return md5sum(new File(paramString));
  }

  public static byte[] md5sum(byte[] paramArrayOfByte)
    throws Exception
  {
    return MessageDigest.getInstance("MD5").digest(paramArrayOfByte);
  }
}
