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

public class GetGroupList {
    @Named(EntityModule.NAME_COMMONBEAN_GROUP)
    @Inject
    CommonBean.group group;
    private Activity context;

    @Inject
    public GetGroupList(Activity context) {
        this.context = context;
    }

    /**
     * 显示设备列表
     */
    public void show_groupList(final String trim, final Onresponse onresponse) {//
        this.onresponse = onresponse;
        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CommonBean.group> group_list = new ArrayList<>();
                switch (trim) {
                    default:
                        group_list = group.querySqlList(group, SqlHelper.sqlgroup_mohu + "'%" + trim + "%'", new BaseDao.onresponse() {

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
                        group_list = group.queryList(group, new BaseDao.onresponse() {

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

                if (group_list.size() == 0) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!iswifi) {
                                        ToastUtil.showToast(context, "分组控制列表为空");
                                        iswifi = false;
                                    }
                                }
                            });
                        }
                    });
                } else {
                    List<Map> group_show_list = new ArrayList<>();
                    for (CommonBean.group group : group_list) {
                        Map map = new HashMap();
                        map.put("name", group.name);
                        map.put("type_item", "分组控制");
                        group_show_list.add(map);
                    }
                    onresponse.onresult(group_show_list, group_list);
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

    public void show_detailcontrolList(final int groupId, final String name, final Onresponse_1 onresponse_1) {
        this.onresponse_1 = onresponse_1;
        //去显示选中项设备显示；
        new Thread(new Runnable() {
            @Override
            public void run() {

                CommonBean.GroupDetail user = new CommonBean.GroupDetail();//直接new为查询全部user表中的数据
                List<CommonBean.GroupDetail> group_detail_list = user.querySqlList(user, SqlHelper.sqlgroupLongCLick + groupId + ")"
                        + sqlorderby, new BaseDao.onresponse() {

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
                if (group_detail_list.size() == 0) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!iswifi) {
                                        ToastUtil.showToast(context, "分组控制详情列表为空");
                                        iswifi = false;
                                    }
                                }
                            });
                        }
                    });
                } else {
                    onresponse_1.onresult(group_detail_list);
                }
            }
        }).start();
    }

    public interface Onresponse {
        void onresult(List<Map> group_show_list, List<CommonBean.group> group_list);
    }

    private Onresponse_1 onresponse_1;

    public interface Onresponse_1 {
        void onresult(List<CommonBean.GroupDetail> group_detail_list);
    }

}
