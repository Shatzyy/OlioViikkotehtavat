package com.example.javabank;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AccountFragment extends Fragment {
    private ArrayList<Account> accountList = new ArrayList<>();
    private ArrayList<String> spinnerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        //Initialize database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Link context & views
        Context context = view.getContext();
        RecyclerView showAccounts = view.findViewById(R.id.showAccounts);
        super.onViewCreated(view, savedInstanceState);

        // Buttons
        Button button_createAcc_accept = view.findViewById(R.id.createAcc_btn_accept);
        Button button_createAcc_decline = view.findViewById(R.id.createAcc_btn_decline);
        Button button_updateAcc_accept = view.findViewById(R.id.updateAcc_btn_accept);
        Button button_updateAcc_decline = view.findViewById(R.id.updateAcc_btn_decline);
        Button button_deleteAcc_accept = view.findViewById(R.id.deleteAcc_btn_accept);

        // EditText fields
        final EditText createAcc_accNr = view.findViewById(R.id.createAccNr);
        final EditText createAcc_balance = view.findViewById(R.id.createBalance);
        final EditText createAcc_creditLimit = view.findViewById(R.id.createCreditLimit);
        final EditText updateAcc_accNr = view.findViewById(R.id.updateAccNr);
        final EditText updateAcc_creditLimit = view.findViewById(R.id.updateCreditLimit);
        EditText deleteAcc_accNr = view.findViewById(R.id.deleteAccNr);

        // Other
        final CheckBox checkCredit = view.findViewById(R.id.creditCheck);

        // Setting Spinner for BankCard connection
        // TODO get bank cards from database and add them to spinnerlist
        // TODO Delete dummy data
        spinnerList.add("");
        spinnerList.add("Card 1");
        spinnerList.add("Card 2");
        spinnerList.add("Card 3");
        final Spinner spin = view.findViewById(R.id.updateAccBankCard);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        // Gather data in list //TODO Fill AccountList from Database
        /*for (int i=0; i<5; i++) {
            if (true) {
                Account tmp = new DebitAccount();
            } else {
                Account tmp = new CreditAccount();
            }
        } */

        // TODO delete dummy data
        Account tmp1 = new DebitAccount("FI23 1400 1225 3244 29", 1000000, "13421");
        Account tmp2 = new DebitAccount("FI78 1400 1225 9981 11", 552312, "13421");
        Account tmp3 = new DebitAccount("FI11 1400 1225 1009 75", 39123, "13421");
        Account tmp4 = new DebitAccount("FI65 1400 1225 3997 66", 0, "13421");
        Account tmp5 = new DebitAccount("FI82 1400 1225 2552 49", 3502000, "13421");
        Account tmp6 = new DebitAccount("FI99 1400 1225 4234 52", 12344, "13421");
        accountList.add(tmp1);
        accountList.add(tmp2);
        accountList.add(tmp3);
        accountList.add(tmp4);
        accountList.add(tmp5);
        accountList.add(tmp6);

        // Set adapter for RecycleView
        RecycleAdapter recycleAdapter = new RecycleAdapter(context, accountList);
        showAccounts.setAdapter(recycleAdapter);
        showAccounts.setLayoutManager(new LinearLayoutManager(context));

        // Set onClick listeners to buttons
        button_createAcc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO create new acc to database
            }
        });

        button_createAcc_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcc_accNr.setText(null);
                createAcc_balance.setText(null);
                createAcc_creditLimit.setText(null);
                checkCredit.setChecked(false);
            }
        });

        button_updateAcc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO update acc in database
            }
        });

        button_updateAcc_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAcc_accNr.setText(null);
                updateAcc_creditLimit.setText(null);
                spin.setSelection(0);
            }
        });

        button_deleteAcc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO delete Acc from database
            }
        });

        // Set OnCheckedChange listener to checkbox
        checkCredit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    createAcc_creditLimit.setVisibility(View.VISIBLE);
                } else {
                    createAcc_creditLimit.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
