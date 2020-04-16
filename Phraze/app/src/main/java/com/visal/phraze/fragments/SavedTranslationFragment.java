package com.visal.phraze.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

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
import android.widget.Spinner;

import com.visal.phraze.Language;
import com.visal.phraze.R;
import com.visal.phraze.SavedTranslationAdapter;
import com.visal.phraze.Translation;
import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

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
    String selectedLanguage;
    String abbreviation;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

        languageSelector = layoutView.findViewById(R.id.saved_translation_spinner);
        updateButton = layoutView.findViewById(R.id.saved_translations_recyclerview_update_button);

        db = new DatabaseHelper(getActivity());
        savedLanguages = new ArrayList<>();
        savedLanguagesString = new ArrayList<>();
        selectedLanguageTranslation = new ArrayList<>();
        savedLanguages = db.getAllSubscriptions();
        savedLanguagesString.add("Select a language");

        for (Language x : savedLanguages) {
            savedLanguagesString.add(x.getName());
        }

        allTranslations = new ArrayList<>();
        allTranslations = db.getAlltranslations();
        selectedLanguageTranslation = allTranslations;

        savedTranslationsRecyclerView = layoutView.findViewById(R.id.saved_translations_recyclerview);
        savedTranslationsRecyclerView.setHasFixedSize(true);
        savedTranslationManager = new LinearLayoutManager(getActivity());
        savedTranslationsRecyclerView.setLayoutManager(savedTranslationManager);
        savedTranslationsAdapter = new SavedTranslationAdapter(getActivity(), selectedLanguageTranslation);
        savedTranslationsRecyclerView.setAdapter(savedTranslationsAdapter);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, savedLanguagesString);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        languageSelector.setAdapter(spinnerAdapter);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedValue = languageSelector.getSelectedItem().toString();
                for (Language x : savedLanguages){
                    if (x.getName().equals(selectedValue)){
                        abbreviation = x.getAbbreviation();
                        Log.d(TAG, "onClick: " + abbreviation);
                    }
                }
                ArrayList<Translation> results = new ArrayList<>();
                for (Translation x : allTranslations){
                    if (x.getAbbreviation().equals(abbreviation)){
                        results.add(x);
                    }
                }
                Log.d(TAG, "onClick: results are " + results);
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

    interface refreshAdapter {

    }

    public static void refreshAdapter() {
        allTranslations = db.getAlltranslations();
        savedTranslationsAdapter.notifyDataSetChanged();
    }
}
