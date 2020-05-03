package com.example.javabank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private ArrayList<Payment> list;

    public HistoryAdapter(ArrayList<Payment> l) {
        this.list = l;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.accounthistory, parent, false);
        return new HistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.date.setText(list.get(position).getDate());
        holder.accNr.setText(list.get(position).getAccTo());
        holder.amount.setText(list.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView date,  accNr, amount;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.historyDate);
            accNr = itemView.findViewById(R.id.historyAcc);
            amount = itemView.findViewById(R.id.historyAmount);
        }
    }
}
