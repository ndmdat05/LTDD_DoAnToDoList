package com.example.myapplication;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> {

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

            // TODO: Kiểm tra mật khẩu cũ trong database
            // TODO: Cập nhật mật khẩu mới vào database

            Toast.makeText(this,
                    "Đổi mật khẩu thành công",
                    Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}
