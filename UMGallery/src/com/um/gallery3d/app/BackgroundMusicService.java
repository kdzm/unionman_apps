
package com.um.gallery3d.app;

import java.io.IOException;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hisilicon.android.mediaplayer.HiMediaPlayer;
import com.hisilicon.android.mediaplayer.HiMediaPlayer.OnCompletionListener;
import com.um.gallery3d.list.MusicModel;

public class BackgroundMusicService extends Service implements OnCompletionListener,
        HiMediaPlayer.OnPreparedListener {
    private final String TAG = "BackgroundMusicService";

    private static final boolean DEBUG = false;

    private HiMediaPlayer mediaPlayer;

    private String musicUrl = "";

    private static int playMusicIndex = 0;

    private List<MusicModel> musicLists = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(DEBUG, TAG, "onStart BackgroundMusicService");
        mediaPlayer = new HiMediaPlayer();
        // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        GalleryApp galleryApp = (GalleryApp) getApplication();
        musicLists = galleryApp.getMediaFileListService().getMusicList();

        if (musicLists != null && musicLists.size() > 0) {
            Log.d(DEBUG, TAG, "musicLists.size()" + musicLists.size());
            playUrl(0);
        }

        super.onStart(intent, startId);
    }

    public void playUrl(int playIndex) {
        Log.d(DEBUG, TAG, "playUrl playIndex=" + playIndex);

        if (musicLists.size() > 0) {
            musicUrl = musicLists.get(playIndex).getPath();

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicUrl);
                mediaPlayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean stopService(Intent name) {
        stop();
        return super.stopService(name);
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void onPrepared(HiMediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Log.d(DEBUG, TAG, "onPrepared");
    }

    @Override
    public void onCompletion(HiMediaPlayer arg0) {
        Log.d(DEBUG, TAG, "onCompletion playMusicIndex = " + playMusicIndex);
        playMusicIndex++;

        if (playMusicIndex >= musicLists.size()) {
            playMusicIndex = 0;
        }

        playUrl(playMusicIndex);
    }

}
