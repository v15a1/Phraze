package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.visal.phraze.helpers.DatabaseHelper;

public class DisplayPhrasesActivity extends AppCompatActivity {
    private static final String TAG = DisplayPhrasesActivity.class.getSimpleName();
    Button displayPhrasesButton;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phrases);
        displayPhrasesButton = findViewById(R.id.display_phrases_button);
        db = new DatabaseHelper(this);

        displayPhrasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor allPhrases = db.getAllPhrases();
                if(allPhrases.getCount() == 0) {
                    // show message
                    Log.d(TAG, "onClick: ");
                    return;
                }

                StringBuilder buffer = new StringBuilder();
                while (allPhrases.moveToNext()) {
                    buffer.append("Id :").append(allPhrases.getString(0)).append("\n");
                    buffer.append("Phrase :").append(allPhrases.getString(1)).append("\n");
                }
                Log.d(TAG, "onClick: the data are as follows \n " + buffer.toString());
            }
        });
    }
}
