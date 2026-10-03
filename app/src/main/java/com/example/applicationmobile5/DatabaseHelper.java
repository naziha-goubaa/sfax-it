package com.example.applicationmobile5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "companiess.db";
    private static final int DB_VERSION = 8;

    // TABLE COMPANIES
    private static final String TABLE = "companies";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_SERVICES = "services";
    private static final String COL_PHONE = "phone";
    private static final String COL_URL = "url";
    private static final String COL_EMAIL = "email";
    private static final String COL_LOCATION = "location";
    private static final String COL_LOGO = "logo";

    // TABLE USERS
    private static final String TABLE_USER = "users";
    private static final String U_ID = "id";
    private static final String U_EMAIL = "email";
    private static final String U_PASS = "password";
    private static final String U_ROLE = "role";

    public DatabaseHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE COMPANIES TABLE
        String q = "CREATE TABLE " + TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_SERVICES + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_URL + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_LOCATION + " TEXT, " +
                COL_LOGO + " TEXT)";
        db.execSQL(q);

        // CREATE USERS TABLE
        String q2 = "CREATE TABLE " + TABLE_USER + " (" +
                U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                U_EMAIL + " TEXT UNIQUE, " +
                U_PASS + " TEXT, " +
                U_ROLE + " TEXT DEFAULT 'user')";
        db.execSQL(q2);

        // Insert default companies
        insertDefaultCompanies(db);

        // Insert default admin
        ContentValues cv = new ContentValues();
        cv.put(U_EMAIL, "admin@admin.com");
        cv.put(U_PASS, sha256("Admin123!"));
        cv.put(U_ROLE, "admin");
        db.insert(TABLE_USER, null, cv);
        Log.d(TAG, "Admin par défaut créé : admin@admin.com / Admin123!");

        // Insert test user
        cv.clear();
        cv.put(U_EMAIL, "user@user.com");
        cv.put(U_PASS, sha256("User123!"));
        cv.put(U_ROLE, "user");
        db.insert(TABLE_USER, null, cv);
        Log.d(TAG, "Utilisateur test créé : user@user.com / User123!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    private void insertDefaultCompanies(SQLiteDatabase db) {
        insertCompany(db, "FOD", "Consulting,Development", "70 000 000",
                "https://fod.com", "contact@fod.com", "Sfax", String.valueOf(R.mipmap.fod));
        insertCompany(db, "Spark-it", "Cloud,Solutions", "71 111 111",
                "https://spark-it.com", "info@spark-it.com", "Sfax", String.valueOf(R.mipmap.spark_it));
        insertCompany(db, "Sofrecom", "Telecom,IT Services", "72 222 222",
                "https://sofrecom.com", "contact@sofrecom.com", "Sfax", String.valueOf(R.mipmap.sofrecom));
    }

    private void insertCompany(SQLiteDatabase db, String name, String services, String phone,
                               String url, String email, String location, String logoUri) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_SERVICES, services);
        cv.put(COL_PHONE, phone);
        cv.put(COL_URL, url);
        cv.put(COL_EMAIL, email);
        cv.put(COL_LOCATION, location);
        cv.put(COL_LOGO, logoUri);
        db.insert(TABLE, null, cv);
    }

    public boolean insertCompany(Company c) {
        if (c == null) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, c.getName());
        cv.put(COL_SERVICES, c.getServices());
        cv.put(COL_PHONE, c.getPhone());
        cv.put(COL_URL, c.getUrl());
        cv.put(COL_EMAIL, c.getEmail());
        cv.put(COL_LOCATION, c.getLocation());
        cv.put(COL_LOGO, c.getLogoUri());
        long id = db.insert(TABLE, null, cv);
        if (id != -1) c.setId((int) id);
        return id != -1;
    }

    public boolean updateCompany(Company c) {
        if (c == null || c.getId() < 0) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, c.getName());
        cv.put(COL_SERVICES, c.getServices());
        cv.put(COL_PHONE, c.getPhone());
        cv.put(COL_URL, c.getUrl());
        cv.put(COL_EMAIL, c.getEmail());
        cv.put(COL_LOCATION, c.getLocation());
        cv.put(COL_LOGO, c.getLogoUri());
        int updated = db.update(TABLE, cv, COL_ID + "=?", new String[]{String.valueOf(c.getId())});
        return updated > 0;
    }

    public void deleteCompany(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public ArrayList<Company> getAllCompanies() {
        ArrayList<Company> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE + " ORDER BY name", null);
        if (c.moveToFirst()) {
            do {
                list.add(new Company(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getString(6),
                        c.getString(7)
                ));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    // MODIFIÉ : Inscription automatique en tant qu'utilisateur
    public boolean insertUser(String email, String password) {
        return insertUser(email, password, "user"); // Toujours "user" par défaut
    }

    public boolean insertUser(String email, String password, String role) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Log.e(TAG, "Email ou mot de passe vide");
            return false;
        }

        // Valider le mot de passe
        if (!validatePasswordStrength(password)) {
            Log.e(TAG, "Mot de passe trop faible: " + password);
            return false;
        }

        // Toujours "user" pour les inscriptions normales
        String finalRole = "user";
        email = email.toLowerCase().trim();
        String hash = sha256(password);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_EMAIL, email);
        cv.put(U_PASS, hash);
        cv.put(U_ROLE, finalRole);

        try {
            long id = db.insertOrThrow(TABLE_USER, null, cv);
            Log.d(TAG, "Utilisateur inséré : " + email + " (" + finalRole + "), ID=" + id);
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "Erreur insertion user : " + email + " (" + finalRole + ")", ex);
            return false;
        }
    }

    public String checkUserAndGetRole(String email, String password) {
        if (email == null || password == null) return null;
        email = email.toLowerCase().trim();
        String hash = sha256(password);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String role = null;
        try {
            c = db.query(TABLE_USER, new String[]{U_ROLE},
                    U_EMAIL + "=? AND " + U_PASS + "=?",
                    new String[]{email, hash},
                    null, null, null);
            if (c != null && c.moveToFirst()) {
                role = c.getString(c.getColumnIndexOrThrow(U_ROLE));
            }
        } catch (Exception ex) {
            Log.e(TAG, "Erreur checkUserAndGetRole", ex);
        } finally {
            if (c != null) c.close();
        }
        return role;
    }

    private String sha256(String base) {
        if (base == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String hexB = Integer.toHexString(0xff & b);
                if (hexB.length() == 1) hex.append('0');
                hex.append(hexB);
            }
            return hex.toString();
        } catch (Exception ex) {
            Log.e(TAG, "Erreur SHA256", ex);
            return "";
        }
    }

    public boolean checkUser(String email, String password) {
        if (email == null || password == null) return false;
        email = email.toLowerCase().trim();
        String hash = sha256(password);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + U_EMAIL + "=? AND " + U_PASS + "=?",
                new String[]{email, hash});
        boolean ok = c.getCount() > 0;
        c.close();
        return ok;
    }

    public static boolean validatePasswordStrength(String pwd) {
        if (pwd == null) return false;
        Pattern p = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#\\$%\\^&\\+=!])(?=\\S+$).{8,}$");
        return p.matcher(pwd).matches();
    }

    public boolean emailExists(String email) {
        if (email == null) return false;
        email = email.toLowerCase().trim();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + U_EMAIL + "=?",
                new String[]{email});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }
}