package com.visal.phraze.viewmodels.interfaces;

import android.view.View;

import java.util.ArrayList;

//interface to implement to get the check boxes selected in a recycler view
public interface RecyclerViewCheckBoxCheckListener {
    public void recyclerOnCheck(View v, ArrayList<Integer> arr);
}
