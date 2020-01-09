package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), com.example.todolistapp.ProfileActivity.class));
        }
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        buttonSignIn=(Button)findViewById(R.id.buttonSignIn);
        progressDialog=new ProgressDialog(this);
        textViewSignUp=(TextView)findViewById(R.id.textViewSignUp);
        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

    }
    private  void userLogin(){
        String email=editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering user.....");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            finish();
                            startActivity(new Intent(getApplicationContext(), com.example.todolistapp.ProfileActivity.class));
                        }


                    }
                });
    }
    @Override
    public void onClick(View v) {
        if(v==buttonSignIn)
        {
            userLogin();
        }
        if(v==textViewSignUp)
        {
            finish();
            startActivity(new Intent(this, com.example.todolistapp.MainActivity.class));
        }
    }
}
