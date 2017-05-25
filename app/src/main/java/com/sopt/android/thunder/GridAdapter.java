package com.sopt.android.thunder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sopt.android.thunder.detail.model.Participant;

import java.util.List;

/**
 * Created by inbiz02 on 2016-01-13.
 */
public class GridAdapter extends BaseAdapter {
    
    Context context;
    int layout;
    List<Participant> persons;
    LayoutInflater inf;


    public GridAdapter(Context context, int layout, List<Participant> persons) {
        this.context = context;
        this.layout = layout;
        this.persons = persons;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        Log.i("Show", "a_inside 1");
    }

    @Override
    public int getCount() {
        Log.i("Show","a_inside 2");
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i("Show","a_inside 3");
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {

        Log.i("Show","a_inside 4");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null)
                convertView = inf.inflate(layout, null);
            Log.i("Show", "getView 실행 " + persons.get(position).getName());
            TextView p_text = (TextView) convertView.findViewById(R.id.person);
            TextView numbs = (TextView) convertView.findViewById(R.id.numbers);

            p_text.setText(persons.get(position).getName());
            numbs.setText(persons.get(position).getNumber().substring(7, 11));
        }catch(Exception e){
            Log.v("Show GridAdapter error", "오류내용 :"+e.toString());
        }
        return convertView;
    }
}