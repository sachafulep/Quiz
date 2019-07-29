package com.sacha.quiz.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sacha.quiz.R;

import java.util.List;

public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.ViewHolder> {
    List<String> names;
    List<Integer> scores;

    public HighScoreAdapter(List<String> names, List<Integer> scores) {
        this.names = names;
        this.scores = scores;
    }

    @NonNull
    @Override
    public HighScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);

        return new HighScoreAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull HighScoreAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(names.get(position));
        holder.tvScore.setText(Integer.toString(scores.get(position)));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvScore;

        ViewHolder(View rootView) {
            super(rootView);
            tvName = rootView.findViewById(R.id.tvName);
            tvScore = rootView.findViewById(R.id.tvScore);
        }
    }
}
