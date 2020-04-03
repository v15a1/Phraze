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

        editPhraseButton = findViewById(R.id.edit_phrase_button);
        savePhraseButton = findViewById(R.id.save_edited_phrase_button);
        phraseValue = findViewById(R.id.edit_phrase_edittext);
        phraseValue.setEnabled(false);

        editPhraseRecyclerView = findViewById(R.id.edit_phrases_recycler_view);
        editPhraseRecyclerView.setHasFixedSize(true);
        editPhraseLayoutManager = new LinearLayoutManager(this);
        editPhraseRecyclerView.setLayoutManager(editPhraseLayoutManager);
        editPhraseAdapter = new EditPhrasesAdapter(allPhrases, this);
        editPhraseRecyclerView.setAdapter(editPhraseAdapter);


        editPhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPhraseIndex >=0){
                    phraseValue.setEnabled(true);
                    phraseValue.setHint(phrasesInDB.get(selectedPhraseIndex));
                }
            }
        });

        savePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPhraseText = phraseValue.getText().toString();
                allPhrases.set(selectedPhraseIndex, newPhraseText);
                editPhraseAdapter.notifyDataSetChanged();
            }
        });
    }

    //implementing an interface to get he index of the selected radiobutton
    @Override
    public void recyclerViewRadioClick(View v, int position) {
        selectedPhraseIndex = position;

    }
}
