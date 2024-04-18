package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<String> courses;
    private OnCourseClickListener onCourseClickListener; // Click listener interface

    public CourseAdapter(List<String> courses, OnCourseClickListener onCourseClickListener) {
        this.courses = courses;
        this.onCourseClickListener = onCourseClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        String course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public interface OnCourseClickListener {
        void onCourseClick(String courseName); // Method to handle course click events
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewCourseName;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCourseName = itemView.findViewById(R.id.textViewCourseName);
            itemView.setOnClickListener(this); // Set click listener for the item
        }

        @Override
        public void onClick(View v) {
            // Call the onCourseClick method of the listener interface when the item is clicked
            onCourseClickListener.onCourseClick(courses.get(getAdapterPosition()));
        }

        public void bind(String courseName) {
            textViewCourseName.setText(courseName);
        }
    }
}


