package com.example.chenf.screenoncount;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chenf on 2015/12/23.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {

    public interface OnRecyclerViewListener{
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;
    private List<Duration> durations;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public RecyclerAdapter(List<Duration> durations){
        this.durations = durations;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new DurationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DurationViewHolder viewHolder = (DurationViewHolder) holder;
        Duration duration = durations.get(position);
        viewHolder.duration.setText(duration.getDuration());
        viewHolder.number.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return durations.size();
    }

    class DurationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        private TextView duration;
        private TextView number;
        private View rootView;

        public DurationViewHolder(View itemView) {
            super(itemView);
            duration = (TextView) itemView.findViewById(R.id.duration);
            number = (TextView) itemView.findViewById(R.id.number);
            rootView = itemView.findViewById(R.id.recycle_view_item_view);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRecyclerViewListener != null) {
                onRecyclerViewListener.onItemClick(this.getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onRecyclerViewListener != null) {
                return onRecyclerViewListener.onItemLongClick(this.getAdapterPosition());
            }
            return false;
        }
    }
}
