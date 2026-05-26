package com.example.jeucalcul;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HighscoreActivity extends AppCompatActivity {

    private TextView texteScoresListe;
    private android.widget.ImageButton boutonRetour;
    private Button ongletClassique, ongletCompetition;
    private boolean afficherCompetition = false;

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
        boutonRetour = findViewById(R.id.bouton_retour);
        ongletClassique = findViewById(R.id.onglet_classique);
        ongletCompetition = findViewById(R.id.onglet_competition);

        boutonRetour.setOnClickListener(v -> finish());

        ongletClassique.setOnClickListener(v -> {
            afficherCompetition = false;
            mettreAJourOnglets();
            afficherScores();
        });

        ongletCompetition.setOnClickListener(v -> {
            afficherCompetition = true;
            mettreAJourOnglets();
            afficherScores();
        });

        afficherScores();
    }

    private void mettreAJourOnglets() {
        if (afficherCompetition) {
            ongletCompetition.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"));
            ongletClassique.setBackgroundColor(android.graphics.Color.parseColor("#B0BEC5"));
        } else {
            ongletClassique.setBackgroundColor(android.graphics.Color.parseColor("#102A43"));
            ongletCompetition.setBackgroundColor(android.graphics.Color.parseColor("#B0BEC5"));
        }
    }

    private void afficherScores() {
        BDHelper db = new BDHelper(this);
        Cursor cursor = db.obtenirMeilleursScores(afficherCompetition);

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
            texteScoresListe.setText(getString(R.string.aucun_score));
        }
    }
}