package com.or.organizerp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.or.organizerp.R;
import com.or.organizerp.model.User;

import java.util.List;



public class UserAdapter <P> extends ArrayAdapter<User> {
    Context context;
    List<User> objects;

    public  UserAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {
        super(context, resource, textViewResourceId, objects);

        this.context=context;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.userraw, parent, false);

        TextView tvfname = (TextView)view.findViewById(R.id.tvfname);
        TextView tvlname = (TextView)view.findViewById(R.id.tvlname);
        TextView tvphone = (TextView)view.findViewById(R.id.tvphone);


        User temp = objects.get(position);
        tvfname.setText(temp.getFname()+"");
        tvlname.setText(temp.getLname()+"");
        tvphone.setText(temp.getPhone()+"");
        return view;
    }

    private View getInflate(ViewGroup parent, LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.userraw, parent, false);
    }
}