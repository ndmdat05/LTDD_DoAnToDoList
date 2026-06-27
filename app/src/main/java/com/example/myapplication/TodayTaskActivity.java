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

        // 2. Đổi mốc thời gian từ chữ sang số int để khớp cú pháp
        taskList = new ArrayList<>();
        // Đổi "Grocery shopping app design" -> "Mua sắm"
        taskList.add(new Task("Mua sắm", "Market Research", 10, "Đã xong"));
        taskList.add(new Task("Mua sắm", "Competitive Analysis", 12, "Đang làm"));

        // Đổi "Uber Eats redesign challange" -> "Cá nhân"
        taskList.add(new Task("Cá nhân", "Create Low-fidelity Wireframe", 19, "Cần làm"));

        taskList.add(new Task("Công việc", "How to pitch a Design Sprint", 21, "Cần làm"));
        // 3. Đổ dữ liệu lên RecyclerView động mượt mà bằng Adapter
        taskAdapter = new TodayTaskAdapter(taskList);
        rcvTasks.setLayoutManager(new LinearLayoutManager(this));
        rcvTasks.setAdapter(taskAdapter);

        // Kiểm tra xem có dữ liệu không để ẩn/hiện chữ trạng thái trống
        checkEmptyState(taskList);

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
                    Intent intent = new Intent(TodayTaskActivity.this, MainActivity.class);
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