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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity implements MyDialog.MyDialogListener {
    // Initialize DB connection
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loadRegister(View v) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }

    public void login(View v) {
        final EditText user = findViewById(R.id.LoginUserName);
        final EditText pass = findViewById(R.id.LoginPassword);

        db.collection("users").document(user.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String salt = task.getResult().getString("salt");
                        String securePassword = task.getResult().getString("password");
                        boolean passwordMatch = PasswordUtils.verifyUserPassword(pass.getText().toString(), securePassword, salt);
                        if(passwordMatch) {
                            MyDialog myDialog = new MyDialog();
                            myDialog.show(getSupportFragmentManager(), "Keylist dialog");
                        } else {
                            System.out.println("Invalid password");
                        }
                    } else {
                        System.out.println("No such username");
                    }
                } else {
                    System.out.println("ERROR: " + task.getException());
                }
            }
        });
    }

    public void cancelLogin(View v) {
        EditText user = findViewById(R.id.LoginUserName);
        EditText pass = findViewById(R.id.LoginPassword);
        user.setText("");
        pass.setText("");
    }

    @Override
    public void loadMainActivity(boolean b) {
        EditText user = findViewById(R.id.LoginUserName);
        EditText pass = findViewById(R.id.LoginPassword);
        if (b) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("userRef", user.getText().toString());
            Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } else {
            user.setText("");
            pass.setText("");
        }
    }
}