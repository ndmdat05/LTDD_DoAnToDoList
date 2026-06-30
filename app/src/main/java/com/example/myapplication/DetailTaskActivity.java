package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailTaskActivity extends AppCompatActivity {

    private LinearLayout layoutSubtasks;
    private TextView tvProgressPercentage;
    private TextView tvTaskCount;
    private TextView tvDescriptionDetail;
    private TextView tvStartDateDetail;
    private TextView tvEndDateDetail;
    private TextView tvSubtaskLabel;
    private android.widget.Button btnCompleteTask;
    private CircularProgressIndicator circularProgress;
    private View btnAddSubtask;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAddSubtask;
    
    private String taskId;
    private DatabaseReference taskRef;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailtask);

        // Initialize Views
        ImageView ivBack = findViewById(R.id.ivBack);
        TextView tvTitleDetail = findViewById(R.id.tvTitleDetail);
        TextView tvCategoryDetail = findViewById(R.id.tvCategoryDetail);
        
        layoutSubtasks = findViewById(R.id.layout_subtasks);
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        tvDescriptionDetail = findViewById(R.id.tvDescriptionDetail);
        tvStartDateDetail = findViewById(R.id.tvStartDateDetail);
        tvEndDateDetail = findViewById(R.id.tvEndDateDetail);
        tvSubtaskLabel = findViewById(R.id.tvSubtaskLabel);
        btnCompleteTask = findViewById(R.id.btnCompleteTask);
        circularProgress = findViewById(R.id.circularProgress);
        btnAddSubtask = findViewById(R.id.btn_add_subtask);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabAddSubtask = findViewById(R.id.fab_add_project);

        // Get Task ID from Intent
        taskId = getIntent().getStringExtra("task_id");
        String title = getIntent().getStringExtra("task_title");
        String category = getIntent().getStringExtra("task_category");

        if (title != null) tvTitleDetail.setText(title);
        if (category != null) tvCategoryDetail.setText(category);

        if (taskId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID công việc!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Lỗi: Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userId = user.getUid();
        taskRef = FirebaseDatabase.getInstance("https://ltdd-doantodolist-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Tasks")
                .child(userId)
                .child(taskId);

        ivBack.setOnClickListener(v -> finish());

        // Setup Add Subtask Click
        btnAddSubtask.setOnClickListener(v -> showSubtaskDialog("Thêm công việc", "", false, null));
        fabAddSubtask.setOnClickListener(v -> showSubtaskDialog("Thêm công việc", "", false, null));

        // Setup Bottom Navigation
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(DetailTaskActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.nav_calendar) {
                    Intent intent = new Intent(DetailTaskActivity.this, TodayTaskActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(DetailTaskActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            });
        }

        // Real-time Firebase Listener
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentTask = snapshot.getValue(Task.class);
                if (currentTask != null) {
                    currentTask.setId(snapshot.getKey());
                    
                    // Update static fields
                    if (currentTask.getContent() != null) tvDescriptionDetail.setText(currentTask.getContent());
                    if (currentTask.getStartDate() != null) tvStartDateDetail.setText(currentTask.getStartDate());
                    if (currentTask.getEndDate() != null) tvEndDateDetail.setText(currentTask.getEndDate());

                    updateUI(currentTask);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailTaskActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Task task) {
        layoutSubtasks.removeAllViews();
        Map<String, Subtask> subtasksMap = task.getSubtasks();
        
        int total = 0;
        int completed = 0;

        if (subtasksMap != null) {
            total = subtasksMap.size();
            for (Map.Entry<String, Subtask> entry : subtasksMap.entrySet()) {
                Subtask subtask = entry.getValue();
                subtask.setId(entry.getKey());
                
                View subtaskView = LayoutInflater.from(this).inflate(R.layout.item_subtask, layoutSubtasks, false);
                setupSubtaskItemView(subtaskView, subtask);
                layoutSubtasks.addView(subtaskView);

                if (subtask.isCompleted()) {
                    completed++;
                }
            }
        }

        int percent = (total > 0) ? (completed * 100 / total) : 0;
        tvProgressPercentage.setText(percent + "%");
        tvTaskCount.setText(completed + " / " + total + " công việc đã hoàn thành");
        tvSubtaskLabel.setText("Danh sách công việc (" + total + ")");
        circularProgress.setProgress(percent, true);

        // Show/Hide Complete Button
        if (percent == 100 && total > 0) {
            btnCompleteTask.setVisibility(View.VISIBLE);
            btnCompleteTask.setOnClickListener(v -> {
                Toast.makeText(this, "Chúc mừng! Bạn đã hoàn thành toàn bộ công việc.", Toast.LENGTH_LONG).show();
                finish();
            });
        } else {
            btnCompleteTask.setVisibility(View.GONE);
        }
        
        // Update progress back to Task on Firebase if it changed
        if (task.getProgress() != percent) {
            taskRef.child("progress").setValue(percent);
            
            // Auto update status
            String newStatus;
            if (percent == 100) {
                newStatus = "Đã xong";
            } else if (percent > 0) {
                newStatus = "Đang làm";
            } else {
                newStatus = "Cần làm";
            }
            
            if (!newStatus.equals(task.getStatus())) {
                taskRef.child("status").setValue(newStatus);
            }
        }
    }

    private void setupSubtaskItemView(View subtaskView, Subtask subtask) {
        ImageView ivCheck = subtaskView.findViewById(R.id.ivCheck);
        ImageView ivEdit = subtaskView.findViewById(R.id.ivEdit);
        ImageView ivDelete = subtaskView.findViewById(R.id.ivDelete);
        TextView tvTitle = subtaskView.findViewById(R.id.tvSubtaskTitle);
        
        tvTitle.setText(subtask.getTitle());
        updateCheckStateUI(ivCheck, subtask.isCompleted());

        // Toggle Checkbox
        subtaskView.setOnClickListener(v -> {
            boolean newState = !subtask.isCompleted();
            taskRef.child("subtasks").child(subtask.getId()).child("completed").setValue(newState);
        });

        // Edit Subtask
        ivEdit.setOnClickListener(v -> {
            showSubtaskDialog("Chỉnh sửa công việc", subtask.getTitle(), true, subtask);
        });

        // Delete Subtask
        ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Xóa công việc")
                .setMessage("Bạn có chắc chắn muốn xóa công việc này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    taskRef.child("subtasks").child(subtask.getId()).removeValue();
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
    }

    private void updateCheckStateUI(ImageView ivCheck, boolean isChecked) {
        if (isChecked) {
            ivCheck.setImageResource(android.R.drawable.checkbox_on_background);
            ivCheck.setColorFilter(getResources().getColor(R.color.purple_primary));
        } else {
            ivCheck.setImageResource(android.R.drawable.checkbox_off_background);
            ivCheck.setColorFilter(getResources().getColor(R.color.grey_text));
        }
    }

    private void showSubtaskDialog(String title, String initialText, boolean isEdit, Subtask subtask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(initialText);
        input.setHint("Nhập tên công việc");
        
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (text.isEmpty()) return;

            if (isEdit && subtask != null) {
                taskRef.child("subtasks").child(subtask.getId()).child("title").setValue(text);
            } else {
                addNewSubtaskToFirebase(text);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addNewSubtaskToFirebase(String title) {
        DatabaseReference newSubtaskRef = taskRef.child("subtasks").push();
        String subtaskId = newSubtaskRef.getKey();
        Subtask newSubtask = new Subtask(subtaskId, title, false);
        newSubtaskRef.setValue(newSubtask);
    }
}
