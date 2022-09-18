package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class LeavesActivity extends AppCompatActivity {
private AppCompatButton req;
private RecyclerView recly;
private DatabaseReference userref, useref;
private FirebaseAuth mAuth;
private String toToken, toUid, classroom, roll, day, fromToken;
private FirebaseRecyclerOptions<Leaves> options;
private FirebaseRecyclerAdapter<Leaves, LeavesActivity.ListHolder> adapter;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_leaves);
req = (AppCompatButton) findViewById(R.id.reqq);
userref = FirebaseDatabase.getInstance().getReference().child("Leaves");
useref = FirebaseDatabase.getInstance().getReference();
mAuth = FirebaseAuth.getInstance();
recly = (RecyclerView) findViewById(R.id.recly);
HashMap<String, Object> up = new HashMap<String, Object>();
recly.setLayoutManager(new LinearLayoutManager(this));
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
day = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
}
useref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists()) {
classroom = snapshot.child("Class").getValue().toString();
roll = snapshot.child("RollNo").getValue().toString();
up.put("RollNo", snapshot.child("RollNo").getValue().toString());
up.put("Token_ID",snapshot.child("Token_ID").child("result").getValue().toString());
up.put("Incharge", snapshot.child("Incharge").getValue().toString());
up.put("Classroom", snapshot.child("Class").getValue().toString());
up.put("UID", mAuth.getCurrentUser().getUid());
useref.child("Users").child(snapshot.child("Incharge").getValue().toString()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot2) {
if(snapshot2.hasChild("Token_ID")) {
toToken = snapshot2.child("Token_ID").child("result").getValue().toString();
Log.d("CheckFlow22", toToken);
up.put("toToken_ID", toToken);
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
//Log.d("Checkdialogclick", toToken);
//Log.d("Checkdialogclick2", roll);
//Log.d("Checkdialogclick3", classroom);


options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();

adapter =
new FirebaseRecyclerAdapter<Leaves, ListHolder>(options) {

@Override
protected void onBindViewHolder(@NonNull ListHolder listHolder, int i, @NonNull Leaves permissions) {

userref.child(getRef(i).getKey()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists()) {
if(snapshot.hasChildren()) {
for(DataSnapshot data : snapshot.getChildren()) {
if (data.getKey().equals(roll)) {
listHolder.dates.setText(data.child("Date").getValue().toString());
if (data.child("Date").getValue().toString().equals(day)) {
if (data.child("isGranted").getValue().toString().equals("2")) {
listHolder.status1.setText("Pending");
} else if (data.child("isGranted").getValue().toString().equals("0")) {
listHolder.status1.setText("Rejected");
} else if (data.child("isGranted").getValue().toString().equals("1")) {
if(data.child("Left").getValue().toString().equals("0")) {
listHolder.status1.setText("Granted");
listHolder.itemView.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
String data = roll + "," + classroom + "," + day;
final ImageView qr = new ImageView(getApplicationContext());
qr.setPadding(10, 10, 10, 10);
QRGEncoder qrencode = new QRGEncoder(data, null, QRGContents.Type.TEXT, 500);
try {
Bitmap qrbit = qrencode.getBitmap();
qr.setImageBitmap(qrbit);

} catch (Exception e) {
}
AlertDialog.Builder builder = new AlertDialog.Builder(LeavesActivity.this, android.R.style.Theme_Holo_Dialog_NoActionBar);
builder.setView(qr);
builder.setCancelable(true);
builder.create().show();
}
});
} else if(data.child("Left").getValue().toString().equals("1")) {
listHolder.status1.setText("Left Campus");
}
}
} else {
if (data.child("isGranted").getValue().toString().equals("2")) {
listHolder.status1.setText("Pending");
} else if (data.child("isGranted").getValue().toString().equals("0")) {
listHolder.status1.setText("Rejected");
} else if (data.child("isGranted").getValue().toString().equals("1")) {
listHolder.status1.setText("Granted");
}
listHolder.status2.setVisibility(View.VISIBLE);
listHolder.status2.setText("Expired");
}
} else {
    listHolder.itemView.setVisibility(View.GONE);
}
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
public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaves_layout,parent,false);
return new ListHolder(view);
}


};

recly.setAdapter(adapter);





req.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
final EditText txtUrl = new EditText(getApplicationContext());
txtUrl.setBackgroundResource(R.drawable.input);
txtUrl.setPadding(10,10,10,10);
txtUrl.setTextColor(Color.WHITE);
AlertDialog.Builder builder = new AlertDialog.Builder(LeavesActivity.this, android.R.style.Theme_Holo_Dialog_NoActionBar);
builder.setIcon(R.drawable.ic_launcher_foreground);
builder.setTitle("Leave Request");
builder.setMessage("Brief your Reason for leave request");
builder.setView(txtUrl);
builder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int whichButton) {
Log.d("Checkdialogclick", toToken);
String url = txtUrl.getText().toString();
up.put("isGranted","2");
up.put("Left", "0");
up.put("Date", day);
up.put("Reason", txtUrl.getText().toString());
useref.child("Leaves").child(day).child(roll).updateChildren(up)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
if(task.isSuccessful()) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(toToken, "Leave Request","Your Student has requested for Leave",getApplicationContext(),LeavesActivity.this);
fns.SendNotifications();
}
}
});
}
}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int whichButton) {
}
}).show();
}
});



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

TextView dates, status1, status2;

public ListHolder(@NonNull View itemView) {
super(itemView);
dates = (TextView) itemView.findViewById(R.id.dates);
status1 = (TextView) itemView.findViewById(R.id.status1);
status2 = (TextView) itemView.findViewById(R.id.status2);
}
}
}