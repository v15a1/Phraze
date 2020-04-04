package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;
import com.visal.phraze.helpers.AccessibilityHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class LanguageSubscriptionActivity extends AppCompatActivity implements RecyclerViewCheckBoxCheckListener {
    private static final String TAG = LanguageSubscriptionActivity.class.getSimpleName();
    private static ArrayList<IdentifiableLanguage> languages;
    private static RecyclerView subscriptionRecyclerView;
    private static RecyclerView.Adapter subscriptionAdapter;
    private RecyclerView.LayoutManager subscriptionLayoutManager;
    private static RelativeLayout progressView;
    private static Button subscriptionUpdateButton;
    private RecyclerViewCheckBoxCheckListener listener = this;
    private ArrayList<Integer> checkedCardIndexes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_subscription);
        new LanguagesTask().execute(true);
        progressView = findViewById(R.id.language_subscription_progressbar);
        subscriptionRecyclerView = findViewById(R.id.language_subscription_recyclerview);
        subscriptionRecyclerView.setHasFixedSize(true);
        subscriptionLayoutManager = new LinearLayoutManager(this);
        subscriptionRecyclerView.setLayoutManager(subscriptionLayoutManager);
        subscriptionUpdateButton = findViewById(R.id.language_subscription_update_button);

        subscriptionUpdateButton.setEnabled(false);
        subscriptionUpdateButton.setAlpha(0.5f);

        checkedCardIndexes = new ArrayList<>();
    }

    @Override
    public void recyclerOnCheck(View v, ArrayList<Integer> arr) {
        checkedCardIndexes = arr;
        Log.d(TAG, "recyclerOnCheck: " + checkedCardIndexes);
    }

    public static class LanguagesTask extends AsyncTask<Boolean, Void, ArrayList<IdentifiableLanguage>> {
        public AccessibilityHelper accessibilityHelper = new AccessibilityHelper();

        @Override
        protected ArrayList<IdentifiableLanguage> doInBackground(Boolean... booleans) {
            ArrayList<IdentifiableLanguage> languages = new ArrayList();
            if (booleans[0]){
                IdentifiableLanguages lans = accessibilityHelper.getTranslator().listIdentifiableLanguages().execute().getResult();
                languages.addAll(lans.getLanguages());
            }
            return languages;
        }

        @Override
        protected void onPostExecute(ArrayList<IdentifiableLanguage> identifiableLanguages) {
            LanguageSubscriptionActivity ls = new LanguageSubscriptionActivity();
            super.onPostExecute(identifiableLanguages);
            languages = identifiableLanguages;
            subscriptionAdapter = new LanguageSubscriptionAdapter(languages, ls.listener);
            subscriptionRecyclerView.setAdapter(subscriptionAdapter);
            progressView.setVisibility(View.GONE);
            subscriptionUpdateButton.setEnabled(true);
            subscriptionUpdateButton.setAlpha(1f);
        }
    }
}
