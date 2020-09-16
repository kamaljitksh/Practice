package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.CellSignalStrength;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txt_login;
    String name, emai,phone,pass,cpass;
    EditText edt_name,edt_email,edt_phn,edt_pass,edt_cpass;
    Button btn_reg;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        txt_login=findViewById(R.id.txt_mainLogin);
        edt_name=findViewById(R.id.edit_name);
        edt_email=findViewById(R.id.email_edit);
        edt_phn=findViewById(R.id.edi_Phone);
        edt_pass=findViewById(R.id.pass_edit);
        edt_cpass=findViewById(R.id.con_pass_edit);
        btn_reg=findViewById(R.id.regis_btn);
        btn_reg.setOnClickListener(this);
        txt_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.txt_mainLogin){
            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }else if(v.getId()==R.id.regis_btn){
            register();
        }
    }

    private void register() {
        name=edt_name.getText().toString().trim();
        emai=edt_email.getText().toString().trim();
        phone=edt_phn.getText().toString().trim();
        pass=edt_pass.getText().toString().trim();
        cpass=edt_cpass.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            edt_name.setError("Name required");
            return;
        }
       else if (TextUtils.isEmpty(emai)){
            edt_email.setError("Email required");
            return;
        }else if (TextUtils.isEmpty(pass)){
            edt_pass.setError("Password required");
            return;
        }else if(TextUtils.isEmpty(phone)){
           edt_phn.setError("Phone number required");
           return;
        }
       else if(pass.length()<6){
            edt_pass.getText().clear();
            edt_pass.requestFocus();
            edt_pass.setError("Password minimum 6 characters");
            return;
        }
        else if (TextUtils.isEmpty(cpass)){
            edt_cpass.setError("Confirm Password required");
            return;
        }else if(!pass.equals(cpass)){
            edt_pass.getText().clear();
            edt_cpass.getText().clear();
            edt_pass.requestFocus();
            edt_cpass.setError("Confirm Password doesn't match");
            return;
        }
        mAuth.createUserWithEmailAndPassword(emai,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                    saveData();
                }else{
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthUserCollisionException e){
                        edt_email.getText().clear();
                        edt_pass.getText().clear();
                        edt_phn.getText().clear();
                        edt_cpass.getText().clear();
                        edt_email.requestFocus();
                        edt_email.setError("Email Already exist");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "Not Register", Toast.LENGTH_SHORT).show();
                }

            }

            private void saveData() {
                Map<String,Object> userdata=new HashMap<>();
                userdata.put("Name",name);
                userdata.put("Email",emai);
                userdata.put("Phone No",phone);
                Log.d("Register","Map="+userdata);
                user=mAuth.getCurrentUser();
                db.collection("Users").document(user.getUid()).set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this,"Data saved",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Data not saved",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        user=mAuth.getCurrentUser();
        if (user!=null){
            Toast.makeText(MainActivity.this, "Already login", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
        }
    }
}