package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    // ham de update list khi tim kiem hoac loc
    public void setFilteredList(List<Task> filteredList) {
        this.taskList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // goi layout item_in_progress
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_progress, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        if (task == null) return;

        // dua data vao
        holder.tvCategory.setText(task.getCategory());
        holder.tvTitle.setText(task.getTitle());
        holder.progressBar.setProgress(task.getProgress());
    }

    @Override
    public int getItemCount() {
        if (taskList != null) return taskList.size();
        return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle;
        ProgressBar progressBar;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}