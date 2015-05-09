package com.mac.voiceprocesing.dataSourcers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mac.voiceprocesing.helper.VPDatabaseHelper;
import com.mac.voiceprocesing.models.AudioTranslation;
import com.mac.voiceprocesing.models.BaseModel;

/**
 * Created by florin on 2/3/2015.
 */
public class VPDataSourcer  {

    protected SQLiteDatabase dbConnector;
    protected VPDatabaseHelper dbHelper;

    public VPDataSourcer(Context context) {
        this.dbHelper = new VPDatabaseHelper(context);
    }

    public long insert(BaseModel data, String tableName){
        this.dbConnector = this.dbHelper.getWritableDatabase();

        long id = this.dbConnector.insert(tableName, null, data.getValues());

        this.dbConnector.close();

        return id;
    }

    public void update(String tableName, BaseModel data, int id){
        this.dbConnector = this.dbHelper.getWritableDatabase();

        this.dbConnector.update(tableName, data.getValues(), "id = ?", new String[]{String.valueOf(id)});

        this.dbConnector.close();
    }

    public void delete(String tableName, int id){
        this.dbConnector = this.dbHelper.getWritableDatabase();

        this.dbConnector.delete(tableName, "id = ?", new String[]{String.valueOf(id)});

        this.dbConnector.close();
    }

    public void delete(String tableName){
        this.dbConnector = this.dbHelper.getWritableDatabase();

        this.dbConnector.delete(tableName, null, null);

        this.dbConnector.close();
    }
}
