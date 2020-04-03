package com.visal.phraze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayPhrasesActivity extends AppCompatActivity {
    private static final String TAG = DisplayPhrasesActivity.class.getSimpleName();
    Button displayPhrasesButton;
    Button clearSearchTextFieldButton;
    DatabaseHelper db;
    private List<CardDetails> cardDetails;
    private ArrayList<String> phrasesInDB;
    RecyclerView phraseRecyclerView;
    RecyclerView.Adapter phraseAdapter;
    RecyclerView.LayoutManager phraseLayoutManager;
    private EditText searchPhrasesTextField;
    String searchValue = "";
    ArrayList<String> allPhrases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phrases);
        db = new DatabaseHelper(this);
        phrasesInDB = db.getAllPhrases();
        sortPhrasesAlphabetically(phrasesInDB);
        displayPhrasesButton = findViewById(R.id.display_phrases_button);
        clearSearchTextFieldButton = findViewById(R.id.display_phrases_clear_button);
        searchPhrasesTextField = findViewById(R.id.display_phrases_searchbar);
        allPhrases = phrasesInDB;
        //accessing the Database
        //initializing RecyclerView
        phraseRecyclerView = findViewById(R.id.phrases_recyclerview);
        phraseRecyclerView.setHasFixedSize(true);
        phraseLayoutManager = new LinearLayoutManager(this);
        phraseRecyclerView.setLayoutManager(phraseLayoutManager);
        phraseAdapter = new DisplayPhrasesAdapter(allPhrases);
        phraseRecyclerView.setAdapter(phraseAdapter);

        //implementing swipe to delete functionality
        if (searchValue.equals("")){
            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                //deleting card on swipe
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder target, int direction) {
                    int position = target.getAdapterPosition();
                    Log.d(TAG, "onSwiped: The node to delete is " + position);
                    phrasesInDB.remove(position);
                    phraseAdapter.notifyDataSetChanged();
                }
            });
            touchHelper.attachToRecyclerView(phraseRecyclerView);
        }

        searchPhrasesTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchValue = s.toString().toUpperCase();
                allPhrases = searchForPhrases(searchValue);
                //updating the recycler view
                Log.d(TAG, "onTextChanged: invoked");
                phraseAdapter = new DisplayPhrasesAdapter(allPhrases);
                phraseRecyclerView.setAdapter(phraseAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearSearchTextFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPhrasesTextField.setText("");
            }
        });

        List<CardDetails> cardDetailsList = new ArrayList<>();
        for (int i = 0; i < phrasesInDB.size(); i++) {
            cardDetailsList.add(new CardDetails("a", i));
        }

        displayPhrasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor allPhrases = db.getAllPhraseData();
                if (allPhrases.getCount() == 0) {
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

    //method to search for phrases
    private ArrayList<String> searchForPhrases(String value) {
        ArrayList<String> results = new ArrayList<>();
        for (String x : phrasesInDB) {
            if (x.contains(value)) {
                results.add(x);
            }
        }
        sortPhrasesAlphabetically(results);
        return results;
    }

    private void sortPhrasesAlphabetically(ArrayList<String> phrases) {
        Collections.sort(phrases);
    }
}
