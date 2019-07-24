package com.sacha.quiz.Database;

import android.content.Context;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.sacha.quiz.Classes.Player;
import com.sacha.quiz.Classes.Question;
import com.sacha.quiz.Classes.Quiz;
import com.sacha.quiz.Classes.Score;

@androidx.room.Database(entities = {Quiz.class, Question.class, Player.class, Score.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static Database instance = null;

    Database() {

    }

    public static Database getMainInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context, Database.class, "mealsDatabase")
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    public static Database getInstance() {
        return instance;
    }

    public abstract QuizDao quizDao();

    public abstract PlayerDao playerDao();

    public abstract QuestionDao questionDao();

    public abstract ScoreDao scoreDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
