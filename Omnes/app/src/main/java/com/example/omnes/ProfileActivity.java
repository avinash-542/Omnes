package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private  CircleImageView pro;

    private Button UpdateAccountSettings, cp, up;
    private EditText userName, userStatus, mnumber, permail, op, np, np1;

    private String currentUserID,role,retrievesStatus;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;

    private HashMap<String, Object> classdetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();
        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(allDetailCheck())
                UpdateSettings();
                else
                    Toast.makeText(ProfileActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
            }
        });
        RetrieveUserInfo();
        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPic();
            }
        });

        cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                op.setVisibility(View.VISIBLE);
                np.setVisibility(View.VISIBLE);
                np1.setVisibility(View.VISIBLE);
                up.setVisibility(View.VISIBLE);
                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(op.getText().toString().isEmpty() || np.getText().toString().isEmpty() || np1.getText().toString().isEmpty()) {
                            Toast.makeText(ProfileActivity.this, "Enter password details", Toast.LENGTH_SHORT).show();
                        } else {
                            if(np1.getText().toString().equals(np.getText().toString())) {
                                AuthCredential cred = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), op.getText().toString());
                                mAuth.getCurrentUser().reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mAuth.getCurrentUser().updatePassword(np.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(ProfileActivity.this, "Error updating", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ProfileActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                                        op.setVisibility(View.GONE);
                                                        np.setVisibility(View.GONE);
                                                        np1.setVisibility(View.GONE);
                                                        up.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Authentication Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void uploadPic() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    private void initialize() {
        pro = (CircleImageView) findViewById(R.id.pro);
        UpdateAccountSettings = (Button) findViewById(R.id.upload);
        userName = (EditText) findViewById(R.id.fname);
        userStatus = (EditText) findViewById(R.id.lname);
        mnumber = (EditText) findViewById(R.id.mnumber);
        permail = (EditText) findViewById(R.id.qualify);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        cp = (Button) findViewById(R.id.cp);
        op = (EditText) findViewById(R.id.op);
        np = (EditText) findViewById(R.id.np);
        np1 = (EditText) findViewById(R.id.np1);
        up = (Button) findViewById(R.id.up);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();


                StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");


                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();

                                        RootRef.child("Users").child(currentUserID).child("image")
                                                .setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            pro.setImageURI(resultUri);
                                                            Toast.makeText(ProfileActivity.this, "Image saved in database successfuly", Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        }
                                                        else{
                                                            String message = task.getException().toString();
                                                            Toast.makeText(ProfileActivity.this, "Error: " + message,Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();

                                                        }

                                                    }
                                                });

                                    }
                                });

                            }
                        });
            }
        }
    }

    private void UpdateSettings() {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("fname", userName.getText().toString());
            profileMap.put("lname", userStatus.getText().toString());
            profileMap.put("Phone",mnumber.getText().toString());
            profileMap.put("personal mail", permail.getText().toString());
            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                //SendUserToMainActivity();
                                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    private boolean allDetailCheck() {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        String mnum = mnumber.getText().toString();
        String qq = permail.getText().toString();
        if(!setUserName.isEmpty())
            if(!setStatus.isEmpty())
                if(!mnum.isEmpty())
                    if (!qq.isEmpty())
                        return true;
        return false;

    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("fname")))
                        {
                            String retrieveUserName = dataSnapshot.child("fname").getValue().toString();
                            String retrievesStatus = null;
                            if(dataSnapshot.hasChild("lname"))
                             retrievesStatus = dataSnapshot.child("lname").getValue().toString();
                            String mnu = dataSnapshot.child("Phone").getValue().toString();
                            String pe = dataSnapshot.child("personal mail").getValue().toString();
                            if((dataSnapshot.hasChild("image"))) {
                                String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(retrieveProfileImage).into(pro);
                        }
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                            mnumber.setText(mnu);
                            permail.setText(pe);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("fname")))
                        {
                            String retrieveUserName = dataSnapshot.child("fname").getValue().toString();
                            if(dataSnapshot.hasChild("lname")) {
                                retrievesStatus = dataSnapshot.child("lname").getValue().toString();
                            } else {
                                retrievesStatus = null;
                            }

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                            //Picasso.get().load(R.drawable.profile).into(pro);
                        }
                        else
                        {

                            Toast.makeText(ProfileActivity.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ProfileActivity.this, DashboardfActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}