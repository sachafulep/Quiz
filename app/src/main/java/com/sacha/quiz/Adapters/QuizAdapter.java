package com.sacha.quiz.Adapters;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.sacha.quiz.AdminActivity;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.LoginActivity;
import com.sacha.quiz.R;

import javax.annotation.Nonnull;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    private List<Quiz> quizzes;
    private boolean isDialog;

    public QuizAdapter(List<Quiz> quizzes, boolean isDialog) {
        this.quizzes = quizzes;
        this.isDialog = isDialog;
    }

    @Nonnull
    @Override
    public QuizAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;

        if (isDialog) {
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_dialog, parent, false);
        } else {
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
        }

        return new QuizAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(QuizAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(quizzes.get(position).getTitle());
        holder.quiz = quizzes.get(position);
        holder.isDialog = isDialog;
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Quiz quiz;
        boolean isDialog;

        ViewHolder(View rootView) {
            super(rootView);
            tvName = rootView.findViewById(R.id.tvName);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message msg = Message.obtain();
                    Bundle bdl = new Bundle();
                    if (isDialog) {
                        bdl.putString("type", "selectQuiz");
                        bdl.putInt("id", quiz.getId());
                        msg.setData(bdl);
                        LoginActivity.handler.sendMessage(msg);
                    } else {
                        bdl.putString("type", "editQuiz");
                        bdl.putInt("id", quiz.getId());
                        msg.setData(bdl);
                        AdminActivity.handler.sendMessage(msg);
                    }
                }
            });
        }
    }
}

