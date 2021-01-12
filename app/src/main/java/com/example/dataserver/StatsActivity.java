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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class StatsActivity extends AppCompatActivity {
    private String currentSensor;
    DatabaseReference mRef;
    private LineGraphSeries<DataPoint> series1;

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

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(6);

        //enable scrolling
        graph.getViewport().setScrollable(true);
        //disable scaling
        graph.getViewport().setScalable(false);
        graph.getViewport().setScalableY(false);


        mRef.orderByKey().limitToLast(12).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> valueHash = (HashMap<String, String>) dataSnapshot.getValue();

                Iterator it = valueHash.entrySet().iterator();

                List<SensorValue> sensorValueList = new ArrayList<>();

                while(it.hasNext()){
                    SensorValue sv = new SensorValue((Map.Entry<String, String>) it.next());
                    sensorValueList.add(sv);

                }

                Log.d("StatsActivity", valueHash.toString() );

                /*

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
*/



                mCurrentValue.setText("Current Value: " + sensorValueList.get(sensorValueList.size()-1).getValue());
                double x, y;


                series1 = new LineGraphSeries<DataPoint>();
                for (int i = 0; i < sensorValueList.size(); i++)
                {
                    x = i;
                    y = sensorValueList.get(i).getValue();
                    series1.appendData(new DataPoint(x, y), true, sensorValueList.size());
                }
                graph.addSeries(series1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}