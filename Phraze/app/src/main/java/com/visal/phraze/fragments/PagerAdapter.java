package com.visal.phraze.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.visal.phraze.helpers.DatabaseHelper;

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LiveTranslationFragment();
            case 1:
                return new SavedTranslationFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}