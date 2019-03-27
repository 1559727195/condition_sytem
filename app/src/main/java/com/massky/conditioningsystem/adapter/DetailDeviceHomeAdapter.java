package com.massky.conditioningsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massky.conditioningsystem.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class DetailDeviceHomeAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();
    Context context;

    public DetailDeviceHomeAdapter(Context context, List<Map> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.detail_home_device, null);
            // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
            int itemWidth = ((context.getResources().getDisplayMetrics().widthPixels
            ) / 2 - dip2px(context, 6)) / 2;
            // Calculate the height by your scale rate, I just use itemWidth here
            // 下面根据比例计算您的item的高度，此处只是使用itemWidth
            int itemHeight = itemWidth;
            AbsListView.LayoutParams params = (AbsListView.LayoutParams) convertView.getLayoutParams();
            if (params == null) {
                params = new AbsListView.LayoutParams(
                        itemWidth,
                        itemHeight / 5 * 3 + itemHeight / 10 / 2);
                convertView.setLayoutParams(params);
            } else {
                params.height = itemHeight;//
                params.width = itemWidth;
            }

            viewHolderContentType.title_room = (TextView) convertView.findViewById(R.id.title_room);
            viewHolderContentType.device_name = (TextView) convertView.findViewById(R.id.device_name);
            viewHolderContentType.status_txt = (TextView) convertView.findViewById(R.id.status_txt);
            viewHolderContentType.imageitem_id = (ImageView) convertView.findViewById(R.id.image);//title_room;//device_name,
            // device_action_or_father_name

            //linear_select
            viewHolderContentType.linear_select = (LinearLayout) convertView.findViewById(R.id.linear_select);
            //title_room;//device_name,

            //scene_img;//场景图片，scene_checkbox;//场景选中
            //
            viewHolderContentType.scene_img = (ImageView) convertView.findViewById(R.id.scene_img);
            viewHolderContentType.scene_checkbox = (CheckBox) convertView.findViewById(R.id.scene_checkbox);
            viewHolderContentType.title_scene = (TextView) convertView.findViewById(R.id.title_scene);
            //linear_device
            viewHolderContentType.linear_device = (LinearLayout) convertView.findViewById(R.id.linear_device);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.scene_img.setVisibility(View.GONE);
        final ViewHolderContentType mHolder = viewHolderContentType;
        switch (list.get(position).get("type_item") == null ?
                "" : list.get(position).get("type_item").toString()) {
            case "设备":
                viewHolderContentType.linear_device.setVisibility(View.VISIBLE);
                viewHolderContentType.title_scene.setVisibility(View.GONE);
                show_device(position, mHolder);
                break;
            case "场景":
            case "分组控制":
                viewHolderContentType.linear_device.setVisibility(View.GONE);
                viewHolderContentType.title_scene.setVisibility(View.VISIBLE);
                viewHolderContentType.title_scene.setText(list.get(position).get("name") == null ?
                        "" : list.get(position).get("name").toString());
                break;
        }


        //type：设备类型，1-灯，2-调光，3-空调，4-窗帘，5-新风，6-地暖
//        mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);

        return convertView;
    }

    /**
     * 显示设备
     *
     * @param position
     * @param mHolder
     */
    private void show_device(int position, ViewHolderContentType mHolder) {
        mHolder.title_room.setText(list.get(position).get("name") == null ?
                "" : list.get(position).get("name").toString());
        mHolder.device_name.setText(list.get(position).get("power") == null ?
                "" : list.get(position).get("power").toString());
        mHolder.status_txt.setText(list.get(position).get("action") == null ?
                "" : list.get(position).get("action").toString());
    }


    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setList(List<Map> deviceList) {
        this.list = deviceList;
    }


//    public void setList(List<Map> dataSourceList) {
//        this.list = new ArrayList<>();
//        this.list = dataSourceList;
//        notifyDataSetChanged();
//    }

    class ViewHolderContentType {
        ImageView imageitem_id;
        TextView title_room;//device_name,device_action_or_father_name
        TextView device_name;
        TextView status_txt;
        LinearLayout linear_select;
        ImageView scene_img;//场景图片
        CheckBox scene_checkbox;//场景选中
        TextView title_scene;
        LinearLayout linear_device;
    }
}
