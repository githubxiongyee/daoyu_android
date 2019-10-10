package com.daoyu.chat.module.mine.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.module.mine.bean.RemainDetailListB;
import com.daoyu.chat.utils.ToolsUtil;

import java.util.List;

public class RemainListAdapter extends BaseQuickAdapter<RemainDetailListB.DataBean, BaseViewHolder> {

    public RemainListAdapter(int layoutId, @Nullable List<RemainDetailListB.DataBean> remainDetailList) {
        super(layoutId,remainDetailList);
    }

    @Override
    protected void convert(BaseViewHolder helper, RemainDetailListB.DataBean item) {
        TextView tvRedBagTitle = helper.getView(R.id.tv_red_bag_title);
        TextView tvDate = helper.getView(R.id.tv_date);
        TextView tvMoneyAmount = helper.getView(R.id.tv_money_amount);
        String payWayStr = "";
        if (item.getPayWay()==1){
            payWayStr = "微信支付";
        }else if (item.getPayWay()==2){
            payWayStr = "支付宝";
        }else if (item.getPayWay() == 3) {
            payWayStr = "云闪付";
        }
        tvMoneyAmount.setText("+"+item.getMoney());
        tvMoneyAmount.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.color_F88F56));

        tvDate.setText(ToolsUtil.formatTime(item.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
        if (item.getOrderNo().isEmpty()||item.getOrderNo()==null)return;
        tvRedBagTitle.setText(payWayStr+" 单号："+item.getOrderNo());
    }

}
