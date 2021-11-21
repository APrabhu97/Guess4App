package com.anish.anish_p4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GuessAdapter extends RecyclerView.Adapter<GuessAdapter.ViewHolder> {

    private List<GuessItem> guessList;

    public GuessAdapter(List<GuessItem> movieList){
        this.guessList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listView = inflater.inflate(R.layout.guess_item, parent, false);
        return new ViewHolder(listView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuessItem m = guessList.get(position);
        holder.guess.setText(m.getGuess());
        holder.correctDigits.setText(m.getCorrectGuesses()+"");
        holder.incorrectPositions.setText(m.getIncorrectPositions()+"");
        holder.missingDigit.setText(Character.toString(m.getIncorrectDigit()));
        holder.guessItem = m;
    }

    @Override
    public int getItemCount() {
        return guessList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView guess;
        public TextView correctDigits;
        public TextView incorrectPositions;
        public TextView missingDigit;
        private View itemView;
        private GuessItem guessItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            guess = (TextView) itemView.findViewById(R.id.guess);
            correctDigits = (TextView) itemView.findViewById(R.id.correctDigits);
            incorrectPositions = (TextView) itemView.findViewById(R.id.incorrectPosition);
            missingDigit = (TextView) itemView.findViewById(R.id.missingDigit);
            this.itemView = itemView;
        }
    }
}

