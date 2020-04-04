package com.visal.phraze;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EditPhrasesAdapter extends RecyclerView.Adapter<EditPhrasesAdapter.PhraseViewHolder> {
    private static final String TAG = EditPhrasesAdapter.class.getSimpleName();
    private List<String> phrases;
    private int selectedCardIndex = -1;
    private static RecyclerViewRadioChangeListener radioButtonListener;

    public EditPhrasesAdapter(){
        phrases = new ArrayList<>();
    }

    public EditPhrasesAdapter(List<String> phrases, RecyclerViewRadioChangeListener listener ) {
        this.phrases = phrases;
        radioButtonListener = listener;
    }

    public int getSelectedCardIndex() {
        return selectedCardIndex;
    }

    @NonNull
    @Override
    public PhraseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_phrases_card_item, parent, false);
        return new PhraseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhraseViewHolder holder, final int position) {
        String phrase = phrases.get(position);
        holder.textView.setText(phrase);
        if (selectedCardIndex == position){
            holder.selectedCard.setChecked(true);
        };
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public class PhraseViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        private RadioButton selectedCard;
        public PhraseViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.edit_phrases_card_textview);
            selectedCard = v.findViewById(R.id.edit_phrase_radio);

            selectedCard.setOnClickListener(new View.OnClickListener() {
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