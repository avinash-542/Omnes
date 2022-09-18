package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FaceditActivity extends AppCompatActivity {
    private EditText email, pass,io,oc, os;
    private Button upload;
    private DatabaseReference userref;
    private CheckBox ar, hod;
    private String [] oclasses, subj;
    private String ifedit, tokenid;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facedit);

        userref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.facmail1);
        io = (EditText) findViewById(R.id.incharge1);
        oc = (EditText) findViewById(R.id.othrec1);
        os = (EditText) findViewById(R.id.othres1);
        hod = (CheckBox) findViewById(R.id.ishod1);
        ar = (CheckBox) findViewById(R.id.adminr1);
        upload = (Button) findViewById(R.id.submit3);

        if(getIntent().getStringExtra("facUID") != null) {
            Log.d("CheckUID", getIntent().getStringExtra("facUID"));
            userref.child("Users").child(ifedit = getIntent().getStringExtra("facUID")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        email.setText(snapshot.child("email").getValue().toString());
                        if(snapshot.hasChild("Incharge to")) {
                            io.setText(snapshot.child("Incharge to").getValue().toString() + "-" + snapshot.child("In-sub").getValue().toString());
                        } else {
                            io.setText(null);
                        }
                        if(snapshot.hasChild("Otherc")) {
                            oc.setText(snapshot.child("Otherc").getValue().toString());
                            os.setText(snapshot.child("Others").getValue().toString());
                        } else {
                            oc.setText(null);
                            os.setText(null);
                        }
                        if(snapshot.child("isAdmin").getValue().toString().equals("1")) {
                            ar.setChecked(true);
                        } else {
                            ar.setChecked(false);
                        }
                        if(snapshot.child("isHod").getValue().toString().equals("1")) {
                            hod.setChecked(true);
                        } else {
                            hod.setChecked(false);
                        }
                        tokenid = snapshot.child("Token_ID").child("result").getValue().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String , Object> up = new HashMap<String, Object>();
                    String [] div = io.getText().toString().toUpperCase().split("-");
                    if(!io.getText().toString().isEmpty()) {
                        up.put("Incharge to", div[0]);
                        up.put("In-sub", div[1]);
                        up.put("email", email.getText().toString());
                        HashMap <String, Object> in = new HashMap<String, Object>();
                        in.put("Subject", div[1]);
                        in.put("UID", getIntent().getStringExtra("facUID"));
                        in.put("Role", "Incharge");
                        in.put("Token_ID", tokenid);
                        userref.child("Classes").child(div[0]).child("Faculty").child(getIntent().getStringExtra("facUID")).updateChildren(in)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(FaceditActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    if(!oc.getText().toString().isEmpty()) {
                        up.put("Otherc", oc.getText().toString().toUpperCase());
                        oclasses = oc.getText().toString().toUpperCase().split(",");
                        if(!os.getText().toString().isEmpty()) {
                            subj = os.getText().toString().toUpperCase().split(",");
                            if(subj.length != oclasses.length) {
                                Toast.makeText(getApplicationContext(), "Enter same number of subjects as classes", Toast.LENGTH_SHORT).show();
                            } else {
                                up.put("Others", os.getText().toString().toUpperCase());

                            }
                        } else {

                        }
                        for (int a = 0; a < oclasses.length; a++) {
                            HashMap<String, Object> us = new HashMap<String, Object>();
                            us.put("Subject", subj[a]);
                            us.put("UID", getIntent().getStringExtra("facUID"));
                            us.put("Role", "Faculty");
                            us.put("Token_ID", tokenid);
                            userref.child("Classes").child(oclasses[a]).child("Faculty").child(getIntent().getStringExtra("facUID")).updateChildren(us)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(FaceditActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                    if(ar.isChecked())
                        up.put("isAdmin", 1);
                    else
                        up.put("isAdmin", 0);
                    if(hod.isChecked())
                        up.put("isHod", 1);
                    else
                        up.put("isHod", 0);
                    userref.child("Users").child(getIntent().getStringExtra("facUID")).updateChildren(up)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(FaceditActivity.this, "Faculty Details Uploaded...", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }


    }
}