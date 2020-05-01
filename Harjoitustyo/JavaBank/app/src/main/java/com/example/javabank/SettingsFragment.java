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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsFragment extends Fragment {
    // Initialize BankManager
    private BankManager bm = BankManager.getInstance();

    // Initialize DB for password checks
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
                    showName.setText(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                    showAddress.setText(Objects.requireNonNull(documentSnapshot.get("address")).toString());
                    showPhone.setText(Objects.requireNonNull(documentSnapshot.get("phone")).toString());
                } else {
                    System.out.println("Current data: null");
                    showName.setText("");
                    showAddress.setText("");
                    showPhone.setText("");
                }

            }
        });

        // Set onClick listeners to buttons
        btnDeclineSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName.setText("");
                updateAddress.setText("");
                updatePhone.setText("");
            }
        });

        btnDeclinePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPassword.setText("");
                newPassword.setText("");
                newPassword2.setText("");
            }
        });

        btnAcceptSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = updateName.getText().toString();
                String address = updateAddress.getText().toString();
                String phone = updatePhone.getText().toString();
                bm.updateInformation(name, address, phone);
                updateName.setText("");
                updateAddress.setText("");
                updatePhone.setText("");
                Toast.makeText(getContext(), "Information updated!", Toast.LENGTH_SHORT).show();
            }
        });

        btnAcceptPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currPw = currPassword.getText().toString();
                final String newPw = newPassword.getText().toString();
                String newPw2 = newPassword2.getText().toString();
                String strRegEx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_])(?=\\S+$).{8,}$";
                if (newPw.equals(newPw2)) {
                    if(newPw.matches(strRegEx)) {
                        db.collection("users").document(bm.getUserRef()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String salt = task.getResult().getString("salt");
                                        String securePassword = task.getResult().getString("password");
                                        boolean passwordMatch = PasswordUtils.verifyUserPassword(currPw, securePassword, salt);
                                        if(passwordMatch) {
                                            Map<String, Object> tmp = new HashMap<>();
                                            String newSalt = PasswordUtils.getSalt(30);
                                            String newSecurePw = PasswordUtils.generateSecurePassword(newPw, newSalt);
                                            tmp.put("salt", newSalt);
                                            tmp.put("password", newSecurePw);
                                            db.collection("users").document(bm.getUserRef()).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                                                    currPassword.setText("");
                                                    newPassword.setText("");
                                                    newPassword2.setText("");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Updating password failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Password incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Username not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    System.out.println("ERROR: " + task.getException());
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "New password is not strong enough", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "New passwords dont match!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}
