package com.visal.phraze.viewmodels.interfaces;

import android.view.View;

//interface to implement to get the selected radio button in a recycler view
public interface RecyclerViewRadioChangeListener {
    public void recyclerViewRadioClick(View v, int position);
}
