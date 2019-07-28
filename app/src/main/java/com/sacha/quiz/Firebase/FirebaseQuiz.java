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
import com.sacha.quiz.Classes.Question;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.MainActivity;

import java.util.ArrayList;

public class FirebaseQuiz {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String quizID;

    public void insert(final Quiz quiz, final ArrayList<Question> questions) {
        db.collection("quizzes")
                .orderBy("id", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String quizID;

                        if (queryDocumentSnapshots.size() < 1) {
                            quizID = "0";
                        } else {
                            String temp = queryDocumentSnapshots.getDocuments().get(0).get("id").toString();
                            quizID = Integer.toString(Integer.parseInt(temp) + 1);
                        }

                        insertQuizWithID(quizID, quiz, questions);
                    }
                });
    }

    private void insertQuizWithID(String quizID, final Quiz quiz, ArrayList<Question> questions) {
        WriteBatch batch = db.batch();

        DocumentReference quizRef = db.collection("quizzes").document(quizID);
        quiz.setId(Integer.parseInt(quizID));
        batch.set(quizRef, quiz);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            question.setQuizID(Integer.parseInt(quizID));
            String idString = quizID + "-" + i;
            question.setId(idString);
            batch.set(db.collection("questions")
                    .document(question.getId()), question);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Message msg = Message.obtain();
                Bundle bdl = new Bundle();
                bdl.putString("type", "addQuiz");
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
                        bdl.putParcelable("quiz", documentSnapshot.toObject(Quiz.class));
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
                            bdl.putString("type", "getQuizzes");
                            ArrayList<Quiz> quizzes = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(MainActivity.TAG, document.getId() + " => " + document.getData());
                                quizzes.add(document.toObject(Quiz.class));
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

    public void delete(final int quizID) {
        final WriteBatch batch = db.batch();
        String id = Integer.toString(quizID);

        batch.delete(db.collection("quizzes").document(id));

        db.collection("questions").whereEqualTo("quizID", quizID)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    batch.delete(snapshot.getReference());
                }

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Message msg = Message.obtain();
                        Bundle bdl = new Bundle();
                        bdl.putString("type", "deleteQuiz");
                        bdl.putInt("quizID", quizID);
                        msg.setData(bdl);
                        AdminActivity.handler.sendMessage(msg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void clear() {

    }

    public void set(Quiz quiz, ArrayList<Question> questions) {
        insertQuizWithID(Integer.toString(quiz.getId()), quiz, questions);
    }
}
