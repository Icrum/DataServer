package com.example.dataserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView myListView;

    ArrayList<String> myArrayList = new ArrayList<>();

    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("Sensors");
        setSupportActionBar(toolbar);

        final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, myArrayList);

        myListView = (ListView) findViewById(R.id.listview1);
        myListView.setAdapter(myArrayAdapter);

        mRef = FirebaseDatabase.getInstance().getReference();

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //String value = dataSnapshot.getValue().toString();
                //myArrayList.add(value);
                String key = dataSnapshot.getKey();
                myArrayList.add(key);
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                myArrayAdapter.notifyDataSetChanged();
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
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentSensor=parent.getItemAtPosition(position).toString();
                Intent SensorIntent=new Intent(getApplicationContext(),StatsActivity.class);
                SensorIntent.putExtra("Sensor",currentSensor);
                startActivity(SensorIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()){
            //Back button
            case android.R.id.home:
                //If this activity started from other activity
                finish();

            /*If you wish to open new activity and close this one
            startNewActivity();
            */
                return true;
            case R.id.profile:
                profile();
                return true;

            case R.id.about:
                about();
                return true;

            case R.id.logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout (){
        FirebaseAuth.getInstance().signOut(); //Logout
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void about (){
        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
        finish();
    }

    public void profile (){
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        finish();
    }


}
