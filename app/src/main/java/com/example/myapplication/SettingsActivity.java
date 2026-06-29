package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    private TextView tvName, tvEmail;
    private LinearLayout layoutProfile, layoutPassword;
    private Switch switchTheme;
    private Button btnLogout;
    private LinearLayout layoutLoggedIn, layoutGuest;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imgAvatar;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs =
                getSharedPreferences("SETTING", MODE_PRIVATE);

        boolean isDark =
                prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        checkLoginState();
        loadUserInfo();
        handleEvents();
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
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
        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(
                        this, LoginActivity.class)));

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(
                        this, RegisterActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        checkLoginState();
        loadUserInfo();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        imgAvatar = findViewById(R.id.imgAvatar);

        layoutProfile = findViewById(R.id.layoutProfile);
        layoutPassword = findViewById(R.id.layoutPassword);

        switchTheme = findViewById(R.id.switchTheme);
        btnLogout = findViewById(R.id.btnLogout);

        layoutLoggedIn = findViewById(R.id.layoutLoggedIn);
        layoutGuest = findViewById(R.id.layoutGuest);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void loadUserInfo() {
        if (currentUser != null) {

            // Email
            tvEmail.setText(currentUser.getEmail());

            // Tên
            String name = currentUser.getDisplayName();

            if (name != null && !name.isEmpty()) {
                tvName.setText(name);
            } else {
                tvName.setText("Người dùng");
            }

            // Ảnh đại diện
            if (currentUser.getPhotoUrl() != null) {

                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(imgAvatar);

            } else {
                imgAvatar.setImageResource(R.drawable.ic_default_avatar);
            }
        }

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

                                FirebaseAuth.getInstance().signOut();

                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);

                                finish();

                            })

                    .setNegativeButton("Không", null)
                    .show();

        });

    }

    private void checkLoginState() {

        if (currentUser != null) {

            layoutLoggedIn.setVisibility(View.VISIBLE);
            layoutGuest.setVisibility(View.GONE);

        } else {
            layoutLoggedIn.setVisibility(View.GONE);
            layoutGuest.setVisibility(View.VISIBLE);
        }
    }
}
