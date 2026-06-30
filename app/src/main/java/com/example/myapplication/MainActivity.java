package com.example.myapplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView rcvInProgress, rcvTaskGroups;
    TaskAdapter taskAdapter;
    TaskGroupAdapter groupAdapter;
    ArrayList<Task> taskList;
    ArrayList<TaskGroup> groupList;
    EditText edtSearch;
    TextView tvFilterAll, tvFilterProgress, tvFilterCompleted;
    TextView tvUserName, tvBannerPercent;
    ProgressBar progressBanner;
    com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigation;
    com.google.firebase.database.DatabaseReference myRefTasks, myRefGroups;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Anh xa view
        rcvInProgress = findViewById(R.id.rcvInProgress);
        rcvTaskGroups = findViewById(R.id.rcvTaskGroups);
        edtSearch = findViewById(R.id.edtSearch);
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterProgress = findViewById(R.id.tvFilterProgress);
        tvFilterCompleted = findViewById(R.id.tvFilterCompleted);
       //Ánh xạ thanh điều hướng từ file XML vào Java
        bottomNavigation = findViewById(R.id.bottom_navigation);
        // anh xa cho Header
        tvUserName = findViewById(R.id.tvUserName);
        tvBannerPercent = findViewById(R.id.tvBannerPercent);
        progressBanner = findViewById(R.id.progressBanner);

        // Lay thong tin user tu dang nhap
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            if (name != null && !name.isEmpty()) {
                tvUserName.setText(name);
            } else {
                tvUserName.setText("Người dùng");
            }
        } else {
            tvUserName.setText("Khách");
        }

        //Khoi tao danh sach rong
        taskList = new ArrayList<>();

        //Khoi tao danh sach nhom rong
        groupList = new ArrayList<>();

        //set adapter cuon ngang
        taskAdapter = new TaskAdapter(taskList);
        rcvInProgress.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvInProgress.setAdapter(taskAdapter);

        //set adapter cuon doc
        groupAdapter = new TaskGroupAdapter(groupList);
        rcvTaskGroups.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvTaskGroups.setAdapter(groupAdapter);

        //Danh sach cv (Tasks)
        if (currentUser != null) {
            myRefTasks = com.google.firebase.database.FirebaseDatabase.getInstance("https://ltdd-doantodolist-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Tasks").child(currentUser.getUid());
            myRefTasks.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                    taskList.clear();
                    int demViecDaXong = 0;

                for (com.google.firebase.database.DataSnapshot data : snapshot.getChildren()) {
                    Task task = data.getValue(Task.class);
                    if (task != null) {
                        task.setId(data.getKey()); // Gán ID từ Firebase key
                        taskList.add(task);
                        if ("Đã xong".equals(task.getStatus())) {
                            demViecDaXong++;
                        }
                    }
                    taskAdapter.notifyDataSetChanged();

                    //Phan tram tien do
                    int tongSoViec = taskList.size();
                    int phanTram = 0;
                    if (tongSoViec > 0) {
                        phanTram = (demViecDaXong * 100) / tongSoViec;
                    }
                    progressBanner.setProgress(phanTram);
                    tvBannerPercent.setText(phanTram + "%");
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                }
            });
        }

        //Danh sach nhom cv (TaskGroups)
        myRefGroups = com.google.firebase.database.FirebaseDatabase.getInstance("https://ltdd-doantodolist-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("TaskGroups");
        myRefGroups.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                groupList.clear();
                for (com.google.firebase.database.DataSnapshot data : snapshot.getChildren()) {
                    TaskGroup group = data.getValue(TaskGroup.class);
                    if (group != null) {
                        groupList.add(group);
                    }
                }
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
            }
        });

        //xu ly click nut loc
        tvFilterAll.setOnClickListener(v -> {
            taskAdapter.setFilteredList(taskList);
            setButtonSelected(tvFilterAll);
        });

        tvFilterProgress.setOnClickListener(v -> {
            ArrayList<Task> filterList = new ArrayList<>();
            for (Task t : taskList) {
                if (t.getStatus().equals("Đang làm")) {
                    filterList.add(t);
                }
            }
            taskAdapter.setFilteredList(filterList);
            setButtonSelected(tvFilterProgress);
        });

        tvFilterCompleted.setOnClickListener(v -> {
            ArrayList<Task> filterList = new ArrayList<>();
            for (Task t : taskList) {
                if (t.getStatus().equals("Đã xong")) {
                    filterList.add(t);
                }
            }
            taskAdapter.setFilteredList(filterList);
            setButtonSelected(tvFilterCompleted);
        });

        //xu ly tim kiem bang TextWatcher
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                //Lay chu ng dung go, chuyen chu thuong, roi bo dau
                String keyword = removeAccents(s.toString().toLowerCase().trim());
                ArrayList<Task> searchList = new ArrayList<>();
                for (Task t : taskList) {
                    //Lay ten task, cung chuyen chu thuong va bo dau
                    String titleNoAccent = removeAccents(t.getTitle().toLowerCase());
                    //Dem so sanh 2 cai da bo dau voi nhau
                    if (titleNoAccent.contains(keyword)) {
                        searchList.add(t);
                    }
                }
                taskAdapter.setFilteredList(searchList);
            }
        });
        //  Lắng nghe sự kiện bấm nút Lịch (Calendar) trên BottomNav
        // để mở màn hình TodayTaskActivity của ông lên.
        // ========================================================================
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_calendar) {
                Intent intent = new Intent(MainActivity.this, TodayTaskActivity.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.nav_profile){
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return true;

        });
        findViewById(R.id.fab_add_project).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });


    }

    //ham doi mau nut
    private void setButtonSelected(TextView selectedBtn) {
        //dua 3 nut ve mau mac dinh
        tvFilterAll.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_light));
        tvFilterAll.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.purple_primary));

        tvFilterProgress.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_light));
        tvFilterProgress.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.purple_primary));

        tvFilterCompleted.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_light));
        tvFilterCompleted.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.purple_primary));
        // to mau dam cho nut hien tai
        selectedBtn.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_primary));
        selectedBtn.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white));
    }

    // Ham bo dau tieng viet de tim kiem
    private String removeAccents(String str) {
        try {
            String temp = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replace("đ", "d").replace("Đ", "D");
        } catch (Exception e) {
            return str;
        }
    }
}