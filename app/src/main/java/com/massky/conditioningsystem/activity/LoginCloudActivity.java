package com.massky.conditioningsystem.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.conditioningsystem.R;
import com.massky.conditioningsystem.Util.DialogUtil;
import com.massky.conditioningsystem.Util.EyeUtil;
import com.massky.conditioningsystem.Util.SharedPreferencesUtil;
import com.massky.conditioningsystem.Util.ToastUtil;
import com.massky.conditioningsystem.Utils.DensityUtils;
import com.massky.conditioningsystem.base.BaseActivity;
import com.massky.conditioningsystem.permissions.RxPermissions;
import com.massky.conditioningsystem.view.ClearEditText;
import com.massky.conditioningsystem.view.ClearLengthEditText;
import com.massky.conditioningsystem.view.TransitionView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhu on 2017/12/29.
 */

public class LoginCloudActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    @InjectView(R.id.eyeimageview_id_gateway)
    ImageView eyeimageview_id_gateway;
    private EyeUtil eyeUtil;
    @InjectView(R.id.usertext_id)
    ClearEditText usertext_id;
    @InjectView(R.id.phonepassword)
    ClearEditText phonepassword;
    @InjectView(R.id.ani_view)
    TransitionView mAnimView;
    @InjectView(R.id.search_gateway_btn)
    TextView search_gateway_btn;


    private DialogUtil dialogUtil;
    private String token;

    @Override
    protected int viewId() {
        return R.layout.login_cloud;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.

        mAnimView.setOnAnimationEndListener(new TransitionView.OnAnimationEndListener() {
            @Override
            public void onEnd() {
                //跳转到主页面
                gotoHomeActivity();
            }
        });
        search_gateway_btn.setOnClickListener(this);
    }

    private void gotoHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    public void singUp() {
        mAnimView.startAnimation();
    }


    @Override
    protected void onEvent() {
        init_permissions();
        btn_login_gateway.setOnClickListener(this);
        eyeimageview_id_gateway.setOnClickListener(this);
        eyeUtil = new EyeUtil(LoginCloudActivity.this, eyeimageview_id_gateway, phonepassword, true);
    }

    @Override
    protected void onData() {
        String loginPhone = (String) SharedPreferencesUtil.getData(LoginCloudActivity.this, "loginPhone", "");
        if (loginPhone != null) {
            usertext_id.setText(loginPhone);
        }
    }

    private void init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_gateway:
                singUp();
                break;//登录网关
            case R.id.eyeimageview_id_gateway:
                eyeUtil.EyeStatus();
                break;
            case R.id.search_gateway_btn:
                showRenameDialog("", "你好", 0);
                break;
        }
    }


    //自定义dialog,自定义重命名dialog

    public void showRenameDialog(final String id, final String name, final int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        final View view = LayoutInflater.from(LoginCloudActivity.this).inflate(R.layout.editscene_dialog, null);
        final ImageView confirm; //确定按钮
        ImageView cancel; //确定按钮
        ImageView tv_title;
//        final TextView content; //内容
        cancel = (ImageView) view.findViewById(R.id.call_cancel);
        confirm = (ImageView) view.findViewById(R.id.call_confirm);
        final ClearLengthEditText edit_password_gateway = (ClearLengthEditText) view.findViewById(R.id.edit_password_gateway);
        edit_password_gateway.setText(name);
        edit_password_gateway.setSelection(edit_password_gateway.getText().length());
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(LoginCloudActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        DisplayMetrics dm = LoginCloudActivity.this.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayHeight / 3 * 2); //宽度设置为屏幕的0.5
        p.height = (int) (p.width / 2); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        pop_animal(view, displayHeight);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * 弹出框动画效果
     *
     * @param view
     * @param displayHeight
     */
    private void pop_animal(View view, int displayHeight) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "ScaleX", 0.1f, 0.3f, 0.4f, 0.5f, 0.6f, 0.87f, 1f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "ScaleY", 0.1f, 0.3f, 0.4f, 0.5f, 0.6f, 0.75f, 0.87f, 1f, 1.1f, 1.0f);
        ObjectAnimator translationAni = ObjectAnimator.ofFloat(view, "TranslationY", displayHeight / 2 * 0.8f, -displayHeight / 2 * 0.2f, 0);
        ObjectAnimator alphaAni = ObjectAnimator.ofFloat(view, "Alpha", 0.5f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, translationAni, alphaAni);
        animatorSet.setDuration(1200);
        animatorSet.start();
    }

}
