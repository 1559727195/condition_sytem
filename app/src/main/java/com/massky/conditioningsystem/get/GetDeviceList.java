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

public class GetDeviceList {
    @Named(EntityModule.NAME_COMMONBEAN_CONTROLLER)
    @Inject
    CommonBean.controller controller;
    private Activity context;

    @Inject
    public GetDeviceList(Activity context) {
        this.context = context;
    }

    /**
     * 显示设备列表
     */
    public void show_deviceList(final Onresponse onresponse) {//
        this.onresponse = onresponse;
        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<CommonBean.controller> controller_list = controller.queryList(controller, new BaseDao.onresponse() {

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
                if (controller_list.size() == 0) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(context, "设备列表为空");
                                }
                            });
                        }
                    });
                } else {
                    List<Map> controller_show_list = getMaps(controller_list);
                    onresponse.onresult(controller_show_list, controller_list);
                }
            }

        }).start();
    }

    private List<Map> getMaps(List<CommonBean.controller> controller_list) {
        List<Map> controller_show_list = new ArrayList<>();
        for (CommonBean.controller controllers : controller_list) {
            Map map = new HashMap();
            map.put("name", controllers.name);
            map.put("power", controllers.power);
            map.put("type_item", "设备");
            switch (controllers.status) {
                case "在线":
                    map.put("action", controllers.mode + "|" + controllers.temperatureSet + "℃|" + controllers.wind);
                    break;
                case "断线":
                    map.put("action", "");
                    break;
            }
            controller_show_list.add(map);
        }
        return controller_show_list;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    break;
            }
        }
    };

    private Onresponse onresponse;

    public interface Onresponse {
        void onresult(List<Map> controller_show_list, List<CommonBean.controller> controller_list);
    }

}
