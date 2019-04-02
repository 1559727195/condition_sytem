package com.massky.conditioningsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.massky.conditioningsystem.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class SceneDetailListAdapter extends android.widget.BaseAdapter {

    private List<Map> listint = new ArrayList<>();
    private List<String> listintwo = new ArrayList<>();
    HashMap<Integer, Boolean> isSelected = new HashMap<>();
    private Context context;

    public SceneDetailListAdapter(Context context, List<Map> listint) {
        this.listint = listint;
        this.context = context;
    }


    @Override
    public int getCount() {
        return listint.size();
    }

    @Override
    public Object getItem(int position) {
        return listint.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.scene_detail_list_item, null);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.panel_scene_name_txt.setText(listint.get(position).get("name").toString());
        viewHolderContentType.execute_scene_txt.setText(listint.get(position).get("action").toString());

        return convertView;
    }


    class ViewHolderContentType {
        TextView panel_scene_name_txt;
        TextView execute_scene_txt;
    }


}
