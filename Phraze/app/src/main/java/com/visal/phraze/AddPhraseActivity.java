package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;

public class AddPhraseActivity extends AppCompatActivity {
    private static final String TAG = AddPhraseActivity.class.getSimpleName();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrase);

//        instance of DatabaseHelper
        db = new DatabaseHelper(this);
//        accessing UI elements
        addPhraseEdittext = findViewById(R.id.add_phrase_edittext);
        saveDataButton = findViewById(R.id.save_data_button);
        cardViewHolder = findViewById(R.id.card_view_holder);
        toggleRecentlyAddedButton = findViewById(R.id.show_recently_added_button);
        collapeArrow = getResources().getDrawable(R.drawable.ic_expand_less_black_24dp);
        expandArrow = getResources().getDrawable(R.drawable.ic_expand_more_black_24dp);
        cardViewText1 = findViewById(R.id.recently_added_phrase_one);
        cardViewText2 = findViewById(R.id.recently_added_phrase_two);
        cardViewText3 = findViewById(R.id.recently_added_phrase_three);

//        getting data from the SQLite database
        recentlyAdded = db.getLastAddedPhrases();
        savedPhrases = db.getAllPhrases();
//        setting recently added cards
        setRecentlyAddedCards(recentlyAdded);


        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phrase = addPhraseEdittext.getText().toString();
                //if the text field is not empty, the data is stored in the database
                if (!phrase.equals("") && !savedPhrases.contains(phrase.toUpperCase())) {
                    boolean isDataInserted = db.insertPhrase(phrase);
                    savedPhrases.add(phrase.toUpperCase());
                    recentlyAdded = db.getLastAddedPhrases();
                    Toast.makeText(addPhraseEdittext.getContext(), "Phrase successfully saved", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: has the phrase been inserted? " + isDataInserted);
                } else if (savedPhrases.contains(phrase.toUpperCase())) {
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
            public void onClick(View v) {
                if (isRecentlyAddedDisplayed) {
                    cardViewHolder.setVisibility(View.VISIBLE);
                    cardViewHolder.animate()
                            .alpha(0f)
                            .setDuration(300);
                    cardViewHolder.setVisibility(View.GONE);
                    toggleRecentlyAddedButton.setCompoundDrawablesWithIntrinsicBounds(expandArrow, null, null, null);
                    toggleRecentlyAddedButton.setText("SHOW RECENTLY ADDED");
                    isRecentlyAddedDisplayed = false;
                } else {
                    cardViewHolder.setVisibility(View.VISIBLE);
                    cardViewHolder.animate()
                            .alpha(1f)
                            .setDuration(300);
                    toggleRecentlyAddedButton.setCompoundDrawablesWithIntrinsicBounds(collapeArrow, null, null, null);
                    toggleRecentlyAddedButton.setText("HIDE RECENTLY ADDED");
                    isRecentlyAddedDisplayed = true;
                }
            }
        });
    }

    public void setRecentlyAddedCards(ArrayList recentlyAdded) {
        if (savedPhrases.size() > 3){
                cardViewText1.setText(recentlyAdded.get(0).toString());
                cardViewText2.setText(recentlyAdded.get(1).toString());
                cardViewText3.setText(recentlyAdded.get(2).toString());
        }else{
            cardViewText1.setVisibility(View.GONE);
            cardViewText2.setVisibility(View.GONE);
            cardViewText3.setVisibility(View.GONE);
            toggleRecentlyAddedButton.setVisibility(View.GONE);
        }
    }
    //TODO: add life cyclemethod to update list on statechange
}
