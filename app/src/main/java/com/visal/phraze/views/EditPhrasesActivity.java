package com.visal.phraze.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.visal.phraze.R;
import com.visal.phraze.viewmodels.adapters.RadioRecyclerPhrasesAdapter;
import com.visal.phraze.viewmodels.interfaces.RecyclerViewRadioChangeListener;
import com.visal.phraze.viewmodels.DatabaseHelper;
import com.visal.phraze.model.Phrase;

import java.util.ArrayList;

public class EditPhrasesActivity extends AppCompatActivity implements RecyclerViewRadioChangeListener {

    private static final String TAG = EditPhrasesActivity.class.getSimpleName();
    private static final String TITLE = "Edit Phrases";

    DatabaseHelper db;
    private ArrayList<String> phrasesInDB;
    RecyclerView editPhraseRecyclerView;
    RecyclerView.Adapter editPhraseAdapter;
    RecyclerView.LayoutManager editPhraseLayoutManager;
    ArrayList<String> allPhrases;
    ArrayList<Phrase> phrases;
    int selectedPhraseIndex = -1;
    Button editPhraseButton;
    Button savePhraseButton;
    ImageButton cancelEditButton;
    EditText phraseValue;
    String newPhraseText;
    ConstraintLayout layout;
    LinearLayout editTextLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrases);
        db = new DatabaseHelper(this);
        //getting phrases
        phrasesInDB = db.getAllPhrases();
        allPhrases = phrasesInDB;
        phrases = db.getAllPhraseData();

        //accessing UI views
        editPhraseButton = findViewById(R.id.edit_phrase_button);
        savePhraseButton = findViewById(R.id.save_edited_phrase_button);
        cancelEditButton = findViewById(R.id.cancel_edit);
        phraseValue = findViewById(R.id.edit_phrase_edittext);
        layout = findViewById(R.id.edit_phrase_layout);
        editTextLayout = findViewById(R.id.edit_phrase_edittext_layout);

        //setting states of views
        savePhraseButton.setEnabled(false);
        savePhraseButton.setTextColor(getResources().getColor(R.color.darkGrey));
        editTextLayout.setVisibility(View.GONE);
        editPhraseButton.setEnabled(false);
        editPhraseButton.setTextColor(getResources().getColor(R.color.darkGrey));

        //edit activity recycler view
        editPhraseRecyclerView = findViewById(R.id.edit_phrases_recycler_view);
        editPhraseRecyclerView.setHasFixedSize(true);
        editPhraseLayoutManager = new LinearLayoutManager(this);
        editPhraseRecyclerView.setLayoutManager(editPhraseLayoutManager);
        editPhraseAdapter = new RadioRecyclerPhrasesAdapter(phrases, this);
        editPhraseRecyclerView.setAdapter(editPhraseAdapter);

        //displaying the actionbat and setting the title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TITLE);
        }

        //onclick listener to get the selected item and enable editting
        editPhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPhraseIndex >=0){
                    editTextLayout.setAlpha(0f);
                    editTextLayout.setVisibility(View.VISIBLE);
                    editTextLayout.animate()
                            .alpha(1f)
                            .setDuration(300);
                    phraseValue.setText(phrases.get(selectedPhraseIndex).getPhrase());
                    savePhraseButton.setEnabled(true);
                    savePhraseButton.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });

        //method to cancel editing
        cancelEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextLayout.animate()
                        .alpha(0f)
                        .setDuration(300);
                editTextLayout.setVisibility(View.GONE);
                savePhraseButton.setEnabled(false);
                savePhraseButton.setTextColor(getResources().getColor(R.color.darkGrey));
                phraseValue.setText("");
            }
        });

        //saving the data in the database and the retrieved list
        //https://medium.com/@suragch/updating-data-in-an-android-recyclerview-842e56adbfd8
        savePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPhraseText = phraseValue.getText().toString();
                phrases.get(selectedPhraseIndex).setPhrase(newPhraseText);
                int updateId = phrases.get(selectedPhraseIndex).getId();
                editPhraseAdapter.notifyDataSetChanged();
                //updating the DB
                boolean updated = db.updateData(updateId , newPhraseText );
                //Snackbars to notify user of success of the updating query
                if (updated){
                    Snackbar.make(layout, "Successfully updated.", Snackbar.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(layout, "Could not Update field", Snackbar.LENGTH_SHORT).show();
                }
                editTextLayout.setVisibility(View.GONE);
                savePhraseButton.setEnabled(false);
                savePhraseButton.setTextColor(getResources().getColor(R.color.darkGrey));
            }
        });
    }

    //implementing an interface to get he index of the selected radiobutton
    @Override
    public void recyclerViewRadioClick(View v, int position) {
        selectedPhraseIndex = position;
        Log.d(TAG, "recyclerViewRadioClick: position is " + selectedPhraseIndex);
        if (position >=0){
            editPhraseButton.setEnabled(true);
            editPhraseButton.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //method to change activities
            startActivity(new Intent(EditPhrasesActivity.this, MainActivity.class));
            finish();       //method call to destroy the activity from the memory
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
