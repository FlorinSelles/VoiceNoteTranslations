package com.mac.voiceprocesing.models;

import com.mac.voiceprocesing.webService.VoiceProcessingService;

/**
 * Created by florin on 2/15/2015.
 */
public class WebServiceLanguageQuery extends WebServiceQuery {

    private WebServicePreparationPackage wsPP;

    @Override
    public Boolean sendData() {
        return null;
    }

    @Override
    public WebServicePreparationPackage getData() {
        WebServicePreparationPackage packageData = new WebServicePreparationPackage();

        switch (this.wsPP.getTypeDownload()){
            case DownloadString:
                packageData.setJson(this.downLoadData());
                break;
            case DownloadFile:
                break;
            default:break;
        }

        return packageData;
    }

    @Override
    protected void upLoadData(String jsonData) {

    }

    @Override
    protected void upLoadData(byte[] file) {

    }

    @Override
    protected String downLoadData() {
        VoiceProcessingService service = new VoiceProcessingService();
        service.setUrl(this.wsPP.getUrl());

        return service.DownloadAllLanguages();
    }

    @Override
    protected byte[] downLoadData(String fileName) {
        return new byte[0];
    }

    public WebServicePreparationPackage getWsPP() {
        return wsPP;
    }

    public void setWsPP(WebServicePreparationPackage wsPP) {
        this.wsPP = wsPP;
    }

    @Override
    protected String downLoadData(int voidNoteId) {
        return null;
    }
}
