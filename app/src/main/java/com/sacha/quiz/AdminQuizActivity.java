package com.sacha.quiz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sacha.quiz.Adapters.QuestionAdapter;
import com.sacha.quiz.Classes.Question;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.Firebase.FirebaseQuestion;
import com.sacha.quiz.Firebase.FirebaseQuiz;

import java.util.ArrayList;
import java.util.List;

public class AdminQuizActivity extends AppCompatActivity {
    static final int CREATE = 1;
    static final int EDIT = 2;
    public static Handler handler;

    private int mode;
    private ArrayList<Question> questions;
    private QuestionAdapter adapter;
    private boolean questionSelected = false;
    private String currentQuestionID = "";
    private Quiz currentQuiz;

    private FirebaseQuiz firebaseQuiz;

    private EditText etQuestion;
    private EditText etAnswer0;
    private EditText etAnswer1;
    private EditText etAnswer2;
    private EditText etAnswer3;
    private Button btnDeleteQuestion;
    private TextView tvPlus;
    private EditText etQuizName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quiz);
        setToolbar();

        etQuestion = findViewById(R.id.etQuestion);
        etAnswer0 = findViewById(R.id.etAnswer0);
        etAnswer1 = findViewById(R.id.etAnswer1);
        etAnswer2 = findViewById(R.id.etAnswer2);
        etAnswer3 = findViewById(R.id.etAnswer3);
        btnDeleteQuestion = findViewById(R.id.btnDeleteQuestion);
        tvPlus = findViewById(R.id.tvPlus);
        etQuizName = findViewById(R.id.etQuizName);

        firebaseQuiz = new FirebaseQuiz();
        FirebaseQuestion firebaseQuestion = new FirebaseQuestion();

        setupMsgHandler();

        mode = getIntent().getIntExtra("mode", -1);

        if (mode == EDIT) {
            int quizID = getIntent().getIntExtra("id", -1);
            firebaseQuiz.get(quizID);
            firebaseQuestion.getQuizQuestions(quizID, "AdminQuiz");
        } else {
            currentQuiz = new Quiz(-1, "");
            questions = new ArrayList<>();
            fillRecyclerViews();
        }

        setButtonClickListeners();
    }

    private void setButtonClickListeners() {
        findViewById(R.id.btnSaveQuestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionIsValid()) {
                    if (questionSelected) {
                        updateQuestion();
                    } else {
                        addQuestion();
                    }
                }
            }
        });

        tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideEditQuestionUI();
            }
        });

        findViewById(R.id.btnSaveQuiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quizIsValid()) {
                    saveQuiz();
                }
            }
        });

        btnDeleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Question question : questions) {
                    if (question.getId().equals(currentQuestionID)) {
                        questions.remove(question);
                        adapter.notifyDataSetChanged();
                        hideEditQuestionUI();
                        emptyInputFields();
                        break;
                    }
                }
            }
        });

        findViewById(R.id.btnDeleteQuiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminQuizActivity.this);
                builder.setMessage("Weet je zeker dat je deze quiz wil verwijderen?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteQuiz();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        findViewById(R.id.btnSetActiveQuiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.activeQuiz = currentQuiz;
                findViewById(R.id.ivActive).setVisibility(View.VISIBLE);
            }
        });
    }

    private void deleteQuiz() {
        if (mode == CREATE) {
            finish();
        } else {
            firebaseQuiz.delete(currentQuiz.getId());
        }

        LoginActivity.activeQuiz = null;

        finish();
    }

    private boolean questionIsValid() {
        String message;
        List<EditText> inputFields = new ArrayList<>();
        int counter = 0;
        inputFields.add(etAnswer0);
        inputFields.add(etAnswer1);
        inputFields.add(etAnswer2);
        inputFields.add(etAnswer3);

        for (EditText et : inputFields) {
            if (!et.getText().toString().equals("")) {
                counter++;
            }
        }

        if (etQuestion.getText().toString().equals("")) {
            message = "Je moet een vraag invullen.";
            displayWarningDialog(message);
            return false;
        } else if (etAnswer0.getText().toString().equals("")) {
            message = "Je moet een correct antwoord invullen.";
            displayWarningDialog(message);
            return false;
        } else if (counter < 2) {
            message = "Je moet minstens twee vragen invullen";
            displayWarningDialog(message);
            return false;
        }

        return true;
    }

    private boolean quizIsValid() {
        String message;

        if (questions.size() < 1) {
            message = "Je moet minstens een vraag toevoegen voor je de quiz op kan slaan.";
            displayWarningDialog(message);
            return false;
        } else if (etQuizName.getText().toString().equals("")) {
            message = "Je moet de quiz een naam geven voor je hem op kan slaan.";
            displayWarningDialog(message);
            return false;
        }

        return true;
    }

    private void displayWarningDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminQuizActivity.this);
        builder.setMessage(message)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveQuiz() {
        currentQuiz.setTitle(etQuizName.getText().toString());

        if (mode == CREATE) {
            firebaseQuiz.insert(currentQuiz, questions);
        } else {
            firebaseQuiz.set(currentQuiz, questions);
        }

        finish();
    }

    private void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                if (!data.isEmpty()) {
                    if (data.getString("type").equals("getQuiz")) {
                        currentQuiz = data.getParcelable("quiz");
                        if (currentQuiz != null) {
                            etQuizName.setText(currentQuiz.getTitle());
                        }
                    } else if (data.getString("type").equals("getQuestions")) {
                        questions = data.getParcelableArrayList("questions");
                        fillRecyclerViews();
                    } else if (data.getString("type").equals("editQuestion")) {
                        showEditQuestionUI(data);
                    }
                }
            }
        };
    }

    private void showEditQuestionUI(Bundle data) {
        questionSelected = true;
        currentQuestionID = data.getString("id");

        btnDeleteQuestion.setVisibility(View.VISIBLE);
        tvPlus.setVisibility(View.VISIBLE);

        emptyInputFields();

        etQuestion.setText(data.getString("text"));
        etAnswer0.setText(data.getString("answer0"));
        etAnswer1.setText(data.getString("answer1"));

        if (data.getString("answer2") != null) {
            etAnswer2.setText(data.getString("answer2"));
        }

        if (data.getString("answer3") != null) {
            etAnswer3.setText(data.getString("answer3"));
        }
    }

    private void hideEditQuestionUI() {
        questionSelected = false;

        btnDeleteQuestion.setVisibility(View.GONE);
        tvPlus.setVisibility(View.GONE);

        emptyInputFields();
    }

    private void emptyInputFields() {
        etQuestion.setText("");
        etAnswer0.setText("");
        etAnswer1.setText("");
        etAnswer2.setText("");
        etAnswer3.setText("");
    }

    private void addQuestion() {
        String text = etQuestion.getText().toString();
        String correntAnswer = etAnswer0.getText().toString();
        List<String> answers = getAnswers();
        emptyInputFields();
        etQuestion.setText("");

        questions.add(new Question(-1, text, Question.convertAnswerList(answers), correntAnswer));
        adapter.notifyDataSetChanged();
    }

    private void updateQuestion() {
        String text = etQuestion.getText().toString();
        String correntAnswer = etAnswer0.getText().toString();
        List<String> answers = getAnswers();
        emptyInputFields();
        etQuestion.setText("");

        for (Question question : questions) {
            if (question.getId().equals(currentQuestionID)) {
                question.setAnswers(Question.convertAnswerList(answers));
                question.setCorrectAnswer(correntAnswer);
                question.setText(text);
                break;
            }
        }

        adapter.notifyDataSetChanged();
    }

    private List<String> getAnswers() {
        List<String> answers = new ArrayList<>();
        String correntAnswer = etAnswer0.getText().toString();
        String answer1 = etAnswer1.getText().toString();
        String answer2 = etAnswer2.getText().toString();
        String answer3 = etAnswer3.getText().toString();
        answers.add(correntAnswer);
        answers.add(answer1);
        answers.add(answer2);
        answers.add(answer3);
        return answers;
    }

    private void fillRecyclerViews() {
        RecyclerView rvQuestions = findViewById(R.id.rvQuestions);
        rvQuestions.setHasFixedSize(true);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionAdapter(questions);
        rvQuestions.setAdapter(adapter);
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
    public boolean onSupportNavigateUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminQuizActivity.this);
        builder.setMessage("Weet je zeker dat je terug wil gaan zonder de quiz op te slaan?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (mode == CREATE) {
                            LoginActivity.activeQuiz = null;
                        }

                        onBackPressed();
                    }
                })
                .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }
}
