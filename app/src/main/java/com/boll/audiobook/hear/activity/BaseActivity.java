package com.boll.audiobook.hear.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.Lifecycle;
//import androidx.lifecycle.LifecycleEventObserver;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.LifecycleRegistry;

import com.boll.audiobook.hear.utils.ActivityCollector;
import com.boll.audiobook.hear.utils.LogUtil;
import com.boll.audiobook.hear.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Activity基类
 *
 * @author Created by zoro on 2020/10/8
 */
public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";

    private Context mContext;
    private static Toast toast;

    public LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        mContext = this;
        mLoadingDialog = new LoadingDialog(mContext);
        setStatus();
        setFullScreen(true);//全屏
//        setHideVirtualKey(true);//隐藏底部导航栏
        //setRequestedOrientation(true);//竖屏设置
        setContentView(initLayout());
        initView();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Integer code) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
        ActivityCollector.removeActivity(this);
    }

    /**
     * 设置各种状态
     */
    public abstract void setStatus();

    /**
     * 初始化布局
     * @return 布局id
     */
    public abstract int initLayout();

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 底部导航栏上滑显示
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 设置是否隐藏Android底部的虚拟按键
     * @param isHide
     */
    public void setHideVirtualKey(boolean isHide){
        if (isHide){
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            window.setAttributes(params);
        }
    }

    /**
     * 设置是否全屏
     * @param isFullScreen
     */
    public void setFullScreen(boolean isFullScreen){
        if (isFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 设置状态栏颜色
     * @param color
     */
    public void setStatusColor(int color){
//        StatusBarUtil.setColor(this,color);
    }

    /**
     * 设置状态栏是否半透明
     * @param isTranslucent
     */
    public void setStatusTranslucent(boolean isTranslucent){
        if (isTranslucent){
//            StatusBarUtil.setTranslucent(this);
        }
    }

    /**
     * 设置状态栏是否全透明
     * @param isTransparent
     */
    public void setStatusTransparent(boolean isTransparent){
        if (isTransparent) {
//            StatusBarUtil.setTransparent(BaseActivity.this);
        }
    }

    /**
     * 横竖屏设置
     * @param isAllowScreenRoate
     */
    public void setRequestedOrientation(boolean isAllowScreenRoate){
        if (!isAllowScreenRoate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 显示提示  toast
     * @param msg 提示信息
     */
    @SuppressLint("ShowToast")
    public void showToast(String msg) {
        try {
            if (null == toast) {
                toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

}