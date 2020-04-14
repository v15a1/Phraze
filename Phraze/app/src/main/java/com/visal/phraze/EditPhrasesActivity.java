package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;

public class EditPhrasesActivity extends AppCompatActivity implements RecyclerViewRadioChangeListener {

    private static final String TAG = EditPhrasesActivity.class.getSimpleName();

    DatabaseHelper db;
    private ArrayList<String> phrasesInDB;
    RecyclerView editPhraseRecyclerView;
    RecyclerView.Adapter editPhraseAdapter;
    RecyclerView.LayoutManager editPhraseLayoutManager;
    ArrayList<String> allPhrases;
    ArrayList<Phrase> phrases;
    int selectedPhraseIndex;
    Button editPhraseButton;
    Button savePhraseButton;
    EditText phraseValue;
    String newPhraseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrases);
        db = new DatabaseHelper(this);
        phrasesInDB = db.getAllPhrases();
        allPhrases = phrasesInDB;
        phrases = db.getAllPhraseData();

        editPhraseButton = findViewById(R.id.edit_phrase_button);
        savePhraseButton = findViewById(R.id.save_edited_phrase_button);
        phraseValue = findViewById(R.id.edit_phrase_edittext);
        phraseValue.setEnabled(false);

        //edit activity recycler view
        editPhraseRecyclerView = findViewById(R.id.edit_phrases_recycler_view);
        editPhraseRecyclerView.setHasFixedSize(true);
        editPhraseLayoutManager = new LinearLayoutManager(this);
        editPhraseRecyclerView.setLayoutManager(editPhraseLayoutManager);
        editPhraseAdapter = new RadioRecyclerPhrasesAdapter(phrases, this);
        editPhraseRecyclerView.setAdapter(editPhraseAdapter);

        //onclick listener to get the selected item and enable editting
        editPhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPhraseIndex >=0){
                    phraseValue.setEnabled(true);
                    phraseValue.setHint(phrasesInDB.get(selectedPhraseIndex));
                }
            }
        });

        //saving the data in the database and the retrieved list
        //https://medium.com/@suragch/updating-data-in-an-android-recyclerview-842e56adbfd8
        savePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPhraseText = phraseValue.getText().toString();
                allPhrases.set(selectedPhraseIndex, newPhraseText);
                editPhraseAdapter.notifyDataSetChanged();
                boolean updated = db.updateData(selectedPhraseIndex + 1, newPhraseText );
                //log statements to check success of the updating query
                if (updated){
                    Log.d(TAG, "onClick: Successfully updated fields");
                }else{
                    Log.d(TAG, "onClick: unsuccessfull update of fields");
                }
            }
        });
    }

    //implementing an interface to get he index of the selected radiobutton
    @Override
    public void recyclerViewRadioClick(View v, int position) {
        selectedPhraseIndex = position;
        Log.d(TAG, "recyclerViewRadioClick: position is " + position);
    }
}
