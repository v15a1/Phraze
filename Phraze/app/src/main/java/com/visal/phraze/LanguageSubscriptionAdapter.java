package com.visal.phraze;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;

import java.util.List;

public class LanguageSubscriptionAdapter extends RecyclerView.Adapter<LanguageSubscriptionAdapter.LanguageViewHolder> {
    private List<IdentifiableLanguage> languages;

    public LanguageSubscriptionAdapter(List<IdentifiableLanguage> languages) {
        this.languages = languages;
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
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder{
        public TextView subscriptionTextView;
        public CheckBox checkBox;
        public LanguageViewHolder(View view ) {
            super(view);
            subscriptionTextView = view.findViewById(R.id.subscription_card_textview);
            checkBox =  view.findViewById(R.id.subscription_checkbox);


        }
    }
}
