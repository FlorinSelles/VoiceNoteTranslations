package com.mac.voiceprocesing.adaptors;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.models.Language;

import java.util.ArrayList;

/**
 * Created by florin on 2/6/2015.
 */
public class LanguageSpinnerAdaptor extends BaseAdapter{

    private ArrayList<Language> dataSet;

    public LanguageSpinnerAdaptor(ArrayList<Language> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public int getCount() {
        return this.dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return this.dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.dataSet.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text = new TextView(parent.getContext());

        text.setText(this.dataSet.get(position).getLanguageName());
        text.setTextSize(18);

        return text;
    }
}
