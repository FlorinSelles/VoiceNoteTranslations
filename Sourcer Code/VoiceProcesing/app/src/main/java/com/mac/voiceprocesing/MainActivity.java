package com.mac.voiceprocesing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mac.voiceprocesing.adaptors.AudioListAdaptor;
import com.mac.voiceprocesing.controllers.AddAudioActivity;
import com.mac.voiceprocesing.controllers.AudioDetailActivity;
import com.mac.voiceprocesing.customViews.MiniAudioControl;
import com.mac.voiceprocesing.dataSourcers.AudioNotesTable;
import com.mac.voiceprocesing.dataSourcers.AudioTranslationsTable;
import com.mac.voiceprocesing.dataSourcers.LanguagesTable;
import com.mac.voiceprocesing.helper.AlertDialogHelper;
import com.mac.voiceprocesing.interfaces.iServiceResponse;
import com.mac.voiceprocesing.models.AudioListItem;
import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.AudioTranslation;
import com.mac.voiceprocesing.models.Language;
import com.mac.voiceprocesing.models.UpdateDbServiceConstants;
import com.mac.voiceprocesing.services.UpdateDBSReciver;
import com.mac.voiceprocesing.services.UpdateDBService;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements iServiceResponse{

    private ListView audio_listView;

    private ProgressDialog updateDBDialog;

    private View lastViewSelected;
    private int lastElementPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.audio_listView = (ListView) this.findViewById(R.id.audio_listView);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_listview, null);
        this.addContentView(emptyView, this.audio_listView.getLayoutParams());
        this.audio_listView.setEmptyView(emptyView);

        ArrayList<AudioListItem> listItems = this.getAudioListItem();

        AudioListAdaptor adaptor = new AudioListAdaptor(listItems);
        this.audio_listView.setAdapter(adaptor);
        this.audio_listView.setOnItemClickListener(new OnVoiceNoteItemSelected());

        this.showNumberOfFiles(listItems == null ? 0 : listItems.size());
        this.showNumberOfTranslations(listItems);

        Button btn_addAudio = (Button) this.findViewById(R.id.btn_addAudio);
        btn_addAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddAudioActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        String value = "";
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_delete:
                value = "delete";
                break;
            case R.id.action_update:
                loadUpdateDBService();
                value = "update";
                break;
            default:break;
        }

        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();

        this.loadElements();
    }

    private void loadElements(){
        ArrayList<AudioListItem> listItems = this.getAudioListItem();

        AudioListAdaptor adaptor = new AudioListAdaptor(listItems);
        this.audio_listView.setAdapter(adaptor);

        this.showNumberOfFiles(listItems == null ? 0 : listItems.size());
        this.showNumberOfTranslations(listItems);
    }

    private void showNumberOfFiles(int elements){
        TextView text = (TextView) this.findViewById(R.id.numberOfElement_tv);

        text.setText(this.getResources().getString(R.string.amountOfaudioFiles).replace("#", String.valueOf(elements)));
    }

    private void showNumberOfTranslations(ArrayList<AudioListItem> listItems){
        int transAmount = 0;
        int transAmountFile = 0;

        if (listItems != null){
            for(AudioListItem item : listItems){
                transAmount += item.getAudioLanguages().size();

                for(AudioTranslation fileItem : item.getAudioLanguages()){
                    if (fileItem.fileExist()){
                        transAmountFile += 1;
                    }
                }
            }
        }

        TextView text = (TextView) this.findViewById(R.id.numberOfTranslations_tv);
        text.setText(this.getResources().getString(R.string.amountOfaudioFilesWithTranslationg).replace("#", transAmountFile + "/" + transAmount));
    }

    private ArrayList<AudioListItem> getAudioListItem(){
        ArrayList<AudioNote> audioNotes = new AudioNotesTable(this).getAllElements();

        if (audioNotes == null || audioNotes.size() == 0){
            return null;
        }

        ArrayList<AudioListItem> listItems = new ArrayList<>();

        for(AudioNote item : audioNotes){
            AudioListItem audioListItem = new AudioListItem();
            audioListItem.setAudio(item);

            audioListItem.setAudioLanguages(new AudioTranslationsTable(this).getAudioTranslations(item.getId()));

            listItems.add(audioListItem);
        }

        return listItems;
    }

    private class OnVoiceNoteItemSelected implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MiniAudioControl miniAudioControl = (MiniAudioControl) view;

            AudioListItem data = miniAudioControl.getAudioListItem();
            if (!data.getAudio().fileExist()){
                AlertDialogHelper.show(view.getContext(), view.getContext().getResources().getString(R.string.app_name), view.getContext().getResources().getString(R.string.noAudioFileAsociated), "OK", null, true, null);
                return;
            }

            Intent intent = new Intent(view.getContext(), AudioDetailActivity.class);
            intent.putExtra("id", data.getAudio().getId());
            startActivity(intent);
        }
    }

    private void loadUpdateDBService(){
        this.updateDBDialog = new ProgressDialog(this);
        this.updateDBDialog.setTitle(this.getResources().getString(R.string.app_name));
        this.updateDBDialog.setMessage("Download data from server...");
        this.updateDBDialog.setCancelable(false);
        this.updateDBDialog.show();

        UpdateDBSReciver reciver = new UpdateDBSReciver();
        reciver.setResponse(this);

        IntentFilter filter = new IntentFilter(UpdateDbServiceConstants.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver (reciver, filter);

        Intent updateDBIntent = new Intent(this, UpdateDBService.class);
        this.startService(updateDBIntent);
    }

    @Override
    public void OnServiceResponse(Intent data) {
        this.updateDBDialog.dismiss();

        ArrayList<AudioTranslation> translations = new AudioTranslationsTable(this).getAllElements();

       // AlertDialogHelper.show(this, this.getResources().getString(R.string.app_name), data.getExtras().getStringArray(UpdateDbServiceConstants.EXTENDED_DATA_STATUS)[0], "OK", null, true, null);

        loadElements();
    }
}
