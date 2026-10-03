package com.example.applicationmobile5;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

public class AddCompanyActivity extends AppCompatActivity {

    EditText edtName, edtServices, edtPhone, edtURL, edtEmail, edtLocation;
    ImageView imgPreview;
    Button btnChooseLogo, btnSave;

    String logoUri = "";
    DatabaseHelper db;
    int companyId = -1; // -1 = nouvelle entreprise

    ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    logoUri = uri.toString();
                    imgPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);

        db = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtName);
        edtServices = findViewById(R.id.edtServices);
        edtPhone = findViewById(R.id.edtPhone);
        edtURL = findViewById(R.id.edtURL);
        edtEmail = findViewById(R.id.edtEmail);
        edtLocation = findViewById(R.id.edtLocation);
        imgPreview = findViewById(R.id.imgPreview);
        btnChooseLogo = findViewById(R.id.btnChooseLogo);
        btnSave = findViewById(R.id.btnSave);

        // Vérifier si c’est un update
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            companyId = intent.getIntExtra("id", -1);
            edtName.setText(intent.getStringExtra("name"));
            edtServices.setText(intent.getStringExtra("services"));
            edtPhone.setText(intent.getStringExtra("phone"));
            edtURL.setText(intent.getStringExtra("url"));
            edtEmail.setText(intent.getStringExtra("email"));
            edtLocation.setText(intent.getStringExtra("location"));
            logoUri = intent.getStringExtra("logoUri");

            if (logoUri != null && !logoUri.isEmpty()) {
                try {
                    if (logoUri.startsWith("content://") || logoUri.startsWith("file://")) {
                        imgPreview.setImageURI(Uri.parse(logoUri));
                    } else {
                        imgPreview.setImageResource(Integer.parseInt(logoUri));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    imgPreview.setImageResource(R.mipmap.ic_launcher);
                }
            }
        }

        btnChooseLogo.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnSave.setOnClickListener(v -> {

            if (edtName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Nom obligatoire !", Toast.LENGTH_SHORT).show();
                return;
            }

            Company c = new Company(
                    companyId,
                    edtName.getText().toString(),
                    edtServices.getText().toString(),
                    edtPhone.getText().toString(),
                    edtURL.getText().toString(),
                    edtEmail.getText().toString(),
                    edtLocation.getText().toString(),
                    logoUri
            );

            boolean success;
            if (companyId == -1) {
                success = db.insertCompany(c);
            } else {
                success = db.updateCompany(c);
            }

            if (success) {
                Toast.makeText(this, "Entreprise sauvegardée !", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
