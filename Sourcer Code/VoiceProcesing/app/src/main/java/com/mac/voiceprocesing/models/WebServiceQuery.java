package com.mac.voiceprocesing.models;

/**
 * Created by florin on 2/15/2015.
 */

public abstract class WebServiceQuery {

    public abstract Boolean sendData();
    public abstract WebServicePreparationPackage getData();

    protected abstract void upLoadData(String jsonData);
    protected abstract void upLoadData(byte[] file);

    protected abstract String downLoadData();
    protected abstract byte[] downLoadData(String fileName);
    protected abstract String downLoadData(int voidNoteId);
}
