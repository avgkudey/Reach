package com.avg.kasun.reach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName, mEmail, mPassword;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDisplayName = findViewById(R.id.login_email);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateBtn = findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);

        progressDialog = new ProgressDialog(this);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((mDisplayName.getEditText().getText().length() > 0 || mEmail.getEditText().getText()
                        .length() > 0 || mPassword.getEditText().getText().length() > 0)) {
                    String display_name = mDisplayName.getEditText().getText().toString();
                    String email = mEmail.getEditText().getText().toString();
                    String password = mPassword.getEditText().getText().toString();

                    progressDialog.setTitle("Registering Account");
                    progressDialog.setMessage("Please wait while we create your Account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(display_name, email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void register_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser current_User = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_User.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("status", "Hi there, I'm using Reach");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", deviceToken);
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    progressDialog.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot Signin.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
