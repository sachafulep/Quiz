package com.sacha.quiz.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sacha.quiz.Classes.Player;
import com.sacha.quiz.Classes.Score;

import java.util.List;

@Dao
public interface PlayerDao {
    @Query("SELECT * FROM players")
    List<Player> getAll();

    @Insert
    void addScore(Score score);

    @Query("SELECT * FROM players WHERE firstName like :firstName AND lastName like :lastName")
    Player get(String firstName, String lastName);

    @Query("SELECT id FROM players ORDER BY id DESC LIMIT 1")
    int getHighestID();

    @Insert
    void add(Player player);

    @Query("SELECT * FROM players WHERE id like :playerID")
    Player get(int playerID);

    @Query("DELETE FROM players")
    void clear();
}
