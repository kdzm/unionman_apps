/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.um.gallery3d.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;

import android.R.anim;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.um.gallery3d.R;
import com.hisilicon.android.sdkinvoke.HiSdkinvoke;
import com.um.gallery3d.common.Utils;
import com.um.gallery3d.data.DataManager;
import com.um.gallery3d.data.MediaItem;
import com.um.gallery3d.data.MediaSet;
import com.um.gallery3d.data.Path;
import com.um.gallery3d.list.Common;
import com.um.gallery3d.list.ImageFileList;
import com.um.gallery3d.list.ImageFileListService;
import com.um.gallery3d.picasasource.PicasaSource;
import com.um.gallery3d.ui.GLRoot;
import com.um.gallery3d.ui.GLView.PICTURE_STATE;
import com.um.gallery3d.util.GalleryUtils;
import java.io.InputStream;

public final class Gallery extends AbstractGalleryActivity implements OnCancelListener {
    public static final String EXTRA_SLIDESHOW = "slideshow";

    public static final String EXTRA_CROP = "crop";

    public static final String ACTION_REVIEW = "com.android.camera.action.REVIEW";

    public static final String KEY_GET_CONTENT = "get-content";

    public static final String KEY_GET_ALBUM = "get-album";

    public static final String KEY_TYPE_BITS = "type-bits";

    public static final String KEY_MEDIA_TYPES = "mediaTypes";

    private static final String TAG = "Gallery";

    private GalleryActionBar mActionBar;

    private Dialog mVersionCheckDialog;
    
    private String picpath;

    public static final String ACTION = "com.um.gallery3d.list.imageservice";

    private SettingDialog settingDialog = null;
    private ImageFileList imageFileList=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        String chipVersion = HiSdkinvoke.getChipVersion();
        Log.i(TAG, "chipVersion:" + chipVersion);

        if (chipVersion.equals("Unknown chip ID")) {
            finish();
        }

        mActionBar = new GalleryActionBar(this);
        GalleryUtils.isGLRootFocus = true;
        mGLRootView.setFocusable(true);
        mGLRootView.requestFocus();

        if (savedInstanceState != null) {
            getStateManager().restoreFromState(savedInstanceState);
        } else {
            initializeByIntent();
        }
        
      
    }

    private static final int REQUEST_PHOTO = 2;

    private void initializeByIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        boolean gifShow = false;
        picpath = intent.getStringExtra("curPath");
        
        boolean flag = intent.getBooleanExtra("flag", false);
        
        imageFileList = intent.getParcelableExtra("ImageFileList");
        if (imageFileList != null) {
            if (imageFileList.getId() == 1) {
                Intent service = new Intent(ACTION);
                conn = new MyServiceConnection();
                context.bindService(service, conn, Context.BIND_AUTO_CREATE);
            }

        }
        else {
 
        	Uri uri = intent.getData();
            getCurrPath = uri.toString();
        }
        
        if (GifUtils.isGifFile(this, intent.getData()))
        {
        	GifOpenHelper tmpgHelper;
            tmpgHelper = new GifOpenHelper();
            try {
	            InputStream is = getContentResolver().openInputStream(intent.getData());
	            tmpgHelper.read(is);
            }
            catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (tmpgHelper.getFrameCount() > 1)
            {
            	gifShow = true;
            }
        }
        
        if (Intent.ACTION_GET_CONTENT.equalsIgnoreCase(action)) {
            startGetContent(intent);
        } else if (Intent.ACTION_PICK.equalsIgnoreCase(action)) {
            // We do NOT really support the PICK intent. Handle it as
            // the GET_CONTENT. However, we need to translate the type
            // in the intent here.
            Log.w(TAG, "action PICK is not supported");
            String type = Utils.ensureNotNull(intent.getType());

            if (type.startsWith("vnd.android.cursor.dir/")) {
                if (type.endsWith("/image")) {
                    intent.setType("image/*");
                }

                if (type.endsWith("/video")) {
                    intent.setType("video/*");
                }
            }

            startGetContent(intent);
        } else if (Intent.ACTION_VIEW.equalsIgnoreCase(action)
                || ACTION_REVIEW.equalsIgnoreCase(action)
                || gifShow) {
            // DTS2012051401263 modify start
            int mediaType = intent.getIntExtra(KEY_MEDIA_TYPES, 0);
            Uri uri = intent.getData();

            if (mediaType != 0) {
                uri = uri.buildUpon()
                        .appendQueryParameter(KEY_MEDIA_TYPES, String.valueOf(mediaType)).build();
            }

            if (isMmsIntent() && isGifType() || GifUtils.isGifFile(this, uri)) {
                Intent intentGif = new Intent(Gallery.this, Gif.class);
                intentGif.setData(getIntent().getData());
                startActivity(intentGif);
                finish();
                return;
            } else {
                startViewAction(intent);
            }
        } else if (flag) {
            Bundle data = new Bundle();
            data.putInt(PhotoPage.KEY_INDEX_HINT, 0);
            data.putString(PhotoPage.KEY_MEDIA_SET_PATH, "/local/image/-10");
            data.putString("photoFilePath", intent.getData().getPath());
            getStateManager().startStateForResult(PhotoPage.class, REQUEST_PHOTO, data);
        } else {
            startDefaultPage();
        }
        
        
        
    }

    public void startDefaultPage() {
        PicasaSource.showSignInReminder(this);
        Bundle data = new Bundle();
        data.putString(AlbumSetPage.KEY_MEDIA_PATH,
                getDataManager().getTopSetPath(DataManager.INCLUDE_IMAGE));
        getStateManager().startState(AlbumSetPage.class, data);
        mVersionCheckDialog = PicasaSource.getVersionCheckDialog(this);

        if (mVersionCheckDialog != null) {
            mVersionCheckDialog.setOnCancelListener(this);
        }
    }

    private void startGetContent(Intent intent) {
        Bundle data = intent.getExtras() != null ? new Bundle(intent.getExtras()) : new Bundle();
        data.putBoolean(KEY_GET_CONTENT, true);
        int typeBits = GalleryUtils.determineTypeBits(this, intent);
        data.putInt(KEY_TYPE_BITS, typeBits);
        data.putString(AlbumSetPage.KEY_MEDIA_PATH, getDataManager().getTopSetPath(typeBits));
        getStateManager().setLaunchGalleryOnTop(true);
        getStateManager().startState(AlbumSetPage.class, data);
    }

    private boolean isMmsIntent() {
        final Intent intent = getIntent();
        final String mmsUri = "content://mms";
        Uri uri = intent.getData();

        if (uri == null) {
            return false;
        }

        return uri.toString().startsWith(mmsUri);
    }

    private boolean isGifType() {
        final Intent intent = getIntent();
        final String giftype = "image/gif";
        String type = intent.resolveType(Gallery.this);
        return giftype.equals(type);
    }

    private String getContentType(Intent intent) {
        String type = intent.getType();

        if (type != null) {
            return type;
        }

        Uri uri = intent.getData();

        try {
            return getContentResolver().getType(uri);
        } catch (Throwable t) {
            Log.w(TAG, "get type fail", t);
            return null;
        }
    }

    private void startViewAction(Intent intent) {
        Boolean slideshow = intent.getBooleanExtra(EXTRA_SLIDESHOW, false);
        getStateManager().setLaunchGalleryOnTop(true);

        if (slideshow) {
            getActionBar().hide();
            DataManager manager = getDataManager();
            Path path = manager.findPathByUri(intent.getData());

            if (path == null || manager.getMediaObject(path) instanceof MediaItem) {
                path = Path.fromString(manager.getTopSetPath(DataManager.INCLUDE_IMAGE));
            }

            Bundle data = new Bundle();
            data.putString(SlideshowPage.KEY_SET_PATH, path.toString());
            data.putBoolean(SlideshowPage.KEY_RANDOM_ORDER, true);
            data.putBoolean(SlideshowPage.KEY_REPEAT, true);
            getStateManager().startState(SlideshowPage.class, data);
        } else {
            Bundle data = new Bundle();
            DataManager dm = getDataManager();
            Uri uri = intent.getData();
            String contentType = getContentType(intent);

            if (contentType == null) {
                Toast.makeText(this, R.string.no_such_item, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (uri == null) {
                int typeBits = GalleryUtils.determineTypeBits(this, intent);
                data.putInt(KEY_TYPE_BITS, typeBits);
                data.putString(AlbumSetPage.KEY_MEDIA_PATH, getDataManager()
                        .getTopSetPath(typeBits));
                getStateManager().setLaunchGalleryOnTop(true);
                getStateManager().startState(AlbumSetPage.class, data);
            } else if (contentType.startsWith(ContentResolver.CURSOR_DIR_BASE_TYPE)) {
                int mediaType = intent.getIntExtra(KEY_MEDIA_TYPES, 0);

                if (mediaType != 0) {
                    uri = uri.buildUpon()
                            .appendQueryParameter(KEY_MEDIA_TYPES, String.valueOf(mediaType))
                            .build();
                }

                Path setPath = dm.findPathByUri(uri);
                MediaSet mediaSet = null;

                if (setPath != null) {
                    mediaSet = (MediaSet) dm.getMediaObject(setPath);
                }

                if (mediaSet != null) {
                    if (mediaSet.isLeafAlbum()) {
                        data.putString(AlbumPage.KEY_MEDIA_PATH, setPath.toString());
                        getStateManager().startState(AlbumPage.class, data);
                    } else {
                        data.putString(AlbumSetPage.KEY_MEDIA_PATH, setPath.toString());
                        getStateManager().startState(AlbumSetPage.class, data);
                    }
                } else {
                    startDefaultPage();
                }
            } else {
                Path itemPath = dm.findPathByUri(uri);
                Path albumPath = dm.getDefaultSetOf(itemPath);
                // TODO: Make this parameter public so other activities can
                // reference it.
                boolean singleItemOnly = intent.getBooleanExtra("SingleItemOnly", false);

                if (!singleItemOnly && albumPath != null) {
                    data.putString(PhotoPage.KEY_MEDIA_SET_PATH, albumPath.toString());
                }

                data.putString(PhotoPage.KEY_MEDIA_ITEM_PATH, itemPath.toString());
                getStateManager().startState(PhotoPage.class, data);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GLRoot root = getGLRoot();
        root.lockRenderThread();

        try {
            return getStateManager().itemSelected(item);
        } finally {
            root.unlockRenderThread();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GLRoot root = getGLRoot();
        root.lockRenderThread();

        try {
            getStateManager().destroy();
        } finally {
            root.unlockRenderThread();
        }
        Common.isLoadpos=false;
        stopService(new Intent(ACTION));
    }

    @Override
    protected void onResume() {
        Utils.assertTrue(getStateManager().getStateCount() > 0);
        super.onResume();

        if (mVersionCheckDialog != null) {
            mVersionCheckDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mVersionCheckDialog != null) {
            mVersionCheckDialog.dismiss();
        }
    }

    @Override
    public GalleryActionBar getGalleryActionBar() {
        return mActionBar;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (dialog == mVersionCheckDialog) {
            mVersionCheckDialog = null;
        }
    }

    // BEGIN:remote control keydown distributed to glRootView
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getGLRoot() != null) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !GalleryUtils.isGLRootFocus) {
                mGLRootView.setFocusable(true);
                mGLRootView.requestFocus();
                GalleryUtils.isGLRootFocus = true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
        
            if (mGLRootView.getState() != PICTURE_STATE.NONE) {
                return mGLRootView.dispatchKeyEvent(event);
            } else {
                GLRoot root = getGLRoot();
                root.lockRenderThread();

                try {
                    getStateManager().onBackPressed();
                } finally {
                    root.unlockRenderThread();
                }
                return true;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	
        	Log.i(TAG, "menupath:"+picpath);
        	Log.i(TAG, "Common.isLoadpos:"+Common.isLoadpos);
        	
        	if(imageFileListService!=null){
        		if(!Common.isLoadpos){
            		  Common.imgCurrPos = imageFileListService.getCurrPosition();
            		  Common.imgListSize =imageFileListService.getSize();
            		  Common.isLoadpos=true;
                  }
            	Log.i(TAG, "currpath============!null==::"+imageFileListService.getCurrPath());
            	
            	Log.i(TAG, "currpath============!size==::"+imageFileListService.getList().size());
            	
            	Log.i(TAG, "currpath============!getCurrPosition==::"+imageFileListService.getCurrPosition());
            	
            	Log.i(TAG, "imageFileListService=getCurrPath=============!null"+getCurrPath);
             	Log.i(TAG, "imageFileListService=getCurrPath=============!Common.imgCurrPos"+Common.imgCurrPos);
            	Log.i(TAG, "imageFileListService=getCurrPath=============!imgCurrPospath"+imageFileListService.getList().get(Common.imgCurrPos).getPath());
        	}else{
        		Log.i(TAG, "imageFileListService==============null");
        	}
        	
    	  
        	
            if (mGLRootView.getState() != PICTURE_STATE.NONE) {
                return true;
            }
            if (settingDialog == null) {
                settingDialog = new SettingDialog(this, mGLRootView);
                settingDialog.setPicpath(URLDecoder.decode(imageFileListService.getList().get(Common.imgCurrPos).getPath().substring(7)));
                settingDialog.show();
            } else {
                if (settingDialog.isShowing()) {
                    settingDialog.dismiss();
                } else {
                    if (mGLRootView.getState() == PICTURE_STATE.NONE) {
                    	settingDialog.setPicpath(URLDecoder.decode(imageFileListService.getList().get(Common.imgCurrPos).getPath().substring(7)));
                        settingDialog.show();
                    }
                }
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // send the back event to the top sub-state
        mGLRootView.setState(PICTURE_STATE.NONE);
        GLRoot root = getGLRoot();
        root.lockRenderThread();

        try {
            getStateManager().onBackPressed();
        } finally {
            root.unlockRenderThread();
        }
    }
    
    public static ImageFileListService imageFileListService = null;
    public MyServiceConnection conn = null;
    private Context context   = Gallery.this;
    private String getCurrPath;
    public class MyServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            imageFileListService = ((ImageFileListService.MyBinder)service).getService();

            if (imageFileListService != null && !com.um.gallery3d.list.Common.isLoadSuccess()) {
                imageFileListService.setThreadStart();
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {}
    }
}
