package com.sacha.quiz.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

public class Question implements Parcelable {
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    private String id;
    private int quizID;
    private String text;
    private String answers;
    private String correctAnswer;

    public Question() {
    }

    public Question(int quizID, String text, String answers, String correctAnswer) {
        this.quizID = quizID;
        this.text = text;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    private Question(Parcel in) {
        id = in.readString();
        quizID = in.readInt();
        text = in.readString();
        answers = in.readString();
        correctAnswer = in.readString();
    }

    public static String convertAnswerList(List<String> answers) {
        StringBuilder result = new StringBuilder();

        for (String answer : answers) {
            if (!answer.equals("")) {
                result.append(answer).append(";");
            }
        }

        return result.toString();
    }

    public List<String> getAnswerList() {
        return Arrays.asList(answers.split(";"));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuizID() {
        return quizID;
    }

    public void setQuizID(int quizID) {
        this.quizID = quizID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(quizID);
        parcel.writeString(text);
        parcel.writeString(answers);
        parcel.writeString(correctAnswer);
    }
}
