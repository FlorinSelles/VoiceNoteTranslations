package com.mac.voiceprocesing.interfaces;

import android.media.MediaPlayer;

/**
 * Created by florin on 1/28/2015.
 */
public interface iAudioPlayer {

    public void play();
    public void stop();
    public void pause();
    public Boolean isPlaying();
}
