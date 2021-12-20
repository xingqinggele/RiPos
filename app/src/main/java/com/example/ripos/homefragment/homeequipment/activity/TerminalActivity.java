package com.example.ripos.homefragment.homeequipment.activity;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.ripos.R;
import com.example.ripos.adapter.ChooseGridViewAdapter1;
import com.example.ripos.base.BaseActivity;
import com.example.ripos.homefragment.homeequipment.adapter.TerminalAdapter;
import com.example.ripos.homefragment.homeequipment.bean.TerminalActivityBean;
import com.example.ripos.homefragment.homeequipment.bean.TerminalBean;
import com.example.ripos.net.HttpRequest;
import com.example.ripos.net.OkHttpException;
import com.example.ripos.net.RequestParams;
import com.example.ripos.net.ResponseCallback;
import com.example.ripos.net.Utils;
import com.example.ripos.views.MyGridView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2020/12/21
 * 描述:终端查询
 */
public class TerminalActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    //侧滑Layout
    private DrawerLayout drawer_layout;
    //筛选
    private TextView terminal_screening;
    //返回键
    private LinearLayout iv_back;
    //侧滑筛选栏adapter
    private ChooseGridViewAdapter1 madapter;
    //实体类
    private TerminalActivityBean msBean;
    //侧滑筛选栏GridView
    private MyGridView gvTest;
    //终端类型
    private String posType = "";
    //搜索框
    private EditText terminal_ed_search;
    //刷新控件
    private SwipeRefreshLayout terminal_swipe_refresh_layout;
    //listView
    private RecyclerView terminal_list_view;
    //列表adapter
    private TerminalAdapter adapter;
    //数据条数
    private TextView terminal_tv_number;
    //筛选栏地下两个按钮
    private RadioButton terminal_determine_rb, terminal_cancel_rb;
    //列表Bean
    private List<TerminalBean> beans = new ArrayList<>();
    private int mCount = 1; //页码
    private int pageSize = 20;  // 请求数据数量
    private String posCode = ""; //搜索框数据
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.terminal_activity;
    }

    @Override
    protected void initView() {
        terminal_determine_rb = findViewById(R.id.terminal_determine_rb);
        terminal_cancel_rb = findViewById(R.id.terminal_cancel_rb);
        terminal_tv_number = findViewById(R.id.terminal_tv_number);
        drawer_layout = findViewById(R.id.drawer_layout);
        iv_back = findViewById(R.id.iv_back);
        terminal_screening = findViewById(R.id.terminal_screening);
        terminal_ed_search = findViewById(R.id.terminal_ed_search);
        gvTest = findViewById(R.id.my_grid1);
        terminal_swipe_refresh_layout = findViewById(R.id.terminal_swipe_refresh_layout);
        terminal_list_view = findViewById(R.id.terminal_list_view);
        initList();
        postData2();
    }

    private void initList() {
        //下拉样式
        terminal_swipe_refresh_layout.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        terminal_swipe_refresh_layout.setOnRefreshListener(this);
        adapter = new TerminalAdapter(R.layout.item_terminal_activity, beans);
        adapter.openLoadAnimation();
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, terminal_list_view);
        adapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.list_empty, null));
        terminal_list_view.setLayoutManager(new LinearLayoutManager(this));
        terminal_list_view.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(TerminalActivity.this, TerminalDetailsActivity.class);
                intent.putExtra("var2", beans.get(position).getVar2());
                intent.putExtra("posCode", beans.get(position).getPosCode());
                intent.putExtra("var1", beans.get(position).getVar1());
                if (beans.get(position).getPosActivateStatus() == null) {
                    intent.putExtra("getPosActivateStatus", "null");
                } else {
                    intent.putExtra("getPosActivateStatus", beans.get(position).getPosActivateStatus());
                }
                intent.putExtra("posBindTime", beans.get(position).getPosBindTime());
                intent.putExtra("posActivity", beans.get(position).getPosActivity());
                intent.putExtra("posDeposit", beans.get(position).getPosDeposit());
                intent.putExtra("posCashback", beans.get(position).getPosCashback());
                intent.putExtra("posModel", beans.get(position).getPosModel());
                startActivity(intent);
            }
        });
        postData(true);
    }

    @Override
    protected void initListener() {
        terminal_screening.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        terminal_determine_rb.setOnClickListener(this);
        terminal_cancel_rb.setOnClickListener(this);
        search();
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.terminal_screening:
                drawer_layout.openDrawer(GravityCompat.END);
                break;
            case R.id.terminal_determine_rb:
                posType = "";
                madapter.newAdd();
                shouLog("terminalType", posType + "111");
                break;
            case R.id.terminal_cancel_rb:
                drawer_layout.closeDrawer(GravityCompat.END);
                shouLog("terminalType", posType + "222");
                onRefresh();
                break;
        }
    }

    //搜索框
    private void search() {
        terminal_ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    Utils.hideKeyboard(terminal_ed_search);
                    posCode = v.getText().toString().trim();
                    onRefresh();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 刷新列表
     */
    @Override
    public void onRefresh() {
        terminal_swipe_refresh_layout.setRefreshing(true);
        setRefresh();
    }


    private void setRefresh() {
        mCount = 1;
        postData(true);
    }

    /**
     * 上拉加载，
     */
    @Override
    public void onLoadMoreRequested() {
        mCount = mCount + 1;
        postData(false);
    }

    // 获取列表数据
    public void postData(boolean isRefresh) {
        RequestParams params = new RequestParams();
        params.put("userId", getUserId());
        params.put("pageNo", mCount + "");
        params.put("pageSize", pageSize + "");
        params.put("posCode", posCode);
        params.put("posType", posType);
        HttpRequest.getEquipmentList(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                terminal_swipe_refresh_layout.setRefreshing(false);
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<TerminalBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<TerminalBean>>() {
                            }.getType());
                    //判断刷新还是加载
                    if (isRefresh) {
                        //判断数组是否为空、为空不需要清空，不为空才需要清空
                        if (beans != null) {
                            beans.clear();
                        }
                    }
                    beans.addAll(memberList);
                    terminal_tv_number.setText(beans.size() + "");
                    if (memberList.size() < pageSize) {
                        adapter.loadMoreEnd();
                    } else {
                        adapter.loadMoreComplete();
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }

    // 获取筛选数据
    public void postData2() {
        RequestParams params = new RequestParams();
        HttpRequest.getPosType(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<TerminalActivityBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<TerminalActivityBean>>() {
                            }.getType());
                    // 每一条筛选添加手动添加一条"全部" 数据
                    for (int i = 0; i < memberList.size(); i++) {
                        msBean = new TerminalActivityBean();
                        msBean.setTypeName("全部");
                        msBean.setId("");
                    }
                    memberList.add(msBean);
                    // 重新排序
                    Collections.reverse(memberList);
                    madapter = new ChooseGridViewAdapter1(TerminalActivity.this,memberList);
                    gvTest.setAdapter(madapter);
                    madapter.setOnAddClickListener(onItemActionClick);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }

    // Adapter回调接口
    ChooseGridViewAdapter1.OnAddClickListener onItemActionClick = new ChooseGridViewAdapter1.OnAddClickListener() {
        @Override
        public void onItemClick(String position) {
            posType = position;
        }
    };

}
