package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button addPhraseActivityBtn;
    Button showPhrasesActivityBtn;
    Button editPhraseActivityBtn;
    Button languageSubscriptionActivityBtn;
    Button translateActivityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addPhraseActivityBtn = findViewById(R.id.add_phrase_activity_button);
        showPhrasesActivityBtn = findViewById(R.id.show_phrases_activity_button);
        editPhraseActivityBtn = findViewById(R.id.edit_phrases_activity_button);
        languageSubscriptionActivityBtn = findViewById(R.id.language_subscription_activity_button);
        translateActivityBtn = findViewById(R.id.translate_activity_button);

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
                startActivity(new Intent(MainActivity.this, TranslatePhraseActivity.class));
            }
        });


    }
}
