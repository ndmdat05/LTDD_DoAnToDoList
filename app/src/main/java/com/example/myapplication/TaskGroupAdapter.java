package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskGroupAdapter extends RecyclerView.Adapter<TaskGroupAdapter.GroupViewHolder> {
    private List<TaskGroup> groupList;

    public TaskGroupAdapter(List<TaskGroup> groupList) {
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // goi layout item_task_group
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        TaskGroup group = groupList.get(position);
        if (group == null) return;

        // dua data vao
        holder.tvGroupName.setText(group.getGroupName());
        holder.tvTaskCount.setText(group.getTaskCount());
        holder.tvGroupPercent.setText(group.getPercent());
    }

    @Override
    public int getItemCount() {
        if (groupList != null) return groupList.size();
        return 0;
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvTaskCount, tvGroupPercent;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvTaskCount = itemView.findViewById(R.id.tvTaskCount);
            tvGroupPercent = itemView.findViewById(R.id.tvGroupPercent);
        }
    }
}