package com.mac.voiceprocesing.controllers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.customViews.MediaPlayerControl;
import com.mac.voiceprocesing.dataSourcers.AudioNotesTable;
import com.mac.voiceprocesing.dataSourcers.AudioTranslationsTable;
import com.mac.voiceprocesing.dataSourcers.LanguagesTable;
import com.mac.voiceprocesing.fragments.AddTranslationDialog;
import com.mac.voiceprocesing.helper.AlertDialogHelper;
import com.mac.voiceprocesing.helper.WebServiceUploadHelper;
import com.mac.voiceprocesing.interfaces.iWebServiceUploadResult;
import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.AudioTranslation;
import com.mac.voiceprocesing.models.Language;
import com.mac.voiceprocesing.models.WebServiceAudioNoteTranslationQuery;
import com.mac.voiceprocesing.models.WebServicePreparationPackage;
import com.mac.voiceprocesing.models.WebServiceUploadType;

import java.io.File;
import java.util.ArrayList;

public class AudioDetailActivity extends ActionBarActivity {

    private TextView voiceNoteMainLanguage_tv;
    private LinearLayout OtherLanguage_ly;
    private TextView audioDescription_edt;
    private MediaPlayerControl mediaPlayerControl_id;
    private ImageButton btn_addTranslation;
    private TextView actionBarTitle_tv;
    private Spinner language_sp;
    private EditText translation_et;

    private AudioNote audioData;
    private ArrayList<AudioTranslation> translations;
    private ArrayList<String> translationsTexts;
    private int previosSelection = -1;

    private ProgressDialog sendingDataToServerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setElevation(0);

        setContentView(R.layout.activity_audio_detail);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null){
            return;
        }

        int id = bundle.getInt("id");

        this.loadComponents();

        this.initialiseComponents(id);

        this.createEvents();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_detail, menu);
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

    private void loadComponents(){
        this.actionBarTitle_tv = (TextView) this.findViewById(R.id.actionBarTitle_tv);
        this.voiceNoteMainLanguage_tv = (TextView) this.findViewById(R.id.voiceNoteMainLanguage_tv);
        this.OtherLanguage_ly = (LinearLayout) this.findViewById(R.id.OtherLanguage_ly);
        this.audioDescription_edt = (TextView) this.findViewById(R.id.audioDescription_edt);
        this.mediaPlayerControl_id = (MediaPlayerControl) this.findViewById(R.id.mediaPlayerControl_id);
        this.btn_addTranslation = (ImageButton) this.findViewById(R.id.btn_addTranslation);
        this.language_sp = (Spinner) this.findViewById(R.id.language_sp);
        this.translation_et = (EditText) this.findViewById(R.id.translation_et);

    }

    private void initialiseComponents(int id){
        this.audioData = new AudioNotesTable(this).getElementById(id);
        this.translations = new AudioTranslationsTable(this).getAudioTranslations(audioData.getId());

        this.actionBarTitle_tv.setText(this.audioData.getAudioFileName());
        this.voiceNoteMainLanguage_tv.setText(this.audioData.getAudioMainLanguage().getLanguageName());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.add("Select one");

        for (AudioTranslation item : this.translations){
            TextView language = new TextView(this);

            LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parameters.setMargins(5, 6, 0, 0);
            language.setLayoutParams(parameters);

            language.setText(item.getLanguageId().getLanguageName());
            language.setTextColor(Color.parseColor("#FFFFFF"));
            language.setElevation(2);

            if (item.fileExist()){
                language.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.language_on_shape));
            }else {
                language.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.language_off_shape));
            }

            adapter.add(item.getLanguageId().getLanguageName());
            this.OtherLanguage_ly.addView(language);
        }

        this.audioDescription_edt.setText(this.audioData.getDescription());

        this.language_sp.setAdapter(adapter);

        this.translationsTexts = new ArrayList<>();

        this.mediaPlayerControl_id.setAudioFileFullPath(this.audioData.getAudioFullPath());
    }

    private void createEvents(){
        this.btn_addTranslation.setOnClickListener(new OnAddTranslationClick());
        this.language_sp.setOnItemSelectedListener(new OnLanguageSpItemSelected());
    }

    private AudioTranslation createNewAudioTranslationFile(){
        String audioFileName = audioData.getAudioFileName();
        Language language1 = new LanguagesTable(this).getLanguageByName(language_sp.getSelectedItem().toString());
        String newTranslationFileName = audioFileName.substring(0, audioFileName.length() - 4) + "_" + language1.getLanguageName() + ".txt";

        AudioTranslation audioTranslation = new AudioTranslationsTable(this).getElementByAudioIdAndLanguage(this.audioData.getId(), language1.getId());
        audioTranslation.setAudioId(audioData);
        audioTranslation.setLanguageId(language1);
        audioTranslation.setTranslationFileName(newTranslationFileName);

        return audioTranslation;
    }

    private class OnAddTranslationClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            sendingDataToServerDialog = new ProgressDialog(v.getContext());
            sendingDataToServerDialog.setTitle(v.getContext().getResources().getString(R.string.app_name));
            sendingDataToServerDialog.setMessage("Sending translation to server...");
            sendingDataToServerDialog.setCancelable(false);
            sendingDataToServerDialog.show();

            AudioTranslation audioTranslation = createNewAudioTranslationFile();
            audioTranslation.setTranslationData(translation_et.getText().toString());

            new AudioTranslationsTable(v.getContext()).update("audioTranslations", audioTranslation, audioTranslation.getId());

            String json = new Gson().toJson(audioTranslation, AudioTranslation.class);

            /*prparing to send data*/
            WebServicePreparationPackage packageToSend = new WebServicePreparationPackage();
            String url = v.getContext().getResources().getString(R.string.webServiceUrl);
            packageToSend.setUrl(url);
            packageToSend.setTypeUpload(WebServiceUploadType.UpLoadString);
            packageToSend.setJson(json);

            WebServiceAudioNoteTranslationQuery query = new WebServiceAudioNoteTranslationQuery();
            query.setWebServicePreparationPackage(packageToSend);

            WebServiceUploadHelper webServiceUploadHelper = new WebServiceUploadHelper(new OnAudioTranslationDataSent(audioTranslation.getAudioTranslationFileBytes()));
            webServiceUploadHelper.execute(query);
        }
    }

    private class OnLanguageSpItemSelected implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0){
                translation_et.setEnabled(false);
                return;
            }else {
                translation_et.setEnabled(true);
            }

            AudioTranslation audioTranslation = createNewAudioTranslationFile();

            if (audioTranslation.fileExist()){
                translation_et.setText(audioTranslation.getTranslationData());
            }else {
                translation_et.setText("");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class OnAudioTranslationDataSent implements iWebServiceUploadResult{

        private byte[] fileToSend;

        private OnAudioTranslationDataSent(byte[] fileToSend) {
            this.fileToSend = fileToSend;
        }

        @Override
        public void getResult(Boolean result) {
            WebServicePreparationPackage packageToSend = new WebServicePreparationPackage();
            packageToSend.setUrl(getResources().getString(R.string.webServiceUrl));
            packageToSend.setTypeUpload(WebServiceUploadType.UpLoadFile);
            packageToSend.setFile(this.fileToSend);

            WebServiceAudioNoteTranslationQuery query = new WebServiceAudioNoteTranslationQuery();
            query.setWebServicePreparationPackage(packageToSend);

            WebServiceUploadHelper webServiceUploadHelper = new WebServiceUploadHelper(new OnAudioTranslationFileSent());
            webServiceUploadHelper.execute(query);
        }
    }

    private class OnAudioTranslationFileSent implements iWebServiceUploadResult{
        @Override
        public void getResult(Boolean result) {
            sendingDataToServerDialog.dismiss();
            AlertDialogHelper.show(sendingDataToServerDialog.getContext(), getResources().getString(R.string.app_name), "The traslaltion has being sended to the server", "OK", null, true, null);
        }
    }
}
