package com.sacha.quiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.sacha.quiz.Adapters.HighScoreAdapter;
import com.sacha.quiz.Adapters.QuizAdapter;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.Classes.User;
import com.sacha.quiz.Firebase.FirebasePlayer;
import com.sacha.quiz.Firebase.FirebaseQuiz;
import com.sacha.quiz.Firebase.FirebaseScore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LoginActivity extends AppCompatActivity {
    public static Handler handler;
    static Quiz currentQuiz;
    private static int selectedQuizID;
    private Button btnSubmit;
    private Button btnAdmin;
    private Button btnSelectQuiz;
    private EditText etFirstName;
    private EditText etLastName;
    private FirebasePlayer firebasePlayer;
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnAdmin = findViewById(R.id.btnAdmin);
        btnSelectQuiz = findViewById(R.id.btnSelectQuiz);

        firebasePlayer = new FirebasePlayer();

        setupMsgHandler();
        setOnClickListeners();

        firebasePlayer.getHighScores();
    }

    private void setOnClickListeners() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuiz == null) {
                    showErrorDialog(getString(R.string.error_select_quiz));
                } else {
                    selectedQuizID = currentQuiz.getId();
                    checkForErrors();
                }
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminDialog();
            }
        });

        btnSelectQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectQuizDialog();
            }
        });
    }

    private void checkForErrors() {
        if (playerNameNotFilledIn()) {
            showErrorDialog(getString(R.string.error_invalid_name));
        } else {
            firebasePlayer.checkIfExists(getFullName());
        }
    }

    private String getFirstName() {
        String firstName = etFirstName.getText().toString().toLowerCase().trim();
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
    }

    private String getLastName() {
        String lastName = etLastName.getText().toString().toLowerCase().trim();
        return lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
    }

    private String getFullName() {
        String firstName = etFirstName.getText().toString().toLowerCase().trim();
        String lastName = etLastName.getText().toString().toLowerCase().trim();

        String firstNameCapitalised = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        String lastNameCapitalised = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);

        return firstNameCapitalised + " " + lastNameCapitalised;
    }

    private boolean playerNameNotFilledIn() {
        if (etFirstName.getText().toString().isEmpty()) {
            return true;
        } else {
            return etLastName.getText().toString().isEmpty();
        }
    }

    private void fillRecyclerView(ArrayList<User> users) {
        Collections.sort(users, new HighScoreComparator());
        RecyclerView rvScores = findViewById(R.id.rvScores);
        rvScores.setHasFixedSize(true);
        rvScores.setLayoutManager(new LinearLayoutManager(this));
        HighScoreAdapter adapter = new HighScoreAdapter(users);
        rvScores.setAdapter(adapter);
    }

    static class HighScoreComparator implements Comparator<User> {
        public int compare(User u1, User u2) {
            if (u1.getScore() < u2.getScore()) {
                return 1;
            } else if (u1.getScore() > u2.getScore()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(message)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startQuiz(String fullName, int id) {
        Intent intent = new Intent(LoginActivity.this, QuizActivity.class);

        intent.putExtra("playerName", fullName);
        intent.putExtra("quizID", id);

        etFirstName.setText("");
        etLastName.setText("");

        startActivity(intent);
    }

    private void showAdminDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText etPassword = new EditText(LoginActivity.this);
        builder.setView(etPassword)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (etPassword.getText().toString().equals("00000")) {
                            Intent intent = new Intent(LoginActivity.this,
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

    private void showSelectQuizDialog() {
        new FirebaseQuiz().getAll("Login");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogView = getLayoutInflater().inflate(R.layout.dialog_select_quiz, null);
        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        alert.show();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    private void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                if (!data.isEmpty()) {
                    switch (data.getString("type")) {
                        case "playerExists":
                            new FirebaseScore().checkIfPlayerHasTakenQuiz(selectedQuizID,
                                    getFullName()
                            );
                            break;
                        case "playerDoesNotExist":
                            firebasePlayer.insert(getFirstName(), getLastName());
                            break;
                        case "insertPlayer":
                        case "hasNotTakenQuiz":
                            startQuiz(getFullName(), selectedQuizID);
                            finish();
                            break;
                        case "hasTakenQuiz":
                            showErrorDialog(getString(R.string.error_already_taken));
                            break;
                        case "getHighScores":
                            fillRecyclerView(data.<User>getParcelableArrayList("users"));
                            break;
                        case "selectQuiz":
                            selectedQuizID = data.getInt("id");
                            checkForErrors();
                            break;
                        case "getQuizzes":
                            RecyclerView rvSelectQuiz = dialogView.findViewById(R.id.rvSelectQuiz);
                            rvSelectQuiz.setHasFixedSize(true);
                            rvSelectQuiz.setLayoutManager(new LinearLayoutManager(LoginActivity.this));
                            QuizAdapter adapter = new QuizAdapter(
                                    data.<Quiz>getParcelableArrayList("quizzes"),
                                    true
                            );
                            rvSelectQuiz.setAdapter(adapter);
                            break;
                        case "insertScore":
                            firebasePlayer.getHighScores();
                            break;
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView tvCurrentQuiz = findViewById(R.id.tvCurrentQuiz);

        if (currentQuiz == null) {
            tvCurrentQuiz.setText(getString(R.string.no_quiz));
        } else {
            tvCurrentQuiz.setText(currentQuiz.getTitle());
        }
    }
}
