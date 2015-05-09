package com.mac.voiceprocesing.models;

import android.content.ContentValues;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.mac.voiceprocesing.interfaces.iAudioPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by florin on 1/28/2015.
 */
public class AudioNote extends BaseModel{
    private int id;
    private String audioFileName;
    private String description;
    private Language audioMainLanguage;

    public AudioNote(int id, String audioFileName, String description, Language audioMainLanguage) {
        this.id = id;
        this.audioFileName = audioFileName;
        this.description = description;
        this.audioMainLanguage = audioMainLanguage;
    }

    public AudioNote() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Language getAudioMainLanguage() {
        return audioMainLanguage;
    }

    public void setAudioMainLanguage(Language audioMainLanguage) {
        this.audioMainLanguage = audioMainLanguage;
    }

    public String getAudioFullPath(){
        return this.getFile(this.audioFileName).getAbsolutePath();
    }

    public Boolean fileExist(){
        return this.getFile(this.audioFileName).exists();
    }

    public byte[] getAudioFileBytes(){
        File audioFile = this.getFile(this.audioFileName);

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

        values.put("audioFileName", this.audioFileName);
        values.put("description", this.description);
        values.put("idLanguage", this.audioMainLanguage.getId());

        return values;
    }

    public void setAudioNoteBytes(byte[] fileBytes){
        try {
            File voiceNoteFile = this.getFile(this.audioFileName);
            if (voiceNoteFile.exists()){
                voiceNoteFile.delete();
            }
            voiceNoteFile.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(voiceNoteFile);
            outputStream.write(fileBytes);

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
