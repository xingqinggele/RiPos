package com.example.ripos.mefragment.meorder.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ripos.R;
import com.example.ripos.base.BaseFragment;
import com.example.ripos.cap.android.CaptureActivity;
import com.example.ripos.cap.bean.ZxingConfig;
import com.example.ripos.cap.common.Constant;
import com.example.ripos.fragment.MeFragment;
import com.example.ripos.homefragment.homeequipment.adapter.RecyclerViewItemDecoration;
import com.example.ripos.homefragment.homeequipment.bean.TerminalBean;
import com.example.ripos.mefragment.meorder.adapter.CeshiAdapter1;
import com.example.ripos.net.HttpRequest;
import com.example.ripos.net.OkHttpException;
import com.example.ripos.net.RequestParams;
import com.example.ripos.net.ResponseCallback;
import com.example.ripos.net.Utils;
import com.example.ripos.utils.SPUtils;
import com.example.ripos.views.MyDialog1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者: qgl
 * 创建日期：2021/3/26
 * 描述: 极具发放，选择划拨
 */
public class HomeIntegerSelectFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, PullLoadMoreRecyclerView.PullLoadMoreListener, View.OnClickListener {
    //可划拨的极具数量
    private static int posNum = 5;
    //接受极具者商户ID
    private static String merchantId;
    //订单Id
    private static String orderID;
    //需要给后台返回pos机value值
    private int[] data;
    //分割线
    private RecyclerViewItemDecoration recyclerViewItemDecoration;
    //头部布局可滑动，吸顶
    private AppBarLayout appBarLayout;
    //上拉刷新
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //上拉加载
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    //列表控件
    private RecyclerView mRecyclerView;
    //列表Adapter
    private CeshiAdapter1 ceshiAdapter;
    //页码
    private int mCount = 1;
    //请求数据数量
    private int pageSize = 20;
    //搜索条件
    private String search_value = "";
    //列表Bean
    private List<TerminalBean> beans = new ArrayList<>();
    private List<TerminalBean> beanList_size = new ArrayList<>();
    //可划拨数
    private TextView tv_number1;
    //搜索按钮
    private Button search_code_btn;
    //搜索框
    private EditText merchants_query_ed_search;
    //选择的机器数
    private TextView merchants_transfer_number;
    //扫码按钮
    private ImageView scan_code_btn;
    //扫码成功返回值
    private final int REQUEST_CODE_SCAN = 1;
    //提交按钮
    private Button bt_sub;
    //pos机类型
    private static String posType = "";
    /**
     * 接受activity数据
     *
     * @param parentNum
     * @param merchId
     * @param orderId
     * @return
     */
    public static HomeIntegerSelectFragment newInstance(String parentNum, String merchId, String orderId,String posTypeId) {
        HomeIntegerSelectFragment fragment = new HomeIntegerSelectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        posNum = Integer.parseInt(parentNum);
        merchantId = merchId;
        orderID = orderId;
        posType = posTypeId;
        return fragment;
    }


    @Override
    protected int getLayoutInflaterResId() {
        return R.layout.homeinteger_select_fragment;
    }

    @Override
    protected void initView(View rootView) {
        recyclerViewItemDecoration = new RecyclerViewItemDecoration(getActivity(), 1);
        appBarLayout = rootView.findViewById(R.id.appBarLayout);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_layout);
        tv_number1 = rootView.findViewById(R.id.tv_number1);
        search_code_btn = rootView.findViewById(R.id.search_code_btn);
        merchants_transfer_number = rootView.findViewById(R.id.merchants_transfer_number);
        merchants_query_ed_search = rootView.findViewById(R.id.merchants_query_ed_search);
        scan_code_btn = rootView.findViewById(R.id.scan_code_btn);
        bt_sub = rootView.findViewById(R.id.bt_sub);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) rootView.findViewById(R.id.listviewa);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    mSwipeRefreshLayout.setEnabled(true);
                } else {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });
        initList();
        search();
        tv_number1.setText(posNum + "");

    }

    @Override
    protected void initListener() {
        search_code_btn.setOnClickListener(this);
        scan_code_btn.setOnClickListener(this);
        bt_sub.setOnClickListener(this);
    }

    private void initList() {
        //下拉样式
        mSwipeRefreshLayout.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //获取mRecyclerView对象
        mRecyclerView = mPullLoadMoreRecyclerView.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //关闭下拉刷新
        mPullLoadMoreRecyclerView.setPullRefreshEnable(false);
        mRecyclerView.addItemDecoration(recyclerViewItemDecoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //设置上拉刷新文字
        mPullLoadMoreRecyclerView.setFooterViewText("loading");
        mPullLoadMoreRecyclerView.setLinearLayout();
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        ceshiAdapter = new CeshiAdapter1(getActivity());
        mPullLoadMoreRecyclerView.setAdapter(ceshiAdapter);
        postData();
    }


    public void postData() {
        RequestParams params = new RequestParams();
        params.put("userId", SPUtils.get(getActivity(), "userId", "-1").toString());
        params.put("posActivateStatus", "0");
        params.put("pageNo", mCount + "");
        params.put("pageSize", pageSize + "");
        params.put("posCode", search_value);
        params.put("operType", "1"); // 1. 划拨 2.回调
        params.put("posType", posType); // pos类型
        HttpRequest.getEquipmentList(params, SPUtils.get(getActivity(), "Token", "-1").toString(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                mSwipeRefreshLayout.setRefreshing(false);
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<TerminalBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<TerminalBean>>() {
                            }.getType());
                    beans.addAll(memberList);
                    ceshiAdapter.addAllData(memberList);
                    ceshiAdapter.addNum(posNum);
                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    ceshiAdapter.setOnAddClickListener(onItemActionClick);
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

    /*************Adapter接口回调********************/
    CeshiAdapter1.OnAddClickListener onItemActionClick = new CeshiAdapter1.OnAddClickListener() {
        @Override
        public void onItemClick() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int len = 0;
                        int lenght = beans.size();
                        if (lenght >= 1) {
                            for (int i = 0; i < lenght; i++) {
                                if (ceshiAdapter.ischeck.get(i, false)) {
                                    len = len + 1;
                                }
                            }
                            merchants_transfer_number.setText("总计:" + len + "台");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    /************************************************/
    @Override
    public void onRefresh() {
        search_value = "";
        mCount = 1;
        ceshiAdapter.clearData();
        beans.clear();
        beanList_size.clear();
        postData();
    }

    @Override
    public void onLoadMore() {
        //页码 + 1
        mCount = mCount + 1;
        postData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //搜索
            case R.id.search_code_btn:
                search_value = merchants_query_ed_search.getText().toString();
                mCount = 1;
                ceshiAdapter.clearData();
                beans.clear();
                beanList_size.clear();
                postData();
                break;
            //扫码
            case R.id.scan_code_btn:
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                 * 也可以不传这个参数
                 * 不传的话  默认都为默认不震动  其他都为true
                 * */
                ZxingConfig config = new ZxingConfig();
                config.setShowbottomLayout(false);//底部布局（包括闪光灯和相册）
                config.setDecodeBarCode(true);//是否扫描条形码 默认为true
                config.setFullScreenScan(true);
                //config.setPlayBeep(true);//是否播放提示音
                //config.setShake(true);//是否震动
                //config.setShowAlbum(true);//是否显示相册
                //config.setShowFlashLight(true);//是否显示闪光灯
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            case R.id.bt_sub:
                StringBuffer sb = new StringBuffer();
                beanList_size = new ArrayList<>();
                for (int i = 0; i < beans.size(); i++) {
                    if (ceshiAdapter.ischeck.get(i, false)) {
                        sb.append(beans.get(i).getPosId().toString());
                        beanList_size.add(beans.get(i));
                    }
                }
                if (sb.toString().equals("") || beanList_size.size() < posNum) {
                    Toast.makeText(getActivity(), "请选择正确选择划拨的机器", Toast.LENGTH_LONG).show();
                } else {
                    //做划拨
                    showDialog();
                }
                break;
        }
    }
    // 可划拨弹框
    private void showDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_fragment, null);
        TextView textView = view.findViewById(R.id.dialog_tv1);
        TextView dialog_cancel = view.findViewById(R.id.dialog_cancel);
        TextView dialog_determine = view.findViewById(R.id.dialog_determine);
        textView.setText("共" + beanList_size.size() + "台,可划拨" + beanList_size.size() + "台");
        Dialog dialog = new MyDialog1(getActivity(), true, true, (float) 0.7).setNewView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交
                dialog.dismiss();
                getPost(beanList_size);
            }
        });
    }

    public void getPost(List<TerminalBean> beanList_size) {
        // 开启加载框
        loadDialog.show();
        data = new int[beanList_size.size()];
        for (int i = 0; i < beanList_size.size(); i++) {
            data[i] = Integer.valueOf(beanList_size.get(i).getPosId());
        }
        RequestParams params = new RequestParams();
        params.put("userId", getUserId());
        params.put("merchId", merchantId);
        params.put("operType", "1");  //1，划拨，2 回调
        params.put("orderId", orderID);  //
        HttpRequest.updPosListFrom(params, SPUtils.get(getActivity(), "Token", "-1").toString(), data, new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                loadDialog.dismiss();
                Toast.makeText(getActivity(), "划拨成功", Toast.LENGTH_LONG).show();
                EventBus.getDefault().post(new MeExchangeFragment());
                EventBus.getDefault().post(new ApplyExchangeFragment());
                getActivity().finish();
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                loadDialog.dismiss();
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });

    }

    //搜索框
    private void search() {
        merchants_query_ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    Utils.hideKeyboard(merchants_query_ed_search);
                    search_value = v.getText().toString();
                    mCount = 1;
                    beans.clear();
                    beanList_size.clear();
                    ceshiAdapter.clearData();
                    postData();
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * 扫描二维码/条码回传
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                merchants_query_ed_search.setText(content);
            }
        }
    }
}
