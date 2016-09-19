package com.um.music.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.um.music.MP3Info;
import com.um.music.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.provider.MediaStore;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.media.IMediaScannerListener;
import android.media.IMediaScannerService;
import android.media.MediaScanner;
import com.hisilicon.android.tvapi.constant.EnumSoundMode;
import com.hisilicon.android.tvapi.constant.EnumSoundTrack;


/**
 * @author
 */
@SuppressWarnings("unchecked")
public class FileUtil {
	public static final boolean LOG_TAG = false;
	private Context context;
	private AlertDialog dialog;
	
	public AlertDialog getDialog() {
		return dialog;
	}

	public void setDialog(AlertDialog dialog) {
		this.dialog = dialog;
	}

	public FileUtil( Context mContext){
    	context=mContext;
    }

    public static String padString(String s, int length) {
        return padString(s, ' ', length);
    }

    public static String padString(String s, char padChar, int length) {
        int slen, numPads = 0;

        if (s == null) {
            s = "";
            numPads = length;
        }
        else if ((slen = s.length()) > length) {
            s = s.substring(0, length);
        }
        else if (slen < length) {
            numPads = length - slen;
        }

        if (numPads == 0) {
            return s;
        }

        char[] c = new char[numPads];
        Arrays.fill(c, padChar);
        return s + new String(c);
    }

    public static String rightPadString(String s, int length) {
        return (rightPadString(s, ' ', length));
    }

    public static String rightPadString(String s, char padChar, int length) {
        int slen, numPads = 0;

        if (s == null) {
            s = "";
            numPads = length;
        }
        else if ((slen = s.length()) > length) {
            s = s.substring(length);
        }
        else if (slen < length) {
            numPads = length - slen;
        }

        if (numPads == 0) {
            return (s);
        }

        char[] c = new char[numPads];
        Arrays.fill(c, padChar);
        return new String(c) + s;
    }
    
    public static int getIndexFromArray(int mode, int[][] arrays) {
        int num = 0;
        if (false) {
            Log.i("getIndexFromArray", "getIndexFromArray");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (LOG_TAG) {
                Log.i("getIndexFromArray", "getIndexFromArray=" + i);
            }
            if (arrays[i][0] == mode) {
                num = i;
                return num;
            }
        }
        return num;
    }
    
    public static String fileSizeMsg(File f) {
        String show = "";
        // file length in bits
        // CNcomment: 文件长度以比特为单位
        long length = f.length();
        DecimalFormat format = new DecimalFormat("#0.0");

        if (length / 1024.0 / 1024 / 1024 >= 1) {
            show = format.format(length / 1024.0 / 1024 / 1024) + "GB";
        }
        else if (length / 1024.0 / 1024 >= 1) {
            show = format.format(length / 1024.0 / 1024) + "MB";
        }
        else if (length / 1024.0 >= 1) {
            show = format.format(length / 1024.0) + "KB";
        }
        else {
            show = length + "B";
        }

        return show;
    }
    
    
    public static int[][] track_mode = {
        { EnumSoundTrack.TRACK_STEREO, R.string.stereo },
        { EnumSoundTrack.TRACK_DOUBLE_MONO, R.string.joint_stereo },
        { EnumSoundTrack.TRACK_DOUBLE_LEFT, R.string.left_track },
        { EnumSoundTrack.TRACK_DOUBLE_RIGHT, R.string.right_track },
        { EnumSoundTrack.TRACK_EXCHANGE, R.string.stereo },
        { EnumSoundTrack.TRACK_ONLY_RIGHT, R.string.stereo },
        { EnumSoundTrack.TRACK_ONLY_LEFT, R.string.stereo } };
    
    public static int[][] sound_mode = {
        { EnumSoundMode.SNDMODE_STANDARD, R.string.sndmode_standard_string },
        { EnumSoundMode.SNDMODE_NEWS, R.string.sndmode_dialog_string },
        { EnumSoundMode.SNDMODE_MUSIC, R.string.sndmode_music_string },
        { EnumSoundMode.SNDMODE_MOVIE, R.string.sndmode_movie_string },

        { EnumSoundMode.SNDMODE_USER, R.string.sndmode_user_string } };
   /* { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite1_string },
    { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite2_string },*/
    public String formatTime(long time)
    {
        time = time/ 1000;
        String strHour = "" + (time/3600);
        String strMinute = "" + time%3600/60;
        String strSecond = "" + time%3600%60;
         
        strHour = strHour.length() < 2? "0" + strHour: strHour;
        strMinute = strMinute.length() < 2? "0" + strMinute: strMinute;
        strSecond = strSecond.length() < 2? "0" + strSecond: strSecond;
         
        String strRsult = "";
         
        if (!strHour.equals("00"))
        {
            strRsult += strHour + ":";
        }
         
        if (!strMinute.equals("00"))
        {
            strRsult += strMinute + ":";
        }
         
        strRsult += strSecond;
         
        return strRsult;
    }
    public void showFileInfo(File f) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_back, null);
        TextView file_name = (TextView) view.findViewById(R.id.file_name);
        TextView file_size = (TextView) view.findViewById(R.id.file_size);
        TextView file_type = (TextView) view.findViewById(R.id.file_type);
        TextView file_artist = (TextView) view.findViewById(R.id.file_artist);
        TextView file_album = (TextView) view.findViewById(R.id.file_album);
        TextView file_duration = (TextView) view.findViewById(R.id.file_duration);
        
        dialog = new AlertDialog.Builder(context,R.style.Dialog_undim).create(); 
		
    	dialog.show();
    	dialog.getWindow().setContentView(view);
        
        /*AlertDialog.Builder builder=new AlertDialog.Builder(context).setTitle(R.string.file_info).setView(view)
        .setPositiveButton(R.string.close, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        
        dialog = builder.create(); 
        dialog.show();*/
        Handler handler = new Handler();  
        handler.postDelayed(new Runnable() {  
 
            public void run() {  
            	dialog.dismiss(); 
            }  
        }, 30000);
        
        try {
            scanFile(f.toString(), null);
        } catch (Exception e) {
            Log.e("com.um.music.util.scanFile", "Exception scanning file", e);
        }

		 String title = "";
		 String size = "";
		 String type = "";
		 String artist = "";
		 String album = "";
		 Long alltime ;
		 String duration="";
		 String fullpath="";
		 
		 Cursor cursor = context.getContentResolver().query(
		 		    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
		 		    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		if (cursor.moveToFirst()) {  
		    do {  
		    	String path = null; 
		        path = cursor.getString(cursor  
		                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));  

		        if (path.equals(f.toString())) {
		        	title = cursor.getString(cursor  
		                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
		        	/*size  = cursor.getString(cursor  
		                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));*/
		        	size = fileSizeMsg(f);
		        	
		        	fullpath  = cursor.getString(cursor  
		                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
		        	type =fullpath.substring(fullpath.lastIndexOf(".")+1, fullpath.length()).toLowerCase();
		        	
		        	artist = cursor.getString(cursor  
		                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
		        	album = cursor.getString(cursor  
		                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
		        	alltime =cursor.getLong(cursor  
		                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
		        	duration=formatTime(alltime);
		            break;  
		        }  
		    } while (cursor.moveToNext());  
		}
        
		 file_name.setText(title);
         file_size.setText(size); 
         file_type.setText(type);
         file_artist.setText(artist);
         file_album.setText(album); 
         file_duration.setText(duration);
	
    }
    
    private Uri scanFile(String path, String mimeType) {
        MediaScanner scanner = createMediaScanner();
        try {
            // make sure the file path is in canonical form
            String canonicalPath = new File(path).getCanonicalPath();
            return scanner.scanSingleFile(canonicalPath, "external", mimeType);
        } catch (Exception e) {
            Log.e("com.um.music.util.scanFile", "bad path " + path + " in scanFile()", e);
            return null;
        }
    }
    
    
    private MediaScanner createMediaScanner() {
        MediaScanner scanner = new MediaScanner(context);
        Locale locale = context.getResources().getConfiguration().locale;
        if (locale != null) {
            String language = locale.getLanguage();
            String country = locale.getCountry();
            String localeString = null;
            if (language != null) {
                if (country != null) {
                    scanner.setLocale(language + "_" + country);
                } else {
                    scanner.setLocale(language);
                }
            }    
        }
        
        return scanner;
    }
}
