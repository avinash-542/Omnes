package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omnes.databinding.ActivityDashboardfBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardfActivity extends AppCompatActivity {
private FirebaseAuth mAuth;
private DatabaseReference userref,tokref;
private String CurrentUserId, CurrentUser, rol, role, token_id, day;
private TextView username;
private ImageView logout;
private SwitchCompat admin;
private CircleImageView pp;
private AppCompatButton pedit;
private List<String> clsti;


@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_dashboardf);
clsti = new ArrayList<>();
admin = (SwitchCompat) findViewById(R.id.adminv);
FirebaseMessaging.getInstance().subscribeToTopic("all");
mAuth = FirebaseAuth.getInstance();
userref = FirebaseDatabase.getInstance().getReference();
tokref = FirebaseDatabase.getInstance().getReference().child("Tokens");
role = getIntent().getStringExtra("Role");
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
if(LocalDate.now().minusDays(1).getDayOfWeek().name().equals("MONDAY")) day = "Monday";
else if(LocalDate.now().minusDays(1).getDayOfWeek().name().equals("TUESDAY")) day = "Tuesday";
else if(LocalDate.now().minusDays(1).getDayOfWeek().name().equals("WEDNESDAY")) day = "Wednesday";
else if(LocalDate.now().minusDays(1).getDayOfWeek().name().equals("THURSDAY")) day = "Thursday";
else if(LocalDate.now().minusDays(1).getDayOfWeek().name().equals("FRIDAY")) day = "Friday";
else if(LocalDate.now().minusDays(1).getDayOfWeek().name().equals("SATURDAY")) day = "Saturday";
else if(LocalDate.now().minusDays(1).getDayOfWeek().name().equals("SUNDAY")) day = "Saturday";
}
userref.child("TimeTables").addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists()) {
if (snapshot.hasChildren()) {
for(DataSnapshot data : snapshot.getChildren()) {
String dtt = data.child("Actual").child(day).getValue().toString();//.substring(1,data.child("Actual").getValue().toString().length()-1);
String[] kvp = dtt.substring(1,dtt.length()-1).split(", ");
HashMap<String, Object> chg = new HashMap<String, Object>();
for(String pair : kvp) {
String[] etr = pair.split("=");
userref.child("TimeTables").child(data.getKey()).child("Changed").child(day).child(etr[0]).setValue(etr[1]);
}
}
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});

userref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot1) {
if(snapshot1.exists()) {
role = snapshot1.child("Role").getValue().toString();
token_id = snapshot1.child("Token_ID").child("result").getValue().toString();
if(snapshot1.hasChild("Incharge to")) {
clsti.add(snapshot1.child("Incharge to").getValue().toString());
}
if(snapshot1.hasChild("Otherc")) {
String[] clcc = snapshot1.child("Otherc").getValue().toString().split(",");
for(int i=0; i<clcc.length-1;i++) {
clsti.add(clcc[i]);
}
}
for(int i=0; i< clsti.size(); i++) {
tokref.child(clsti.get(i)).child(token_id).child("UID").setValue(mAuth.getCurrentUser().getUid());
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});
initialize();
pedit.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
sendToProfile();
}
});

if(role.equals("Student")) {
getSupportFragmentManager().beginTransaction().add(R.id.place, new StudentFragment()).commit();
Log.d("Check", "Stufag");
} else if(role.equals("Faculty")) {
getSupportFragmentManager().beginTransaction().add(R.id.place, new FacultyFragment()).commit();
Log.d("Check", "Facfag");
checkRole(mAuth);
userref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.hasChild("isAdmin") && snapshot.child("isAdmin").getValue().toString().equals("1")) {
rol = "Admin";
admin.setVisibility(View.VISIBLE);
admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
@Override
public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
if (b) {
getSupportFragmentManager().beginTransaction().replace(R.id.place, new AdminFragment()).commit();

}
else {
getSupportFragmentManager().beginTransaction().replace(R.id.place, new FacultyFragment()).commit();
}
}
});
} else
admin.setVisibility(View.GONE);
}
@Override
public void onCancelled(@NonNull DatabaseError error) {
}
});
}

logout.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
Toast.makeText(DashboardfActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
mAuth.signOut();
sendUserToLogin();
}
});
}

private void sendToProfile() {
Intent i  = new Intent(getApplicationContext(), ProfileActivity.class);
i.putExtra("Role", role);
startActivity(i);
}
private void checkRole(FirebaseAuth mAuth) {
userref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(!snapshot.hasChild("isAdmin")) {
HashMap<String , Object> role = new HashMap<String, Object>();
role.put("isAdmin", 1);
role.put("isHod", 0);
role.put("isIncharge",0);
rol = "Admin";
userref.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(role)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
if(task.isSuccessful())
admin.setVisibility(View.VISIBLE);
else
admin.setVisibility(View.GONE);
}
})
.addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
Toast.makeText(DashboardfActivity.this, "Error : " + e, Toast.LENGTH_SHORT).show();
}
});
} else if(snapshot.child("isAdmin").getValue().toString().equals("1")) {
admin.setVisibility(View.VISIBLE);
if (snapshot.child("isAdmin").getValue().toString().equals("1") ) {
rol = "Admin";
} else if(snapshot.child("isHod").getValue().toString().equals("1")) {
rol = "HOD";
}
} else {
rol = "Faculty";
}
}
@Override
public void onCancelled(@NonNull DatabaseError error) {
}
});
}

@Override
protected void onStart() {
super.onStart();
if (mAuth.getCurrentUser() != null) {
updateName();
}
}
private void updateName() {
userref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if (snapshot.hasChild("fname")) {
username.setText(snapshot.child("fname").getValue().toString());
if(snapshot.hasChild("image")) {
Picasso.get().load(snapshot.child("image").getValue().toString()).into(pp);
}
} else if(snapshot.hasChild("UserID")) {
sendUserToUpdate();
}
}
@Override
public void onCancelled(@NonNull DatabaseError error) {
}
});
}
private void sendUserToUpdate() {
Intent i = new Intent(DashboardfActivity.this, ProfileActivity.class);
i.putExtra("Role", role);
i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
startActivity(i);
}
private void initialize() {
CurrentUserId = mAuth.getCurrentUser().getUid();
username = (TextView) findViewById(R.id.user1);
logout = (ImageView) findViewById(R.id.logout);
pp = (CircleImageView) findViewById(R.id.pp);
pedit = (AppCompatButton) findViewById(R.id.pedit);
}
private void sendUserToLogin() {
Intent i = new Intent(DashboardfActivity.this, LoginActivity.class);
i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(i);
}
}
