package com.mac.voiceprocesing.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by florin on 2/5/2015.
 */
public class Language extends BaseModel implements Parcelable{
    private int id;
    private String languageName;

    public Language() {
    }

    public Language(int id, String languageName) {
        this.id = id;
        this.languageName = languageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public ContentValues getValues(){
        ContentValues values = new ContentValues();

        values.put("languageName", this.languageName);

        return values;
    }

    /*Parcelable section*/

    public static final Parcelable.Creator<Language> CREATOR = new Parcelable.Creator<Language>() {
       @Override
        public Language createFromParcel(Parcel source) {
            return new Language(source);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };

    private Language (Parcel soucer){
        this.id = soucer.readInt();
        this.languageName = soucer.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.languageName);
    }
}
