package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class DisplayPhrasesActivity extends AppCompatActivity {
    private static final String TAG = DisplayPhrasesActivity.class.getSimpleName();
    Button displayPhrasesButton;
    DatabaseHelper db;
    private List<CardDetails> cardDetails;
    private ArrayList allPhrases;
    ListView phraseListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phrases);
        displayPhrasesButton = findViewById(R.id.display_phrases_button);
        db = new DatabaseHelper(this);
        allPhrases = db.getAllPhrases();
//        recyclerView = findViewById(R.id.all_phrase_recyclerview);
        phraseListView = findViewById(R.id.phrases_listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_item, allPhrases);
        phraseListView.setAdapter(arrayAdapter);


        List<CardDetails> cardDetailsList = new ArrayList<>();
        for (int i = 0; i < allPhrases.size(); i++){
            cardDetailsList.add(new CardDetails("a", i));
        }

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
