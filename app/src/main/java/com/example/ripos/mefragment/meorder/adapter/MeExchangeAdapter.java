package com.example.ripos.mefragment.meorder.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.ripos.R;
import com.example.ripos.mefragment.meorder.bean.MeExchangeBean;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2020/11/13
 * 描述: 系统消息adapter
 */
public class MeExchangeAdapter extends BaseQuickAdapter<MeExchangeBean, BaseViewHolder> {
    public MeExchangeAdapter(int layoutResId, @Nullable List<MeExchangeBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MeExchangeBean item) {
        SimpleDraweeView item_me_exchange_logo = helper.itemView.findViewById(R.id.item_me_exchange_logo);
        Uri imgurl = Uri.parse(item.getImgPath());
        // 清除Fresco对这条验证码的缓存
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(imgurl);
        imagePipeline.evictFromDiskCache(imgurl);
        // combines above two lines
        imagePipeline.evictFromCache(imgurl);
        item_me_exchange_logo.setImageURI(imgurl);
        helper.setText(R.id.item_me_exchange_type, item.getTypeName());
        helper.setText(R.id.item_me_exchange_price, item.getReturnIntegral());
        helper.setText(R.id.item_me_exchange_time, item.getTime());
        if (item.getState().equals("0")) {
            helper.setTextColor(R.id.item_me_exchange_state, Color.parseColor("#C62101"));
            helper.setText(R.id.item_me_exchange_state, "待发货");
        } else if (item.getState().equals("1")) {
            helper.setTextColor(R.id.item_me_exchange_state, Color.parseColor("#7EC601"));
            helper.setText(R.id.item_me_exchange_state, "已完成");
        } else if (item.getState().equals("2")) {
            helper.setTextColor(R.id.item_me_exchange_state, Color.parseColor("#ffff9920"));
            helper.setText(R.id.item_me_exchange_state, "已超时");
        }
        helper.setText(R.id.item_me_exchange_number, "x" + item.getNumber());
        if (item.getPersonstate().equals("服务中心")) {
            helper.setText(R.id.item_me_exchange_name, "服务商:   " + item.getPersonstate());
        } else {
            helper.setText(R.id.item_me_exchange_name, "申请人:   " + item.getPersonstate());
        }
        helper.setText(R.id.item_me_exchange_total_amount, "合计: ￥" + item.getTotalamount());


    }




}
