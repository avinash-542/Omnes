package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AttenfActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userref,useref;
    private TextView stv;
    private String currentUserID, rr;
    private String [] all;
    private List<String> cls = new ArrayList<>();
    private RecyclerView rc;
    private int a=0;
    private AppCompatButton inreport;
    private FirebaseRecyclerOptions<Leaves> options;
    private FirebaseRecyclerAdapter<Leaves, AttenfActivity.Holder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attenf);
        inreport = (AppCompatButton) findViewById(R.id.facreport);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rr = getIntent().getStringExtra("Role");
        userref = FirebaseDatabase.getInstance().getReference("Classes");
        useref = FirebaseDatabase.getInstance().getReference();
        useref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Incharge to")) {
                    inreport.setVisibility(View.VISIBLE);
                    inreport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(AttenfActivity.this, AtteniActivity.class);
                            i.putExtra("inClass", snapshot.child("Incharge to").getValue().toString());
                            startActivity(i);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        rc = (RecyclerView) findViewById(R.id.recyatten);
        rc.setLayoutManager(new LinearLayoutManager(this));
        useref.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            if (snapshot.hasChild("Incharge to")) {
                                cls.add(snapshot.child("Incharge to").getValue().toString());
                                Log.d("Checking54", cls.toString());
                            }
                            if(snapshot.hasChild("Otherc")) {
                                String[] all = snapshot.child("Otherc").getValue().toString().toUpperCase().split(",");
                                for(int i=0; i<all.length; i++) {
                                    cls.add(all[i]);
                                    Log.d("C", cls.toString() + "=" + all[i]);
                                }
                            } else {
                                Toast.makeText(AttenfActivity.this, "No others", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();

        adapter =
                new FirebaseRecyclerAdapter<Leaves, AttenfActivity.Holder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull AttenfActivity.Holder listHolder, int i, @NonNull Leaves permissions) {
                        final String croom  = getRef(i).getKey();
                            userref.child(croom).child("Faculty").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                            listHolder.cname.setText(croom);
                                            listHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent i = new Intent(listHolder.itemView.getContext(), AttenpageActivity.class);
                                                    i.putExtra("AttenClass", croom);
                                                    i.putExtra("AttenSub", snapshot.child(mAuth.getCurrentUser().getUid()).child("Subject").getValue().toString());
                                                    i.putExtra("Iam", "Faculty");
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    listHolder.itemView.getContext().startActivity(i);
                                                }
                                            });
                                        } else {
                                            listHolder.itemView.setVisibility(View.GONE);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                    }

                    @NonNull
                    @Override
                    public AttenfActivity.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_names,parent,false);
                        return new AttenfActivity.Holder(view);
                    }


                };

        rc.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null) {
            adapter.stopListening();
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView cname;
        public Holder(@NonNull View itemView) {
            super(itemView);
            cname = itemView.findViewById(R.id.tname);
        }
    }
}