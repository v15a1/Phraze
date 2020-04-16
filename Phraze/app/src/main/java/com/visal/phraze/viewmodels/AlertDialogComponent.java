package com.visal.phraze.viewmodels;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.visal.phraze.model.Phrase;

import java.util.ArrayList;

//method to invoke and display custom Alert Dialogs
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

    public static void DeletePhraseAlert(final Context context, final ArrayList<Phrase> phrases, final int position, final int idToDelete, final RecyclerView.Adapter adapter, final View view) {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);

        //setting a customized alert title
        alertDialogBuilder
                .setMessage("Do you want to delete the phrase \"" + phrases.get(position).getPhrase() + "\"")
                .setCancelable(false)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //deleting an item on swipe
                        phrases.remove(position);
                        DatabaseHelper db = new DatabaseHelper(context);
                        //deleting item from the database
                        boolean isDeleted = db.deletePhrase(idToDelete);
                        adapter.notifyDataSetChanged();
                        //notifying user if successful deletion
                        if (isDeleted) {
                            Snackbar.make(view, "Phrase successfully deleted.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(view, "Phrase was NOT deleted.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();     //showing the alert
    }


}
