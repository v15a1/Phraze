package com.visal.phraze.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;
import com.visal.phraze.viewmodels.AlertDialogComponent;
import com.visal.phraze.viewmodels.adapters.LanguageSubscriptionAdapter;
import com.visal.phraze.R;
import com.visal.phraze.viewmodels.interfaces.RecyclerViewCheckBoxCheckListener;
import com.visal.phraze.viewmodels.AccessibilityHelper;
import com.visal.phraze.viewmodels.DatabaseHelper;
import com.visal.phraze.viewmodels.NetworkAccessHelper;
import com.visal.phraze.model.Language;

import java.util.ArrayList;

public class LanguageSubscriptionActivity extends AppCompatActivity implements RecyclerViewCheckBoxCheckListener {

    private static final String TAG = LanguageSubscriptionActivity.class.getSimpleName();
    private static final String TITLE = "Language Subscription";

    private static ArrayList<IdentifiableLanguage> languages;
    private static RecyclerView subscriptionRecyclerView;
    private static RecyclerView.Adapter subscriptionAdapter;
    private RecyclerView.LayoutManager subscriptionLayoutManager;
    private static RelativeLayout progressView;
    private static Button subscriptionUpdateButton;
    private RecyclerViewCheckBoxCheckListener listener = this;
    private static ArrayList<Integer> checkedCardIndexes = new ArrayList<>();
    private static ArrayList<Language> subscribedLanguages = new ArrayList<>();
    private View layout;
    DatabaseHelper db;
    NetworkAccessHelper networkAccessHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_subscription);
        //getting all the subscribed languages
        db = new DatabaseHelper(this);
        subscribedLanguages = db.getAllSubscriptions();

        //adding the indexes of the subscribed languages
        for (Language x : subscribedLanguages) {
            checkedCardIndexes.add(x.getIndex());
        }

        //getting network availability
        networkAccessHelper = new NetworkAccessHelper(this);
        if (networkAccessHelper.isNetworkAvailable()) {
            //executing task to retrieve language list
            new LanguagesTask().execute(true);
        } else {
            AlertDialogComponent.basicAlert(this, "Network error. Please check if you are connected to the internet");
        }
        //accessing UI elements
        layout = findViewById(R.id.language_subs_activity);
        progressView = findViewById(R.id.language_subscription_progressbar);
        subscriptionRecyclerView = findViewById(R.id.language_subscription_recyclerview);
        subscriptionRecyclerView.setHasFixedSize(true);
        subscriptionLayoutManager = new LinearLayoutManager(this);
        subscriptionRecyclerView.setLayoutManager(subscriptionLayoutManager);
        subscriptionUpdateButton = findViewById(R.id.language_subscription_update_button);

        //setting state of views
        subscriptionUpdateButton.setEnabled(false);
        subscriptionUpdateButton.setTextColor(getResources().getColor(R.color.darkGrey));

        //displaying the actionbat and setting the title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TITLE);
        }

        //updating the DB with the new subscriptions
        subscriptionUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: indexes are " + checkedCardIndexes);
                db.deleteLanguageSubscriptionTableData();
                for (int x = 0; x < checkedCardIndexes.size(); x++) {
                    int y = checkedCardIndexes.get(x);
                    //updating the DB
                    boolean success = db.insertLanguageSubscriptions(y, languages.get(y).getName(), languages.get(y).getLanguage());
                    //snackbar to display success of failure
                    if (success) {
                        Snackbar.make(layout, "Successfully updated subscriptions", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(layout, "Could NOT updated subscriptions", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //overridden method in an interface which is used to get the arraylist of the indexes of checked languages
    @Override
    public void recyclerOnCheck(View v, ArrayList<Integer> arr) {
        checkedCardIndexes = arr;
    }

    //class that executes and retrieves all the languages asynchronously
    public class LanguagesTask extends AsyncTask<Boolean, Void, ArrayList<IdentifiableLanguage>> {
        public AccessibilityHelper accessibilityHelper = new AccessibilityHelper();

        //method to execute in the background
        @Override
        protected ArrayList<IdentifiableLanguage> doInBackground(Boolean... booleans) {
            ArrayList<IdentifiableLanguage> languages = new ArrayList();
            if (booleans[0]) {
                //getting all the languages
                IdentifiableLanguages lans = accessibilityHelper.getService().listIdentifiableLanguages().execute().getResult();
                languages.addAll(lans.getLanguages());
            }
            return languages;
        }

        //methods, states, variables to assign or set post execution
        @Override
        protected void onPostExecute(ArrayList<IdentifiableLanguage> identifiableLanguages) {
            LanguageSubscriptionActivity ls = new LanguageSubscriptionActivity();
            super.onPostExecute(identifiableLanguages);
            languages = identifiableLanguages;
            subscriptionAdapter = new LanguageSubscriptionAdapter(languages, ls.listener, checkedCardIndexes);
            checkedCardIndexes.clear();
            subscriptionRecyclerView.setAdapter(subscriptionAdapter);
            progressView.setVisibility(View.GONE);
            subscriptionUpdateButton.setEnabled(true);
            subscriptionUpdateButton.setTextColor(getResources().getColor(R.color.green));

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //method to change activities
            startActivity(new Intent(LanguageSubscriptionActivity.this, MainActivity.class));
            finish();       //method call to destroy the activity from the memory
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
