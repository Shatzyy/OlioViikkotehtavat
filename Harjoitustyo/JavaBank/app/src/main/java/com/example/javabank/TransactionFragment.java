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

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class TransactionFragment extends Fragment {
    // Initialize DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Initialize BankManager
    BankManager bm = BankManager.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Link EditTexts
        final EditText depositAmount = view.findViewById(R.id.depositAmount);
        final EditText withdrawAmount = view.findViewById(R.id.withdrawAmount);
        final EditText transferAmount = view.findViewById(R.id.transferAmount);
        final EditText transferAccountExternal = view.findViewById(R.id.transferOutsideAccount);

        // Link Spinners
        final Spinner selectDepositAccount = view.findViewById(R.id.selectDepositAccount);
        final Spinner selectWithdrawAccount = view.findViewById(R.id.selectWithdrawAccount);
        final Spinner selectTransferAccountFrom = view.findViewById(R.id.selectTransferAccountFrom);
        final Spinner selectTransferAccountTo = view.findViewById(R.id.selectTransferAccountTo);

        // Link buttons
        Button btnAcceptDeposit = view.findViewById(R.id.accept_deposit_button);
        Button btnDeclineDeposit = view.findViewById(R.id.decline_deposit_button);
        Button btnAcceptWithdraw = view.findViewById(R.id.accept_withdraw_button);
        Button btnDeclineWithdraw = view.findViewById(R.id.decline_withdraw_button);
        Button btnAcceptTransfer = view.findViewById(R.id.accept_transfer_button);
        Button btnDeclineTransfer = view.findViewById(R.id.decline_transfer_button);

        // Other
        final CheckBox checkTransferExternal = view.findViewById(R.id.selectOutsideTransfer);

        // Set checkbox listener
        checkTransferExternal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkTransferExternal.isChecked()) {
                    selectTransferAccountTo.setVisibility(View.INVISIBLE);
                    transferAccountExternal.setVisibility(View.VISIBLE);
                    transferAccountExternal.setText("");
                } else {
                    selectTransferAccountTo.setVisibility(View.VISIBLE);
                    transferAccountExternal.setVisibility(View.INVISIBLE);
                    transferAccountExternal.setText("");
                }
            }
        });

        // Set database realtime listener for inflating spinnerList
        final ArrayList<String> spinnerList = new ArrayList<>();
        db.collection("users").document(bm.getUserRef()).collection("accounts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("Realtime update failed!");
                    return;
                }
                if (value != null) {
                    spinnerList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        spinnerList.add(doc.getId());
                    }
                    try {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, spinnerList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectDepositAccount.setAdapter(adapter);
                        selectWithdrawAccount.setAdapter(adapter);
                        selectTransferAccountFrom.setAdapter(adapter);
                        selectTransferAccountTo.setAdapter(adapter);
                    } catch (Exception ignored) {
                    }
                } else {
                    System.out.println("Current data: null");
                    spinnerList.clear();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, spinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    selectDepositAccount.setAdapter(adapter);
                    selectWithdrawAccount.setAdapter(adapter);
                    selectTransferAccountFrom.setAdapter(adapter);
                    selectTransferAccountTo.setAdapter(adapter);
                }
            }
        });

        // Set onClick listeners to buttons
        btnAcceptDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acc = selectDepositAccount.getSelectedItem().toString();
                String am = depositAmount.getText().toString();
                try {
                    long amount;
                    if (am.contains(".") || am.contains(",")) {
                        amount = Long.parseLong(am.replace(",", "").replace(".", ""));
                    } else {
                        amount = Long.parseLong(am)*100;
                    }
                    bm.depositMoney(acc, amount, getContext());
                    depositAmount.setText("");
                    selectDepositAccount.setSelection(0);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Deposit failed! Check your inputs.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDeclineDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                depositAmount.setText("");
                selectDepositAccount.setSelection(0);
            }
        });

        btnAcceptWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acc = selectWithdrawAccount.getSelectedItem().toString();
                String am = withdrawAmount.getText().toString();
                try {
                    long amount;
                    if (am.contains(".") || am.contains(",")) {
                        amount = Long.parseLong(am.replace(",", "").replace(".", ""));
                    } else {
                        amount = Long.parseLong(am)*100;
                    }
                    bm.withdrawMoney(acc, amount, getContext());
                    withdrawAmount.setText("");
                    selectWithdrawAccount.setSelection(0);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Deposit failed! Check your inputs.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDeclineWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawAmount.setText("");
                selectWithdrawAccount.setSelection(0);
            }
        });

        btnAcceptTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkTransferExternal.isChecked()) {
                    if (selectTransferAccountFrom.getSelectedItem().toString().equals(transferAccountExternal.getText().toString())) {
                        Toast.makeText(getContext(), "Cant transfer to same account!", Toast.LENGTH_SHORT).show();
                    } else {
                        String transferFrom = selectTransferAccountFrom.getSelectedItem().toString();
                        String transferTo = transferAccountExternal.getText().toString();
                        String am = transferAmount.getText().toString();
                        try {
                            long amount;
                            if (am.contains(".") || am.contains(",")) {
                                amount = Long.parseLong(am.replace(",", "").replace(".", ""));
                            } else {
                                amount = Long.parseLong(am)*100;
                            }
                            bm.externalTransfer(transferFrom, transferTo, amount, getContext());
                            selectTransferAccountFrom.setSelection(0);
                            selectTransferAccountTo.setSelection(0);
                            transferAmount.setText("");
                            checkTransferExternal.setChecked(false);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Transfer failed! Check your inputs.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (selectTransferAccountFrom.getSelectedItem().toString().equals(selectTransferAccountTo.getSelectedItem().toString())) {
                        Toast.makeText(getContext(), "Cant transfer to same account!", Toast.LENGTH_SHORT).show();
                    } else {
                        String transferFrom = selectTransferAccountFrom.getSelectedItem().toString();
                        String transferTo = selectTransferAccountTo.getSelectedItem().toString();
                        String am = transferAmount.getText().toString();
                        try {
                            long amount;
                            if (am.contains(".") || am.contains(",")) {
                                amount = Long.parseLong(am.replace(",", "").replace(".", ""));
                            } else {
                                amount = Long.parseLong(am)*100;
                            }
                            bm.transferMoney(transferFrom, transferTo, amount, getContext());
                            selectTransferAccountFrom.setSelection(0);
                            selectTransferAccountTo.setSelection(0);
                            transferAmount.setText("");
                            checkTransferExternal.setChecked(false);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Transfer failed! Check your inputs.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        btnDeclineTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferAmount.setText("");
                transferAccountExternal.setText("");
                selectTransferAccountFrom.setSelection(0);
                selectTransferAccountTo.setSelection(0);
                checkTransferExternal.setChecked(false);
            }
        });

    }
}
