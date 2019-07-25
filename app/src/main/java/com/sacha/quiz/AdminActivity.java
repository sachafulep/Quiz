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

import com.sacha.quiz.Adapters.PlayerAdapter;
import com.sacha.quiz.Adapters.QuizAdapter;
import com.sacha.quiz.Classes.Player;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.Database.Database;
import com.sacha.quiz.Firebase.FirebaseQuiz;
import com.sacha.quiz.FirebaseClasses.QuizF;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    public static Handler handler;
    Database database;
    QuizAdapter quizAdapter;
    PlayerAdapter playerAdapter;
    List<Quiz> quizzes;
    List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setToolbar();

        database = Database.getInstance();

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
                        database.scoreDao().clear();
                        database.questionDao().clear();
                        database.quizDao().clear();
                        database.playerDao().clear();

//                        fillRecyclerViews();

                        dialog.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                if (!data.isEmpty()) {
                    if (data.containsKey("quizzes")) {
                        ArrayList<? extends QuizF> quizzes = data.getParcelableArrayList("quizzes");
                        fillRecyclerViews((List<QuizF>) quizzes);
                    } else {
                        Intent intent = new Intent(AdminActivity.this, AdminQuizActivity.class);
                        intent.putExtra("mode", AdminQuizActivity.EDIT);
                        intent.putExtra("id", data.getInt("id"));
                        startActivity(intent);
                    }
                }
            }
        };
    }

//    private void fillRecyclerViews() {
//        RecyclerView rvQuizzes = findViewById(R.id.rvQuizzes);
//        RecyclerView rvPlayers = findViewById(R.id.rvPlayers);
//        rvQuizzes.setHasFixedSize(true);
//        rvPlayers.setHasFixedSize(true);
//        rvQuizzes.setLayoutManager(new LinearLayoutManager(this));
//        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
//        quizzes = database.quizDao().getAll();
//        players = database.playerDao().getAll();
//        quizAdapter = new QuizAdapter(quizzes);
//        playerAdapter = new PlayerAdapter(players);
//        rvQuizzes.setAdapter(quizAdapter);
//        rvPlayers.setAdapter(playerAdapter);
//    }

    private void fillRecyclerViews(List<QuizF> quizzes) {
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

//        fillRecyclerViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
