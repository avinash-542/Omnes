package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResourcesActivity extends AppCompatActivity {
    private TextView chek;
    private RecyclerView rec;
    private FirebaseAuth mAuth;
    private DatabaseReference userref, useref;
    private String currentUserID, classroom;
    private ProgressDialog loadingBar;
    private FirebaseRecyclerOptions<Leaves> options;
    private FirebaseRecyclerAdapter<Leaves, ResourcesActivity.ListHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        chek = (TextView) findViewById(R.id.chek1);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        useref = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
        rec = (RecyclerView) findViewById(R.id.filess1);
        rec.setLayoutManager(new LinearLayoutManager(this));
        chek.setText(getIntent().getStringExtra("Classroom"));

        useref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Class")) {
                    classroom = snapshot.child("Class").getValue().toString();
                    chek.setText(snapshot.child("Class").getValue().toString());
                    Log.d("Check5", chek.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userref = FirebaseDatabase.getInstance().getReference().child("Notes");

        options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();

        adapter = new FirebaseRecyclerAdapter<Leaves, ResourcesActivity.ListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ResourcesActivity.ListHolder holder, int position, @NonNull Leaves model) {
                userref.child(getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()) {

                                    holder.name.setText(snapshot.child("filename").getValue().toString());
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(snapshot.child("fileurl").getValue().toString()));
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            holder.itemView.getContext().startActivity(i);
                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ResourcesActivity.ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_design,parent,false);
                return new ResourcesActivity.ListHolder(view);
            }
        };

        rec.setAdapter(adapter);
    }


    public static class ListHolder extends RecyclerView.ViewHolder {
        ImageView file;
        TextView name;
        CardView filec;

        public ListHolder(@NonNull View itemView) {
            super(itemView);
            file = itemView.findViewById(R.id.senderview);
            name = itemView.findViewById(R.id.sendt);
        }
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
}