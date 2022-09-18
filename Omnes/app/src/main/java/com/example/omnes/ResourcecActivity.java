package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcecActivity extends AppCompatActivity {
private TextView chek;
private FloatingActionButton up;
private RecyclerView rec;
private FirebaseAuth mAuth;
private DatabaseReference userref, useref;
private String currentUserID;
private ProgressDialog loadingBar;
private Uri fileUri;
private FirebaseRecyclerOptions<Leaves> options;
private FirebaseRecyclerAdapter<Leaves, ResourcecActivity.ListHolder> adapter;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_resourcec);
chek = (TextView) findViewById(R.id.chek);
mAuth = FirebaseAuth.getInstance();
currentUserID = mAuth.getCurrentUser().getUid();
useref = FirebaseDatabase.getInstance().getReference();
userref = FirebaseDatabase.getInstance().getReference().child("Notes");
up = (FloatingActionButton) findViewById(R.id.uploadn);
loadingBar = new ProgressDialog(this);
rec = (RecyclerView) findViewById(R.id.filess);
rec.setLayoutManager(new LinearLayoutManager(this));
chek.setText(getIntent().getStringExtra("Classroom"));

options = new FirebaseRecyclerOptions.Builder<Leaves>().setQuery(userref,Leaves.class).build();

adapter = new FirebaseRecyclerAdapter<Leaves, ListHolder>(options) {
@Override
protected void onBindViewHolder(@NonNull ListHolder holder, int i, @NonNull Leaves model) {
final String file = getRef(i).getKey();
userref.child(file).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.hasChildren()) {
if(snapshot.child("senderID").getValue().toString().equals(currentUserID) && snapshot.child("toClass").getValue().toString().equals(getIntent().getStringExtra("Classroom"))) {
holder.name.setText(snapshot.child("filename").getValue().toString());
holder.itemView.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(snapshot.child("fileurl").getValue().toString()));
i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
holder.itemView.getContext().startActivity(i);
}
});
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
View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_design,parent,false);
return new ListHolder(view);
}
};
rec.setAdapter(adapter);

up.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
Intent i = new Intent();
i.setAction(Intent.ACTION_GET_CONTENT);
i.setType("*/*");
startActivityForResult(i,101);
}
});

}

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
super.onActivityResult(requestCode, resultCode, data);

if(requestCode == 101 && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
loadingBar.setTitle("Sending file");
loadingBar.setMessage("Please wait...");
loadingBar.setCanceledOnTouchOutside(false);
loadingBar.show();
fileUri = data.getData();
StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Class files");

String filen = getFilename(fileUri);
int rin = filen.indexOf(".");
String filen2 = filen.substring(0,rin);
final StorageReference filepath = storageReference.child(filen);
filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
@Override
public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
@Override
public void onSuccess(Uri uri) {
String downloadUrl = uri.toString();

Map notedetails = new HashMap();
notedetails.put("filename",filen );
notedetails.put("fileurl",downloadUrl);
notedetails.put("senderID", currentUserID);
notedetails.put("toClass", getIntent().getStringExtra("Classroom"));

useref.child("Notes").child(filen2).updateChildren(notedetails);
loadingBar.dismiss();

}
}).addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
loadingBar.dismiss();
Toast.makeText(ResourcecActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
}
});


}
}).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
@Override
public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
double p = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
loadingBar.setMessage((int) p + " % Uploading...");
}
});
}
}

private String getFilename(Uri filepath) {
String result = null;
if (filepath.getScheme().equals("content")) {
Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
try {
if (cursor != null && cursor.moveToFirst()) {
result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
}
} finally {
cursor.close();
}
}
if (result == null) {
result = filepath.getPath();
int cut = result.lastIndexOf('/');
if (cut != -1) {
result = result.substring(cut + 1);
}
}
return result;
}

public static class ListHolder extends RecyclerView.ViewHolder {
ImageView file;
TextView name;
CardView filec;

public ListHolder(@NonNull View itemView) {
super(itemView);
file = itemView.findViewById(R.id.senderview);
name = itemView.findViewById(R.id.sendt);
}
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
}