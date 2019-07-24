package com.sacha.quiz.Adapters;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sacha.quiz.AdminQuizActivity;
import com.sacha.quiz.Classes.Question;
import com.sacha.quiz.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<Question> questions;

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        return new QuestionAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(QuestionAdapter.ViewHolder holder, int position) {
        String text = "";
        Question question = questions.get(position);
        text += question.getText();
        holder.tvTitle.setText(text);
        holder.question = questions.get(position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public Question question;

        public ViewHolder(View rootView) {
            super(rootView);
            tvTitle = rootView.findViewById(R.id.tvName);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message msg = Message.obtain();
                    Bundle bdl = new Bundle();
                    List<String> answers = question.getAnswerList();

                    bdl.putInt("id", question.getId());
                    bdl.putString("text", question.getText());

                    for (int i = 0; i < answers.size(); i++) {
                        String key = "answer" + i;
                        bdl.putString(key, answers.get(i));
                    }

                    msg.setData(bdl);
                    AdminQuizActivity.handler.sendMessage(msg);
                }
            });
        }
    }
}

