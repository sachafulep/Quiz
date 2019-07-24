package com.sacha.quiz.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sacha.quiz.Classes.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    @Query("SELECT * FROM questions WHERE quizId LIKE :id")
    List<Question> getQuestions(int id);

    @Query("SELECT id FROM questions ORDER BY id DESC LIMIT 1")
    int getHighestID();

    @Insert
    void addQuestions(List<Question> questions);

    @Query("DELETE FROM questions WHERE quizId LIKE :id")
    void deleteQuestions(int id);

    @Query("DELETE FROM questions")
    void clear();
}
