package com.visal.phraze;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.visal.phraze.helpers.NetworkAccessHelper;
import com.visal.phraze.helpers.SpeechTask;

import java.util.ArrayList;
import java.util.List;

public class SavedTranslationAdapter extends RecyclerView.Adapter<SavedTranslationAdapter.PhraseViewHolder> {

    private static final String TAG = DisplayPhrasesAdapter.class.getSimpleName();
    private List<Translation> translations;
    public int cardIndex = -1;
    public Context context;
    public ArrayList<Boolean> isPressed;
    NetworkAccessHelper networkAccessHelper;

    //default constructor
    public SavedTranslationAdapter(Context context, List<Translation> translations) {
        this.translations = translations;
        this.context = context;
        this.networkAccessHelper = new NetworkAccessHelper(context);
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
        if (networkAccessHelper.isNetworkAvailable()) {
            if (position == cardIndex) {
                Log.d(TAG, "onBindViewHolder: going to speak");
                new SpeechTask(holder.playVoiceButton, holder.progress).execute(holder.translatedPhrase.getText().toString());
            }
        }else{
            holder.playVoiceButton.setEnabled(false);
            holder.playVoiceButton.setAlpha(0.5f);
        }
    }

    @Override
    public int getItemCount() {
        return translations.size();
    }

    public class PhraseViewHolder extends RecyclerView.ViewHolder {
        public TextView englishPhrase;
        public TextView translatedPhrase;
        public ImageButton playVoiceButton;
        public ProgressBar progress;

        public PhraseViewHolder(View v) {
            super(v);
            englishPhrase = v.findViewById(R.id.saved_translation_phrase_textview);
            translatedPhrase = v.findViewById(R.id.saved_translation_translation_textview);
            playVoiceButton = v.findViewById(R.id.play_saved_text_to_speech);
            progress = v.findViewById(R.id.saved_voice_progress);
            progress.setVisibility(View.GONE);
            playVoiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardIndex = getAdapterPosition();
                    Log.d(TAG, "onClick: card index is " + cardIndex);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
