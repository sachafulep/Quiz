package com.sacha.quiz.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sacha.quiz.Classes.Player;
import com.sacha.quiz.MainActivity;
import com.sacha.quiz.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private List<Player> players;

    public ScoreAdapter(List<Player> players) {
        this.players = players;
        Collections.sort(this.players, new CustomComparator());
    }

    @Override
    public ScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = "";
        Player player = players.get(position);
        name = player.getFirstName() + " " + player.getLastName();

        int score = MainActivity.database.scoreDao().getTotalScore(player.getId());

        holder.tvName.setText(name);
        holder.tvScore.setText(Integer.toString(score));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvScore;

        public ViewHolder(View rootView) {
            super(rootView);
            tvName = rootView.findViewById(R.id.tvName);
            tvScore = rootView.findViewById(R.id.tvScore);
        }
    }

    public class CustomComparator implements Comparator<Player> {

        @Override
        public int compare(Player player, Player t1) {
            Integer player1Score = MainActivity.database.scoreDao().getTotalScore(t1.getId());
            Integer player2Score = MainActivity.database.scoreDao().getTotalScore(player.getId());

            return player1Score.compareTo(player2Score);
        }
    }
}
