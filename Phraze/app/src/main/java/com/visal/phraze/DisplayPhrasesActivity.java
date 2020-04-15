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
    Button clearSearchTextFieldButton;
    DatabaseHelper db;
    private ArrayList<Phrase> phrases;
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
//        sortPhrasesAlphabetically(phrasesInDB);
        clearSearchTextFieldButton = findViewById(R.id.display_phrases_clear_button);
        searchPhrasesTextField = findViewById(R.id.display_phrases_searchbar);
        allPhrases = phrasesInDB;
        phrases = db.getAllPhraseData();
        //accessing the Database
        //initializing RecyclerView
        phraseRecyclerView = findViewById(R.id.phrases_recyclerview);
        phraseRecyclerView.setHasFixedSize(true);
        phraseLayoutManager = new LinearLayoutManager(this);
        phraseRecyclerView.setLayoutManager(phraseLayoutManager);
        phraseAdapter = new DisplayPhrasesAdapter(phrases);
        phraseRecyclerView.setAdapter(phraseAdapter);

        //implementing swipe to delete functionality
        if (searchValue.equals("")) {
            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                //deleting card on swipe
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder target, int direction) {
                    int position = target.getAdapterPosition();
                    int phraseIdToDelete = phrases.get(position).id;
                   AlertDialogComponent.DeletePhraseAlert(DisplayPhrasesActivity.this, phrases, position, phraseIdToDelete, phraseAdapter);
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
                phrases = searchForPhrases(searchValue);
                //updating the recycler view
                Log.d(TAG, "onTextChanged: invoked");
                phraseAdapter = new DisplayPhrasesAdapter(phrases);
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
    }

    //method to search for phrases
    private ArrayList<Phrase> searchForPhrases(String value) {
        ArrayList<Phrase> results = new ArrayList<>();
        for (Phrase x : phrases) {
            if (x.phrase.contains(value)) {
                results.add(x);
            }
        }
//        sortPhrasesAlphabetically(results);
        return results;
    }

//    private void sortPhrasesAlphabetically(ArrayList<Phrase> phrases) {
//        Collections.sort(phrases);
//    }
}
