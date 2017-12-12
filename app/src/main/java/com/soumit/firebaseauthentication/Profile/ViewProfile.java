package com.soumit.firebaseauthentication.Profile;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soumit.firebaseauthentication.Models.Doctor;
import com.soumit.firebaseauthentication.Models.Patient;
import com.soumit.firebaseauthentication.R;
import com.soumit.firebaseauthentication.Utils.ViewProfileFragment;

/**
 * Created by SOUMIT on 11/7/2017.
 */

public class ViewProfile extends AppCompatActivity{

    private static final String TAG = "ViewProfile";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    TextView tv_name, tv_email, tv_address;
    TextView tv_qualifications, tv_time;
    Button btnEditProfile;

    String glideImageUrl;

    ImageView propic;

    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        LinearLayout emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        LinearLayout qualificationsLayout = (LinearLayout) findViewById(R.id.qualificationsLayout);
        LinearLayout addressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        LinearLayout timeLayout = (LinearLayout) findViewById(R.id.timeLayout);

        Intent received_intent = getIntent();
        String temp_id = received_intent.getStringExtra("recognition_id");

        Toast.makeText(this, "message: "+temp_id, Toast.LENGTH_SHORT).show();

        if(temp_id != null && temp_id.equals("patient")){
            qualificationsLayout.setVisibility(View.GONE);
            timeLayout.setVisibility(View.GONE);
        }

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_qualifications = (TextView) findViewById(R.id.tv_qualifications);
        tv_time = (TextView) findViewById(R.id.tv_time);

        propic = (ImageView) findViewById(R.id.profile_picture);

        btnEditProfile = (Button) findViewById(R.id.edit_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        Toast.makeText(ViewProfile.this,
                "Current uid: " + uid, Toast.LENGTH_LONG).show();

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

                                  tv_name.setText(user.name);
                                  tv_email.setText(user.email);
                                  tv_address.setText(user.address);

                                  glideImageUrl = user.imageUrl;

                                  Glide.with(ViewProfile.this)
                                          .load(glideImageUrl)
                                          .into(propic);
                              }

                              @Override
                              public void onCancelled(DatabaseError error) {
                                  // Failed to read value
                                  Log.e(TAG, "Failed to read user", error.toException());
                              }
                          });
              }else{
                  Toast.makeText(this, "this is doc!", Toast.LENGTH_SHORT).show();
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

                                  tv_name.setText(user.name);
                                  tv_email.setText(user.email);
                                  tv_address.setText(user.address);
                                  tv_qualifications.setText(user.qualifications);
                                  tv_time.setText(user.time);

//                                  glideImageUrl = user.imageUrl;
//
//                                  Glide.with(ViewProfile.this)
//                                          .load(glideImageUrl)
//                                          .into(propic);
                              }

                              @Override
                              public void onCancelled(DatabaseError error) {
                                  // Failed to read value
                                  Log.e(TAG, "Failed to read user", error.toException());
                              }
                          });





              }


                btnEditProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewProfile.this, EditProfileAndUploadImage.class);

                        Intent received_intent = getIntent();
                        String temp_id = received_intent.getStringExtra("recognition_id");

                        if(temp_id != null && temp_id.equals("patient")){
                           intent.putExtra("edit_id", "patient");
                        }else if(temp_id != null && temp_id.equals("doctor")){
                            intent.putExtra("edit_id", "doctor");
                        }

                        startActivity(intent);
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
    }





}





















