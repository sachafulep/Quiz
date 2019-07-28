package com.sacha.quiz.Firebase;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sacha.quiz.AdminQuizActivity;
import com.sacha.quiz.Classes.Question;
import com.sacha.quiz.MainActivity;
import com.sacha.quiz.QuizActivity;

import java.util.ArrayList;

public class FirebaseQuestion {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getQuizQuestions(int quizID, final String activity) {
        db.collection("questions")
                .whereEqualTo("quizID", quizID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Message msg = Message.obtain();
                            Bundle bdl = new Bundle();
                            ArrayList<Question> questions = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(MainActivity.TAG, document.getId() + " => " + document.getData());
                                questions.add(document.toObject(Question.class));
                            }

                            bdl.putString("type", "getQuestions");
                            bdl.putParcelableArrayList("questions", questions);
                            msg.setData(bdl);
                            if (activity.equals("AdminQuiz")) {
                                AdminQuizActivity.handler.sendMessage(msg);
                            } else if (activity.equals("Quiz")) {
                                QuizActivity.handler.sendMessage(msg);
                            }
                        } else {
                            Log.d(MainActivity.TAG, "Error getting questions: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void delete() {

    }

    public void clear() {

    }
}
