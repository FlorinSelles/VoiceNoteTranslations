package com.mac.voiceprocesing.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.helper.AlertDialogHelper;
import com.mac.voiceprocesing.interfaces.iAudioPlayer;
import com.mac.voiceprocesing.models.AudioListItem;
import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.AudioTranslation;
import com.mac.voiceprocesing.models.Language;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by florin on 1/28/2015.
 */
public class MiniAudioControl extends LinearLayout implements iAudioPlayer{

    private int controlId;
    private AudioListItem data = null;

    private ImageView audio_control_img;
    private TextView audio_name_tv;
    private TextView mainLanguage;
    private LinearLayout otherLanguagesContainer;

    private MediaPlayer mediaPlayer;

    public MiniAudioControl(Context context) {
        this(context, null);
    }

    public MiniAudioControl(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.startMediaPlayer();

        LayoutInflater.from(context).inflate(R.layout.mini_audio_control, this);

        this.loadComponetFromLayout();

        this.initializeComponents();

        this.createControlEvent();
    }

    public int getControlId() {
        return controlId;
    }

    public void setControlId(int controlId) {
        this.controlId = controlId;
    }

    public AudioListItem getAudioListItem() {
        return data;
    }

    public void setAudioListItem(AudioListItem data) {
        this.data = data;
        this.initializeComponents();
    }

    @Override
    public void play() {
        if (this.data.getAudio().getAudioFileName() != null){
            if (! this.data.getAudio().fileExist()){
                AlertDialogHelper.show(getContext(), getContext().getResources().getString(R.string.app_name), getContext().getResources().getString(R.string.noAudioFileAsociated), "OK", null, true, null);
                return;
            }

            try {
                this.mediaPlayerInitialization();
            }catch (Exception e){}
        }
        this.audio_control_img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.stop_icon));

        this.mediaPlayer.start();
        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audio_control_img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.play_icon));
            }
        });
    }

    @Override
    public void stop() {
        this.mediaPlayer.stop();

        this.audio_control_img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.play_icon));
    }

    @Override
    public void pause() {
        this.mediaPlayer.pause();
    }

    @Override
    public Boolean isPlaying() {
        return this.mediaPlayer.isPlaying();
    }

    private void mediaPlayerInitialization() throws Exception{
        if (this.isPlaying()){
            this.stop();
        }

        Uri filePath = Uri.fromFile(new File(this.data.getAudio().getAudioFullPath() ));
        this.mediaPlayer.reset();
        this.mediaPlayer.setDataSource(getContext(), filePath);
        this.mediaPlayer.prepare();
    }

    private void startMediaPlayer(){
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void loadComponetFromLayout(){
        this.audio_control_img = (ImageView) this.findViewById(R.id.audio_control_img);
        this.audio_name_tv = (TextView) this.findViewById(R.id.audio_name_tv);
        this.mainLanguage = (TextView) this.findViewById(R.id.mainLanguage);
        this.otherLanguagesContainer = (LinearLayout) this.findViewById(R.id.otherLanguagesContainer);
    }

    private void initializeComponents(){
        if (this.data == null){
            return;
        }

        if (this.data.getAudio().getAudioFullPath() != null && !this.data.getAudio().getAudioFullPath().isEmpty()){
            String text = this.data.getAudio().getAudioFileName();
            if (! this.data.getAudio().fileExist() ){
                text += this.getContext().getResources().getString(R.string.audioDeleted);
            }

            this.audio_name_tv.setText(text);
        }else {
            this.audio_name_tv.setText(getContext().getResources().getString(R.string.audioNameItem));
        }

        this.mainLanguage.setText(this.data.getAudio().getAudioMainLanguage().getLanguageName().isEmpty() ? getContext().getResources().getString(R.string.interminated) : this.data.getAudio().getAudioMainLanguage().getLanguageName());

        for (AudioTranslation item : this.data.getAudioLanguages()){
            TextView language = new TextView(getContext());

            LinearLayout.LayoutParams parameters = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parameters.setMargins(5, 0, 0, 0);
            language.setLayoutParams(parameters);

            language.setText(item.getLanguageId().getLanguageName());
            language.setTextColor(Color.parseColor("#FFFFFF"));
            language.setElevation(2);

            if (item.fileExist()){
                language.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.language_on_shape));
            }else {
                language.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.language_off_shape));
            }

            this.otherLanguagesContainer.addView(language);
        }
    }

    private void createControlEvent(){
        this.audio_control_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()){
                    stop();
                }else {
                    play();
                }
            }
        });
    }
}
