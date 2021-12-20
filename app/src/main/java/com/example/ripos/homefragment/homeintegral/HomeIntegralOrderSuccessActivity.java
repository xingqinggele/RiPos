package com.example.ripos.homefragment.homeintegral;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ripos.R;
import com.example.ripos.base.BaseActivity;

/**
 * 作者: qgl
 * 创建日期：2021/2/22
 * 描述: 订单生成界面
 */
public class HomeIntegralOrderSuccessActivity extends BaseActivity implements View.OnClickListener {
    private TextView item_me_exchange_number; //数量
    private TextView item_me_exchange_total_price; //总金额
    private TextView item_me_exchange_person; //申请人
    private TextView item_me_exchange_person_tv; //
    private TextView item_me_exchange_idnumber; //订单号
    private TextView item_me_exchange_time; //时间
    private LinearLayout iv_back;

    //配送方式
    private TextView distribution_type;
    //地址
    private TextView address_tv;
    //复制按钮
    private TextView tv_my1;
    //快递方式 显示布局
    private LinearLayout add_liner;
    //收货人姓名
    private TextView address_name_tv;
    //收货人电话
    private TextView address_phone_tv;
    //价格
    private TextView item_me_exchange_price;
    private TextView item_me_exchange_type;

    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.home_integral_ordersuccess_activity;
    }

    @Override
    protected void initView() {
        item_me_exchange_price = findViewById(R.id.item_me_exchange_price);
        item_me_exchange_number = findViewById(R.id.item_me_exchange_number);
        item_me_exchange_total_price = findViewById(R.id.item_me_exchange_total_price);
        item_me_exchange_person = findViewById(R.id.item_me_exchange_person);
        item_me_exchange_person_tv = findViewById(R.id.item_me_exchange_person_tv);
        item_me_exchange_idnumber = findViewById(R.id.item_me_exchange_idnumber);
        item_me_exchange_time = findViewById(R.id.item_me_exchange_time);
        iv_back = findViewById(R.id.iv_back);
        distribution_type = findViewById(R.id.distribution_type);
        address_tv = findViewById(R.id.address_tv);
        tv_my1 = findViewById(R.id.tv_my1);
        add_liner = findViewById(R.id.add_liner);
        address_name_tv = findViewById(R.id.address_name_tv);
        address_phone_tv = findViewById(R.id.address_phone_tv);
        item_me_exchange_type = findViewById(R.id.item_me_exchange_type);
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        tv_my1.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        item_me_exchange_type.setText(getIntent().getStringExtra("nameType"));
        item_me_exchange_price.setText("￥" + getIntent().getStringExtra("Price"));

        item_me_exchange_number.setText("x" + getIntent().getStringExtra("num"));
        item_me_exchange_total_price.setText(getIntent().getStringExtra("money"));
        if (getIntent().getStringExtra("parentName").equals("服务中心")){
            item_me_exchange_person_tv.setText("服务商");
        }else {
            item_me_exchange_person_tv.setText("申请人");
        }
        item_me_exchange_person.setText(getIntent().getStringExtra("parentName"));
        item_me_exchange_idnumber.setText(getIntent().getStringExtra("orderNo"));
        item_me_exchange_time.setText(getIntent().getStringExtra("createTime"));
        //配送方式
        distribution_type.setText(getIntent().getStringExtra("dispatchType"));
        if (getIntent().getStringExtra("dispatchType").equals("快递运送")){
            add_liner.setVisibility(View.VISIBLE);
            address_tv.setText(getIntent().getStringExtra("dataAddress"));
            address_name_tv.setText(getIntent().getStringExtra("dataAddressName"));
            address_phone_tv.setText(getIntent().getStringExtra("dataAddressPhone"));
        }else {
            add_liner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_my1:
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", address_tv.getText().toString().trim());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                showToast(2,"复制成功");
                break;
        }
    }
}
