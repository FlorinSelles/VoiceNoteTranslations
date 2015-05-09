package com.mac.voiceprocesing.models;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by florin on 2/5/2015.
 */
public class AudioListItem extends BaseModel{
    private AudioNote audio;
    private ArrayList<AudioTranslation> audioLanguages;

    public AudioListItem() {
    }

    public AudioListItem(AudioNote audio, ArrayList<AudioTranslation> audioLanguages) {
        this.audio = audio;
        this.audioLanguages = audioLanguages;
    }

    public AudioNote getAudio() {
        return audio;
    }

    public void setAudio(AudioNote audio) {
        this.audio = audio;
    }

    public ArrayList<AudioTranslation> getAudioLanguages() {
        return audioLanguages;
    }

    public void setAudioLanguages(ArrayList<AudioTranslation> audioLanguages) {
        this.audioLanguages = audioLanguages;
    }

    @Override
    public ContentValues getValues() {
        return null;
    }
}
