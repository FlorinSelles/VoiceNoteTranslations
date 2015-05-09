package com.mac.voiceprocesing.adaptors;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.mac.voiceprocesing.R;
import com.mac.voiceprocesing.customViews.MiniAudioControl;
import com.mac.voiceprocesing.models.AudioListItem;
import com.mac.voiceprocesing.models.AudioNote;

import java.util.ArrayList;

/**
 * Created by florin on 1/28/2015.
 */
public class AudioListAdaptor extends BaseAdapter {

    private ArrayList<AudioListItem> audioList;

    public AudioListAdaptor(ArrayList<AudioListItem> audioList) {
        this.audioList = audioList;
    }

    @Override
    public int getCount() {
        return this.audioList != null ? this.audioList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return this.audioList != null ? this.audioList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return this.audioList != null ? this.audioList.get(position).getAudio().getId() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MiniAudioControl view = new MiniAudioControl(parent.getContext());
        view.setControlId(position);
        view.setAudioListItem(this.audioList.get(position));
/*
        try {
            Animation anim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_in_left);

            view.startAnimation(anim);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
*/
        notifyDataSetChanged();

        return view;

    }
}
