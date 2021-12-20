package com.example.ripos.homefragment.homeintegral;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ripos.R;
import com.example.ripos.base.BaseActivity;
import com.example.ripos.homefragment.homeequipment.activity.TransferPersonActivity;
import com.example.ripos.mefragment.meaddres.MeAddressActivity;
import com.example.ripos.net.HttpRequest;
import com.example.ripos.net.OkHttpException;
import com.example.ripos.net.RequestParams;
import com.example.ripos.net.ResponseCallback;
import com.example.ripos.utils.Convenient_utils;
import com.example.ripos.utils.SPUtils;
import com.example.ripos.views.AmountView;
import com.example.ripos.views.MyDialog;
import com.example.ripos.views.MyDialog1;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2021/2/20
 * 描述:积分兑换订单
 */
public class HomeIntegralOrderActivity extends BaseActivity implements View.OnClickListener, AmountView.OnAmountChangeListener, RadioGroup.OnCheckedChangeListener {
    //商品加减控件
    private AmountView mAmountView;
    //总金额
    private TextView total_amount;
    //兑换对象显示控件
    private TextView option_type;
    // 兑换对象选择
    private RelativeLayout select_object;
    private int Initial_Amount = 0; //订单总金额、积分
    private int Machine_Price = 0; //机器积分
    private int Me_Amount = 0; //我的可用积分
    private String select_code = ""; //择兑换对象值、上传给后台值ID
    private LinearLayout iv_back; //返回键
    private Button submit_btn; // 提交订单按钮
    private TextView item_me_name; //姓名手机号
    private int machineCode = 5; //可选择划拨的机器数量、默认5台
    private String dispatchType = "当面发货"; //选择的发货方式
    private RadioGroup radio_group; //选择发货方式、选择框
    private TextView order_num_intal; //可用积分
    private RelativeLayout select_partner;//选择合作伙伴按钮、跳转
    private static final int PcCODE = 1;
    //收货地址线
    private View address_view;
    //收货地址区域
    private RelativeLayout address_relative;
    //默认地址
    private TextView order_address;
    //后台返回的地址ID
    private String addressId = "";
    //pos价格
    private TextView item_me_exchange_price;
    //pos名称
    private TextView item_me_exchange_type;
    //pos类型id
    private String TypeId = "";

    //xml界面
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.homeintegralorder_activity;
    }

    //控件初始化
    @Override
    protected void initView() {
        mAmountView = findViewById(R.id.amount_view);
        total_amount = findViewById(R.id.total_acoment);
        select_object = findViewById(R.id.select_object);
        option_type = findViewById(R.id.option_type);
        iv_back = findViewById(R.id.iv_back);
        submit_btn = findViewById(R.id.submit_btn);
        item_me_name = findViewById(R.id.item_me_name);
        radio_group = findViewById(R.id.radio_group);
        order_num_intal = findViewById(R.id.order_num_intal);
        select_partner = findViewById(R.id.select_partner);
        address_view = findViewById(R.id.address_view);
        address_relative = findViewById(R.id.address_relative);
        order_address = findViewById(R.id.order_address);
        item_me_exchange_price = findViewById(R.id.item_me_exchange_price);
        item_me_exchange_type = findViewById(R.id.item_me_exchange_type);
    }

    //点击事件绑定
    @Override
    protected void initListener() {
        select_object.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        select_partner.setOnClickListener(this);
        mAmountView.setOnAmountChangeListener(this);
        radio_group.setOnCheckedChangeListener(this);
        address_relative.setOnClickListener(this);
    }

    // 数据配置
    @Override
    protected void initData() {
        TypeId = getIntent().getStringExtra("id");
        item_me_exchange_price.setText("￥" + getIntent().getStringExtra("price"));
        item_me_exchange_type.setText(getIntent().getStringExtra("name"));
        Me_Amount = Integer.parseInt(getIntent().getStringExtra("integral"));
        Machine_Price = Integer.parseInt(getIntent().getStringExtra("price"));
        //默认选择5台机器、价格乘于5 = 总积分
        Initial_Amount = Machine_Price * 5;
        //默认显示5台机器价格
        total_amount.setText("￥ " + Initial_Amount);
        if (Initial_Amount > Me_Amount) {
            Convenient_utils.UndBtn(false, submit_btn);
        } else {
            Convenient_utils.UndBtn(true, submit_btn);
        }
        item_me_name.setText(SPUtils.get(this, "nickName", "").toString() + "\t" + getUserName().substring(0, 3) + "***" + getUserName().substring(getUserName().length() - 4));
        order_num_intal.setText("（可用积分" + Me_Amount + "）");

    }

    /**
     * 按钮点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_object:
                showDialog();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.submit_btn:
                if (TextUtils.isEmpty(select_code)) {
                    showToast(3, "请选择兑换对象");
                    return;
                }
                if (dispatchType.equals("快递运送")) {
                    if (TextUtils.isEmpty(addressId)) {
                        showToast(3, "请选择收货地址");
                        return;
                    }
                }
                showDialog1();
                break;
            //跳转到选择同团队的伙伴界面
            case R.id.select_partner:
                startActivityForResult(new Intent(HomeIntegralOrderActivity.this, HomeIntegralOrderPersonActivity.class), PcCODE);
                break;
            //跳转到我的收货地址
            case R.id.address_relative:
                Intent intent = new Intent(HomeIntegralOrderActivity.this, MeAddressActivity.class);
                intent.putExtra("status", "1");
                startActivityForResult(intent, 1);
                break;
        }
    }

    /**
     * 商品加减控件监听事件
     *
     * @param view
     * @param amount
     */
    @Override
    public void onAmountChange(View view, int amount) {
        Initial_Amount = Machine_Price * amount;
        machineCode = amount;
        if (Initial_Amount > Me_Amount) {
            Convenient_utils.UndBtn(false, submit_btn);
        } else {
            Convenient_utils.UndBtn(true, submit_btn);
        }
        total_amount.setText("￥ " + Initial_Amount);
    }


    //提交数据到后台
    public void getData() {
        RequestParams params = new RequestParams();
        params.put("userId", getUserId());
        params.put("num", machineCode + "");
        params.put("money", Initial_Amount + "");
        params.put("parentId", select_code + "");
        params.put("dispatchType", dispatchType);
        params.put("addressId", addressId);
        params.put("posTypeId", TypeId);
        HttpRequest.getSubmit_orders(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject dataAddress = new JSONObject(result.getJSONObject("dataAddress").toString());
                    Intent intent = new Intent(HomeIntegralOrderActivity.this, HomeIntegralOrderSuccessActivity.class);
                    intent.putExtra("orderNo", result.getJSONObject("data").getString("orderNo"));
                    intent.putExtra("num", result.getJSONObject("data").getString("num"));
                    intent.putExtra("money", result.getJSONObject("data").getString("money"));
                    intent.putExtra("parentName", result.getJSONObject("data").getString("parentName"));
                    intent.putExtra("createTime", result.getJSONObject("data").getString("createTime"));
                    intent.putExtra("dispatchType", result.getJSONObject("data").getString("dispatchType"));
                    intent.putExtra("dataAddress", dataAddress.getString("address"));
                    intent.putExtra("dataAddressName", dataAddress.getString("name"));
                    intent.putExtra("dataAddressPhone", dataAddress.getString("phone"));
                    intent.putExtra("Price",Machine_Price + "");
                    intent.putExtra("nameType",getIntent().getStringExtra("name"));
                    startActivity(intent);
                    finish();
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


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.face_delivery:
                dispatchType = "当面发货";
                address_view.setVisibility(View.GONE);
                address_relative.setVisibility(View.GONE);
                break;
            case R.id.courier_delivery:
                dispatchType = "快递运送";
                address_view.setVisibility(View.VISIBLE);
                address_relative.setVisibility(View.VISIBLE);
                orderAddress();
                break;
        }
    }

    //获取我的默认收货地址
    private void orderAddress() {
        RequestParams params = new RequestParams();
        HttpRequest.orderAddress(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    if (("1").equals(result.getJSONObject("data").getString("isAddress"))) {
                        addressId = "";
                        order_address.setText("暂无地址, 请添加...");
                    } else {
                        addressId = result.getJSONObject("data").getString("id");
                        order_address.setText(result.getJSONObject("data").getString("addr") + result.getJSONObject("data").getString("addrInfo"));
                    }
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

    public void showDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_integral_order_dialog_item, null);
        Dialog dialog = new MyDialog(HomeIntegralOrderActivity.this, true, true, (float) 1).setNewView(view);
        TextView tv1 = view.findViewById(R.id.tv1);
        TextView tv2 = view.findViewById(R.id.tv2);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option_type.setText(tv1.getText().toString().trim());
                //选服务中心 ID 默认为 2
                select_code = "2";
                select_partner.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option_type.setText(tv2.getText().toString().trim());
                select_partner.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showDialog1() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_fragment, null);
        TextView textView = view.findViewById(R.id.dialog_tv1);
        TextView dialog_cancel = view.findViewById(R.id.dialog_cancel);
        TextView dialog_determine = view.findViewById(R.id.dialog_determine);
        textView.setText("你是否要提交订单？");
        Dialog dialog = new MyDialog1(HomeIntegralOrderActivity.this, true, true, (float) 0.7).setNewView(view);
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
                getData();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PcCODE && resultCode == 3) {
            option_type.setText(data.getStringExtra("name"));
            select_code = data.getStringExtra("id");
        } else if (requestCode == 1 && resultCode == 110) {
            order_address.setText(data.getStringExtra("address"));
            addressId = data.getStringExtra("id");
        }
    }
}
