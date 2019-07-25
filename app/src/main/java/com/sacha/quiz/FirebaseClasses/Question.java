package com.sacha.quiz.FirebaseClasses;

import java.util.Arrays;
import java.util.List;

public class Question {
    private int id;
    private int quizId;
    private String text;
    private String answers;
    private String correctAnswer;

    public Question(int quizId, String text, String answers, String correctAnswer) {
        this.quizId = quizId;
        this.text = text;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
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
}
