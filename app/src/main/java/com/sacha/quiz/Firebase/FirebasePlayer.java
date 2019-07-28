package com.sacha.quiz.Firebase;

import android.os.Bundle;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sacha.quiz.AdminQuizActivity;
import com.sacha.quiz.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class FirebasePlayer {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String playerID;

    public void insert(String firstName, String lastName) {
        Map<String, String> player = new HashMap<>();
        player.put("firstName", firstName);
        player.put("lastName", lastName);

        db.collection("players")
                .document(firstName + " " + lastName)
                .set(player)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Message msg = Message.obtain();
                        Bundle bdl = new Bundle();
                        bdl.putString("type", "insertPlayer");
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

    public void checkIfExists(String fullName) {
        db.collection("players").document(fullName).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Message msg = Message.obtain();
                        Bundle bdl = new Bundle();

                        if (documentSnapshot.exists()) {
                            bdl.putString("type", "playerExists");
                        } else {
                            bdl.putString("type", "playerDoesNotExist");
                        }

                        msg.setData(bdl);
                        LoginActivity.handler.sendMessage(msg);
                    }
                });
    }
}

