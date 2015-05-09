package com.mac.voiceprocesing.models;

import android.content.ContentValues;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by florin on 2/5/2015.
 */
public class AudioTranslation extends BaseModel{

    private int id;
    private AudioNote audioId;
    private Language languageId;
    private String translationFileName;

    public AudioTranslation() {
    }

    public AudioTranslation(int id, AudioNote audioId, Language languageId, String translationFileName) {
        this.id = id;
        this.audioId = audioId;
        this.languageId = languageId;
        this.translationFileName = translationFileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AudioNote getAudioId() {
        return audioId;
    }

    public void setAudioId(AudioNote audioId) {
        this.audioId = audioId;
    }

    public Language getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Language languageId) {
        this.languageId = languageId;
    }

    public String getTranslationFileName() {
        return translationFileName;
    }

    public void setTranslationFileName(String translationFileName) {
        this.translationFileName = translationFileName;
    }

    public String getTranlationFileFullPath(){
        return this.getFile(this.translationFileName).getAbsolutePath();
    }

    public Boolean fileExist(){
        File file = this.getFile(this.translationFileName);
        if (file == null){
            return false;
        }

        return file.exists();
    }

    public String getTranslationData(){
        File translationFile = this.getFile(this.translationFileName);

        if (translationFile == null){
            return null;
        }
        byte[] bytes = new byte[(int) translationFile.length()];

        FileInputStream in = null;
        try {
            in = new FileInputStream(translationFile);
            in.read(bytes);
        }catch (Exception e){}

        return new String(bytes);
    }

    public void setTranslationData(String data){
        File translationFile = this.getFile(this.translationFileName);

        if (translationFile.exists()){
            translationFile.delete();

            try {
                translationFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream stream = new FileOutputStream(translationFile);
            stream.write(data.getBytes());
        } catch (Exception e){

        }
    }

    public byte[] getAudioTranslationFileBytes(){
        File audioFile = this.getFile(this.translationFileName);

        byte[] buffer = new byte[(int) audioFile.length()];
        try {
            InputStream inputStream = new FileInputStream(audioFile);
            inputStream.read(buffer);
            return buffer;
        }catch (Exception e){
            return null;
        }
    }

    public ContentValues getValues(){
        ContentValues values = new ContentValues();

        values.put("idAudio", this.audioId.getId());
        values.put("idLanguage", this.languageId.getId());
        values.put("translationFileName", this.translationFileName);

        return values;
    }

}
