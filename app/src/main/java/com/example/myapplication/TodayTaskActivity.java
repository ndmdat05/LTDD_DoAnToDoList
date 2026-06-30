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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;

public class TodayTaskActivity extends AppCompatActivity {
    private RecyclerView rcvTasks;
    private TodayTaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private TextView tvFilterAll, tvFilterTodo, tvFilterInProgress, tvFilterCompleted, tvEmptyState;
    private FloatingActionButton fabAddProject;

    BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todaytask);

        // Ánh xạ toàn bộ các View từ XML
        rcvTasks = findViewById(R.id.rcvTasks);
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterTodo = findViewById(R.id.tvFilterTodo);
        tvFilterInProgress = findViewById(R.id.tvFilterInProgress);
        tvFilterCompleted = findViewById(R.id.tvFilterCompleted);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddProject = findViewById(R.id.fab_add_project);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish(); // Bấm mũi tên back thì đóng màn hình này để về trang chủ
        });
        // ========================================================================

        //  Khởi tạo cấu trúc và nạp Adapter lên RecyclerView trước
        taskList = new ArrayList<>();
        taskAdapter = new TodayTaskAdapter(taskList);
        rcvTasks.setLayoutManager(new LinearLayoutManager(this));
        rcvTasks.setAdapter(taskAdapter);

        //  Kết nối mạng trực tuyến Realtime Database dựa theo tài khoản người dùng
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://ltdd-doantodolist-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Tasks")
                .child(currentUserId);

        // Cài đặt bộ lắng nghe để tự bốc dữ liệu mềm từ mạng về điện thoại
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear(); // Xóa dữ liệu cũ tránh trùng lặp việc khi mạng có cập nhật mới

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TodayTaskActivity.this, "Lỗi kết nối Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();            }
        });

        //  Bấm nút dấu cộng (+) -> Tự động chuyển cảnh sang màn hình Thêm Dự Án
        fabAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayTaskActivity.this, AddProjectActivity.class);
                startActivity(intent);
            }
        });

        //  Xử lý sự kiện bấm các Tab bộ lọc trạng thái công việc
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
        setupDynamicWeekCalendar();
    }

    private void setupDynamicWeekCalendar() {
        int[] monthIds = {R.id.tv_week_month_1, R.id.tv_week_month_2, R.id.tv_week_month_3, R.id.tv_week_month_4, R.id.tv_week_month_5};
        int[] dateIds = {R.id.tv_week_date_1, R.id.tv_week_date_2, R.id.tv_week_date_3, R.id.tv_week_date_4, R.id.tv_week_date_5};
        int[] dayIds = {R.id.tv_week_day_1, R.id.tv_week_day_2, R.id.tv_week_day_3, R.id.tv_week_day_4, R.id.tv_week_day_5};
        String[] dayNames = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (int i = 0; i < 5; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, i - 2);
            TextView tvMonth = findViewById(monthIds[i]);
            TextView tvDate = findViewById(dateIds[i]);
            TextView tvDay = findViewById(dayIds[i]);

            if (tvMonth != null) {
                tvMonth.setText("Th" + (calendar.get(Calendar.MONTH) + 1));
            }
            if (tvDate != null) {
                tvDate.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            }
            if (tvDay != null) {
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1: Chủ Nhật, 2: Thứ Hai...
                tvDay.setText(dayNames[dayOfWeek - 1]);
            }
        }
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
        int purplePrimary = ContextCompat.getColor(this, R.color.purple_primary);
        int white = ContextCompat.getColor(this, R.color.white);

        TextView[] buttons = {tvFilterAll, tvFilterTodo, tvFilterInProgress, tvFilterCompleted};
        for (TextView btn : buttons) {
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_light));
            btn.setTextColor(purplePrimary);
        }

        selectedBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_primary));
        selectedBtn.setTextColor(white);
    }
}