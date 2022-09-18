package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeditActivity extends AppCompatActivity {
    private TextView cn, t1,t2,t3,t4,t5,t6,t7, d1;
    private EditText s1,s2,s3,s4,s5,s6,s7;
    private AppCompatButton tup;
    private DatabaseReference userref;
    private FirebaseAuth mAuth;
    private List<String> day, sub, fsub;
    private int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tedit);
        cn = (TextView) findViewById(R.id.classname);
        d1 = (TextView) findViewById(R.id.d1);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        t6 = (TextView) findViewById(R.id.t6);
        t7 = (TextView) findViewById(R.id.t7);

        s1 = (EditText) findViewById(R.id.s1);
        s2 = (EditText) findViewById(R.id.s2);
        s3 = (EditText) findViewById(R.id.s3);
        s4 = (EditText) findViewById(R.id.s4);
        s5 = (EditText) findViewById(R.id.s5);
        s6 = (EditText) findViewById(R.id.s6);
        s7 = (EditText) findViewById(R.id.s7);
        tup = (AppCompatButton) findViewById(R.id.tup);
        mAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference();
        day = new ArrayList<>();
        sub = new ArrayList<>();
        fsub = new ArrayList<>();
        day.add("Monday");
        day.add("Tuesday");
        day.add("Wednesday");
        day.add("Thursday");
        day.add("Friday");
        day.add("Saturday");
        i=0;
        cn.setText(getIntent().getStringExtra("Classt"));
        d1.setText(day.get(i));
        Log.d("Check444", day.get(i));
        tup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (s1.getText().toString().isEmpty() || s2.getText().toString().isEmpty() || s3.getText().toString().isEmpty() || s4.getText().toString().isEmpty() || s5.getText().toString().isEmpty() || s6.getText().toString().isEmpty()) {
                        Toast.makeText(TeditActivity.this, "Add subjects", Toast.LENGTH_SHORT).show();
                    } else {
                        HashMap<String, Object> tt = new HashMap<String, Object>();
                        tt.put(t1.getText().toString(), s1.getText().toString().toUpperCase());
                        tt.put(t2.getText().toString(), s2.getText().toString().toUpperCase());
                        tt.put(t3.getText().toString(), s3.getText().toString().toUpperCase());
                        tt.put(t4.getText().toString(), s4.getText().toString().toUpperCase());
                        tt.put(t5.getText().toString(), s5.getText().toString().toUpperCase());
                        tt.put(t6.getText().toString(), s6.getText().toString().toUpperCase());
                        tt.put(t7.getText().toString(), s7.getText().toString().toUpperCase());
                        sub.add(s1.getText().toString().toUpperCase());
                        sub.add(s2.getText().toString().toUpperCase());
                        sub.add(s3.getText().toString().toUpperCase());
                        sub.add(s4.getText().toString().toUpperCase());
                        sub.add(s5.getText().toString().toUpperCase());
                        sub.add(s6.getText().toString().toUpperCase());
                        sub.add(s7.getText().toString().toUpperCase());
                        userref.child("TimeTables").child(getIntent().getStringExtra("Classt")).child("Actual").child(day.get(i)).updateChildren(tt)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            userref.child("TimeTables").child(getIntent().getStringExtra("Classt")).child("Changed").child(day.get(i)).updateChildren(tt)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(TeditActivity.this, day.get(i) + " schedule uploaded", Toast.LENGTH_SHORT).show();
                                                                if(day.get(i).equals("Saturday")) {
                                                                    Log.d("Checkday", day.get(i));
                                                                    finish();
                                                                } else {
                                                                    d1.setText(day.get(++i));
                                                                    Log.d("Checkday2", d1.getText().toString());
                                                                    s1.setText(null);s2.setText(null);s3.setText(null);s4.setText(null);s5.setText(null);s6.setText(null);s7.setText(null);
                                                                }
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(TeditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TeditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

    }
}