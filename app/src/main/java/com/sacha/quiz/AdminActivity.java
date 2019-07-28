package com.sacha.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sacha.quiz.Adapters.QuizAdapter;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.Firebase.FirebaseQuiz;

import java.util.List;

public class AdminActivity extends AppCompatActivity {
    public static Handler handler;
    private QuizAdapter quizAdapter;
    private List<Quiz> quizzes;
    List<String> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setToolbar();

        setOnClickListeners();
        setupMsgHandler();

        new FirebaseQuiz().getAll();
    }

    private void setOnClickListeners() {
        findViewById(R.id.btnAddNewQuiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminQuizActivity.class);
                intent.putExtra("mode", AdminQuizActivity.CREATE);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnDeleteAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFirstWarningDialog();
            }
        });
    }

    private void showFirstWarningDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Weet je zeker dat je alles wil verwijderen?")
                .setPositiveButton("Ja, verwijder alles.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showSecondWarningDialog();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSecondWarningDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Weet je het echt zeker?")
                .setPositiveButton("Nee", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Ja, verwijder alles.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        dialog.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                if (!data.isEmpty()) {
                    switch (data.getString("type")) {
                        case "getQuizzes":
                            quizzes = data.getParcelableArrayList("quizzes");
                            fillRecyclerViews(quizzes);
                            break;
                        case "addQuiz":
                            Quiz quiz = data.getParcelable("quiz");
                            if (quizzes.contains(quiz)) {
                                quizzes.set(quizzes.indexOf(quiz), quiz);
                            } else {
                                quizzes.add(quiz);
                            }
                            quizAdapter.notifyDataSetChanged();
                            break;
                        case "deleteQuiz":
                            Quiz temp = null;

                            for (Quiz q : quizzes) {
                                if (q.getId() == data.getInt("quizID", -1)) {
                                    temp = q;
                                    break;
                                }
                            }

                            quizzes.remove(temp);
                            quizAdapter.notifyDataSetChanged();
                            break;
                        case "editQuiz":
                            Intent intent = new Intent(AdminActivity.this, AdminQuizActivity.class);
                            intent.putExtra("mode", AdminQuizActivity.EDIT);
                            intent.putExtra("id", data.getInt("id"));
                            startActivity(intent);
                            break;
                    }
                }
            }
        };
    }

    private void fillRecyclerViews(List<Quiz> quizzes) {
        RecyclerView rvQuizzes = findViewById(R.id.rvQuizzes);
        rvQuizzes.setHasFixedSize(true);
        rvQuizzes.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(quizzes);
        rvQuizzes.setAdapter(quizAdapter);
    }

    private void setToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
