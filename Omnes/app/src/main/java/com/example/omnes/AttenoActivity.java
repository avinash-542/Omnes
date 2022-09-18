package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AttenoActivity extends AppCompatActivity {
    private RadarChart chart;
    private List<String> classes;
    private List<Float> percentages;
    private List<Integer> totcount, attencount;
    private DatabaseReference useref;
    private FirebaseAuth mAuth;
    private String classroom, roll;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atteno);
        result = findViewById(R.id.perce);
        classes = new ArrayList<>();
        chart = (RadarChart) findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        useref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        useref.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.hasChild("Class")) {
                        classroom = snapshot.child("Class").getValue().toString();
                        roll = snapshot.child("RollNo").getValue().toString();
                        List<String> sub = new ArrayList<>();
                        useref.child("Classes").child(classroom).child("Faculty").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                if(snapshot1.hasChildren()) {
                                    for(DataSnapshot ds : snapshot1.getChildren()) {
                                        sub.add(ds.child("Subject").getValue().toString());
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        useref.child("Attendance").child(classroom).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                if(snapshot3.hasChildren()) {
                                    ArrayList<RadarEntry> data = new ArrayList<>();
                                    float totper=0;
                                    for(int i=0; i< sub.size(); i++) {
                                        int attended = 0, totalc = 0;
                                        for (DataSnapshot ds1 : snapshot3.getChildren()) {
                                            if (ds1.hasChildren()) {
                                                for (DataSnapshot ds12 : ds1.getChildren()) {
                                                    if(ds12.child("Subject").getValue().toString().equals(sub.get(i))) {
                                                        if (ds12.child(roll).exists()) {
                                                            if (ds12.child(roll).getValue().toString().equals("Leave") || ds12.child(roll).getValue().toString().equals("Present")) {
                                                                totalc++;
                                                                attended++;
                                                            } else if (ds12.child(roll).getValue().toString().equals("Absent")) {
                                                                totalc++;
                                                            }
                                                    }
                                                    }
                                                }
                                            }
                                        }
                                        float percent = (float) attended*100/totalc;
                                        totper = totper + percent;
                                        data.add(new RadarEntry(percent));
                                        RadarDataSet dataset = new RadarDataSet(data,"Subject-wise report");
                                        dataset.setColor(Color.GREEN);
                                        dataset.setFillColor(Color.rgb(0, 255, 0));
                                        dataset.setDrawFilled(true);
                                        RadarData newdata = new RadarData();
                                        newdata.addDataSet(dataset);
                                        newdata.setValueTextSize(12f);
                                        chart.animateXY(1400, 1400, Easing.EaseInOutQuad);
                                        XAxis xAxis = chart.getXAxis();
                                        xAxis.setTextSize(12f);
                                        xAxis.setYOffset(0f);
                                        xAxis.setXOffset(0f);
                                        xAxis.setValueFormatter(new IndexAxisValueFormatter(sub));
                                        xAxis.setTextColor(Color.BLACK);

                                        YAxis yAxis = chart.getYAxis();
                                        yAxis.setTextSize(12f);
                                        yAxis.setDrawLabels(false);

                                        Legend l = chart.getLegend();
                                        l.setTextSize(12f);
                                        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                                        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                                        l.setOrientation(Legend.LegendOrientation.VERTICAL);
                                        l.setDrawInside(false);
                                        l.setXEntrySpace(7f);
                                        l.setYEntrySpace(0f);
                                        l.setYOffset(0f);



                                        chart.setData(newdata);
                                        chart.invalidate();
                                    }
                                    result.setText("Your Attendance Percent is : "+(float) totper/sub.size()+"%");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

}