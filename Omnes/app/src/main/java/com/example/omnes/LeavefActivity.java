package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeavefActivity extends AppCompatActivity {
private RecyclerView recly;
private DatabaseReference userref, useref;
private FirebaseAuth mAuth;
private FirebaseRecyclerOptions<Leaves> options;
private FirebaseRecyclerAdapter<Leaves, LeavefActivity.ListHolder> adapter;
private String classroom, imu, day;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_leavef);
userref = FirebaseDatabase.getInstance().getReference().child("Leaves");
useref = FirebaseDatabase.getInstance().getReference();
mAuth = FirebaseAuth.getInstance();
recly = (RecyclerView) findViewById(R.id.recly11);
recly.setLayoutManager(new LinearLayoutManager(this));
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
day = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
}
useref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists()) {
if (snapshot.hasChild("Incharge to")) {
classroom = snapshot.child("Incharge to").getValue().toString();
}
}
}
@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref, Leaves.class).build();
adapter = new FirebaseRecyclerAdapter<Leaves, ListHolder>(options) {
@Override
protected void onBindViewHolder(@NonNull ListHolder holder, int position, @NonNull Leaves model) {
final String croom = getRef(position).getKey();
userref.child(croom).addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists()) {
if (snapshot.hasChildren()) {
for(DataSnapshot data1 : snapshot.getChildren()) {
if (data1.child("Incharge").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
holder.roll.setText(data1.child("RollNo").getValue().toString());
holder.reason.setText(data1.child("Reason").getValue().toString());
useref.child("Users").child(data1.child("UID").getValue().toString()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if (snapshot.exists()) {
if (snapshot.hasChild("image")) {
imu = snapshot.child("image").getValue().toString();
Picasso.get().load(imu).into(holder.pro);
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});

if (data1.child("isGranted").getValue().toString().equals("1")) {
holder.ref.setVisibility(View.GONE);
holder.gr.setText("Granted");
holder.gr.setClickable(false);
} else if (data1.child("isGranted").getValue().toString().equals("0")) {
holder.gr.setVisibility(View.GONE);
holder.ref.setText("Rejected");
holder.ref.setClickable(false);
} else if (data1.child("isGranted").getValue().toString().equals("2")) {
holder.gr.setVisibility(View.VISIBLE);
holder.gr.setText("Grant");
holder.gr.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
useref.child("Leaves").child(day).child(data1.child("RollNo").getValue().toString()).child("isGranted")
.setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
if (task.isSuccessful()) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(data1.child("Token_ID").getValue().toString(), "Leave Request", "Your Request has been granted", getApplicationContext(), LeavefActivity.this);
fns.SendNotifications();
}
}
});
}
});
holder.ref.setVisibility(View.VISIBLE);
holder.ref.setText("Reject");
holder.ref.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
useref.child("Leaves").child(day).child(data1.child("RollNo").getValue().toString()).child("isGranted")
.setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
if (task.isSuccessful()) {
useref.child("Attendance").child(day).child(mAuth.getCurrentUser().getUid()).child(classroom).child(data1.child("RollNo").getValue().toString()).child("tookLeave").setValue("0");
FirebaseNotificationSender fns = new FirebaseNotificationSender(data1.child("Token_ID").getValue().toString(), "Leave Request", "Your Request has been Rejected", getApplicationContext(), LeavefActivity.this);
fns.SendNotifications();
}
}
});
}
});
}
}
}
} else {
holder.itemView.setVisibility(View.GONE);
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
View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_layout,parent,false);
return new ListHolder(view);
}
};
recly.setAdapter(adapter);

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
CircleImageView pro;
TextView roll, reason;
AppCompatButton gr, ref;
public ListHolder(@NonNull View itemView) {
super(itemView);
pro = (CircleImageView) itemView.findViewById(R.id.reim);
roll = (TextView) itemView.findViewById(R.id.reid);
reason = (TextView) itemView.findViewById(R.id.reres);
gr = (AppCompatButton) itemView.findViewById(R.id.regr);
ref = (AppCompatButton) itemView.findViewById(R.id.reref);
}
}
}