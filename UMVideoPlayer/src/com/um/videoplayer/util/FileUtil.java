package com.um.videoplayer.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import com.um.videoplayer.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.media.IMediaScannerListener;
import android.media.IMediaScannerService;
import android.media.MediaMetadataRetriever;
import android.media.MediaScanner;
import android.os.SystemProperties;

import com.hisilicon.android.tvapi.constant.EnumSoundMode;
import com.hisilicon.android.tvapi.constant.EnumPictureMode;
import com.hisilicon.android.tvapi.constant.EnumSoundTrack;

public class FileUtil {
    private static final String TAG = "VideoFileUtil";
    
    public static final boolean LOG_TAG = false;

	public String currentFilePath;
    private Context context;
    private AlertDialog dialog;
    private Uri onefile;
    
    public AlertDialog getDialog() {
		return dialog;
	}

	public void setDialog(AlertDialog dialog) {
		this.dialog = dialog;
	}

	public FileUtil( Context mContext){
    	context=mContext;
    }

    public FileInfo getFileInfo(File f) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(f.getName());
        

        if (f.isFile()) {
            Log.w("FILE", "FILE");
            fileInfo.setFileSize(fileSizeMsg(f));
            String end = f
                         .getName()
                         .substring(f.getName().lastIndexOf(".") + 1,
                                    f.getName().length()).toLowerCase();
            fileInfo.setFileType(end);
        }
        else {
            Log.w("FOLDER", "FOLDER");
            fileInfo.setFileSize("");
            fileInfo.setFileType("Folder");
        }

        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        String modify = simple.format(new Date(f.lastModified()));
        fileInfo.setLastModifyTime(modify);
        return fileInfo;
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
    
    public void showFileInfo(File f,String DolbyVideo,int vDuration,String mDolbyInfo,String resolution) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_back, null);
        TextView fileName = (TextView) view.findViewById(R.id.file_name);
        TextView fileDuration = (TextView) view.findViewById(R.id.file_duration);
        TextView fileSize = (TextView) view.findViewById(R.id.file_size);
        TextView fileVideo = (TextView) view.findViewById(R.id.file_video);
        TextView fileAudio = (TextView) view.findViewById(R.id.file_audio);
        TextView fileReso = (TextView) view.findViewById(R.id.file_reso);
        dialog = new AlertDialog.Builder(context,R.style.Dialog_undim).create(); 
		
    	dialog.show();
    	dialog.getWindow().setContentView(view);
        
        /*AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(R.string.file_info).setView(view)
                .setPositiveButton(R.string.close, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	dialog.dismiss();
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
        
        //String w = SystemProperties.get("persist.sys.reslutionWidth");
    	//String h = SystemProperties.get("persist.sys.reslutionHight");
    	//Log.i("hehe", "File_w:"+w+"h:"+h);
    	//String resolution=w+"×"+h;
        FileInfo info = getFileInfo(f);
        // populated with data
        
        fileName.setText(info.getFileName());
        fileDuration.setText(formatTime(vDuration));
        fileSize.setText(info.getFileSize());
        fileVideo.setText(DolbyVideo);
        /*if(mDolbyInfo.length()>3){
        	fileAudio.setText(mDolbyInfo.substring(3)) ;
        }else{
        	fileAudio.setText(mDolbyInfo);
        }*/

        fileAudio.setText(mDolbyInfo);
        fileReso.setText(resolution);
    }
    

    public class FileInfo{
    	private String fileName;
    	private String fileType;
    	private String fileSize;
    	private String lastModifyTime;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getFileType() {
			return fileType;
		}
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
		public String getFileSize() {
			return fileSize;
		}
		public void setFileSize(String fileSize) {
			this.fileSize = fileSize;
		}
		public String getLastModifyTime() {
			return lastModifyTime;
		}
		public void setLastModifyTime(String lastModifyTime) {
			this.lastModifyTime = lastModifyTime;
		}
    	
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
        { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite1_string },
        { EnumSoundMode.SNDMODE_SPORTS, R.string.sndmode_onsite2_string },
        { EnumSoundMode.SNDMODE_USER, R.string.sndmode_user_string } };
    
    public static int picture_mode[][] = {
        { EnumPictureMode.PICMODE_STANDARD,
                R.string.picmode_standard_string },
        { EnumPictureMode.PICMODE_DYNAMIC, R.string.picmode_dynamic_string },
        { EnumPictureMode.PICMODE_SOFTNESS,
                R.string.picmode_softness_string },
        { EnumPictureMode.PICMODE_USER, R.string.picmode_user_string } };
}
