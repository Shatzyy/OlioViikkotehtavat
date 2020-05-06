package com.example.javabank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    // Initialize DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    // Method for checking register forms validity & creating account
    public void register(View v) {
        final EditText user = findViewById(R.id.RegisterUserName);
        final EditText pass = findViewById(R.id.RegisterPassword);
        EditText pass2 = findViewById(R.id.RegisterPasswordAgain);
        String strRegEx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_])(?=\\S+$).{8,}$";

        // Test are passwords matching, strong enough & does the account already exist in the database
        if (pass.getText().toString().equals(pass2.getText().toString())) {
            if (pass.getText().toString().matches(strRegEx)) {
                db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {
                        boolean bol = true;
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.exists()) {
                                if (doc.getId().equals(user.getText().toString())) {
                                    Toast.makeText(Register.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                                    bol = false;
                                }
                            }
                        }
                        if (bol) {
                            Map<String, Object> tmp = new HashMap<>();
                            String salt = PasswordUtils.getSalt(30);
                            String hashedPassword = PasswordUtils.generateSecurePassword(pass.getText().toString(), salt);
                            tmp.put("salt", salt);
                            tmp.put("password", hashedPassword);
                            db.collection("users").document(user.getText().toString()).set(tmp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Register.this, "Account created! Please login.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Password isn't strong enough!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
        }
    }

    // Method for switching view to login
    public void loadLogin(View v) {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }

    // Method for cancelling registering, clearing input fields
    public void cancelRegister(View v) {
        EditText user = findViewById(R.id.RegisterUserName);
        EditText pass = findViewById(R.id.RegisterPassword);
        EditText pass2 = findViewById(R.id.RegisterPasswordAgain);
        user.setText("");
        pass.setText("");
        pass2.setText("");
    }
}
