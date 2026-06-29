package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class TodayTaskActivity extends AppCompatActivity {
    private RecyclerView rcvTasks;
    private TodayTaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private TextView tvFilterAll, tvFilterTodo, tvFilterInProgress, tvFilterCompleted, tvEmptyState;
    private FloatingActionButton fabAddProject;

    // ========================================================================
    // >>> CHỖ NÀY THÊM: Khai báo biến thanh điều hướng đáy cho màn hình này
    // ========================================================================
    com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todaytask);

        // 1. Ánh xạ toàn bộ các View từ XML
        rcvTasks = findViewById(R.id.rcvTasks);
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterTodo = findViewById(R.id.tvFilterTodo);
        tvFilterInProgress = findViewById(R.id.tvFilterInProgress);
        tvFilterCompleted = findViewById(R.id.tvFilterCompleted);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddProject = findViewById(R.id.fab_add_project);

        // ========================================================================
        // >>> CHỖ NÀY THÊM: Ánh xạ thanh điều hướng đáy và nút mũi tên quay lại
        // ========================================================================
        bottomNavigation = findViewById(R.id.bottom_navigation);

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish(); // Bấm mũi tên back thì đóng màn hình này để về trang chủ
        });
        // ========================================================================

        // 2. Khởi tạo cấu trúc và nạp Adapter lên RecyclerView trước
        taskList = new ArrayList<>();
        taskAdapter = new TodayTaskAdapter(taskList);
        rcvTasks.setLayoutManager(new LinearLayoutManager(this));
        rcvTasks.setAdapter(taskAdapter);

        // 3. Kết nối mạng trực tuyến Realtime Database dựa theo tài khoản người dùng
        String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        com.google.firebase.database.DatabaseReference databaseRef =
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("tasks").child(currentUserId);

        // Cài đặt bộ lắng nghe để tự bốc dữ liệu mềm từ mạng về điện thoại
        databaseRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                taskList.clear(); // Xóa dữ liệu cũ tránh trùng lặp việc khi mạng có cập nhật mới

                for (com.google.firebase.database.DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }

                // Ra lệnh cho Adapter vẽ lại giao diện dựa theo việc thật vừa tải về
                taskAdapter.setFilteredList(taskList);
                checkEmptyState(taskList);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                android.widget.Toast.makeText(TodayTaskActivity.this, "Lỗi kết nối Firebase: " + error.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Bấm nút dấu cộng (+) -> Tự động chuyển cảnh sang màn hình Thêm Dự Án
        fabAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayTaskActivity.this, AddProjectActivity.class);
                startActivity(intent);
            }
        });

        // 5. Xử lý sự kiện bấm các Tab bộ lọc trạng thái công việc
        tvFilterAll.setOnClickListener(v -> {
            taskAdapter.setFilteredList(taskList);
            setButtonSelected(tvFilterAll);
            checkEmptyState(taskList);
        });

        tvFilterTodo.setOnClickListener(v -> {
            ArrayList<Task> filterList = new ArrayList<>();
            for (Task task : taskList) {
                if (task.getStatus().equals("Cần làm")) {
                    filterList.add(task);
                }
            }
            taskAdapter.setFilteredList(filterList);
            setButtonSelected(tvFilterTodo);
            checkEmptyState(filterList);
        });

        tvFilterInProgress.setOnClickListener(v -> {
            ArrayList<Task> filterList = new ArrayList<>();
            for (Task task : taskList) {
                if (task.getStatus().equals("Đang làm")) {
                    filterList.add(task);
                }
            }
            taskAdapter.setFilteredList(filterList);
            setButtonSelected(tvFilterInProgress);
            checkEmptyState(filterList);
        });

        tvFilterCompleted.setOnClickListener(v -> {
            ArrayList<Task> filterList = new ArrayList<>();
            for (Task task : taskList) {
                if (task.getStatus().equals("Đã xong")) {
                    filterList.add(task);
                }
            }
            taskAdapter.setFilteredList(filterList);
            setButtonSelected(tvFilterCompleted);
            checkEmptyState(filterList);
        });

        // ========================================================================
        // >>> CHỖ NÀY THÊM: Lắng nghe sự kiện thanh đáy để bấm chuyển tab điều hướng
        // ========================================================================
        if (bottomNavigation != null) {
            // Giữ cho icon Lịch (Calendar) luôn sáng đèn khi đang ở màn hình này
            bottomNavigation.setSelectedItemId(R.id.nav_calendar);

            bottomNavigation.setOnItemSelectedListener(item -> {
                // Nếu bấm vào icon Trang chủ (Home) thì quay trở lại MainActivity
                if (item.getItemId() == R.id.nav_home) {
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.nav_profile){
                    Intent intent = new Intent(TodayTaskActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            });
        }
        // ========================================================================
    }

    // Hàm kiểm tra ẩn hiện chữ "Chưa có công việc nào!"
    private void checkEmptyState(ArrayList<Task> list) {
        if (list.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rcvTasks.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rcvTasks.setVisibility(View.VISIBLE);
        }
    }

    // Hàm đổi màu làm sáng nút bộ lọc đang click chọn
    private void setButtonSelected(TextView selectedBtn) {
        int purplePrimary = androidx.core.content.ContextCompat.getColor(this, R.color.purple_primary);
        int white = androidx.core.content.ContextCompat.getColor(this, R.color.white);

        TextView[] buttons = {tvFilterAll, tvFilterTodo, tvFilterInProgress, tvFilterCompleted};
        for (TextView btn : buttons) {
            btn.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_light));
            btn.setTextColor(purplePrimary);
        }

        selectedBtn.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_primary));
        selectedBtn.setTextColor(white);
    }
}