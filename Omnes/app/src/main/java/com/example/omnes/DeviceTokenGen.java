package com.example.omnes;

import android.widget.EditText;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class DeviceTokenGen extends FirebaseMessagingService {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Tokens");
    private String name;
    private EditText uname;

    //DeviceTokenGen(String name) { this.name = name; }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {

        super.onNewToken(s);
        ref.child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                HashMap<String, Object> data = new HashMap<String, Object>();
                data.put("Token_ID", s);


                //data.put("UserName", name);
                ref.child(s).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            String.format(getResources().getString(R.string.token), s);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }


}
