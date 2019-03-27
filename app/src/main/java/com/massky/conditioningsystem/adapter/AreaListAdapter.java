package com.massky.conditioningsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.massky.conditioningsystem.R;
import com.massky.conditioningsystem.base.BaseAdapter;
import com.massky.conditioningsystem.sql.CommonBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AreaListAdapter extends BaseAdapter {
    private List<CommonBean.GroupDetail> list = new ArrayList<>();

    public AreaListAdapter(Context context, List<CommonBean.GroupDetail> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.area_list_item, null);
            viewHolderContentType.area_item_txt = (TextView) convertView.findViewById(R.id.area_item_txt);
            //area_linear_item
            viewHolderContentType.area_linear_item = (LinearLayout) convertView.findViewById(R.id.area_linear_item);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }


        viewHolderContentType.area_item_txt.setText(list.get(position).name);

        return convertView;
    }

    class ViewHolderContentType {
        TextView area_item_txt;
        LinearLayout area_linear_item;
    }
}
