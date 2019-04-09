package com.example.android.loginandsignup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextRegNum;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;

    private Button buttonRegister;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.loginandsignup.R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!=null) {
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextRegNum = (EditText) findViewById(R.id.editTextRegNum);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordConfirm= (EditText) findViewById(R.id.editTextPasswordConfirm);

        buttonRegister.setOnClickListener(this);
    }

    private void registerUser(){
        String name = editTextName.getText().toString().trim();
        String regNum = editTextRegNum.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String reconfirmPassword = editTextPasswordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter your name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(regNum)){
            Toast.makeText(this,"Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your registration number.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(reconfirmPassword)){
            Toast.makeText(this,"Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(reconfirmPassword)){
            Toast.makeText(this,"Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Registered!",Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            saveUserInfo();

                            finish();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                        }
                        else {
                            Toast.makeText(MainActivity.this,"Registration Failed.",Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }
    private void saveUserInfo(){

        String name = editTextName.getText().toString().trim();
        String regNumTemp = editTextRegNum.getText().toString().trim();
        int regNum = Integer.valueOf(regNumTemp);

        UserInformation userInformation = new UserInformation(name,regNum);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference.child("Students").child(firebaseUser.getUid()).setValue(userInformation);
    }

    @Override
    public void onClick(View v) {
        if (v==buttonRegister){
            registerUser();
        }
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}

