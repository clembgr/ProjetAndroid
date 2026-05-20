package com.example.jeucalcul;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HighscoreActivity extends AppCompatActivity {

    private TextView texteScoresListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_highscore);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        texteScoresListe = findViewById(R.id.texte_scores_liste);

        afficherScores();
    }

    private void afficherScores() {
        BDHelper db = new BDHelper(this);
        android.database.Cursor cursor = db.obtenirMeilleursScores();

        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder builder = new StringBuilder();
            int rang = 1;

            while (cursor.moveToNext()) {
                String nom = cursor.getString(0);
                int score = cursor.getInt(1);

                builder.append(rang).append(".  ").append(nom).append("  -  ").append(score).append(" pts\n");
                rang++;
            }
            cursor.close();
            texteScoresListe.setText(builder.toString());
        } else {
            // Le changement est ici
            texteScoresListe.setText(getString(R.string.aucun_score));
        }
    }
}