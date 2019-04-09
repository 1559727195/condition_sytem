package com.massky.conditioningsystem.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.andview.refreshview.XRefreshView;
import com.massky.conditioningsystem.R;
import com.massky.conditioningsystem.Util.DialogUtil;
import com.massky.conditioningsystem.Util.ListViewForScrollView_New;
import com.massky.conditioningsystem.Util.SharedPreferencesUtil;
import com.massky.conditioningsystem.Util.ToastUtil;
import com.massky.conditioningsystem.adapter.AreaListAdapter;
import com.massky.conditioningsystem.adapter.DetailDeviceHomeAdapter;
import com.massky.conditioningsystem.adapter.HomeDeviceListAdapter;
import com.massky.conditioningsystem.base.BaseActivity;
import com.massky.conditioningsystem.di.module.EntityModule;
import com.massky.conditioningsystem.presenter.HomePresenter;
import com.massky.conditioningsystem.presenter.contract.HomeContract;
import com.massky.conditioningsystem.sql.CommonBean;
import com.massky.conditioningsystem.sql.DBUtilNew;
import com.massky.conditioningsystem.sql.SqlHelper;
import com.massky.conditioningsystem.view.ClearEditText;
import com.massky.conditioningsystem.view.CommomDialog;
import com.massky.conditioningsystem.view.SceneDialog;
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
    @InjectView(R.id.login_out)
    TextView login_out;
    @InjectView(R.id.search_txt)
    TextView search_txt;
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
    private boolean popcheck_init;
    private int scene_list_long_click_position;
    private View view_long_click;
    private int item_click_position;
    private String fuzzy_query = "";

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
                mPresenter.getSqlCounts();
                fuzzy_query = "";
                get_list(fuzzy_query);
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
        login_out.setOnClickListener(this);
        search_txt.setOnClickListener(this);
        init_device_onclick();
    }

    /**
     * 控制设备成功或失败
     */
    private void on_control_scuess() {
        int status = operate_list.get(0).status;
        switch (current_dsc_position) {
            case 0://设备控制
                refreh_control_scuress(status);
                break;
            default:
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
                break;
        }
    }

    /**
     * 空调新风控制成功返回
     *
     * @param status
     */
    private void refreh_control_scuress(int status) {
        switch (controller_list.get(item_click_position).getType() == null ? "" : controller_list.get(item_click_position).getType()) {
            case "空调":
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
                break;
            case "新风":
                switch (status) {
                    case 100:
                        time_end();
                        ToastUtil.showToast(HomeActivity.this, controller_list.get(item_click_position).getPower() == null ? "控制"
                                : controller_list.get(item_click_position).getPower() +
                                "成功");

                        break;
                    case 101:
                        time_end();
                        ToastUtil.showToast(HomeActivity.this, controller_list.get(item_click_position).getPower() == null ? "控制"
                                : controller_list.get(item_click_position).getPower() +
                                "失败");
                        break;
                }
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
                if (operate_list.size() == 0) {
                    return;
                }
                CommonBean.operate operate = new CommonBean.operate();
                operate.setId(operate_list.get(0).id);
                operate.deleteList(operate);
            }
        }).start();
    }

    @Override
    protected void onData() {
        init_ip();
        room_list_show_adapter();
        device_list_show_adapter();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_out:
                showCenterDeleteDialog("是否退出登录?");
                break;
            case R.id.search_txt://模糊查询
                showSearchDialog("");
                break;
        }
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        name_gloud.setText(name);
        tv_title.setVisibility(View.GONE);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(HomeActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayHeight / 3 * 2); //宽度设置为屏幕的0.5
//        p.height = (int) (p.width / 3 * 2); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_out();
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void login_out() {
        HomeActivity.this.finish();
        startActivity(new Intent(HomeActivity.this, LoginCloudActivity.class));
        SharedPreferencesUtil.saveData(HomeActivity.this, "loginflag", false);

        SharedPreferencesUtil.saveData(HomeActivity.this, "username", "");
        SharedPreferencesUtil.saveData(HomeActivity.this, "pass", "");
        SharedPreferencesUtil.saveData(HomeActivity.this, "localIp", "");
    }

    private void init_ip() {
        String localIp = (String) SharedPreferencesUtil.getData(HomeActivity.this, "localIp", "");
        if (!localIp.equals("")) {
            DBUtilNew.ip = localIp;
        } else {
            ToastUtil.showToast(HomeActivity.this, "请设置服务器IP");
            return;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (intfirst_time == 1) {
            intfirst_time = 2;
            current_dsc_position = 0;
        }
        mPresenter.getSqlCounts();
        get_list(fuzzy_query);
    }

    /**
     * 获取首页设备列表
     */
    private void get_list(String name) {
        switch (current_dsc_position) {
            case 0:
                mPresenter.show_deviceList(name);
                break;
            case 1:
                mPresenter.show_sceneList(name);
                break;
            case 2:
                mPresenter.show_controlList(name);
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
                item_click_position = position;
                switch (current_dsc_position) {
                    case 0:
                        air_refresh_control(position, location);
                        break;
                    case 1://场景控制
                        scene_control(position);
                        break;
                    case 2://分组控制
                        dialogShow(group_list.get(position).type, group_list.get(position).id
                                , group_list.get(position).name);
                        break;
                }
            }
        });
        mDragGridView.setOnItemLongClickListener(this);
    }

    /**
     * 新风空调设备控制
     *
     * @param position
     * @param location
     */
    private void air_refresh_control(int position, int[] location) {
        switch (controller_list.get(position).type) {
            case "空调":
                showControlDialog(location, controller_list.get(position));
                break;
            case "新风"://新风直接去控制，
                int status = 2;
                final String[] power = {controller_list.get(position).getPower() == null ? "" : controller_list.get(position).getPower()};
                time_end();
                switch (power[0]) {
                    case "打开":
                        power[0] = "关闭";
                        break;
                    case "关闭":
                        power[0] = "打开";
                        break;
                }
                controller_list.get(position).setPower(power[0]);
                common_ctrol_device(controller_list.get(position), status);
                break;
        }
    }


    /*
     *场景控制
     */
    private void scene_control(int position) {
        time_end();
        CommonBean.operate operate = new CommonBean.operate();//直接new为查询全部user表中的数据
        operate.setStatus(5);
        operate.setFlag(0);
        operate.setAddress(scene_list.get(position).id);
        operate.setPower("打开");


        //提交温度
        mPresenter.show_control_device(SqlHelper.getString(SqlHelper.sqlcontrol, operate, CommonBean.operate.class), operate, SqlHelper.selectMaxid);
        /*        1. 主键ID 自增 ，插入数据后返回这条数据的ID值
                insert into tableName() values() select @@identity*/
        dialogUtil.loadDialog();
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
            map.put("count", "0");
            switch (i) {
                case 0:
                    map.put("name", "设备控制");
                    break;
                case 1:
                    map.put("name", "场景控制");
                    break;
                case 2:
                    map.put("name", "分组控制");
                    break;
            }
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
        fuzzy_query = "";
        get_list(fuzzy_query);
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
//        mPresenter.show_operateStatus(operate_max_id);
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
        get_list(fuzzy_query);//控制完后重新获取设备列表
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
                if (view_long_click != null)
                    item_click_animal(view_long_click);
                showCenterSceneDialog(group_list.get(group_list_long_click_position).name, "", null);
            }
        });
    }

    /**
     * 场景长按显示数据
     *
     * @param scene_detail_show_list
     */
    @Override
    public void scene_detail_show_list(final List<Map> scene_detail_show_list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view_long_click != null)
                    item_click_animal(view_long_click);
                new SceneDialog(HomeActivity.this, R.style.BottomDialog, scene_detail_show_list, scene_list.get(scene_list_long_click_position)
                        .name, new SceneDialog.OnCloseListener() {
                    @Override
                    public void onClick(String action) {

                    }
                }).setTitle("").show();
            }
        });
    }


    /**
     * 控制弹出框
     *
     * @param location
     * @param controller
     */
    public void showControlDialog(int[] location, final CommonBean.controller controller) {

        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.promat_item_pop_dialog, null);
        final TextView tempture_txt = view.findViewById(R.id.tempture_txt); //确定按钮
        final TextView mode_txt = view.findViewById(R.id.mode_txt); //确定按钮
        final TextView speed_txt = view.findViewById(R.id.speed_txt);
        TextView promat_txt = view.findViewById(R.id.promat_txt);
        ImageView temp_add = view.findViewById(R.id.temp_add);
        ImageView temp_del = view.findViewById(R.id.temp_del);
        RelativeLayout rel_moshi = view.findViewById(R.id.rel_moshi);//模式
        RelativeLayout rel_fengsu = view.findViewById(R.id.rel_fengsu);//风速
        final CheckBox tv_upper = view.findViewById(R.id.tv_upper);
        ImageView btn_guanbi = view.findViewById(R.id.btn_guanbi);
        LinearLayout linear_temp = view.findViewById(R.id.linear_temp);
        LinearLayout linear_mode = view.findViewById(R.id.linear_mode);

        Pop_dialog pop_dialog = new Pop_dialog(view).invoke();
        int displayWidth = pop_dialog.getDisplayWidth();
        int displayHeight = pop_dialog.getDisplayHeight();
        pop_animal(view, displayHeight, displayWidth, location);

        //linear_temp,linear_mode,
        air_fresh_control(controller, tempture_txt, mode_txt, speed_txt, promat_txt, temp_add, temp_del, rel_moshi, rel_fengsu, tv_upper, btn_guanbi, linear_temp, linear_mode);
    }

    /**
     * 空调新风集控系统
     *
     * @param controller
     * @param tempture_txt
     * @param mode_txt
     * @param speed_txt
     * @param promat_txt
     * @param temp_add
     * @param temp_del
     * @param rel_moshi
     * @param rel_fengsu
     * @param tv_upper
     * @param btn_guanbi
     * @param linear_temp
     * @param linear_mode
     */
    private void air_fresh_control(CommonBean.controller controller, TextView tempture_txt, TextView mode_txt, TextView speed_txt, TextView promat_txt, ImageView temp_add, ImageView temp_del, RelativeLayout rel_moshi, RelativeLayout rel_fengsu, CheckBox tv_upper, ImageView btn_guanbi, LinearLayout linear_temp, LinearLayout linear_mode) {
        int status = 0;
        final String[] power = {controller.getPower() == null ? "" : controller.getPower()};
        promat_txt.setText(controller.name);
        switch (controller.type) {
            case "空调":
                status = 1;
                linear_temp.setVisibility(View.VISIBLE);
                linear_mode.setVisibility(View.VISIBLE);
                rel_moshi.setVisibility(View.VISIBLE);
                rel_fengsu.setVisibility(View.VISIBLE);
                tempture_txt.setText(controller.getTemperatureSet() + "" + "℃");
                final int[] tempture = {controller.getTemperatureSet()};
                mode_txt.setText(controller.getMode());
                speed_txt.setText(controller.getWind());
                final String[] modeflag = {controller.getMode() == null ? "" : controller.getMode()};
                final String[] wind = {controller.getWind() == null ? "" : controller.getWind()};
                pop_click_event(controller, tempture_txt, mode_txt, speed_txt, temp_add, temp_del, rel_moshi, rel_fengsu, tv_upper, btn_guanbi, tempture, modeflag, power, wind, status);
                break;
            case "新风":
                status = 2;
                linear_temp.setVisibility(View.GONE);
                linear_mode.setVisibility(View.GONE);
                rel_moshi.setVisibility(View.GONE);
                rel_fengsu.setVisibility(View.GONE);
                final int[] tempture1 = {0};
                final String[] modeflag1 = {""};
                final String[] wind1 = {""};
                pop_click_event(controller, tempture_txt, mode_txt, speed_txt, temp_add, temp_del, rel_moshi, rel_fengsu, tv_upper, btn_guanbi, tempture1,
                        modeflag1, power, wind1, status);//不能传null,新风int = 0,
                //varchar 为"";
                break;
        }

        if (controller.getPower().equals("打开")) {
            popcheck_init = true;
        } else {
            popcheck_init = false;
        }
        tv_upper.setChecked(controller.getPower().equals("打开") ? true : false);
    }

    /**
     * pop点击事件
     *
     * @param controller
     * @param tempture_txt
     * @param mode_txt
     * @param speed_txt
     * @param temp_add
     * @param temp_del
     * @param rel_moshi
     * @param rel_fengsu
     * @param tv_upper
     * @param btn_guanbi
     * @param tempture
     * @param modeflag
     * @param power
     * @param wind
     * @param status
     */
    private void pop_click_event(final CommonBean.controller controller, final TextView tempture_txt,
                                 final TextView mode_txt, final TextView speed_txt,
                                 ImageView temp_add, ImageView temp_del,
                                 RelativeLayout rel_moshi, RelativeLayout rel_fengsu,
                                 CheckBox tv_upper, ImageView btn_guanbi,
                                 final int[] tempture, final String[] modeflag,
                                 final String[] power, final String[] wind, final int status) {
        btn_guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        temp_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//温度加
                time_end();
                temp_add(power[0], tempture, tempture_txt, controller, status);
            }
        });

        temp_del.setOnClickListener(new View.OnClickListener() {//温度减
            @Override
            public void onClick(View view) {
                time_end();
                temp_del(power[0], tempture, tempture_txt, controller, status);
            }
        });

        rel_moshi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_end();
                mode_control(power[0], modeflag, controller, mode_txt, status);
            }
        });

        rel_fengsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_end();
                speed_control(power[0], wind, controller, speed_txt, status);
            }
        });

        tv_upper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!popcheck_init) {
                    time_end();
                    power_control(b, power, controller, status);
                } else {
                    popcheck_init = false;
                }
            }
        });
    }

    /**
     * 温度-控制
     *
     * @param s
     * @param tempture
     * @param tempture_txt
     * @param controller
     * @param status
     */
    private void temp_del(String s, int[] tempture, TextView tempture_txt, CommonBean.controller controller, int status) {
        if (s.equals("打开")) {
            if (tempture[0] > 16)
                tempture[0]--;
            tempture_txt.setText(tempture[0] + "" + "℃");
            controller.setTemperatureSet(tempture[0]);
            common_ctrol_device(controller, status);
        } else {
            ToastUtil.showToast(HomeActivity.this, "请先打开空调");
        }
    }

    /*
     *温度+控制
     */
    private void temp_add(String s, int[] tempture, TextView tempture_txt, CommonBean.controller controller, int status) {
        //模式状态
        if (s.equals("打开")) {
            init_temper_Add(tempture, tempture_txt);
            controller.setTemperatureSet(tempture[0]);
            common_ctrol_device(controller, status);
        } else {
            ToastUtil.showToast(HomeActivity.this, "请先打开空调");
        }
    }

    /**
     * 模式控制
     *
     * @param s
     * @param modeflag
     * @param controller
     * @param mode_txt
     * @param status
     */
    private void mode_control(String s, String[] modeflag, CommonBean.controller controller, TextView mode_txt, int status) {
        //模式状态
        if (s.equals("打开")) {
            switch (modeflag[0]) {
                case "制冷":
                    modeflag[0] = "制热";
                    break;
                case "制热":
                    modeflag[0] = "通风";
                    break;
                case "通风":
                    modeflag[0] = "自动";
                    break;
                case "自动":
                    modeflag[0] = "制冷";
                    break;
            }
            controller.setMode(modeflag[0]);
            common_ctrol_device(controller, status);


        } else {
            ToastUtil.showToast(HomeActivity.this, "请先打开空调");
        }

        mode_txt.setText(modeflag[0]);
    }

    /**
     * 开关控制
     *
     * @param b
     * @param power
     * @param controller
     * @param status
     */
    private void power_control(boolean b, String[] power, CommonBean.controller controller, int status) {
        if (b) {
            power[0] = "打开";
        } else {
            power[0] = "关闭";
        }

        controller.setPower(power[0]);
        common_ctrol_device(controller, status);
    }

    /**
     * 风速控制
     *
     * @param s
     * @param wind
     * @param controller
     * @param speed_txt
     * @param status
     */
    private void speed_control(String s, String[] wind, CommonBean.controller controller, TextView speed_txt, int status) {
        //模式状态
        if (s.equals("打开")) {
            switch (wind[0]) {
                case "低风":
                    wind[0] = "中风";
                    break;
                case "中风":
                    wind[0] = "高风";
                    break;
                case "高风":
                    wind[0] = "自动";
                    break;
                case "自动":
                    wind[0] = "低风";
                    break;
            }

            controller.setWind(wind[0]);
            common_ctrol_device(controller, status);

        } else {
            ToastUtil.showToast(HomeActivity.this, "请先打开空调");
        }
        speed_txt.setText(wind[0]);
    }

    /**
     * 设备控制
     *
     * @param controller
     */
    private void common_ctrol_device(CommonBean.controller controller, int status) {
        CommonBean.operate operate = init_operate_params(controller, status);
        //提交温度
        mPresenter.show_control_device(SqlHelper.getString(SqlHelper.sqlcontrol, operate, CommonBean.operate.class), operate, SqlHelper.selectMaxid);
        /*        1. 主键ID 自增 ，插入数据后返回这条数据的ID值
                insert into tableName() values() select @@identity*/
        dialogUtil.loadDialog();
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
     * @return
     */
    private CommonBean.operate init_operate_params(CommonBean.controller controller, int status) {
        CommonBean.operate operate = new CommonBean.operate();//直接new为查询全部user表中的数据
        operate.setStatus(status);
        operate.setFlag(0);
        operate.setIp(SqlHelper.sqlcontrol_ip + controller.communicatorID + ")");
        operate.setAddress(controller.address);
        operate.setPower(controller.power);
        switch (status) {
            case 1://空调
                operate.setTemperatureSet(controller.temperatureSet);//控制温度
                operate.setMode(controller.mode);
                operate.setWind(controller.wind);
                break;
            case 2://新风
                operate.setTemperatureSet(0);//控制温度
                operate.setMode("");
                operate.setWind("");
                break;
        }

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
        view_long_click = view;
        switch (current_dsc_position) {
            case 1://场景长按，场景长按获取执行动作
                mPresenter.show_scenecontroller(scene_list.get(i).id);
                scene_list_long_click_position = i;
                return true;
            case 2:
                //查看分组详情
                mPresenter.show_detailcontrolList(group_list.get(i).id, group_list.get(i).name);
                group_list_long_click_position = i;
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

    private class Pop_dialog {
        private View view;
        private int displayWidth;
        private int displayHeight;

        public Pop_dialog(View view) {
            this.view = view;
        }

        public int getDisplayWidth() {
            return displayWidth;
        }

        public int getDisplayHeight() {
            return displayHeight;
        }

        public Pop_dialog invoke() {
            //显示数据
            dialog1 = new Dialog(HomeActivity.this, R.style.BottomDialog);
            dialog1.setContentView(view);
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setCancelable(true);//设置它可以取消
            dialog1.setCanceledOnTouchOutside(false);

            DisplayMetrics dm = getResources().getDisplayMetrics();
            displayWidth = dm.widthPixels;
            displayHeight = dm.heightPixels;
            android.view.WindowManager.LayoutParams p = dialog1.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = (int) (displayHeight / 3 * 2); //宽度设置为屏幕的0.5
            p.height = (int) (p.width); //宽度设置为屏幕的0.5
            dialog1.getWindow().setAttributes(p);  //设置生效
            dialog1.show();
            return this;
        }
    }

    private void dialogShow(final String type, final int id, final String name) {
        time_end();
        new CommomDialog(type, name, HomeActivity.this, R.style.BottomDialog, "", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(String action) {
                //提交温度
                air_fresh_group_control(action, id, type);
            }
        }).setTitle("").show();
    }

    /**
     * 空调新风组控制
     *
     * @param action
     * @param id
     * @param type
     */
    private void air_fresh_group_control(String action, int id, String type) {
        CommonBean.operate operate = new CommonBean.operate();//直接new为查询全部user表中的数据
        operate.setFlag(0);
        operate.setAddress(id);
        switch (type) {
            case "空调":
                operate.setStatus(3);
                if (action != null) {
                    switch (action) {
                        case "关闭":
                            operate.setPower(action);
                            operate.setTemperatureSet(0);//控制温度
                            operate.setMode("");
                            operate.setWind("");
                            break;
                        default:
                            operate.setPower("打开");
                            String[] splits = action.split("\\|");
                            if (splits != null && splits.length == 3) {
                                operate.setTemperatureSet(Integer.parseInt(splits[1]));//控制温度
                                operate.setMode(splits[2]);
                                operate.setWind(splits[0]);
                            }
                            break;
                    }
                }
                break;
            case "新风":
                operate.setStatus(4);
                if (action != null) {
                    switch (action) {
                        case "关闭":
                            operate.setPower(action);
                            break;
                        default:
                            operate.setPower("打开");
                    }

                    operate.setTemperatureSet(0);//控制温度
                    operate.setMode("");
                    operate.setWind("");
                }
                break;
        }


        mPresenter.show_control_device(SqlHelper.getString(SqlHelper.sqlcontrol, operate, CommonBean.operate.class), operate, SqlHelper.selectMaxid);
        /*        1. 主键ID 自增 ，插入数据后返回这条数据的ID值
                insert into tableName() values() select @@identity*/
        dialogUtil.loadDialog();
    }


    //自定义dialog,模糊查询

    public void showSearchDialog(final String name) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        final View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.edit_search_dialog, null);
        final TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        final ClearEditText edit_password_gateway = (ClearEditText) view.findViewById(R.id.edit_password_gateway);
        edit_password_gateway.setText(name);
        edit_password_gateway.setSelection(edit_password_gateway.getText().length());
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(HomeActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayHeight / 3 * 2); //宽度设置为屏幕的0.5
        p.height = (int) (p.width / 2); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_password_gateway.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(HomeActivity.this, "查询条件为空");
                    return;
                }
                fuzzy_query = SqlHelper.sqlencode(edit_password_gateway.getText().toString().trim());
                get_list(fuzzy_query);
                dialog.dismiss();
            }
        });
    }
}
