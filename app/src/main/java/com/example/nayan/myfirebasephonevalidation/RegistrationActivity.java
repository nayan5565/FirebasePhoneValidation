package com.example.nayan.myfirebasephonevalidation;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName, mReffer, tilDeposit;
    private Button mCreateBtn;

    private Toolbar mToolbar;

    private ProgressBar progressBar;

    //Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Toolbar Set
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = findViewById(R.id.loadingProgress);


        // Firebase Auth

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        // Android Fields

        mDisplayName = findViewById(R.id.register_display_name);
        mReffer = findViewById(R.id.reg_Reffer);
        tilDeposit = findViewById(R.id.tilDeposit);
        mCreateBtn = findViewById(R.id.reg_create_btn);

        db.collection(Global.CONFIG).document(Global.ALL).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Global.config = task.getResult().toObject(MConfig.class);
                    mCreateBtn.setEnabled(true);
                }
            }
        });
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mDisplayName.getEditText().toString().trim();
                if (!TextUtils.isEmpty(name)) {

                    getUserInfo();
                }


            }
        });


    }

    private String getReferNum(String phone) {
        phone = phone.startsWith("+88") ? phone : "+88" + phone;
        return phone;
    }

    private void getUserInfo() {
        progressBar.setVisibility(View.VISIBLE);
        final String deviceToken = FirebaseInstanceId.getInstance().getToken();

        userId = mAuth.getCurrentUser().getUid();
        String name = mDisplayName.getEditText().getText().toString().trim();
        final String referPhone = mReffer.getEditText().getText().toString().trim();
        String depositAmountStr = tilDeposit.getEditText().getText().toString().trim();
        int depositAmount = Integer.parseInt(depositAmountStr);

        final MUser user = new MUser();
        user.userId = userId;
        user.name = name;
        user.deviceToken = deviceToken;
        user.phone = mAuth.getCurrentUser().getPhoneNumber();
        user.depositAmount = depositAmount;


        db.collection(Global.KEY_USERS)
                .whereEqualTo("phone", getReferNum(referPhone))
                .get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Global.user = user;
                            if (!task.getResult().getDocuments().isEmpty()) {
                                MUser parent = task.getResult().getDocuments().get(0).toObject(MUser.class);
                                if (!parent.userId.equals(user.userId)) {
                                    user.level1Id = parent.phone;
                                    user.root = parent.phone;
                                    if (parent.root != null && parent.root.length() > 0) {
                                        user.root += "#" + parent.root;

                                        String[] levels = user.root.split("#");
                                        for (int i = 0; i < levels.length; i++) {
                                            if (i == 1) {
                                                user.level2Id = levels[1];
                                            } else if (i == 2) {
                                                user.level3Id = levels[2];
                                            } else if (i == 3) {
                                                user.level4Id = levels[3];
                                            } else if (i == 4) {
                                                user.level5Id = levels[4];
                                            } else if (i == 5) {
                                                user.level6Id = levels[5];
                                            } else if (i == 6) {
                                                user.level7Id = levels[6];
                                            } else if (i == 7) {
                                                user.level8Id = levels[7];
                                            } else if (i == 8) {
                                                user.level9Id = levels[8];
                                            } else if (i == 9) {
                                                user.level10Id = levels[9];
                                            }
                                        }
                                    }

                                }

                            } else {
                                Log.e("Regis", "No phone no found");
                            }
                        }
                        saveUserToDB(user);

                    }
                });


    }

    private void saveUserToDB(MUser user) {
        db.collection(Global.KEY_USERS).document(userId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        db.collection(Global.KEY_USERS).document(userId).update("createdDate", FieldValue.serverTimestamp());
                        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
