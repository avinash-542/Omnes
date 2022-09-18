package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.UidVerifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;


public class FacentryActivity extends AppCompatActivity {
    private EditText email, pass,io,oc, os, hodto;
    private Button upload;
    private DatabaseReference userref;
    private CheckBox ar, hod;
    private String [] oclasses, subj;
    private String ifedit;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facentry);
        userref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.facmail);
        pass =  (EditText) findViewById(R.id.facpass);
        io = (EditText) findViewById(R.id.incharge);
        oc = (EditText) findViewById(R.id.othrec);
        os = (EditText) findViewById(R.id.othres);
        hodto = (EditText) findViewById(R.id.hodto);
        hod = (CheckBox) findViewById(R.id.ishod);
        ar = (CheckBox) findViewById(R.id.adminr);





        if(ar.isChecked())
            hod.setChecked(false);
        if(hod.isChecked())
            ar.setChecked(false);



        upload = (Button) findViewById(R.id.submit2);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty() | pass.getText().toString().isEmpty()) {
                    Toast.makeText(FacentryActivity.this, "Faculty Details can't be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseOptions op = new FirebaseOptions.Builder()
                            .setApiKey("AIzaSyAuntyvs0jV2kCYKDRe9Wo24vJ1f6V5pBQ")
                            .setDatabaseUrl("https://omnes-uno-default-rtdb.firebaseio.com/")
                            .setApplicationId("com.example.omnes")
                            .setProjectId("omnes-uno")
                            .build();

                    FirebaseApp sauth1 = FirebaseApp.initializeApp(getApplicationContext(), op, "https://omnes-uno-default-rtdb.firebaseio.com/");
                    FirebaseAuth.getInstance(sauth1).createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        String uid = task.getResult().getUser().getUid();
                                        String userid =  email.getText().toString().replace("@mrcet.ac.in", "");
                                        HashMap<String , Object> up = new HashMap<String, Object>();
                                        up.put("UserID", userid);
                                        up.put("UID", uid);
                                        up.put("Role", "Faculty");
                                        String [] div = io.getText().toString().toUpperCase().split("-");
                                        if(!io.getText().toString().isEmpty()) {
                                            up.put("Incharge to", div[0]);
                                            up.put("In-sub", div[1]);
                                            up.put("email", email.getText().toString());
                                            up.put("isIncharge", 1);
                                            HashMap <String, Object> in = new HashMap<String, Object>();
                                            in.put("Subject", div[1]);
                                            in.put("UID", uid);
                                            in.put("Role", "Incharge");
                                            userref.child("Classes").child(div[0]).child("Faculty").child(uid).updateChildren(in)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                HashMap<String, Object> le = new HashMap<>();
                                                                le.put("UID",uid);
                                                                userref.child("Leaves").child(div[0]).child("Incharge").updateChildren(le)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()) {

                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(FacentryActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            up.put("isIncharge", 0);
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
                                            for (int a = 0; a < oclasses.length-1; a++) {
                                                HashMap<String, Object> us = new HashMap<String, Object>();
                                                us.put("Subject", subj[a]);
                                                us.put("UID", uid);
                                                us.put("Role", "Faculty");
                                                userref.child("Classes").child(oclasses[a]).child("Faculty").child(uid).updateChildren(us)
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
                                                                Toast.makeText(FacentryActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                        if(ar.isChecked())
                                            up.put("isAdmin", 1);
                                        else
                                            up.put("isAdmin", 0);
                                        if(hod.isChecked()) {
                                            up.put("isHod", 1);
                                            if(hodto.getText().toString().isEmpty()) {
                                                Toast.makeText(FacentryActivity.this, "Enter Hod Branch", Toast.LENGTH_SHORT).show();
                                            } else {
                                                up.put("HodTo", hodto.getText().toString());
                                            }
                                        } else {
                                            up.put("isHod", 0);
                                        }
                                        userref.child("Users").child(uid).updateChildren(up)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(FacentryActivity.this, "Faculty Details Uploaded...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        FirebaseAuth.getInstance(sauth1).signOut();
                                        sauth1.delete();
                                        email.setText(null);
                                        pass.setText(null);
                                        io.setText(null);
                                        oc.setText(null);
                                        os.setText(null);
                                        if(ar.isChecked()) ar.setChecked(false);
                                        if(hod.isChecked()) hod.setChecked(false);
                                    }
                                }
                            });

                }
            }
        });




    }

    private void establish(String ifedit) {
        userref.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(ifedit)) {
                    email.setText(snapshot.child(ifedit).child("email").getValue().toString());
                    if(snapshot.child(ifedit).hasChild("Incharge to")) {
                        String com = snapshot.child(ifedit).child("Incharge to").getValue().toString() + "-" + snapshot.child(ifedit).child("In-sub").getValue().toString();
                        io.setText(com);
                    }
                    if(snapshot.child(ifedit).hasChild("Otherc")) {
                        oc.setText(snapshot.child(ifedit).child("Otherc").getValue().toString());
                        os.setText(snapshot.child(ifedit).child("Others").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog_NoActionBar);
        builder.setTitle("INSTRUCTIONS");
        builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setMessage(R.string.facentrymess);
        builder.setCancelable(true);
        builder.create().show();

    }
}