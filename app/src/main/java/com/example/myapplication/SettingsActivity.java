package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {
    private TextView tvName,tvEmail;
    private LinearLayout layoutProfile, layoutPassword;
    private Switch switchTheme;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        loadUserInfo();
        handleEvents();
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home){
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.nav_calendar) {
                Intent intent = new Intent(SettingsActivity.this, TodayTaskActivity.class);
                startActivity(intent);
                return true;
            }
            return true;
        });
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);

        layoutProfile = findViewById(R.id.layoutProfile);
        layoutPassword = findViewById(R.id.layoutPassword);

        switchTheme = findViewById(R.id.switchTheme);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserInfo() {
        tvName.setText("Cristiano Ronaldo");
        tvEmail.setText("cr7@gmail.com");

    }

    private void handleEvents() {

        // Xem thông tin tài khoản
        layoutProfile.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);

            startActivity(intent);

        });


        // Đổi mật khẩu
        layoutPassword.setOnClickListener(v -> {

            Intent intent =
                    new Intent(SettingsActivity.this,
                            ChangePasswordActivity.class);

            startActivity(intent);

        });


        // Dark mode
        SharedPreferences prefs =
                getSharedPreferences("SETTING", MODE_PRIVATE);

        boolean isDark = prefs.getBoolean("dark_mode", false);

        switchTheme.setChecked(isDark);

        switchTheme.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("dark_mode", isChecked);
                    editor.apply();

                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO);
                    }

                });


        // Đăng xuất
        btnLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Có",
                            (dialog, which) -> {

                                // Xóa phiên đăng nhập

                                SharedPreferences preferences =
                                        getSharedPreferences(
                                                "USER_FILE",
                                                MODE_PRIVATE);

                                preferences.edit().clear().apply();

                                /*
                                Nếu Firebase:

                                FirebaseAuth.getInstance().signOut();
                                */

                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);

                                finish();

                            })

                    .setNegativeButton("Không", null)
                    .show();

        });

    }
}
