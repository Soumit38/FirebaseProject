package com.soumit.firebaseauthentication.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soumit.firebaseauthentication.Models.Doctor;
import com.soumit.firebaseauthentication.R;

import static com.soumit.firebaseauthentication.R.id.address;

/**
 * Created by SOUMIT on 10/20/2017.
 */

public class DoctorSignup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = DoctorSignup.class.getSimpleName();
    private EditText inputEmail, inputPassword, inputConfPass;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseAuth mAuth;
    ImageView proPic;
    private String userId;

    private Spinner spinner;
    private static final String[]paths = {"Doctor", "Patient"};

    //Patient DB info
    private EditText inputNameDb, inputEmailDb, inputAddressDb, inputQualificationsDb;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mDatabase;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_signup);

        auth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

//        //validation
//        FirebaseUser currentUser = auth.getCurrentUser();
//
//        if(currentUser != null)
//            userId = auth.getCurrentUser().getUid();

        //Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DoctorSignup.this,
                android.R.layout.simple_spinner_dropdown_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfPass = (EditText) findViewById(R.id.confpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        proPic = (ImageView) findViewById(R.id.pro_pic);

        //Patient DB info
        inputNameDb = (EditText) findViewById(R.id.name);
        inputEmailDb = (EditText) findViewById(R.id.email);
        inputQualificationsDb = (EditText) findViewById(R.id.qualifications);
        inputAddressDb = (EditText) findViewById(address);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("doctors");

        mAuth = FirebaseAuth.getInstance();

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DoctorSignup.this, ResetPasswordActivity.class));
//            }
//        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confpass = inputConfPass.getText().toString().trim();

                final String name = inputNameDb.getText().toString().trim();
                String emailDb = inputEmailDb.getText().toString().trim();
                final String qualifications = inputQualificationsDb.getText().toString().trim();
                final String address = inputAddressDb.getText().toString().trim();
                final String verificationCode = "1234";

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isValidEmailAddress(email)){
                    Toast.makeText(getApplicationContext(), "Email address not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(confpass) || !password.equals(confpass)){
                    Toast.makeText(getApplicationContext(), "Confirm password! ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                signUp();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }


    /**
     * from android quickstart
     */
    private void signUp(){
        Log.d(TAG, "signUp: ");

        progressBar.setVisibility(View.VISIBLE);

        String email = inputEmailDb.getText().toString();
        String password = inputPassword.getText().toString();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(DoctorSignup.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void onAuthSuccess(FirebaseUser currentUser){
        String username = inputNameDb.getText().toString();
        String email = inputEmailDb.getText().toString();
        String address = inputAddressDb.getText().toString();
        String qualifications = inputQualificationsDb.getText().toString();

        writeNewUser(currentUser.getUid(), username, currentUser.getEmail(), address, qualifications);

        startActivity(new Intent(DoctorSignup.this, LoginActivity.class));
        auth.signOut();
        finish();

//        sendVerificationEmail();

    }

    private void writeNewUser(String userId, String name,
                              String email, String address, String qualifications){

        Doctor user = new Doctor(name, email, address, qualifications);

        mDatabase.child("doctors")
                .child(userId)
                .setValue(user);

        Log.d(TAG, "writeNewUser: executed");
    }


    /* Checking if email address is valid or not */

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    //


    //Patient DB info

    private void createUser(String name, String email, String address,
                            String qualifications, String verificationCode) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
//        if (TextUtils.isEmpty(userId)) {
////            userId = mFirebaseDatabase.push().getKey();
//            userId = auth.getCurrentUser().getUid();
//        }

        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null)
            userId = auth.getCurrentUser().getUid();



        Doctor user = new Doctor(name, email, address, qualifications, verificationCode);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Doctor user = dataSnapshot.getValue(Doctor.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email +
                         "," + user.address + ", " + user.qualifications + ", " + user.verificationCode);


                // clear edit text
                inputEmail.setText("");
                inputNameDb.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position){

            case 0:

//                finish();
                break;
            case 1:
                startActivity(new Intent(DoctorSignup.this, SignupActivity.class));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}