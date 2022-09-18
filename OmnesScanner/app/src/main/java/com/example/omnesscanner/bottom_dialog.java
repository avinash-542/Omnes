package com.example.omnesscanner;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class bottom_dialog extends BottomSheetDialogFragment {
private TextView details, status;
private ImageView close;
private DatabaseReference userref;
private String rollno, classname, date1, day;

@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
View view = inflater.inflate(R.layout.bottom_dialog, container, false);
details = view.findViewById(R.id.details);
status  = view.findViewById(R.id.status);
close = view.findViewById(R.id.close);
userref = FirebaseDatabase.getInstance().getReference();
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
day = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
}

details.setText("Roll No : " + rollno + "\nClass : " + classname + "\nDate : " + date1);

userref.child("Leaves").child(date1).child(rollno).addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists()) {
if(date1.equals(day) && snapshot.child("isGranted").getValue().toString().equals("1")) {
if(snapshot.child("Left").getValue().toString().equals("0")) {
status.setText("Granted");
status.setBackgroundResource(R.drawable.granted);
status.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
userref.child("Leaves").child(date1).child(rollno).child("Left").setValue("1");
dismiss();
}
});
} else {
status.setText("Left Campus");
status.setBackgroundResource(R.drawable.others);
}
} else if(date1.equals(day) && snapshot.child("isGranted").getValue().toString().equals("0")){
status.setText("Rejected");
status.setBackgroundResource(R.drawable.refused);
} else if(!date1.equals(day)) {
status.setText("Expired");
status.setBackgroundResource(R.drawable.expired);
}
} else {
status.setText("No Such requests");
status.setBackgroundResource(R.drawable.nsr);
}
}

@Override
public void onCancelled(@NonNull DatabaseError error) {

}
});




close.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
dismiss();
}
});
return view;
}

public void fetchData(String roll, String classroom, String date) {
ExecutorService executorService = Executors.newSingleThreadExecutor();
Handler handler = new Handler(Looper.getMainLooper());
executorService.execute(new Runnable() {
@Override
public void run() {
rollno = roll;
classname = classroom;
date1 = date;
}
});
}
}
