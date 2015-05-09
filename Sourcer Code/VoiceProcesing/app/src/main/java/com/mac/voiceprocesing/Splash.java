package com.mac.voiceprocesing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mac.voiceprocesing.dataSourcers.LanguagesTable;
import com.mac.voiceprocesing.helper.WebServiceDownloadHelper;
import com.mac.voiceprocesing.interfaces.iWebServiceDownloadResult;
import com.mac.voiceprocesing.models.Language;
import com.mac.voiceprocesing.models.WebServiceDownloadType;
import com.mac.voiceprocesing.models.WebServiceLanguageQuery;
import com.mac.voiceprocesing.models.WebServicePreparationPackage;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Splash extends ActionBarActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ArrayList<Language> languages = new LanguagesTable(this).getAllElements();
        if (languages == null || languages.size() == 0){
            this.updatingDB();
            return;
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                changeActivity();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, this.SPLASH_SCREEN_DELAY);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeActivity(){
        Intent intent = new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updatingDB(){
        this.dialog = new ProgressDialog(this);
        dialog.setTitle(this.getResources().getString(R.string.app_name));
        dialog.setMessage("Updating Aplication...");
        dialog.setCancelable(false);
        dialog.show();

        /*for the moment*/
        WebServicePreparationPackage packageData = new WebServicePreparationPackage();
        packageData.setUrl(this.getResources().getString(R.string.webServiceUrl));
        packageData.setTypeDownload(WebServiceDownloadType.DownloadString);

        WebServiceLanguageQuery languageQuery = new WebServiceLanguageQuery();
        languageQuery.setWsPP(packageData);

        WebServiceDownloadHelper downloadHelper = new WebServiceDownloadHelper(new OnDownloadLanguageTableFinish(this));
        downloadHelper.execute(languageQuery);
    }

    private class OnDownloadLanguageTableFinish implements iWebServiceDownloadResult{

        private Context context;

        private OnDownloadLanguageTableFinish(Context context) {
            this.context = context;
        }

        @Override
        public void getResult(WebServicePreparationPackage wsP) {
            String json = wsP.getJson();

            ArrayList<Language> languages = new ArrayList<>();
            languages.add(new Language(1, "Select one"));
            ArrayList<Language> elements = new Gson().fromJson(json, new TypeToken<List<Language>>(){}.getType());
            languages.addAll(elements);

            if (languages != null || languages.size() > 0){
                for (Language item : languages){
                    new LanguagesTable(this.context).insert(item, "languages");
                }
            }

            dialog.dismiss();

            changeActivity();
        }
    }
}
