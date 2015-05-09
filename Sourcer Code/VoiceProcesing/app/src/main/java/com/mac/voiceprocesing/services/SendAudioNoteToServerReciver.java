package com.mac.voiceprocesing.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mac.voiceprocesing.interfaces.iServiceResponse;

/**
 * Created by florin on 2/22/2015.
 */
public class SendAudioNoteToServerReciver extends BroadcastReceiver {
    private iServiceResponse response;

    public SendAudioNoteToServerReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.response.OnServiceResponse(intent);
    }

    public iServiceResponse getResponse() {
        return response;
    }

    public void setResponse(iServiceResponse response) {
        this.response = response;
    }
}
