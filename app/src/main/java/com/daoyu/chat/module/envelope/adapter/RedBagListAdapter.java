package com.daoyu.chat.module.envelope.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daoyu.chat.R;
import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.module.mine.bean.GetPackageListB;
import com.daoyu.chat.utils.ToolsUtil;

import java.util.List;

public class RedBagListAdapter extends BaseQuickAdapter<GetPackageListB.DataBeanX.DataBean, BaseViewHolder> {

    public RedBagListAdapter(int layoutId, @Nullable List<GetPackageListB.DataBeanX.DataBean> packageList) {
        super(layoutId,packageList);
    }

    @Override
    protected void convert(BaseViewHolder helper, GetPackageListB.DataBeanX.DataBean item) {
        TextView tvRedBagTitle = helper.getView(R.id.tv_red_bag_title);
        TextView tvDate = helper.getView(R.id.tv_date);
        TextView tvMoneyAmount = helper.getView(R.id.tv_money_amount);

        if (item.getVipStatus()==1){
            if (item.getSource()==null){
                tvRedBagTitle.setText("平台红包");
            }else {
                if (item.getSource().equals("NearBy")){
                    tvRedBagTitle.setText("附近红包");
                }else if (item.getSource().equals("CheckIn")){
                    tvRedBagTitle.setText("签到红包");
                }else if (item.getSource().equals("ListMIn")){
                    tvRedBagTitle.setText("通讯录红包");
                }else if (item.getSource().equals("InVite")){
                    tvRedBagTitle.setText("邀请码红包");
                }else if (item.getSource().equals("GroupHong")){
                    tvRedBagTitle.setText("群红包");
                }else {
                    tvRedBagTitle.setText("平台红包");
                }
            }
            tvMoneyAmount.setText(String.format("+%.2f", item.getVipCredit()));
            tvMoneyAmount.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.color_F88F56));
        }else if (item.getVipStatus()==2){
            tvMoneyAmount.setText(String.format("-%.2f", item.getVipCredit()));
            tvMoneyAmount.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.black));
            tvRedBagTitle.setText("平台红包");
        }else if (item.getVipStatus() == 4){
            tvMoneyAmount.setText(String.format("-%.2f", item.getVipCredit()));
            tvMoneyAmount.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.black));
            tvRedBagTitle.setText("平台红包");
        } else if (item.getVipStatus() == 3) {
            tvMoneyAmount.setText(String.format("-%.2f", item.getVipCredit()));
            tvMoneyAmount.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.black));
            tvRedBagTitle.setText("平台红包");
        }

        tvDate.setText(ToolsUtil.formatTime(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));

    }

}
