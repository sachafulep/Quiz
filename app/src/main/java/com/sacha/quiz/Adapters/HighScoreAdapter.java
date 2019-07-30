package com.sacha.quiz.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sacha.quiz.Classes.User;
import com.sacha.quiz.R;

import java.util.List;

public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.ViewHolder> {
    List<User> users;

    public HighScoreAdapter(List<User> users) {
        this.users = users;
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
        holder.tvName.setText(users.get(position).getFirstName() + " " + users.get(position).getLastName());
        holder.tvScore.setText(Integer.toString(users.get(position).getScore()));
    }

    @Override
    public int getItemCount() {
        return users.size();
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
