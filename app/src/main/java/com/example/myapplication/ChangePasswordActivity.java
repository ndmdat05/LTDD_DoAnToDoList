package com.example.myapplication;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {

        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        if (oldPass.isEmpty() ||
                newPass.isEmpty() ||
                confirmPass.isEmpty()) {

            Toast.makeText(this,
                    "Vui lòng nhập đầy đủ thông tin",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {

            Toast.makeText(this,
                    "Mật khẩu xác nhận không khớp",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {

            Toast.makeText(this,
                    "Mật khẩu mới phải từ 6 ký tự",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String email = currentUser.getEmail();

        // Xác thực lại tài khoản
        AuthCredential credential =
                EmailAuthProvider.getCredential(email, oldPass);

        currentUser.reauthenticate(credential)
                .addOnSuccessListener(unused -> {

                    // Đổi mật khẩu
                    currentUser.updatePassword(newPass)
                            .addOnSuccessListener(unused1 -> {

                                Toast.makeText(
                                        ChangePasswordActivity.this,
                                        "Đổi mật khẩu thành công",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                            })

                            .addOnFailureListener(e ->

                                    Toast.makeText(
                                            ChangePasswordActivity.this,
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show());

                })

                .addOnFailureListener(e ->

                        Toast.makeText(
                                ChangePasswordActivity.this,
                                "Mật khẩu cũ không đúng",
                                Toast.LENGTH_SHORT
                        ).show());
    }
}