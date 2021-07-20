package com.mysafe.lib_base.expansion.NetState;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mysafe.lib_base.R;

public class NetObserveDialog extends Dialog {
    private Context mContext;

    public NetObserveDialog(@NonNull Context context) {
        super(context);
        View inflate;

        mContext = context;
        inflate = LayoutInflater.from(context).inflate(R.layout.dialog_network_state, null);
        setContentView(inflate);
    }

    public NetObserveDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        View view;

        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_network_state, null);
        TextView tipBtn = (TextView) view.findViewById(R.id.tv_network_tip_txt);
        tipBtn.setOnClickListener(v -> {
            mContext.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        });
        setContentView(view);
    }


    protected NetObserveDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setDialogLocation() {
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.END;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setWindowAnimations(R.style.dialogReceiver);
        win.setAttributes(lp);
    }
}