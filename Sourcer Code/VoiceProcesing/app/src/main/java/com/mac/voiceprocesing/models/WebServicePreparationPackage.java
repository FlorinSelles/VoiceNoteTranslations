package com.mac.voiceprocesing.models;

import android.content.Intent;

/**
 * Created by florin on 2/15/2015.
 */
public class WebServicePreparationPackage {

    private WebServiceUploadType typeUpload;
    private WebServiceDownloadType typeDownload;

    private Integer voidNoteID = null;
    private String json;
    private String fileName;
    private byte[] file;

    private String url;

    public Integer getVoidNoteID() {
        return voidNoteID;
    }

    public void setVoidNoteID(Integer voidNoteID) {
        this.voidNoteID = voidNoteID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public WebServiceUploadType getTypeUpload() {
        return typeUpload;
    }

    public void setTypeUpload(WebServiceUploadType typeUpload) {
        this.typeUpload = typeUpload;
    }

    public WebServiceDownloadType getTypeDownload() {
        return typeDownload;
    }

    public void setTypeDownload(WebServiceDownloadType typeDownload) {
        this.typeDownload = typeDownload;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
