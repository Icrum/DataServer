package com.example.dataserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class StatsActivity extends AppCompatActivity {
    private String currentSensor;
    DatabaseReference mRef;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        final TextView mCurrentValue = (TextView) findViewById(R.id.textView_CurrentValue);

        currentSensor = getIntent().getExtras().get("Sensor").toString();
        Toast.makeText(StatsActivity.this, currentSensor, Toast.LENGTH_SHORT).show();

        Toolbar toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle(currentSensor);
        setSupportActionBar(toolbar);

        mRef = FirebaseDatabase.getInstance().getReference();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String str = dataSnapshot.child(currentSensor).getValue().toString() + ",";
                str = str.replace(" ", "");
                str = str.replace("}", "");
                str = str.replace("{", "");
                str = str.replace("-", "");
                String currentvalue = str.substring(str.length() - 26);
                currentvalue = currentvalue.substring(currentvalue.indexOf("=") + 1, str.indexOf(","));
                mCurrentValue.setText("Current Value: " + currentvalue);

                String value;
                str = str.substring(str.length() - 312);
                if (str.indexOf("=") > str.indexOf(","))
                    str = str.substring(str.indexOf(",") + 1);
                String[] temps = new String[12];
                for(int i = 0; i <= temps.length - 1; i++)
                {
                    if (!str.isEmpty())
                    {
                        value = str.substring(str.indexOf("=") + 1, str.indexOf(","));
                        temps[i] = value;
                        str = str.substring(str.indexOf(",") + 1);
                    }
                }

                temps[11] = currentvalue;
                /*for (String strArray : temps)
                {
                    mCurrentValue.setText(mCurrentValue.getText() + strArray);
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}