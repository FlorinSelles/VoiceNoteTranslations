package com.mac.voiceprocesing.dataSourcers;

import android.content.Context;
import android.database.Cursor;

import com.mac.voiceprocesing.models.AudioListItem;
import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.AudioTranslation;
import com.mac.voiceprocesing.models.Language;

import java.util.ArrayList;

/**
 * Created by florin on 2/5/2015.
 */
public class AudioTranslationsTable extends VPDataSourcer{

    private final String[] COLUMS = new String[]{"id", "idAudio", "idLanguage", "translationFileName"};
    public static final String TABLENAME = "audioTranslations";

    public AudioTranslationsTable(Context context) {
        super(context);
    }

    public ArrayList<AudioTranslation> getAllElements(){
        return this.executeQuery(null, null);
    }

    public AudioTranslation getElementById(int id){
        ArrayList<AudioTranslation> dataset = this.executeQuery("where audioTranslations.id = ?", new String[]{String.valueOf(id)});

        if (dataset == null || dataset.size() == 0){
            return null;
        }

        return dataset.get(0);
    }

    public ArrayList<AudioTranslation> getAudioTranslations(int audioId){
        return this.executeQuery("where idAudio = ?", new String[]{String.valueOf(audioId)});
    }

    public AudioTranslation getElementByAudioIdAndLanguage(int id, int languageId){
        ArrayList<AudioTranslation> dataset = this.executeQuery("where idAudio = ? and idLanguage = ?", new String[]{String.valueOf(id), String.valueOf(languageId)});

        if (dataset == null || dataset.size() == 0){
            return null;
        }

        return dataset.get(0);
    }

    private ArrayList<AudioTranslation> executeQuery(String selection, String[] selectionArg){
        this.dbConnector = this.dbHelper.getReadableDatabase();

        String query = "select audioTranslations.id, idAudio, idLanguage, languageName, translationFileName from audioTranslations inner join languages as L on idLanguage = L.id";
        if (selection != null){
            query += " " + selection;
        }

        Cursor cursor = this.dbConnector.rawQuery(query, selectionArg);

        if (cursor.getCount() == 0){
            return null;
        }

        ArrayList<AudioTranslation> dataset = new ArrayList<>();

        cursor.moveToNext();
        while (!cursor.isAfterLast()){
            dataset.add(this.cursorToData(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        this.dbConnector.close();

        return dataset;
    }

    private AudioTranslation cursorToData (Cursor cursor){
        AudioTranslation data = new AudioTranslation();

        data.setId(cursor.getInt(0));

        AudioNote audioNote = new AudioNote();
        audioNote.setId(cursor.getInt(1));
        data.setAudioId(audioNote);

        Language language = new Language();
        language.setId(cursor.getInt(2));
        language.setLanguageName(cursor.getString(3));

        data.setLanguageId(language);
        data.setTranslationFileName(cursor.getString(4));

        return data;
    }
}
