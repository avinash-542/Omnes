package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttenpageActivity extends AppCompatActivity {
    private DatabaseReference userref, atten, ref;
    private FirebaseAuth mAuth;
    private String sub, date, time;
    private List<String> sturolls, stunames, stuimgs;
    private attenAdapter adapter;
    SwipeFlingAdapterView flingAdapterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attenpage);
        flingAdapterView=findViewById(R.id.swipe);
        mAuth = FirebaseAuth.getInstance();
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        try {
            Date userDate = parser.parse(formatter.format(currentDate));
            if (userDate.after(parser.parse("09:30")) && userDate.before(parser.parse("10:20"))) {
                time = "09:30";
            } else if (userDate.after(parser.parse("10:20")) && userDate.before(parser.parse("11:10"))) {
                time = "10:20";
            } else if (userDate.after(parser.parse("11:10")) && userDate.before(parser.parse("12:00"))) {
                time = "11:10";
            } else if (userDate.after(parser.parse("12:00")) && userDate.before(parser.parse("12:50"))) {
                time = "12:00";
            } else if (userDate.after(parser.parse("13:30")) && userDate.before(parser.parse("14:20"))) {
                time = "13:30";
            } else if (userDate.after(parser.parse("14:20")) && userDate.before(parser.parse("15:10"))) {
                time = "14:20";
            } else if (userDate.after(parser.parse("15:10")) && userDate.before(parser.parse("16:00"))) {
                time = "15:10";
            } else {
                finish();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stunames = new ArrayList<>();
        stuimgs = new ArrayList<>();
        sturolls = new ArrayList<>();
        String  cls = getIntent().getStringExtra("AttenClass");
        ref = FirebaseDatabase.getInstance().getReference();
        atten = FirebaseDatabase.getInstance().getReference().child("Attendance").child(date).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(cls);
        userref = FirebaseDatabase.getInstance().getReference().child("Classes").child(getIntent().getStringExtra("AttenClass"));

        userref.child("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Log.d("CP1", snapshot.getKey());
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Log.d("CP2", ds.getKey());
                        if(ds.child("Role").getValue().toString().equals("Student")) {
                            if(ds.child("Class").getValue().toString().equals(getIntent().getStringExtra("AttenClass"))) {
                                sturolls.add(ds.child("RollNo").getValue().toString());
                                stunames.add(ds.child("fname").getValue().toString());
                                if(ds.hasChild("image")) {
                                    stuimgs.add(ds.child("image").getValue().toString());
                                } else {
                                    stuimgs.add("None");
                                }
                            }
                        }
                    }
                    Log.d("Check details", sturolls.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("CheckAtten1", stunames.toString());


        int count = stunames.size();

        adapter = new attenAdapter(this,stunames, sturolls, stuimgs,getLayoutInflater());

        flingAdapterView.setAdapter(adapter);
        flingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {


            String s ;
            int absent=0, present=0, leave=0;
            HashMap<String, Object> attenup = new HashMap<String, Object>();
            HashMap<String,Object> total = new HashMap<String, Object>();

            @Override
            public void removeFirstObjectInAdapter() {
                s = sturolls.get(0);
                stuimgs.remove(0);
                sturolls.remove(0);
                stunames.remove(0);
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onLeftCardExit(Object o) {

                userref.child("Faculty").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            ref.child("Leaves").child(date).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    if(snapshot1.exists()) {
                                        if(snapshot1.hasChild(s)) {
                                            if(snapshot1.child(s).hasChild("Left") && snapshot1.child(s).child("Left").getValue().toString().equals("1")) {
                                                leave++;
                                                attenup.put(s,"Leave");
                                            } else {
                                                absent++;
                                                attenup.put(s,"Absent");
                                            }
                                        } else {
                                            absent++;
                                            attenup.put(s,"Absent");
                                        }
                                    } else {
                                        absent++;
                                        attenup.put(s,"Absent");
                                    }
                                    total.put("Absent",absent);
                                    total.put("Present", present);
                                    total.put("onLeave", leave);
                                    total.put("Total", present+absent+leave);
                                    String subj = snapshot.child("Subject").getValue().toString();
                                    attenup.put("Subject", subj);
                                    attenup.put("Count", total);
                                    Log.d("CheckAbsent", attenup.toString());
                                    ref.child("Attendance").child(getIntent().getStringExtra("AttenClass")).child(date).child(time).updateChildren(attenup);

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

            @Override
            public void onRightCardExit(Object o) {
                userref.child("Faculty").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            ref.child("Leaves").child(date).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    if(snapshot1.exists()) {
                                        if(snapshot1.hasChild(s)) {
                                            if(snapshot1.child(s).hasChild("Left") && snapshot1.child(s).child("Left").getValue().toString().equals("1")) {
                                                leave++;
                                                attenup.put(s,"Leave");
                                            } else {
                                                present++;
                                                attenup.put(s,"Present");
                                            }
                                        } else {
                                            present++;
                                            attenup.put(s,"Present");
                                        }
                                    } else {
                                        present++;
                                        attenup.put(s,"Present");
                                    }
                                    total.put("Absent",absent);
                                    total.put("Present", present);
                                    total.put("onLeave", leave);
                                    total.put("Total", present+absent+leave);
                                    String subj = snapshot.child("Subject").getValue().toString();
                                    attenup.put("Subject", subj);
                                    attenup.put("Count", total);
                                    Log.d("CheckPresent", attenup.toString());
                                    ref.child("Attendance").child(getIntent().getStringExtra("AttenClass")).child(date).child(time).updateChildren(attenup);

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


            @Override
            public void onAdapterAboutToEmpty(int i) {
                if(i==0) {
                    finish();
                }
            }

            @Override
            public void onScroll(float v) {

            }



        });


        flingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int i, Object o) {
                Toast.makeText(AttenpageActivity.this, "data is "+sturolls.get(i)+" "+sturolls.size(),Toast.LENGTH_SHORT).show();
            }
        });

        Button like,dislike;

        like=findViewById(R.id.like);
        dislike=findViewById(R.id.dislike);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingAdapterView.getTopCardListener().selectRight();
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingAdapterView.getTopCardListener().selectLeft();
            }
        });
    }
}