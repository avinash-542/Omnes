package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

public class StudentryActivity extends AppCompatActivity {

    private EditText fname, lname, pass, mail, lec;
    private Button ent;
    private ImageView info;
    private FirebaseAuth mauth;
    private DatabaseReference userref;
    private String inuid;
    private int count=0;
    private String ifedit=null;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentry);

        initialize();
        ifedit = getIntent().getStringExtra("StudUID");




        info.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(StudentryActivity.this);
                builder1.setTitle("OMNES");
                builder1.setMessage("Enter in the format 'BRANCH_year_SECTION'");
                builder1.setCancelable(true);
                builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alert11 = builder1.create();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        alert11.show();
                        break;
                    case MotionEvent.ACTION_UP:
                        alert11.dismiss();
                        break;
                }
                return true;
            }
        });


        ent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mail.getText().toString().isEmpty() || pass.getText().toString().isEmpty() || fname.getText().toString().isEmpty() || lname.getText().toString().isEmpty() || lec.getText().toString().isEmpty() ) {
                    Toast.makeText(StudentryActivity.this, "Student Details can't be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseOptions op = new FirebaseOptions.Builder()
                            .setApiKey("AIzaSyAuntyvs0jV2kCYKDRe9Wo24vJ1f6V5pBQ")
                            .setDatabaseUrl("https://omnes-uno-default-rtdb.firebaseio.com/")
                            .setApplicationId("com.example.omnes")
                            .setProjectId("omnes-uno")
                            .build();

                    String email = mail.getText().toString().toUpperCase() + "@mrcet.ac.in";
                    userref.child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot data : snapshot.getChildren()) {
                                if(data.hasChild("Incharge to") && data.child("Incharge to").getValue().toString().equals(lec.getText().toString().toUpperCase())) {
                                    inuid = data.child("UID").getValue().toString();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    FirebaseApp sauth1 = FirebaseApp.initializeApp(getApplicationContext(), op, "https://omnes-uno-default-rtdb.firebaseio.com/");
                    FirebaseAuth.getInstance(sauth1).createUserWithEmailAndPassword(email, pass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        String uid = task.getResult().getUser().getUid();
                                        String rollno =  mail.getText().toString().toUpperCase();
                                        String[] cldet = lec.getText().toString().toUpperCase().split("_");
                                        HashMap<String , Object> up = new HashMap<String, Object>();
                                        up.put("RollNo", rollno);
                                        up.put("UserID", fname.getText().toString());
                                        up.put("fname", fname.getText().toString());
                                        up.put("lname", lname.getText().toString());
                                        up.put("UID", uid);
                                        up.put("Role", "Student");
                                        up.put("email", email);
                                        up.put("Class", lec.getText().toString().toUpperCase());
                                        up.put("isAdmin", 0);
                                        up.put("isHod", 0);
                                        up.put("Incharge", inuid);
                                        up.put("isIncharge",0);
                                        userref.child("Classes").child(lec.getText().toString().toUpperCase()).child("Students").child(rollno)
                                                .updateChildren(up).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });

                                        userref.child("Users").child(uid).updateChildren(up)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(StudentryActivity.this, "Students Details Uploaded...", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        FirebaseAuth.getInstance(sauth1).signOut();
                                        sauth1.delete();
                                        mail.setText(null);
                                        pass.setText(null);
                                        fname.setText(null);
                                        lname.setText(null);
                                        lec.setText(null);
                                    }
                                }
                            });

                }
            }
        });

    }

    private void initialize() {
        fname = (EditText) findViewById(R.id.stufname);
        lname = (EditText) findViewById(R.id.stulname);
        mail = (EditText) findViewById(R.id.stumail);
        pass = (EditText) findViewById(R.id.stupass);
        lec = (EditText) findViewById(R.id.stulec);
        mauth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference();
        info = (ImageView) findViewById(R.id.infog);
        ent = (Button) findViewById(R.id.entry);
    }
}