package com.soumit.firebaseauthentication.Others;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.soumit.firebaseauthentication.Models.Doctor;
import com.soumit.firebaseauthentication.R;
import com.soumit.firebaseauthentication.Utils.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class DefaultDoctorList extends AppCompatActivity {

    private static final String TAG = "SearchDemo";
    private Context mContext = DefaultDoctorList.this;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Doctor> listItems;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mReference;

    SearchView searchInput;
    String searchString;
    TextView doctorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_demo);

        listItems = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchInput = (SearchView) findViewById(R.id.searchInput);
        searchInput.setVisibility(View.GONE);

        doctorList = (TextView) findViewById(R.id.doctorList);
        doctorList.setVisibility(View.VISIBLE);


//        for(int i=0;i<10;i++){
//            Doctor item = new Doctor(
//                    "Item : " + (i+1),
//                    "Test Description"
//            );
//
//            listItems.add(item);
//        }




    }


    @Override
    protected void onStart() {
        super.onStart();

        /**
         * loading all doctors
         */
        mReference = reference.child("doctors");

        loadListviewFromFirebase();


//        Query query = reference.child("doctors").startAt("a");

//        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchString = searchInput.getQuery().toString().trim();
//                Toast.makeText(mContext, searchString, Toast.LENGTH_SHORT).show();
//
//                loadListviewFromFirebase(searchString);
//
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                listItems.clear();
//                return false;
//            }
//        });

//        Toast.makeText(mContext, searchString, Toast.LENGTH_SHORT).show();



            }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void searchForMatch(String keyword){
        Log.d(TAG, "searchForMatch: searching for a match: " + keyword);
        //update the users list view
        if(keyword.length() == 0){

        }else{

        }
    }


    private void loadListviewFromFirebase(){
        mReference.orderByChild("name")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Doctor doctor = dataSnapshot.getValue(Doctor.class);

                        String key = dataSnapshot.getKey();
                        doctor.userid = key;
//                Toast.makeText(mContext, key, Toast.LENGTH_SHORT).show();

                        listItems.add(doctor);

                        adapter = new MyAdapter(DefaultDoctorList.this, listItems);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


}











