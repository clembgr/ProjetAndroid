package com.example.jeucalcul;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDHelper extends SQLiteOpenHelper {

    // Nom et version de la base de données
    private static final String DATABASE_NAME = "JeuCalcul.db";
    private static final int DATABASE_VERSION = 1;

    public BDHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Requête SQL pour créer la table
        String createTable = "CREATE TABLE highscores (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, score INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS highscores");
        onCreate(db);
    }

    // Méthode pour ajouter un score facilement depuis notre jeu
    public void ajouterScore(String nom, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nom", nom);
        values.put("score", score);

        db.insert("highscores", null, values);
        db.close();
    }
    // Méthode pour récupérer les 10 meilleurs scores
    public android.database.Cursor obtenirMeilleursScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Tri par score décroissant, limité aux 10 premiers
        return db.rawQuery("SELECT nom, score FROM highscores ORDER BY score DESC LIMIT 10", null);
    }
}