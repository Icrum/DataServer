package com.example.dataserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dataserver.models.SensorValue;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class StatsActivity extends AppCompatActivity {
    private String currentSensor;
    private LineGraphSeries<DataPoint> series1;

    DatabaseReference mRef;
    List<SensorValue> sensorValueList = new ArrayList<>(); // model

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

        mRef = FirebaseDatabase.getInstance().getReference()
                .child(currentSensor);

        final GraphView graph = (GraphView) findViewById(R.id.graph);

        // activate horizontal zooming and scrolling
        graph.getViewport().setScalable(false);
        // activate horizontal scrolling
        graph.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        graph.getViewport().setScalableY(false);
        // activate vertical scrolling
        graph.getViewport().setScrollableY(false);


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator it = dataSnapshot.getChildren().iterator();
                sensorValueList.clear();

                while(it.hasNext()){
                    SensorValue sv = new SensorValue((DataSnapshot) it.next());
                    sensorValueList.add(sv);
                }

                sensorValueList.sort(new Comparator<SensorValue>() {
                    @Override
                    public int compare(SensorValue o1, SensorValue o2) {
                        if (o1.getDate().after(o2.getDate())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });

                sensorValueList = sensorValueList.subList(sensorValueList.size()-14, sensorValueList.size());
                mCurrentValue.setText("Current Value: " + sensorValueList.get(sensorValueList.size()-1).getValue());

                Date x;
                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                SimpleDateFormat sdf2 = new SimpleDateFormat("mm");
                double y, x1, x2;

                series1 = new LineGraphSeries<DataPoint>();
                for (int i = 0; i < sensorValueList.size(); i++)
                {
                    x = sensorValueList.get(i).getDate();
                    y = sensorValueList.get(i).getValue();
                    x1 = Double.parseDouble(sdf.format(x.getTime()));
                    x2 = Double.parseDouble(sdf2.format(x.getTime()));

                    series1.appendData(new DataPoint(x1+(x2*0.016), y), true, sensorValueList.size());
                }

                graph.addSeries(series1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}