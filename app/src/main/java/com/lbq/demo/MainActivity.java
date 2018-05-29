package com.lbq.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);
        listView.setAdapter(new ListAdapter());
    }
    private class ListAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return 50;
        }
        @Override
        public Object getItem(int position)
        {
            return null;
        }
        @Override
        public long getItemId(int position)
        {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_text,null);
                holder.name = convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            }
            else holder = (ViewHolder) convertView.getTag();
            holder.name.setText(String.valueOf(position + 1));
            return convertView;
        }
        private class ViewHolder
        {
            TextView name;
        }
    }
}
