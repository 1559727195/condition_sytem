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
import com.massky.conditioningsystem.view.DownLoadProgressbar;
import com.massky.conditioningsystem.view.ListViewAdaptWidth;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    //小红点开始坐标
    Point mCircleStartPoint = new Point();
    //小红点结束坐标
    Point mCircleEndPoint = new Point();
    //小红点控制点坐标
    Point mCircleConPoint = new Point();
    //小红点的移动坐标
    Point mCircleMovePoint = new Point();
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
        intfirst_time = 1;
    }

    @Override
    protected void onEvent() {
        init_device_onclick();
    }




    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtil.showToast(HomeActivity.this, msg.obj.toString());
                    break;
                case 3://去显示场景列表
                    show_scenes();
                    break;
                case 4://去显示分组控制列表
                    show_groups();
                    break;
                case 5://去显示分组控制详情列表
                    showCenterSceneDialog(msg.obj.toString(), "231", null);
                    break;
                case 7://根据控制结果，去获取status
                    get_operate_status_byid((long) msg.obj);
                    break;
                case 8://去控制设备之后返回100或101
                    on_control_scuess();
                    break;

            }
        }
    };

    /**
     * 通过id获取operate的status
     *
     * @param operate_id
     */
    private void get_operate_status_byid(final long operate_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommonBean.operate operate = new CommonBean.operate();

                operate.setId(operate_id);
                operate_list = operate.queryList(operate, new BaseDao.onresponse() {
                    @Override
                    public void onresponse(final String content) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                message.obj = content;
                                message.what = 0;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });

                if (operate_list.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(HomeActivity.this, "控制操作失效");
                                }
                            });
                        }
                    });
                } else {//得到控制后的返回值
                    handler.sendEmptyMessage(8);
                }
            }
        }).start();

    }

    /**
     * 控制设备成功或失败
     */
    private void on_control_scuess() {
        int status = operate_list.get(0).status;
        switch (status) {
            case 100:
                ToastUtil.showToast(HomeActivity.this, "控制成功");
                break;
            case 101:
                ToastUtil.showToast(HomeActivity.this, "控制失败");
                break;
        }
        excute_delete_control();

    }

    /**
     * 执行删除动作
     */
    private void excute_delete_control() {
        CommonBean.operate operate = new CommonBean.operate();
        operate.setId(operate_list.get(0).id);
        operate.deleteList(operate);
    }




    /**
     * 显示分组控制列表
     */
    private void show_groups() {
        group_show_list = new ArrayList<>();
        for (CommonBean.group group : group_list) {
            Map map = new HashMap();
            map.put("name", group.name);
            map.put("type_item", "分组控制");
            group_show_list.add(map);
        }
//                    mDragGridView.setAdapter(deviceListAdapter);//设备侧栏列表
        deviceListAdapter.setList(group_show_list);
        deviceListAdapter.notifyDataSetChanged();
    }

    /**
     * 显示场景列表
     */
    private void show_scenes() {
        scene_show_list = new ArrayList<>();
        for (CommonBean.scene scene : scene_list) {
            Map map = new HashMap();
            map.put("name", scene.name);
            map.put("type_item", "场景");
            scene_show_list.add(map);
        }
//                    mDragGridView.setAdapter(deviceListAdapter);//设备侧栏列表
        deviceListAdapter.setList(scene_show_list);
        deviceListAdapter.notifyDataSetChanged();
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
                show_sceneList();
                break;
            case 2:
                show_controlList();
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
                show_sceneList();
                break;
            case 2:
                show_controlList();
                break;
        }
    }


    /**
     * 显示场景列表
     */
    private void show_sceneList() {
        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommonBean.scene user = new CommonBean.scene();//直接new为查询全部user表中的数据

                scene_list = user.queryList(user, new BaseDao.onresponse() {

                    @Override
                    public void onresponse(final String content) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                message.obj = content;
                                message.what = 0;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
                if (scene_list.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(HomeActivity.this, "场景列表为空");
                                }
                            });
                        }
                    });
                } else {
                    handler.sendEmptyMessage(3);
                }
            }
        }).start();
    }

    /**
     * 显示分组控制列表
     */
    private void show_controlList() {
        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommonBean.group user = new CommonBean.group();//直接new为查询全部user表中的数据

                group_list = user.queryList(user, new BaseDao.onresponse() {

                    @Override
                    public void onresponse(final String content) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                message.obj = content;
                                message.what = 0;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
                if (group_list.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(HomeActivity.this, "分组控制列表为空");
                                }
                            });
                        }
                    });
                } else {
                    handler.sendEmptyMessage(4);
                }
            }
        }).start();
    }

    /**
     * 提交控制动作
     */
    private void show_control_device(final String sql, final CommonBean.operate operate, final String selectMaxId) {
        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object operate_max_id = operate.insertSqlList(operate, sql + selectMaxId, new BaseDao.onresponse() {
                    @Override
                    public void onresponse(final String content) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                message.obj = content;
                                message.what = 0;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });

                //用operate_max_id去查询status,
                if (operate_max_id == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(HomeActivity.this, "数据库插入失败");
                                }
                            });
                        }
                    });
                } else {
                    Message message = Message.obtain();
                    message.obj = Long.valueOf(operate_max_id.toString()).longValue();
                    message.what = 7;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 显示分组控制详情列表
     */
    private void show_detailcontrolList(final int groupId, final String name) {
        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {

                CommonBean.GroupDetail user = new CommonBean.GroupDetail();//直接new为查询全部user表中的数据

                group_detail_list = user.querySqlList(user, SqlHelper.sqlgroupLongCLick + groupId + ")", new BaseDao.onresponse() {

                    @Override
                    public void onresponse(final String content) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                message.obj = content;
                                message.what = 0;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
                if (group_detail_list.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(HomeActivity.this, "分组控制详情列表为空");
                                }
                            });
                        }
                    });
                } else {
                    Message message = Message.obtain();
                    message.obj = name;
                    message.what = 5;
                    handler.sendMessage(message);
                }
            }
        }).start();
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


    /**
     * 设置开始点和移动点
     *
     * @param x
     * @param y
     */
    public void setCircleStartPoint(int x, int y) {
        this.mCircleStartPoint.x = x;
        this.mCircleStartPoint.y = y;
        this.mCircleMovePoint.x = x;
        this.mCircleMovePoint.y = y;
    }

    /**
     * 设置结束点
     *
     * @param x
     * @param y
     */
    public void setCircleEndPoint(int x, int y) {
        this.mCircleEndPoint.x = x;
        this.mCircleEndPoint.y = y;
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (mCircleStartPoint == null || mCircleEndPoint == null) {
            return;
        }


        //设置控制点
        mCircleConPoint.x = ((mCircleStartPoint.x + mCircleEndPoint.x) / 2);
        mCircleConPoint.y = (20);

        //设置值动画
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new CirclePointEvaluator(), mCircleStartPoint, mCircleEndPoint);
        valueAnimator.setDuration(600);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point goodsViewPoint = (Point) animation.getAnimatedValue();
                mCircleMovePoint.x = goodsViewPoint.x;
                mCircleMovePoint.y = goodsViewPoint.y;
//                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                ViewGroup viewGroup = (ViewGroup) getParent();
//                viewGroup.removeView(GoodsView.this);
            }
        });
        valueAnimator.start();

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
     * @param controller_show_list
     * @param controller_list
     */
    @Override
    public void show_deviceList(final List<Map> controller_show_list, List<CommonBean.controller> controller_list) {
        this.controller_show_list = controller_show_list;
        this.controller_list =controller_list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceListAdapter.setList(controller_show_list);
                deviceListAdapter.notifyDataSetChanged();
            }
        });
    }


    public class CirclePointEvaluator implements TypeEvaluator {

        /**
         * @param t          当前动画进度
         * @param startValue 开始值
         * @param endValue   结束值
         * @return
         */
        @Override
        public Object evaluate(float t, Object startValue, Object endValue) {

            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            int x = (int) (Math.pow((1 - t), 2) * startPoint.x + 2 * (1 - t) * t * mCircleConPoint.x + Math.pow(t, 2) * endPoint.x);
            int y = (int) (Math.pow((1 - t), 2) * startPoint.y + 2 * (1 - t) * t * mCircleConPoint.y + Math.pow(t, 2) * endPoint.y);
            return new Point(x, y);
        }
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name1, final String name2, int[] location, final CommonBean.controller controller) {
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
                if (tempture[0] < 16) {
                    tempture[0] = 16;
                }
                if (tempture[0] < 30)
                    tempture[0]++;
                tempture_txt.setText(tempture[0] + "" + "℃");

                CommonBean.operate operate = new CommonBean.operate();//直接new为查询全部user表中的数据
                operate.setStatus(1);
                operate.setFlag(0);
                operate.setIp(SqlHelper.sqlcontrol_ip + controller.communicatorID + ")");
                operate.setAddress(controller.address);
                operate.setPower(controller.power);
                operate.setTemperatureSet(tempture[0]);//控制温度
                operate.setMode(controller.mode);
                operate.setWind(controller.wind);
                //提交温度
                show_control_device(SqlHelper.getString(SqlHelper.sqlcontrol, operate, CommonBean.operate.class), operate, SqlHelper.selectMaxid);//
        /*        1. 主键ID 自增 ，插入数据后返回这条数据的ID值
                insert into tableName() values() select @@identity*/

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
                show_detailcontrolList(group_list.get(i).id, group_list.get(i).name);
                item_click_animal(view);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTimerUtil.cancel();
    }

    @Override
    protected void initInject() {
        getActivityComponent(new EntityModule())
                .inject(this);
    }
}
