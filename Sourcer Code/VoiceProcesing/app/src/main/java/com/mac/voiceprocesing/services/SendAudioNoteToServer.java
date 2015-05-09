package com.mac.voiceprocesing.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.interfaces.iServiceResponse;
import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.AudioTranslation;
import com.mac.voiceprocesing.models.UpdateDbServiceConstants;
import com.mac.voiceprocesing.models.WebServiceAudioNoteQuery;
import com.mac.voiceprocesing.models.WebServiceAudioNoteTranslationQuery;
import com.mac.voiceprocesing.models.WebServicePreparationPackage;
import com.mac.voiceprocesing.models.WebServiceUploadType;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by florin on 2/22/2015.
 */
public class SendAudioNoteToServer extends IntentService  {

    public final static String SERVICEBROADCATSSIGN = "com.mac.voiceprocesing.services.SERVICEBROADCATSSIGN";

    private AudioNote audioNote;
    private ArrayList<AudioTranslation> translations;
    private iServiceResponse caller;

    public SendAudioNoteToServer(){
        this(null);
    }

    public SendAudioNoteToServer(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String voiceNoteJson = intent.getStringExtra("audioNote");
        String voiceNoteTranslationArrayJson = intent.getStringExtra("translations");

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<AudioTranslation>>(){}.getType();

        this.audioNote = (AudioNote) gson.fromJson(voiceNoteJson, AudioNote.class);
        this.translations = (ArrayList<AudioTranslation>) gson.fromJson(voiceNoteTranslationArrayJson, listType);

        String error = "";
        try {
            this.sendVoiceNoteData();

            this.sendVoiceNote();

            this.sendRequiredTranslation();
        } catch (Exception e) {
            error = e.getMessage();
        }

        Intent responseInt = this.createIntentResponse(error);
        LocalBroadcastManager.getInstance(this).sendBroadcast(responseInt);
    }

    private void sendVoiceNoteData(){
        String audioNoteJson = new Gson().toJson(this.audioNote, AudioNote.class);

        WebServicePreparationPackage wspp = new WebServicePreparationPackage();
        wspp.setTypeUpload(WebServiceUploadType.UpLoadString);
        wspp.setJson(audioNoteJson);
        wspp.setUrl(getResources().getString(R.string.webServiceUrl));

        WebServiceAudioNoteQuery wsAudioNote = new WebServiceAudioNoteQuery();
        wsAudioNote.setWebServicePreparationPackage(wspp);

        wsAudioNote.sendData();
    }

    private void sendVoiceNote(){
        WebServicePreparationPackage wspp = new WebServicePreparationPackage();
        wspp.setUrl(getResources().getString(R.string.webServiceUrl));
        wspp.setTypeUpload(WebServiceUploadType.UpLoadFile);
        wspp.setFile(audioNote.getAudioFileBytes());

        WebServiceAudioNoteQuery wsAudioNote = new WebServiceAudioNoteQuery();
        wsAudioNote.setWebServicePreparationPackage(wspp);

        wsAudioNote.sendData();
    }

    private void sendRequiredTranslation(){
        WebServicePreparationPackage wspp = new WebServicePreparationPackage();
        wspp.setTypeUpload(WebServiceUploadType.UpLoadString);
        wspp.setUrl(getResources().getString(R.string.webServiceUrl));

        WebServiceAudioNoteTranslationQuery query = new WebServiceAudioNoteTranslationQuery();

        Gson gson = new Gson();
        for (AudioTranslation item : this.translations){
            wspp.setJson(gson.toJson(item, AudioTranslation.class));

            query.setWebServicePreparationPackage(wspp);
            query.sendData();
        }
    }

    private Intent createIntentResponse(String error){
        Intent intentResponse = new Intent(this.SERVICEBROADCATSSIGN);
        intentResponse.putExtra(UpdateDbServiceConstants.EXTENDED_DATA_STATUS, error);

        return intentResponse;
    }

    public iServiceResponse getCaller() {
        return caller;
    }

    public void setCaller(iServiceResponse caller) {
        this.caller = caller;
    }
}
