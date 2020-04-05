package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.visal.phraze.helpers.AccessibilityHelper;
import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;

public class TranslatePhraseActivity extends AppCompatActivity implements RecyclerViewRadioChangeListener {

    private static final String TAG = TranslatePhraseActivity.class.getSimpleName();
    Spinner languageSpinner;
    DatabaseHelper db;
    ArrayList<Language> subscribedLanguages;
    ArrayList<String> subscribedLanguagesNames;
    ArrayList<String> allPhrases;
    RecyclerView translationPhraseRecyclerView;
    RecyclerView.LayoutManager translatePhraseLayoutManager;
    RecyclerView.Adapter translatePhraseAdapter;
    TextView translatedTextView;
    Button translatePhraseButton;
    RelativeLayout progressLayout;
    ConstraintLayout layout;
    int selectedPhraseIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_phrase);
        languageSpinner = findViewById(R.id.subscribed_language_spinner);
        translationPhraseRecyclerView = findViewById(R.id.translate_phrase_recyclerview);
        translatedTextView = findViewById(R.id.translation_textview);
        translatePhraseButton = findViewById(R.id.translate_phrase_button);
        progressLayout = findViewById(R.id.phrase_translation_progressbar);
        layout = findViewById(R.id.translate_phrase_constraint_layout);
        progressLayout.setVisibility(View.GONE);
        translatedTextView.setVisibility(View.GONE);

        db = new DatabaseHelper(this);
        subscribedLanguages = new ArrayList<>();
        subscribedLanguagesNames = new ArrayList<>();
        allPhrases = new ArrayList<>();

        subscribedLanguages = db.getAllSubscriptions();
        allPhrases = db.getAllPhrases();
        subscribedLanguagesNames.add("Select a language");  //setting initial value for the spinner
        for (Language x : subscribedLanguages) {
            subscribedLanguagesNames.add(x.getName());
        }

        translationPhraseRecyclerView.setHasFixedSize(true);
        translatePhraseLayoutManager = new LinearLayoutManager(this);
        translationPhraseRecyclerView.setLayoutManager(translatePhraseLayoutManager);
        translatePhraseAdapter = new RadioRecyclerPhrasesAdapter(allPhrases, this);
        translationPhraseRecyclerView.setAdapter(translatePhraseAdapter);

        //setting values into the spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, subscribedLanguagesNames);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        languageSpinner.setAdapter(spinnerAdapter);

        translatePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSpinnerValue = languageSpinner.getSelectedItem().toString();
                if (!selectedSpinnerValue.equals("Select a language")) {
                    String abbreviation = "";
                    for (Language x : subscribedLanguages){
                        if (x.getName().equals(selectedSpinnerValue)){
                            abbreviation = x.getAbbreviation();
                        }
                    }
                    Log.d(TAG, "onClick: abbr is " + abbreviation);
                    new TranslationTask().execute(allPhrases.get(selectedPhraseIndex), abbreviation);
                    translationPhraseRecyclerView.setEnabled(false);
                    progressLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void recyclerViewRadioClick(View v, int position) {
        selectedPhraseIndex = position;
        Log.d(TAG, "recyclerViewRadioClick: " + selectedPhraseIndex);
    }

    public class TranslationTask extends AsyncTask<String, Void, String> {
        public AccessibilityHelper accessibilityHelper = new AccessibilityHelper();

        @Override
        protected String doInBackground(String... strings) {
            TranslateOptions translateOptions = new TranslateOptions.Builder()
                    .addText(strings[0])
                    .source(com.ibm.watson.language_translator.v3.util.Language.ENGLISH)
                    .target(strings[1])
                    .build();

            TranslationResult result = accessibilityHelper.getService().translate(translateOptions).execute().getResult();
            IdentifiableLanguages languages = accessibilityHelper.getService().listIdentifiableLanguages().execute().getResult();
            Log.d(TAG, "doInBackground: " + languages.getLanguages().size());
            return result.getTranslations().get(0).getTranslation();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            translatedTextView.setText(s);
            translationPhraseRecyclerView.setEnabled(false);
            progressLayout.setVisibility(View.GONE);
            translatedTextView.setVisibility(View.VISIBLE);
        }
    }
}
