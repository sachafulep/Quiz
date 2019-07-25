package com.sacha.quiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sacha.quiz.Adapters.ScoreAdapter;
import com.sacha.quiz.Classes.Player;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.Database.Database;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static Database database;
    static Quiz activeQuiz;
    private EditText etFirstName;
    private EditText etLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Database.getMainInstance(MainActivity.this);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        fillRecyclerView();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activeQuiz == null) {
                    showErrorDialog(getString(R.string.error_select_quiz));
                } else if (playerNameInvalid()) {
                    showErrorDialog(getString(R.string.error_invalid_name));
                } else if (playerHasAlreadyTakenThisQuiz()) {
                    showErrorDialog(getString(R.string.error_already_taken));
                } else {
                    startQuiz();
                }
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminDialog();
            }
        });
    }

    private boolean playerNameInvalid() {
        if (etFirstName.getText().toString().isEmpty()) {
            return true;
        } else return etLastName.getText().toString().isEmpty();

    }

    private void fillRecyclerView() {
        RecyclerView rvScores = findViewById(R.id.rvScores);
        rvScores.setHasFixedSize(true);
        rvScores.setLayoutManager(new LinearLayoutManager(this));
        List<Player> players = MainActivity.database.playerDao().getAll();
        ScoreAdapter adapter = new ScoreAdapter(players);
        rvScores.setAdapter(adapter);
    }

    private boolean playerHasAlreadyTakenThisQuiz() {
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();

        Player player = MainActivity.database.playerDao().get(firstName, lastName);

        return player != null && MainActivity.database.scoreDao().get(activeQuiz.getId(), player.getId()) != null;
    }

    private void showErrorDialog(String message) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle(message)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startQuiz() {
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();

        Player player = MainActivity.database.playerDao().get(firstName, lastName);

        if (player == null) {
            int id = MainActivity.database.playerDao().getHighestID() + 1;
            player = new Player(id, firstName, lastName);
            MainActivity.database.playerDao().add(player);
        }

        Intent intent = new Intent(MainActivity.this, QuizActivity.class);

        intent.putExtra("playerID", player.getId());
        intent.putExtra("quizID", activeQuiz.getId());

        etFirstName.setText("");
        etLastName.setText("");

        startActivity(intent);
    }

    private void showAdminDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText etPassword = new EditText(MainActivity.this);
        builder.setView(etPassword)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (etPassword.getText().toString().equals("00000")) {
                            Intent intent = new Intent(MainActivity.this,
                                    AdminActivity.class);
                            startActivity(intent);
                        } else {
                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, "Verkeerd wachtwoord",
                                    duration);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fillRecyclerView();

        TextView tvCurrentQuiz = findViewById(R.id.tvCurrentQuiz);

        if (activeQuiz == null) {
            tvCurrentQuiz.setText(getString(R.string.no_quiz));
        } else {
            tvCurrentQuiz.setText(activeQuiz.getTitle());
        }
    }
}