package com.visal.phraze;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
public class DisplayPhrasesAdapter extends RecyclerView.Adapter<DisplayPhrasesAdapter.PhraseViewHolder> {

    private static final String TAG = DisplayPhrasesAdapter.class.getSimpleName();
    private List<String> phrases;

    //default constructor
    public DisplayPhrasesAdapter(List<String> phrases) {
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
        String phrase = phrases.get(position);
        holder.textView.setText(phrase);
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public static class PhraseViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public PhraseViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.display_phrases_card_textview);
        }
    }
}
