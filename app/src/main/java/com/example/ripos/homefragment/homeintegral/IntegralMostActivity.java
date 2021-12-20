package com.example.ripos.homefragment.homeintegral;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.ripos.R;
import com.example.ripos.base.BaseActivity;
import com.example.ripos.homefragment.homeequipment.CallbackPersonActivity;
import com.example.ripos.homefragment.homeequipment.activity.TransferCallbackActivity;
import com.example.ripos.homefragment.homeintegral.adpter.IntegraMostListAdapter;
import com.example.ripos.homefragment.homeintegral.adpter.OrderPersonAdapter;
import com.example.ripos.homefragment.homeintegral.bean.IntegralAllBean;
import com.example.ripos.homefragment.homeintegral.bean.IntegralMostBean;
import com.example.ripos.net.HttpRequest;
import com.example.ripos.net.OkHttpException;
import com.example.ripos.net.RequestParams;
import com.example.ripos.net.ResponseCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2021/5/18
 * 描述: 积分极具列表
 */
public class IntegralMostActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, View.OnClickListener {
    //实体类List
    private List<IntegralMostBean>mData = new ArrayList<>();
    //adapter
    private IntegraMostListAdapter mAdapter;
    //上拉刷新
    private SwipeRefreshLayout integral_most_swipe;
    //列表
    private RecyclerView integral_most_list;
    //返回键
    private LinearLayout iv_back;
    //通用积分
    private String integral = "";
    //活动积分
    private String activityIntegral = "";
    //总积分
    private String totalintegral = "";


    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.integral_most_activity;
    }

    @Override
    protected void initView() {
        integral_most_swipe = findViewById(R.id.integral_most_swipe);
        integral_most_list = findViewById(R.id.integral_most_list);
        iv_back = findViewById(R.id.iv_back);
        initList();
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
    }

    //适配列表、刷新控件、adapter
    public void initList() {
        //下拉样式
        integral_most_swipe.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        //上拉刷新初始化
        integral_most_swipe.setOnRefreshListener(this);
        //adapter配置data
        mAdapter = new IntegraMostListAdapter(R.layout.integra_most_list_item, mData);
        //设置启用加载更多
        mAdapter.setEnableLoadMore(false);
        //设置为加载更多监听器
        mAdapter.setOnLoadMoreListener(this, integral_most_list);
        //数据为空显示xml
        mAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.list_empty, null));
        // RecyclerView设置布局管理器
        integral_most_list.setLayoutManager(new LinearLayoutManager(this));
        //RecyclerView配置adapter
        integral_most_list.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(IntegralMostActivity.this, HomeIntegraExchangeActivity.class);
                // 通用积分
                intent.putExtra("integral", integral);
                //活动积分
                intent.putExtra("activityIntegral", activityIntegral);
                //总积分
                intent.putExtra("totalintegral", totalintegral);
                //机器ID
                intent.putExtra("id", mData.get(position).getId());
                //价格
                intent.putExtra("returnIntegral", mData.get(position).getReturnIntegral());
                //名称
                intent.putExtra("typeName", mData.get(position).getTypeName());
                //图片
                intent.putExtra("detailImg", mData.get(position).getDetailImg());
                startActivity(intent);
            }
        });
        posDate(true);
    }

    @Override
    protected void initData() {
        integral = getIntent().getStringExtra("integral");
        activityIntegral = getIntent().getStringExtra("activityIntegral");
        totalintegral = getIntent().getStringExtra("totalintegral");
    }

    private void posDate(boolean isRefresh){
        RequestParams params = new RequestParams();
        HttpRequest.getMostList(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                integral_most_swipe.setRefreshing(false);
                //需要转化为实体对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<IntegralMostBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<IntegralMostBean>>() {
                            }.getType());
                    //判断刷新还是加载
                    if (isRefresh){
                        //判断数组是否为空、为空不需要清空，不为空才需要清空
                        if (mData != null){
                            mData.clear();
                        }
                    }
                    //在adapter List 中添加 list
                    mData.addAll(memberList);
                    mAdapter.loadMoreEnd();
                    //更新adapter
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(OkHttpException failuer) {
                integral_most_swipe.setRefreshing(false);
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });


    }

    @Override
    public void onRefresh() {
        //开启刷新
        integral_most_swipe.setRefreshing(true);
        posDate(true);
    }

    @Override
    public void onLoadMoreRequested() {

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
