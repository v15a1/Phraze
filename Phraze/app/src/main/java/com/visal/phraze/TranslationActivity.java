package com.visal.phraze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.visal.phraze.R;
import com.visal.phraze.fragments.LiveTranslationFragment;
import com.visal.phraze.fragments.PagerAdapter;
import com.visal.phraze.fragments.SavedTranslationFragment;

public class TranslationActivity extends AppCompatActivity implements LiveTranslationFragment.OnFragmentInteractionListener, SavedTranslationFragment.OnFragmentInteractionListener, LiveTranslationFragment.RefreshTranslations{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        TabLayout tabLayout = findViewById(R.id.translation_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Live Translation"));
        tabLayout.addTab(tabLayout.newTab().setText("Saved Translations"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

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
    public void refresh() {
        SavedTranslationFragment.refreshAdapter();
    }
}
