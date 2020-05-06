package com.example.javabank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// This class handles inflating RecyclerView for showing cards
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private ArrayList<Card> list;

    public CardAdapter(ArrayList<Card> l) {
        this.list = l;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardlist, parent, false);
        return new CardAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.cardId.setText(list.get(position).getCardId());
        holder.accId.setText(list.get(position).getAccId());
        holder.dailyLimit.setText(list.get(position).getDailyLimit());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardId,  accId, dailyLimit;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardId = itemView.findViewById(R.id.CardIdView);
            accId = itemView.findViewById(R.id.linkedAccNrView);
            dailyLimit = itemView.findViewById(R.id.cardLimitView);
        }
    }
}
