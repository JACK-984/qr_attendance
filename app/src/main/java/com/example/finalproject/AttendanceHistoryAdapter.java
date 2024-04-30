package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder> {

    private List<AttendanceHistory> attendanceList;
    private Context context;

    public AttendanceHistoryAdapter(List<AttendanceHistory> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceHistory item = attendanceList.get(position);
        holder.textCourseId.setText("Course ID: " + item.getCourseId());
        holder.textCourseName.setText("Course name: " + item.getCourseName());
        holder.textSessionDateTime.setText("Session Date & Time: " + formatDate(item.getSessionDateTime()));
        holder.textStatus.setText("Status: " + item.getStatus());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCourseId, textCourseName;
        TextView textSessionDateTime;
        TextView textStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourseId = itemView.findViewById(R.id.text_course_id);
            textCourseName = itemView.findViewById(R.id.text_course_name);
            textSessionDateTime = itemView.findViewById(R.id.text_session_datetime);
            textStatus = itemView.findViewById(R.id.text_status);
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }
}
