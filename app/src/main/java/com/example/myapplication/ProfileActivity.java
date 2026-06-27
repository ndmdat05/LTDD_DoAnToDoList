package com.example.myapplication;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone;
    private Button btnUpdate;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);

        // Demo dữ liệu
        edtName.setText("Cristiano Ronaldo");
        edtEmail.setText("cr7@gmail.com");
        edtPhone.setText("0123456789");

        btnBack.setOnClickListener(v -> finish());

        btnUpdate.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Cập nhật thành công",
                    Toast.LENGTH_SHORT).show();

            // TODO: cập nhật vào Database
        });
    }
}
