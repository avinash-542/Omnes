package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

public class FacdetailsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userref,RootRef;
    private TextView stv;
    private String currentUserID, rr;
    private String [] all;
    private List<String> facnames, facuid, facid;
    private RecyclerView rec;
    private FirebaseRecyclerOptions<Leaves> options;
    private FirebaseRecyclerAdapter<Leaves, FacdetailsActivity.ListHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facdetails);
        rec = (RecyclerView) findViewById(R.id.faclist);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        rec.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    protected void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();
        adapter =
                new FirebaseRecyclerAdapter<Leaves, FacdetailsActivity.ListHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FacdetailsActivity.ListHolder listHolder, int i, @NonNull Leaves permissions) {
                        Log.d("Checking1", "_"+i+"_");
                        final String fac = getRef(i).getKey();
                        userref.child(fac).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    Log.d("Check11", snapshot.getValue().toString());
                                    if(snapshot.child("Role").getValue().toString().equals("Faculty")) {
                                        if(snapshot.hasChild("image")) {
                                            Picasso.get().load(snapshot.child("image").getValue().toString()).into(listHolder.files);
                                        }
                                        if(snapshot.hasChild("fname"))
                                            listHolder.names.setText(snapshot.child("fname").getValue().toString());
                                        else
                                            listHolder.names.setText(snapshot.child("UserID").getValue().toString());
                                        Log.d("Check123", listHolder.names.getText().toString());
                                        listHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent i = new Intent(getApplication(), FaceditActivity.class);
                                                i.putExtra("facUID", snapshot.child("UID").getValue().toString());
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                getApplication().startActivity(i);
                                            }
                                        });
                                    }  else {
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
                    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faclistv,parent,false);
                        return new ListHolder(view);
                    }

                };
        rec.setAdapter(adapter);
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
        TextView names;
        public ListHolder(@NonNull View itemView) {
            super(itemView);
            files = itemView.findViewById(R.id.uimage);
            names = itemView.findViewById(R.id.udet);
        }
    }
}