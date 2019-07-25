package com.sacha.quiz.FirebaseClasses;

public class Score {
    private int id;
    private int quizID;
    private int playerID;
    private int score;

    public Score(int id, int quizID, int playerID, int score) {
        this.id = id;
        this.quizID = quizID;
        this.playerID = playerID;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizID() {
        return quizID;
    }

    public void setQuizID(int quizID) {
        this.quizID = quizID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
