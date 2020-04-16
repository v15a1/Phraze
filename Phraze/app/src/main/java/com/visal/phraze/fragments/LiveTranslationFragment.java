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
import com.visal.phraze.Phrase;
import com.visal.phraze.R;
import com.visal.phraze.RadioRecyclerPhrasesAdapter;
import com.visal.phraze.RecyclerViewRadioChangeListener;
import com.visal.phraze.RefreshRecyclerView;
import com.visal.phraze.helpers.AccessibilityHelper;
import com.visal.phraze.helpers.DatabaseHelper;
import com.visal.phraze.helpers.SpeechTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LiveTranslationFragment extends Fragment implements RecyclerViewRadioChangeListener {

    private static final String TAG = LiveTranslationFragment.class.getSimpleName();
    private View layoutView;
    private Spinner languageSpinner;
    private static DatabaseHelper db;
    private static ArrayList<Language> subscribedLanguages;
    private ArrayList<String> subscribedLanguagesNames;
    private static ArrayList<String> allPhrases;
    private static ArrayList<Phrase> phrases;
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
    private static String abbreviation = "";
    private static Hashtable<ArrayList<String>, String> allTranslatedPhrases;
    private static String selectedSpinnerValue;
    private ArrayList<String> abbreviationList;
    OnFragmentInteractionListener mListener;
    private static Context context;

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

        context = getActivity();
        db = new DatabaseHelper(getActivity());
        subscribedLanguages = new ArrayList<>();
        subscribedLanguagesNames = new ArrayList<>();
        allPhrases = new ArrayList<>();
        allTranslatedPhrases = new Hashtable<>();
        abbreviationList = new ArrayList();

        subscribedLanguages = db.getAllSubscriptions();
        phrases = db.getAllPhraseData();
        for (Phrase x : phrases) {
            allPhrases.add(x.getPhrase());
        }

        subscribedLanguagesNames.add("Select a language");  //setting initial value for the spinner
        for (Language x : subscribedLanguages) {
            subscribedLanguagesNames.add(x.getName());
        }
        for (Language x : subscribedLanguages) {
            abbreviationList.add(x.getAbbreviation());
        }

        translationPhraseRecyclerView.setHasFixedSize(true);
        translatePhraseLayoutManager = new LinearLayoutManager(getActivity());
        translationPhraseRecyclerView.setLayoutManager(translatePhraseLayoutManager);
        translatePhraseAdapter = new RadioRecyclerPhrasesAdapter(phrases, this);
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
                    Log.d(TAG, "onClick: abbr is " + abbreviation);
                    new TranslationTask(getActivity()).execute(allPhrases.get(selectedPhraseIndex), abbreviation);
                    translationPhraseRecyclerView.setEnabled(false);
                    progressLayout.setVisibility(View.VISIBLE);

                }
            }
        });

        translateAllPhrasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteTranslations();
                translationPhraseRecyclerView.setEnabled(false);
                Log.d(TAG, "onClick: abbreviations are " + abbreviationList);
                new TranslateAllPhrasesTask(getActivity()).execute(allPhrases, abbreviationList);
            }
        });

        playTextToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpeechTask(playTextToSpeechButton, voiceProgress).execute(translatedTextView.getText().toString());
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

    public interface RefreshTranslations {
        public void refresh();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void recyclerViewRadioClick(View v, int position) {
        selectedPhraseIndex = position;
        Log.d(TAG, "recyclerViewRadioClick: position is " + selectedPhraseIndex);
    }

    public static final class TranslationTask extends AsyncTask<String, Void, String> {
        public AccessibilityHelper accessibilityHelper = new AccessibilityHelper();
        private Context context;

        @Override
        protected void onPreExecute() {
            progressLayout.setVisibility(View.VISIBLE);
        }

        public TranslationTask(Context context) {
            this.context = context;
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
                return result.getTranslations().get(0).getTranslation();
            } catch (Exception e) {
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
            } else {
                AlertDialogComponent.basicAlert(context, "Could not translate to " + selectedSpinnerValue);
            }
            progressLayout.setVisibility(View.GONE);
        }
    }

    public static final class TranslateAllPhrasesTask extends AsyncTask<ArrayList<String>, Void, Hashtable<ArrayList<String>, String>> {
        public AccessibilityHelper accessibilityHelper = new AccessibilityHelper();
        private Context context;

        public TranslateAllPhrasesTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressLayout.setVisibility(View.VISIBLE);
        }

        @SafeVarargs
        @Override
        protected final Hashtable<ArrayList<String>, String> doInBackground(ArrayList<String>... strings) {
            try {
                Hashtable<ArrayList<String>, String> allTranslations = new Hashtable<>();
                for (int x = 0; x < strings[0].size(); x++) {
                    for (int y = 0; y < strings[1].size(); y++) {
                        TranslateOptions translateOptions = new TranslateOptions.Builder()
                                .addText(strings[0].get(x))
                                .source(com.ibm.watson.language_translator.v3.util.Language.ENGLISH)
                                .target(strings[1].get(y))
                                .build();

                        TranslationResult result = accessibilityHelper.getService().translate(translateOptions).execute().getResult();
                        String res = result.getTranslations().get(0).getTranslation();
                        ArrayList<String> translationResult = new ArrayList<>();
                        translationResult.add(res);
                        translationResult.add(strings[0].get(x));
                        allTranslations.put(translationResult, strings[1].get(y));
                    }
                }
                return allTranslations;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Hashtable<ArrayList<String>, String> s) {
            if (s != null) {
                allTranslatedPhrases.putAll(s);
                Log.d(TAG, "onPostExecute: size of array is " + allTranslatedPhrases);
                for (Map.Entry<ArrayList<String>, String> entry : allTranslatedPhrases.entrySet()) {
                    for (int y = 0; y < subscribedLanguages.size(); y++) {
                        String abbr = entry.getValue();
                        String translation = entry.getKey().get(0);
                        String englishPhrase = entry.getKey().get(1);
                        String language = "";
                        for (Language x : subscribedLanguages) {
                            if (x.getAbbreviation().equals(abbr)) {
                                language = x.getName();
                            }
                        }
                        boolean successfulPersistence = db.insertTranslations(abbr, language, englishPhrase, translation);
                        Log.d(TAG, "onPostExecute: is data saved " + successfulPersistence);
                    }
                }
            } else {
                AlertDialogComponent.basicAlert(context, "Could not translate all the phrases to " + selectedSpinnerValue);
            }
            progressLayout.setVisibility(View.GONE);
        }
    }
}

