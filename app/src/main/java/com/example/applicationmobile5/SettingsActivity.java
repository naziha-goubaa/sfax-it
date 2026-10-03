package com.example.applicationmobile5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends BaseActivity {

    private boolean firstSelection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        Switch switchTheme = findViewById(R.id.switchTheme);
        Spinner spinnerLang = findViewById(R.id.spinnerLang);
        Button btnLogout = findViewById(R.id.btnLogout);

        // 🌙 THEME
        switchTheme.setChecked(prefs.getBoolean("dark_mode", false));
        switchTheme.setOnCheckedChangeListener((b, checked) -> {
            prefs.edit().putBoolean("dark_mode", checked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    checked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
            recreate();
        });

        // 🌍 LANGUE
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Français", "English", "العربية"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLang.setAdapter(adapter);

        String lang = prefs.getString("lang", "fr");
        spinnerLang.setSelection(lang.equals("en") ? 1 : lang.equals("ar") ? 2 : 0);

        spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int pos, long id) {

                // ✅ IGNORER la sélection automatique
                if (firstSelection) {
                    firstSelection = false;
                    return;
                }

                String code = pos == 1 ? "en" : pos == 2 ? "ar" : "fr";

                if (!code.equals(prefs.getString("lang", "fr"))) {
                    prefs.edit().putString("lang", code).apply();
                    restartApp();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🚪 LOGOUT
        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            restartApp();
        });
    }

    private void restartApp() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
