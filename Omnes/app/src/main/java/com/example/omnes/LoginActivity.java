package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
private EditText uname,pass;
private ImageView login;
private TextView fp;
private SpinKitView skv;
private CardView v1;
private String token_id;
private ImageView logo, show;
private List<String> clsti;
private FirebaseAuth mAuth;
private DatabaseReference userref, tokref;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_login);
FirebaseMessaging.getInstance().subscribeToTopic("all");
mAuth = FirebaseAuth.getInstance();
userref = FirebaseDatabase.getInstance().getReference().child("Users");
tokref = FirebaseDatabase.getInstance().getReference().child("Tokens");
logo = (ImageView) findViewById(R.id.info);
clsti = new ArrayList<>();
logo.setOnLongClickListener(new View.OnLongClickListener() {
@Override
public boolean onLongClick(View view) {
AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
builder1.setTitle("OMNES");
builder1.setMessage("This is a classroom automation system for both faculty and students.");
builder1.setCancelable(true);

builder1.setPositiveButton(
"OK",
new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int id) {
dialog.cancel();
}
});

AlertDialog alert11 = builder1.create();
alert11.show();
return false;
}
});
InitializeComponents();
show.setOnTouchListener(new View.OnTouchListener() {
@Override
public boolean onTouch(View view, MotionEvent motionEvent) {
switch (motionEvent.getAction()) {
case MotionEvent.ACTION_DOWN:
pass.setInputType(InputType.TYPE_CLASS_TEXT);
show.setImageResource(R.drawable.hide);
break;
case MotionEvent.ACTION_UP:
pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
show.setImageResource(R.drawable.show);
break;
}
return true;
}
});
skv.setVisibility(View.GONE);
login.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
AllowUserToLogin(1);
}

});
fp.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
changePassword();
}
});
}

@Override
protected void onStart() {
super.onStart();
FirebaseUser user = mAuth.getCurrentUser();
updateUI(user);

}

private void updateUI(FirebaseUser user) {
if(user != null) {
String umail = user.getEmail();
int index = umail.indexOf('@');
umail = umail.substring(0,index);
if(umail.substring(0,1).equals("1") || umail.substring(0,1).equals("2") || umail.substring(0,1).equals("3")){
sendUserToDashboard("Student");
} else {
sendUserToDashboard("Faculty");
}
Log.d("Check", umail.substring(0,1));
} else if(user == null){
AllowUserToLogin(2);
}
}

private void sendUserToDashboard(String role) {
Intent i = null;
i = new Intent(LoginActivity.this, DashboardfActivity.class);
i.putExtra("Role", role);
i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(i);
}

private void changePassword() {
Intent i = new Intent(LoginActivity.this, ForgotActivity.class);
startActivity(i);
}

private void AllowUserToLogin(int i) {
String user = uname.getText().toString();
String pword = pass.getText().toString();

if(user.isEmpty()){
if(i == 1)
Toast.makeText(this, "Username can't be empty",Toast.LENGTH_LONG).show();
}
if(pword.isEmpty()) {
if(i == 1)
Toast.makeText(this, "Password is empty", Toast.LENGTH_LONG).show();
} else {
v1.setVisibility(View.VISIBLE);
skv.setVisibility(View.VISIBLE);


mAuth.signInWithEmailAndPassword(user,pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull Task<AuthResult> task) {

if(task.isSuccessful()) {
String userid = user.replace("@mrcet.ac.in","");
HashMap<String, Object> data = new HashMap<>();
String role ="";
data.put("UserID",userid);
data.put("email",mAuth.getCurrentUser().getEmail().toString());
data.put("UID", mAuth.getCurrentUser().getUid());
data.put("Token_ID", FirebaseMessaging.getInstance().getToken());
if(user.substring(0,1).equals("1") || user.substring(0,1).equals("2") || user.substring(0,1).equals("3")) {
data.put("Role", "Student");
role = "Student";
} else {
data.put("Role", "Faculty");
role = "Faculty";
}
String finalRole = role;
userref.child(mAuth.getCurrentUser().getUid()).updateChildren(data)
.addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {

if(task.isSuccessful()) {
userref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists()) {
if(snapshot.hasChild("Incharge to")) {
HashMap<String, Object> le = new HashMap<>();
clsti.add(snapshot.child("Incharge to").getValue().toString());
le.put("UID", mAuth.getCurrentUser().getUid());
le.put("Token_ID", snapshot.child("Token_ID").child("result").getValue().toString());
token_id = snapshot.child("Token_ID").child("result").getValue().toString();
userref.child("Leaves").child(snapshot.child("Incharge to").getValue().toString()).child("Incharge")
.updateChildren(le).addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
if(task.isSuccessful()){

}
}
});
}
if(snapshot.hasChild("Otherc")) {
String[] te = snapshot.child("Otherc").getValue().toString().split(",");
for(int i=0; i<te.length; i++)
clsti.add(te[i]);
}
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});

for(String a : clsti) {
userref.child("Classes").child(a).child("Faculty").child(mAuth.getCurrentUser().getUid()).child("Token_ID")
.setValue(token_id).addOnSuccessListener(new OnSuccessListener<Void>() {
@Override
public void onSuccess(Void unused) {

}
});
}
sendUserToDashboard(finalRole);

}

}
});




} else {
v1.setVisibility(View.GONE);
skv.setVisibility(View.GONE);
String message = task.getException().toString();
Toast.makeText(LoginActivity.this,"Error : " + message, Toast.LENGTH_LONG).show();

}

}
});



}
}

private void InitializeComponents() {
uname = (EditText) findViewById(R.id.username);
pass = (EditText) findViewById(R.id.password);
login = (ImageView) findViewById(R.id.loginbtn);
fp = (TextView) findViewById(R.id.fp);
show = (ImageView) findViewById(R.id.show);
skv = (SpinKitView) findViewById(R.id.spin_kit);
v1 = (CardView) findViewById(R.id.load);
}
}