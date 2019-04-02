package com.massky.conditioningsystem.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.massky.conditioningsystem.R;
import com.massky.conditioningsystem.Util.ListViewForScrollView_New;
import com.massky.conditioningsystem.Util.SharedPreferencesUtil;
import com.massky.conditioningsystem.adapter.SceneDetailListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SceneDialog extends Dialog implements View.OnClickListener {


    private  String name;
    @InjectView(R.id.maclistview_id_result)
    ListViewForScrollView_New maclistview_id_result;
    @InjectView(R.id.promat_txt)
    TextView promat_txt;
    @InjectView(R.id.close_img)
    ImageView close_img;
    private Map map_panel = new HashMap();
    private OnCloseListener listener;
    private String title;
    private String positiveName;
    private String negativeName;
    private Context mContext;
    List<Map> scene_detail_show_list = new ArrayList<>();
    private SceneDetailListAdapter scenedetaillistadapter;

    /**
     * 空调
     *
     * @return
     */


    public SceneDialog(Context context, int bottomDialog, List<Map> scene_detail_show_list, String name,OnCloseListener listener) {
        super(context);
        this.mContext = context;
        this.scene_detail_show_list = scene_detail_show_list;
        this.name = name;
    }

    public SceneDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
    }

    public SceneDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    protected SceneDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public SceneDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public SceneDialog setPositiveButton(String name) {
        this.positiveName = name;
        return this;
    }

    public SceneDialog setNegativeButton(String name) {
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promat_item_scene_dialog);
        setCanceledOnTouchOutside(false);
        ButterKnife.inject(this);
        init_dialog_window();
        initdata();
    }

    private void initdata() {
        promat_txt.setText(name);
        scenedetaillistadapter = new SceneDetailListAdapter(mContext,
                scene_detail_show_list);
        maclistview_id_result.setAdapter(scenedetaillistadapter);
        close_img.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_img:
                dismiss();
                break;
        }
    }


    public interface OnCloseListener {
        void onClick(String action);
    }
}
