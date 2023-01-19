package com.usa.tripjungle.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.usa.tripjungle.connect.AvsSpeakItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioPlayer {
    private MediaPlayer mMediaPlayer;
    private Context mContext;

    public AudioPlayer(Context context) {
        mContext = context;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    public void play(AvsSpeakItem playItem) {
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        //write out our raw audio data to a file
        File path = new File(mContext.getCacheDir(), System.currentTimeMillis() + ".mp3");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(playItem.getAudio());
            fos.close();
            mMediaPlayer.setDataSource(path.getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
