package com.visal.phraze.views;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.visal.phraze.viewmodels.AlertDialogComponent;
import com.visal.phraze.model.Language;
import com.visal.phraze.model.Phrase;
import com.visal.phraze.R;
import com.visal.phraze.viewmodels.adapters.RadioRecyclerPhrasesAdapter;
import com.visal.phraze.viewmodels.interfaces.RecyclerViewRadioChangeListener;
import com.visal.phraze.viewmodels.AccessibilityHelper;
import com.visal.phraze.viewmodels.DatabaseHelper;
import com.visal.phraze.viewmodels.SpeechTask;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

//Using Fragments for a tablayout
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
    private static Button translatePhraseButton;
    private static Button translateAllPhrasesButton;
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

        //accessing UI views
        languageSpinner = layoutView.findViewById(R.id.subscribed_language_spinner);
        translationPhraseRecyclerView = layoutView.findViewById(R.id.translate_phrase_recyclerview);
        translatedTextView = layoutView.findViewById(R.id.translation_textview);
        translatePhraseButton = layoutView.findViewById(R.id.translate_phrase_button);
        progressLayout = layoutView.findViewById(R.id.phrase_translation_progressbar);
        translationDisplayLayout = layoutView.findViewById(R.id.translation_display_layout);
        playTextToSpeechButton = layoutView.findViewById(R.id.play_text_to_speech);
        voiceProgress = layoutView.findViewById(R.id.voice_progress);
        translateAllPhrasesButton = layoutView.findViewById(R.id.translate_all_phrases_button);

        //setting state of views
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

        //setting recyclerview values
        translationPhraseRecyclerView.setHasFixedSize(true);
        translatePhraseLayoutManager = new LinearLayoutManager(getActivity());
        translationPhraseRecyclerView.setLayoutManager(translatePhraseLayoutManager);
        translatePhraseAdapter = new RadioRecyclerPhrasesAdapter(phrases, this);
        translationPhraseRecyclerView.setAdapter(translatePhraseAdapter);

        //setting values into the spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, subscribedLanguagesNames);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        languageSpinner.setAdapter(spinnerAdapter);

        //getting the spinner value and getting the abbreviation needed for translation
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

        //button to invoke asynchronous methods
        translatePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedSpinnerValue.equals("Select a language")) {
                    Log.d(TAG, "onClick: abbr is " + abbreviation);
                    //translating the selected word
                    new TranslationTask(getActivity()).execute(allPhrases.get(selectedPhraseIndex), abbreviation);
                    translationPhraseRecyclerView.setEnabled(false);
                    progressLayout.setVisibility(View.VISIBLE);
                } else {
                    AlertDialogComponent.basicAlert(getActivity(), "Please select a language to translate to.");
                }
            }
        });

        //button to translate all the phrases by all the subscribed languages
        translateAllPhrasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedSpinnerValue.equals("Select a language")) {
                    //truncating the table
                    db.deleteTranslations();
                    translationPhraseRecyclerView.setEnabled(false);
                    Log.d(TAG, "onClick: abbreviations are " + abbreviationList);
                    //translating all the phrases
                    new TranslateAllPhrasesTask(getActivity()).execute(allPhrases, abbreviationList);
                }else{
                    AlertDialogComponent.basicAlert(getActivity(), "Please select a language to translate to.");
                }
            }
        });

        //button to invoke text to speech
        playTextToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //text to speech execution
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

    public static void setButtonEnabledState() {
        translateAllPhrasesButton.setEnabled(true);
        translatePhraseButton.setEnabled(true);
        translateAllPhrasesButton.setTextColor(context.getResources().getColor(R.color.colorAccent));
        translatePhraseButton.setTextColor(context.getResources().getColor(R.color.colorAccent));
    }

    public static void setButtonDisabledState() {
        translateAllPhrasesButton.setEnabled(false);
        translatePhraseButton.setEnabled(false);
        translateAllPhrasesButton.setTextColor(context.getResources().getColor(R.color.darkGrey));
        translatePhraseButton.setTextColor(context.getResources().getColor(R.color.darkGrey));
    }

    //class that executes and retrieves the translations asynchronously
    public static final class TranslationTask extends AsyncTask<String, Void, String> {
        public AccessibilityHelper accessibilityHelper = new AccessibilityHelper();
        private Context context;

        @Override
        protected void onPreExecute() {
            progressLayout.setVisibility(View.VISIBLE);
            setButtonDisabledState();
        }

        public TranslationTask(Context context) {
            this.context = context;
        }

        //asynchronous method
        @Override
        protected String doInBackground(String... strings) {
            //error prevention using try-catch statements
            try {
                //getting the translation
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

        //setting values and state post execution
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
            setButtonEnabledState();
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
            setButtonDisabledState();
        }

        @SafeVarargs
        @Override
        protected final Hashtable<ArrayList<String>, String> doInBackground(ArrayList<String>... strings) {
            Hashtable<ArrayList<String>, String> allTranslations = new Hashtable<>();
            Log.d(TAG, "doInBackground: 0" + strings[0]);
            Log.d(TAG, "doInBackground: 1" + strings[1]);
            //translating all values by all languages
            //using try catch for error prevention
            for (int x = 0; x < strings[1].size(); x++) {
                for (int y = 0; y < strings[0].size(); y++) {
                    //translation
                    try {
                        TranslateOptions translateOptions = new TranslateOptions.Builder()
                                .addText(strings[0].get(y))
                                .source(com.ibm.watson.language_translator.v3.util.Language.ENGLISH)
                                .target(strings[1].get(x))
                                .build();

                        TranslationResult result = accessibilityHelper.getService().translate(translateOptions).execute().getResult();
                        String res = result.getTranslations().get(0).getTranslation();
                        ArrayList<String> translationResult = new ArrayList<>();
                        translationResult.add(res);
                        translationResult.add(strings[0].get(y));
                        allTranslations.put(translationResult, strings[1].get(x));
                        Log.d(TAG, "doInBackground: all translations " + res + ", " + strings[0].get(y));
                    } catch (Exception e) {
                        Log.d(TAG, "doInBackground: unsuccessfull translation");
                    }
                }
            }
            return allTranslations;
        }

        //setting state or values post execution
        @Override
        protected void onPostExecute(Hashtable<ArrayList<String>, String> s) {
            if (s.size() > 0) {
                allTranslatedPhrases.putAll(s);
                boolean success = true;
                Log.d(TAG, "onPostExecute: size of array is " + allTranslatedPhrases);
                for (Map.Entry<ArrayList<String>, String> entry : allTranslatedPhrases.entrySet()) {
                    String abbr = entry.getValue();
                    String translation = entry.getKey().get(0);
                    String englishPhrase = entry.getKey().get(1);
                    String language = "";
                    boolean successfulPersistence = db.insertTranslations(abbr, language, englishPhrase, translation);
                }
                if (s.size() != (allPhrases.size() * subscribedLanguages.size())) {
                    AlertDialogComponent.translationPageAlert(context, "Not all phrases could be translated. The translated phrases have been saved.");
                } else {
                    AlertDialogComponent.translationPageAlert(context, "All the phrases have been translated and saved");
                }
            } else {
                AlertDialogComponent.translationPageAlert(context, "Could not translate all the phrases. Check the languages as some ma");
            }
            progressLayout.setVisibility(View.GONE);
            setButtonEnabledState();
        }
    }
}

