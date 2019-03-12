package com.massky.conditioningsystem.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.DownloadManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.massky.conditioningsystem.R;
import com.massky.conditioningsystem.Utils.DensityUtils;
import com.massky.conditioningsystem.adapter.DetailDeviceHomeAdapter;
import com.massky.conditioningsystem.adapter.HomeDeviceListAdapter;
import com.massky.conditioningsystem.base.BaseActivity;
import com.massky.conditioningsystem.view.DownLoadProgressbar;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * 作者：漆可 on 2016/9/1 18:24
 */
public class HomeActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    @InjectView(R.id.status_view)
    StatusView statusView;
    private HomeDeviceListAdapter homeDeviceListAdapter;
    private List<Map> roomList = new ArrayList<>();
    @InjectView(R.id.home_listview)
    ListView home_listview;
    @InjectView(R.id.dragGridView)
    GridView mDragGridView;
    @InjectView(R.id.refresh_view)
    XRefreshView refresh_view;
    private List<Map> deviceList = new ArrayList<>();
    private DetailDeviceHomeAdapter deviceListAdapter;
    private Dialog dialog1;

    @Override
    protected int viewId() {
        return R.layout.home_activity;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        refresh_view.setScrollBackDuration(300);
        refresh_view.setPinnedTime(1000);
        refresh_view.setPullLoadEnable(false);
        refresh_view.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                refresh_view.stopRefresh();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
            }
        });
    }

    @Override
    protected void onEvent() {
        init_device_onclick();
    }

    @Override
    protected void onData() {
        room_list_show_adapter();
        device_list_show_adapter();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 初始化设备点击事件
     */
    private void init_device_onclick() {
        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linear_select = (LinearLayout) view.findViewById(R.id.linear_select);
                item_click_animal(view);
                int[] location = new int[2];
                view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
                view.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
                showCenterDeleteDialog("123", "321", location);
            }
        });
        mDragGridView.setOnItemLongClickListener(this);
    }

    /**
     * item点击时的动画效果
     *
     * @param view
     */
    private void item_click_animal(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "ScaleX", 0.87f, 1f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "ScaleY", 0.87f, 1f, 1.1f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    /**
     * 弹出框动画效果
     *
     * @param view
     * @param displayHeight
     * @param location
     */
    private void pop_animal(View view, int displayHeight, int displayWidth, int[] location) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "ScaleX", 0.1f, 0.3f, 0.4f, 0.5f, 0.6f, 0.87f, 1f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "ScaleY", 0.1f, 0.3f, 0.4f, 0.5f, 0.6f, 0.75f, 0.87f, 1f, 1.1f, 1.0f);
        ObjectAnimator translationAni = ObjectAnimator.ofFloat(view, "TranslationY", -location[1] / 2 * 0.8f, (location[1] - displayHeight) / 2 * 0.8f,
                -(location[1] - displayHeight) / 2 * 0.2f, 0);
        ObjectAnimator translatioX = ObjectAnimator.ofFloat(view, "TranslationX", -location[0] / 2 * 0.8f, (location[0] - displayWidth) / 2 * 0.8f
                , -(location[0] - displayWidth) / 2 * 0.2f, 0);
        ObjectAnimator alphaAni = ObjectAnimator.ofFloat(view, "Alpha", 0.5f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, translationAni, translatioX, alphaAni);
        animatorSet.setDuration(1200);
        animatorSet.start();
    }


    /**
     * 具体房间下的设备列表显示
     */
    private void device_list_show_adapter() {

        deviceList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Map map = new HashMap();
            map.put("name", "" +
                    "开");
            deviceList.add(map);
        }
        deviceListAdapter = new DetailDeviceHomeAdapter(HomeActivity.this, deviceList);
        mDragGridView.setAdapter(deviceListAdapter);//设备侧栏列表
        home_listview.setOnItemClickListener(this);
    }

    /**
     * 侧栏房间列数据显示
     */
    private void room_list_show_adapter() {
        roomList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Map map = new HashMap();
            map.put("name", "设备控制" + i);
            map.put("count", "" + i);
            roomList.add(map);
        }
        homeDeviceListAdapter = new HomeDeviceListAdapter(HomeActivity.this, roomList, new HomeDeviceListAdapter.HomeDeviceItemClickListener() {
            @Override
            public void homedeviceClick(String number) {//获取单个房间关联信息（APP->网关）

            }
        });
        home_listview.setAdapter(homeDeviceListAdapter);//设备侧栏列表
        home_listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        LinearLayout linear_item_select = view.findViewById(R.id.linear_item_select);
        for (int i = 0; i < roomList.size(); i++) {
            if (i == position) {
                HomeDeviceListAdapter.getIsSelected().put(i, true);
                anim_scale(linear_item_select);
            } else {
                HomeDeviceListAdapter.getIsSelected().put(i, false);
            }
        }
        homeDeviceListAdapter.notifyDataSetChanged();
    }

    /**
     * 缩放动画
     *
     * @param linear_item_select
     */
    private void anim_scale(LinearLayout linear_item_select) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(linear_item_select, "ScaleX", 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        linear_item_select.setPivotX(0);
        animator.start();
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name1, final String name2, int[] location) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.promat_item_pop_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
        ImageView btn_guanbi;
//        final TextView content; //内容

//        mProgress = (DownLoadProgressbar) view.findViewById(R.id.dp_game_progress);
//        mProgress.setMaxValue(100);

//        tv_title.setText("是否拨打119");
//        content.setText(message);
        btn_guanbi = (ImageView) view.findViewById(R.id.btn_guanbi);
        //显示数据
        dialog1 = new Dialog(HomeActivity.this, R.style.BottomDialog);
        dialog1.setContentView(view);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setCancelable(true);//设置它可以取消
        dialog1.setCanceledOnTouchOutside(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog1.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayHeight / 3 * 2); //宽度设置为屏幕的0.5
        p.height = (int) (p.width); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog1.getWindow().setAttributes(p);  //设置生效
        dialog1.show();
        pop_animal(view, displayHeight, displayWidth, location);

        btn_guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

    }

    //场景长按显示
    public void showCenterSceneDialog(final String name1, final String name2, int[] location) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.promat_item_scene_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
        ImageView close_img;
//        final TextView content; //内容

//        mProgress = (DownLoadProgressbar) view.findViewById(R.id.dp_game_progress);
//        mProgress.setMaxValue(100);

//        tv_title.setText("是否拨打119");
//        content.setText(message);
//        btn_guanbi = (ImageView) view.findViewById(R.id.btn_guanbi);
        close_img = (ImageView) view.findViewById(R.id.close_img);
        //显示数据
        dialog1 = new Dialog(HomeActivity.this, R.style.BottomDialog);
        dialog1.setContentView(view);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setCancelable(true);//设置它可以取消
        dialog1.setCanceledOnTouchOutside(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog1.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayHeight); //宽度设置为屏幕的0.5
        p.height = (int) (p.width / 3 * 2); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog1.getWindow().setAttributes(p);  //设置生效
        dialog1.show();
        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        item_click_animal(view);
        showCenterSceneDialog("123", "231", null);
        return true;
    }
}
