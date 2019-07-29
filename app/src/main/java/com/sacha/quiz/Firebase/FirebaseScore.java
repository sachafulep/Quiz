package com.sacha.quiz.Firebase;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;
import com.sacha.quiz.Classes.Score;
import com.sacha.quiz.LoginActivity;
import com.sacha.quiz.MainActivity;

public class FirebaseScore {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void insert(final Score score) {
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = db.collection("players")
                        .document(score.getPlayerName());

                DocumentSnapshot snapshot = transaction.get(docRef);

                long newScore = (long) snapshot.get("score") + score.getScore();
                transaction.update(docRef, "score", newScore);
                transaction.set(db.collection("scores")
                        .document(score.getQuizID() + " " + score.getPlayerName()), score);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(MainActivity.TAG, "Tnsert score success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(MainActivity.TAG, "Tnsert score failure.", e);
            }
        });
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
