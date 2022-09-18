package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class TfacultyActivity extends AppCompatActivity {
private TextView classname1, day1, sub1, sub2, sub3, sub4, sub5, sub6, sub7, time1, time2, time3, time4, time5, time6, time7;
private DatabaseReference userref, tokref;
private FirebaseAuth mAuth;
private String day, subject;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_tfaculty);
classname1 = (TextView) findViewById(R.id.classname12);
day1 = (TextView) findViewById(R.id.day12);
sub1 = (TextView) findViewById(R.id.sub12);
sub2 = (TextView) findViewById(R.id.sub22);
sub3 = (TextView) findViewById(R.id.sub32);
sub4 = (TextView) findViewById(R.id.sub42);
sub5 = (TextView) findViewById(R.id.sub52);
sub6 = (TextView) findViewById(R.id.sub62);
sub7 = (TextView) findViewById(R.id.sub72);
time1 = (TextView) findViewById(R.id.time12);
time2 = (TextView) findViewById(R.id.time22);
time3 = (TextView) findViewById(R.id.time32);
time4 = (TextView) findViewById(R.id.time42);
time5 = (TextView) findViewById(R.id.time52);
time6 = (TextView) findViewById(R.id.time62);
time7 = (TextView) findViewById(R.id.time72);
mAuth = FirebaseAuth.getInstance();
userref = FirebaseDatabase.getInstance().getReference();
tokref = FirebaseDatabase.getInstance().getReference("Tokens");
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
if(LocalDate.now().getDayOfWeek().name().equals("MONDAY")) day = "Monday";
else if(LocalDate.now().getDayOfWeek().name().equals("TUESDAY")) day = "Tuesday";
else if(LocalDate.now().getDayOfWeek().name().equals("WEDNESDAY")) day = "Wednesday";
else if(LocalDate.now().getDayOfWeek().name().equals("THURSDAY")) day = "Thursday";
else if(LocalDate.now().getDayOfWeek().name().equals("FRIDAY")) day = "Friday";
else if(LocalDate.now().getDayOfWeek().name().equals("SATURDAY")) day = "Saturday";
else if(LocalDate.now().getDayOfWeek().name().equals("SUNDAY")) day = "Sunday";
}
//getIntent().getStringExtra("ClassroomT") = getIntent().getStringExtra("ClassroomT");
Log.d("Checkcr", getIntent().getStringExtra("ClassroomT"));

userref.child("Classes").child(getIntent().getStringExtra("ClassroomT")).child("Faculty").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot1) {
if(snapshot1.exists()) {
if(snapshot1.hasChild("Subject")) {
subject = snapshot1.child("Subject").getValue().toString();
classname1.setText(getIntent().getStringExtra("ClassroomT"));
day1.setText(day);
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day)
.addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.hasChildren()) {
Log.d("Checktt", snapshot.getKey());
sub1.setText(snapshot.child(time1.getText().toString()).getValue().toString());
if(snapshot.child(time1.getText().toString()).getValue().toString().equals(subject)) {
sub1.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time1.getText().toString()).setValue("Substitute")
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Available",getIntent().getStringExtra("ClassroomT") + " has a substitute class at " + time1.getText().toString(),getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
if(snapshot.child(time1.getText().toString()).getValue().toString().equals("Substitute")) {
sub1.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time1.getText().toString()).setValue(subject)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Replaced",getIntent().getStringExtra("ClassroomT") + "'s substitute class at " + time1.getText().toString() + " has been replaced by " + subject,getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
sub2.setText(snapshot.child(time2.getText().toString()).getValue().toString());
if(snapshot.child(time2.getText().toString()).getValue().toString().equals(subject)) {
sub2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time2.getText().toString()).setValue("Substitute")
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Available",getIntent().getStringExtra("ClassroomT") + " has a substitute class at " + time2.getText().toString(),getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
if(snapshot.child(time2.getText().toString()).getValue().toString().equals("Substitute")) {
sub2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time2.getText().toString()).setValue(subject)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Replaced",getIntent().getStringExtra("ClassroomT") + "'s substitute class at " + time2.getText().toString()+ " has been replaced by " + subject,getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
sub3.setText(snapshot.child(time3.getText().toString()).getValue().toString());
if(snapshot.child(time3.getText().toString()).getValue().toString().equals(subject)) {
sub3.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time3.getText().toString()).setValue("Substitute")
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Available",getIntent().getStringExtra("ClassroomT") + " has a substitute class at " + time3.getText().toString(),getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
if(snapshot.child(time3.getText().toString()).getValue().toString().equals("Substitute")) {
sub2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time3.getText().toString()).setValue(subject)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Replaced",getIntent().getStringExtra("ClassroomT") + "'s substitute class at " + time3.getText().toString()+ " has been replaced by " + subject,getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
sub4.setText(snapshot.child(time4.getText().toString()).getValue().toString());
if(snapshot.child(time4.getText().toString()).getValue().toString().equals(subject)) {
sub4.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time4.getText().toString()).setValue("Substitute")
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {

FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Available",getIntent().getStringExtra("ClassroomT") + " has a substitute class at " + time4.getText().toString(),getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
if(snapshot.child(time4.getText().toString()).getValue().toString().equals("Substitute")) {
sub2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time4.getText().toString()).setValue(subject)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Replaced",getIntent().getStringExtra("ClassroomT") + "'s substitute class at " + time4.getText().toString()+ " has been replaced by " + subject,getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
sub5.setText(snapshot.child(time5.getText().toString()).getValue().toString());
if(snapshot.child(time5.getText().toString()).getValue().toString().equals(subject)) {
sub5.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time5.getText().toString()).setValue("Substitute")
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Available",getIntent().getStringExtra("ClassroomT") + " has a substitute class at " + time5.getText().toString(),getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
if(snapshot.child(time5.getText().toString()).getValue().toString().equals("Substitute")) {
sub2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time5.getText().toString()).setValue(subject)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Replaced",getIntent().getStringExtra("ClassroomT") + "'s substitute class at " + time5.getText().toString()+ " has been replaced by " + subject,getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
sub6.setText(snapshot.child(time6.getText().toString()).getValue().toString());
if(snapshot.child(time6.getText().toString()).getValue().toString().equals(subject)) {
sub6.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time6.getText().toString()).setValue("Substitute")
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Available",getIntent().getStringExtra("ClassroomT") + " has a substitute class at " + time6.getText().toString(),getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
if(snapshot.child(time6.getText().toString()).getValue().toString().equals("Substitute")) {
sub2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time6.getText().toString()).setValue(subject)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Replaced",getIntent().getStringExtra("ClassroomT") + "'s substitute class at " + time6.getText().toString()+ " has been replaced by " + subject,getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
sub7.setText(snapshot.child(time7.getText().toString()).getValue().toString());
if(snapshot.child(time7.getText().toString()).getValue().toString().equals(subject)) {
sub7.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time7.getText().toString()).setValue("Substitute")
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
Log.d("CheckToken", ds.getKey());
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Available",getIntent().getStringExtra("ClassroomT") + " has a substitute class at " + time7.getText().toString(),getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
if(snapshot.child(time7.getText().toString()).getValue().toString().equals("Substitute")) {
sub2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("TimeTables").child(getIntent().getStringExtra("ClassroomT")).child("Changed").child(day).child(time7.getText().toString()).setValue(subject)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
tokref.child(getIntent().getStringExtra("ClassroomT")).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot3) {
if(snapshot3.exists()) {
for(DataSnapshot ds : snapshot3.getChildren()) {
if(!ds.child("UID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
FirebaseNotificationSender fns = new FirebaseNotificationSender(ds.getKey(), "Substitute Replaced",getIntent().getStringExtra("ClassroomT") + "'s substitute class at " + time7.getText().toString()+ " has been replaced by " + subject,getApplicationContext(),TfacultyActivity.this);
fns.SendNotifications();
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
});
}
});
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
}
}
