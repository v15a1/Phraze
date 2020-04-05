package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;
import com.visal.phraze.helpers.AccessibilityHelper;
import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;

public class LanguageSubscriptionActivity extends AppCompatActivity implements RecyclerViewCheckBoxCheckListener {
    private static final String TAG = LanguageSubscriptionActivity.class.getSimpleName();
    private static ArrayList<IdentifiableLanguage> languages;
    private static RecyclerView subscriptionRecyclerView;
    private static RecyclerView.Adapter subscriptionAdapter;
    private RecyclerView.LayoutManager subscriptionLayoutManager;
    private static RelativeLayout progressView;
    private static Button subscriptionUpdateButton;
    private RecyclerViewCheckBoxCheckListener listener = this;
    private static ArrayList<Integer> checkedCardIndexes = new ArrayList<>();

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_subscription);
        db = new DatabaseHelper(this);
        Cursor cursor = db.getAllSubscriptions();
        while (cursor.moveToNext()){
            checkedCardIndexes.add(cursor.getInt(0));
        }

        Log.d(TAG, "onCreate: checked are" + checkedCardIndexes);
        new LanguagesTask().execute(true);
        progressView = findViewById(R.id.language_subscription_progressbar);
        subscriptionRecyclerView = findViewById(R.id.language_subscription_recyclerview);
        subscriptionRecyclerView.setHasFixedSize(true);
        subscriptionLayoutManager = new LinearLayoutManager(this);
        subscriptionRecyclerView.setLayoutManager(subscriptionLayoutManager);
        subscriptionUpdateButton = findViewById(R.id.language_subscription_update_button);

        subscriptionUpdateButton.setEnabled(false);
        subscriptionUpdateButton.setAlpha(0.5f);

        subscriptionUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: indexes are " + checkedCardIndexes);
                db.deleteLanguageSubscriptionTableData();
                for (int x = 0; x < checkedCardIndexes.size(); x++) {
                    Log.d(TAG, "onClick: inside loop");
                    boolean success = db.insertLanguageSubscriptions(checkedCardIndexes.get(x));
                    Toast.makeText(LanguageSubscriptionActivity.this, "subs sent? " + success, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public void recyclerOnCheck(View v, ArrayList<Integer> arr) {
        for (int x = 0; x < arr.size(); x++){
            if (!checkedCardIndexes.contains(arr.get(x))){
                checkedCardIndexes.add(arr.get(x));
            }
        }
        Log.d(TAG, "recyclerOnCheck: " + checkedCardIndexes);
    }

    public static class LanguagesTask extends AsyncTask<Boolean, Void, ArrayList<IdentifiableLanguage>> {
        public AccessibilityHelper accessibilityHelper = new AccessibilityHelper();

        @Override
        protected ArrayList<IdentifiableLanguage> doInBackground(Boolean... booleans) {
            ArrayList<IdentifiableLanguage> languages = new ArrayList();
            if (booleans[0]) {
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
            subscriptionAdapter = new LanguageSubscriptionAdapter(languages, ls.listener, checkedCardIndexes);
            subscriptionRecyclerView.setAdapter(subscriptionAdapter);
            progressView.setVisibility(View.GONE);
            subscriptionUpdateButton.setEnabled(true);
            subscriptionUpdateButton.setAlpha(1f);
        }
    }
}
