package com.mac.voiceprocesing.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mac.voiceprocesing.interfaces.iAlertDialogHelperButtonClick;

/**
 * Created by florin on 1/28/2015.
 */
public class AlertDialogHelper {

    private static AlertDialog.Builder dialogBuilder;

    public static void show(Context context, String title, String message, String okButtonTitle, String noButtonTitle, Boolean allowCancel, final iAlertDialogHelperButtonClick event){
        create(context, title, message, okButtonTitle, noButtonTitle, allowCancel, event);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public static void show(Context context, String title, String message, String okButtonTitle, String noButtonTitle, Boolean allowCancel, final iAlertDialogHelperButtonClick event, int layout){
        create(context, title, message, okButtonTitle, noButtonTitle, allowCancel, event);

        dialogBuilder.setView(layout);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private AlertDialogHelper(){
    }

    private static void create(Context context, String title, String message, String okButtonTitle, String noButtonTitle, Boolean allowCancel, final iAlertDialogHelperButtonClick event){
        if (dialogBuilder == null){
            dialogBuilder = new AlertDialog.Builder(context);
        }

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(allowCancel);

        dialogBuilder.setPositiveButton(okButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (event != null){
                    event.onDialogPositiveButtonClick(dialog);
                }
            }
        });

        if (noButtonTitle != null && !noButtonTitle.isEmpty()){
            dialogBuilder.setNegativeButton(noButtonTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (event != null) {
                        event.onDialogNegativeButtonClick(dialog);
                    }
                }
            });
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
