package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courses;
    private OnCourseClickListener onCourseClickListener; // Click listener interface

    public CourseAdapter(List<Course> courses, OnCourseClickListener onCourseClickListener) {
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
        String courseName = courses.get(position).getCourseName();
        String qrCodeURL = courses.get(position).getQrImageUrl();
        holder.bind(courseName,qrCodeURL);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public interface OnCourseClickListener {
        void onCourseClick(String courseName,String courseID); // Method to handle course click events
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewCourseName;
        ImageView courseQRImage;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCourseName = itemView.findViewById(R.id.textViewCourseName);
            courseQRImage = itemView.findViewById(R.id.QRCODE);
            itemView.setOnClickListener(this); // Set click listener for the item
        }

        @Override
        public void onClick(View v) {
            // Get the course name
            String courseName = courses.get(getAdapterPosition()).getCourseName();

            // Reference to the "courses" collection in Firestore
            FirebaseFirestore.getInstance().collection("courses")
                    .whereEqualTo("name", courseName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Get the document snapshot of the first result (assuming course names are unique)
                            QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                            // Get the document ID (courseID) of the course
                            String courseID = documentSnapshot.getId();
                            // Call the onCourseClick method of the listener interface with the courseName and courseID
                            onCourseClickListener.onCourseClick(courseName, courseID);
                        } else {
                            // Handle case where no course with the given name is found
                            Toast.makeText(itemView.getContext(), "Course not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(itemView.getContext(), "Failed to retrieve course ID", Toast.LENGTH_SHORT).show();
                    });
        }
        public void bind(String courseName,String URL) {
            textViewCourseName.setText(courseName);
            Picasso.get().load(URL).into(courseQRImage);
        }
    }
}


