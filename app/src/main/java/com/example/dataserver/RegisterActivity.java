package com.example.dataserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName, mEmail, mPassword;
    Button mRegisterBtn, mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName=findViewById(R.id.editText_Name);
        mEmail=findViewById(R.id.editText_Email);
        mPassword=findViewById(R.id.editText_Password);
        mRegisterBtn=findViewById(R.id.button_Login);
        mLoginBtn=findViewById(R.id.button_Create);

        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBar);

        mRegisterBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
                final String fullname=mFullName.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }

                if(password.length()<6){
                    mPassword.setError("Password needs to have at least 6 characters");
                }

                progressBar.setVisibility(View.VISIBLE);

                //Registration in Firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User created.", Toast.LENGTH_SHORT).show();
                            userID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fstore.collection("users").document(userID);
                            Map<String,Object> user=new HashMap<>();
                            user.put("fName",fullname);
                            user.put("email",email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: "+ e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.VISIBLE);
                        }

                    }
                });

            }
        }));

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }
}