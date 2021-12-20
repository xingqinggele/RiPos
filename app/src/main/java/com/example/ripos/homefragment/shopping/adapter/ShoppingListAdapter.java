package com.example.ripos.homefragment.shopping.adapter;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.ripos.R;
import com.example.ripos.datafragment.databenefit.bean.DataBenefitBean;
import com.example.ripos.homefragment.shopping.bean.ShoppingBean;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2021/5/17
 * 描述:商城列表Adapter
 */
public class ShoppingListAdapter extends BaseQuickAdapter<ShoppingBean, BaseViewHolder> {

    public ShoppingListAdapter(int layoutResId, @Nullable List<ShoppingBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShoppingBean item) {
        SimpleDraweeView shop_im = helper.itemView.findViewById(R.id.shop_im);
        shop_im.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.shopping_img)).build());
        helper.setText(R.id.shop_name, item.getName());
    }
}
