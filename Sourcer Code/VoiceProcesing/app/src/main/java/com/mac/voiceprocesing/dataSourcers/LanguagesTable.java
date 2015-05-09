package com.mac.voiceprocesing.dataSourcers;

import android.content.Context;
import android.database.Cursor;

import com.mac.voiceprocesing.models.BaseModel;
import com.mac.voiceprocesing.models.Language;

import java.util.ArrayList;

/**
 * Created by florin on 2/6/2015.
 */
public class LanguagesTable extends VPDataSourcer {

    private final String[] COLUMS = new String[]{"id", "languageName"};
    public final String TABLENAME = "languages";

    public LanguagesTable(Context context) {
        super(context);
    }

    public ArrayList<Language> getAllElements(){
        return this.executeQuery(null, null, null, null, null);
    }

    public Language getLanguageById(int id){
        ArrayList<Language> dataset = this.executeQuery("id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (dataset == null || dataset.size() == 0){
            return null;
        }

        return dataset.get(0);
    }

    public Language getLanguageByName(String languageName){
        ArrayList<Language> dataset = this.executeQuery("languageName = ?", new String[]{languageName}, null, null, null);

        if (dataset == null || dataset.size() == 0){
            return null;
        }

        return dataset.get(0);
    }

    private ArrayList<Language> executeQuery(String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        this.dbConnector = this.dbHelper.getReadableDatabase();

        Cursor cursor = this.dbConnector.query(this.TABLENAME, this.COLUMS, selection, selectionArgs, groupBy, having, orderBy);

        if (cursor.getCount() == 0){
            return null;
        }

        ArrayList<Language> dataset = new ArrayList<>();

        cursor.moveToNext();
        while (!cursor.isAfterLast()){
            dataset.add(this.cursorToData(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        this.dbConnector.close();

        return dataset;
    }

    private Language cursorToData(Cursor cursor){
        Language data = new Language();

        data.setId(cursor.getInt(0));
        data.setLanguageName(cursor.getString(1));

        return data;
    }
}
