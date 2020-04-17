package com.example.viikko9_3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {

    ArrayList<Movie> movieList = null;
    Context context;

    public RecycleAdapter(Context ct, ArrayList<Movie> mList) {
        this.movieList = mList;
        context = ct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // TODO Checkit hakijan parametreille search + alkuaika
        holder.titleView.setText(movieList.get(position).getTitle());
        holder.auditView.setText(movieList.get(position).getTheatreAuditorium());
        holder.timeView.setText(movieList.get(position).getShowStart().toString());
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleView, auditView, timeView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            auditView = itemView.findViewById(R.id.auditView);
            timeView = itemView.findViewById(R.id.timeView);
        }

    }
}
