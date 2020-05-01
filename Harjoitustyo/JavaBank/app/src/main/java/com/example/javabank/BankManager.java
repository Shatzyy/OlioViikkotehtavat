package com.example.javabank;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
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

    public boolean createCreditAccount(String acc, String bal, String cLim) {
        try {
            long balance = Long.parseLong(bal);
            long creditLimit = Long.parseLong(cLim);
            Account tmp = new CreditAccount(acc, balance, userRef, creditLimit);
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

    public boolean updateAccount(String acc, String credLim, String linkCard) {
        try {
            long cLim;
            if (credLim.length()==0) {
                cLim = 0;
            } else {
                cLim = Long.parseLong(credLim);
            }
            if (cLim ==0 && linkCard.length()==0) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("creditLimit", FieldValue.delete());
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
            } else if (cLim==0) {
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
            } else if (linkCard.length()==0) {
                db.collection("users").document(userRef).collection("cardLinks").document(acc).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("Card link removed.");
                    }
                });
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
            }
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
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
    public void withdrawMoney() {
        // TODO withdraw
    }

    public void depositMoney(final String acc, final long bal) {
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
                                    System.out.println("Balance updated!");
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

    public void transferMoney() {
        // TODO Transfer
    }

    // Method for writing account information into JSON -file
    public void writeJSON(String fname) {
        // TODO Write JSON
    }

    public ArrayList<String> getBankCardNames() {
        ArrayList<String> list = new ArrayList<>();
        list.add("");
        list.add("Card 1");
        return list;
    }

    public ArrayList<Account> getAccounts() {
        return null;
    }

    public void setUserRef(String s) {
        this.userRef = s;
    }

    public void updateInformation(String name, String address, String phone) {
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
                System.out.println("User information updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Updating user information failed!");
            }
        });
    }

    public String getUserRef() {
        return userRef;
    }
}
