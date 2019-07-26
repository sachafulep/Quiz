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
import com.sacha.quiz.FirebaseClasses.QuestionF;
import com.sacha.quiz.FirebaseClasses.QuizF;
import com.sacha.quiz.MainActivity;

import java.util.ArrayList;

public class FirebaseQuiz {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String quizID;

    public void insertQuiz(final QuizF quiz, final ArrayList<QuestionF> questions) {
        db.collection("quizzes")
                .orderBy("id", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String quizID = "";

                        if (queryDocumentSnapshots.size() < 1) {
                            quizID = "0";
                        } else {
                            String temp = queryDocumentSnapshots.getDocuments().get(0).get("id").toString();
                            quizID = Integer.toString(Integer.parseInt(temp) + 1);
                        }

                        insert(quizID, quiz, questions);
                    }
                });
    }

    private void insert(String quizID, final QuizF quiz, ArrayList<QuestionF> questions) {
        WriteBatch batch = db.batch();

        DocumentReference quizRef = db.collection("quizzes").document(quizID);
        quiz.setId(Integer.parseInt(quizID));
        batch.set(quizRef, quiz);

        for (QuestionF question : questions) {
            question.setQuizID(Integer.parseInt(quizID));
            batch.set(db.collection("questions")
                    .document(Integer.toString(question.getId())), question);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Message msg = Message.obtain();
                Bundle bdl = new Bundle();
                bdl.putParcelable("quiz", quiz);
                msg.setData(bdl);
                AdminActivity.handler.sendMessage(msg);
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
                        bdl.putString("type", "getQuiz");
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
