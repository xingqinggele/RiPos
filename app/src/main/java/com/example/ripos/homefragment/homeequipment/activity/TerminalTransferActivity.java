package com.example.ripos.homefragment.homeequipment.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import com.example.ripos.R;
import com.example.ripos.adapter.ChooseGridViewAdapter1;
import com.example.ripos.adapter.MyViewPageAdapter;
import com.example.ripos.base.BaseActivity;
import com.example.ripos.homefragment.homeequipment.bean.TerminalActivityBean;
import com.example.ripos.homefragment.homeequipment.bean.TerminalEvenBusBean;
import com.example.ripos.homefragment.homeequipment.bean.TerminalEvenBusBean1;
import com.example.ripos.homefragment.homeequipment.fragment.transfer.IntervalTransferFragment;
import com.example.ripos.homefragment.homeequipment.fragment.transfer.SelectTransferFragment1;
import com.example.ripos.net.HttpRequest;
import com.example.ripos.net.OkHttpException;
import com.example.ripos.net.RequestParams;
import com.example.ripos.net.ResponseCallback;
import com.example.ripos.views.MyGridView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者: qgl
 * 创建日期：2020/12/24
 * 描述:终端划拨
 */
public class TerminalTransferActivity extends BaseActivity implements View.OnClickListener {
    private TabLayout tab_layout;
    private ViewPager viewpager;
    private LinearLayout iv_back;
    ArrayList<String> titleDatas = new ArrayList<>();
    ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private DrawerLayout drawer_layout;
    private RadioButton terminal_transfer_determine_rb, terminal_transfer_cancel_rb;
    //侧滑筛选栏adapter
    private ChooseGridViewAdapter1 madapter;
    //侧滑筛选栏GridView
    private MyGridView gvTest;
    // 点击侧滑的Fragment 1.选择划拨,2.区间划拨
    private int fragmentCode = 1;
    private TerminalEvenBusBean busBean;
    private TerminalEvenBusBean1 busBean1;

    //终端类型
    private String posType = "";
    private TerminalActivityBean msBean;


    @Override
    protected int getLayoutId() {
        // 设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.terminal_transfer_activity;
    }

    @Override
    protected void initView() {
        terminal_transfer_determine_rb = findViewById(R.id.terminal_transfer_determine_rb);
        terminal_transfer_cancel_rb = findViewById(R.id.terminal_transfer_cancel_rb);
        tab_layout = findViewById(R.id.terminal_transfer_tab_layout);
        viewpager = findViewById(R.id.terminal_transfer_viewpager);
        iv_back = findViewById(R.id.iv_back);
        drawer_layout = findViewById(R.id.drawer_layout);
        gvTest = findViewById(R.id.my_grid1);
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        terminal_transfer_determine_rb.setOnClickListener(this);
        terminal_transfer_cancel_rb.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        titleDatas.add("选择划拨");
        titleDatas.add("区间划拨");
        fragmentList.add(new SelectTransferFragment1());
        fragmentList.add(new IntervalTransferFragment());
        init();
        postData1();
    }

    private void init() {
        MyViewPageAdapter myViewPageAdapter = new MyViewPageAdapter(getSupportFragmentManager(), titleDatas, fragmentList);
        tab_layout.setSelectedTabIndicator(0);
        viewpager.setAdapter(myViewPageAdapter);
        tab_layout.setupWithViewPager(viewpager);
        tab_layout.setTabsFromPagerAdapter(myViewPageAdapter);
    }

    public void setListSize(int value) {
        fragmentCode = value;
        if (fragmentCode == 2) {
            // 重置
            posType = "";
            madapter.newAdd();
        }
        // 点击了筛选
        drawer_layout.openDrawer(GravityCompat.END);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.terminal_transfer_determine_rb:
                // 重置
                posType = "";
                madapter.newAdd();
                break;
            case R.id.terminal_transfer_cancel_rb:
                drawer_layout.closeDrawer(GravityCompat.END);
                // 选择划拨
                busBean = new TerminalEvenBusBean();
                busBean.setTerminalType(posType);
                EventBus.getDefault().post(busBean);
                break;
        }
    }

    // 获取筛选数据
    public void postData1() {
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
                    madapter = new ChooseGridViewAdapter1(TerminalTransferActivity.this, memberList);
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
