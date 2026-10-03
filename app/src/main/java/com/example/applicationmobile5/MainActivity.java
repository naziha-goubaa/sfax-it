package com.example.applicationmobile5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private DatabaseHelper db;
    private LinearLayout container;
    private String userEmail, userRole;
    private EditText searchEditText;
    private LinearLayout generalSettingsContainer;
    private DrawerLayout drawerLayout;
    private TextView txtUserName, txtUserEmail, txtUserRole;
    private Button btnLogoutGeneral;
    private SharedPreferences prefs;
    private FloatingActionButton btnAdd;

    @Override
    protected void onResume() {
        super.onResume();
        displayCompanies();
    }

    private void restartToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INIT PREFS
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // Vérifier connexion
        if (!prefs.getBoolean("logged", false)) {
            restartToLogin();
            return;
        }

        userEmail = prefs.getString("email", "");
        userRole = prefs.getString("role", "user");

        // INIT USER INFO
        db = new DatabaseHelper(this);
        container = findViewById(R.id.containerLayout);
        generalSettingsContainer = findViewById(R.id.generalSettingsContainer);
        searchEditText = findViewById(R.id.searchEditText);
        drawerLayout = findViewById(R.id.drawerLayout);
        txtUserName = findViewById(R.id.txtName);
        txtUserEmail = findViewById(R.id.txtEmail);
        txtUserRole = findViewById(R.id.txtRole);
        btnLogoutGeneral = findViewById(R.id.btnLogoutGeneral);
        btnAdd = findViewById(R.id.btnAdd);

        // Récupérer les TextViews dans le header du drawer (s'ils existent)
        NavigationView navigationView = findViewById(R.id.userSettingsDrawer);
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                txtUserName = headerView.findViewById(R.id.txtName);
                txtUserEmail = headerView.findViewById(R.id.txtEmail);
                txtUserRole = headerView.findViewById(R.id.txtRole);
            }
        }

        // Affichage infos utilisateur
        if (userEmail != null && !userEmail.isEmpty()) {
            txtUserEmail.setText(userEmail);
            txtUserName.setText(userEmail.split("@")[0].toUpperCase(Locale.ROOT));
            txtUserRole.setText(userRole.toUpperCase(Locale.ROOT));
        } else {
            txtUserEmail.setText("Invité");
            txtUserName.setText("INVITE");
            txtUserRole.setText("-");
        }

        // Déconnexion
        if (btnLogoutGeneral != null) {
            btnLogoutGeneral.setOnClickListener(v -> {
                prefs.edit().clear().apply();
                restartToLogin();
            });
        }

        // Mini-settings général
        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            if (generalSettingsContainer != null) {
                generalSettingsContainer.setVisibility(
                        generalSettingsContainer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
                );
            }
        });

        // Menu drawer
        findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // FloatingButton ajout pour admin
        if (btnAdd != null) {
            if (!"admin".equalsIgnoreCase(userRole)) {
                btnAdd.setVisibility(View.GONE);
            } else {
                btnAdd.setOnClickListener(v ->
                        startActivity(new Intent(this, AddCompanyActivity.class))
                );
            }
        }

        // Recherche dynamique
        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    displayCompanies(s.toString().trim());
                }
            });
        }

        displayCompanies();
    }

    private void displayCompanies() {
        displayCompanies(searchEditText != null ? searchEditText.getText().toString().trim() : "");
    }

    private void displayCompanies(String filter) {
        if (container == null) return;

        container.removeAllViews();
        ArrayList<Company> list = db.getAllCompanies();
        ArrayList<Company> filteredList = new ArrayList<>();

        for (Company c : list) {
            if (c.getName().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))) {
                filteredList.add(c);
            }
        }

        for (Company c : filteredList) {
            CardView card = new CardView(this);
            card.setRadius(16);
            card.setCardElevation(6);
            card.setUseCompatPadding(true);
            card.setCardBackgroundColor(getResources().getColor(android.R.color.background_light));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 20, 0, 0);
            card.setLayoutParams(params);

            LinearLayout verticalLayout = new LinearLayout(this);
            verticalLayout.setOrientation(LinearLayout.VERTICAL);
            verticalLayout.setPadding(16,16,16,16);
            verticalLayout.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

            ImageView img = new ImageView(this);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            img.setPadding(20,20,20,20);
            img.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 300
            ));

            if (c.getLogoUri() != null && !c.getLogoUri().isEmpty()) {
                try {
                    if (c.getLogoUri().startsWith("content://") || c.getLogoUri().startsWith("file://")) {
                        img.setImageURI(Uri.parse(c.getLogoUri()));
                    } else {
                        int resId = Integer.parseInt(c.getLogoUri());
                        img.setImageResource(resId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    img.setImageResource(R.mipmap.ic_launcher);
                }
            } else img.setImageResource(R.mipmap.ic_launcher);

            TextView txtName = new TextView(this);
            txtName.setText(c.getName());
            txtName.setTextSize(18);
            txtName.setTypeface(null, android.graphics.Typeface.BOLD);
            txtName.setGravity(android.view.Gravity.CENTER);
            txtName.setPadding(0, 10, 0, 10);
            txtName.setTextColor(getResources().getColor(android.R.color.black));

            TextView txtServices = new TextView(this);
            txtServices.setText(c.getServices());
            txtServices.setTextSize(14);
            txtServices.setGravity(android.view.Gravity.CENTER);
            txtServices.setPadding(0, 0, 0, 10);
            txtServices.setTextColor(getResources().getColor(android.R.color.darker_gray));

            verticalLayout.addView(img);
            verticalLayout.addView(txtName);
            verticalLayout.addView(txtServices);

            // Admin Buttons (Update/Delete)
            if ("admin".equalsIgnoreCase(userRole)) {
                LinearLayout btnLayout = new LinearLayout(this);
                btnLayout.setOrientation(LinearLayout.HORIZONTAL);
                btnLayout.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

                Button btnUpdate = new Button(this);
                btnUpdate.setText("Modifier");
                btnUpdate.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                ));
                btnUpdate.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, AddCompanyActivity.class);
                    intent.putExtra("id", c.getId());
                    intent.putExtra("name", c.getName());
                    intent.putExtra("services", c.getServices());
                    intent.putExtra("phone", c.getPhone());
                    intent.putExtra("url", c.getUrl());
                    intent.putExtra("email", c.getEmail());
                    intent.putExtra("location", c.getLocation());
                    intent.putExtra("logoUri", c.getLogoUri());
                    startActivity(intent);
                });

                Button btnDelete = new Button(this);
                btnDelete.setText("Supprimer");
                btnDelete.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                ));
                btnDelete.setOnClickListener(v -> {
                    db.deleteCompany(c.getId());
                    displayCompanies(filter);
                    Toast.makeText(MainActivity.this, "Entreprise supprimée", Toast.LENGTH_SHORT).show();
                });

                btnLayout.addView(btnUpdate);
                btnLayout.addView(btnDelete);
                verticalLayout.addView(btnLayout);
            }

            card.addView(verticalLayout);

            // Click pour détails
            card.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                i.putExtra("name", c.getName());
                i.putExtra("phone", c.getPhone());
                i.putExtra("url", c.getUrl());
                i.putExtra("email", c.getEmail());
                i.putExtra("location", c.getLocation());
                i.putExtra("logoUri", c.getLogoUri());
                i.putStringArrayListExtra("services",
                        new ArrayList<>(java.util.Arrays.asList(c.getServices().split(","))));
                i.putExtra("role", userRole);
                startActivity(i);
            });

            container.addView(card);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}