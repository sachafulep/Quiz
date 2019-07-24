package com.sacha.quiz.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sacha.quiz.Classes.Quiz;

import java.util.List;

@Dao
public interface QuizDao {
    @Query("SELECT * FROM quizzes")
    List<Quiz> getAll();

    @Insert
    void addQuiz(Quiz quiz);

    @Query("SELECT * FROM quizzes WHERE id LIKE :id")
    Quiz get(int id);

    @Update
    void updateQuiz(Quiz currentQuiz);

    @Delete
    void deleteQuiz(Quiz currentQuiz);

    @Query("DELETE FROM quizzes")
    void clear();
}
