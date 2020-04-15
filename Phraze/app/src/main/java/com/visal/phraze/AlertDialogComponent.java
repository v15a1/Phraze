package com.visal.phraze;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;

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

    public static void DeletePhraseAlert(final Context context, final ArrayList<Phrase> phrases, final int position, final int idToDelete, final RecyclerView.Adapter adapter) {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);

        //setting a customized alert title
        alertDialogBuilder
                .setMessage("Do you want to delete the phrase \"" + phrases.get(position) + "\"")
                .setCancelable(false)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        phrases.remove(position);
                        DatabaseHelper db = new DatabaseHelper(context);
                        db.deletePhrase(idToDelete);
                        adapter.notifyDataSetChanged();
                    }
                });
        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();     //showing the alert
    }


}
