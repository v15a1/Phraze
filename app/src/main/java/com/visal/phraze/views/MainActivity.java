package com.visal.phraze.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.visal.phraze.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    Button addPhraseActivityBtn;
    Button showPhrasesActivityBtn;
    Button editPhraseActivityBtn;
    Button languageSubscriptionActivityBtn;
    Button translateActivityBtn;
    Button toggleButtonLayoutBtn;
    LinearLayout buttonLayout;
    boolean isButtonsDisplayed;
    Drawable expandArrow;
    Drawable collapeArrow;

    //use of Shared Preferences to save state
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sharedPrefFile = "com.visal.phraze";

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addPhraseActivityBtn = findViewById(R.id.add_phrase_activity_button);
        showPhrasesActivityBtn = findViewById(R.id.show_phrases_activity_button);
        editPhraseActivityBtn = findViewById(R.id.edit_phrases_activity_button);
        languageSubscriptionActivityBtn = findViewById(R.id.language_subscription_activity_button);
        translateActivityBtn = findViewById(R.id.translate_activity_button);
        toggleButtonLayoutBtn = findViewById(R.id.show_all_button);
        buttonLayout = findViewById(R.id.button_linearLayout);
        collapeArrow = getResources().getDrawable(R.drawable.ic_expand_less_black_24dp);
        expandArrow = getResources().getDrawable(R.drawable.ic_expand_more_black_24dp);

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        isButtonsDisplayed = sharedPreferences.getBoolean("showButtonMenu", false);


        buttonLayout.setVisibility(View.GONE);
        toggleButtonLayoutBtn.setVisibility(View.GONE);
        toggleButtonLayoutBtn.setAlpha(0f);

        toggleButtonLayoutBtn.setVisibility(View.VISIBLE);
        //animating the button
        toggleButtonLayoutBtn.animate()
                .alpha(1f)
                .setDuration(300);

        buttonLayout.setAlpha(0f);
        //animating the button layout and displaying the buttons
        toggleButtonLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonLayoutDisplayState(isButtonsDisplayed);
            }
        });

        //OnClickListeners to start the activities
        addPhraseActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddPhraseActivity.class));
            }
        });

        showPhrasesActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DisplayPhrasesActivity.class));
            }
        });

        editPhraseActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditPhrasesActivity.class));
            }
        });

        languageSubscriptionActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LanguageSubscriptionActivity.class));
            }
        });

        translateActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TranslationActivity.class));
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        //saving state using Shared Preferences
        editor = sharedPreferences.edit();
        editor.putBoolean("showButtonMenu", isButtonsDisplayed);
        editor.apply();
    }

    private void setButtonLayoutDisplayState(boolean visible){
        {
            if (visible) {
                buttonLayout.setVisibility(View.VISIBLE);
                buttonLayout.animate()
                        .alpha(1.0f)
                        .translationY(0)
                        .setDuration(300);
                buttonLayout.setEnabled(true);
                toggleButtonLayoutBtn.setCompoundDrawablesWithIntrinsicBounds(null, expandArrow, null, null);
                toggleButtonLayoutBtn.setText("Hide buttons");
                isButtonsDisplayed = false;
            } else {
                buttonLayout.setVisibility(View.VISIBLE);
                buttonLayout.animate()
                        .alpha(0.0f)
                        .translationY(buttonLayout.getHeight())
                        .setDuration(300);
                buttonLayout.setVisibility(View.GONE);
                buttonLayout.setEnabled(false);
                toggleButtonLayoutBtn.setCompoundDrawablesWithIntrinsicBounds(null, collapeArrow, null, null);
                toggleButtonLayoutBtn.setText("Show buttons");
                isButtonsDisplayed = true;
            }
        }
    }
}
