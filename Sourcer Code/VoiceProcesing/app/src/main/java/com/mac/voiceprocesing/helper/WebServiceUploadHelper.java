package com.mac.voiceprocesing.helper;

import android.os.AsyncTask;

import com.mac.voiceprocesing.interfaces.iWebServiceUploadResult;
import com.mac.voiceprocesing.models.WebServiceQuery;

/**
 * Created by florin on 2/15/2015.
 */
public class WebServiceUploadHelper extends AsyncTask<WebServiceQuery, Void, Boolean> {

    private iWebServiceUploadResult result;

    public WebServiceUploadHelper(iWebServiceUploadResult result) {
        this.result = result;
    }

    @Override
    protected Boolean doInBackground(WebServiceQuery... params) {
        WebServiceQuery webServiceQuery = params[0];

        Boolean result = webServiceQuery.sendData();

        return result;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (this.result != null){
            this.result.getResult(aBoolean);
        }
    }
}
