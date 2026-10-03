package com.example.applicationmobile5;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {

    private String phone, url, email, location, logo, userRole;
    private LinearLayout weatherLayout, cvLayout;
    private TextView tvWeather;
    private Button btnUploadCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // ---------- UI ----------
        ImageView img = findViewById(R.id.companyImage);
        TextView txtName = findViewById(R.id.companyName);
        ListView listServices = findViewById(R.id.listServices);
        weatherLayout = findViewById(R.id.weatherLayout);
        tvWeather = findViewById(R.id.tvWeather);
        cvLayout = new LinearLayout(this);
        cvLayout.setOrientation(LinearLayout.VERTICAL);
        cvLayout.setPadding(16,16,16,16);

        weatherLayout.setVisibility(View.GONE);

        // ---------- INTENT DATA ----------
        Intent i = getIntent();
        if (i != null) {
            txtName.setText(i.getStringExtra("name"));
            phone = i.getStringExtra("phone");
            url = i.getStringExtra("url");
            email = i.getStringExtra("email");
            location = i.getStringExtra("location");
            logo = i.getStringExtra("logoUri");
            userRole = i.getStringExtra("role");
        }

        // ---------- SERVICES ----------
        ArrayList<String> servicesList = i.getStringArrayListExtra("services");
        if (servicesList != null && !servicesList.isEmpty()) {
            listServices.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    servicesList
            ));
        }

        // ---------- LOGO ----------
        try {
            if (logo != null && !logo.isEmpty()) {
                if (logo.startsWith("content://") || logo.startsWith("file://")) {
                    img.setImageURI(Uri.parse(logo));
                } else {
                    img.setImageResource(Integer.parseInt(logo));
                }
            } else img.setImageResource(R.mipmap.ic_launcher);
        } catch (Exception e) {
            img.setImageResource(R.mipmap.ic_launcher);
        }

        // ---------- ACTIONS ----------
        findViewById(R.id.btnWebsite).setOnClickListener(v -> {
            if (url != null && !url.isEmpty()) startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        findViewById(R.id.btnCall).setOnClickListener(v -> {
            if (phone != null && !phone.isEmpty()) startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
        });

        findViewById(R.id.btnLocation).setOnClickListener(v -> {
            if (location != null && !location.isEmpty()) {
                Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        findViewById(R.id.btnCalendar).setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            Intent cal = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, "Rendez-vous : " + txtName.getText())
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c.getTimeInMillis());
            startActivity(cal);
        });

        findViewById(R.id.btnMail).setOnClickListener(v -> showEmailDialog());

        // ---------- WEATHER ----------
        loadWeather();

        // ---------- CV UPLOAD (SEULEMENT USER) ----------
        if ("user".equalsIgnoreCase(userRole)) {
            btnUploadCV = new Button(this);
            btnUploadCV.setText("Déposer votre CV (PDF)");
            btnUploadCV.setOnClickListener(v -> uploadCV());

            cvLayout.addView(btnUploadCV);
            ((LinearLayout) findViewById(R.id.containerLayout)).addView(cvLayout);
        }
    }

    // ================= EMAIL =================
    private void showEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Envoyer un email");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText subject = new EditText(this);
        subject.setHint("Objet");

        EditText message = new EditText(this);
        message.setHint("Message");

        layout.addView(subject);
        layout.addView(message);
        builder.setView(layout);

        builder.setPositiveButton("Envoyer", (d, w) -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
            emailIntent.putExtra(Intent.EXTRA_TEXT, message.getText().toString());

            startActivity(Intent.createChooser(emailIntent, "Choisir une application"));
            showRatingDialog();
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    // ================= RATING =================
    private void showRatingDialog() {
        AlertDialog.Builder rate = new AlertDialog.Builder(this);
        rate.setTitle("Évaluer l’entreprise");

        RatingBar rb = new RatingBar(this);
        rb.setNumStars(5);
        rb.setStepSize(1f);

        rate.setView(rb);
        rate.setPositiveButton("OK", (d, w) ->
                Toast.makeText(this,
                        "Note : " + rb.getRating() + " ★",
                        Toast.LENGTH_SHORT).show()
        );

        rate.show();
    }

    // ================= WEATHER =================
    private void loadWeather() {
        if (location == null || location.trim().isEmpty()) return;

        if (!isNetworkAvailable()) {
            weatherLayout.setVisibility(View.VISIBLE);
            tvWeather.setText("Pas de connexion Internet");
            return;
        }

        weatherLayout.setVisibility(View.VISIBLE);
        tvWeather.setText("Chargement météo...");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        String apiKey = "YOUR_OPENWEATHER_API_KEY"; // ⚠️ Remplacer par ta clé API

        // Encodage correct de la ville
        String cityEncoded = Uri.encode(location);

        service.getWeather(cityEncoded, "metric", apiKey)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().main != null
                                && response.body().weather != null
                                && !response.body().weather.isEmpty()) {

                            WeatherResponse w = response.body();
                            String txt = String.format(
                                    Locale.getDefault(),
                                    "%s — %.1f°C — %s",
                                    w.name,
                                    w.main.temp,
                                    w.weather.get(0).description
                            );
                            tvWeather.setText(txt);

                        } else {
                            tvWeather.setText("Météo indisponible");
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        tvWeather.setText("Erreur météo : " + t.getMessage());
                        t.printStackTrace();
                    }

                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            android.net.Network network = cm.getActiveNetwork();
            if (network == null) return false;
            android.net.NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
    }


    // ================= CV UPLOAD =================
    private void uploadCV() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Sélectionner un CV PDF"), 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri pdfUri = data.getData();
            Toast.makeText(this, "CV sélectionné : " + pdfUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
            // Ici tu peux ajouter le code pour envoyer le PDF sur un serveur ou le stocker localement
        }
    }
}
