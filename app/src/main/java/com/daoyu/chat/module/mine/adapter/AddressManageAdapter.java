package com.daoyu.chat.module.mine.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daoyu.chat.R;
import com.daoyu.chat.module.mine.bean.ShippingAddressData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressManageAdapter extends RecyclerView.Adapter<AddressManageAdapter.AddressListViewHolder> {

    private Context context;
    private ArrayList<ShippingAddressData> shippingAddress;
    private OnAddressViewClickListener listener;

    public void setListener(OnAddressViewClickListener listener) {
        this.listener = listener;
    }

    public AddressManageAdapter(Context context, ArrayList<ShippingAddressData> shippingAddress) {
        this.context = context;
        this.shippingAddress = shippingAddress;
    }

    @NonNull
    @Override
    public AddressListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AddressListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_address_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressListViewHolder holder, int position) {
        ShippingAddressData address = shippingAddress.get(position);
        holder.tvName.setText(address.aNick);
        holder.tvTel.setText(address.aPhone);
        String[] split = address.aAddress.split("=\\+=");
        holder.tvAddress.setText(split[0] + split[1]);
        String isDef = address.isDef;
        if ("Y".equals(isDef)) {
            holder.cbDefault.setChecked(true);
            holder.tvIsDef.setVisibility(View.VISIBLE);

        } else {
            holder.cbDefault.setChecked(false);
            holder.tvIsDef.setVisibility(View.GONE);
        }

        holder.clAddress.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onAddressItemViewClickListener(position, address);
        });
        holder.btnWrite.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onMenuClickListener(position, address, "write");
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener == null) return;
            listener.onMenuClickListener(position, address, "delete");
        });

    }

    @Override
    public int getItemCount() {
        return shippingAddress == null ? 0 : shippingAddress.size();
    }

    static class AddressListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_tel)
        TextView tvTel;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.cb_default)
        CheckBox cbDefault;
        @BindView(R.id.cl_address)
        ConstraintLayout clAddress;
        @BindView(R.id.btn_write)
        Button btnWrite;
        @BindView(R.id.btn_delete)
        Button btnDelete;
        @BindView(R.id.tv_address_def)
        TextView tvIsDef;

        public AddressListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAddressViewClickListener {
        void onMenuClickListener(int position, ShippingAddressData address, String type);

        void onAddressItemViewClickListener(int position, ShippingAddressData address);
    }

}
