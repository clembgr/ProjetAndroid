package com.example.jeucalcul;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;
import java.util.Random;

public class JeuActivity extends AppCompatActivity {

    private TextView texteVies, texteScore, texteCalcul, texteErreur;
    private EditText inputReponse;
    private Button boutonValider;
    private android.widget.ImageButton boutonRetour;

    private int vies = 3;
    private int score = 0;
    private int bonneReponse = 0;
    private Random random = new Random();

    private boolean isCompetition;
    private CountDownTimer timer;
    private AlertDialog dialogQuitter;

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

        isCompetition = getIntent().getBooleanExtra("MODE_COMPETITION", false);

        texteVies = findViewById(R.id.texte_vies);
        texteScore = findViewById(R.id.texte_score);
        texteCalcul = findViewById(R.id.texte_calcul);
        texteErreur = findViewById(R.id.texte_erreur);
        inputReponse = findViewById(R.id.input_reponse);
        boutonValider = findViewById(R.id.bouton_valider);
        boutonRetour = findViewById(R.id.bouton_retour);

        inputReponse.setShowSoftInputOnFocus(false);
        texteErreur.setText("");

        if (isCompetition) {
            texteVies.setTextColor(Color.parseColor("#FF9800"));
        } else {
            texteVies.setText(getString(R.string.texte_vies) + vies);
        }

        configurerClavier();
        genererCalcul();

        boutonValider.setOnClickListener(v -> verifierReponse());
        boutonRetour.setOnClickListener(v -> afficherPopupQuitter());

        inputReponse.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (boutonValider.isEnabled()) {
                    verifierReponse();
                }
                return true;
            }
            return false;
        });
    }

    private void configurerClavier() {
        View.OnClickListener listenerChiffres = v -> {
            Button b = (Button) v;
            inputReponse.append(b.getText().toString());
        };

        int[] idsChiffres = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9};
        for (int id : idsChiffres) {
            findViewById(id).setOnClickListener(listenerChiffres);
        }

        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            inputReponse.setText("");
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
                int temp1 = random.nextInt(50) + 1;
                int temp2 = random.nextInt(50) + 1;
                num1 = Math.max(temp1, temp2);
                num2 = Math.min(temp1, temp2);
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

        if (isCompetition) {
            demarrerTimer();
        }
    }

    private void demarrerTimer() {
        if (timer != null) {
            timer.cancel();
        }

        int secondesAllouees = Math.max(3, 10 - (score / 10));
        long tempsAlloueMillis = secondesAllouees * 1000L;

        timer = new CountDownTimer(tempsAlloueMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                double sec = millisUntilFinished / 1000.0;
                texteVies.setText(String.format(Locale.getDefault(), getString(R.string.format_temps), sec, secondesAllouees));
            }

            @Override
            public void onFinish() {
                texteVies.setText(String.format(Locale.getDefault(), getString(R.string.format_temps), 0.0, secondesAllouees));
                texteErreur.setTextColor(Color.RED);
                texteErreur.setText(getString(R.string.temps_ecoule));
                boutonValider.setEnabled(false);

                if (dialogQuitter != null && dialogQuitter.isShowing()) {
                    dialogQuitter.dismiss();
                }

                afficherPopupFinJeu();
            }
        }.start();
    }

    private void arreterTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void verifierReponse() {
        String saisie = inputReponse.getText().toString();

        if (saisie.isEmpty()) {
            texteErreur.setTextColor(Color.RED);
            texteErreur.setText(getString(R.string.erreur_vide));
            return;
        }

        int reponseJoueur;
        try {
            reponseJoueur = Integer.parseInt(saisie);
        } catch (NumberFormatException e) {
            texteErreur.setTextColor(Color.RED);
            texteErreur.setText(getString(R.string.erreur_limite_nombre));
            inputReponse.setText("");
            return;
        }

        if (reponseJoueur == bonneReponse) {
            score++;
            texteScore.setText(getString(R.string.texte_score) + score);
            texteErreur.setTextColor(Color.parseColor("#4CAF50"));
            texteErreur.setText(getString(R.string.succes_reponse));
            genererCalcul();
        } else {
            texteErreur.setTextColor(Color.RED);
            if (isCompetition) {
                arreterTimer();
                boutonValider.setEnabled(false);
                texteErreur.setText(getString(R.string.erreur_faux));
                afficherPopupFinJeu();
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
    }

    private void afficherPopupQuitter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.quitter_titre));
        builder.setMessage(getString(R.string.quitter_msg));

        builder.setPositiveButton(getString(R.string.quitter_confirmer), (dialog, which) -> {
            arreterTimer();
            finish();
        });

        builder.setNegativeButton(getString(R.string.quitter_annuler), (dialog, which) -> {
            dialog.dismiss();
        });

        builder.setCancelable(false);
        dialogQuitter = builder.show();
    }

    private void afficherPopupFinJeu() {
        arreterTimer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.game_over_titre));
        builder.setMessage(getString(R.string.game_over_msg1) + " " + score + getString(R.string.game_over_msg2));

        final EditText inputNom = new EditText(this);
        inputNom.setHint(getString(R.string.hint_pseudo));
        builder.setView(inputNom);

        builder.setPositiveButton(getString(R.string.bouton_enregistrer), (dialog, which) -> {
            String nom = inputNom.getText().toString().trim();
            if (nom.isEmpty()) {
                nom = "Anonyme";
            }

            BDHelper db = new BDHelper(JeuActivity.this);
            db.ajouterScore(nom, score, isCompetition);

            finish();
        });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arreterTimer();
    }
}