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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
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
    LinearLayout translationDisplayLayout;
    ImageButton playTextToSpeechButton;
    ProgressBar voiceProgress;
    int selectedPhraseIndex;
    private TextToSpeech textService;
    private StreamPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_phrase);
        languageSpinner = findViewById(R.id.subscribed_language_spinner);
        translationPhraseRecyclerView = findViewById(R.id.translate_phrase_recyclerview);
        translatedTextView = findViewById(R.id.translation_textview);
        translatePhraseButton = findViewById(R.id.translate_phrase_button);
        progressLayout = findViewById(R.id.phrase_translation_progressbar);
        translationDisplayLayout = findViewById(R.id.translation_display_layout);
        playTextToSpeechButton = findViewById(R.id.play_text_to_speech);
        voiceProgress = findViewById(R.id.voice_progress);

        progressLayout.setVisibility(View.GONE);
        translationDisplayLayout.setVisibility(View.GONE);
        voiceProgress.setVisibility(View.GONE);

        db = new DatabaseHelper(this);
        subscribedLanguages = new ArrayList<>();
        subscribedLanguagesNames = new ArrayList<>();
        allPhrases = new ArrayList<>();
        textService = initTextToSpeechService();
        player = new StreamPlayer();

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
                    for (Language x : subscribedLanguages) {
                        if (x.getName().equals(selectedSpinnerValue)) {
                            abbreviation = x.getAbbreviation();
                        }
                    }
                    try {
                        Log.d(TAG, "onClick: abbr is " + abbreviation);
                        new TranslationTask().execute(allPhrases.get(selectedPhraseIndex), abbreviation);
                        translationPhraseRecyclerView.setEnabled(false);
                        progressLayout.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        AlertDialogComponent.basicAlert(v.getContext(), "Could not translate the word to " + selectedSpinnerValue);
                    }
                }
            }
        });
        
        playTextToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpeechTask().execute(translatedTextView.getText().toString());
                voiceProgress.setVisibility(View.VISIBLE);
                playTextToSpeechButton.setVisibility(View.GONE);
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
            try {
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(strings[0])
                        .source(com.ibm.watson.language_translator.v3.util.Language.ENGLISH)
                        .target(strings[1])
                        .build();

                TranslationResult result = accessibilityHelper.getService().translate(translateOptions).execute().getResult();
                IdentifiableLanguages languages = accessibilityHelper.getService().listIdentifiableLanguages().execute().getResult();
                Log.d(TAG, "doInBackground: " + languages.getLanguages().size());
                return result.getTranslations().get(0).getTranslation();
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: whoooppssss");
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.equals("")) {
                super.onPostExecute(s);
                translatedTextView.setText(s);
                translationPhraseRecyclerView.setEnabled(false);
                translationDisplayLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
            } else {
                progressLayout.setVisibility(View.GONE);
            }
        }
    }

    private class SpeechTask extends AsyncTask <String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .text(strings[0])
                    .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
                    .accept(HttpMediaType.AUDIO_WAV)
                    .build();
            player.playStream(textService.synthesize(synthesizeOptions).execute().getResult());
            return "Did sythesize";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            voiceProgress.setVisibility(View.GONE);
            playTextToSpeechButton.setVisibility(View.VISIBLE);
        }
    }

    private TextToSpeech initTextToSpeechService() {
        Authenticator authenticator = new
                IamAuthenticator(getString(R.string.text_speech_apikey));
        TextToSpeech service = new TextToSpeech(authenticator);
        service.setServiceUrl(getString(R.string.text_speech_url));
        return service;
    }
}
