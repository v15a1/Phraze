package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.visal.phraze.fragments.TranslationActivity;
import com.visal.phraze.helpers.DatabaseHelper;

import java.util.ArrayList;

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

        buttonLayout.setVisibility(View.GONE);
        toggleButtonLayoutBtn.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleButtonLayoutBtn.setVisibility(View.VISIBLE);
            }
        }, 2000);

        toggleButtonLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonsDisplayed) {
                    buttonLayout.setVisibility(View.VISIBLE);
                    toggleButtonLayoutBtn.setCompoundDrawablesWithIntrinsicBounds(null, expandArrow, null, null);
                    toggleButtonLayoutBtn.setText("Hide buttons");
                    isButtonsDisplayed = false;
                } else {
                    buttonLayout.setVisibility(View.GONE);
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
}
