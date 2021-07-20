package com.mysafe.lib_base.custom_control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.mysafe.lib_base.R;
import com.mysafe.lib_base.util.log.SaveLogUtil;

import java.util.Objects;


/**
 * 相信自己，创造未来
 *
 * @author 小兵
 * @date 2017/10/21
 */

public class TopView extends FrameLayout {

    private Context context;
    private ConstraintLayout constraintLayout;
    private ImageView ivStartBack;
    private TextView tvStartTitle;
    private TextView tvCenterTitle;
    private TextView tvRightContent;
    private ImageView ivEndRight;

    private long oldTime;
    private int clickTimes = 1;

    public TopView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public TopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        setFitsSystemWindows(true);
        setClipToPadding(true);
        LayoutInflater.from(context).inflate(R.layout.layout_top_view, this, true);
        constraintLayout = (ConstraintLayout) this.findViewById(R.id.rl_bg);
        ivStartBack = (ImageView) this.findViewById(R.id.iv_back);
        tvStartTitle = (TextView) this.findViewById(R.id.tv_start_title);
        tvCenterTitle = (TextView) this.findViewById(R.id.tv_center_title);
        tvRightContent = (TextView) this.findViewById(R.id.tv_end_content);
        ivEndRight = (ImageView) this.findViewById(R.id.iv_end_right);
        constraintLayout.setOnClickListener(v -> {
            if (System.currentTimeMillis() - oldTime > 5000) {
                clickTimes = 1;
            }
            if (clickTimes == 1) {
                oldTime = System.currentTimeMillis();
            }
            if (clickTimes == 5) {
                Toast.makeText(context, "正在打印日志！", Toast.LENGTH_SHORT).show();
                saveLog();
                clickTimes = 1;
                Toast.makeText(context, "日志打印完成！", Toast.LENGTH_SHORT).show();
            } else if (clickTimes > 5) {
                Toast.makeText(context, "请稍后点击！", Toast.LENGTH_SHORT).show();
            }

            clickTimes++;
        });
    }


    private void saveLog() {
        String path = context.getExternalFilesDir("log").toString();
        String packageName = context.getPackageName();
        Objects.requireNonNull(SaveLogUtil.getInstance()).saveLogFile(path, packageName);
    }

    public void initClick(OnClickTopListener onClickTopListener) {
        if (onClickTopListener != null) {

            ivStartBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onLeft();
                }
            });
            tvRightContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onRight();
                }
            });
            ivEndRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onRight();
                    onClickTopListener.onRight(v);
                }
            });
        }
    }

    public void init(boolean isBack, int title, int rightContent, final OnClickTopListener onClickTopListener) {
        tvCenterTitle.setText(context.getString(title));
        if (isBack) {
            ivStartBack.setVisibility(View.VISIBLE);
        } else {
            ivStartBack.setVisibility(View.GONE);
        }
        if (rightContent != 0) {
            tvRightContent.setVisibility(VISIBLE);
            tvRightContent.setText(context.getString(rightContent));
        }
        if (onClickTopListener != null) {
            ivStartBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onLeft();
                }
            });
            tvRightContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onRight();
                }
            });
            ivEndRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onRight();
                    onClickTopListener.onRight(v);
                }
            });
        }
    }

    public void init(boolean isBack, String title, int rightContent, final OnClickTopListener onClickTopListener) {

        tvCenterTitle.setText(title);
        if (isBack) {
            ivStartBack.setVisibility(View.VISIBLE);
        } else {
            ivStartBack.setVisibility(View.GONE);
        }
        if (rightContent != 0) {
            tvRightContent.setVisibility(VISIBLE);
            tvRightContent.setText(context.getString(rightContent));
        }
        if (onClickTopListener != null) {
            ivStartBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onLeft();
                }
            });
            tvRightContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onRight();
                }
            });
            ivEndRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTopListener.onRight();
                }
            });
        }
    }

    public void setTitleSize(float size) {
        tvCenterTitle.setTextSize(size);
    }

    public void setTitleColor(int resId) {
        tvCenterTitle.setTextColor(getResources().getColor(resId));
    }

    public void setTitleText(String text) {
        tvCenterTitle.setText(text);
    }


    public String getTitleText() {
        return tvCenterTitle.getText().toString().trim();
    }

    public void setTitleGravity(int gravity) {
        tvCenterTitle.setGravity(gravity);
    }

    public void setTvStartTitle(String txt) {
        tvStartTitle.setText(txt);
//        if (txt != null && !txt.isEmpty()) {
//            ivStartBack.setVisibility(View.VISIBLE);
//        } else {
//            ivStartBack.setVisibility(View.GONE);
//        }
    }

    public void setRightSize(float size) {
        tvRightContent.setTextSize(size);
    }

    public void setRightColor(int resId) {
        tvRightContent.setTextColor(getResources().getColor(resId));
    }


    public void setRightText(String text) {
        tvRightContent.setText(text);
        tvRightContent.setVisibility(VISIBLE);
    }

    public void setRigthImage(int resId) {
        tvRightContent.setVisibility(View.GONE);
        ivEndRight.setImageResource(resId);
        ivEndRight.setVisibility(View.VISIBLE);
    }

    public void setRigthImageVisvible(boolean visvible) {
        if (visvible) {
            ivEndRight.setVisibility(VISIBLE);
        } else {
            ivEndRight.setVisibility(GONE);
        }
    }


    public void setRightImageClickListener(OnClickListener onClickListener) {
        ivEndRight.setOnClickListener(onClickListener);
    }

    public void setRightTextClickListener(OnClickListener onClickListener) {
        tvRightContent.setOnClickListener(onClickListener);
    }

    public void setRightTextClickable(boolean flag) {
        tvRightContent.setEnabled(flag);
    }

    public void setRightTextVisvible(int flag) {
        tvRightContent.setVisibility(flag);
    }


    public static abstract class OnClickTopListener {
        public void onLeft() {
        }

        public void onTitle() {
        }

        public void onRight() {
        }

        public void onRight(View view) {
        }

        public void onClose() {
        }
    }

    public void setTvRightContenteVisvibe(int visvibe) {
        tvRightContent.setVisibility(visvibe);
    }
}
