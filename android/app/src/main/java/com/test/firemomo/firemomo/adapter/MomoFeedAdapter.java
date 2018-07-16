package com.test.firemomo.firemomo.adapter;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.firemomo.firemomo.R;
import com.test.firemomo.firemomo.adapter.views.MomoViewHolder;
import com.test.firemomo.firemomo.models.Momo;

import java.util.Collections;
import java.util.List;

public class MomoFeedAdapter extends RecyclerView.Adapter<MomoViewHolder>  {
    private List<Momo> lstMenu = Collections.EMPTY_LIST;


    @Override
    public MomoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.momo_feed_item, parent, false);

        MomoViewHolder vh = new MomoViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder( MomoViewHolder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return 0;
    }
}
