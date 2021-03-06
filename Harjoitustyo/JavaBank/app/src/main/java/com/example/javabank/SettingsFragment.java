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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingsFragment extends Fragment {
    // Initialize BankManager
    private BankManager bm = BankManager.getInstance();

    // Initialize DB
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Link TextViews
        final TextView showAcc = view.findViewById(R.id.show_accName);
        final TextView showName = view.findViewById(R.id.show_fullName);
        final TextView showAddress = view.findViewById(R.id.show_address);
        final TextView showPhone = view.findViewById(R.id.show_phone);

        // Link EditTexts
        final EditText updateName = view.findViewById(R.id.settingsFullName);
        final EditText updateAddress = view.findViewById(R.id.settingsAddress);
        final EditText updatePhone = view.findViewById(R.id.settingsPhoneNumber);
        final EditText currPassword = view.findViewById(R.id.settingsCurrentPassword);
        final EditText newPassword = view.findViewById(R.id.settingsNewPassword);
        final EditText newPassword2 = view.findViewById(R.id.settingsNewPasswordAgain);

        // Link buttons
        Button btnAcceptSettings = view.findViewById(R.id.accept_settings_button);
        Button btnDeclineSettings = view.findViewById(R.id.decline_settings_button);
        Button btnAcceptPassword = view.findViewById(R.id.accept_password_button);
        Button btnDeclinePassword = view.findViewById(R.id.decline_password_button);

        showAcc.setText(bm.getUserRef());
        // Set database realtime listener for showing user data
        db.collection("users").document(bm.getUserRef()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("Realtime update failed!");
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (documentSnapshot.contains("name")) {
                        showName.setText(documentSnapshot.get("name").toString());
                    }
                    if (documentSnapshot.contains("address")) {
                        showAddress.setText(documentSnapshot.get("address").toString());
                    }
                    if (documentSnapshot.contains("phone")) {
                        showPhone.setText(documentSnapshot.get("phone").toString());
                    }
                } else {
                    System.out.println("Current data: null");
                    showName.setText("");
                    showAddress.setText("");
                    showPhone.setText("");
                }

            }
        });

        // Set onClick listeners to buttons
        // Decline clears input fields
        btnDeclineSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName.setText("");
                updateAddress.setText("");
                updatePhone.setText("");
            }
        });

        // Decline clears input fields
        btnDeclinePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPassword.setText("");
                newPassword.setText("");
                newPassword2.setText("");
            }
        });

        // Calls for BankManager method for updating settings
        btnAcceptSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = updateName.getText().toString();
                String address = updateAddress.getText().toString();
                String phone = updatePhone.getText().toString();
                bm.updateInformation(name, address, phone, getContext());
                updateName.setText("");
                updateAddress.setText("");
                updatePhone.setText("");
            }
        });

        // Tests does new password match, is it strong enough & validates old password. If all checks are clear, creates new hashed password & new salt into database
        btnAcceptPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currPw = currPassword.getText().toString();
                final String newPw = newPassword.getText().toString();
                String newPw2 = newPassword2.getText().toString();
                String strRegEx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_])(?=\\S+$).{8,}$";
                if (newPw.equals(newPw2)) {
                    if(newPw.matches(strRegEx)) {
                        bm.updatePassword(currPw, newPw, getContext());
                    } else {
                        Toast.makeText(getContext(), "New password is not strong enough", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "New passwords dont match!", Toast.LENGTH_SHORT).show();

                }
                currPassword.setText("");
                newPassword.setText("");
                newPassword2.setText("");
            }
        });
    }
}
