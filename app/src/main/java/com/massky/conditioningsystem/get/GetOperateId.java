package com.massky.conditioningsystem.get;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.massky.conditioningsystem.Util.ToastUtil;
import com.massky.conditioningsystem.activity.HomeActivity;
import com.massky.conditioningsystem.di.module.EntityModule;
import com.massky.conditioningsystem.sql.BaseDao;
import com.massky.conditioningsystem.sql.CommonBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

public class GetOperateId {
    @Named(EntityModule.NAME_COMMONBEAN_OPERATE)
    @Inject
    CommonBean.operate operate;
    private Activity context;

    @Inject
    public GetOperateId(Activity context) {
        this.context = context;
    }

    /**
     * 显示设备列表
     */
    public void show_operateId(final Onresponse onresponse, final String sql, final CommonBean.operate operate,
                               final String selectMaxId) {//
        this.onresponse = onresponse;
        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object operate_max_id = operate.insertSqlList(operate, sql + selectMaxId, new BaseDao.onresponse() {
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

                //用operate_max_id去查询status,
                if (operate_max_id == null) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!iswifi) {
                                        ToastUtil.showToast(context, "数据库插入失败");
                                        iswifi = false;
                                    }
                                }
                            });
                        }
                    });
                } else {
                    onresponse.onresult(Long.valueOf(operate_max_id.toString()).longValue());
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

    /**
     * 根据最大maxid 去获取status
     *
     * @param operate_max_id
     */
    public void show_operateStatus(final long operate_max_id, final Onresponse_1 onresponse_1) {
        this.onresponse_1 = onresponse_1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                operate = new CommonBean.operate();
                operate.setId(operate_max_id);

                List<CommonBean.operate> operate_list = operate.queryList(operate, new BaseDao.onresponse() {
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

                if (operate_list.size() == 0) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!iswifi) {
//                                        ToastUtil.showToast(context, "控制操作失效");
                                        iswifi = false;
                                    }
                                }
                            });
                        }
                    });
                } else {//得到控制后的返回值
                    onresponse_1.onresult(operate_list);
                }
            }
        }).start();
    }

    public interface Onresponse {
        void onresult(long operate_max_id);
    }

    private Onresponse_1 onresponse_1;

    public interface Onresponse_1 {
        void onresult(List<CommonBean.operate> operate_list);
    }


}
