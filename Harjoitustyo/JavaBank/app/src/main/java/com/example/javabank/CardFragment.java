package com.example.javabank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CardFragment extends Fragment {
    // Initialize DB
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Initialize BankManager
    private BankManager bm = BankManager.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Link Buttons
        Button updateCardBtn_accept = view.findViewById(R.id.updateCard_btn_accept);
        Button updateCardBtn_decline = view.findViewById(R.id.updateCard_btn_decline);
        Button deleteCardBtn = view.findViewById(R.id.deleteCard_btn_accept);

        // Link EditTexts
        final EditText updateCardNr = view.findViewById(R.id.updateCardNr);
        final EditText updateCardLimit = view.findViewById(R.id.updateCardLimit);
        final EditText deleteCardNr = view.findViewById(R.id.deleteCardNr);

        // Other
        final Spinner updateAccLink = view.findViewById(R.id.updateAccBankCard);

        // Set onClick listeners to buttons
        updateCardBtn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNr = updateCardNr.getText().toString();
                String cardLimit = updateCardLimit.getText().toString();
                String accLink = updateAccLink.getSelectedItem().toString();
                if (cardNr.length()==0 || accLink.length()==0) {
                    Toast.makeText(getContext(), "Please insert all required data.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        long limit;
                        if (cardLimit.contains(".") || cardLimit.contains(",")) {
                            limit = Long.parseLong(cardLimit.replace(",", "").replace(".", ""));
                        } else {
                            limit = Long.parseLong(cardLimit)*100;
                        }
                        bm.createUpdateBankCard(cardNr, accLink, limit, getContext());
                        updateCardNr.setText("");
                        updateCardLimit.setText("");
                        updateAccLink.setSelection(0);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Updating card failed! Please check your inputs.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        updateCardBtn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCardNr.setText("");
                updateCardLimit.setText("");
                updateAccLink.setSelection(0);
            }
        });

        deleteCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String delCardNr = deleteCardNr.getText().toString();
                if (delCardNr.length()!=0) {
                    bm.deleteBankCard(delCardNr, getContext());
                    deleteCardNr.setText("");
                } else {
                    Toast.makeText(getContext(), "Please insert card number you'd like to delete.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set realtime listener for database for showing cards
        final RecyclerView showCards = view.findViewById(R.id.showCards);
        db.collection("users").document(bm.getUserRef()).collection("cardLinks").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                final ArrayList<Card> cardList = new ArrayList<>();
                // Create cardList from database query
                for (QueryDocumentSnapshot doc : value) {
                    String cardNr = "";
                    String accNr = "";
                    String limit = "";
                    if (doc.contains("linkedCard")) {
                        cardNr = doc.getString("linkedCard");
                    }
                    if (doc.contains("accountNr")) {
                        accNr = doc.getString("accountNr");
                    }
                    if (doc.contains("limit")) {
                        limit = doc.getString("limit");
                    }
                    Card tmp = new Card(cardNr, accNr, limit);
                    cardList.add(tmp);
                }
                // Set adapter for RecyclerView for showing cards
                CardAdapter cardAdapter = new CardAdapter(cardList);
                showCards.setLayoutManager(new LinearLayoutManager(getContext()));
                showCards.setAdapter(cardAdapter);
            }
        });

        // Set realtime listener for database for showing accounts spinner
        db.collection("users").document(bm.getUserRef()).collection("accounts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                final ArrayList<String> accList = new ArrayList<>();
                // Create cardList from database query
                for (QueryDocumentSnapshot doc : value) {
                    String acc = doc.getId();
                    accList.add(acc);
                }
                // Set adapter for Spinner for showing accounts
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                updateAccLink.setAdapter(adapter);
            }
        });
    }
}
