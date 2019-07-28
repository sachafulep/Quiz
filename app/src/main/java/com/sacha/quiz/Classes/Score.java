package com.sacha.quiz.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Score implements Parcelable {
    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };
    private int quizID;
    private String playerName;
    private int score;

    public Score(int quizID, String playerName, int score) {
        this.quizID = quizID;
        this.playerName = playerName;
        this.score = score;
    }

    private Score(Parcel in) {
        quizID = in.readInt();
        playerName = in.readString();
        score = in.readInt();
    }

    public int getQuizID() {
        return quizID;
    }

    public void setQuizID(int quizID) {
        this.quizID = quizID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(quizID);
        parcel.writeString(playerName);
        parcel.writeInt(score);
    }
}
