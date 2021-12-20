package com.example.ripos.homefragment.shopping;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.ripos.R;
import com.example.ripos.base.BaseActivity;
import com.example.ripos.datafragment.databenefit.adapter.DataBenefitAdapter;
import com.example.ripos.homefragment.shopping.adapter.ShoppingListAdapter;
import com.example.ripos.homefragment.shopping.bean.ShoppingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2021/5/17
 * 描述:购物列表
 */
public class ShoppingListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, View.OnClickListener {
    //返回键
    private LinearLayout iv_back;
    //实体类
    private List<ShoppingBean> mData = new ArrayList<>();
    private ShoppingBean bean;
    //下拉刷新控件
    private SwipeRefreshLayout shopping_swipe;
    //列表控件
    private RecyclerView shopping_list_view;
    //adapter
    private ShoppingListAdapter mAdapter;
    //数据请求页码
    private int mCount = 1;
    // 请求数据数量
    private int pageSize = 20;

    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.shoppinglist_activity;
    }

    @Override
    protected void initView() {
        iv_back = findViewById(R.id.iv_back);
        shopping_swipe = findViewById(R.id.shopping_swipe);
        shopping_list_view = findViewById(R.id.shopping_list_view);
        initList();
    }

    //适配列表、刷新控件、adapter
    public void initList() {
        //下拉样式
        shopping_swipe.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        //上拉刷新初始化
        shopping_swipe.setOnRefreshListener(this);
        //adapter配置data
        mAdapter = new ShoppingListAdapter(R.layout.shopping_list_item, mData);
        //打开加载动画
        mAdapter.openLoadAnimation();
        //设置启用加载更多
        mAdapter.setEnableLoadMore(true);
        //设置为加载更多监听器
        mAdapter.setOnLoadMoreListener(this, shopping_list_view);
        //数据为空显示xml
        mAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.list_empty, null));
        // RecyclerView设置布局管理器
        shopping_list_view.setLayoutManager(new LinearLayoutManager(this));
        //RecyclerView配置adapter
        shopping_list_view.setAdapter(mAdapter);
        posData(true);
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    //请求商品列表
    private void posData(boolean isRefresh){
        bean = new ShoppingBean("1","快钱电签POS1","120","");
        bean = new ShoppingBean("2","快钱电签POS2","100","");
        bean = new ShoppingBean("3","快钱电签POS3","110","");
        bean = new ShoppingBean("4","快钱电签POS4","130","");
        mData.add(bean);
        mAdapter.loadMoreEnd();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        //开启刷新
        shopping_swipe.setRefreshing(true);
        //调用刷新逻辑
        setRefresh();
    }

    //处理刷新逻辑
    private void setRefresh() {
        //页码设置为1
        mCount = 1;
        //请求接口、填充数据
        posData(true);
    }

    @Override
    public void onLoadMoreRequested() {
        //页码 n + 1
        mCount = mCount + 1;
        //请求接口、填充数据
        posData(false);
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
