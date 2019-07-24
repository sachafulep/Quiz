package com.sacha.quiz.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sacha.quiz.Classes.Score;

import java.util.List;

@Dao
public interface ScoreDao {
    @Insert
    void add(Score score);

    @Query("SELECT id FROM scores ORDER BY id DESC LIMIT 1")
    int getHighestID();

    @Query("SELECT * FROM scores WHERE quizID LIKE :quizID AND playerID LIKE :playerID")
    Score get(int quizID, int playerID);

    @Query("SELECT * FROM scores")
    List<Score> getAll();

    @Query("SELECT SUM(score) FROM scores WHERE playerID like :playerID")
    int getTotalScore(int playerID);

    @Query("DELETE FROM scores")
    void clear();
}
