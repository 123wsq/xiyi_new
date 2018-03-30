package com.example.wsq.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wsq.android.R;
import com.example.wsq.android.constant.ResponseKey;
import com.example.wsq.android.fragment.DeviceFragment;

import java.util.List;
import java.util.Map;

/**
 * Created by wsq on 2017/12/19.
 */

public class DefaultAdapter extends RecyclerView.Adapter<DefaultAdapter.ViewHolder>{

//    private Context mContext;
    private DeviceFragment mFragment;
    private List<Map<String, Object>> mData;
    private int selectPosition  = 0;
    public DefaultAdapter(DeviceFragment fragment, List<Map<String, Object>> list){
        this.mFragment = fragment;
        this.mData = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getActivity()).inflate(R.layout.layout_default_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tv_name.setText(mData.get(position).get(ResponseKey.NAME)+"");


        //设置选中效果  默认选中 0
        if(position == selectPosition){
            holder.ll_layout.setBackgroundResource(R.drawable.image_equ_left_arrow);
        }else{
            holder.ll_layout.setBackgroundColor(mFragment.getResources().getColor(R.color.color_white));
        }


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;
        LinearLayout ll_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_layout = itemView.findViewById(R.id.ll_layout);

            ll_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        List<Map<String, Object>> list = (List<Map<String, Object>>) mData.get(getPosition()).get(ResponseKey.SHEBEI);
            mFragment.updateData(list);
            selectPosition = getPosition();
            notifyDataSetChanged();
        }
    }
}
