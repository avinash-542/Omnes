package com.example.omnes;

import static android.widget.AdapterView.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

public class ResourceActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userref,useref;
    private TextView stv;
    private String currentUserID, rr;
    private String [] all;
    private List<String> cls = new ArrayList<>();
    private RecyclerView rc;
    private int a=0;
    private FirebaseRecyclerOptions<Leaves> options;
    private FirebaseRecyclerAdapter<Leaves, ResourceActivity.Holder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rr = getIntent().getStringExtra("Role");
        Log.d("Check", rr);
        userref = FirebaseDatabase.getInstance().getReference("Classes");
        useref = FirebaseDatabase.getInstance().getReference();
        rc = (RecyclerView) findViewById(R.id.recy);
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
                                Toast.makeText(ResourceActivity.this, "No others", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();

        adapter =
                new FirebaseRecyclerAdapter<Leaves, ResourceActivity.Holder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull ResourceActivity.Holder listHolder, int i, @NonNull Leaves permissions) {

                            final String croom = getRef(i).getKey();
                            userref.child(croom).child("Faculty").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                            listHolder.cname.setText(croom);
                                            listHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent i = new Intent(listHolder.itemView.getContext(), ResourcecActivity.class);
                                                    i.putExtra("Classroom", croom);
                                                    i.putExtra("Iam", "Faculty");
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    listHolder.itemView.getContext().startActivity(i);
                                                }
                                            });
                                        } else {
                                            listHolder.itemView.setVisibility(GONE);
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
                    public ResourceActivity.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_names,parent,false);
                        return new ResourceActivity.Holder(view);
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