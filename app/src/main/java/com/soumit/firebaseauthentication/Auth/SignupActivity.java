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
import com.soumit.firebaseauthentication.Models.Patient;
import com.soumit.firebaseauthentication.R;

/**
 * Created by SOUMIT on 10/14/2017.
 */

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = SignupActivity.class.getSimpleName();
    private EditText inputEmail, inputPassword, inputConfPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    ImageView proPic;
    private String userId;

    private Spinner spinner;
    private static final String[]paths = {"Patient", "Doctor"};

    //Patient DB info
    private EditText inputNameDb, inputEmailDb, inputAgeDb, inputAddressDb;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

//        //extra validation
//        FirebaseUser currentUser = auth.getCurrentUser();
//
//        if(currentUser != null)
//            userId = auth.getCurrentUser().getUid();
        //---------------------------------------------

        //Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignupActivity.this,
                android.R.layout.simple_spinner_dropdown_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfPassword = (EditText) findViewById(R.id.confpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        proPic = (ImageView) findViewById(R.id.pro_pic);

        //Patient DB info
        inputNameDb = (EditText) findViewById(R.id.name);
        inputEmailDb = (EditText) findViewById(R.id.email);
        inputAgeDb = (EditText) findViewById(R.id.age);
        inputAddressDb = (EditText) findViewById(R.id.address);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
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
                String confpass = inputConfPassword.getText().toString().trim();

                final String name = inputNameDb.getText().toString().trim();
                String emailDb = inputEmailDb.getText().toString().trim();
                final String age = inputAgeDb.getText().toString().trim();
                final String address = inputAddressDb.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isValidEmailAddress(email)){
                    Toast.makeText(getApplicationContext(), "Email address not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(confpass) || !password.equals(confpass) ){
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
                            Toast.makeText(SignupActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void onAuthSuccess(FirebaseUser currentUser){
        String username = inputNameDb.getText().toString();
        String age = inputAgeDb.getText().toString();
        String address = inputAddressDb.getText().toString();

        writeNewUser(currentUser.getUid(), username, currentUser.getEmail(), age, address);

        sendVerificationEmail();

        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        auth.signOut();
        finish();

//        sendVerificationEmail();

    }


    private void writeNewUser(String userId, String name,
                              String email, String age, String address){

        Patient user = new Patient(name, email, age, address);

        mDatabase.child("users")
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


    private void sendEmail(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Email sent!");
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignupActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /* Email verification method */

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent

                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }

    //


    private void createUser(String name, String email, String age, String address) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
//            userId = mFirebaseDatabase.push().getKey();

//            userId = auth.getCurrentUser().getUid();
                    //extra validation
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null)
            userId = auth.getCurrentUser().getUid();
        }

        Patient user = new Patient(name, email, age, address);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mDatabase.child("users")
                 .child(userId)
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateUser(String name, String email, String age, String address) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(userId).child("name").setValue(name);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabase.child(userId).child("email").setValue(email);

        if(!TextUtils.isEmpty(age))
            mFirebaseDatabase.child(userId).child("age").setValue(age);

        if(!TextUtils.isEmpty(address))
            mFirebaseDatabase.child(userId).child("address").setValue(address);
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

                break;
            case 1:
                startActivity(new Intent(SignupActivity.this, DoctorSignup.class));
//                finish();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}









