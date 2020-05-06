package com.example.javabank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

// This class handles inflating RecycleView for showing Account information
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {
    private ArrayList<Account> list;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    RecycleAdapter(Context ct, ArrayList<Account> l) {
        this.list = l;
        context = ct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.accountlist, parent, false);
        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.accNrView.setText(list.get(position).getAccountNr());
        holder.accTypeView.setText(list.get(position).getClass().getSimpleName().split("A")[0]);
        // Formatting Account balance into regular way of expressing it
        BigDecimal balance = BigDecimal.valueOf(list.get(position).getBalance(), 2);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        holder.accBalanceView.setText(formatter.format(balance));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView accNrView, accBalanceView, accTypeView;
        ImageView viewAccount;

        MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            // Link views
            super(itemView);
            accNrView = itemView.findViewById(R.id.accNrView);
            accBalanceView = itemView.findViewById(R.id.accBalanceView);
            accTypeView = itemView.findViewById(R.id.accTypeView);
            viewAccount = itemView.findViewById(R.id.viewAccount);

            viewAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }
}