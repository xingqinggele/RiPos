package com.example.ripos.mefragment.meorder;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.example.ripos.R;
import com.example.ripos.adapter.MyViewPageAdapter;
import com.example.ripos.base.BaseActivity;
import com.example.ripos.homefragment.homemessage.fragment.BusinessMessageFragment;
import com.example.ripos.homefragment.homemessage.fragment.SystemMessageFragment;
import com.example.ripos.mefragment.meorder.fragment.ApplyExchangeFragment;
import com.example.ripos.mefragment.meorder.fragment.MeExchangeFragment;

import java.util.ArrayList;

/**
 * 作者: qgl
 * 创建日期：2021/2/20
 * 描述: 我的订单
 */
public class MeOrderActivity extends BaseActivity implements View.OnClickListener {
    //滑动table
    private TabLayout meorder_table_layout;
    //滑动ViewPager
    private ViewPager meorder_viewpager;
    //返回键
    private LinearLayout iv_back;
    //table名容器
    ArrayList<String> title_dates = new ArrayList<>();
    //fragment容器
    ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    //xml界面
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.meorder_activity;
    }
    //初始化控件
    @Override
    protected void initView() {
        meorder_table_layout = findViewById(R.id.meorder_table_layout);
        meorder_viewpager = findViewById(R.id.meorder_viewpager);
        iv_back = findViewById(R.id.iv_back);
    }
    //点击事件绑定
    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
    }
    //适配数据、界面
    @Override
    protected void initData() {
        title_dates.add("我的兑换");
        title_dates.add("兑换申请");
        fragmentList.add(new MeExchangeFragment());
        fragmentList.add(new ApplyExchangeFragment());
        init();
    }
    //viewpager适配adapter
    private void init() {
        MyViewPageAdapter myViewPageAdapter = new MyViewPageAdapter(getSupportFragmentManager(), title_dates, fragmentList);
        meorder_table_layout.setSelectedTabIndicator(0);
        meorder_viewpager.setAdapter(myViewPageAdapter);
        meorder_table_layout.setupWithViewPager(meorder_viewpager);
        meorder_table_layout.setTabsFromPagerAdapter(myViewPageAdapter);
    }
    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回键
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
