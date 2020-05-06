package com.example.javabank;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Random;

// This class inflates two-factor authentication dialog and handles two-factor authentication check
public class MyDialog extends AppCompatDialogFragment {
    private EditText edit;
    private TextView text;
    private MyDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        edit = view.findViewById(R.id.keylist_insert);
        text = view.findViewById(R.id.keylist_show);

        // Set random number
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        String s = String.format("%06d", number);
        text.setText(s);

        builder.setView(view)
                .setTitle(">>>>Two-factor authentication<<<<")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.loadMainActivity(false);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(edit.getText().toString().equals(text.getText().toString())) {
                            listener.loadMainActivity(true);
                        } else {
                            listener.loadMainActivity(false);
                        }
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (MyDialogListener) context;
    }

    // Interface for returning boolean to Login-activity
    public interface MyDialogListener{
        void loadMainActivity(boolean b);
    }
}
