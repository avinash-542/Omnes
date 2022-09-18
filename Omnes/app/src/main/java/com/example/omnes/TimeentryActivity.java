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
import android.widget.ImageView;
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

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class TimeentryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userref,useref;
    private String currentUserID, rr;
    private List<String> cls = new ArrayList<>();
    private RecyclerView rc;
    private FirebaseRecyclerOptions<Leaves> options;
    private FirebaseRecyclerAdapter<Leaves, TimeentryActivity.ListHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeentry);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rr = getIntent().getStringExtra("Role");
        userref = FirebaseDatabase.getInstance().getReference("Classes");
        useref = FirebaseDatabase.getInstance().getReference();
        rc = (RecyclerView) findViewById(R.id.recy1);
        rc.setLayoutManager(new LinearLayoutManager(this));
        options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();

        adapter =
                new FirebaseRecyclerAdapter<Leaves, TimeentryActivity.ListHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull TimeentryActivity.ListHolder listHolder, int i, @NonNull Leaves permissions) {
                        final String croom = getRef(i).getKey();
                        userref.child(croom).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    if(snapshot.hasChildren()) {
                                        for(DataSnapshot data : snapshot.getChildren()) {
                                            cls.add(croom);
                                            listHolder.cname.setText(croom);
                                            listHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent i = new Intent(listHolder.itemView.getContext(), TeditActivity.class);
                                                    i.putExtra("Classt", croom);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    listHolder.itemView.getContext().startActivity(i);
                                                }
                                            });
                                        }
                                        Log.d("Check111", cls.toString());
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
                    public TimeentryActivity.ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_names,parent,false);
                        return new TimeentryActivity.ListHolder(view);
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

    public static class ListHolder extends RecyclerView.ViewHolder {

        TextView cname;

        public ListHolder(@NonNull View itemView) {
            super(itemView);
            cname = (TextView) itemView.findViewById(R.id.tname);
        }
    }
}