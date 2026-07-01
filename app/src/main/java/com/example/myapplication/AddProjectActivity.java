package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.widget.LinearLayout;
import java.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.MenuItem;
import android.widget.DatePicker;
import androidx.appcompat.widget.PopupMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AddProjectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tvSelectedGroup = findViewById(R.id.tv_selected_group);
        ImageView imgArrowGroup = findViewById(R.id.img_arrow_group);
        ImageView imgGroupIcon = findViewById(R.id.img_group_icon);

        imgArrowGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AddProjectActivity.this, imgArrowGroup);
                String[] taskGroups = getResources().getStringArray(R.array.array_task_groups);
                for (String group : taskGroups) {
                    popupMenu.getMenu().add(group);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {                    @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String selected = item.getTitle().toString();
                    tvSelectedGroup.setText(selected);

                    if (selected.equals("Công việc")) {
                        imgGroupIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFDEEF")));
                        imgGroupIcon.setImageResource(R.drawable.outline_work_24);
                        imgGroupIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF4081")));
                    } else if (selected.equals("Cá nhân")) {
                        imgGroupIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0F2FE")));
                        imgGroupIcon.setImageResource(R.drawable.outline_person_24);
                        imgGroupIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#0284C7")));
                    } else if (selected.equals("Sức khỏe")) {
                        imgGroupIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1F2D9")));
                        imgGroupIcon.setImageResource(R.drawable.outline_fitness_trackers_24);
                        imgGroupIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#16A34A")));
                    } else if (selected.equals("Mua sắm")) {
                        imgGroupIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FEF3C7")));
                        imgGroupIcon.setImageResource(R.drawable.outline_store_24);
                        imgGroupIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#D97706")));
                    }

                    return true;
                }
                });

                popupMenu.show();
            }
        });

        LinearLayout layoutStartDate = findViewById(R.id.layout_start_date);
        TextView tvStartDate = findViewById(R.id.tv_start_date);
        LinearLayout layoutEndDate = findViewById(R.id.layout_end_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);

        layoutStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {                                String date = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                                tvStartDate.setText(date);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        layoutEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {                                String date = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                                tvEndDate.setText(date);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        EditText edtTaskTitle = findViewById(R.id.edt_task_title);
        EditText edtTaskContent = findViewById(R.id.edt_task_content);
        Button btnSaveTask = findViewById(R.id.btn_add_project);
        View btnBell = findViewById(R.id.btn_bell);

        if (btnBell != null) {
            btnBell.setOnClickListener(v -> {
                Intent intent = new Intent(AddProjectActivity.this, TodayTaskActivity.class);
                intent.putExtra("filter_type", "Cần làm");
                startActivity(intent);
            });
        }

        if (btnSaveTask != null) {
            btnSaveTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = (edtTaskTitle != null) ? edtTaskTitle.getText().toString().trim() : "";
                    String content = (edtTaskContent != null) ? edtTaskContent.getText().toString().trim() : "";
                    String category = tvSelectedGroup.getText().toString();
                    String startDate = tvStartDate.getText().toString();
                    String endDate = tvEndDate.getText().toString();

                    if (title.isEmpty()) {
                        if (edtTaskTitle != null) {
                            edtTaskTitle.setError("Vui lòng nhập tên công việc!");
                        }
                        return;
                    }
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        Toast.makeText(AddProjectActivity.this, "Lỗi: Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Đã đưa về đúng 4 tham số khớp với Constructor hiện tại của Task để sạch bóng báo đỏ
                    Task newTask = new Task(category, title, content, 0, "Cần làm", startDate, endDate);

                    FirebaseDatabase.getInstance("https://ltdd-doantodolist-default-rtdb.asia-southeast1.firebasedatabase.app/")                            .getReference("Tasks")
                            .child(user.getUid())
                            .push()
                            .setValue(newTask)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddProjectActivity.this, "Thêm công việc thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddProjectActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }
    }
}