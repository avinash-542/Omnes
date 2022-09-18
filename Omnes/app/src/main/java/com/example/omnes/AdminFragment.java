package com.example.omnes;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AdminFragment extends Fragment {
    private CardView facentry, studentry, ttentry, studdetails, facdetails;
    private View list;

    public AdminFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        list = inflater.inflate(R.layout.fragment_admin, container, false);
        facentry = (CardView) list.findViewById(R.id.facentry);
        facdetails = (CardView)list.findViewById(R.id.facdetails);
        studentry = (CardView) list.findViewById(R.id.studentry);
        studdetails = (CardView) list.findViewById(R.id.studdetails);
        ttentry = (CardView) list.findViewById(R.id.ttentry);
        return list;
    }

    @Override
    public void onStart() {
        super.onStart();
        facentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(getContext(), FacentryActivity.class);
                startActivity(i);
            }
        });

        facdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(getContext(), FacdetailsActivity.class);
                startActivity(i);
            }
        });

        studentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), StudentryActivity.class);
                i.putExtra("editor", "admin");
                startActivity(i);
            }
        });
        studdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(getContext(), StudetailsActivity.class);
                startActivity(i);
            }
        });

        ttentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), TimeentryActivity.class);
                startActivity(i);
            }
        });
    }
}
