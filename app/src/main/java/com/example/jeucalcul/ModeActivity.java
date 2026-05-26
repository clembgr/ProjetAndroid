package com.example.jeucalcul;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ModeActivity extends AppCompatActivity {

    private Button boutonClassique;
    private Button boutonCompetition;
    private ImageButton boutonRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mode);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boutonClassique = findViewById(R.id.bouton_mode_classique);
        boutonCompetition = findViewById(R.id.bouton_mode_competition);
        boutonRetour = findViewById(R.id.bouton_retour);

        boutonRetour.setOnClickListener(v -> finish());

        boutonClassique.setOnClickListener(v -> lancerJeu(false));
        boutonCompetition.setOnClickListener(v -> lancerJeu(true));
    }

    private void lancerJeu(boolean isCompetition) {
        Intent intent = new Intent(this, JeuActivity.class);
        intent.putExtra("MODE_COMPETITION", isCompetition);
        startActivity(intent);
        finish();
    }
}