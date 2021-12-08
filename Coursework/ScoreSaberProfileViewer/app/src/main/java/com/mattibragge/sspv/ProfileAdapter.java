package com.mattibragge.sspv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileAdapter extends BaseAdapter {

    LayoutInflater m_inflater;
    ArrayList<String> names;

    public ProfileAdapter(Context c, ArrayList<String> n) {
        names = n;
        m_inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int i) {
        return names.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = m_inflater.inflate(R.layout.listview_layout, null);
        TextView name_tv= (TextView) v.findViewById(R.id.nameTv);
        TextView rank_tv = (TextView) v.findViewById(R.id.rankTv);

        String name = names.get(i);

        name_tv.setText(name);
        rank_tv.setText("#" + (i+1));

        return v;
    }
}
