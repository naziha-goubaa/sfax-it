package com.example.applicationmobile5;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateBaseContextLocale(newBase));
    }

    private Context updateBaseContextLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE);
        String lang = prefs.getString("lang", "fr");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        android.content.res.Configuration config = new android.content.res.Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Appliquer le thème avant super.onCreate()
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean dark = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Forcer la langue pour cette activité
        applyLanguage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vérifier les changements de préférences
        applyLanguage();
    }

    private void applyLanguage() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String lang = prefs.getString("lang", "fr");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        android.content.res.Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}