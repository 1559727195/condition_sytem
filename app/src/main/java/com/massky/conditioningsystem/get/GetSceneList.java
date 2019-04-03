package com.massky.conditioningsystem.get;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.massky.conditioningsystem.Util.ToastUtil;
import com.massky.conditioningsystem.activity.HomeActivity;
import com.massky.conditioningsystem.di.module.EntityModule;
import com.massky.conditioningsystem.sql.BaseDao;
import com.massky.conditioningsystem.sql.CommonBean;
import com.massky.conditioningsystem.sql.SqlHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import static com.massky.conditioningsystem.sql.SqlHelper.sqlorderby;

public class GetSceneList {
    @Named(EntityModule.NAME_COMMONBEAN_SCENE)
    @Inject
    CommonBean.scene scene;
    private Activity context;

    @Inject
    public GetSceneList(Activity context) {
        this.context = context;
    }

    /**
     * 显示设备列表
     */
    public void show_sceneList(final String trim, final Onresponse onresponse) {//
        this.onresponse = onresponse;

        //去显示选中项设备显示；
        new Thread(new Runnable() {
            List<CommonBean.scene> scene_list = new ArrayList<>();

            @Override
            public void run() {
                switch (trim) {
                    default:
                        scene_list = scene.querySqlList(scene, SqlHelper.sqlscene_mohu + "'%" + trim + "%'", new BaseDao.onresponse() {

                            @Override
                            public void onresponse(final String content) {
                                context.runOnUiThread(new Runnable() {
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
                        break;
                    case "":
                        scene_list = scene.queryList(scene, new BaseDao.onresponse() {

                            @Override
                            public void onresponse(final String content) {
                                context.runOnUiThread(new Runnable() {
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
                        break;
                }


                if (scene_list.size() == 0) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!iswifi) {
                                        ToastUtil.showToast(context, "场景列表为空");
                                        iswifi = false;
                                    }
                                }
                            });
                        }
                    });
                } else {
                    List<Map> scene_show_list = new ArrayList<>();
                    for (CommonBean.scene scene : scene_list) {
                        Map map = new HashMap();
                        map.put("name", scene.name);
                        map.put("type_item", "场景");
                        scene_show_list.add(map);
                    }
                    onresponse.onresult(scene_show_list, scene_list);
                }
            }

        }).start();
    }


    boolean iswifi;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    iswifi = true;
                    ToastUtil.showToast(context, msg.obj.toString());
                    break;
            }
        }
    };

    private Onresponse onresponse;

    public void show_scenecontroller(final int id, final Onresponse_1 onresponse_1) {
        this.onresponse_1 = onresponse_1;
        final CommonBean.sceneDetail sceneDetail = new CommonBean.sceneDetail();


        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CommonBean.sceneDetail> scene_detail_list = sceneDetail.querySqlList(sceneDetail,
                        SqlHelper.sqlsceneLongCLick_one_air + id + SqlHelper.sqlsceneLongCLick_two + id + sqlorderby, new BaseDao.onresponse() {

                            @Override
                            public void onresponse(final String content) {
                                context.runOnUiThread(new Runnable() {//order by control.name->排序
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
                if (scene_detail_list.size() == 0) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!iswifi) {
                                        ToastUtil.showToast(context, "场景详情列表为空");
                                        iswifi = false;
                                    }
                                }
                            });
                        }
                    });
                } else {
                    List<Map> scene_detail_show_list = new ArrayList<>();
                    for (CommonBean.sceneDetail controllers : scene_detail_list) {
                        Map map = new HashMap();
                        map.put("name", controllers.name);
                        map.put("controllerID", controllers.id);
                        switch (controllers.type) {
                            case "空调":
                                switch (controllers.power) {
                                    case "打开":
                                        map.put("action", controllers.mode + "|" + controllers.temperatureSet + "℃|" + controllers.wind);
                                        break;
                                    case "关闭":
                                        map.put("action", "关闭");
                                        break;
                                }
                                break;
                            case "新风":
                                map.put("action", controllers.power);
                                break;
                        }
                        scene_detail_show_list.add(map);
                    }
                    if (onresponse_1 != null) onresponse_1.onresult(scene_detail_show_list);
                }
            }

        }).start();
    }

    public interface Onresponse {
        void onresult(List<Map> scene_show_list, List<CommonBean.scene> scene_list);
    }

    private Onresponse_1 onresponse_1;

    public interface Onresponse_1 {
        void onresult(List<Map> scene_detail_show_list);
    }

}
