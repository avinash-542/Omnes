package com.example.omnes;

import static android.view.View.GONE;

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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TfacultysActivity extends AppCompatActivity {
    private FirebaseAuth mauth;
    private DatabaseReference userref, useref;
    private List<String> classes, subjects, tokens;
    private String day, subb;
    private int a=0;
    private RecyclerView rc;
    private FirebaseRecyclerOptions<Leaves> options;
    private FirebaseRecyclerAdapter<Leaves, TfacultysActivity.Holder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tfacultys);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if(LocalDate.now().getDayOfWeek().name().equals("MONDAY")) day = "Monday";
            else if(LocalDate.now().getDayOfWeek().name().equals("TUESDAY")) day = "Tuesday";
            else if(LocalDate.now().getDayOfWeek().name().equals("WEDNESDAY")) day = "Wednesday";
            else if(LocalDate.now().getDayOfWeek().name().equals("THURSDAY")) day = "Thursday";
            else if(LocalDate.now().getDayOfWeek().name().equals("FRIDAY")) day = "Friday";
            else if(LocalDate.now().getDayOfWeek().name().equals("SATURDAY")) day = "Saturday";
            else if(LocalDate.now().getDayOfWeek().name().equals("SUNDAY")) day = "Sunday";
        }
        Log.d("Checkday", day);
        mauth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference().child("Classes");
        useref = FirebaseDatabase.getInstance().getReference();
        classes = new ArrayList<>();
        subjects = new ArrayList<>();
        tokens = new ArrayList<>();
        rc = (RecyclerView) findViewById(R.id.rec3);
        rc.setLayoutManager(new LinearLayoutManager(this));
        options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();

        adapter =
                new FirebaseRecyclerAdapter<Leaves, TfacultysActivity.Holder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull TfacultysActivity.Holder listHolder, int i, @NonNull Leaves permissions) {
                        final String croom = getRef(i).getKey();
                        Log.d("Croom", croom);
                        userref.child(croom).child("Faculty")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()) {
                                            if(snapshot.hasChild(mauth.getCurrentUser().getUid())) {
                                                listHolder.tname2.setText(croom);
                                                listHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent i = new Intent(listHolder.itemView.getContext(), TfacultyActivity.class);
                                                        i.putExtra("ClassroomT", croom);
                                                        startActivity(i);
                                                    }
                                                });
                                            }else {
                                                listHolder.itemView.setVisibility(GONE);
                                            }
                                        } else {
                                            listHolder.itemView.setVisibility(GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    }

                    @NonNull
                    @Override
                    public TfacultysActivity.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_names,parent,false);
                        return new TfacultysActivity.Holder(view);
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
        public TextView tname2, clt1, cls1;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tname2 = itemView.findViewById(R.id.tname);
        }
    }
}