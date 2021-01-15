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
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
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
    private LineGraphSeries<DataPoint> series;

    DatabaseReference mRef;
    List<SensorValue> sensorValueList = new ArrayList<>(); // model
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        final TextView mCurrentValue = (TextView) findViewById(R.id.textView_CurrentValue);
        //final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        currentSensor = getIntent().getExtras().get("Sensor").toString();
        Toast.makeText(StatsActivity.this, currentSensor, Toast.LENGTH_SHORT).show();

        Toolbar toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle(currentSensor);
        setSupportActionBar(toolbar);

        mRef = FirebaseDatabase.getInstance().getReference()
                .child(currentSensor);

        graph = (GraphView) findViewById(R.id.graph);
        // activate horizontal scrolling
        graph.getViewport().setScrollable(true);

        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);

        series = new LineGraphSeries();

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

                sensorValueList = sensorValueList.subList(sensorValueList.size()-12,
                        sensorValueList.size());
                mCurrentValue.setText("Current Value: " + sensorValueList
                        .get(sensorValueList.size()-1)
                        .getValue());

                Date x = null, x1;
                double y;
                //get first date in sensorValueList
                x1 = sensorValueList.get(0).getDate();

                for (int i = 0; i < sensorValueList.size(); i++)
                {
                    x = sensorValueList.get(i).getDate();
                    y = sensorValueList.get(i).getValue();
                    series.appendData(new DataPoint(x.getTime(), y),
                            true,
                            sensorValueList.size());
                }
                graph.addSeries(series);

                // set date label formatter
                graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter
                        (StatsActivity.this)
                {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if(isValueX){
                            return sdf.format(new Date((long) value));
                        }
                        else{
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });

                // set manual x bounds to have nice steps
                graph.getViewport().setMinX(x1.getTime());
                graph.getViewport().setMaxX(x.getTime());
                graph.getViewport().setXAxisBoundsManual(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
//String[] time= new String[12];

/*for (int j = 0; j<=time.length; j++){
    time[j]= String.valueOf(x1 = Double.parseDouble(sdf.format(x.getTime())))+":"+String.valueOf(Double.parseDouble(sdf2.format(x.getTime())));
 }*/