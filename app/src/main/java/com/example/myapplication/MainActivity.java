package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView rcvTasks;
    TaskAdapter taskAdapter;
    ArrayList<Task> taskList;
    TextView tvFilterAll, tvFilterTodo, tvFilterInProgress, tvFilterCompleted, tvEmptyState;
    EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todaytask);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.today_task_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// //////////////////
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_project);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddProjectActivity.class);
                startActivity(intent);
            }
        });
        //Tìm id
        rcvTasks = findViewById(R.id.rcvTasks);
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterTodo = findViewById(R.id.tvFilterTodo);
        tvFilterInProgress = findViewById(R.id.tvFilterInProgress);
        tvFilterCompleted = findViewById(R.id.tvFilterCompleted);
        edtSearch = findViewById(R.id.edtSearch);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        //Tạo ds công việc
        taskList = new ArrayList<>();

        //Đưa dữ liệu lên RecyclerView
        taskAdapter = new TaskAdapter(taskList);
        rcvTasks.setLayoutManager(new LinearLayoutManager(this));
        rcvTasks.setAdapter(taskAdapter);

        //Kiểm tra danh sách trống
        if (taskList.isEmpty()) {
            tvEmptyState.setVisibility(android.view.View.VISIBLE); //"Chưa có cv"
            rcvTasks.setVisibility(android.view.View.GONE); // Ẩn ds
        } else {
            tvEmptyState.setVisibility(android.view.View.GONE);
            rcvTasks.setVisibility(android.view.View.VISIBLE); // Hiện ds
        }

        //Nút "tất cả"
        tvFilterAll.setOnClickListener(v -> {
            taskAdapter.setFilteredList(taskList);
            setButtonSelected(tvFilterAll);//Sáng nút tất cả
        });

        //Nút "cần làm"
        tvFilterTodo.setOnClickListener(v -> {
            ArrayList<Task> filterList = new ArrayList<>();
            for (Task task : taskList) {
                if (task.getStatus().equals("Cần làm")) {
                    filterList.add(task);
                }
            }
            taskAdapter.setFilteredList(filterList);
            setButtonSelected(tvFilterTodo);//Sáng nút cần làm
        });

        //Nút "đang làm"
        tvFilterInProgress.setOnClickListener(v -> {
            ArrayList<Task> filterList = new ArrayList<>();
            for (Task task : taskList) {
                if (task.getStatus().equals("Đang làm")) {
                    filterList.add(task);
                }
            }
            taskAdapter.setFilteredList(filterList);
            setButtonSelected(tvFilterInProgress);//Sáng nút đang làm
        });

        //Nút "đã xong"
        tvFilterCompleted.setOnClickListener(v -> {
            ArrayList<Task> filterList = new ArrayList<>();
            for (Task task : taskList) {
                if (task.getStatus().equals("Đã xong")) {
                    filterList.add(task);
                }
            }
            taskAdapter.setFilteredList(filterList);
            setButtonSelected(tvFilterCompleted);// Sáng nút đã xong
        });

        //Khung tìm kiếm
        edtSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                //Chuyển chữ thành chữ thường dễ so sánh
                String keyword = s.toString().toLowerCase().trim();

                //Tạo ds chứa cv tìm được
                ArrayList<Task> searchList = new ArrayList<>();

                //Chạy vòng lặp check ds cv
                for (Task task : taskList) {
                    //Nếu tiêu đề công việc đúng như tìm kiếm thì đưa vào danh sách
                    if (task.getTitle().toLowerCase().contains(keyword)) {
                        searchList.add(task);
                    }
                }

                //Update lại list để hiện ds tìm kiếm
                taskAdapter.setFilteredList(searchList);
            }
        });
    }

    //Đổi màu khi nhấn nút
    private void setButtonSelected(TextView selectedBtn) {
        //Mặc định nút chưa tô màu
        tvFilterAll.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_light));
        tvFilterAll.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.purple_primary));

        tvFilterTodo.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_light));
        tvFilterTodo.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.purple_primary));

        tvFilterInProgress.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_light));
        tvFilterInProgress.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.purple_primary));

        tvFilterCompleted.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_light));
        tvFilterCompleted.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.purple_primary));

        //Set màu khi bấm nút
        selectedBtn.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(this, R.color.purple_primary));
        selectedBtn.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white));
    }
}