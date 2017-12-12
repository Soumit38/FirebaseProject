package com.soumit.firebaseauthentication.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soumit.firebaseauthentication.Models.Patient;
import com.soumit.firebaseauthentication.R;

/**
 * Created by SOUMIT on 11/7/2017.
 */

public class EditProfile extends AppCompatActivity{

    private static final String TAG = "EditProfile";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    TextView et_name, et_email, et_address;
    Button btnUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        et_name = (TextView) findViewById(R.id.et_name);
        et_email = (TextView) findViewById(R.id.et_email);
        et_address = (TextView) findViewById(R.id.et_address);
        btnUpdate = (Button) findViewById(R.id.update_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        final String uid = mAuth.getCurrentUser().getUid();

        Toast.makeText(EditProfile.this,
                "Current uid: " + uid, Toast.LENGTH_LONG).show();

        setProfileFields(uid);

        final String name = et_name.getText().toString();
        final String email = et_email.getText().toString();
        final String address = et_address.getText().toString();

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(name)){
                    mDatabase.child("users").child(uid).child("name").setValue(name);
                }

                if(!TextUtils.isEmpty(email)){
                    mDatabase.child("users").child(uid).child("email").setValue(email);
                }

                //if(!TextUtils.isEmpty(address)){
                    mDatabase.child("users").child(uid).child("address").setValue(address);
                //}

                Toast.makeText(EditProfile.this, "Profile updated !", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//              Patient user = dataSnapshot.getValue(Patient.class);
//
//              tv_name.setText(user.name);
//              tv_email.setText(user.email);
//              tv_address.setText(user.address);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // [START_EXCLUDE]
//                Toast.makeText(EditProfile.this, "Failed to load post.",
//                        Toast.LENGTH_SHORT).show();
//                // [END_EXCLUDE]
//            }
//        };
//        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(postListener);
//        // [END post_value_event_listener]

    }


    private void setProfileFields(String uid){
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
    }


}
