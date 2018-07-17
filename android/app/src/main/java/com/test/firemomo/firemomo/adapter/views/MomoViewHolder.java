package com.test.firemomo.firemomo.adapter.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.firemomo.firemomo.R;

public class MomoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtUser;
    public TextView txtTime;
    public TextView txtLikes;
    public TextView txtComments;
    public TextView txtTitle;
    public ImageView momoImage;

    public ImageView momo;
    public MomoViewHolder(View itemView) {

        super(itemView);
        txtTitle=(TextView) itemView.findViewById(R.id.momo_title);
        txtUser=(TextView) itemView.findViewById(R.id.momo_user);
        txtTime=(TextView) itemView.findViewById(R.id.momo_timestamp);
        txtLikes=(TextView) itemView.findViewById(R.id.momo_likes);
        txtComments=(TextView) itemView.findViewById(R.id.momo_comments);
        momoImage=(ImageView)itemView.findViewById(R.id.momoImage);
    }

    @Override
    public void onClick(View v) {

    }
}
