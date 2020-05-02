package com.example.javabank;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Class for managing all Bank activities
public class BankManager {
    // Single instance of BankManager allowed
    private static final BankManager bm = new BankManager();
    // Initialize Database connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userRef = "";
    public BankManager () {

    }

    // Singleton for BankManager
    public static BankManager getInstance() {
        return bm;
    }


    // Methods for managing accounts
    public boolean createDebitAccount(String acc, String bal) {
        try {
            long balance = Long.parseLong(bal);
            Account tmp = new DebitAccount(acc, balance, userRef);
            db.collection("users").document(userRef).collection("accounts").document(acc).set(tmp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteAccount(String acc) {
        db.collection("users").document(userRef).collection("accounts").document(acc).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Account deleted!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Deleting account failed!");
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

    public void createUpdateAccount(final String acc, String credLim, final String linkCard, final Context ct) {
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
                        if (cLim == 0 && linkCard.length() == 0) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("creditLimit", FieldValue.delete());
                            db.collection("users").document(userRef).collection("accounts").document(acc).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Credit limit removed.");
                                }
                            });
                            db.collection("users").document(userRef).collection("cardLinks").document(acc).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Card link removed.");
                                }
                            });
                            Toast.makeText(ct, "Credit limit & linked card removed!", Toast.LENGTH_SHORT).show();
                        } else if (cLim == 0) {
                            Map<String, Object> updates = new HashMap<>();
                            Map<String, Object> cardUpdates = new HashMap<>();
                            updates.put("creditLimit", FieldValue.delete());
                            cardUpdates.put("accountNr", acc);
                            cardUpdates.put("linkedCard", linkCard);
                            db.collection("users").document(userRef).collection("accounts").document(acc).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Credit limit updated.");
                                }
                            });
                            db.collection("users").document(userRef).collection("cardLinks").document(acc).set(cardUpdates, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Card link updated.");
                                }
                            });
                            Toast.makeText(ct, "Credit limit updated & card linked!", Toast.LENGTH_SHORT).show();
                        } else if (linkCard.length() == 0) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("creditLimit", cLim);
                            db.collection("users").document(userRef).collection("accounts").document(acc).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Credit limit updated.");
                                }
                            });
                            db.collection("users").document(userRef).collection("cardLinks").document(acc).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Card link removed.");
                                }
                            });
                            Toast.makeText(ct, "Credit limit updated & card link removed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> updates = new HashMap<>();
                            Map<String, Object> cardUpdates = new HashMap<>();
                            updates.put("creditLimit", cLim);
                            cardUpdates.put("accountNr", acc);
                            cardUpdates.put("linkedCard", linkCard);
                            db.collection("users").document(userRef).collection("accounts").document(acc).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Credit limit updated.");
                                }
                            });
                            db.collection("users").document(userRef).collection("cardLinks").document(acc).set(cardUpdates, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Card link updated.");
                                }
                            });
                            Toast.makeText(ct, "Credit limit & card link updated!", Toast.LENGTH_SHORT).show();
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

    // Methods for managing bank cards
    public void createBankCard() {
        //TODO card managing
    }

    public void deleteBankCard() {
        //TODO card managing
    }

    public void updateBankCard() {
        //TODO card managing
    }

    // Methods for transfers, deposits & withdraws
    public void withdrawMoney(final String selectWithdrawAccount, final String withdrawAmount, final Context ct) {
        db.collection("users").document(bm.getUserRef()).collection("accounts").document(selectWithdrawAccount).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                long balance = task.getResult().getLong("balance");
                if (task.getResult().contains("creditLimit")) {
                    try {
                        Long creditLimit = task.getResult().getLong("creditLimit");
                        final long withdraw = Long.parseLong(withdrawAmount);
                        if ((creditLimit+balance)>=(100*withdraw)) {
                            balance = balance - (100*withdraw);
                            Map<String, Object> tmp = new HashMap<>();
                            tmp.put("balance", balance);
                            db.collection("users").document(bm.getUserRef()).collection("accounts").document(selectWithdrawAccount).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ct, "Withdraw successful! " + withdraw + "$ taken from the account.", Toast.LENGTH_SHORT).show();
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
                        final long withdraw = Long.parseLong(withdrawAmount);
                        if (balance>=(100*withdraw)) {
                            balance = balance - (100*withdraw);
                            Map<String, Object> tmp = new HashMap<>();
                            tmp.put("balance", balance);
                            db.collection("users").document(bm.getUserRef()).collection("accounts").document(selectWithdrawAccount).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ct, "Withdraw successful! " + withdraw + "$ taken from the account.", Toast.LENGTH_SHORT).show();
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

    public void depositMoney(final String acc, final long bal, final Context ct) {
        db.collection("users").document(userRef).collection("accounts").document(acc).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            long currBal = Objects.requireNonNull(task.getResult()).getLong("balance");
                            currBal = currBal + (bal*100);
                            Map<String, Object> tmp = new HashMap<>();
                            tmp.put("balance", currBal);
                            db.collection("users").document(userRef).collection("accounts").document(acc).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ct, "Deposit completed!", Toast.LENGTH_SHORT).show();
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

    public void transferMoney(final String accFrom, final String accTo, String amt, final Context ct) {
        try {
            final long amount = (Long.parseLong(amt)*100);
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
                                                                    Toast.makeText(ct, "Transfer successful!", Toast.LENGTH_SHORT).show();
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
                                                                    Toast.makeText(ct, "Transfer successful!", Toast.LENGTH_SHORT).show();
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

    // Method for writing account information into JSON -file
    public void writeJSON(String fname) {
        // TODO Write JSON
    }

    public ArrayList<String> getBankCardNames() { // TODO database connection
        ArrayList<String> list = new ArrayList<>();
        list.add("");
        list.add("Card 1");
        return list;
    }

    public void setUserRef(String s) {
        this.userRef = s;
    }

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

    public String getUserRef() {
        return userRef;
    }

    public void updatePassword(final String currPw, final String newPw, final Context ct) {
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

    public void externalTransfer(final String transferFrom, final String transferTo, String amt, final Context ct) {
        try {
            final Long amount = Long.parseLong(amt)*100;
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
                                                    String owner = doc.getString("ownerID");
                                                    String docId = doc.getId();
                                                    System.out.println(docId + owner);
                                                    Map<String, Object> tmp = new HashMap<>();
                                                    tmp.put("balance", newBal);
                                                    db.collection("users").document(owner).collection("accounts").document(docId).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(ct, "Transfer done to an existing bank account in our system!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                if(tester==docs.size()) {
                                                    Toast.makeText(ct, "Transfer done as a payment!", Toast.LENGTH_SHORT).show();
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
                                                    String owner = doc.getString("ownerID");
                                                    String docId = doc.getId();
                                                    System.out.println(docId + owner);
                                                    Map<String, Object> tmp = new HashMap<>();
                                                    tmp.put("balance", newBal);
                                                    db.collection("users").document(owner).collection("accounts").document(docId).set(tmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(ct, "Transfer done to an existing bank account in our system!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                if(tester==docs.size()) {
                                                    Toast.makeText(ct, "Transfer done as a payment!", Toast.LENGTH_SHORT).show();
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
}
