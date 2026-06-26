package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setFilteredList(List<Task> filteredList) {
        this.taskList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Gọi layout item task
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        if (task == null) {
            return;
        }

        //Đưa dữ liệu vào view
        holder.tvCategory.setText(task.getCategory());
        holder.tvTitle.setText(task.getTitle());
        holder.tvTime.setText(task.getTime());
        holder.tvStatus.setText(task.getStatus());

        //Xét đổi trạng thái khi bấm nút
        if (task.getStatus().equals("Đã xong")) {
            holder.tvStatus.setTextColor(Color.parseColor("#2ECC71"));
            holder.tvStatus.getBackground().setTint(Color.parseColor("#E8F8F5"));
        } else if (task.getStatus().equals("Đang làm")) {
            holder.tvStatus.setTextColor(Color.parseColor("#E67E22"));
            holder.tvStatus.getBackground().setTint(Color.parseColor("#FFF2E6"));
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#3498DB"));
            holder.tvStatus.getBackground().setTint(Color.parseColor("#EBF5FB"));
        }

        // Sự kiện click vào item để sang trang chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailTaskActivity.class);
            intent.putExtra("task_title", task.getTitle());
            intent.putExtra("task_category", task.getCategory());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (taskList != null) {
            return taskList.size();
        }
        return 0;
    }

    //Tạo class TaskViewHolder
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvTime, tvStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            // Tìm view bằng id
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
