package com.mac.voiceprocesing.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.adaptors.LanguageSpinnerAdaptor;
import com.mac.voiceprocesing.customViews.MediaPlayerControl;
import com.mac.voiceprocesing.customViews.RecordingControl;
import com.mac.voiceprocesing.dataSourcers.AudioNotesTable;
import com.mac.voiceprocesing.dataSourcers.AudioTranslationsTable;
import com.mac.voiceprocesing.dataSourcers.LanguagesTable;
import com.mac.voiceprocesing.helper.AlertDialogHelper;
import com.mac.voiceprocesing.helper.WebServiceUploadHelper;
import com.mac.voiceprocesing.interfaces.iAlertDialogHelperButtonClick;
import com.mac.voiceprocesing.interfaces.iServiceResponse;
import com.mac.voiceprocesing.interfaces.iWebServiceUploadResult;
import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.AudioTranslation;
import com.mac.voiceprocesing.models.Language;
import com.mac.voiceprocesing.models.UpdateDbServiceConstants;
import com.mac.voiceprocesing.models.WebServiceAudioNoteQuery;
import com.mac.voiceprocesing.models.WebServicePreparationPackage;
import com.mac.voiceprocesing.models.WebServiceQuery;
import com.mac.voiceprocesing.models.WebServiceUploadType;
import com.mac.voiceprocesing.services.SendAudioNoteToServer;
import com.mac.voiceprocesing.services.SendAudioNoteToServerReciver;
import com.mac.voiceprocesing.services.UpdateDBSReciver;
import com.mac.voiceprocesing.services.UpdateDBService;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class AddAudioActivity extends ActionBarActivity {

    private File audioFile;
    private MediaPlayerControl mediaPlayerControl;
    private RecordingControl recordingComponent;
    private EditText fileName_et;
    private Spinner audioVoiceMainLanguage_sp;
    private Spinner translateTo_sp;
    private EditText voidNoteDescription_et;

    private AudioNote audioNote;
    private ArrayList<AudioTranslation> audioTranslations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setElevation(0);

        setContentView(R.layout.activity_add_audio);

        this.loadComponents();

        this.initializateComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_audio, menu);
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
        this.mediaPlayerControl = (MediaPlayerControl) this.findViewById(R.id.mediaPlayerControl);
        this.fileName_et = (EditText) this.findViewById(R.id.fileName_et);
        this.audioVoiceMainLanguage_sp = (Spinner) this.findViewById(R.id.audioVoiceMainLanguage_sp);
        this.translateTo_sp = (Spinner) this.findViewById(R.id.translateTo_sp);
        this.voidNoteDescription_et = (EditText) this.findViewById(R.id.voidNoteDescription_et);
    }

    private void initializateComponents(){
        this.audioNote = new AudioNote();
        this.audioTranslations = new ArrayList<>();

        ArrayList<Language> languages = new LanguagesTable(this).getAllElements();

        LanguageSpinnerAdaptor adaptor = new LanguageSpinnerAdaptor(languages);

        this.audioVoiceMainLanguage_sp.setAdapter(adaptor);
        this.audioVoiceMainLanguage_sp.setOnItemSelectedListener(new OnSpinnerMainLanguageItemSelected());

        this.translateTo_sp.setAdapter(adaptor);
        this.translateTo_sp.setOnItemSelectedListener(new OnSpinnerRequestedTranslateToItemSelected());

        this.recordingComponent = (RecordingControl) this.findViewById(R.id.recordingComponent);
        recordingComponent.setOnClickListener(new OnRecorderClick());

        Button btn_addAudio = (Button) this.findViewById(R.id.btn_addAudio);
        btn_addAudio.setOnClickListener(new OnAddAudioBtnClick());
    }

    private void addRequestedTranslation(Language translation){
        Boolean isOn = false;

        for (AudioTranslation item : this.audioTranslations){
            if (item.getLanguageId().getId() == translation.getId()){
                this.audioTranslations.remove(item);
                isOn = true;
                break;
            }
        }

        if (isOn == false){
            AudioTranslation element = new AudioTranslation();
            element.setLanguageId(translation);

            this.audioTranslations.add(element);
        }
    }

    private class OnRecorderClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String fileName = fileName_et.getText().toString();
            if (fileName.isEmpty()) {
                AlertDialogHelper.show(v.getContext(), v.getContext().getString(R.string.mandatoryFile), v.getContext().getString(R.string.fileName), "OK", null, true, null);
                return;
            }

            if (recordingComponent.getRecordingState() == RecordingControl.RecordingState.NonRecording){
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                audioFile = new File(storageDir.getAbsolutePath()+ "/" + fileName + ".3gp");

                if (audioFile.exists()){
                   AlertDialogHelper.show(v.getContext(), v.getContext().getString(R.string.app_name), v.getContext().getString(R.string.audioFileExist), "OK", null, true, null);
                   return;
                }

                recordingComponent.setAudioFileFullPath(audioFile.getAbsolutePath());
                try {
                    recordingComponent.startRecording();
                }catch (Exception e){
                    AlertDialogHelper.show(v.getContext(), v.getContext().getString(R.string.recordingError), e.getMessage(), "OK", null, true, null);
                }
            }else {
                recordingComponent.stopRecording();
                AlertDialogHelper.show(v.getContext(), v.getContext().getString(R.string.recordingFinish), v.getResources().getString(R.string.recordingFinishMes), "OK", null, true, null);
                mediaPlayerControl.setAudioFileFullPath(recordingComponent.getAudioFileFullPath());
            }
        }
    }

    private class OnAddAudioBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (recordingComponent.getRecordingState() == RecordingControl.RecordingState.Recording){
                AlertDialogHelper.show(v.getContext(), v.getContext().getResources().getString(R.string.app_name), v.getContext().getResources().getString(R.string.recording), "OK", null, true, null);
                return;
            }

            if (recordingComponent.getAudioFileFullPath() == null || recordingComponent.getAudioFileFullPath().isEmpty()){
                AlertDialogHelper.show(v.getContext(), v.getContext().getResources().getString(R.string.app_name), v.getContext().getResources().getString(R.string.recordAudioFirst), "OK", null, true, null);
                return;
            }

            File file = new File(recordingComponent.getAudioFileFullPath());

            audioNote.setAudioFileName(file.getName());
            audioNote.setDescription(voidNoteDescription_et.getText().toString());

            if (audioNote.getAudioMainLanguage() == null){
                audioNote.setAudioMainLanguage((Language)audioVoiceMainLanguage_sp.getSelectedItem());
            }

            if (audioTranslations.size() == 0){
                AudioTranslation tmp = new AudioTranslation();
                tmp.setLanguageId((Language) translateTo_sp.getSelectedItem());
                tmp.setTranslationFileName(null);

                audioTranslations.add(tmp);
            }

            audioNote.setId((int) new AudioNotesTable(v.getContext()).insert(audioNote, "audioNotes"));

            for (AudioTranslation item : audioTranslations)
            {
                item.setAudioId(audioNote);
                new AudioTranslationsTable(v.getContext()).insert(item, "audioTranslations");
            }

            AlertDialogHelper.show(v.getContext(), v.getContext().getResources().getString(R.string.app_name), v.getContext().getResources().getString(R.string.audioFileAddToDB), "OK", null, true, new OnUploadAudioToServer(v.getContext()));
        }
    }

    private class OnUploadAudioToServer implements iAlertDialogHelperButtonClick, iServiceResponse{

        private Context context;
        private ProgressDialog sendingDataDialog;

        private OnUploadAudioToServer(Context context) {
            this.context = context;
        }

        @Override
        public void onDialogPositiveButtonClick(DialogInterface dialog) {
            dialog.dismiss();

            this.sendingDataDialog = new ProgressDialog(this.context);
            this.sendingDataDialog.setTitle(getResources().getString(R.string.app_name));
            this.sendingDataDialog.setMessage("Sending data to the server...");
            this.sendingDataDialog.setCancelable(false);
            this.sendingDataDialog.show();

            SendAudioNoteToServerReciver reciver = new SendAudioNoteToServerReciver();
            reciver.setResponse(this);

            IntentFilter filter = new IntentFilter(SendAudioNoteToServer.SERVICEBROADCATSSIGN);
            LocalBroadcastManager.getInstance(this.context).registerReceiver (reciver, filter);

            Intent sendVoiceNoteDataIntent = new Intent(this.context, SendAudioNoteToServer.class);
            Gson gson = new Gson();
            sendVoiceNoteDataIntent.putExtra("audioNote", gson.toJson(audioNote, AudioNote.class));

            Type listType = new TypeToken<ArrayList<AudioTranslation>>(){}.getType();
            sendVoiceNoteDataIntent.putExtra("translations", gson.toJson(audioTranslations, listType));

            this.context.startService(sendVoiceNoteDataIntent);
        }

        @Override
        public void onDialogNegativeButtonClick(DialogInterface dialog) {

        }

        @Override
        public void OnServiceResponse(Intent data) {
            this.sendingDataDialog.dismiss();
            finish();
        }
    }

    private class OnSpinnerMainLanguageItemSelected implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0){
                return;
            }

            LanguageSpinnerAdaptor adaptor = (LanguageSpinnerAdaptor) parent.getAdapter();

            audioNote.setAudioMainLanguage((Language) adaptor.getItem(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class OnSpinnerRequestedTranslateToItemSelected implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0){
                return;
            }

            LanguageSpinnerAdaptor adaptor = (LanguageSpinnerAdaptor) parent.getAdapter();

            addRequestedTranslation((Language) adaptor.getItem(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
