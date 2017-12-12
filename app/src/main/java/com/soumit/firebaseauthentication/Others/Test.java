package com.soumit.firebaseauthentication.Others;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.soumit.firebaseauthentication.Auth.LoginActivity;
import com.soumit.firebaseauthentication.Profile.ViewProfile;
import com.soumit.firebaseauthentication.R;
import com.soumit.firebaseauthentication.Utils.BottomNavigationViewHelper;

public class Test extends AppCompatActivity {
    private static final String TAG = "Test";

    Button btnLink;

    private Context mContext = Test.this;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        auth = FirebaseAuth.getInstance();

        setupToolbar();
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        btnLink = (Button) findViewById(R.id.btnLink);
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent receivedIntent = getIntent();
                final String temp_id = receivedIntent.getStringExtra("rec_id");

                Intent intent = new Intent(mContext, ViewProfile.class);
                if(temp_id != null && temp_id.equals("patient")){
                    intent.putExtra("recognition_id", "patient");
                }else if(temp_id != null && temp_id.equals("doctor")){
                    intent.putExtra("recognition_id", "doctor");
                }
                startActivity(intent);
            }
        });


        setupBottomNavigationView();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.logout_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.proflie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out: {
                auth.signOut();
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
                break;
            }
            case android.R.id.home:
                Toast.makeText(mContext, "back button pressed", Toast.LENGTH_SHORT).show();
                break;
            // case blocks for other MenuItems (if any)
        }
        return false;
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up bottomNav");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

//        for(int i=0;i<menu.size();i++){
//            MenuItem menuItem = menu.getItem(i);
//            menuItem.setChecked(true);
//        }

    }

}
