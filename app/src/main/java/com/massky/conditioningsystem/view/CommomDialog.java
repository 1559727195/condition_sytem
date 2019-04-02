package com.massky.conditioningsystem.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.massky.conditioningsystem.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.massky.conditioningsystem.sql.DBUtilNew.close;

public class CommomDialog extends Dialog implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private String name;
    private String type;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.project_select)
    TextView project_select;
    private Context mContext;
    private String content;
    @InjectView(R.id.view_speed)
    View view_speed;
    @InjectView(R.id.view_mode)
    View view_mode;
    @InjectView(R.id.view_temp)
    View view_temp;


    /**
     * 空调
     *
     * @return
     */

    @InjectView(R.id.air_control_radio_model)
    RadioGroup air_control_radio_model;
    @InjectView(R.id.air_control_tmp_del)
    ImageView air_control_tmp_del;
    //air_control_tmp_add
    @InjectView(R.id.air_control_tmp_add)
    ImageView air_control_tmp_add;
    @InjectView(R.id.air_control_speed)
    RadioGroup air_control_speed;
    @InjectView(R.id.air_control_radio_open_close)
    RadioGroup air_control_radio_open_close;
    @InjectView(R.id.tmp_txt)
    TextView tmp_txt;
    private Map air_control_map = new HashMap();
    private int tempture;
    private Map sensor_map = new HashMap();//传感器map
    @InjectView(R.id.mode_linear)
    LinearLayout mode_linear;
    @InjectView(R.id.linear_temp)
    LinearLayout linear_temp;
    @InjectView(R.id.linear_speed)
    LinearLayout linear_speed;


    private Map map_panel = new HashMap();
    private OnCloseListener listener;
    private String title;
    private String positiveName;
    private String negativeName;

    /**
     * 空调
     *
     * @return
     */


    public CommomDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CommomDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public CommomDialog(String type, String name, Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
        this.type = type;
        this.name = name;
    }

    protected CommomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CommomDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CommomDialog setPositiveButton(String name) {
        this.positiveName = name;
        return this;
    }

    public CommomDialog setNegativeButton(String name) {
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.air_linkage_control_act);
        setCanceledOnTouchOutside(false);
        ButterKnife.inject(this);
        init_dialog_window();
        initData();
        init_action_select();
        init_action();
        onEvent();
    }

    private void initData() {
        switch (type == null ? "" : type) {

            case "空调":

                break;
            case "新风":
                view_speed.setVisibility(View.GONE);
                view_mode.setVisibility(View.GONE);
                view_temp.setVisibility(View.GONE);
                mode_linear.setVisibility(View.GONE);
                linear_temp.setVisibility(View.GONE);
                linear_speed.setVisibility(View.GONE);

                break;
        }
        project_select.setText(name);
        air_control_map.put("status", "1");
        air_control_map.put("speed", "1");
        air_control_map.put("mode", "1");
        air_control_map.put("temperature", "20");
    }

    private void init_dialog_window() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayHeight / 6 * 5); //宽度设置为屏幕的0.5
        p.height = (int) (displayHeight / 6 * 4 + displayHeight / 10); //宽度设置为屏幕的0.5
        getWindow().setAttributes(p);  //设置生效
    }


    private void onEvent() {
        air_control_radio_model();
        air_control_speed();
        air_control_radio_open_close();
        air_control_tmp_del.setOnClickListener(this);
        air_control_tmp_add.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    /**
     * 初始化空调动作
     */
    private void init_action_select() {

        //温度选择
        String tempture = (String) air_control_map.get("temperature");
        if (Integer.parseInt(tempture) < 16) {
            tempture = 16 + "";
        }
        tmp_txt.setText(tempture + "");

        //状态，在线，离线
        String status = (String) air_control_map.get("status");
        switch (status) {
            case "1":
                air_control_radio_open_close.check(R.id.radio_status_one);
                break;
            case "0":
                air_control_radio_open_close.check(R.id.radio_status_two);
                break;
        }

        String speed = (String) air_control_map.get("speed");
        switch (speed) {
            case "1":
                air_control_speed.check(R.id.radio_one_speed);
                break;
            case "2":
                air_control_speed.check(R.id.radio_two_speed);
                break;
            case "3":
                air_control_speed.check(R.id.radio_three_speed);
                break;
            case "6":
                air_control_speed.check(R.id.radio_four_speed);
                break;
            default:
                air_control_speed.check(R.id.radio_four_speed);
                break;
        }
        String model = (String) air_control_map.get("mode");

        switch (model) {
            case "1":
                air_control_radio_model.check(R.id.radio_one_model);
                break;
            case "2":
                air_control_radio_model.check(R.id.radio_two_model);
                break;
            case "3":
                air_control_radio_model.check(R.id.radio_three_model);
                break;
            case "4":
                air_control_radio_model.check(R.id.radio_four_model);
                break;
            default:
                air_control_radio_model.check(R.id.radio_four_model);
                break;
        }
    }


    /**
     * 风速
     */
    private void air_control_speed() {
        /**
         * 风速
         */
        for (int i = 0; i < air_control_speed.getChildCount(); i++) {
            final View view = air_control_speed.getChildAt(i);
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) view.getTag();
                    switch (position) {
                        case 0:
                            common_doit("speed", "1");
                            break;
                        case 1:
                            common_doit("speed", "2");
                            break;
                        case 2:
                            common_doit("speed", "3");
                            break;
                        case 3:
                            common_doit("speed", "6");
                            break;
                    }
                }
            });
        }
    }

    /**
     * 开关
     */
    private void air_control_radio_open_close() {
        /**
         * 开关
         */
        for (int i = 0; i < air_control_radio_open_close.getChildCount(); i++) {
            final View view = air_control_radio_open_close.getChildAt(i);
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) view.getTag();
                    switch (position) {
                        case 0:
                            common_doit("status", "1");
                            break;
                        case 1:
                            common_doit("status", "0");
                            break;
                    }
                }
            });
        }
    }

    /**
     * 公共项
     *
     * @param status
     * @param value
     */
    private void common_doit(String status, String value) {
        air_control_map.put(status, value);
    }

    /**
     * 模式
     */
    private void air_control_radio_model() {
        /**
         * 模式
         */
        for (int i = 0; i < air_control_radio_model.getChildCount(); i++) {
            final View view = air_control_radio_model.getChildAt(i);
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) view.getTag();
                    switch (position) {
                        case 0:
                            common_doit("mode", "1");
                            break;
                        case 1:
                            common_doit("mode", "2");
                            break;
                        case 2:
                            common_doit("mode", "3");
                            break;
                        case 3:
                            common_doit("mode", "4");
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                dismiss();
                break;
            case R.id.next_step_txt:
                init_action();
                String action = air_control_map.get("action").toString();
                if (listener != null) listener.onClick(action);
                dismiss();
                break;
            case R.id.air_control_tmp_add:
                tempture = Integer.parseInt(air_control_map.get("temperature").toString());
                if (tempture < 16) {
                    tempture = 16;
                }
                if (tempture < 30)
                    tempture++;
                tmp_txt.setText(tempture + "");
                common_doit("temperature", "" + tempture);
                break;
            case R.id.air_control_tmp_del:
                tempture = Integer.parseInt(air_control_map.get("temperature").toString());
                if (tempture > 16)
                    tempture--;
                tmp_txt.setText(tempture + "");
                common_doit("temperature", "" + tempture);
                break;
        }
    }

    /**
     * 初始化空调动作
     */
    private void init_action() {
        String status = (String) air_control_map.get("status");
        StringBuffer temp = new StringBuffer();
        switch (status) {
            case "1":
                temp = onLine();
                break;
            case "0":
                temp.append("关闭");
                break;
        }
        common_doit("action", temp.toString());
    }

    private StringBuffer onLine() {
        StringBuffer temp = new StringBuffer();
        String speed = (String) air_control_map.get("speed");
        switch (speed) {
            case "1":
                temp.append("低风");
                break;
            case "2":
                temp.append("中风");
                break;
            case "3":
                temp.append("高风");
                break;
            case "6":
                temp.append("自动");
                break;
            default:
                temp.append("自动");
                break;
        }
        String temperature = (String) air_control_map.get("temperature");

        temp.append("|" + temperature);
        common_mode(temp);
        return temp;
    }

    private void common_mode(StringBuffer temp) {
        String model = (String) air_control_map.get("mode");
        //

        switch (model) {
            case "1":
                temp.append("|" + "制冷");
                break;
            case "2":
                temp.append("|" + "制热");
                break;
            case "3":
                temp.append("|" + "除湿");
                break;
            case "4":
                temp.append("|" + "自动");
                break;
            default:
                temp.append("自动");
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        String position_index = "";
        switch (seekBar.getId()) {

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface OnCloseListener {
        void onClick(String action);
    }
}
