package com.visal.phraze.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.visal.phraze.AlertDialogComponent;
import com.visal.phraze.Language;
import com.visal.phraze.R;
import com.visal.phraze.RadioRecyclerPhrasesAdapter;
import com.visal.phraze.RecyclerViewRadioChangeListener;
import com.visal.phraze.helpers.AccessibilityHelper;
import com.visal.phraze.helpers.DatabaseHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LiveTranslationFragment extends Fragment implements RecyclerViewRadioChangeListener {

    private static final String TAG = LiveTranslationFragment.class.getSimpleName();
    private View layoutView;
    private Spinner languageSpinner;
    private DatabaseHelper db;
    private ArrayList<Language> subscribedLanguages;
    private ArrayList<String> subscribedLanguagesNames;
    private ArrayList<String> allPhrases;
    private static RecyclerView translationPhraseRecyclerView;
    private RecyclerView.LayoutManager translatePhraseLayoutManager;
    private RecyclerView.Adapter translatePhraseAdapter;
    private static TextView translatedTextView;
    private Button translatePhraseButton;
    private Button translateAllPhrasesButton;
    private static RelativeLayout progressLayout;
    private static LinearLayout translationDisplayLayout;
    private ImageButton playTextToSpeechButton;
    private ProgressBar voiceProgress;
    private int selectedPhraseIndex;
    private TextToSpeech textService;
    private StreamPlayer player;
    private String abbreviation = "";
    private static ArrayList<String> allTranslatedPhrases;
    private String selectedSpinnerValue;
    OnFragmentInteractionListener mListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_live_translation, container, false);

        languageSpinner = layoutView.findViewById(R.id.subscribed_language_spinner);
        translationPhraseRecyclerView = layoutView.findViewById(R.id.translate_phrase_recyclerview);
        translatedTextView = layoutView.findViewById(R.id.translation_textview);
        translatePhraseButton = layoutView.findViewById(R.id.translate_phrase_button);
        progressLayout = layoutView.findViewById(R.id.phrase_translation_progressbar);
        translationDisplayLayout = layoutView.findViewById(R.id.translation_display_layout);
        playTextToSpeechButton = layoutView.findViewById(R.id.play_text_to_speech);
        voiceProgress = layoutView.findViewById(R.id.voice_progress);
        translateAllPhrasesButton = layoutView.findViewById(R.id.translate_all_phrases_button);

        progressLayout.setVisibility(View.GONE);
        translationDisplayLayout.setVisibility(View.GONE);
        voiceProgress.setVisibility(View.GONE);

        db = new DatabaseHelper(getActivity());
        subscribedLanguages = new ArrayList<>();
        subscribedLanguagesNames = new ArrayList<>();
        allPhrases = new ArrayList<>();
        allTranslatedPhrases = new ArrayList<>();
        textService = initTextToSpeechService();
        player = new StreamPlayer();

        subscribedLanguages = db.getAllSubscriptions();
        allPhrases = db.getAllPhrases();
        subscribedLanguagesNames.add("Select a language");  //setting initial value for the spinner
        for (Language x : subscribedLanguages) {
            subscribedLanguagesNames.add(x.getName());
        }

        translationPhraseRecyclerView.setHasFixedSize(true);
        translatePhraseLayoutManager = new LinearLayoutManager(getActivity());
        translationPhraseRecyclerView.setLayoutManager(translatePhraseLayoutManager);
        translatePhraseAdapter = new RadioRecyclerPhrasesAdapter(allPhrases, this);
        translationPhraseRecyclerView.setAdapter(translatePhraseAdapter);

        //setting values into the spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, subscribedLanguagesNames);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        languageSpinner.setAdapter(spinnerAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerValue = languageSpinner.getSelectedItem().toString();
                for (Language x : subscribedLanguages) {
                    if (x.getName().equals(selectedSpinnerValue)) {
                        abbreviation = x.getAbbreviation();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        translatePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedSpinnerValue.equals("Select a language")) {
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

        translateAllPhrasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: pressed translateAllPhrasesButton");
                if (!selectedSpinnerValue.equals("Select a language")) {
                    Log.d(TAG, "onClick: inside if");
                    try {
                        Log.d(TAG, "onClick: inside try");
                        translationPhraseRecyclerView.setEnabled(false);
                        for (String x : allPhrases) {
                            Log.d(TAG, "onClick: inside for");
                            new TranslationTask().execute(x, abbreviation);
                        }
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
        return layoutView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void recyclerViewRadioClick(View v, int position) {
        selectedPhraseIndex = position;
        Log.d(TAG, "recyclerViewRadioClick: position is " + selectedPhraseIndex);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public static final class TranslationTask extends AsyncTask<String, Void, String> {
        public AccessibilityHelper accessibilityHelper = new AccessibilityHelper();

        @Override
        protected void onPreExecute() {
            progressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(strings[0])
                        .source(com.ibm.watson.language_translator.v3.util.Language.ENGLISH)
                        .target(strings[1])
                        .build();

                TranslationResult result = accessibilityHelper.getService().translate(translateOptions).execute().getResult();
                String res = result.getTranslations().get(0).getTranslation();
                if (!allTranslatedPhrases.contains(res)) {
                    allTranslatedPhrases.add(res);
                }
                return res;
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

    public class SpeechTask extends AsyncTask<String, Void, String> {

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

