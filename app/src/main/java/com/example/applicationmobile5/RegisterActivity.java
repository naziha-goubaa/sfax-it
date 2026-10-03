package com.example.applicationmobile5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends BaseActivity {

    private DatabaseHelper db;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialisation DB
        db = new DatabaseHelper(this);

        // Récupération des champs
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);
        EditText edtConfirm = findViewById(R.id.edtPasswordConfirm);
        Button btnCreate = findViewById(R.id.btnCreate);
        TextView txtLogin = findViewById(R.id.txtLogin);

        TextInputLayout emailLayout = findViewById(R.id.emailLayout);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
        TextInputLayout confirmLayout = findViewById(R.id.confirmLayout);

        // Validation en temps réel
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !DatabaseHelper.validatePasswordStrength(s.toString())) {
                    passwordLayout.setError("8 caractères, majuscule, minuscule, chiffre, spécial");
                } else {
                    passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Click listener pour inscription
        btnCreate.setOnClickListener(v -> {
            Log.d(TAG, "Bouton Créer cliqué");

            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString();
            String confirm = edtConfirm.getText().toString();

            // Validation
            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.setError("Email invalide");
                return;
            }

            if (!pass.equals(confirm)) {
                confirmLayout.setError("Les mots de passe ne correspondent pas");
                return;
            }

            if (!DatabaseHelper.validatePasswordStrength(pass)) {
                passwordLayout.setError("Mot de passe trop faible");
                return;
            }

            if (db.emailExists(email)) {
                emailLayout.setError("Cet email est déjà utilisé");
                return;
            }

            // Tenter l'insertion (toujours en tant qu'utilisateur)
            boolean ok = db.insertUser(email, pass);
            if (ok) {
                Toast.makeText(this, "Compte créé avec succès!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Utilisateur créé : " + email + " (user)");

                // Se connecter automatiquement
                getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                        .putBoolean("logged", true)
                        .putString("role", "user")
                        .putString("email", email)
                        .apply();

                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erreur insertion user : " + email);
            }
        });

        // Aller à Login
        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}