package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.visal.phraze.helpers.DatabaseHelper;

import java.util.List;

public class DisplayPhrasesActivity extends AppCompatActivity {
    private static final String TAG = DisplayPhrasesActivity.class.getSimpleName();
    Button displayPhrasesButton;
    DatabaseHelper db;
    private List<CardDetails> cardDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phrases);
        displayPhrasesButton = findViewById(R.id.display_phrases_button);
        db = new DatabaseHelper(this);

        displayPhrasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor allPhrases = db.getAllPhraseData();
                if(allPhrases.getCount() == 0) {
                    Log.d(TAG, "onClick: returned 0 entries");
                    return;
                }

                StringBuilder buffer = new StringBuilder();
                while (allPhrases.moveToNext()) {
                    Log.d(TAG, "onClick: got values");
                    buffer.append("Id :").append(allPhrases.getString(0)).append("\n");
                    buffer.append("Phrase :").append(allPhrases.getString(1)).append("\n");
                }
                Log.d(TAG, "onClick: the data are as follows \n " + buffer.toString());
            }
        });
    }
}
