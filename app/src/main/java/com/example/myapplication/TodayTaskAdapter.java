package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TodayTaskAdapter extends RecyclerView.Adapter<TodayTaskAdapter.TodayTaskViewHolder> {
    private List<Task> taskList;

    public TodayTaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setFilteredList(List<Task> filteredList) {
        this.taskList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodayTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_task, parent, false);
        return new TodayTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayTaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        if (task == null) return;

        holder.tvCategory.setText(task.getCategory());
        holder.tvTitle.setText(task.getTitle());

        // ĐỔI GIỜ: Dịch số int (10, 12, 19) sang chuỗi AM/PM hiển thị lên đồng hồ
        int hour = task.getProgress();
        String timeStr;
        if (hour >= 12) {
            int displayHour = (hour == 12) ? 12 : (hour - 12);
            timeStr = String.format("%02d:00 PM", displayHour);
        } else {
            timeStr = String.format("%02d:00 AM", hour);
        }
        holder.tvTime.setText(timeStr);

        String status = task.getStatus();
        holder.tvStatusBadge.setText(status);

        // ========================================================================
        // >>> CHỖ NÀY SỬA: Thay thế toàn bộ icon lỗi hệ thống bằng 4 file XML custom có sẵn của ông
        // ========================================================================
        String category = task.getCategory(); // Lấy trực tiếp tên danh mục gốc
        holder.tvStatusBadge.setText(task.getStatus());

        if ("Công việc".equals(category)) {
            holder.imgCategoryIcon.setImageResource(R.drawable.outline_work_24);
            holder.imgCategoryIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFDEEF")));
            holder.imgCategoryIcon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF4081")));

        } else if ("Cá nhân".equals(category)) {
            holder.imgCategoryIcon.setImageResource(R.drawable.outline_person_24);
            holder.imgCategoryIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E0F2FE")));
            holder.imgCategoryIcon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0284C7")));

        } else if ("Sức khỏe".equals(category)) {
            holder.imgCategoryIcon.setImageResource(R.drawable.outline_fitness_trackers_24);
            holder.imgCategoryIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D1F2D9")));
            holder.imgCategoryIcon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#16A34A")));

        } else if ("Mua sắm".equals(category)) {
            holder.imgCategoryIcon.setImageResource(R.drawable.outline_store_24);
            holder.imgCategoryIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FEF3C7")));
            holder.imgCategoryIcon.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D97706")));
        }

        // 2. Khớp điều kiện đổ màu nền pastel cho Badge trạng thái phía dưới
        if ("Đã xong".equals(status)) {
            holder.tvStatusBadge.setText("Done");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#7B1FA2"));
            holder.tvStatusBadge.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.purple_light));
        } else if ("Đang làm".equals(status)) {
            holder.tvStatusBadge.setText("In Progress");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#FF6D00"));
            holder.tvStatusBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFE0B2")));
        } else { // Cần làm
            holder.tvStatusBadge.setText("To-do");
            holder.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#00B0FF"));
            holder.tvStatusBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E1F5FE")));
        }

        // Sự kiện nhấn vào item để chuyển sang màn hình chi tiết
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), DetailTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            intent.putExtra("task_title", task.getTitle());
            intent.putExtra("task_category", task.getCategory());
            v.getContext().startActivity(intent);
        });
        // ========================================================================
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public static class TodayTaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvTime, tvStatusBadge;
        ImageView imgCategoryIcon;

        public TodayTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            imgCategoryIcon = itemView.findViewById(R.id.imgCategoryIcon);
        }
    }
}