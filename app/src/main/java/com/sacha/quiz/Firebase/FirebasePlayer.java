package com.sacha.quiz.Firebase;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sacha.quiz.AdminActivity;
import com.sacha.quiz.AdminQuizActivity;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.LoginActivity;
import com.sacha.quiz.MainActivity;

import java.util.ArrayList;
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

    public void getAll() {
        db.collection("players")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Message msg = Message.obtain();
                            Bundle bdl = new Bundle();
                            bdl.putString("type", "getPlayers");
                            ArrayList<String> players = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(MainActivity.TAG, document.getId() + " => " + document.getData());
                                players.add(document.getId());
                            }

                            bdl.putStringArrayList("players", players);
                            msg.setData(bdl);
                            AdminActivity.handler.sendMessage(msg);
                        } else {
                            Log.d(MainActivity.TAG, "Error getting quizzes: ", task.getException());
                        }
                    }
                });
    }

    public void getHighScores() {
        db.collection("players")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Message msg = Message.obtain();
                            Bundle bdl = new Bundle();
                            bdl.putString("type", "getHighScores");
                            ArrayList<String> names = new ArrayList<>();
                            ArrayList<Integer> scores = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(MainActivity.TAG, document.getId() + " => " + document.getData());
                                names.add(document.getId());
                                scores.add((int) (long) document.get("score"));
                            }

                            bdl.putStringArrayList("names", names);
                            bdl.putIntegerArrayList("scores", scores);
                            msg.setData(bdl);
                            LoginActivity.handler.sendMessage(msg);
                        } else {
                            Log.d(MainActivity.TAG, "Error getting quizzes: ", task.getException());
                        }
                    }
                });
    }
}

