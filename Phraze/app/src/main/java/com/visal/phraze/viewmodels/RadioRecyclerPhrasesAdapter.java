package com.visal.phraze.viewmodels;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.visal.phraze.R;
import com.visal.phraze.model.Phrase;

import java.util.List;

public class RadioRecyclerPhrasesAdapter extends RecyclerView.Adapter<RadioRecyclerPhrasesAdapter.PhraseViewHolder> {
    private static final String TAG = RadioRecyclerPhrasesAdapter.class.getSimpleName();
    private List<Phrase> phrases;
    private int selectedCardIndex = -1;
    private static RecyclerViewRadioChangeListener radioButtonListener;

    public RadioRecyclerPhrasesAdapter(List<Phrase> phrases, RecyclerViewRadioChangeListener listener) {
        this.phrases = phrases;
        radioButtonListener = listener;
    }


    @NonNull
    @Override
    public PhraseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_card_item, parent, false);
        return new PhraseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhraseViewHolder holder, final int position) {
        String phrase = phrases.get(position).getPhrase();
        holder.phraseTextview.setText(phrase);
        holder.daysAgoTextview.setText(DateTime.getDaysPassed(phrases.get(position).getDateAdded()));
        if (selectedCardIndex == position){
            holder.radioButton.setChecked(true);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#6A6A6A"));
            holder.phraseTextview.setTextColor(Color.parseColor("#44DEA5"));
        }else{
            holder.radioButton.setChecked(false);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            holder.phraseTextview.setTextColor(Color.parseColor("#333333"));
        }
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public class PhraseViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public TextView phraseTextview;
        public TextView daysAgoTextview;
        private RadioButton radioButton;
        public PhraseViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.radio_cardview);
            phraseTextview = v.findViewById(R.id.edit_phrases_card_textview);
            daysAgoTextview = v.findViewById(R.id.edit_phrase_card_date);
            radioButton = v.findViewById(R.id.edit_phrase_radio);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCardIndex = getAdapterPosition();
                    Log.d(TAG, "onClick: the selected index is " + selectedCardIndex);
                    radioButtonListener.recyclerViewRadioClick(v,selectedCardIndex);
                    notifyDataSetChanged();
                }
            });
        }


    }
}
