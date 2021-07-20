package com.mysafe.lib_base.expansion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.mysafe.lib_base.R;

public class EX_AlertDialog extends AlertDialog {

    private View ContentView;

    private boolean IsSetTitle;
    private boolean IsSetContent;
    private boolean IsSetCancel;
    private boolean IsSetSure;
    private boolean IsDiyLayout;

    public View SetContentView(int layId) {
        this.ContentView = LayoutInflater.from(this.getContext()).inflate(layId, null);
        IsDiyLayout = true;
        return ContentView;
    }

    public View SetContentView(View view) {
        this.ContentView = view;
        IsDiyLayout = true;
        return ContentView;
    }

    public EX_AlertDialog(@NonNull Context context) {
        super(context);
        Init();
    }

    public EX_AlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        Init();
    }

    public EX_AlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        Init();
    }

    @Override
    public void show() {
        if (!IsDiyLayout) {
            if (!IsSetCancel)
                ConfigCancelEvent();
            if (!IsSetSure)
                ConfigSureEvent();
            if (!IsSetTitle)
                ContentView.findViewById(R.id.tv_title).setVisibility(View.GONE);
            if (!IsSetContent)
                ContentView.findViewById(R.id.tv_content).setVisibility(View.GONE);
        }
        this.setView(ContentView);
        super.show();
    }

    private void Init() {
        this.ContentView = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog_standard, null);
        IsSetTitle = false;
        IsSetContent = false;
        IsSetCancel = false;
        IsSetSure = false;
        IsDiyLayout = false;
    }

    private void ConfigCancelEvent() {
        this.ContentView.findViewById(R.id.tv_cancel).setOnClickListener(v -> EX_AlertDialog.this.dismiss());
    }

    private void ConfigSureEvent() {
        this.ContentView.findViewById(R.id.tv_sure).setOnClickListener(v -> EX_AlertDialog.this.dismiss());
    }

    //region 外部方法
    public EX_AlertDialog SetTitle(String msg) {
        ((TextView) this.ContentView.findViewById(R.id.tv_title)).setText(msg);
        IsSetTitle = true;
        return this;
    }

    public EX_AlertDialog SetContent(String content) {
        ((TextView) this.ContentView.findViewById(R.id.tv_content)).setText(content);
        IsSetContent = true;
        return this;
    }

    public EX_AlertDialog ConfigSureButton(String text, IEventCallBack callBack) {
        TextView sure = (TextView) this.ContentView.findViewById(R.id.tv_sure);
        sure.setText(text);
        sure.setOnClickListener(v -> {
            EX_AlertDialog.this.dismiss();
            callBack.OnEvent();
        });
        IsSetSure = true;
        return this;
    }

    public EX_AlertDialog ConfigCancelButton(String text, IEventCallBack callBack) {
        TextView cancel = (TextView) this.ContentView.findViewById(R.id.tv_cancel);
        cancel.setText(text);
        cancel.setOnClickListener(v -> {
            EX_AlertDialog.this.dismiss();
            callBack.OnEvent();
        });
        IsSetCancel = true;
        return this;
    }

    public EX_AlertDialog ConfigButtonEvent(@Nullable IEventCallBack_4Lz callBack) {
        this.ContentView.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            EX_AlertDialog.this.dismiss();
            if (callBack != null)
                callBack.OnCancel();
        });
        this.ContentView.findViewById(R.id.tv_sure).setOnClickListener(v -> {
            EX_AlertDialog.this.dismiss();
            if (callBack != null)
                callBack.OnSure();
        });
        IsSetCancel = true;
        IsSetSure = true;
        return this;
    }
//endregion


    public interface IEventCallBack {
        void OnEvent();
    }

    public interface IEventCallBack_4Lz {
        void OnSure();

        void OnCancel();
    }
}
