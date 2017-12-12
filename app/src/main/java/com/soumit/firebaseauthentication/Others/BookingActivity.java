package com.soumit.firebaseauthentication.Others;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.soumit.firebaseauthentication.R;
import com.soumit.firebaseauthentication.Utils.BottomNavigationViewHelper;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity {

    private static final String TAG = "BookingActivity";

    private Context mContext = BookingActivity.this;

    ArrayList<String> idList = new ArrayList<>();

    String key, currentUserId, nameIndex;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mReference;

    FirebaseAuth mAuth;

    private TextView name, description, rating,
                     address, qualifications, visitingHours;
    private Bundle extras;

    static int count=0;

    private Button btnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        extras = getIntent().getExtras();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String nameSaved = preferences.getString("idCount", "");
        if(!nameSaved.equalsIgnoreCase(""))
        {
            count = Integer.valueOf(nameSaved);

        }

        name = (TextView) findViewById(R.id.ttl);
        address = (TextView) findViewById(R.id.showAddress);
        qualifications = (TextView) findViewById(R.id.showQualifications);
        visitingHours = (TextView) findViewById(R.id.showVisitingHours);
        description = (TextView) findViewById(R.id.desc);
        btnRequest = (Button) findViewById(R.id.request);

        if(extras != null){
//            nameIndex = extras.getString("name");
            name.setText(extras.getString("name"));
            description.setText(extras.getString("description"));
            address.setText(extras.getString("address"));
            qualifications.setText(extras.getString("qualifications"));
            visitingHours.setText(extras.getString("visitingHours"));
            key = extras.getString("userid");
//            Toast.makeText(mContext, extras.getString("userid"), Toast.LENGTH_SHORT).show();
        }

//        idList.add(key);
        mReference = reference.child("doctors").child(key).child("useridList").child(String.valueOf(count));

        count++;

        /**
         * Saving value using sharedpreference
         */
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("idCount", String.valueOf(count));
        editor.apply();


        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        setupBottomNavigationView();

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, count+ " : " + currentUserId, Toast.LENGTH_SHORT).show();
                mReference.setValue(currentUserId);
                btnRequest.setText("Request sent");
            }
        });

    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up bottomNav");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
    }

}
