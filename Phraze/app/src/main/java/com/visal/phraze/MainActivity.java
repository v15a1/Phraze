package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    Button addPhraseActivityBtn;
    Button showPhrasesActivityBtn;
    Button editPhraseActivityBtn;
    Button languageSubscriptionActivityBtn;
    Button translateActivityBtn;
    Button toggleButtonLayoutBtn;
    LinearLayout buttonLayout;
    boolean isButtonsDisplayed = true;
    Drawable expandArrow;
    Drawable collapeArrow;
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

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleButtonLayoutBtn.setVisibility(View.VISIBLE);
                toggleButtonLayoutBtn.animate()
                .alpha(1f)
                .setDuration(300);
            }
        }, 2000);

        buttonLayout.setAlpha(0f);
        toggleButtonLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonsDisplayed) {
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
        });
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
        editor = sharedPreferences.edit();
        editor.putBoolean("showButtonMenu", isButtonsDisplayed);
        editor.apply();
    }
}
