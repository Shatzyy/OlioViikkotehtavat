package com.example.javabank;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Class for managing all Bank activities
class BankManager {
    // Single instance of BankManager allowed
    private static final BankManager bm = new BankManager();
    // Initialize Database connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userRef = "";
    private BankManager () {
        // Empty constructor required
    }

    // Singleton for BankManager
    public static BankManager getInstance() {
        return bm;
    }

    // Sets userReference of which user is logged in
    public void setUserRef(String s) {
        this.userRef = s;
    }

    // Get userReference
    public String getUserRef() {
        return userRef;
    }

    // Methods for managing accounts
    ////////////////////////////////////////////////////////////////////////
    // Finds account and deletes it & card links assosiated with the account
    public void deleteAccount(String acc, final Context ct) {
        db.collection("users").document(userRef).collection("accounts").document(acc).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ct, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ct, "Deleting account failed!", Toast.LENGTH_SHORT).show();
            }
        });
        db.collection("users").document(userRef).collection("cardLinks").document(acc).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Card link deleted!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Deleting card link failed!");
            }
        });

    }

    // Checks does the given account exist, if it does, updates creditLimit & cardLink, if doesn't, creates the account
    public void createUpdateAccount(final String acc, String credLim, final Context ct) {
        try {
            final long cLim;
            if (credLim.length()==0) {
                cLim = 0;
            } else {
                cLim = Long.parseLong(credLim)*100;
            }
            db.collection("users").document(userRef).collection("accounts").document(acc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (cLim == 0) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("creditLimit", FieldValue.delete());
                            db.collection("users").document(userRef).collection("accounts").document(acc).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ct, "Account updated!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("creditLimit", cLim);
                            db.collection("users").document(userRef).collection("accounts").document(acc).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ct, "Account updated!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        if (cLim==0) {
                            Account tmp = new DebitAccount(acc, 0, userRef);
                            db.collection("users").document(userRef).collection("accounts").document(acc).set(tmp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ct, "Debit account created!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ct, "Creating account failed! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Account tmp = new CreditAccount(acc, 0, userRef, cLim);
                            db.collection("users").document(userRef).collection("accounts").document(acc).set(tmp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ct, "Credit account created!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ct, "Creating account failed! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ct, "Database search failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(ct, "Update failed! Check your inputs!", Toast.LENGTH_SHORT).show();
        }
    }


    // Method for writing account information into CSV -file
    public void writeCSV(final String fName, final String accNr, final Context ct) {
        db.collection("users").document(userRef).collection("accounts").document(accNr).collection("history").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                StringBuilder data = new StringBuilder();
                data.append("Date,Account,Transaction,Amount");
                for (QueryDocumentSnapshot doc : value) {
                    data.append("\n").append(doc.getString("date")).append(",").append(doc.getString("accFrom")).append(",").append(doc.getString("accTo")).append(",").append(doc.getString("amount"));
                }
                try {
                    File rootFolder = ct.getExternalFilesDir(null);
                    File file = new File(rootFolder, fName);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    bw.write(data.toString());
                    bw.close();
                    Toast.makeText(ct, "File " + fName + " saved to local files.", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(ct, "I/O Error! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Methods for managing bank cards
    ////////////////////////////////////////////////////////////////////////
    // Checks does the given card exist, if it does, updates daily limit & cardLink, if doesn't, creates the card
    public void createUpdateBankCard(final String card, final String acc, final long limit, final Context ct) {
        db.collection("users").document(userRef).collection("cardLinks").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot value) {
                int tester = 0;
                List docs = value.getDocuments();
                // Boolean for testing is there card linked already to the chosen account, that isn't the same card user tries to update
                boolean test = false;
                for (int y = 0; y<docs.size();y++) {
                    DocumentSnapshot doc = (DocumentSnapshot) docs.get(y);
                    if (doc.getId().equals(acc)) {
                        if (doc.getString("cardId").equals(card)) {
                        } else {
                            test = true;
                        }
                    }
                }
                if (test) {
                    Toast.makeText(ct, "Account already has a linked card! Please remove link first.", Toast.LENGTH_SHORT).show();
                } else {
                    BigDecimal bigLim = BigDecimal.valueOf(limit, 2);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    String stringLim = formatter.format(bigLim);
                    final Map<String, Object> tmp = new HashMap<>();
                    tmp.put("accId", acc);
                    tmp.put("cardId", card);
                    tmp.put("dailyLimit", stringLim);
                    for (int i = 0; i < docs.size(); i++) {
                        DocumentSnapshot doc = (DocumentSnapshot) docs.get(i);
                        if (doc.getString("cardId").equals(card)) {
                            tester++;
                            db.collection("users").document(userRef).collection("cardLinks").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("users").document(userRef).collection("cardLinks").document(acc).set(tmp);
                                    Toast.makeText(ct, "Card updated!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ct, "Updating card failed! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                    if (tester == 0) {
                        db.collection("users").document(userRef).collection("cardLinks").document(acc).set(tmp);
                        Toast.makeText(ct, "Card created!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Deletes bank card if found in database
    public void deleteBankCard(final String card, final Context ct) {
        db.collection("users").document(userRef).collection("cardLinks").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot doc : value) {
                    if (doc.exists()) {
                        if (doc.getString("cardId").equals(card)) {
                            db.collection("users").document(userRef).collection("cardLinks").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ct, "Card deleted!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ct, "Deleting card failed! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });
    }


    // Methods for transfers, deposits & withdraws
    ////////////////////////////////////////////////////////////////////////
    // Checks is there enough balance or credit limit to do withdraw, updates balance & account history to the database
    public void withdrawMoney(final String selectWithdrawAccount, final long withdraw, final Context ct) {
        db.collection("users").document(bm.getUserRef()).collection("accounts").document(selectWithdrawAccount).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                long balance = task.getResult().getLong("balance");
                if (task.getResult().contains("creditLimit")) {
                    try {
                        Long creditLimit = task.getResult().getLong("creditLimit");
                        if ((creditLimit+balance)>=withdraw) {
                            balance = balance - withdraw;
                            Map<String, Object> tmp = new HashMap<>();
                            tmp.put("balance", balance);
                            db.collection("users").document(bm.getUserRef()).collection("accounts").document(selectWithdrawAccount).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    BigDecimal amt = BigDecimal.valueOf(withdraw, 2);
                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                    Toast.makeText(ct, "Withdraw successful! " + formatter.format(amt) + " taken from the account.", Toast.LENGTH_SHORT).show();
                                    Map<String, Object> tmp = new HashMap<>();
                                    String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                    String amountString = formatter.format(amt.negate());
                                    tmp.put("accFrom", selectWithdrawAccount);
                                    tmp.put("accTo", "Withdraw");
                                    tmp.put("date", currentDate);
                                    tmp.put("amount", amountString);
                                    db.collection("users").document(userRef).collection("accounts").document(selectWithdrawAccount).collection("history").document().set(tmp);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ct, "Error updating withdraw! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ct, "Not enough credit limit!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(ct, "Withdraw failed! Please check your inputs.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    try {
                        if (balance>=withdraw) {
                            balance = balance - withdraw;
                            Map<String, Object> tmp = new HashMap<>();
                            tmp.put("balance", balance);
                            db.collection("users").document(bm.getUserRef()).collection("accounts").document(selectWithdrawAccount).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    BigDecimal amt = BigDecimal.valueOf(withdraw, 2);
                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                    Toast.makeText(ct, "Withdraw successful! " + formatter.format(amt) + " taken from the account.", Toast.LENGTH_SHORT).show();
                                    Map<String, Object> tmp = new HashMap<>();
                                    String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                    String amountString = formatter.format(amt.negate());
                                    tmp.put("accFrom", selectWithdrawAccount);
                                    tmp.put("accTo", "Withdraw");
                                    tmp.put("date", currentDate);
                                    tmp.put("amount", amountString);
                                    db.collection("users").document(userRef).collection("accounts").document(selectWithdrawAccount).collection("history").document().set(tmp);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ct, "Error updating withdraw! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ct, "Not enough balance!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(ct, "Withdraw failed! Please check your inputs.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ct, "Database error! Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Deposits money to an account & updates account history
    public void depositMoney(final String acc, final long amount, final Context ct) {
        db.collection("users").document(userRef).collection("accounts").document(acc).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            long currBal = task.getResult().getLong("balance");
                            currBal = currBal + amount;
                            Map<String, Object> tmp = new HashMap<>();
                            tmp.put("balance", currBal);
                            db.collection("users").document(userRef).collection("accounts").document(acc).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    BigDecimal amt = BigDecimal.valueOf(amount, 2);
                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                    Toast.makeText(ct, "Deposit completed! " + formatter.format(amt) + " deposited to the account.", Toast.LENGTH_SHORT).show();
                                    Map<String, Object> tmp = new HashMap<>();
                                    String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                    String amountString = formatter.format(amt);
                                    tmp.put("accFrom", acc);
                                    tmp.put("accTo", "Deposit");
                                    tmp.put("date", currentDate);
                                    tmp.put("amount", amountString);
                                    db.collection("users").document(userRef).collection("accounts").document(acc).collection("history").document().set(tmp);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Database search failed!");
                    }
                });
    }

    // Transfers money between two own accounts & updates account history for both accounts
    public void transferMoney(final String accFrom, final String accTo, final long amount, final Context ct) {
        try {
            db.collection("users").document(userRef).collection("accounts").document(accFrom).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            final Long fromBalance = document.getLong("balance");
                            if (document.contains("creditLimit")) {
                                Long creditLimit = document.getLong("creditLimit");
                                if ((fromBalance+creditLimit)>=amount) {
                                    Long newBalance = fromBalance - amount;
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put("balance", newBalance);
                                    db.collection("users").document(userRef).collection("accounts").document(accFrom).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            db.collection("users").document(userRef).collection("accounts").document(accTo).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Long oldBal = document.getLong("balance");
                                                            Long newBal = oldBal + amount;
                                                            Map<String, Object> tmp = new HashMap<>();
                                                            tmp.put("balance", newBal);
                                                            db.collection("users").document(userRef).collection("accounts").document(accTo).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    BigDecimal amt = BigDecimal.valueOf(amount, 2);
                                                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                                    Toast.makeText(ct, "Transfer successful! " + formatter.format(amt) + " transferred to " + accTo + "!", Toast.LENGTH_SHORT).show();

                                                                    // Set transfer history to account from which the transfer was made
                                                                    Map<String, Object> tmp1 = new HashMap<>();
                                                                    String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                                                    String amountString = formatter.format(amt.negate());
                                                                    tmp1.put("accFrom", accFrom);
                                                                    tmp1.put("accTo", accTo);
                                                                    tmp1.put("date", currentDate);
                                                                    tmp1.put("amount", amountString);
                                                                    db.collection("users").document(userRef).collection("accounts").document(accFrom).collection("history").document().set(tmp1);

                                                                    // Set transfer history to account to which the transfer was made
                                                                    Map<String, Object> tmp2 = new HashMap<>();
                                                                    String currentDate2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                                                    String amountString2 = formatter.format(amt);
                                                                    tmp2.put("accFrom", accTo);
                                                                    tmp2.put("accTo", accFrom);
                                                                    tmp2.put("date", currentDate2);
                                                                    tmp2.put("amount", amountString2);
                                                                    db.collection("users").document(userRef).collection("accounts").document(accTo).collection("history").document().set(tmp2);
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(ct, "Account not found!", Toast.LENGTH_SHORT).show();
                                                            Map<String, Object> tmp = new HashMap<>();
                                                            tmp.put("balance", fromBalance);
                                                            db.collection("users").document(userRef).collection("accounts").document(accFrom).set(tmp, SetOptions.merge());
                                                        }
                                                    } else {
                                                        System.out.println("ERROR: " + task.getException());
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(ct, "Not enough credit limit!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (fromBalance>=amount) {
                                    Long newBalance = fromBalance - amount;
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put("balance", newBalance);
                                    db.collection("users").document(userRef).collection("accounts").document(accFrom).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            db.collection("users").document(userRef).collection("accounts").document(accTo).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Long oldBal = document.getLong("balance");
                                                            Long newBal = oldBal + amount;
                                                            Map<String, Object> tmp = new HashMap<>();
                                                            tmp.put("balance", newBal);
                                                            db.collection("users").document(userRef).collection("accounts").document(accTo).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    BigDecimal amt = BigDecimal.valueOf(amount, 2);
                                                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                                    Toast.makeText(ct, "Transfer successful! " + formatter.format(amt) + " transferred to " + accTo + "!", Toast.LENGTH_SHORT).show();

                                                                    // Set transfer history to account from which the transfer was made
                                                                    Map<String, Object> tmp1 = new HashMap<>();
                                                                    String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                                                    String amountString = formatter.format(amt.negate());
                                                                    tmp1.put("accFrom", accFrom);
                                                                    tmp1.put("accTo", accTo);
                                                                    tmp1.put("date", currentDate);
                                                                    tmp1.put("amount", amountString);
                                                                    db.collection("users").document(userRef).collection("accounts").document(accFrom).collection("history").document().set(tmp1);

                                                                    // Set transfer history to account to which the transfer was made
                                                                    Map<String, Object> tmp2 = new HashMap<>();
                                                                    String currentDate2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                                                    String amountString2 = formatter.format(amt);
                                                                    tmp2.put("accFrom", accTo);
                                                                    tmp2.put("accTo", accFrom);
                                                                    tmp2.put("date", currentDate2);
                                                                    tmp2.put("amount", amountString2);
                                                                    db.collection("users").document(userRef).collection("accounts").document(accTo).collection("history").document().set(tmp2);
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(ct, "Account not found!", Toast.LENGTH_SHORT).show();
                                                            Map<String, Object> tmp = new HashMap<>();
                                                            tmp.put("balance", fromBalance);
                                                            db.collection("users").document(userRef).collection("accounts").document(accFrom).set(tmp, SetOptions.merge());
                                                        }
                                                    } else {
                                                        System.out.println("ERROR: " + task.getException());
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(ct, "Not enough balance!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(ct, "Account not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        System.out.println("ERROR: " + task.getException());
                    }
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(ct, "Transfer failed! Please check your inputs.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    // Method for doing external transfer to an external account either in the same bank or different bank
    public void externalTransfer(final String transferFrom, final String transferTo, final long amount, final Context ct) {
        try {
            db.collection("users").document(userRef).collection("accounts").document(transferFrom).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    final Long oldBal = task.getResult().getLong("balance");
                    if (task.getResult().contains("creditLimit")) {
                        Long creditLimit = task.getResult().getLong("creditLimit");
                        if ((oldBal+creditLimit)>=amount) {
                            Map<String, Object> tmp = new HashMap<>();
                            Long newBal = oldBal - amount;
                            tmp.put("balance", newBal);
                            db.collection("users").document(userRef).collection("accounts").document(transferFrom).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collectionGroup("accounts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            List docs = task.getResult().getDocuments();
                                            int tester = 0;
                                            for (int i=0;i<docs.size();i++) {
                                                tester++;
                                                DocumentSnapshot doc = (DocumentSnapshot)docs.get(i);
                                                if(doc.getId().equals(transferTo)) {
                                                    tester--;
                                                    long oldBal = doc.getLong("balance");
                                                    long newBal = oldBal + amount;
                                                    final String owner = doc.getString("ownerID");
                                                    String docId = doc.getId();
                                                    final Map<String, Object> tmp = new HashMap<>();
                                                    tmp.put("balance", newBal);
                                                    db.collection("users").document(owner).collection("accounts").document(docId).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            BigDecimal amt = BigDecimal.valueOf(amount, 2);
                                                            NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                            Toast.makeText(ct, "Transfer of " + formatter.format(amt) + " successfully done to " + transferTo + "!", Toast.LENGTH_SHORT).show();

                                                            // Set transfer history to account from which transfer was made
                                                            Map<String, Object> tmp1 = new HashMap<>();
                                                            String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                                            String amountString1 = formatter.format(amt.negate());
                                                            tmp1.put("accFrom", transferFrom);
                                                            tmp1.put("accTo", transferTo);
                                                            tmp1.put("date", currentDate);
                                                            tmp1.put("amount", amountString1);
                                                            db.collection("users").document(userRef).collection("accounts").document(transferFrom).collection("history").document().set(tmp1);

                                                            // Set transfer history to account to which transfer was made
                                                            Map<String, Object> tmp2 = new HashMap<>();
                                                            String amountString2 = formatter.format(amt);
                                                            tmp2.put("accFrom", transferTo);
                                                            tmp2.put("accTo", transferFrom);
                                                            tmp2.put("date", currentDate);
                                                            tmp2.put("amount", amountString2);
                                                            db.collection("users").document(owner).collection("accounts").document(transferTo).collection("history").document().set(tmp2);


                                                        }
                                                    });
                                                }
                                                if(tester==docs.size()) {
                                                    BigDecimal amt = BigDecimal.valueOf(amount, 2);
                                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                    Toast.makeText(ct, "Payment of " + formatter.format(amt) + " successfully done to " + transferTo + "!", Toast.LENGTH_SHORT).show();

                                                    // Set transfer history
                                                    Map<String, Object> tmp = new HashMap<>();
                                                    String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                                    String amountString = formatter.format(amt.negate());
                                                    tmp.put("accFrom", transferFrom);
                                                    tmp.put("accTo", transferTo);
                                                    tmp.put("date", currentDate);
                                                    tmp.put("amount", amountString);
                                                    db.collection("users").document(userRef).collection("accounts").document(transferFrom).collection("history").document().set(tmp);
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(ct, "Not enough credit limit!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (oldBal>=amount) {
                            Map<String, Object> tmp = new HashMap<>();
                            Long newBal = oldBal - amount;
                            tmp.put("balance", newBal);
                            db.collection("users").document(userRef).collection("accounts").document(transferFrom).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collectionGroup("accounts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            List docs = task.getResult().getDocuments();
                                            int tester = 0;
                                            for (int i=0;i<docs.size();i++) {
                                                tester++;
                                                DocumentSnapshot doc = (DocumentSnapshot)docs.get(i);
                                                if(doc.getId().equals(transferTo)) {
                                                    tester--;
                                                    long oldBal = doc.getLong("balance");
                                                    long newBal = oldBal + amount;
                                                    final String owner = doc.getString("ownerID");
                                                    String docId = doc.getId();
                                                    System.out.println(docId + owner);
                                                    Map<String, Object> tmp = new HashMap<>();
                                                    tmp.put("balance", newBal);
                                                    db.collection("users").document(owner).collection("accounts").document(docId).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(ct, "Transfer done to an existing bank account in our system!", Toast.LENGTH_SHORT).show();

                                                            // Set transfer history to account from which transfer was made
                                                            Map<String, Object> tmp = new HashMap<>();
                                                            BigDecimal amt = BigDecimal.valueOf(amount, 2);
                                                            NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                            String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                                            String amountString = formatter.format(amt.negate());
                                                            tmp.put("accFrom", transferFrom);
                                                            tmp.put("accTo", transferTo);
                                                            tmp.put("date", currentDate);
                                                            tmp.put("amount", amountString);
                                                            db.collection("users").document(userRef).collection("accounts").document(transferFrom).collection("history").document().set(tmp);

                                                            // Set transfer history to account to which transfer was made
                                                            Map<String, Object> tmp2 = new HashMap<>();
                                                            String amountString2 = formatter.format(amt);
                                                            tmp2.put("accFrom", transferTo);
                                                            tmp2.put("accTo", transferFrom);
                                                            tmp2.put("date", currentDate);
                                                            tmp2.put("amount", amountString2);
                                                            db.collection("users").document(owner).collection("accounts").document(transferTo).collection("history").document().set(tmp2);
                                                        }
                                                    });
                                                }
                                                if(tester==docs.size()) {
                                                    Toast.makeText(ct, "Transfer done as a payment!", Toast.LENGTH_SHORT).show();

                                                    // Set transfer history
                                                    Map<String, Object> tmp = new HashMap<>();
                                                    BigDecimal amt = BigDecimal.valueOf(amount, 2);
                                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                    String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
                                                    String amountString = formatter.format(amt.negate());
                                                    tmp.put("accFrom", transferFrom);
                                                    tmp.put("accTo", transferTo);
                                                    tmp.put("date", currentDate);
                                                    tmp.put("amount", amountString);
                                                    db.collection("users").document(userRef).collection("accounts").document(transferFrom).collection("history").document().set(tmp);
                                                }
                                            }
                                        }
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(ct, "Not enough balance!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(ct, "Transfer failed! Check your inputs.", Toast.LENGTH_SHORT).show();
        }

    }

    // Methods for managing user settings
    ////////////////////////////////////////////////////////////////////////
    // Method for updating user information
    public void updateInformation(String name, String address, String phone, final Context ct) {
        Map<String, Object> tmp = new HashMap<>();
        if (name.length()!=0) {
            tmp.put("name", name);
        }
        if (address.length()!=0) {
            tmp.put("address", address);
        }
        if (phone.length()!=0) {
            tmp.put("phone", phone);
        }
        if (phone.length()==0 && address.length()==0 && name.length()==0) {
            Toast.makeText(ct, "Please insert information.", Toast.LENGTH_SHORT).show();
        } else {
            db.collection("users").document(userRef).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(ct, "Information updated!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ct, "Updating information failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method for updating users password
    void updatePassword(final String currPw, final String newPw, final Context ct) {
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
                                    Toast.makeText(ct, "Password changed!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ct, "Updating password failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ct, "Password incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ct, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    System.out.println("ERROR: " + task.getException());
                }
            }
        });
    }
}
