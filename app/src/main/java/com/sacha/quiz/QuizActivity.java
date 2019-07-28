package com.sacha.quiz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.sacha.quiz.Classes.Question;
import com.sacha.quiz.Classes.Score;
import com.sacha.quiz.Firebase.FirebaseQuestion;
import com.sacha.quiz.Firebase.FirebaseScore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    public static Handler handler;
    private String playerName;
    private int quizID;
    private List<Question> questions;
    private TextView tvQuestion;
    private Button btnSubmit;
    private List<Button> answerButtons;

    private Question currentQuestion;
    private String currentAnswer;

    private int index = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnSubmit = findViewById(R.id.btnSubmit);

        playerName = getIntent().getStringExtra("playerName");
        quizID = getIntent().getIntExtra("quizID", -1);

        new FirebaseQuestion().getQuizQuestions(quizID, "Quiz");

        setupMsgHandler();
    }

    private void startQuiz() {
        setUpButtons();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        updateQuestion();
    }

    private void updateQuestion() {
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

        currentAnswer = null;
        index++;
    }

    private void checkAnswer() {
        if (currentAnswer != null) {
            if (currentAnswer.equals(currentQuestion.getCorrectAnswer())) {
                score++;
            }

            if (questions.size() > index) {
                updateQuestion();
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
                new FirebaseScore().insert(new Score(quizID, playerName, score));
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

    private void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                if (!data.isEmpty()) {
                    if (data.getString("type").equals("getQuestions")) {
                        questions = data.getParcelableArrayList("questions");
                        startQuiz();
                    }
                }
            }
        };
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