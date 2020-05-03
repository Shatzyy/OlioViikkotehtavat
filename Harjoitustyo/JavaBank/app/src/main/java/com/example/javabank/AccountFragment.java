package com.example.javabank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AccountFragment extends Fragment {
    // Initialize DB
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Initialize BankManager
    private BankManager bm = BankManager.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Buttons
        Button button_updateAcc_accept = view.findViewById(R.id.updateAcc_btn_accept);
        Button button_updateAcc_decline = view.findViewById(R.id.updateAcc_btn_decline);
        Button button_deleteAcc_accept = view.findViewById(R.id.deleteAcc_btn_accept);

        // EditText fields
        final EditText updateAcc_accNr = view.findViewById(R.id.updateAccNr);
        final EditText updateAcc_creditLimit = view.findViewById(R.id.updateCreditLimit);
        final EditText deleteAcc_accNr = view.findViewById(R.id.deleteAccNr);

        // Other
        final CheckBox checkCredit = view.findViewById(R.id.creditCheck);
        final Spinner spin = view.findViewById(R.id.updateAccBankCard);
        final RecyclerView showAccounts = view.findViewById(R.id.showAccounts);

        // Set realtime listener for database for showing cards in spinner
        db.collection("users").document(bm.getUserRef()).collection("cards").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                final ArrayList<String> cardList = new ArrayList<>();
                // Create cardList from database query
                for (QueryDocumentSnapshot doc : value) {
                    String card = doc.getId();
                    cardList.add(card);
                }
                // Set adapter for Spinner for showing accounts
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cardList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin.setAdapter(adapter);
            }
        });

        // Set realtime listener for database
        db.collection("users").document(bm.getUserRef()).collection("accounts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                final ArrayList<Account> accountList = new ArrayList<>();
                // Create accountList from database query
                for (QueryDocumentSnapshot doc : value) {
                    String accNr = doc.getId();
                    Long balance = doc.getLong("balance");
                    if (doc.contains("creditLimit")) {
                        long cLim = doc.getLong("creditLimit");
                        Account tmp = new CreditAccount(accNr, balance, bm.getUserRef(), cLim);
                        accountList.add(tmp);
                    } else {
                        Account tmp = new DebitAccount(accNr, balance, bm.getUserRef());
                        accountList.add(tmp);
                    }
                }
                // Set adapter for RecycleView for showing accounts
                RecycleAdapter recycleAdapter = new RecycleAdapter(getContext(), accountList);
                showAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
                showAccounts.setAdapter(recycleAdapter);
                recycleAdapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // Initialize fragmentManager & transaction
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        // Create new fragment
                        SingleAccountFragment fragment = new SingleAccountFragment();
                        // Create bundle for storing data
                        Bundle bundle = new Bundle();
                        String accNr = accountList.get(position).getAccountNr();
                        bundle.putString("accountNr", accNr);
                        // Set arguments to fragment
                        fragment.setArguments(bundle);
                        // Load new fragment
                        fragmentTransaction.replace(R.id.container, fragment).commit();
                    }
                });
            }
        });

        // Set onClick listeners to buttons
        // Clicking updating account button checks are required fields filled and calls BankManager method to update or create account
        button_updateAcc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accNr = updateAcc_accNr.getText().toString();
                String credLim = updateAcc_creditLimit.getText().toString();
                String cardLink = spin.getSelectedItem().toString();
                if (updateAcc_accNr.getText().toString().length()!=0) {
                    bm.createUpdateAccount(accNr, credLim, cardLink, getContext());
                    updateAcc_accNr.setText("");
                    updateAcc_creditLimit.setText("");
                    spin.setSelection(0);
                } else {
                    Toast.makeText(getContext(), "Please insert account number.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Clicking cancel button clears input fields
        button_updateAcc_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAcc_accNr.setText("");
                updateAcc_creditLimit.setText("");
                checkCredit.setChecked(false);
                spin.setSelection(0);
            }
        });

        // Clicking delete account button checks is there account number filled and calls BankManager method to delete account. Shows Toast based on result
        button_deleteAcc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteAcc_accNr.getText().toString().length()!=0) {
                    bm.deleteAccount(deleteAcc_accNr.getText().toString(), getContext());
                    deleteAcc_accNr.setText("");
                } else {
                    Toast.makeText(getContext(), "Please insert account number you'd like to delete.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Set OnCheckedChange listener to checkbox. Sets CreditLimit field visibility
        checkCredit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    updateAcc_creditLimit.setVisibility(View.VISIBLE);
                } else {
                    updateAcc_creditLimit.setVisibility(View.INVISIBLE);
                    updateAcc_creditLimit.setText("");
                }
            }
        });
    }
}
