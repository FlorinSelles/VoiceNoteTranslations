package com.mac.voiceprocesing.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.adaptors.AudioListAdaptor;
import com.mac.voiceprocesing.dataSourcers.AudioNotesTable;
import com.mac.voiceprocesing.dataSourcers.AudioTranslationsTable;
import com.mac.voiceprocesing.models.AudioNote;
import com.mac.voiceprocesing.models.AudioTranslation;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by florin on 2/13/2015.
 */
public class AddTranslationDialog extends DialogFragment {

    private AudioNote audioNote;
    private LinearLayout dialogView;
    private TextView voiceNoteName_tv;
    private Spinner language_sp;

    public AddTranslationDialog() {
    }

    public static AddTranslationDialog getNewInstance(int audioId){
        Bundle bundle = new Bundle();
        bundle.putInt("audioId", audioId);

        AddTranslationDialog dialog = new AddTranslationDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();

        if (bundle != null){
            this.audioNote = new AudioNotesTable(getActivity()).getElementById(bundle.getInt("audioId"));
        }else {
            return null;
        }

        this.loadComponents();

        this.initComponents();

        AlertDialog dialog = this.createDialog();
        dialog.setView(this.dialogView);

        return dialog;
    }

    private void loadComponents(){
        this.dialogView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.add_translation_dialog, null);
        this.voiceNoteName_tv = (TextView) this.dialogView.findViewById(R.id.voiceNoteName_tv);
        this.language_sp = (Spinner) this.dialogView.findViewById(R.id.language_sp);
    }

    private void initComponents(){
        this.voiceNoteName_tv.setText(this.voiceNoteName_tv.getText() + " " + this.audioNote.getAudioFileName());
        this.setLanguages(this.audioNote.getId());
    }

    private void setLanguages(int audioId){
        ArrayList<AudioTranslation> translations = new AudioTranslationsTable(getActivity()).getAudioTranslations(audioId);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        for(AudioTranslation item : translations){
            if (!item.fileExist()){
                adapter.add(item.getLanguageId().getLanguageName());
            }
        }

        this.language_sp.setAdapter(adapter);
    }

    private AlertDialog createDialog(){
        AlertDialog.Builder dialoBuilder = new AlertDialog.Builder(getActivity());
        dialoBuilder.setTitle(getResources().getString(R.string.addTranslationDialogTitle));

        dialoBuilder.setPositiveButton("Add", new OnPositiveButtonClick());
        dialoBuilder.setNegativeButton("Cancel", new OnNegativeButtonClick());

        return dialoBuilder.create();
    }

    private class OnPositiveButtonClick implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    }

    private class OnNegativeButtonClick implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    }
}
