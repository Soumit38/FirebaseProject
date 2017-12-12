package com.soumit.firebaseauthentication.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.soumit.firebaseauthentication.Maps.MapsActivity;
import com.soumit.firebaseauthentication.Others.DefaultDoctorList;
import com.soumit.firebaseauthentication.R;
import com.soumit.firebaseauthentication.Others.SearchDemo;
import com.soumit.firebaseauthentication.Others.Test;

/**
 * Created by Soumit on 11/18/2017.
 */

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHelper";

    @SuppressLint("LongLogTag")
    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "Setting up bottomNav");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationView view) {
        view.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_house:
                        Intent intent_house = new Intent(context, DefaultDoctorList.class);
                        context.startActivity(intent_house);
                        break;

                    case R.id.ic_search:
                        Intent intent_search = new Intent(context, SearchDemo.class);
                        context.startActivity(intent_search);
                        break;

                    case R.id.ic_circle:
                        Intent intent_circle = new Intent(context, MapsActivity.class);
                        context.startActivity(intent_circle);
                        break;

                    case R.id.ic_android:
                        Intent intent_android = new Intent(context, Test.class);
                        context.startActivity(intent_android);
                        break;

                }
            }
        });

    }


}
