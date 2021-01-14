package com.example.dataserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dataserver.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {
    TextView email;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button BtnSave;
    EditText fullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fullName=findViewById(R.id.editText_FullName);
        email=findViewById(R.id.textView_emailadress);
        BtnSave=findViewById(R.id.button_save);


        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();


        userID=fAuth.getCurrentUser().getUid();

        final DocumentReference documentReference=fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                fullName.setText(documentSnapshot.getString("fName"));
                email.setText(documentSnapshot.getString("email"));
            }
        });

        Toolbar toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);

        BtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.UpdateSettings();
            }
        });
            

    }

    private void sendToMain(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }

    private void UpdateSettings() {
        String set_name = fullName.getText().toString();
        fAuth=FirebaseAuth.getInstance();

        if (TextUtils.isEmpty(set_name)){
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }
        else{
            userID=fAuth.getCurrentUser().getUid();
            DocumentReference documentReference=fStore.collection("users").document(userID);
            Map<String,Object> user=new HashMap<>();
            user.put("fName", fullName.getText().toString());

            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void Void) {
                    Toast.makeText(ProfileActivity.this, "Your Profile was updated Successfully", Toast.LENGTH_SHORT).show();
                    sendToMain();
                }
             });
        }
    }

}