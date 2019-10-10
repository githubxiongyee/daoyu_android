package com.daoyu.chat.module.mine.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.common.Constant;
import com.daoyu.chat.module.envelope.bean.CreateChangeB;
import com.daoyu.chat.module.mine.activity.OrderDetailActivity;
import com.daoyu.chat.module.mine.activity.PaymentOrderActivity;
import com.daoyu.chat.utils.ImageUtils;
import java.util.List;

public class OrderAdapter extends BaseQuickAdapter<CreateChangeB.CreateChangeData, BaseViewHolder> {
    private OnItemClickListener listener;
    Context mContext;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OrderAdapter(Context context,int layoutResId, @Nullable List<CreateChangeB.CreateChangeData> data) {
        super(layoutResId,data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CreateChangeB.CreateChangeData dataBean) {
        TextView tvOperating = helper.getView(R.id.tv_logistics);
        tvOperating.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onItemClick(this, tvOperating, helper.getPosition());
        });
        ((TextView)helper.getView(R.id.tv_order)).setText("订单编号："+dataBean.orderNumber);
        ((TextView)helper.getView(R.id.tv_goods_name)).setText(dataBean.proName);
        ((TextView)helper.getView(R.id.tv_price)).setText(""+dataBean.orderPrice);
        ((TextView)helper.getView(R.id.tv_amount)).setText(String.format("¥%.2f", dataBean.orderPrice));
        if (dataBean.orderStatus.equals("1")){
            ((TextView)helper.getView(R.id.tv_order_status)).setText("待兑换");
            helper.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, PaymentOrderActivity.class);
                intent.putExtra(Constant.ORDER_INFO, dataBean);
                mContext.startActivity(intent);
            });
        }else if (dataBean.orderStatus.equals("2")){
            ((TextView)helper.getView(R.id.tv_order_status)).setText("已取消");
            helper.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                intent.putExtra(Constant.ORDER_NUMBER, dataBean.orderNumber);
                Log.i("ORDER_NUMBER","ORDER_NUMBER======="+dataBean.orderNumber);
                intent.putExtra(Constant.ORDER_PIC_URL, dataBean.proImg);
                intent.putExtra(Constant.ORDER_PRODUCT_NAME, dataBean.proName);
                intent.putExtra(Constant.ORDER_ORI_PRICE, ""+dataBean.orderPrice);
                intent.putExtra(Constant.ORDER_STATUS, 2);
                mContext.startActivity(intent);
            });
        }else if (dataBean.orderStatus.equals("3")){
            ((TextView)helper.getView(R.id.tv_order_status)).setText("已兑换");
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    intent.putExtra(Constant.ORDER_NUMBER, dataBean.orderNumber);

                    intent.putExtra(Constant.ORDER_PIC_URL, dataBean.proImg);
                    intent.putExtra(Constant.ORDER_PRODUCT_NAME, dataBean.proName);
                    intent.putExtra(Constant.ORDER_ORI_PRICE, ""+dataBean.orderPrice);
                    intent.putExtra(Constant.ORDER_STATUS, 3);
                    mContext.startActivity(intent);
                }
            });
        }else if (dataBean.orderStatus.equals("4")){
            ((TextView)helper.getView(R.id.tv_order_status)).setText("已发货");
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    intent.putExtra(Constant.ORDER_NUMBER, dataBean.orderNumber);
                    Log.i("ORDER_NUMBER","ORDER_NUMBER======="+dataBean.orderNumber);
                    intent.putExtra(Constant.ORDER_PIC_URL, dataBean.proImg);
                    intent.putExtra(Constant.ORDER_PRODUCT_NAME, dataBean.proName);
                    intent.putExtra(Constant.ORDER_ORI_PRICE, ""+dataBean.orderPrice);
                    intent.putExtra(Constant.ORDER_STATUS, 4);
                    mContext.startActivity(intent);
                }
            });
        }
        ImageUtils.setNormalImage(mContext, dataBean.proImg, R.drawable.ic_placeholder, R.drawable.ic_placeholder, helper.getView(R.id.iv_goods));

    }
}
