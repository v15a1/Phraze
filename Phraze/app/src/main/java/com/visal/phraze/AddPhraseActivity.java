package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.visal.phraze.helpers.DatabaseHelper;

public class AddPhraseActivity extends AppCompatActivity {
    private static final String TAG = AddPhraseActivity.class.getSimpleName();

    EditText addPhraseEdittext;
    Button saveDataButton;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrase);

        addPhraseEdittext = findViewById(R.id.add_phrase_edittext);
        saveDataButton = findViewById(R.id.save_data_button);
        db = new DatabaseHelper(this);

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phrase = addPhraseEdittext.getText().toString();
                //if the text field is not empty, the data is stored in the database
                if (!phrase.equals("")){
                    Log.d(TAG, "onClick: The entered phrase is - \'" + phrase + "\'");
                    boolean isDataInserted = db.insertPhrase(phrase);
                    Log.d(TAG, "onClick: has the phrase been inserted? " + isDataInserted);
                }else {
                    Log.d(TAG, "onClick: enter something into the text field");
                }
            }
        });
    }
}
