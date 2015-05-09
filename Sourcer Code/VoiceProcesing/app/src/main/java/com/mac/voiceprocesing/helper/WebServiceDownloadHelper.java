package com.mac.voiceprocesing.helper;
import android.os.AsyncTask;
import com.mac.voiceprocesing.interfaces.iWebServiceDownloadResult;
import com.mac.voiceprocesing.models.WebServicePreparationPackage;
import com.mac.voiceprocesing.models.WebServiceQuery;

import static com.mac.voiceprocesing.models.WebServiceQuery.*;

/**
 * Created by florin on 2/15/2015.
 */
public class WebServiceDownloadHelper extends AsyncTask<WebServiceQuery, Void, WebServicePreparationPackage>{

    private iWebServiceDownloadResult iwebServie;

    public WebServiceDownloadHelper(iWebServiceDownloadResult iwebServie) {
        this.iwebServie = iwebServie;
    }

    @Override
    protected WebServicePreparationPackage doInBackground(WebServiceQuery... params) {
        WebServiceQuery webServiceQuery = params[0];

        WebServicePreparationPackage tmp = webServiceQuery.getData();

        return tmp;
    }

    @Override
    protected void onPostExecute(WebServicePreparationPackage webServicePreparationPackage) {
        this.iwebServie.getResult(webServicePreparationPackage);
    }
}
