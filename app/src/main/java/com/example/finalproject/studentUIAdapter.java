package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

// MyAdapter.java
public class studentUIAdapter extends RecyclerView.Adapter<studentUIAdapter.ViewHolder> {

    private String[] mData;
    private LayoutInflater mInflater;
    private Context mContext;

    public studentUIAdapter(Context context, String[] data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.ui_list_item_faculty, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mData[position];
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            switch (position) {
                case 0:
                    mContext.startActivity(new Intent(mContext, QRScanner.class));
                    break;
                case 1:
                    mContext.startActivity(new Intent(mContext, StudentViewCourses.class));
                    break;
                case 2:
                    FirebaseAuth.getInstance().signOut();
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    break;
                // Add cases for other items here
            }
        }
    }
}
