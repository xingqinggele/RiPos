package com.example.ripos.homefragment.homeintegral;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ripos.R;
import com.example.ripos.base.BaseActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

/**
 * 作者: qgl
 * 创建日期：2021/2/19
 * 描述: 积分兑换设备
 */
public class HomeIntegraExchangeActivity extends BaseActivity implements View.OnClickListener {
    private Button submit_btn; //兑换按钮
    private LinearLayout iv_back; //返回键
    private String integral = ""; //通用积分
    private String activityIntegral = ""; //活动积分
    private String totalintegral = ""; //总积分
    private TextView total_integral; //总积分tv
    private TextView tv_integral_all;//
    //pos名称
    private TextView tv_type;
    //pos价格
    private TextView tv_price;
    //pos类型ID
    private String TypeId = "";
    //详情图
    private SimpleDraweeView detail_img;
    //价格
    private String price = "";
    //名称
    private String name = "";
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.homeintegraexchange_activity;
    }

    @Override
    protected void initView() {
        iv_back = findViewById(R.id.iv_back);
        submit_btn = findViewById(R.id.submit_btn);
        total_integral = findViewById(R.id.total_integral);
        tv_integral_all = findViewById(R.id.tv_integral_all);
        tv_type = findViewById(R.id.tv_type);
        tv_price = findViewById(R.id.tv_price);
        detail_img = findViewById(R.id.detail_img);
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
    }

    @Override
    protected void initData() {

        Uri imgurl = Uri.parse(getIntent().getStringExtra("detailImg"));
        // 清除Fresco对这条验证码的缓存
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(imgurl);
        imagePipeline.evictFromDiskCache(imgurl);
        // combines above two lines
        imagePipeline.evictFromCache(imgurl);
        detail_img.setImageURI(imgurl);
        integral = getIntent().getStringExtra("integral");
        TypeId = getIntent().getStringExtra("id");
        activityIntegral = getIntent().getStringExtra("activityIntegral");
        totalintegral = getIntent().getStringExtra("totalintegral");
        name = getIntent().getStringExtra("typeName");
        tv_type.setText(name);
        price = getIntent().getStringExtra("returnIntegral");
        tv_price.setText("￥" + price);
        total_integral.setText("积分：" + totalintegral);
        tv_integral_all.setText("（通用积分" + integral + ",活动积分" + activityIntegral +"）");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit_btn:
                Intent intent = new Intent(HomeIntegraExchangeActivity.this,HomeIntegralOrderActivity.class);
                intent.putExtra("integral",integral);
                intent.putExtra("id",TypeId);
                intent.putExtra("price",price);
                intent.putExtra("name",name);
                startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
