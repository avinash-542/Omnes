package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudetailsActivity extends AppCompatActivity {
    private EditText name, mail, lec;
    private Button ent;
    private ImageView info;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    private String inuid, currentUserID;
    private FirebaseAuth auth;
    private List<String> facnames, facuid, facid, facroll;
    private RecyclerView rec;
    private FirebaseRecyclerOptions<Leaves> options;
    private FirebaseRecyclerAdapter<Leaves, StudetailsActivity.ListHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studetails);

        facnames = new ArrayList<>();
        facuid =new ArrayList<>();
        facid = new ArrayList<>();
        facroll = new ArrayList<>();
        rec = (RecyclerView) findViewById(R.id.faclist2);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference("Users");
        rec.setLayoutManager(new LinearLayoutManager(this));

        options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();
        adapter =
                new FirebaseRecyclerAdapter<Leaves, StudetailsActivity.ListHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull StudetailsActivity.ListHolder listHolder, int i, @NonNull Leaves permissions) {
                        final String user = getRef(i).getKey();
                        userref.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    if(snapshot.hasChildren()) {
                                            if(snapshot.child("Role").getValue().toString().equals("Student")) {
                                                if(snapshot.hasChild("image")) {
                                                    Picasso.get().load(snapshot.child("image").getValue().toString()).into(listHolder.files);
                                                }
                                                if(snapshot.hasChild("fname"))
                                                    listHolder.names.setText(snapshot.child("fname").getValue().toString());
                                                else
                                                    listHolder.names.setText(snapshot.child("UserID").getValue().toString());
                                                listHolder.rolls.setText(snapshot.child("RollNo").getValue().toString());
                                                listHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent i = new Intent(listHolder.itemView.getContext(),StueditActivity.class);
                                                        i.putExtra("stuUID", snapshot.child("UID").getValue().toString());
                                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        listHolder.itemView.getContext().startActivity(i);
                                                    }
                                                });
                                            } else {
                                                listHolder.itemView.setVisibility(View.GONE);
                                            }

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
                    public StudetailsActivity.ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faclistv,parent,false);
                        return new StudetailsActivity.ListHolder(view);
                    }

                };
        rec.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!= null) {
            adapter.stopListening();
        }
    }



    public static class ListHolder extends RecyclerView.ViewHolder {
        CircleImageView files;
        TextView names, rolls;
        public ListHolder(@NonNull View itemView) {
            super(itemView);
            files = itemView.findViewById(R.id.uimage);
            names = itemView.findViewById(R.id.udet);
            rolls = itemView.findViewById(R.id.udet2);
        }
    }

}