package com.mac.voiceprocesing.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mac.voiceprocesing.dataSourcers.LanguagesTable;
import com.mac.voiceprocesing.models.Language;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by florin on 2/3/2015.
 */
public class VPDatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASENAME = "voiceProcessing.sqlite";
    private final static int DATABASEVERSION = 1;

    private Context context;
 
    public VPDatabaseHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
        this.context = context;

        JSONObject a = new JSONObject();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ArrayList<String> queries = new ArrayList<>();
        queries.add("CREATE TABLE \"audioNotes\" (\"id\" INTEGER PRIMARY KEY  NOT NULL , \"audioFileName\" TEXT NOT NULL, \"description\" TEXT, \"idLanguage\" INTEGER)");
        queries.add("CREATE TABLE \"audioTranslations\" (\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"idAudio\" INTEGER NOT NULL , \"idLanguage\" INTEGER NOT NULL , \"translationFileName\" TEXT)");
        queries.add("CREATE TABLE \"languages\" (\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"languageName\" TEXT)");

        for (String query : queries){
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
