package com.example.jeucalcul;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class JeuActivity extends AppCompatActivity {

    private TextView texteVies, texteScore, texteCalcul, texteErreur;
    private EditText inputReponse;
    private Button boutonValider;

    private int vies = 3;
    private int score = 0;
    private int bonneReponse = 0;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jeu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        texteVies = findViewById(R.id.texte_vies);
        texteScore = findViewById(R.id.texte_score);
        texteCalcul = findViewById(R.id.texte_calcul);
        texteErreur = findViewById(R.id.texte_erreur);
        inputReponse = findViewById(R.id.input_reponse);
        boutonValider = findViewById(R.id.bouton_valider);

        inputReponse.setShowSoftInputOnFocus(false);

        texteVies.setText(getString(R.string.texte_vies) + vies);
        texteErreur.setText("");

        configurerClavier();
        genererCalcul();

        boutonValider.setOnClickListener(v -> verifierReponse());
    }

    private void configurerClavier() {
        View.OnClickListener listenerChiffres = v -> {
            Button b = (Button) v;
            inputReponse.append(b.getText().toString());
            texteErreur.setText("");
        };

        int[] idsChiffres = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9};
        for (int id : idsChiffres) {
            findViewById(id).setOnClickListener(listenerChiffres);
        }

        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            inputReponse.setText("");
            texteErreur.setText("");
        });
    }

    private void genererCalcul() {
        int typeOperation = random.nextInt(4);
        int num1, num2;

        switch (typeOperation) {
            case 0:
                num1 = random.nextInt(50) + 1;
                num2 = random.nextInt(50) + 1;
                bonneReponse = num1 + num2;
                texteCalcul.setText(num1 + " + " + num2 + " = ?");
                break;
            case 1:
                num1 = random.nextInt(50) + 1;
                num2 = random.nextInt(50) + 1;
                bonneReponse = num1 - num2;
                texteCalcul.setText(num1 + " - " + num2 + " = ?");
                break;
            case 2:
                num1 = random.nextInt(10) + 1;
                num2 = random.nextInt(10) + 1;
                bonneReponse = num1 * num2;
                texteCalcul.setText(num1 + " × " + num2 + " = ?");
                break;
            case 3:
                num2 = random.nextInt(10) + 1;
                bonneReponse = random.nextInt(10) + 1;
                num1 = num2 * bonneReponse;
                texteCalcul.setText(num1 + " ÷ " + num2 + " = ?");
                break;
        }
        inputReponse.setText("");
        texteErreur.setText("");
    }

    private void verifierReponse() {
        String saisie = inputReponse.getText().toString();

        if (saisie.isEmpty()) {
            texteErreur.setText(getString(R.string.erreur_vide));
            return;
        }

        int reponseJoueur = Integer.parseInt(saisie);

        if (reponseJoueur == bonneReponse) {
            score++;
            texteScore.setText(getString(R.string.texte_score) + score);
            genererCalcul();
        } else {
            vies--;
            texteVies.setText(getString(R.string.texte_vies) + vies);

            if (vies <= 0) {
                boutonValider.setEnabled(false);
                afficherPopupFinJeu();
            } else {
                texteErreur.setText(getString(R.string.erreur_faux));
                inputReponse.setText("");
            }
        }
    }

    private void afficherPopupFinJeu() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.game_over_titre));
        builder.setMessage(getString(R.string.game_over_msg1) + score + getString(R.string.game_over_msg2));

        final EditText inputNom = new EditText(this);
        inputNom.setHint(getString(R.string.hint_pseudo));
        builder.setView(inputNom);

        builder.setPositiveButton(getString(R.string.bouton_enregistrer), (dialog, which) -> {
            String nom = inputNom.getText().toString().trim();
            if (nom.isEmpty()) {
                nom = "Anonyme";
            }

            BDHelper db = new BDHelper(JeuActivity.this);
            db.ajouterScore(nom, score);

            finish();
        });

        builder.setCancelable(false);
        builder.show();
    }
}