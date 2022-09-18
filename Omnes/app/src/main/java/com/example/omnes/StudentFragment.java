package com.example.omnes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StudentFragment extends Fragment {
    private CardView atten, notes, tt, req;
    private View list;
    private FirebaseAuth mauth;
    private DatabaseReference userref;
    private String classr, iam;
    private List<String> rrr = new ArrayList<>();

    public StudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        list = inflater.inflate(R.layout.fragment_student, container, false);
        atten = (CardView) list.findViewById(R.id.atten);
        notes = (CardView) list.findViewById(R.id.reso);
        tt = (CardView) list.findViewById(R.id.timetable);
        req = (CardView) list.findViewById(R.id.req);
        mauth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());

        return list;
    }

    @Override
    public void onStart() {
        super.onStart();

        atten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AttenoActivity.class);
                userref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        i.putExtra("classstu", snapshot.child("Class").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                startActivity(i);
            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ResourcesActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), TstudentActivity.class);
                i.putExtra("Role", "Student");
                startActivity(i);
            }
        });

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), LeavesActivity.class);
                i.putExtra("Role", "Student");
                startActivity(i);
            }
        });

    }
}