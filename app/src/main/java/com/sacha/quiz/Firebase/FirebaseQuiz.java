package com.sacha.quiz.Firebase;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.sacha.quiz.AdminActivity;
import com.sacha.quiz.AdminQuizActivity;
import com.sacha.quiz.FirebaseClasses.QuizF;
import com.sacha.quiz.MainActivity;

import java.util.ArrayList;

public class FirebaseQuiz {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void insert(QuizF quiz) {
        db.collection("quizzes").document(String.valueOf(quiz.getId()))
                .set(quiz)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(MainActivity.TAG, "Quiz successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(MainActivity.TAG, "Error writing quiz", e);
                    }
                });
    }

    public void get(int id) {
        db.collection("quizzes").document(String.valueOf(id))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Message msg = Message.obtain();
                        Bundle bdl = new Bundle();
                        bdl.putParcelable("quiz", documentSnapshot.toObject(QuizF.class));
                        msg.setData(bdl);
                        AdminQuizActivity.handler.sendMessage(msg);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void getAll() {
        db.collection("quizzes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Message msg = Message.obtain();
                            Bundle bdl = new Bundle();
                            ArrayList<QuizF> quizzes = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(MainActivity.TAG, document.getId() + " => " + document.getData());
                                quizzes.add(document.toObject(QuizF.class));
                            }

                            bdl.putParcelableArrayList("quizzes", quizzes);
                            msg.setData(bdl);
                            AdminActivity.handler.sendMessage(msg);
                        } else {
                            Log.d(MainActivity.TAG, "Error getting quizzes: ", task.getException());
                        }
                    }
                });
    }

    public void update() {

    }

    public void delete() {

    }

    public void clear() {

    }
}
