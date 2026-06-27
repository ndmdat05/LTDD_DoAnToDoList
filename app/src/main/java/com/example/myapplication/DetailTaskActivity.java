package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class DetailTaskActivity extends AppCompatActivity {

    private LinearLayout layoutSubtasks;
    private TextView tvProgressPercentage;
    private TextView tvTaskCount;
    private CircularProgressIndicator circularProgress;
    private View btnAddSubtask;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAddSubtask;

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
        circularProgress = findViewById(R.id.circularProgress);
        btnAddSubtask = findViewById(R.id.btn_add_subtask);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabAddSubtask = findViewById(R.id.fab_add_project);

        // Set Task Data from Intent
        String title = getIntent().getStringExtra("task_title");
        String category = getIntent().getStringExtra("task_category");

        if (title != null) tvTitleDetail.setText(title);
        if (category != null) tvCategoryDetail.setText(category);

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

        // Setup Initial Subtasks
        setupExistingSubtasks();
        
        // Initial update
        updateProgress();
    }

    private void setupExistingSubtasks() {
        int count = layoutSubtasks.getChildCount();
        for (int i = 0; i < count; i++) {
            setupSubtaskItem(layoutSubtasks.getChildAt(i));
        }
    }

    private void setupSubtaskItem(View subtaskView) {
        ImageView ivCheck = subtaskView.findViewById(R.id.ivCheck);
        ImageView ivEdit = subtaskView.findViewById(R.id.ivEdit);
        ImageView ivDelete = subtaskView.findViewById(R.id.ivDelete);
        TextView tvTitle = subtaskView.findViewById(R.id.tvSubtaskTitle);
        
        // Set initial state (false) if not already set
        if (ivCheck.getTag() == null) {
            ivCheck.setTag(false);
        }

        // Toggle Checkbox
        subtaskView.setOnClickListener(v -> {
            boolean isChecked = (boolean) ivCheck.getTag();
            isChecked = !isChecked;
            updateCheckState(ivCheck, isChecked);
            updateProgress();
        });

        // Edit Subtask
        ivEdit.setOnClickListener(v -> {
            showSubtaskDialog("Chỉnh sửa công việc", tvTitle.getText().toString(), true, subtaskView);
        });

        // Delete Subtask
        ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Xóa công việc")
                .setMessage("Bạn có chắc chắn muốn xóa công việc này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    layoutSubtasks.removeView(subtaskView);
                    updateProgress();
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
    }

    private void updateCheckState(ImageView ivCheck, boolean isChecked) {
        ivCheck.setTag(isChecked);
        if (isChecked) {
            ivCheck.setImageResource(android.R.drawable.checkbox_on_background);
            ivCheck.setColorFilter(getResources().getColor(R.color.purple_primary));
        } else {
            ivCheck.setImageResource(android.R.drawable.checkbox_off_background);
            ivCheck.setColorFilter(getResources().getColor(R.color.grey_text));
        }
    }

    private void showSubtaskDialog(String title, String initialText, boolean isEdit, View itemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(initialText);
        input.setHint("Nhập tên công việc");
        
        // Add padding to EditText for better look in Dialog
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        builder.setView(input);
        
        // Alternatively, use a custom layout if needed, but for simplicity:
        // builder.setView(input); is direct. 

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (text.isEmpty()) return;

            if (isEdit && itemView != null) {
                TextView tv = itemView.findViewById(R.id.tvSubtaskTitle);
                tv.setText(text);
            } else {
                addNewSubtask(text);
            }
            updateProgress();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addNewSubtask(String title) {
        View subtaskView = LayoutInflater.from(this).inflate(R.layout.item_subtask, layoutSubtasks, false);
        TextView tvTitle = subtaskView.findViewById(R.id.tvSubtaskTitle);
        tvTitle.setText(title);
        
        setupSubtaskItem(subtaskView);
        layoutSubtasks.addView(subtaskView);
    }

    private void updateProgress() {
        int total = layoutSubtasks.getChildCount();
        int completed = 0;
        
        for (int i = 0; i < total; i++) {
            View subtaskView = layoutSubtasks.getChildAt(i);
            ImageView ivCheck = subtaskView.findViewById(R.id.ivCheck);
            if (ivCheck.getTag() != null && (boolean) ivCheck.getTag()) {
                completed++;
            }
        }
        
        int percent = (total > 0) ? (completed * 100 / total) : 0;
        
        tvProgressPercentage.setText(percent + "%");
        tvTaskCount.setText(completed + " / " + total + " công việc đã hoàn thành");
        circularProgress.setProgress(percent, true);
    }
}
