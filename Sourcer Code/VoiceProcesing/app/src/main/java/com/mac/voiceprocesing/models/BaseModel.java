package com.mac.voiceprocesing.models;

import android.content.ContentValues;
import android.os.Environment;

import java.io.File;

/**
 * Created by florin on 2/5/2015.
 */
public abstract class BaseModel {

    protected File getFile(String fileName){
        if (fileName == null){
            return null;
        }

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        return new File(storageDir.getAbsolutePath()+ "/"+ fileName);
    }

    public abstract ContentValues getValues();
}
