package com.sacha.quiz.Classes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "scores",
        foreignKeys = {@ForeignKey(entity = Quiz.class,
                parentColumns = "id",
                childColumns = "quizID",
                onDelete = CASCADE), @ForeignKey(entity = Player.class,
                parentColumns = "id",
                childColumns = "playerID",
                onDelete = CASCADE)}
)
public class Score {
    @PrimaryKey
    private int id;
    @ColumnInfo(index = true)
    private int quizID;
    @ColumnInfo(index = true)
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
