package com.example.omnes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AtteniActivity extends AppCompatActivity {
    private String cname, tota;
    private PieChart chart2;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    private int tot=0,pre=0,lea=0,abs=0;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atteni);
        mAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference();
        cname = getIntent().getStringExtra("inClass");
        msg = (TextView) findViewById(R.id.msg);
        chart2 = (PieChart) findViewById(R.id.chart2);
        chart2.setUsePercentValues(true);
        chart2.getDescription().setEnabled(false);
        chart2.setExtraOffsets(5, 10, 5, 5);
        chart2.setDragDecelerationFrictionCoef(0.95f);
        chart2.setCenterText(getIntent().getStringExtra("inClass")+" Report");
        chart2.setCenterTextSize(12f);
        chart2.setDrawHoleEnabled(true);
        chart2.setHoleColor(Color.WHITE);
        chart2.setTransparentCircleColor(Color.WHITE);
        chart2.setTransparentCircleAlpha(110);
        chart2.setHoleRadius(58f);
        chart2.setTransparentCircleRadius(61f);
        chart2.setDrawCenterText(true);
        chart2.setRotationAngle(0);
        chart2.setRotationEnabled(true);
        chart2.setHighlightPerTapEnabled(true);

        Legend l = chart2.getLegend();
        l.setTextSize(12f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);


        chart2.setEntryLabelColor(Color.WHITE);
        chart2.setEntryLabelTextSize(12f);

        userref.child("Attendance").child(getIntent().getStringExtra("inClass")).child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).child("15:10").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.hasChild("Count")) {
                        tot = Integer.parseInt(snapshot.child("Count").child("Total").getValue().toString());
                        pre = Integer.parseInt(snapshot.child("Count").child("Present").getValue().toString());
                        abs = Integer.parseInt(snapshot.child("Count").child("Absent").getValue().toString());
                        lea = Integer.parseInt(snapshot.child("Count").child("onLeave").getValue().toString());
                        tota = snapshot.child("Count").child("Total").getValue().toString();
                        float preper = (float) pre*100/tot;
                        float absper = (float) abs*100/tot;
                        float leaper = (float) lea*100/tot;

                        ArrayList<PieEntry> pp = new ArrayList<>();
                        pp.add(new PieEntry(absper, "Absentees"));
                        pp.add(new PieEntry(preper, "Presentees"));
                        pp.add(new PieEntry(leaper, "On Leave"));
                        ArrayList<Integer> colors = new ArrayList<>();
                        colors.add(Color.rgb(255, 51, 0));
                        colors.add(Color.rgb(0, 230, 0));
                        colors.add(Color.rgb(51, 51, 255));
                        PieDataSet dataset = new PieDataSet(pp, "Attendance report");
                        dataset.setColors(colors);
                        PieData data = new PieData(dataset);
                        data.setDrawValues(true);
                        data.setValueFormatter(new PercentFormatter(chart2));
                        data.setValueTextSize(12f);
                        data.setValueTextColor(Color.BLACK);
                        chart2.animateY(1400, Easing.EaseInOutQuad);
                        chart2.setData(data);
                        chart2.invalidate();
                    }
                } else {
                    chart2.setVisibility(View.GONE);
                    msg.setVisibility(View.VISIBLE);
                    msg.setText("No Reports Generated, Please check after 16:00");
                    msg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}