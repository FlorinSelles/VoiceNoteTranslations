package com.mac.voiceprocesing.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.dataSourcers.AudioNotesTable;
import com.mac.voiceprocesing.dataSourcers.AudioTranslationsTable;
import com.mac.voiceprocesing.dataSourcers.LanguagesTable;
import com.mac.voiceprocesing.helper.WebServiceDownloadHelper;
import com.mac.voiceprocesing.interfaces.iWebServiceDownloadResult;
import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.AudioTranslation;
import com.mac.voiceprocesing.models.UpdateDbServiceConstants;
import com.mac.voiceprocesing.models.WebServiceAudioNoteQuery;
import com.mac.voiceprocesing.models.WebServiceAudioNoteTranslationQuery;
import com.mac.voiceprocesing.models.WebServiceDownloadType;
import com.mac.voiceprocesing.models.WebServicePreparationPackage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by florin on 2/20/2015.
 */
public class UpdateDBService extends IntentService {

    public UpdateDBService(){
        super(null);
    }

    public UpdateDBService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<String> errors = new ArrayList<>();

        ArrayList<AudioNote> voiceNotes = this.getAudioNotesFromServer();
        if (voiceNotes == null){
            Intent resultIntent = this.createIntentResponse((String[]) errors.toArray());
            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
            return;
        }

        new AudioNotesTable(this).delete(AudioNotesTable.TABLENAME);
        new AudioTranslationsTable(this).delete(AudioTranslationsTable.TABLENAME);

        for (AudioNote item : voiceNotes){
            long id = new AudioNotesTable(this).insert(item, AudioNotesTable.TABLENAME);

            byte[] fileBytes = this.getVoiceNoteAudioFile(item.getAudioFileName()).getFile();
            if (fileBytes != null){
                item.setAudioNoteBytes(fileBytes);
            }

            ArrayList<AudioTranslation> translations = this.getAudioTranslation(item.getId());
            for(AudioTranslation itemTrans : translations){
                itemTrans.getAudioId().setId((int)id);
                int languageID = new LanguagesTable(this).getLanguageByName(itemTrans.getLanguageId().getLanguageName()).getId();
                itemTrans.getLanguageId().setId(languageID);

                new AudioTranslationsTable(this).insert(itemTrans, AudioTranslationsTable.TABLENAME);

                if (itemTrans.getTranslationFileName() != null)
                {
                    byte[] translationBytes = this.getVoiceNoteAudioTranslationFile(itemTrans.getTranslationFileName()).getFile();
                    if (translationBytes != null){
                        try {
                            itemTrans.setTranslationData(new String(translationBytes, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        Intent resultIntent = this.createIntentResponse(errors.size() == 0 ? null : (String[])errors.toArray());
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    private Intent createIntentResponse(String[] value){
        Intent intentResponse = new Intent(UpdateDbServiceConstants.BROADCAST_ACTION);
        intentResponse.putExtra(UpdateDbServiceConstants.EXTENDED_DATA_STATUS, value);

        return intentResponse;
    }

    private ArrayList<AudioNote> getAudioNotesFromServer(){
        WebServicePreparationPackage packegeData = new WebServicePreparationPackage();
        packegeData.setTypeDownload(WebServiceDownloadType.DownloadString);
        packegeData.setUrl(this.getResources().getString(R.string.webServiceUrl));

        WebServiceAudioNoteQuery query = new WebServiceAudioNoteQuery();
        query.setWebServicePreparationPackage(packegeData);

        String jsonResult = query.getData().getJson();
        if (jsonResult == null || jsonResult.isEmpty()){
            return null;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<AudioNote>>(){}.getType();

        return gson.fromJson(jsonResult, listType);
    }

    private ArrayList<AudioTranslation> getAudioTranslation(int voidNoteid){
        WebServicePreparationPackage packegeData = new WebServicePreparationPackage();
        packegeData.setTypeDownload(WebServiceDownloadType.DownloadString);
        packegeData.setVoidNoteID(voidNoteid);
        packegeData.setUrl(this.getResources().getString(R.string.webServiceUrl));

        WebServiceAudioNoteTranslationQuery query = new WebServiceAudioNoteTranslationQuery();
        query.setWebServicePreparationPackage(packegeData);

        String jsonResult = query.getData().getJson();
        if (jsonResult == null || jsonResult.isEmpty()){
            return null;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<AudioTranslation>>(){}.getType();

        ArrayList<AudioTranslation> tmp = null;
        try {
            tmp = gson.fromJson(jsonResult, listType);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return tmp;
    }

    private WebServicePreparationPackage getVoiceNoteAudioFile(String fileName){
        WebServicePreparationPackage packegeData = new WebServicePreparationPackage();
        packegeData.setUrl(this.getResources().getString(R.string.webServiceUrl));
        packegeData.setTypeDownload(WebServiceDownloadType.DownloadFile);
        packegeData.setFileName(fileName);

        WebServiceAudioNoteQuery query = new WebServiceAudioNoteQuery();
        query.setWebServicePreparationPackage(packegeData);

        WebServicePreparationPackage response = query.getData();

        return response;
    }

    private WebServicePreparationPackage getVoiceNoteAudioTranslationFile(String fileName){
        WebServicePreparationPackage packegeData = new WebServicePreparationPackage();
        packegeData.setUrl(this.getResources().getString(R.string.webServiceUrl));
        packegeData.setTypeDownload(WebServiceDownloadType.DownloadFile);
        packegeData.setFileName(fileName);

        WebServiceAudioNoteTranslationQuery query = new WebServiceAudioNoteTranslationQuery();
        query.setWebServicePreparationPackage(packegeData);

        WebServicePreparationPackage response = query.getData();

        return response;
    }
}
