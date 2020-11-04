package com.example.StepApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.StepApp.Models.User;
import com.example.StepApp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserEditActivity extends AppCompatActivity {

    private CircleImageView imageProfile;
    private ImageView close;
    private TextView save;
    private TextView changePhoto;
    private MaterialEditText fullname;
    private MaterialEditText userMail;
    private MaterialEditText userPassword;
    private MaterialEditText userBirth;
    private MaterialEditText userHeight;
    private MaterialEditText userWeight;

    private FirebaseUser fUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        imageProfile = (CircleImageView) findViewById(R.id.imageProfile);
        save = (TextView) findViewById(R.id.saveChanges);
        close = (ImageView) findViewById(R.id.close);
        changePhoto = (TextView) findViewById(R.id.changePicture);
        fullname = (MaterialEditText) findViewById(R.id.fullName);
        userPassword = (MaterialEditText) findViewById(R.id.userPass);
        userMail = (MaterialEditText) findViewById(R.id.userMail);
        userBirth = (MaterialEditText) findViewById(R.id.userBDate);
        userHeight = (MaterialEditText) findViewById(R.id.userHeight);
        userWeight = (MaterialEditText) findViewById(R.id.userWeight);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child("StepAppDB").child("Uploads");

        FirebaseDatabase.getInstance().getReference().child("StepAppDB").child("Users").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        fullname.setText(Objects.requireNonNull(user).getUserName());
                        userMail.setText(user.getEmail());
                        userPassword.setText(user.getPassword());
                        Picasso.get().load(user.getImageurl()).into(imageProfile);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
                        .start(UserEditActivity.this);
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
                        .start(UserEditActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", fullname.getText().toString().trim());
        map.put("email", userMail.getText().toString().trim());
        map.put("password", userPassword.getText().toString().trim());
        map.put("birthDate", userBirth.getText().toString().trim());
        map.put("Height", userHeight.getText().toString().trim());
        map.put("Weight", userWeight.getText().toString().trim());

        FirebaseDatabase.getInstance().getReference().child("StepAppDB").child("Users")
                .child(fUser.getUid()).updateChildren(map);
        uploadImage();
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Updating your information, please wait...");
        pd.show();

        if (mImageUri != null) {
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpeg");
            fileRef.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null){
                            FirebaseDatabase.getInstance().getReference().child("StepAppDB").child("Users").child(fUser.getUid())
                                .child("Imageurl").setValue(downloadUri.toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("myDebug", "onSuccess: ");
                                            pd.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("myDebug", "onFailure: "+e.getMessage());
                                }
                            });
                        }
                    }
                }
            });
//            uploadTask = fileRef.putFile(mImageUri);
//            uploadTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        Log.d("myDebug", "then: "+task.getException().getMessage());
//                        throw task.getException();
//                    }
//                    return fileRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//
//                    if (task.isSuccessful()) {
//                        Uri downloadUri = task.getResult();
//                        String Url = downloadUri.toString();
//                        FirebaseDatabase.getInstance().getReference().child("StepAppDB").child("Users").child(fUser.getUid())
//                                .child("Imageurl").setValue(Url);
//                        pd.dismiss();
//                    } else {
//                        Toast.makeText(UserEditActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
                resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            Toast.makeText(this, "Update Succeeded!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }


}