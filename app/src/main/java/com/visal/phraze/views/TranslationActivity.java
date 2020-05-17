package com.visal.phraze.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.visal.phraze.R;
import com.visal.phraze.viewmodels.adapters.PagerAdapter;

public class TranslationActivity extends AppCompatActivity implements LiveTranslationFragment.OnFragmentInteractionListener, SavedTranslationFragment.OnFragmentInteractionListener, LiveTranslationFragment.RefreshTranslations{

    private static final String TITLE = "Translations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        //accessing the tablayout
        TabLayout tabLayout = findViewById(R.id.translation_tab_layout);
        //setting the tablayout titles
        tabLayout.addTab(tabLayout.newTab().setText("Live Translation"));
        tabLayout.addTab(tabLayout.newTab().setText("Saved Translations"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        //displaying the actionbat and setting the title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(TITLE);
        }

        //setting the fragments for the tablayout
        final ViewPager viewPager = findViewById(R.id.fragment_view_pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //method to change activities
            startActivity(new Intent(TranslationActivity.this, MainActivity.class));
            finish();       //method call to destroy the activity from the memory
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh() {
        SavedTranslationFragment.refreshAdapter();
    }
}
