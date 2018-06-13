package com.example.nayan.myfirebasephonevalidation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvName, tvPhone;

    //Firebase
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private MUser user;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initFirebase();

    }

    @Override
    protected void onStart() {
        super.onStart();


        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            prepareDis();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_log_out) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void init() {
        setContentView(R.layout.activity_main);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

    }

    private void prepareDis() {
        userId = mAuth.getCurrentUser().getUid();
        user = Global.user;
        tvName.setText(user.name);
        tvPhone.setText(user.phone);


    }


}
