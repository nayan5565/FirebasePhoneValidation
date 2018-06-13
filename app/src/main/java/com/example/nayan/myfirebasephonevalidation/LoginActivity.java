package com.example.nayan.myfirebasephonevalidation;

import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    int STATUS_CODE_SENT = 1;
    int status = 0;


    private Button mLogin_btn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvStatus, tvCountryCode;
    private EditText edtPhone;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;

        }

    }

    private void init() {
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        mLogin_btn = findViewById(R.id.login_btn);
        tvStatus = findViewById(R.id.tvStatus);
        tvCountryCode = findViewById(R.id.tvCountryCode);
        edtPhone = findViewById(R.id.edtPhone);


        progressBar = findViewById(R.id.loadingProgress);
        progressBar.setVisibility(View.GONE);
        mLogin_btn.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                updateUI(phoneAuthCredential.getSmsCode());
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                tvStatus.setText(e.getMessage());
            }

            @Override
            public void onCodeSent(String verifcationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verifcationId, forceResendingToken);
                mVerificationId = verifcationId;
                status = STATUS_CODE_SENT;
                edtPhone.setText("");
                edtPhone.setHint("Code");
                tvCountryCode.setText("SMS");
                mLogin_btn.setText("LET'S GO");
            }
        };

    }

    private void updateUI(String code) {
        edtPhone.setText(code);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void login() {
        String phoneNumber = edtPhone.getText().toString().trim();

        if (!TextUtils.isEmpty(phoneNumber)) {

            if (status == 0) {

                String countryCode = "+880";
                String num = (phoneNumber.startsWith("0") ? phoneNumber.substring(1) : phoneNumber);
                num = countryCode + num;
                sendSMS(num);
            } else if (status == STATUS_CODE_SENT) {
                verifyPhoneNumberWithCode(mVerificationId, phoneNumber);
            }


        }
    }

    private void sendSMS(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getGlobalConfig();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.e("Login", "ERR:" + task.getException().getMessage());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void getUser() {
        userId = mAuth.getCurrentUser().getUid();
        db.collection(Global.KEY_USERS).document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Global.user = task.getResult().toObject(MUser.class);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void getGlobalConfig() {
        db.collection(Global.CONFIG).document(Global.ALL).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful() && task.getResult().exists()) {
                    Global.config = task.getResult().toObject(MConfig.class);
                    checkUserStatus();
                } else {
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    private void checkUserStatus() {
        userId = mAuth.getCurrentUser().getUid();
        db.collection(Global.KEY_USERS).document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    boolean isUserExistInDB = task.getResult().exists();

                    if (isUserExistInDB) {
                        getUser();

                    } else {
                        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                        finish();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Login", "ERR:" + e.getMessage());
            }
        });


//
    }


}
