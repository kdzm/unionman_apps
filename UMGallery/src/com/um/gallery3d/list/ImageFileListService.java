
package com.um.gallery3d.list;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.um.gallery3d.util.Log;

public class ImageFileListService extends Service {
    private String TAG = "ImageFileListService";

    private IBinder binder = new MyBinder();

    private static final boolean DEBUG = false;

    private ImageModel _currImageModel = null;

    private boolean _haveGetPosition = false; // whether have get the position

    public String _currPath = null;

    private List<ImageModel> _list = null;

    private GetImageListThread _getImageListThread = null;

    private boolean _runFlag = false;

    private Object lock = new Object();

    public int _currPosition = 0; // current Music file's position

    public int getCurrPosition() {
        return _currPosition;
    }

    public void setCurrPosition(int _currPosition) {
        this._currPosition = _currPosition;
    }

    private List<MusicModel> _musicList = null;

    public boolean isHaveGetPosition() {
        synchronized (lock) {
            return this._haveGetPosition;
        }
    }

    public void setHaveGetPosition(boolean haveGetPosition) {
        synchronized (lock) {
            this._haveGetPosition = haveGetPosition;
        }
    }

    public String getCurrPath() {
        synchronized (lock) {
            return this._currPath;
        }
    }

    public void setCurrPath(String currPath) {
        synchronized (lock) {
            this._currPath = currPath;
        }
    }

    public List<MusicModel> getMusicList() {
        synchronized (lock) {
            return this._musicList;
        }
    }

    public void setMusicList(ArrayList<MusicModel> musicList) {
        synchronized (lock) {
            this._musicList = musicList;
        }
    }

    public List<ImageModel> getList() {
        synchronized (lock) {
            return this._list;
        }
    }

    public void setList(ArrayList<ImageModel> list) {
        synchronized (lock) {
            this._list = list;
        }
    }

    public GetImageListThread getThread() {
        synchronized (lock) {
            return this._getImageListThread;
        }
    }

    public void setThread(GetImageListThread t) {
        synchronized (lock) {
            this._getImageListThread = t;
        }
    }

    public boolean isRunFlag() {
        synchronized (lock) {
            return this._runFlag;
        }
    }

    public void setStopFlag(boolean runFlag) {
        synchronized (lock) {
            this._runFlag = runFlag;
        }
    }

    public ImageModel getCurrImageModel() {
        synchronized (lock) {
            return this._currImageModel;
        }
    }

    public void setCurrImageModel(ImageModel model) {
        synchronized (lock) {
            this._currImageModel = model;
        }
    }

    public class MyBinder extends Binder {
        public ImageFileListService getService() {
            return ImageFileListService.this;
        }
    }

    @Override
    public void onCreate() {
        FilterType.filterTypeImage(getApplicationContext());
        FilterType.filterTypeMusic(getApplicationContext());
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    String curMusicParentPath = "";
    public boolean accept(String filename) {
        Map<String, String> map = ((Map<String, String>) getSharedPreferences("IMAGE", Context.MODE_WORLD_READABLE).getAll());
        final Set<String> keys = map.keySet();

        for (String s : keys) {
            if (filename.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "ImageFileListService.onStart");
        super.onStart(intent, startId);

        if (getList() != null) {
            getList().clear();
        } else {
            setList(new ArrayList<ImageModel>());
        }

        if (getMusicList() != null) {
            getMusicList().clear();
        } else {
            setMusicList(new ArrayList<MusicModel>());
        }

        // setCurrPosition(0);
        setHaveGetPosition(false);

        try {
            Log.d(DEBUG, TAG, "intent=" + intent + "  intent.getData()=" + intent.getData());

            if (intent == null || intent.getData() == null) {
                return;
            }

            String curPath = intent.getData().getPath();
            String filename = curPath.toUpperCase();
            if( !accept(filename) ) {
                return;
            }
            /*
             * String curMusicPath = intent.getStringExtra("path"); Log.d(DEBUG,
             * TAG, "intent.getData().getPath()=" + curPath + " curMusicPath = "
             * + curMusicPath); if (curMusicPath.equals("")) { curMusicPath =
             * curPath; } File fileMusic = new File(curMusicPath); Log.d(DEBUG,
             * TAG, "fileMusic.isFile()=" + fileMusic.isFile() +
             * "  fileMusic.isDirectory()=" + fileMusic.isDirectory()); if
             * (fileMusic != null) { if (fileMusic.exists() &&
             * fileMusic.isFile()) { curMusicParentPath =
             * curMusicPath.substring(0, curMusicPath.lastIndexOf("/")); } else
             * if (fileMusic.isDirectory()) { curMusicParentPath = curMusicPath;
             * } }
             */
            setCurrPath(curPath);
            File f = new File(curPath);
            ImageModel currImage = new ImageModel();
            currImage.setTitle(f.getName());
            currImage.setAddedTime(f.lastModified());
            currImage.setPath(f.getPath());
            currImage.setSize(f.length());
            setCurrImageModel(currImage);
            String currPathParent = getCurrPath().substring(0, getCurrPath().lastIndexOf("/"));
            File file = new File(currPathParent);

            if (file.exists() && file.isDirectory()) {
                setStopFlag(true);
                waitThreadToIdle(getThread());
                setThread(new GetImageListThread(file));
                setStopFlag(false);
                getThread().start();
                Intent i = new Intent();
                Log.d(TAG, "intent.getData():" + intent.getData());
                i.setClassName("com.um.gallery3d",
                        "com.um.gallery3d.app.Gallery");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setDataAndType(intent.getData(), "image/*");
                i.putExtra("flag", true);
                i.putExtra("curPath", curPath);
                i.putExtra("ImageFileList", new FMImageFileList(getCurrPath(), f.getName()));
                ImageFileListService.this.startActivity(i);
            }
        } catch (Exception e) {
            Log.e(TAG, "onstart error :" + e);
            this.stopSelf(startId);
        }
    }

    /**
     * get all Image file from current folder
     */
    private class GetImageListThread extends Thread {
        private File file = null;

        public GetImageListThread(File file) {
            this.file = file;
        }

        @SuppressWarnings("deprecation")
        public void run() {
            Log.d(TAG, "GetImageListThread.run");
            Common.setLoadSuccess(false);
            ImageModel model = null;
            // MusicModel musicModel = null;

            // int musicIdIndex = 0;
            @SuppressWarnings("unchecked")
            Map<String, String> map = ((Map<String, String>) getSharedPreferences("IMAGE",
                    Context.MODE_WORLD_READABLE).getAll());
            final Set<String> keys = map.keySet();
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File file) {
                    // TODO Auto-generated method stub
                    String filename = file.getName().toUpperCase();
                    for (String s : keys) {
                        if (filename.endsWith(s)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            File[] files = file.listFiles(filter);
            HashMap<String, Integer> hashMap = new HashMap<String, Integer>();

            for (int i = 0; i < files.length; i++) {
                if (!isRunFlag()) {
                    String filename = files[i].getName();
                    model = new ImageModel();

                    hashMap.put(filename, Integer.valueOf(i));
                    model.setPath(Uri.fromFile(files[i]).toString());
                    model.setTitle(filename);
                    model.setSize(files[i].length());
                    model.setAddedTime(files[i].lastModified());
                    getList().add(model);
                } else {
                    break;
                }
            }
            Log.i(TAG, "loop over");
            Integer index = hashMap.get(new File(_currImageModel.getPath()).getName());
            setCurrPosition(index);
            _currImageModel.setId(index);
            _currImageModel.setPath(_currImageModel.getPath());
            /*
             * File fileMusicDir = new File(curMusicParentPath); if
             * (fileMusicDir != null && fileMusicDir.exists() &&
             * fileMusicDir.isDirectory()) { File[] fileMusics =
             * fileMusicDir.listFiles(); for (int j = 0; j < fileMusics.length;
             * j++) { if (!isRunFlag()) { if (fileMusics[j].isFile()) { String
             * filename = fileMusics[j].getName(); String dex =
             * filename.substring( filename.lastIndexOf(".") + 1,
             * filename.length()); dex = dex.toUpperCase(); SharedPreferences
             * musicShare = getSharedPreferences( "AUDIO",
             * Context.MODE_WORLD_READABLE); String musicSuffix =
             * musicShare.getString(dex, ""); if (!musicSuffix.equals("")) {
             * Log.d(DEBUG, TAG, "musicfilename=" + filename); musicModel = new
             * MusicModel(); musicModel.setPath(fileMusics[j].getPath());
             * musicModel.setTitle(filename);
             * musicModel.setSize(fileMusics[j].length());
             * musicModel.setAddedTime(fileMusics[j] .lastModified());
             * musicModel.setId(musicIdIndex); getMusicList().add(musicModel);
             * musicIdIndex++; Log.d(DEBUG, TAG, " musicIdIndex=" + musicIdIndex
             * + " fileMusics[j].getPath()=" + fileMusics[j].getPath()); } } } }
             * }
             */
            Common.setLoadSuccess(true);
            Log.i(TAG, "thread run over:" + getList().size());
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        setStopFlag(true);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * check if the thread thrd is busy
     *
     * @param thrd
     * @return
     */
    private static boolean threadBusy(Thread thrd) {
        if (thrd == null) {
            return false;
        }

        if ((thrd.getState() != Thread.State.TERMINATED) && (thrd.getState() != Thread.State.NEW)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * before recreate a new thread, be sure the old thread is idle
     *
     * @param thrd
     */
    private static void waitThreadToIdle(Thread thrd) {
        while (threadBusy(thrd)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getSize() {
        if (null == getList()) {
            return 0;
        } else {
            return getList().size();
        }
    }
    
    public void setThreadStart() {
        synchronized (lock) {
            try {
                if (this._getImageListThread != null)
                {
                   this._getImageListThread.start();
                }else{
                   ImageModel currPathParent = getList().get(0);
                   String filecurrPathParent = currPathParent.getPath().substring(0, currPathParent.getPath().lastIndexOf("/"));
                   File file = new File(filecurrPathParent);
                   if (file.exists() && file.isDirectory())
                   {
                     setCurrPath(currPathParent.getPath());
                     getList().clear();
                     this._getImageListThread = new GetImageListThread(file);
                     this._getImageListThread.start();
                   }
                  }
            }
            catch (IllegalThreadStateException ex) {
                Log.e(TAG, "setThreadStart error!" + ex);
            }
        }
    }

}
