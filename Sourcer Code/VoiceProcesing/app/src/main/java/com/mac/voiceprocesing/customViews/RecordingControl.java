package com.mac.voiceprocesing.customViews;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mac.voiceprocesing.R;

/**
 * Created by florin on 1/31/2015.
 */
public class RecordingControl extends LinearLayout {

    public enum RecordingState {Recording, NonRecording};

    private ImageView recording_background_iv;
    private ImageView recording_icon_iv;
    private TextView recordingText_tv;

    private String audioFileFullPath = null;
    private MediaRecorder mediaRecorder;
    private RecordingState recordingState;
    private AnimationDrawable animationDrawable;

    public RecordingControl(Context context) {
        this(context, null);
    }

    public RecordingControl(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.recording_control, this);

        this.recording_background_iv = (ImageView) this.findViewById(R.id.recording_background_iv);
        this.recording_icon_iv = (ImageView) this.findViewById(R.id.recording_icon_iv);
        this.recordingText_tv = (TextView) this.findViewById(R.id.recordingText_tv);

        this.recordingState = RecordingState.NonRecording;
    }

    public String getAudioFileFullPath() {
        return audioFileFullPath;
    }

    public void setAudioFileFullPath(String audioFileFullPath) {
        this.audioFileFullPath = audioFileFullPath;
    }

    public RecordingState getRecordingState() {
        return recordingState;
    }

    public void setRecordingState(RecordingState recordingState) {
        this.recordingState = recordingState;
    }

    public void startRecording() throws Exception{
        if (this.recordingState == RecordingState.NonRecording && this.audioFileFullPath != null ){
            try {
                this.initializarMediaRecorder();
                this.mediaRecorder.start();

                this.startIconAnimation();
                this.recordingText_tv.setText(this.getResources().getString(R.string.Recording));
                this.recordingState = RecordingState.Recording;
            }catch (Exception e){
                throw e;
            }
        }
    }

    public void stopRecording(){
        this.mediaRecorder.stop();

        this.stopIconAnimation();
        this.recordingText_tv.setText(this.getResources().getString(R.string.startRecording));
        this.recordingState = RecordingState.NonRecording;
    }

    private void startIconAnimation(){
        this.recording_icon_iv.setImageDrawable(null);
        this.recording_icon_iv.setBackgroundResource(R.drawable.microphone);

        this.animationDrawable = (AnimationDrawable) recording_icon_iv.getBackground();
        this.animationDrawable.start();
    }

    private void stopIconAnimation(){
        if (this.animationDrawable != null){
            this.animationDrawable.stop();
            this.animationDrawable.clearColorFilter();
        }

        this.recording_icon_iv.setBackground(null);
        this.recording_icon_iv.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.m1));
    }

    private void initializarMediaRecorder() throws Exception{
        this.mediaRecorder = new MediaRecorder();
        this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        this.mediaRecorder.setOutputFile(this.audioFileFullPath);

        try {
            this.mediaRecorder.prepare();
        }catch (Exception e){
            throw e;
        }
    }
}
