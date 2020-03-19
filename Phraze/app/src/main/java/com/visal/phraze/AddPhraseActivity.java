package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.visal.phraze.helpers.DatabaseHelper;

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
    CardView cardView1;
    CardView cardView2;
    CardView cardView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrase);

        addPhraseEdittext = findViewById(R.id.add_phrase_edittext);
        saveDataButton = findViewById(R.id.save_data_button);
        cardViewHolder = findViewById(R.id.card_view_holder);
        toggleRecentlyAddedButton = findViewById(R.id.show_recently_added_button);
        collapeArrow = getResources().getDrawable(R.drawable.ic_expand_less_black_24dp);
        expandArrow = getResources().getDrawable(R.drawable.ic_expand_more_black_24dp);

        db = new DatabaseHelper(this);

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phrase = addPhraseEdittext.getText().toString();
                //if the text field is not empty, the data is stored in the database
                if (!phrase.equals("")){
                    boolean isDataInserted = db.insertPhrase(phrase);
                    Log.d(TAG, "onClick: has the phrase been inserted? " + isDataInserted);
                }else {
                    AlertDialogComponent.basicAlert(AddPhraseActivity.this, "Please enter a phrase to save");
                }
            }
        });

        //toggling the visibility of the cardview and changing the icons
        toggleRecentlyAddedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecentlyAddedDisplayed){
                    cardViewHolder.setVisibility(View.GONE);
                    toggleRecentlyAddedButton.setCompoundDrawablesWithIntrinsicBounds(expandArrow, null, null, null);
                    isRecentlyAddedDisplayed = false;
                }else{
                    cardViewHolder.setVisibility(View.VISIBLE);
                    toggleRecentlyAddedButton.setCompoundDrawablesWithIntrinsicBounds(collapeArrow, null, null, null);
                    isRecentlyAddedDisplayed = true;
                }
            }
        });
    }
}
