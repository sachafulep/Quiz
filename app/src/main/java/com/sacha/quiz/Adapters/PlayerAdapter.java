package com.sacha.quiz.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sacha.quiz.R;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    private List<String> players;

    public PlayerAdapter(List<String> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        return new PlayerAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(players.get(position));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        ViewHolder(View rootView) {
            super(rootView);
            tvName = rootView.findViewById(R.id.tvName);
        }
    }
}
