package com.test.firemomo.firemomo.adapter.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.firemomo.firemomo.R;

public class MomoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtUser;
    public TextView txtTime;
    public TextView txtTitle;

    public Button buttonLikes;
    public Button buttonComments;

    public ImageView momoImage;

    public MomoViewHolder(View itemView) {

        super(itemView);
        txtTitle = itemView.findViewById(R.id.momo_title);
        txtUser = itemView.findViewById(R.id.momo_user);
        txtTime = itemView.findViewById(R.id.momo_timestamp);

        buttonLikes = itemView.findViewById(R.id.button_likes);
        buttonComments = itemView.findViewById(R.id.button_comments);

        momoImage = itemView.findViewById(R.id.momoImage);
    }

    @Override
    public void onClick(View v) {

    }
}
