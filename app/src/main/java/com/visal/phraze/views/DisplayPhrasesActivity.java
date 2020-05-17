package com.visal.phraze.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.visal.phraze.viewmodels.AlertDialogComponent;
import com.visal.phraze.viewmodels.adapters.DisplayPhrasesAdapter;
import com.visal.phraze.R;
import com.visal.phraze.viewmodels.DatabaseHelper;
import com.visal.phraze.model.Phrase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DisplayPhrasesActivity extends AppCompatActivity {
    private static final String TAG = DisplayPhrasesActivity.class.getSimpleName();
    private static final String TITLE = "Show Phrases";
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
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phrases);
        db = new DatabaseHelper(this);
        phrasesInDB = db.getAllPhrases();
        clearSearchTextFieldButton = findViewById(R.id.display_phrases_clear_button);
        searchPhrasesTextField = findViewById(R.id.display_phrases_searchbar);
        layout = findViewById(R.id.display_layout);
        //accessing the Database
        allPhrases = phrasesInDB;
        phrases = db.getAllPhraseData();
        //sorting the already retrieved Phrase arraylist to alphabetical order
        Collections.sort(phrases, new SortAlphabetically());

        //initializing RecyclerView
        phraseRecyclerView = findViewById(R.id.phrases_recyclerview);
        phraseRecyclerView.setHasFixedSize(true);
        phraseLayoutManager = new LinearLayoutManager(this);
        phraseRecyclerView.setLayoutManager(phraseLayoutManager);
        phraseAdapter = new DisplayPhrasesAdapter(phrases);
        phraseRecyclerView.setAdapter(phraseAdapter);

        //displaying the actionbat and setting the title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TITLE);
        }

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
                    int phraseIdToDelete = phrases.get(position).getId();
                    AlertDialogComponent.DeletePhraseAlert(DisplayPhrasesActivity.this, phrases, position, phraseIdToDelete, phraseAdapter, layout);
                    phraseAdapter.notifyDataSetChanged();
                }
            });
            touchHelper.attachToRecyclerView(phraseRecyclerView);
        }

        //method used for searching for phrases by updating the phrases onTextChange
        searchPhrasesTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchValue = s.toString().toUpperCase();
                phrases = db.getAllPhraseData();
                phrases = searchForPhrases(searchValue);
                //updating the recycler view
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
            if (x.getPhrase().toUpperCase().contains(value)) {
                results.add(x);
            }
        }
        //sorting the searched results
        Collections.sort(phrases, new SortAlphabetically());
        return results;
    }

    //class which implements the Comparator interface to sort the arraylist of Phrases
    class SortAlphabetically implements Comparator<Phrase> {
        public int compare(Phrase a, Phrase b) {
            return a.getPhrase().compareTo(b.getPhrase());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //method to change activities
            startActivity(new Intent(DisplayPhrasesActivity.this, MainActivity.class));
            finish();       //method call to destroy the activity from the memory
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
