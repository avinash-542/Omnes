package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class StueditActivity extends AppCompatActivity {
    private EditText name, mail, lec;
    private Button ent;
    private ImageView info;
    private FirebaseAuth mauth;
    private DatabaseReference userref;
    private String inuid;
    private String ifedit=null;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuedit);
        name = (EditText) findViewById(R.id.stuname1);
        mail = (EditText) findViewById(R.id.stumail1);
        lec = (EditText) findViewById(R.id.stulec1);
        mauth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference();
        info = (ImageView) findViewById(R.id.infog1);
        ent = (Button) findViewById(R.id.entry1);

        info.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(StueditActivity.this);
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

        if(getIntent().getStringExtra("stuUID") != null) {
            userref.child("Users").child(ifedit = getIntent().getStringExtra("stuUID")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        mail.setText(snapshot.child("RollNo").getValue().toString());
                        name.setText(snapshot.child("UserID").getValue().toString());
                        lec.setText(snapshot.child("Class").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String , Object> up = new HashMap<String, Object>();
                    up.put("UserID", name.getText().toString());
                    up.put("email", mail);
                    up.put("Class", lec.getText().toString().toUpperCase());

                    userref.child("Users").child(getIntent().getStringExtra("stuUID")).updateChildren(up)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(StueditActivity.this, "Student Details Updateded...", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }

    }
}