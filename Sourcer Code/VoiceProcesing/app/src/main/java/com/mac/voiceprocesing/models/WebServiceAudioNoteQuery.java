package com.mac.voiceprocesing.models;

import com.mac.voiceprocesing.webService.VectorByte;
import com.mac.voiceprocesing.webService.VoiceProcessingService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by florin on 2/15/2015.
 */
public class WebServiceAudioNoteQuery extends WebServiceQuery {

    private WebServicePreparationPackage wsPP;

    public WebServicePreparationPackage getWebServicePreparationPackage() {
        return wsPP;
    }

    public void setWebServicePreparationPackage(WebServicePreparationPackage wsPP) {
        this.wsPP = wsPP;
    }

    @Override
    public Boolean sendData(){
        switch (this.wsPP.getTypeUpload()){
            case UpLoadFile:
                this.upLoadData(this.wsPP.getFile());
                break;
            case UpLoadString:
                this.upLoadData(this.wsPP.getJson());
                break;
            default:break;
        }

        return true;
    }

    @Override
    public WebServicePreparationPackage getData(){
        WebServicePreparationPackage tmp = new WebServicePreparationPackage();

        switch (this.wsPP.getTypeDownload()){
            case DownloadFile:
                tmp.setFile(this.downLoadData(this.wsPP.getFileName()));
                break;
            case DownloadString:
                tmp.setJson(this.downLoadData());
                break;
            default:break;
        }

        return tmp;
    }

    @Override
    protected void upLoadData(String jsonData) {
        VoiceProcessingService service = new VoiceProcessingService();
        service.setUrl(this.wsPP.getUrl());

        service.UpLoadAudioNote(this.wsPP.getJson());
    }

    @Override
    protected void upLoadData(byte[] file) {
        VoiceProcessingService service = new VoiceProcessingService();
        service.setUrl(this.wsPP.getUrl());

        VectorByte vectorByte = new VectorByte(this.wsPP.getFile());
        service.UploadAudioNoteFile(vectorByte);
    }

    @Override
    protected String downLoadData() {
        VoiceProcessingService service = new VoiceProcessingService();
        service.setUrl(this.wsPP.getUrl());

        String value = service.DownloadAllAudioNotes();

        return value;
    }

    @Override
    protected byte[] downLoadData(String fileName) {
        VoiceProcessingService service = new VoiceProcessingService();
        service.setUrl(this.wsPP.getUrl());

        VectorByte vectorByte = service.DownloadAudioNoteFile(fileName);
        return vectorByte.toBytes();
    }

    @Override
    protected String downLoadData(int voidNoteId) {
        return null;
    }
}
