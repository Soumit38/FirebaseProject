package com.soumit.firebaseauthentication.Profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.soumit.firebaseauthentication.Models.Doctor;
import com.soumit.firebaseauthentication.Models.Patient;
import com.soumit.firebaseauthentication.R;

import java.io.IOException;

/**
 * Created by SOUMIT on 10/20/2017.
 */

public class EditProfileAndUploadImage extends AppCompatActivity {

    private static final String TAG = "EditProfileAndUploadIma";

    String Storage_Path = "All_image_uploads/";
    String Database_Path = "All_image_uploads_database";

    Button ChooseButton, UploadButton;
//    EditText ImageName ;
    ImageView SelectImage;

    Uri FilePathUri;
    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    int Image_Request_Code = 7;
    ProgressDialog progressDialog ;

    //
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    EditText et_name, et_email, et_address;
    EditText et_qualifications, et_time;

    Button btnUpdate;

    ImageUploadInfo imageUploadInfo;

    String picUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_and_image);

        LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        LinearLayout emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        LinearLayout qualificationsLayout = (LinearLayout) findViewById(R.id.qualificationsLayout);
        LinearLayout addressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        LinearLayout timeLayout = (LinearLayout) findViewById(R.id.timeLayout);

        Intent received_intent = getIntent();
        final String temp_id = received_intent.getStringExtra("edit_id");

        if(temp_id != null && temp_id.equals("patient")){
            qualificationsLayout.setVisibility(View.GONE);
            timeLayout.setVisibility(View.GONE);
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        ChooseButton = (Button) findViewById(R.id.ButtonChooseImage);
        UploadButton = (Button) findViewById(R.id.ButtonUploadImage);

        imageUploadInfo = new ImageUploadInfo();

//        ImageName = (EditText) findViewById(R.id.ImageNameEditText);
        SelectImage = (ImageView) findViewById(R.id.ShowImageView);

        progressDialog = new ProgressDialog(EditProfileAndUploadImage.this);

        //
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_address = (EditText) findViewById(R.id.et_address);
        et_qualifications = (EditText) findViewById(R.id.et_qualifications);
        et_time = (EditText) findViewById(R.id.et_time);

        btnUpdate = (Button) findViewById(R.id.update_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        final String uid = mAuth.getCurrentUser().getUid();

        Toast.makeText(EditProfileAndUploadImage.this,
                "Current uid: " + uid, Toast.LENGTH_LONG).show();

        setProfileFields(uid);

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String name = et_name.getText().toString();
                final String email = et_email.getText().toString();
                final String address = et_address.getText().toString();
                final String qualifications = et_qualifications.getText().toString();
                final String visiting_hours = et_time.getText().toString();
                final String imgUrl = imageUploadInfo.getImageURL();


                Toast.makeText(EditProfileAndUploadImage.this, uid, Toast.LENGTH_SHORT).show();

                if(temp_id != null && temp_id.equals("patient")){

                    if(!TextUtils.isEmpty(name)){
                        mDatabase.child("users").child(uid).child("name").setValue(name);
                        //Toast.makeText(EditProfileAndUploadImage.this,uid+" "+name , Toast.LENGTH_SHORT).show();
                    }

                    if(!TextUtils.isEmpty(email)){
                        mDatabase.child("users").child(uid).child("email").setValue(email);
                    }

                    if(!TextUtils.isEmpty(address)){
                        mDatabase.child("users").child(uid).child("address").setValue(address);
                    }

                    mDatabase.child("users").child(uid).child("imageUrl").setValue(picUrl);

                }else{

                    if(!TextUtils.isEmpty(name)){
                        mDatabase.child("doctors").child(uid).child("name").setValue(name);
                        //Toast.makeText(EditProfileAndUploadImage.this,uid+" "+name , Toast.LENGTH_SHORT).show();
                    }

                    if(!TextUtils.isEmpty(email)){
                        mDatabase.child("doctors").child(uid).child("email").setValue(email);
                    }

                    if(!TextUtils.isEmpty(address)){
                        mDatabase.child("doctors").child(uid).child("address").setValue(address);
                    }

                    mDatabase.child("doctors").child(uid).child("qualifications").setValue(qualifications);

                    mDatabase.child("doctors").child(uid).child("visiting_hours").setValue(visiting_hours);

                    mDatabase.child("doctors").child(uid).child("imageUrl").setValue(picUrl);
                }



                Toast.makeText(EditProfileAndUploadImage.this, "imageUrl: " + picUrl, Toast.LENGTH_LONG).show();

                Toast.makeText(EditProfileAndUploadImage.this, "Profile updated !", Toast.LENGTH_SHORT).show();

            }
        });

        //

        ChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);

            }
        });


        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UploadImageFileToFirebaseStorage();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code &&
                resultCode == RESULT_OK && data != null
                && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                SelectImage.setImageBitmap(bitmap);

                ChooseButton.setText("Image Selected");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    public void UploadImageFileToFirebaseStorage() {

        if (FilePathUri != null) {

            progressDialog.setTitle("Image is Uploading...");

            progressDialog.show();
//
//            StorageReference storageReference2nd =
//                    storageReference.child(Storage_Path +
//                            System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            StorageReference storageReference2nd =
                    storageReference.child(Storage_Path +
                            FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + GetFileExtension(FilePathUri) );

            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                            String TempImageName = ImageName.getText().toString().trim();
                            String TempImageName = "profile picture";

                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(),
                                    "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo =
                                    new ImageUploadInfo(TempImageName, taskSnapshot.getDownloadUrl().toString());

                            picUrl = taskSnapshot.getDownloadUrl().toString();


                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();

                            String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                            databaseReference.child(userKey).setValue(imageUploadInfo);
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            progressDialog.dismiss();

                            Toast.makeText(EditProfileAndUploadImage.this,
                                    exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        }
        else {

            Toast.makeText(EditProfileAndUploadImage.this,
                    "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

    //
    private void setProfileFields(String uid){
        Intent received_intent = getIntent();
        String temp_id = received_intent.getStringExtra("edit_id");

        if(temp_id != null && temp_id.equals("patient")){
            mDatabase.child("users")
                    .child(uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Patient user = dataSnapshot.getValue(Patient.class);

                            // Check for null
                            if (user == null) {
                                Log.e(TAG, "User data is null!");
                                return;
                            }

                            Log.d(TAG, "User data is changed!" +
                                    user.name + ", " + user.email + "," + user.age + "," + user.address);

                            et_name.setText(user.name);
                            et_email.setText(user.email);
                            et_address.setText(user.address);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.e(TAG, "Failed to read user", error.toException());
                        }
                    });
        }else{
            mDatabase.child("doctors")
                    .child(uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Doctor user = dataSnapshot.getValue(Doctor.class);

                            // Check for null
                            if (user == null) {
                                Log.e(TAG, "User data is null!");
                                return;
                            }

                            Log.d(TAG, "User data is changed!" +
                                    user.name + ", " + user.email + "," + user.address);

                            et_name.setText(user.name);
                            et_email.setText(user.email);
                            et_address.setText(user.address);
                            et_qualifications.setText(user.qualifications);
//                            et_time.setText(user.time);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.e(TAG, "Failed to read user", error.toException());
                        }
                    });

        }
    }




}








