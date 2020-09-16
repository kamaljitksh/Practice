package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
TextView txt_register;
EditText email_editxt,passwrd_editxt;
Button loginbtn;
String email,pass;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        txt_register=findViewById(R.id.txt_loginRegister);
        txt_register.setOnClickListener(this);
        email_editxt=findViewById(R.id.email_edit_txt);
        passwrd_editxt=findViewById(R.id.passwrd_edit_txt);
        loginbtn=findViewById(R.id.login_btn);
        loginbtn.setOnClickListener(this);
        txt_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txt_loginRegister){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }else if(v.getId()==R.id.login_btn){
                login();
        }

    }
    private void login() {
        email = email_editxt.getText().toString().trim();
        pass = passwrd_editxt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            email_editxt.setError("Email is required");
            return;
        } else if (TextUtils.isEmpty(pass)) {
            passwrd_editxt.setError("Password is required");
            return;
        } else if (pass.length() < 6) {
            passwrd_editxt.setError("minimum 6 characters");
            return;
        }
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                }else{
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        email_editxt.getText().clear();
                        passwrd_editxt.getText().clear();
                        email_editxt.requestFocus();
                        email_editxt.setError("Invalid cred");
                    }catch (FirebaseAuthInvalidUserException e){
                        email_editxt.getText().clear();
                        passwrd_editxt.getText().clear();
                        email_editxt.requestFocus();
                        email_editxt.setError("User not registered");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}