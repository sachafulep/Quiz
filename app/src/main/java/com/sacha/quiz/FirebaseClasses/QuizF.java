package com.sacha.quiz.FirebaseClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class QuizF implements Parcelable {
    private int id;
    private String title;

    public QuizF() {
    }

    public QuizF(int id, String title) {
        this.id = id;
        this.title = title;
    }

    protected QuizF(Parcel in) {
        id = in.readInt();
        title = in.readString();
    }

    public static final Creator<QuizF> CREATOR = new Creator<QuizF>() {
        @Override
        public QuizF createFromParcel(Parcel in) {
            return new QuizF(in);
        }

        @Override
        public QuizF[] newArray(int size) {
            return new QuizF[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
    }
}
