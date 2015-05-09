package com.mac.voiceprocesing.dataSourcers;

import android.content.Context;
import android.database.Cursor;

import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.Language;

import java.util.ArrayList;

/**
 * Created by florin on 2/3/2015.
 */
public class AudioNotesTable extends VPDataSourcer {

    private final String[] COLUMS = new String[]{"id", "audioFileName", "description", "idLanguage"};
    public static final String TABLENAME = "audioNotes";

    public AudioNotesTable(Context context) {
        super(context);
    }

    public AudioNote getElementById(int id){
        ArrayList<AudioNote> dataset = this.executeQuery("where audioNotes.id = ?", new String[]{String.valueOf(id)});

        if (dataset == null || dataset.size() == 0){
            return null;
        }

        return dataset.get(0);
    }

    public ArrayList<AudioNote> getAllElements(){
        return this.executeQuery(null, null);
    }

    public ArrayList<AudioNote> executeQuery(String selection, String[] selectionArgs){
        ArrayList<AudioNote> dataset = new ArrayList<>();

        this.dbConnector = this.dbHelper.getReadableDatabase();

        String query = "select audioNotes.id, audioFileName, description, idLanguage, languageName from audioNotes inner join languages as L on idLanguage = L.id";
        if (selection != null){
            query += " " + selection;
        }

        Cursor cursor = this.dbConnector.rawQuery(query, selectionArgs);

        if (cursor.getCount() == 0){
            return null;
        }

        cursor.moveToNext();
        while (!cursor.isAfterLast()){
            dataset.add(this.cursorToData(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        this.dbConnector.close();

        return dataset;
    }

    private AudioNote cursorToData (Cursor cursor){
        AudioNote data = new AudioNote();

        data.setId(cursor.getInt(0));
        data.setAudioFileName(cursor.getString(1));
        data.setDescription(cursor.getString(2));

        Language language = new Language();
        language.setId(cursor.getInt(3));
        language.setLanguageName(cursor.getString(4));

        data.setAudioMainLanguage(language);
        return data;
    }

}
