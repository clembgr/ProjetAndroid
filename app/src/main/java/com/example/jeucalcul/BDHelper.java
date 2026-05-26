package com.example.jeucalcul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "JeuCalcul.db";
    private static final int DATABASE_VERSION = 2;

    public BDHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE highscores (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, score INTEGER)");
        db.execSQL("CREATE TABLE highscores_comp (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, score INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS highscores");
        db.execSQL("DROP TABLE IF EXISTS highscores_comp");
        onCreate(db);
    }

    public void ajouterScore(String nom, int score, boolean isCompetition) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nom", nom);
        values.put("score", score);

        String table = isCompetition ? "highscores_comp" : "highscores";
        db.insert(table, null, values);
        db.close();
    }

    public Cursor obtenirMeilleursScores(boolean isCompetition) {
        SQLiteDatabase db = this.getReadableDatabase();
        String table = isCompetition ? "highscores_comp" : "highscores";
        return db.rawQuery("SELECT nom, score FROM " + table + " WHERE score > 0 ORDER BY score DESC LIMIT 10", null);
    }
}