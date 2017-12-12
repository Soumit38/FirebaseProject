package com.soumit.firebaseauthentication.Others;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.soumit.firebaseauthentication.Models.Doctor;
import com.soumit.firebaseauthentication.R;
import com.soumit.firebaseauthentication.Utils.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchDemo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "SearchDemo";
    private Context mContext = SearchDemo.this;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Doctor> listItems;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mReference;

    SearchView searchInput;
    String searchString;

    String dropdownSearch;

    private Spinner spinner;
    private static final String[]paths = {"Name", "Category", "Address"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_demo);

        //------------------
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchDemo.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //-----------------------


        searchInput = (SearchView) findViewById(R.id.searchInput);

        searchString = searchInput.getQuery().toString();

        listItems = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    protected void onStart() {
        super.onStart();

        /**
         * loading all doctors
         */
        mReference = reference.child("doctors");


//        Query query = reference.child("doctors").startAt("a");

        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString = searchInput.getQuery().toString().trim();
                Toast.makeText(mContext, searchString, Toast.LENGTH_SHORT).show();

                loadListviewFromFirebase(searchString, dropdownSearch);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listItems.clear();
                return false;
            }
        });

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


    private void loadListviewFromFirebase(String searchString, String dropdownSearch){
        Toast.makeText(mContext, dropdownSearch, Toast.LENGTH_SHORT).show();
        dropdownSearch = dropdownSearch.trim();
        mReference.orderByChild(dropdownSearch)
                .startAt(searchString)
                .endAt(searchString+"\uf8ff")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Doctor doctor = dataSnapshot.getValue(Doctor.class);

                        String key = dataSnapshot.getKey();
                        doctor.userid = key;
//                Toast.makeText(mContext, key, Toast.LENGTH_SHORT).show();

                        listItems.add(doctor);

                        adapter = new MyAdapter(SearchDemo.this, listItems);
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


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                dropdownSearch = "name";
//                Toast.makeText(mContext, dropdownSearch, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                dropdownSearch = "tag";
//                Toast.makeText(mContext, dropdownSearch , Toast.LENGTH_SHORT).show();
                break;
            case 2:
                dropdownSearch = "address";
//                Toast.makeText(mContext, dropdownSearch , Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}











