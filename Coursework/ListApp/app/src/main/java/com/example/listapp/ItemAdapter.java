package com.example.listapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ItemAdapter extends BaseAdapter {

    LayoutInflater m_inflater;
    String[] items;
    String[] prices;
    String[] descriptions;

    public ItemAdapter(Context c, String[] i, String[] p, String[] d) {
        items = i;
        prices = p;
        descriptions = d;
        m_inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = m_inflater.inflate(R.layout.my_listview_detail, null);
        TextView name_text_view = (TextView) v.findViewById(R.id.nameTextView);
        TextView description_text_view = (TextView) v.findViewById(R.id.descriptionTextView);
        TextView price_text_view = (TextView) v.findViewById(R.id.priceTextView);

        String name = items[position];
        String desc = descriptions[position];
        String cost = prices[position];

        name_text_view.setText(name);
        description_text_view.setText(desc);
        price_text_view.setText(cost);

        return v;
    }
}
