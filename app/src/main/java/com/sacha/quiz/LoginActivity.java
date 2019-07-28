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
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.Firebase.FirebasePlayer;
import com.sacha.quiz.Firebase.FirebaseScore;

public class LoginActivity extends AppCompatActivity {
    public static Handler handler;
    static Quiz activeQuiz;
    private Button btnSubmit;
    private Button btnAdmin;
    private EditText etFirstName;
    private EditText etLastName;
    private FirebasePlayer firebasePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnAdmin = findViewById(R.id.btnAdmin);

        firebasePlayer = new FirebasePlayer();

        fillRecyclerView();
        setupMsgHandler();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activeQuiz == null) {
                    showErrorDialog(getString(R.string.error_select_quiz));
                } else if (playerNameNotFilledIn()) {
                    showErrorDialog(getString(R.string.error_invalid_name));
                } else {
                    firebasePlayer.checkIfExists(getFullName());
                }
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        AdminActivity.class);
                startActivity(intent);
//                showAdminDialog();
            }
        });
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

    private void fillRecyclerView() {
//        RecyclerView rvScores = findViewById(R.id.rvScores);
//        rvScores.setHasFixedSize(true);
//        rvScores.setLayoutManager(new LinearLayoutManager(this));
//        List<Player> players = MainActivity.database.playerDao().getAll();
//        ScoreAdapter adapter = new ScoreAdapter(players);
//        rvScores.setAdapter(adapter);
    }

    private boolean playerHasAlreadyTakenThisQuiz() {
        return false;
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

    private void startQuiz(String fullName) {
        Intent intent = new Intent(LoginActivity.this, QuizActivity.class);

        intent.putExtra("playerName", fullName);
        intent.putExtra("quizID", activeQuiz.getId());

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

    private void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                if (!data.isEmpty()) {
                    switch (data.getString("type")) {
                        case "playerExists":
                            new FirebaseScore().checkIfPlayerHasTakenQuiz(activeQuiz.getId(), getFullName());
                            break;
                        case "playerDoesNotExist":
                            firebasePlayer.insert(getFirstName(), getLastName());
                            break;
                        case "insertPlayer":
                            startQuiz(getFullName());
                            break;
                        case "hasTakenQuiz":
                            showErrorDialog(getString(R.string.error_already_taken));
                            break;
                        case "hasNotTakenQuiz":
                            startQuiz(getFullName());
                            break;
                    }
                }
            }
        };
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