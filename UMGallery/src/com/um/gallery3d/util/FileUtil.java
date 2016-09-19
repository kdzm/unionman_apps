package com.um.gallery3d.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.um.gallery3d.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

//import android.webkit.MimeTypeMap;

/**
 * file tool
 * @author qian_wei,ni_guanhua //Common operations to deal with a number of
 */
public class FileUtil {
    private static final String TAG = "GalleryFileUtil";

    public String currentFilePath;
    private Context context;
    private AlertDialog aDialog;
    
    public FileUtil( Context mContext){
    	context=mContext;
    }

    public FileInfo getFileInfo(File f) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(f.getName());
        
       // BufferedImage sourceImg =ImageIO.read(new FileInputStream(f));
        
        Bitmap  bitmapSize=BitmapFactory.decodeFile(f.getPath());
        
        fileInfo.setFilePicsize(bitmapSize.getWidth()+" * "+bitmapSize.getHeight());

        if (f.isFile()) {
            Log.w("FILE", "FILE");
            fileInfo.setFileSize(fileSizeMsg(f));
            String end = f
                         .getName()
                         .substring(f.getName().lastIndexOf(".") + 1,
                                    f.getName().length()).toLowerCase();
            fileInfo.setFiltType(end);
        }
        else {
            Log.w("FOLDER", "FOLDER");
            fileInfo.setFileSize("");
            fileInfo.setFiltType(context.getString(R.string.dir));
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
    
    public void showFileInfo(File f) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_back, null);
        TextView fileName = (TextView) view.findViewById(R.id.file_name);
        TextView fileType = (TextView) view.findViewById(R.id.file_type);
        TextView fileSize = (TextView) view.findViewById(R.id.file_size);
        TextView filePicsize = (TextView) view.findViewById(R.id.file_picsize);
        TextView fileModifyTime = (TextView) view
                                  .findViewById(R.id.file_modify);       
        
        aDialog = new AlertDialog.Builder(context,R.style.dialog).create(); 
		
        aDialog.show();
        aDialog.getWindow().setContentView(view);
        
        Handler handler = new Handler();  
        handler.postDelayed(new Runnable() {  
 
            public void run() {  
            	aDialog.dismiss(); 
            }  
        }, 30000);
        
        FileInfo info = getFileInfo(f);
        // populated with data
        fileName.setText(info.getFileName());
        fileType.setText(info.getFiltType());
        fileSize.setText(info.getFileSize());
        filePicsize.setText(info.getFilePicsize());
        fileModifyTime.setText(info.getLastModifyTime());
    }

	public AlertDialog getaDialog() {
		return aDialog;
	}

	public void setaDialog(AlertDialog aDialog) {
		this.aDialog = aDialog;
	}

}
