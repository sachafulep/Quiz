package com.sacha.quiz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sacha.quiz.Classes.Question;
import com.sacha.quiz.Classes.Score;
import com.sacha.quiz.Database.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    int playerID;
    int quizID;
    List<Question> questions;

    TextView tvQuestion;
    Button btnSubmit;
    List<Button> answerButtons;

    Question currentQuestion;
    String currentAnswer;

    Database database = Database.getInstance();

    int index = 0;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnSubmit = findViewById(R.id.btnSubmit);

        playerID = getIntent().getIntExtra("playerID", -1);
        quizID = getIntent().getIntExtra("quizID", -1);
        questions = database.questionDao().getQuestions(quizID);

        setUpButtons();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        updateQuestion();
        index++;
    }

    public void updateQuestion() {
        currentQuestion = questions.get(index);
        tvQuestion.setText(currentQuestion.getText());
        List<String> answers = currentQuestion.getAnswerList();
        Collections.shuffle(answers);

        for (int i = 0; i < answerButtons.size(); i++) {
            Button button = answerButtons.get(i);
            button.setBackgroundColor(getColor(R.color.colorPrimary));

            if (answers.size() > i) {
                button.setText(answers.get(i));
            } else {
                button.setText("");
            }
        }
    }

    void checkAnswer() {
        if (currentAnswer != null) {
            if (currentAnswer.equals(currentQuestion.getCorrectAnswer())) {
                score++;
            }

            if (questions.size() > index) {
                updateQuestion();
                currentAnswer = null;
                index++;
            } else {
                showScoreDialog();
            }
        }
    }

    private void showScoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);

        if (score == 1) {
            builder.setTitle("Je hebt " + score + " punt gescoord!");
        } else {
            builder.setTitle("Je hebt " + score + " punten gescoord!");
        }

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int scoreID = database.scoreDao().getHighestID() + 1;
                database.scoreDao().add(new Score(scoreID, quizID, playerID, score));
                finish();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setUpButtons() {
        AnswerListener listener = new AnswerListener();
        answerButtons = new ArrayList<>();
        answerButtons.add((Button) findViewById(R.id.btn_answer0));
        answerButtons.add((Button) findViewById(R.id.btn_answer1));
        answerButtons.add((Button) findViewById(R.id.btn_answer2));
        answerButtons.add((Button) findViewById(R.id.btn_answer3));
        for (Button button : answerButtons) {
            button.setOnClickListener(listener);
            button.setBackgroundColor(getColor(R.color.colorPrimary));
        }
    }

    class AnswerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;

            if (button.getText() != "") {
                currentAnswer = (String) button.getText();

                for (Button answerButton : answerButtons) {
                    answerButton.setBackgroundColor(getColor(R.color.colorPrimary));
                }

                button.setBackgroundColor(getColor(R.color.colorAccent));
            }
        }
    }
}