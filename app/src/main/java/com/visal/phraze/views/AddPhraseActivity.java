package com.visal.phraze.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.visal.phraze.viewmodels.AlertDialogComponent;
import com.visal.phraze.R;
import com.visal.phraze.viewmodels.DatabaseHelper;

import java.util.ArrayList;

public class AddPhraseActivity extends AppCompatActivity {
    private static final String TAG = AddPhraseActivity.class.getSimpleName();
    private static final String TITLE = "Add Phrases";

    EditText addPhraseEdittext;
    Button saveDataButton;
    Button toggleRecentlyAddedButton;
    DatabaseHelper db;
    LinearLayout cardViewHolder;
    boolean isRecentlyAddedDisplayed;
    Drawable expandArrow;
    Drawable collapeArrow;
    ArrayList recentlyAdded;
    ArrayList savedPhrases;
    TextView cardViewText1;
    TextView cardViewText2;
    TextView cardViewText3;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sharedPrefFile = "com.visal.phraze";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrase);

        //instance of DatabaseHelper
        db = new DatabaseHelper(this);
        //accessing UI elements
        addPhraseEdittext = findViewById(R.id.add_phrase_edittext);
        saveDataButton = findViewById(R.id.save_data_button);
        cardViewHolder = findViewById(R.id.card_view_holder);
        toggleRecentlyAddedButton = findViewById(R.id.show_recently_added_button);
        collapeArrow = getResources().getDrawable(R.drawable.ic_expand_less_black_24dp);
        expandArrow = getResources().getDrawable(R.drawable.ic_expand_more_black_24dp);
        cardViewText1 = findViewById(R.id.recently_added_phrase_one);
        cardViewText2 = findViewById(R.id.recently_added_phrase_two);
        cardViewText3 = findViewById(R.id.recently_added_phrase_three);

        //displaying the actionbat and setting the title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TITLE);
        }

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        isRecentlyAddedDisplayed = sharedPreferences.getBoolean("showRecentlyAdded", false);
        //getting data from the SQLite database
        recentlyAdded = db.getLastAddedPhrases();
        savedPhrases = db.getAllPhrases();
        //setting recently added cards
        setRecentlyAddedCards(recentlyAdded);
        editor = sharedPreferences.edit();
        isRecentlyAddedDisplayed = sharedPreferences.getBoolean("isRecentlyAddedDisplayed", false);
        setCardViewState();

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phrase = addPhraseEdittext.getText().toString();
                //if the text field is not empty, the data is stored in the database
                if (!phrase.equals("") && !savedPhrases.contains(phrase.toUpperCase())) {
                    //adding phrase to the DB
                    boolean isDataInserted = db.insertPhrase(phrase);
                    savedPhrases.add(phrase.toUpperCase());
                    recentlyAdded = db.getLastAddedPhrases();
                    //invoking method to set recently added phrases
                    setRecentlyAddedCards(db.getLastAddedPhrases());
                    //Snackbars to notify the user
                    Snackbar.make(v, "Item added successful.", Snackbar.LENGTH_SHORT).show();
                } else if (savedPhrases.contains(phrase.toUpperCase())) {
                    //displaying custom alert dialogs to the user
                    if (phrase.equals("")) {
                        AlertDialogComponent.basicAlert(AddPhraseActivity.this, "Please enter a phrase to save.");
                    } else {
                        AlertDialogComponent.basicAlert(AddPhraseActivity.this, "The entered phrase is already in the database. Please enter a different phrase.");
                    }
                }
            }
        });

        //toggling the visibility of the cardview and changing the icons
        toggleRecentlyAddedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                setCardViewState();
            }
        });
    }

    //method to set recently added in the UI
    public void setRecentlyAddedCards(ArrayList recentlyAdded) {
        if (savedPhrases.size() > 3) {
            cardViewText1.setText(recentlyAdded.get(0).toString());
            cardViewText2.setText(recentlyAdded.get(1).toString());
            cardViewText3.setText(recentlyAdded.get(2).toString());
        } else {
            cardViewText1.setVisibility(View.GONE);
            cardViewText2.setVisibility(View.GONE);
            cardViewText3.setVisibility(View.GONE);
            toggleRecentlyAddedButton.setVisibility(View.GONE);
        }
    }

    private void setCardViewState() {
        if (isRecentlyAddedDisplayed) {
            //animations
            cardViewHolder.setVisibility(View.VISIBLE);
            cardViewHolder.animate()
                    .alpha(0f)
                    .setDuration(300);
            cardViewHolder.setVisibility(View.GONE);
            toggleRecentlyAddedButton.setCompoundDrawablesWithIntrinsicBounds(expandArrow, null, null, null);
            toggleRecentlyAddedButton.setText("SHOW RECENTLY ADDED");
            isRecentlyAddedDisplayed = false;
        } else {
            //animations
            cardViewHolder.setVisibility(View.VISIBLE);
            cardViewHolder.animate()
                    .alpha(1f)
                    .setDuration(300);
            toggleRecentlyAddedButton.setCompoundDrawablesWithIntrinsicBounds(collapeArrow, null, null, null);
            toggleRecentlyAddedButton.setText("HIDE RECENTLY ADDED");
            isRecentlyAddedDisplayed = true;
        }
        editor.putBoolean("isRecentlyAddedDisplayed", isRecentlyAddedDisplayed);
        editor.apply();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //method to change activities
            startActivity(new Intent(AddPhraseActivity.this, MainActivity.class));
            finish();       //method call to destroy the activity from the memory
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
