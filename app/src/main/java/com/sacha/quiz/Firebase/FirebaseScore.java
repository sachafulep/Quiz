package com.sacha.quiz.Firebase;

import android.os.Bundle;
import android.os.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sacha.quiz.Classes.Score;
import com.sacha.quiz.LoginActivity;

public class FirebaseScore {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void insert(Score score) {
        db.collection("scores")
                .document(score.getQuizID() + " " + score.getPlayerName())
                .set(score);
    }

    public void checkIfPlayerHasTakenQuiz(int id, String fullName) {
        db.collection("scores")
                .document(id + " " + fullName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Message msg = Message.obtain();
                        Bundle bdl = new Bundle();

                        if (documentSnapshot.exists()) {
                            bdl.putString("type", "hasTakenQuiz");
                        } else {
                            bdl.putString("type", "hasNotTakenQuiz");
                        }

                        msg.setData(bdl);
                        LoginActivity.handler.sendMessage(msg);
                    }
                });
    }
}
