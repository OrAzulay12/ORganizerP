package com.or.organizerp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.or.organizerp.R;
import com.or.organizerp.model.GroupEvent;


import java.util.List;

public class GroupEventAdapter <P> extends ArrayAdapter <GroupEvent> {
    Context context;
    List<GroupEvent> objects;

    public  GroupEventAdapter(Context context, int resource, int textViewResourceId, List<GroupEvent> objects) {
        super(context, resource, textViewResourceId, objects);

        this.context=context;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.eventrow, parent, false);

        TextView tvGroupEventname = (TextView)view.findViewById(R.id.tvEventname);
        TextView tvGroupEventtype = (TextView)view.findViewById(R.id.tvEventtype);
        TextView tvGroupEventdate = (TextView)view.findViewById(R.id.tvEventdate);


        GroupEvent temp = objects.get(position);
        tvGroupEventname.setText(temp.getName()+"");
        tvGroupEventtype.setText(temp.getType()+"");

        tvGroupEventdate.setText(temp.getDate()+"");
        return view;
    }


}