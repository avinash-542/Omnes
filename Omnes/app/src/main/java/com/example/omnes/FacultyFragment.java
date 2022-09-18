package com.example.omnes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.time.LocalDate;
import java.util.HashMap;


public class FacultyFragment extends Fragment {

    private CardView atten, notes, tt, req;
    private View list;
    private DatabaseReference userref;
    private FirebaseAuth mAuth;
    private String day;
    public FacultyFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        list = inflater.inflate(R.layout.fragment_faculty, container, false);
        atten = (CardView) list.findViewById(R.id.atten);
        notes = (CardView) list.findViewById(R.id.reso);
        tt = (CardView) list.findViewById(R.id.timetable);
        req = (CardView) list.findViewById(R.id.req);
        mAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            day = LocalDate.now().minusDays(1).getDayOfWeek().name();
        }

        return list;
    }

    @Override
    public void onStart() {
        super.onStart();


        atten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AttenfActivity.class);
                i.putExtra("Role", "Faculty");

                startActivity(i);
            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ResourceActivity.class);
                i.putExtra("Role", "Faculty");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), TfacultysActivity.class);
                i.putExtra("Role", "Faculty");
                startActivity(i);
            }
        });
        userref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("isIncharge")) {
                    if(snapshot.child("isIncharge").getValue().toString().equals("1")) {
                        req.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getContext(), LeavefActivity.class);
                                i.putExtra("Role", "Faculty");
                                startActivity(i);
                            }
                        });
                    } else {
                        req.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}