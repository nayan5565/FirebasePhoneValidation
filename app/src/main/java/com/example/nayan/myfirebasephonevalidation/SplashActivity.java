package com.example.nayan.myfirebasephonevalidation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar loadingProgress;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        checkStatus();

    }

    private void init() {
        setContentView(R.layout.activity_splash);
        loadingProgress = findViewById(R.id.loadingProgress);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


    }

    private void checkStatus() {


        if (mAuth.getCurrentUser() != null) {
            loadingProgress.setVisibility(View.VISIBLE);

            userId = mAuth.getCurrentUser().getUid();
            Log.e("Splash", "s1:");

            db.collection(Global.CONFIG).document(Global.ALL).update("today", FieldValue.serverTimestamp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db.collection(Global.CONFIG).document(Global.ALL).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                Global.config = task.getResult().toObject(MConfig.class);
                                Log.e("Splash", "s2:");
//                                handleApp(Global.config);
                                getUser();
                            } else {
                                loadingProgress.setVisibility(View.GONE);
                                Log.e("Splash", "s3:" + task.getException().getMessage());
                            }
                        }
                    });

                }
            });


        } else {

            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }

    }

    private void getUser() {
        Log.e("Splash", "s4:");
        db.collection(Global.KEY_USERS).document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Log.e("Splash", "s5:");
                    Global.user = task.getResult().toObject(MUser.class);
                    loadingProgress.setVisibility(View.GONE);
                    db.collection(Global.KEY_USERS).document(userId).update("appVerCode", BuildConfig.VERSION_CODE);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.e("Splash", "s6:");
                    loadingProgress.setVisibility(View.GONE);
                    startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
                    finish();
                }
            }
        });
    }

    private void handleApp(MConfig config) {

        int currentVer = BuildConfig.VERSION_CODE;
        if (config.isUpdateMandatory && currentVer < config.appVerCode) {
            Toast.makeText(this, "You have to use updated app.", Toast.LENGTH_SHORT).show();
            finish();
        } else
            getUser();
    }
}
