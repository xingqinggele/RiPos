package com.example.ripos.homefragment.homebagactivity;

import android.view.View;
import android.widget.LinearLayout;

import com.example.ripos.R;
import com.example.ripos.base.BaseActivity;

/**
 * 作者: qgl
 * 创建日期：2020/12/16
 * 描述: 礼包活动
 */
public class HomeBagActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout iv_back;
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.home_bag_activity;
    }

    @Override
    protected void initView() {
        iv_back = findViewById(R.id.iv_back);

    }
    //点击事件
    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
