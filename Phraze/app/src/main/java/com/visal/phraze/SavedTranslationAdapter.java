package com.visal.phraze;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedTranslationAdapter extends RecyclerView.Adapter<SavedTranslationAdapter.PhraseViewHolder>{

    private static final String TAG = DisplayPhrasesAdapter.class.getSimpleName();
    private List<Translation> translations;

    //default constructor
    public SavedTranslationAdapter(List<Translation> translations) {
        this.translations = translations;
    }

    @NonNull
    @Override
    public PhraseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_translation_card, parent, false);
        return new PhraseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhraseViewHolder holder, int position) {
        Translation translation = translations.get(position);
        holder.englishPhrase.setText(translation.getEnglishPhrase());
        holder.translatedPhrase.setText(translation.getTranslation());
    }

    @Override
    public int getItemCount() {
        return translations.size();
    }

    public static class PhraseViewHolder extends RecyclerView.ViewHolder{
        public TextView englishPhrase;
        public TextView translatedPhrase;
        public PhraseViewHolder(View v) {
            super(v);
            englishPhrase = v.findViewById(R.id.saved_translation_phrase_textview);
            translatedPhrase = v.findViewById(R.id.saved_translation_translation_textview);
        }
    }
}
