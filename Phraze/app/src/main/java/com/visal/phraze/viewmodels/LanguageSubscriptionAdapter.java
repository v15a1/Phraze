package com.visal.phraze.viewmodels;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;
import com.visal.phraze.R;
import com.visal.phraze.views.LanguageSubscriptionActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageSubscriptionAdapter extends RecyclerView.Adapter<LanguageSubscriptionAdapter.LanguageViewHolder> {
    private List<IdentifiableLanguage> languages;
    private List<Integer> selectedCardIndexes;
    private boolean[] ischeckedState;
    private static final String TAG = LanguageSubscriptionActivity.class.getSimpleName();
    private static RecyclerViewCheckBoxCheckListener checkboxListener;

    public LanguageSubscriptionAdapter(List<IdentifiableLanguage> languages, RecyclerViewCheckBoxCheckListener listener, ArrayList<Integer> selectedCardIndexes) {
        this.languages = languages;
        ischeckedState = new boolean[languages.size()];
        checkboxListener = listener;
        setCheckedCard(selectedCardIndexes);
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_subscription_card_item, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        String languageName = languages.get(position).getName();
        holder.subscriptionTextView.setText(languageName);
        if (ischeckedState[position]){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder {
        public TextView subscriptionTextView;
        public CheckBox checkBox;

        public LanguageViewHolder(final View view) {
            super(view);
            subscriptionTextView = view.findViewById(R.id.subscription_card_textview);
            checkBox = view.findViewById(R.id.subscription_checkbox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ischeckedState[getAdapterPosition()]) {
                        ischeckedState[getAdapterPosition()] = false;
                    } else {
                        ischeckedState[getAdapterPosition()] = true;
                    }
                    Log.d(TAG, "onClick 1: " + Arrays.toString(ischeckedState));
                    ArrayList<Integer> checkedIndexes = new ArrayList<>();
                    for (int x = 0; x < ischeckedState.length; x++){
                        if (ischeckedState[x]) {
                            checkedIndexes.add(x);
                        }
                    }


                    Log.d(TAG, "onClick 2: " + Arrays.toString(ischeckedState));
                    checkboxListener.recyclerOnCheck(view, checkedIndexes);
                    notifyDataSetChanged();

                }
            });
        }

    }

    public void setCheckedCard(List<Integer> indexes) {
        for (Integer x : indexes){
            ischeckedState[x] = true;
        }
        Log.d(TAG, "setCheckedCard: " + Arrays.toString(ischeckedState));
    }
}

