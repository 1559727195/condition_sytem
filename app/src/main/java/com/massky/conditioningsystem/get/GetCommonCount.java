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

public class GetCommonCount {
    @Named(EntityModule.NAME_COMMONBEAN)
    @Inject
    CommonBean.Count count;
    private Activity context;

    @Inject
    public GetCommonCount(Activity context) {
        this.context = context;
    }

    /**
     * 连表查询获取设备控制，场景控制，分组控制数据个数
     */
    public void sqlCounts(final Onresponse onresponse) {
        this.onresponse = onresponse;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CommonBean.Count> counts = count.querySqlList(count, SqlHelper.sqlCount, new BaseDao.onresponse() {
                    @Override
                    public void onresponse(String content) {
                        Message message = Message.obtain();
                        message.obj = content;
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                });

                if (counts.size() == 0) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!iswifi) {
                                ToastUtil.showToast(context, "设备，场景，组控列表为空");
                                iswifi = false;
                            }
                        }
                    });
                } else {
                    List<Map> list_dsc_count = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        Map map = new HashMap();
                        switch (i) {
                            case 0:
                                map.put("count", counts.get(0).deviceCount + "台设备");
                                map.put("name", "设备控制");
                                break;
                            case 1:
                                map.put("count", counts.get(0).sceneCount + "个场景");
                                map.put("name", "场景控制");
                                break;
                            case 2:
                                map.put("count", counts.get(0).controlCount + "个分组");
                                map.put("name", "分组控制");
                                break;
                        }
                        list_dsc_count.add(map);
                    }
                    onresponse.onresult(list_dsc_count);
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

    private  Onresponse onresponse;
    public interface Onresponse {
        void onresult(List<Map> list);
    }

}
