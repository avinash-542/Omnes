package com.example.omnes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import android.content.Context;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class attenAdapter extends BaseAdapter {

    private Context context;
    private List<String> stunames, sturolls, stuimgs;
    private LayoutInflater li;

    public attenAdapter(Context context, List<String> stunames, List<String> sturolls, List<String> stuimgs, LayoutInflater li) {
        this.context = context;
        this.stunames = stunames;
        this.sturolls = sturolls;
        this.stuimgs = stuimgs;
        this.li = li;
    }


    @Override
    public int getCount() {
        return stunames.size();
    }

    @Override
    public Object getItem(int position) {
        return stunames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = li.inflate(R.layout.student_card, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.img = (CircleImageView) convertView.findViewById(R.id.stuimage);
            viewHolder.name = (TextView) convertView.findViewById(R.id.stuname1);
            viewHolder.roll = (TextView) convertView.findViewById(R.id.sturoll1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (!stuimgs.get(position).equals("None")) {
            Picasso.get().load(stuimgs.get(position)).into(viewHolder.img);
        }
        viewHolder.name.setText(stunames.get(position));
        viewHolder.roll.setText(sturolls.get(position));

        return convertView;
    }

    private static class ViewHolder {
        public CircleImageView img;
        public TextView name;
        public TextView roll;
    }

}
