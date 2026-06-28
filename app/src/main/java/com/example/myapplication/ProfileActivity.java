package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone;
    private Button btnUpdate;
    private ImageButton btnBack;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imgAvatar;
    private Button btnChangeAvatar;
    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference userRef;
    private String name;
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {

        if (uri != null) {
            imageUri = uri;
            imgAvatar.setImageURI(uri);
        }

    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);

        storageRef = FirebaseStorage.getInstance().getReference("avatars");

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        btnChangeAvatar.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        edtEmail.setText(currentUser.getEmail());

        // Hiển thị tên
        name = currentUser.getDisplayName();

        if (name != null && !name.isEmpty()) {
            edtName.setText(name);
        } else {
            edtName.setText("Người dùng");
        }

        // Hiển thị ảnh đại diện
        if (currentUser.getPhotoUrl() != null) {

            Glide.with(ProfileActivity.this).load(currentUser.getPhotoUrl()).into(imgAvatar);

        }
        btnBack.setOnClickListener(v -> finish());

        btnUpdate.setOnClickListener(v -> {

            name = edtName.getText().toString().trim();

            if (imageUri != null) {
                uploadAvatar(name);
            } else {

                UserProfileChangeRequest profileUpdates =
                        new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                currentUser.updateProfile(profileUpdates)
                        .addOnSuccessListener(unused -> {

                            Toast.makeText(
                                    this,
                                    "Cập nhật thành công",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();
                        });
            }
        });
    }

    private void uploadAvatar(String name) {

        StorageReference avatarRef =
                storageRef.child(currentUser.getUid() + ".jpg");

        avatarRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->

                        avatarRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {

                                    UserProfileChangeRequest profileUpdates =
                                            new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .setPhotoUri(uri)
                                                    .build();

                                    currentUser.updateProfile(profileUpdates)
                                            .addOnSuccessListener(unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Cập nhật thành công",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                finish();
                                            });

                                }))

                .addOnFailureListener(e ->

                        Toast.makeText(
                                this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show());
    }
}
