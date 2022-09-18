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
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TstudentActivity extends AppCompatActivity {
    private TextView classname1, day1, sub1, sub2, sub3, sub4, sub5, sub6, sub7, time1, time2, time3, time4, time5, time6, time7;
    private DatabaseReference userref;
    private FirebaseAuth mAuth;
    private String day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tstudent);
        classname1 = (TextView) findViewById(R.id.classname1);
        day1 = (TextView) findViewById(R.id.day1);
        sub1 = (TextView) findViewById(R.id.sub1);
        sub2 = (TextView) findViewById(R.id.sub2);
        sub3 = (TextView) findViewById(R.id.sub3);
        sub4 = (TextView) findViewById(R.id.sub4);
        sub5 = (TextView) findViewById(R.id.sub5);
        sub6 = (TextView) findViewById(R.id.sub6);
        sub7 = (TextView) findViewById(R.id.sub7);
        time1 = (TextView) findViewById(R.id.time1);
        time2 = (TextView) findViewById(R.id.time2);
        time3 = (TextView) findViewById(R.id.time3);
        time4 = (TextView) findViewById(R.id.time4);
        time5 = (TextView) findViewById(R.id.time5);
        time6 = (TextView) findViewById(R.id.time6);
        time7 = (TextView) findViewById(R.id.time7);

        mAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if(LocalDate.now().getDayOfWeek().name().equals("MONDAY")) day = "Monday";
            else if(LocalDate.now().getDayOfWeek().name().equals("TUESDAY")) day = "Tuesday";
            else if(LocalDate.now().getDayOfWeek().name().equals("WEDNESDAY")) day = "Wednesday";
            else if(LocalDate.now().getDayOfWeek().name().equals("THURSDAY")) day = "Thursday";
            else if(LocalDate.now().getDayOfWeek().name().equals("FRIDAY")) day = "Friday";
            else if(LocalDate.now().getDayOfWeek().name().equals("SATURDAY")) day = "Saturday";
            else if(LocalDate.now().getDayOfWeek().name().equals("SUNDAY")) day = "Sunday";
        }
        userref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                if(snapshot1.hasChild("Class")) {
                    Log.d("Checkst", snapshot1.child("Class").getKey().toString());
                    classname1.setText(snapshot1.child("Class").getValue().toString());
                    day1.setText(day);
                    userref.child("TimeTables").child(snapshot1.child("Class").getValue().toString()).child("Changed").child(day)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChildren()) {
                                        Log.d("Checktt", snapshot.getKey());

                                        sub1.setText(snapshot.child(time1.getText().toString()).getValue().toString());
                                        sub2.setText(snapshot.child(time2.getText().toString()).getValue().toString());
                                        sub3.setText(snapshot.child(time3.getText().toString()).getValue().toString());
                                        sub4.setText(snapshot.child(time4.getText().toString()).getValue().toString());
                                        sub5.setText(snapshot.child(time5.getText().toString()).getValue().toString());
                                        sub6.setText(snapshot.child(time6.getText().toString()).getValue().toString());
                                        sub7.setText(snapshot.child(time7.getText().toString()).getValue().toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}