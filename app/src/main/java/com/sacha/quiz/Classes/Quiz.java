package com.sacha.quiz.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Quiz implements Parcelable {
    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };
    private int id;
    private String title;

    public Quiz() {
    }

    public Quiz(int id, String title) {
        this.id = id;
        this.title = title;
    }

    protected Quiz(Parcel in) {
        id = in.readInt();
        title = in.readString();
    }

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
