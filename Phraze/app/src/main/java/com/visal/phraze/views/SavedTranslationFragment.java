package com.visal.phraze.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.visal.phraze.model.Language;
import com.visal.phraze.R;
import com.visal.phraze.viewmodels.adapters.SavedTranslationAdapter;
import com.visal.phraze.model.Translation;
import com.visal.phraze.viewmodels.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//using fragments for tablayout
public class SavedTranslationFragment extends Fragment {

    private static DatabaseHelper db;
    private TranslationActivity activity;
    private View layoutView;
    private RecyclerView savedTranslationsRecyclerView;
    static RecyclerView.Adapter savedTranslationsAdapter;
    RecyclerView.LayoutManager savedTranslationManager;
    private static ArrayList<Translation> allTranslations;
    private static final String TAG = SavedTranslationFragment.class.getSimpleName();
    private Spinner languageSelector;
    private ArrayList<String> savedLanguagesString;
    private ArrayList<Language> savedLanguages;
    private ArrayList<Translation> selectedLanguageTranslation;
    Button updateButton;
    String abbreviation;


    private OnFragmentInteractionListener mListener;

    public SavedTranslationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = new TranslationActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_saved_translation, container, false);

        //accessing UI views
        languageSelector = layoutView.findViewById(R.id.saved_translation_spinner);
        updateButton = layoutView.findViewById(R.id.saved_translations_recyclerview_update_button);

        //getting values from the DB
        db = new DatabaseHelper(getActivity());
        savedLanguages = new ArrayList<>();
        savedLanguagesString = new ArrayList<>();
        selectedLanguageTranslation = new ArrayList<>();
        savedLanguages = db.getAllSubscriptions();
        savedLanguagesString.add("Show all");

        for (Language x : savedLanguages) {
            savedLanguagesString.add(x.getName());
        }

        allTranslations = new ArrayList<>();
        allTranslations = db.getAlltranslations();
        selectedLanguageTranslation = allTranslations;

        //setting state of the views
        savedTranslationsRecyclerView = layoutView.findViewById(R.id.saved_translations_recyclerview);
        savedTranslationsRecyclerView.setHasFixedSize(true);
        savedTranslationManager = new LinearLayoutManager(getActivity());
        savedTranslationsRecyclerView.setLayoutManager(savedTranslationManager);
        savedTranslationsAdapter = new SavedTranslationAdapter(getActivity(), selectedLanguageTranslation);
        savedTranslationsRecyclerView.setAdapter(savedTranslationsAdapter);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, savedLanguagesString);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        languageSelector.setAdapter(spinnerAdapter);


        //updating recyclerview to display values corresponding to the selected language
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedValue = languageSelector.getSelectedItem().toString();
                for (Language x : savedLanguages) {
                    if (x.getName().equals(selectedValue)) {
                        abbreviation = x.getAbbreviation();
                    }
                }
                ArrayList<Translation> results = new ArrayList<>();
                //getting the translations and adding them to an array to display
                if (selectedValue.equals("Show all")) {
                    results.addAll(allTranslations);
                } else {
                    for (Translation x : allTranslations) {
                        if (x.getAbbreviation().equals(abbreviation)) {
                            results.add(x);
                        }
                    }
                }
                //sorting the results
                Collections.sort(results, new SortResultAlphabetically());
                savedTranslationsAdapter = new SavedTranslationAdapter(getActivity(), results);
                savedTranslationsRecyclerView.setAdapter(savedTranslationsAdapter);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static void refreshAdapter() {
        allTranslations = db.getAlltranslations();
        savedTranslationsAdapter.notifyDataSetChanged();
    }

    //class with comparator implemented for sorting
    class SortResultAlphabetically implements Comparator<Translation> {
        @Override
        public int compare(Translation o1, Translation o2) {
            return o1.getEnglishPhrase().compareTo(o2.getEnglishPhrase());
        }
    }
}
