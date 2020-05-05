package com.example.javabank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public void register(View v) {
        EditText user = findViewById(R.id.RegisterUserName);
        EditText pass = findViewById(R.id.RegisterPassword);
        EditText pass2 = findViewById(R.id.RegisterPasswordAgain);
        String strRegEx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_])(?=\\S+$).{8,}$";

        if (pass.getText().toString().equals(pass2.getText().toString())) {
            if (pass.getText().toString().matches(strRegEx)) {
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
            } else {
                Toast.makeText(this, "Password isn't strong enough!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadLogin(View v) {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void cancelRegister(View v) {
        EditText user = findViewById(R.id.RegisterUserName);
        EditText pass = findViewById(R.id.RegisterPassword);
        EditText pass2 = findViewById(R.id.RegisterPasswordAgain);
        user.setText("");
        pass.setText("");
        pass2.setText("");
    }
}
