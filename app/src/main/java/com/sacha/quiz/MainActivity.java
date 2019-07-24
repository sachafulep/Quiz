package com.sacha.quiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sacha.quiz.Adapters.ScoreAdapter;
import com.sacha.quiz.Classes.Player;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.Database.Database;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "GoogleAuth";
    public static Database database;
    public static Quiz activeQuiz;
    EditText etFirstName;
    EditText etLastName;
    ScoreAdapter adapter;

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
                    showErrorDialog("Vertel Tamas dat hij een quiz moet selecteren.");
                } else if (playerNameInvalid()) {
                    showErrorDialog("Vul alstublieft je voor en achternaam in.");
                } else if (playerHasAlreadyTakenThisQuiz()) {
                    showErrorDialog("Je hebt deze quiz al gedaan.");
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

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            firebaseAuth(account);
        } else {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
        }
    }

    private void firebaseAuth(GoogleSignInAccount account) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuth(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
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
        List<Player> players = database.playerDao().getAll();
        adapter = new ScoreAdapter(players);
        rvScores.setAdapter(adapter);
    }

    private boolean playerHasAlreadyTakenThisQuiz() {
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();

        Player player = database.playerDao().get(firstName, lastName);

        return player != null && database.scoreDao().get(activeQuiz.getId(), player.getId()) != null;
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

        Player player = database.playerDao().get(firstName, lastName);

        if (player == null) {
            int id = database.playerDao().getHighestID() + 1;
            player = new Player(id, firstName, lastName);
            database.playerDao().add(player);
        }

        Intent intent = new Intent(MainActivity.this, QuizActivity.class);

        intent.putExtra("playerID", player.getId());
        intent.putExtra("quizID", activeQuiz.getId());

        etFirstName.setText("");
        etLastName.setText("");

        startActivity(intent);
    }

    public void showAdminDialog() {
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