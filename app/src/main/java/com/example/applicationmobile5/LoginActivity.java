package com.example.applicationmobile5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SharedPreferences prefs;
    private LinearLayout settingsContainer;
    private ImageView btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Vérifier si déjà connecté
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (prefs.getBoolean("logged", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Initialisation DB
        try {
            db = new DatabaseHelper(this);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur base de données", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        // Récupération des vues
        settingsContainer = findViewById(R.id.settingsContainer);
        btnSettings = findViewById(R.id.btnSettings);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView txtRegister = findViewById(R.id.txtRegister);

        // Ouvrir/fermer mini-settings
        btnSettings.setOnClickListener(v -> {
            if (settingsContainer.getVisibility() == View.GONE) {
                settingsContainer.setVisibility(View.VISIBLE);
            } else {
                settingsContainer.setVisibility(View.GONE);
            }
        });

        // Login
        btnLogin.setOnClickListener(v -> loginUser());

        // Register
        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        // Remplir avec un utilisateur test
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);
        edtEmail.setText("user@user.com");
        edtPassword.setText("User123!");
    }

    private void loginUser() {
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);

        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String pass = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db == null) {
            Toast.makeText(this, "Base de données non disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = db.checkUserAndGetRole(email, pass);
        if (role != null) {
            prefs.edit()
                    .putBoolean("logged", true)
                    .putString("role", role)
                    .putString("email", email)
                    .apply();

            Toast.makeText(this, "Connexion réussie - " + role, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}