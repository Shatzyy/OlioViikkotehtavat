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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class AccountFragment extends Fragment {
    private ArrayList<Account> accountList = new ArrayList<>();
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
        final EditText deleteAcc_accNr = view.findViewById(R.id.deleteAccNr);

        // Other
        final CheckBox checkCredit = view.findViewById(R.id.creditCheck);
        final Spinner spin = view.findViewById(R.id.updateAccBankCard);
        RecyclerView showAccounts = view.findViewById(R.id.showAccounts);

        // Setting Spinner for BankCard connection
        ArrayList<String> spinnerList = bm.getBankCardNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

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

        // Set adapter for RecycleView //TODO Set Listener when database changed and update view after changing database
        //ArrayList<Account> accountList = bm.getAccounts();
        RecycleAdapter recycleAdapter = new RecycleAdapter(getContext(), accountList);
        showAccounts.setAdapter(recycleAdapter);
        showAccounts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set onClick listeners to buttons
        // Clicking creating account button checks are required fields filled and calls BankManager method to create account. Shows Toast based on result
        button_createAcc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createAcc_accNr.getText().toString().length()!=0) {
                    if (checkCredit.isChecked()) {
                        if(bm.createCreditAccount(createAcc_accNr.getText().toString(), createAcc_balance.getText().toString(), createAcc_creditLimit.getText().toString())) {
                            Toast.makeText(getContext(), "Account created!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Creating account failed! Please check your inputs.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if(bm.createDebitAccount(createAcc_accNr.getText().toString(), createAcc_balance.getText().toString())) {
                            Toast.makeText(getContext(), "Account created!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Creating account failed! Please check your inputs.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Please insert account number.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Clicking cancel button clears input fields
        button_createAcc_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcc_accNr.setText(null);
                createAcc_balance.setText(null);
                createAcc_creditLimit.setText(null);
                checkCredit.setChecked(false);
            }
        });

        // Clicking updating account button checks are required fields filled and calls BankManager method to update account. Shows Toast based on result
        button_updateAcc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateAcc_accNr.getText().toString().length()!=0) {
                    if(bm.updateAccount(updateAcc_accNr.getText().toString(), updateAcc_creditLimit.getText().toString(), spin.getSelectedItem().toString())) {
                        Toast.makeText(getContext(), "Account updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Updating account failed! Please check your inputs.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please insert account number.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Clicking cancel button clears input fields
        button_updateAcc_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAcc_accNr.setText(null);
                updateAcc_creditLimit.setText(null);
                spin.setSelection(0);
            }
        });

        // Clicking delete account button checks is there account number filled and calls BankManager method to delete account. Shows Toast based on result
        button_deleteAcc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteAcc_accNr.getText().toString().length()!=0) {
                    bm.deleteAccount(deleteAcc_accNr.getText().toString());
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
                    createAcc_creditLimit.setVisibility(View.VISIBLE);
                } else {
                    createAcc_creditLimit.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
