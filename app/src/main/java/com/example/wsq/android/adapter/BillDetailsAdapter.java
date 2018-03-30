package com.example.wsq.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wsq.android.R;
import com.example.wsq.android.constant.ResponseKey;
import com.example.wsq.android.inter.OnDefaultClickListener;

import java.util.List;
import java.util.Map;

/**
 * Created by wsq on 2017/12/19.
 */

public class BillDetailsAdapter extends RecyclerView.Adapter<BillDetailsAdapter.ViewHolder>{

    private Context mContext;
    private List<Map<String, Object>> mData;
    private OnDefaultClickListener mListener;
    public BillDetailsAdapter(Context context, List<Map<String, Object>> list, OnDefaultClickListener clickListener){
        this.mContext = context;
        this.mData = list;
        this.mListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_baill_details_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tv_apply_time.setText(mData.get(position).get(ResponseKey.CREAT_AT)+"");
        holder.tv_money.setText("-"+mData.get(position).get(ResponseKey.MONEY)+"");
        holder.tv_balance.setText(mData.get(position).get(ResponseKey.ACTUAL_MONEY)+"");
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_apply_time;
        TextView tv_balance;
        TextView tv_money;
        LinearLayout ll_layout;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_apply_time = itemView.findViewById(R.id.tv_apply_time);
            ll_layout = itemView.findViewById(R.id.ll_layout);
            tv_balance = itemView.findViewById(R.id.tv_balance);
            tv_money = itemView.findViewById(R.id.tv_money);

            ll_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mListener.onClickListener(getPosition());
        }
    }
}
