package com.example.ripos.homefragment.homeintegral.adpter;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.ripos.R;
import com.example.ripos.homefragment.homeintegral.bean.IntegralMostBean;
import com.example.ripos.homefragment.shopping.bean.ShoppingBean;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2021/5/17
 * 描述:商城列表Adapter
 */
public class IntegraMostListAdapter extends BaseQuickAdapter<IntegralMostBean, BaseViewHolder> {

    public IntegraMostListAdapter(int layoutResId, @Nullable List<IntegralMostBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, IntegralMostBean item) {
        SimpleDraweeView shop_im = helper.itemView.findViewById(R.id.shop_im);
        Uri imgurl = Uri.parse(item.getImgPath());
        // 清除Fresco对这条验证码的缓存
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(imgurl);
        imagePipeline.evictFromDiskCache(imgurl);
        // combines above two lines
        imagePipeline.evictFromCache(imgurl);
        shop_im.setImageURI(imgurl);
        helper.setText(R.id.shop_name, item.getTypeName());
        helper.setText(R.id.shop_price, "￥" + item.getReturnIntegral());
    }
}
