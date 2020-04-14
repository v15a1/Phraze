package com.visal.phraze;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

public class AlertDialogComponent {
    //static method declared to invoke an alert dialog to display to the user
    public static void basicAlert(Context context, String message) {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);

        //setting a customized alert title
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();     //showing the alert
    }
}
