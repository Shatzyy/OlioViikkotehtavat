package com.example.javabank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class SingleAccountFragment extends Fragment {
    // Initialize DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Initialize BankManager
    BankManager bm = BankManager.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_singleacc, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Get arguments from last fragment
        Bundle bundle = this.getArguments();
        final String accNr = bundle.getString("accountNr");

        // Link TextView & set account number in a title
        TextView title = view.findViewById(R.id.history_accNr);
        title.setText(accNr);

        // Link TextView & set realtime database listener for updating information
        final TextView balance = view.findViewById(R.id.history_balance);
        final TextView credLim = view.findViewById(R.id.history_creditLimit);
        final TextView linkCard = view.findViewById(R.id.history_bankCard);
        db.collection("users").document(bm.getUserRef()).collection("accounts").document(accNr).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("ERROR: " + e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    long bal = documentSnapshot.getLong("balance");
                    if (documentSnapshot.contains("creditLimit")) {
                        long cred = documentSnapshot.getLong("creditLimit");
                        BigDecimal credAm = BigDecimal.valueOf(cred, 2);
                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                        credLim.setText(formatter.format(credAm));
                    } else {
                        credLim.setText("");
                    }
                    BigDecimal amt = BigDecimal.valueOf(bal, 2);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    balance.setText(formatter.format(amt));
                } else {
                    System.out.println("Current data: null");
                }
            }
        });
        db.collection("users").document(bm.getUserRef()).collection("cardLinks").document(accNr).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("ERROR: " + e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String card = documentSnapshot.getString("cardId");
                    linkCard.setText(card);
                } else {
                    System.out.println("Current data: null");
                    linkCard.setText("");
                }
            }
        });

        // Link return button & set OnClickListener for going back to AccountFragment
        Button returnViewBtn = view.findViewById(R.id.returnViewBtn);
        returnViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize fragmentManager & transaction
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Load new fragment
                AccountFragment fragment = new AccountFragment();
                fragmentTransaction.replace(R.id.container, fragment).commit();
            }
        });

        // Link savefile button & field, set OnClickListener for writing data into a file
        final EditText fileName = view.findViewById(R.id.fileName);
        Button printFile = view.findViewById(R.id.printFileBtn);
        printFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = fileName.getText().toString();
                if (fName.length()==0) {
                    Toast.makeText(getContext(), "Please insert a file name.", Toast.LENGTH_SHORT).show();
                } else {
                    bm.writeCSV(fName, accNr, getContext());
                    fileName.setText("");
                }
            }
        });

        // Set realtime listener for database for showing account history
        final RecyclerView showHistory = view.findViewById(R.id.showHistory);
        db.collection("users").document(bm.getUserRef()).collection("accounts").document(accNr).collection("history").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
            final ArrayList<Payment> historyList = new ArrayList<>();
            // Create historyList from database query
            for (QueryDocumentSnapshot doc : value) {
                String accFrom = doc.getString("accFrom");
                String accTo = doc.getString("accTo");
                String amount = doc.getString("amount");
                String date = doc.getString("date");
                Payment tmp = new Payment(accFrom, accTo, date, amount);
                historyList.add(tmp);
            }
            // Set adapter for RecyclerView for showing account history
            HistoryAdapter historyAdapter = new HistoryAdapter(historyList);
            showHistory.setLayoutManager(new LinearLayoutManager(getContext()));
            showHistory.setAdapter(historyAdapter);
            }
        });

    }
}
