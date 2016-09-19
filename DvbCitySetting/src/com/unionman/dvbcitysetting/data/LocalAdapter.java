package com.unionman.dvbcitysetting.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unionman.dvbcitysetting.R;

import java.util.List;

/**
 * Created by Administrator on 2014/11/1.
 */
public class LocalAdapter<T> extends BaseAdapter {
    private List<T> objects;
    private LayoutInflater inflater;

    public LocalAdapter(Context context, List<T> objects){
        this.inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.city_spinner_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.text1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Object object = objects.get(i);
        holder.name.setText(object.toString());
        return view;
    }

    public final class ViewHolder{
        public TextView name;
    }
}
