package com.mysafe.lib_base.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.mysafe.lib_base.R;
import com.mysafe.lib_base.expansion.NetState.NetObserveDialog;
import com.mysafe.lib_base.expansion.NetState.NetworkReceiver;
import com.mysafe.lib_base.expansion.NetState.NetworkState;
import com.mysafe.lib_base.expansion.NetState.OnNetworkListener;
import com.mysafe.lib_base.util.NetCheckUtil;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements OnNetworkListener {
    public T dataBinding;
    private boolean isShowDialog;
    private NetObserveDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, getContentViewId());
        NetworkReceiver.Companion.getInstance().init(this);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isShowDialog = true;
        if (!NetCheckUtil.checkNet(this)) {
            showDialog();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideDialog();
        isShowDialog = false;
    }

    @Override
    public void onNetworkState(int state) {
        if (!isShowDialog){
            return;
        }
        switch (state) {
            case NetworkState.NONE:
                runOnUiThread(() -> {
                    Log.i("TAG_NET_onAvailable", "onNetworkState: start NONE ");
                    showDialog();
                });

                break;
            case NetworkState.CONNECT:
                runOnUiThread(() -> {
                    Log.i("TAG_NET_onAvailable", "onNetworkState:start CONNECT ");
                    hideDialog();
                });

                break;
            default:
                break;
        }
        Log.d("TAG_NET_onAvailable: ", "state:" + state);
    }

    private NetObserveDialog getDialog() {
        if (dialog == null && ActivityManager.Instance().getActivitys() > 0) {
            dialog = new NetObserveDialog(this, R.style.dialogReceiver);
            dialog.setDialogLocation();
        }
        return dialog;
    }

    private void showDialog() {
        if (getDialog() != null) {
            Log.i("TAG_NET_onAvailable", "onNetworkState: succeed NONE ");
            getDialog().show();
        }
    }

    private void hideDialog() {
        if (getDialog() != null && getDialog().isShowing()) {
            Log.i("TAG_NET_onAvailable", "onNetworkState:succeed CONNECT ");
            getDialog().dismiss();
        }
    }

    protected void GotoOtherActivity(Class target, boolean isFinish) {
        startActivity(new Intent(this, target));
        if (isFinish)
            this.finish();
    }

    protected void hideSystemUI() {
        //不能在onWindowFocusChanged()中执行，因为onWindow执行时View已经绘制完成，里面的子View的高度已经确认，
        // 这时候去隐藏导航栏状态栏，导致，绘制出来的View高度不能全屏
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    protected void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public abstract int getContentViewId();

    public abstract void init();

    //region 小键盘管理

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        //判断得到的焦点控件是否包含EditText
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            //得到输入框在屏幕中上下左右的位置
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    //endregion


    //region 生命周期
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getDialog() != null) {
            getDialog().dismiss();
        }
        dialog = null;
    }
    //endregion


}
