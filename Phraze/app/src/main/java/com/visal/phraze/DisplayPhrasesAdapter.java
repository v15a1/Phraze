package com.visal.phraze;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.visal.phraze.helpers.DateTime;

import java.util.ArrayList;
import java.util.List;
//https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
public class DisplayPhrasesAdapter extends RecyclerView.Adapter<DisplayPhrasesAdapter.PhraseViewHolder> {

    private static final String TAG = DisplayPhrasesAdapter.class.getSimpleName();
    private ArrayList<Phrase> phrases;

    //default constructor
    public DisplayPhrasesAdapter(ArrayList<Phrase> phrases) {
        this.phrases = new ArrayList<>();
        this.phrases = phrases;
    }

    @NonNull
    @Override
    public PhraseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_phrases_card_item, parent, false);
        return new PhraseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhraseViewHolder holder, int position) {
        String phrase = phrases.get(position).phrase;
        String daysAgo = DateTime.getDaysPassed(phrases.get(position).dateAdded);

        holder.phraseTextView.setText(phrase);
        holder.daysBeforeTextView.setText(daysAgo);
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public static class PhraseViewHolder extends RecyclerView.ViewHolder{
        public TextView phraseTextView;
        public TextView daysBeforeTextView;
        public PhraseViewHolder(View v) {
            super(v);
            phraseTextView = v.findViewById(R.id.display_phrases_card_textview);
            daysBeforeTextView = v.findViewById(R.id.display_phrases_days_before);
        }
    }
}
