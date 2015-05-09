package com.mac.voiceprocesing.customViews;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.helper.AlertDialogHelper;

import java.io.File;

/**
 * Created by florin on 2/1/2015.
 */
public class MediaPlayerControl extends LinearLayout {

    private ImageView mplayer_bar_btn;
    private ImageView mplayer_player_btn;
    private ImageView mplayer_stop_btn;
    private SeekBar mplayer_seekbar;

    private MediaPlayer mediaPlayer;
    private Boolean audioPause = false;
    private String audioFileFullPath = null;

    private MPSeekBarSinc mpSeekBarSinc;

    public MediaPlayerControl(Context context) {
        this(context, null);
    }

    public MediaPlayerControl(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.startMediaPlayer();

        LayoutInflater.from(context).inflate(R.layout.media_player_control, this);

        this.loadComponentFromLayout();

        this.createControlEvent();
    }

    public String getAudioFileFullPath() {
        return audioFileFullPath;
    }

    public void setAudioFileFullPath(String audioFileFullPath) {
        this.audioFileFullPath = audioFileFullPath;
        try {
            this.mediaPlayerInitialization();
        }catch (Exception e){
            String a = e.getMessage();

        }
    }

    public void play(){
        if (this.audioFileFullPath == null){
            AlertDialogHelper.show(getContext(), getContext().getResources().getString(R.string.app_name), getContext().getResources().getString(R.string.noAudioFileAsociated), "OK", null, true, null);
           return;
        }

        this.mplayer_player_btn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.mplayer_pause_btn));

        this.startSeekBarSyncronization();

        this.mediaPlayer.start();
        this.audioPause = false;
    }

    public void stop(Boolean restart){
        if (this.mpSeekBarSinc != null || this.mpSeekBarSinc.getStatus() == AsyncTask.Status.RUNNING){
            this.mpSeekBarSinc.setStopProcessStarted(true);
            this.mpSeekBarSinc.cancel(true);
        }

        if (this.mediaPlayer.isPlaying() || this.audioPause == true){
            this.mediaPlayer.stop();
            this.mediaPlayer.prepareAsync();
        }

        this.audioPause = false;

        this.mplayer_player_btn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.mplayer_player_btn));

        if (restart){
            this.initializeSeekBar();
        }
    }

    public void pause(){
        this.mediaPlayer.pause();
        this.audioPause = true;

        this.mplayer_player_btn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.mplayer_player_btn));
    }

    public Boolean isPlaying(){
        return this.mediaPlayer.isPlaying();
    }

    private void startMediaPlayer(){
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop(false);
            }
        });
    }

    private void loadComponentFromLayout(){
        this.mplayer_bar_btn = (ImageView) this.findViewById(R.id.mplayer_bar_btn);
        this.mplayer_player_btn = (ImageView) this.findViewById(R.id.mplayer_player_btn);
        this.mplayer_stop_btn = (ImageView) this.findViewById(R.id.mplayer_stop_btn);
        this.mplayer_seekbar = (SeekBar) this.findViewById(R.id.mplayer_seekbar);
    }

    private void createControlEvent(){
        this.mplayer_player_btn.setOnClickListener(new OnMediaPlayerPlay());
        this.mplayer_stop_btn.setOnClickListener(new OnMediaPlayerStop());
        this.mplayer_seekbar.setOnSeekBarChangeListener(new SeekBakChange());
    }

    private void mediaPlayerInitialization() throws Exception{
        Uri filePath = Uri.fromFile(new File(this.audioFileFullPath));
        this.mediaPlayer.setDataSource(getContext(), filePath);
        this.mediaPlayer.prepareAsync();
    }

    private void initializeSeekBar(){
        this.mplayer_seekbar.setMax(0);
        this.mplayer_seekbar.setProgress(0);
        this.mplayer_seekbar.refreshDrawableState();
    }

    private void startSeekBarSyncronization(){
        if (this.mpSeekBarSinc == null || this.mpSeekBarSinc.getStatus() == AsyncTask.Status.FINISHED){
            this.mpSeekBarSinc = new MPSeekBarSinc(this);
            this.mpSeekBarSinc.execute();
        }
    }

    private void onAudioMoveFoward(){
        if (this.mplayer_seekbar.getMax() != this.mediaPlayer.getDuration()){
            this.mplayer_seekbar.setMax(this.mediaPlayer.getDuration());
        }
        this.mplayer_seekbar.setProgress(this.mediaPlayer.getCurrentPosition());
    }

    private class OnMediaPlayerPlay implements OnClickListener{
        @Override
        public void onClick(View v) {
            if (audioFileFullPath == null){
                AlertDialogHelper.show(getContext(), getContext().getResources().getString(R.string.app_name), getContext().getResources().getString(R.string.noAudioFileAsociated), "OK", null, true, null);
                return;
            }
            if (isPlaying()){
                pause();
            }else{
                play();
            }
        }
    }

    private class OnMediaPlayerStop implements OnClickListener{
        @Override
        public void onClick(View v) {
            if (audioFileFullPath == null){
                AlertDialogHelper.show(getContext(), getContext().getResources().getString(R.string.app_name), getContext().getResources().getString(R.string.noAudioFileAsociated), "OK", null, true, null);
                return;
            }

            stop(true);
        }
    }

    private class MPSeekBarSinc extends AsyncTask<Void, Void, Void>{

        private MediaPlayerControl mediaPlayerControl;
        private Boolean stopProcessStarted;

        private MPSeekBarSinc(MediaPlayerControl mediaPlayerControl) {
            this.mediaPlayerControl = mediaPlayerControl;
        }

        @Override
        protected Void doInBackground(Void... params) {
            this.stopProcessStarted = false;

            while (true){
                if (this.isCancelled()){
                    return null;
                }

                if (this.stopProcessStarted == false){
                    this.onProgressUpdate();
                }

                try {
                    this.wait(1000);
                }catch (Exception e){}

            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            this.mediaPlayerControl.onAudioMoveFoward();
        }

        public Boolean getStopProcessStarted() {
            return stopProcessStarted;
        }

        public void setStopProcessStarted(Boolean stopProcessStarted) {
            this.stopProcessStarted = stopProcessStarted;
        }
    }

    private class SeekBakChange implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (audioFileFullPath == null){
                AlertDialogHelper.show(getContext(), getContext().getResources().getString(R.string.app_name), getContext().getResources().getString(R.string.noAudioFileAsociated), "OK", null, true, null);
                return;
            }

            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }

}
