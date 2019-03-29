package com.massky.conditioningsystem.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.DownloadManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import com.massky.conditioningsystem.Util.DialogUtil;
import com.massky.conditioningsystem.Util.ListViewForScrollView_New;
import com.massky.conditioningsystem.Util.RxTimerUtil;
import com.massky.conditioningsystem.Util.SharedPreferencesUtil;
import com.massky.conditioningsystem.Util.ToastUtil;
import com.massky.conditioningsystem.Utils.DensityUtils;
import com.massky.conditioningsystem.adapter.AreaListAdapter;
import com.massky.conditioningsystem.adapter.DetailDeviceHomeAdapter;
import com.massky.conditioningsystem.adapter.HomeDeviceListAdapter;
import com.massky.conditioningsystem.base.BaseActivity;
import com.massky.conditioningsystem.di.module.EntityModule;
import com.massky.conditioningsystem.presenter.HomePresenter;
import com.massky.conditioningsystem.presenter.contract.HomeContract;
import com.massky.conditioningsystem.sql.BaseDao;
import com.massky.conditioningsystem.sql.CommonBean;
import com.massky.conditioningsystem.sql.SqlHelper;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;

/**
 * 作者：漆可 on 2016/9/1 18:24
 */
public class HomeActivity extends BaseActivity<HomePresenter> implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, HomeContract.View {
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
    private List<CommonBean.Count> counts = new ArrayList<>();
    private List<Map> list_dsc_count = new ArrayList<>();
    private int intfirst_time;
    private int current_dsc_position;
    private List<CommonBean.controller> controller_list = new ArrayList<>();
    private List<Map> controller_show_list = new ArrayList<>();
    private List<CommonBean.scene> scene_list = new ArrayList<>();
    private List<Map> scene_show_list = new ArrayList<>();
    private List<CommonBean.group> group_list = new ArrayList<>();
    private List<CommonBean.GroupDetail> group_detail_list = new ArrayList<>();
    private List<Map> group_show_list = new ArrayList<>();
    private List<CommonBean.operate> operate_list = new ArrayList<>();
    private int group_list_long_click_position;
    private long operate_max_id;
    private Timer timer;
    private TimerTask timerTask;
    private DialogUtil dialogUtil;

    @Override
    protected int viewId() {
        return R.layout.home_activity;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(this);
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
        intfirst_time = 1;
    }

    @Override
    protected void onEvent() {
        init_device_onclick();
    }


    /**
     * 控制设备成功或失败
     */
    private void on_control_scuess() {
        int status = operate_list.get(0).status;
        switch (status) {
            case 100:
                time_end();
                ToastUtil.showToast(HomeActivity.this, "控制成功");

                break;
            case 101:
                time_end();
                ToastUtil.showToast(HomeActivity.this, "控制失败");
                break;
        }

    }

    /**
     * 执行删除动作
     */
    private void excute_delete_control() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommonBean.operate operate = new CommonBean.operate();
                operate.setId(operate_list.get(0).id);
                operate.deleteList(operate);
            }
        }).start();
    }

    @Override
    protected void onData() {
        room_list_show_adapter();
        device_list_show_adapter();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (intfirst_time == 1) {
            intfirst_time = 2;
            current_dsc_position = 0;
        }
        mPresenter.getSqlCounts();
        switch (current_dsc_position) {
            case 0:
                mPresenter.show_deviceList();
                break;
            case 1:
                mPresenter.show_sceneList();
                break;
            case 2:
                mPresenter.show_controlList();
                break;
        }
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
                switch (current_dsc_position) {
                    case 0:
                        showCenterDeleteDialog("123", "321", location, controller_list.get(position));
                        break;
                }
            }
        });
        mDragGridView.setOnItemLongClickListener(this);
    }


    /**
     * //去显示房间列表
     */
    private void display_room_list(int position) {
        for (int i = 0; i < roomList.size(); i++) {
            if (i == position) {
                HomeDeviceListAdapter.getIsSelected().put(i, true);
            } else {
                HomeDeviceListAdapter.getIsSelected().put(i, false);
            }
        }
        homeDeviceListAdapter.notifyDataSetChanged();
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

        //从点击位置到屏幕中间利用贝塞尔曲线显示

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
            map.put("name", "设备控制");
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
        display_room_list(0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        current_dsc_position = position;
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
        switch (current_dsc_position) {
            case 0:
                mPresenter.show_deviceList();
                break;
            case 1:
                mPresenter.show_sceneList();
                break;
            case 2:
                mPresenter.show_controlList();
                break;
        }
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


    @Override
    public void showError(String msg) {

    }

    /**
     * 连表查询获取设备控制，场景控制，分组控制数据个数
     */
    @Override
    public void showsqlCounts(final List<Map> list_dsc_count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeDeviceListAdapter.setList1(list_dsc_count);
                homeDeviceListAdapter.notifyDataSetChanged();
                display_room_list(current_dsc_position);
            }
        });

    }

    /**
     * 显示设备列表
     *
     * @param controller_show_list
     * @param controller_list
     */
    @Override
    public void show_deviceList(final List<Map> controller_show_list, List<CommonBean.controller> controller_list) {
        this.controller_show_list = controller_show_list;
        this.controller_list = controller_list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceListAdapter.setList(controller_show_list);
                deviceListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 场景显示
     *
     * @param scene_show_list
     * @param scene_list
     */
    @Override
    public void show_sceneList(final List<Map> scene_show_list, List<CommonBean.scene> scene_list) {
        this.scene_show_list = scene_show_list;
        this.scene_list = scene_list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceListAdapter.setList(scene_show_list);
                deviceListAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 显示分组列表
     *
     * @param group_show_list
     * @param group_list
     */
    @Override
    public void show_groupList(final List<Map> group_show_list, List<CommonBean.group> group_list) {
        this.group_show_list = group_show_list;
        this.group_list = group_list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceListAdapter.setList(group_show_list);
                deviceListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 控制时获取的最大operateId
     *
     * @param operate_max_id
     */
    @Override
    public void show_operate_max_id(long operate_max_id) {
        //每次控制最多耗时5s,10* 500ms
        this.operate_max_id = operate_max_id;
        mPresenter.show_operateStatus(operate_max_id);
        init_time();
    }

    /**
     * 根据maxid获取status
     *
     * @param operate_list
     */
    @Override
    public void show_operateStatus(List<CommonBean.operate> operate_list) {
        this.operate_list = operate_list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                on_control_scuess();
            }
        });
    }

    /**
     * 显示组控制长按
     *
     * @param group_detail_list
     */
    @Override
    public void show_detailcontrolList(List<CommonBean.GroupDetail> group_detail_list) {
        this.group_detail_list = group_detail_list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showCenterSceneDialog(group_list.get(group_list_long_click_position).name, "", null);
            }
        });
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name1, final String name2, int[] location, final CommonBean.controller controller) {

        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.promat_item_pop_dialog, null);
        final TextView tempture_txt = view.findViewById(R.id.tempture_txt); //确定按钮
        TextView mode_txt = view.findViewById(R.id.mode_txt); //确定按钮
        TextView speed_txt = view.findViewById(R.id.speed_txt);
        ImageView temp_add = view.findViewById(R.id.temp_add);
        ImageView temp_del = view.findViewById(R.id.temp_del);

        TextView name_gloud;
        ImageView btn_guanbi;
//        final TextView content; //内容

//        mProgress = (DownLoadProgressbar) view.findViewById(R.id.dp_game_progress);
//        mProgress.setMaxValue(100);

//        tv_title.setText("是否拨打119");
//        content.setText(message);
        tempture_txt.setText(controller.getTemperatureSet() + "" + "℃");
        final int[] tempture = {controller.getTemperatureSet()};
        mode_txt.setText(controller.getMode() + "");
        speed_txt.setText(controller.getWind() + "");
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

        temp_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//温度加
                init_temper_Add(tempture, tempture_txt);

                CommonBean.operate operate = init_operate_params(controller, tempture[0]);
                //提交温度
                mPresenter.show_control_device(SqlHelper.getString(SqlHelper.sqlcontrol, operate, CommonBean.operate.class), operate, SqlHelper.selectMaxid);
        /*        1. 主键ID 自增 ，插入数据后返回这条数据的ID值
                insert into tableName() values() select @@identity*/
                dialogUtil.loadDialog();
            }
        });
        temp_del.setOnClickListener(new View.OnClickListener() {//温度减
            @Override
            public void onClick(View view) {
                if (tempture[0] > 16)
                    tempture[0]--;
                tempture_txt.setText(tempture[0] + "" + "℃");
            }
        });
    }

    /**
     * 初始化控制时间
     */
    private void init_time() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        timer.schedule(timerTask, 500, 500);//延时500ms，每隔500毫秒执行一次run方法
    }


    int delaytime;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mPresenter.show_operateStatus(operate_max_id);
                delaytime++;
                if (delaytime == 10) {
                    time_end();
                }
            }
            super.handleMessage(msg);
        }
    };


    private void time_end() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.cancel();
        }
        timerTask = null;
        timer = null;
        delaytime = 0;
        dialogUtil.removeDialog();
        excute_delete_control();
    }


    /**
     * 初始化温度+
     *
     * @param tempture
     * @param tempture_txt
     */
    private void init_temper_Add(int[] tempture, TextView tempture_txt) {
        if (tempture[0] < 16) {
            tempture[0] = 16;
        }
        if (tempture[0] < 30)
            tempture[0]++;
        tempture_txt.setText(tempture[0] + "" + "℃");
    }

    /**
     * 初始化operate表控制参数
     *
     * @param controller
     * @param temperatureSet
     * @return
     */
    private CommonBean.operate init_operate_params(CommonBean.controller controller, int temperatureSet) {
        CommonBean.operate operate = new CommonBean.operate();//直接new为查询全部user表中的数据
        operate.setStatus(1);
        operate.setFlag(0);
        operate.setIp(SqlHelper.sqlcontrol_ip + controller.communicatorID + ")");
        operate.setAddress(controller.address);
        operate.setPower(controller.power);
        operate.setTemperatureSet(temperatureSet);//控制温度
        operate.setMode(controller.mode);
        operate.setWind(controller.wind);
        return operate;
    }


    //场景长按显示
    public void showCenterSceneDialog(final String name, final String name2,
                                      int[] location) {

        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.promat_item_fenzu_dialog, null);
        TextView promat_txt;
        ImageView close_img;

        final ListViewForScrollView_New wv = (ListViewForScrollView_New) view.findViewById(R.id.wheel_view_wv);
        final AreaListAdapter areaListAdapter = new AreaListAdapter(HomeActivity.this, group_detail_list);
        wv.setAdapter(areaListAdapter);
        close_img = (ImageView) view.findViewById(R.id.close_img);
        promat_txt = (TextView) view.findViewById(R.id.promat_txt);
        promat_txt.setText(name + "分组设备列表");
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
        switch (current_dsc_position) {
            case 2:
                //查看分组详情
                mPresenter.show_detailcontrolList(group_list.get(i).id, group_list.get(i).name);
                group_list_long_click_position = i;
                item_click_animal(view);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        time_end();
    }

    @Override
    protected void initInject() {
        getActivityComponent(new EntityModule())
                .inject(this);
    }
}
