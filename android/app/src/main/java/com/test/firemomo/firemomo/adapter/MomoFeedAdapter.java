package com.test.firemomo.firemomo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.test.firemomo.firemomo.R;
import com.test.firemomo.firemomo.adapter.views.MomoViewHolder;
import com.test.firemomo.firemomo.models.Momo;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MomoFeedAdapter extends RecyclerView.Adapter<MomoViewHolder> {

  private static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("MM/dd", Locale.getDefault());

  private List<Momo> lstMomo = Collections.EMPTY_LIST;

  public MomoFeedAdapter(List<Momo> lstMenu) {
    this.lstMomo = lstMenu;
  }

  @Override
  public MomoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.momo_feed_item, parent, false);

    MomoViewHolder vh = new MomoViewHolder(v);

    return vh;
  }

  @Override
  public void onBindViewHolder(MomoViewHolder holder, int position) {
    Momo momo = lstMomo.get(position);

    String dateString = DATE_FORMAT.format(momo.getTimeStamp());

    holder.txtUser.setText(momo.getUsrName());
    holder.txtTime.setText(dateString);
    holder.txtTitle.setText(momo.getTitle());
    holder.buttonLikes.setText(momo.getLikes());
    holder.buttonComments.setText(momo.getCommentCount());

    Glide.with(holder.momoImage)
            .load(momo.getImageURL())
            .apply(RequestOptions.placeholderOf(R.drawable.gray))
            .into(holder.momoImage);
  }

  @Override
  public int getItemCount() {
    return lstMomo.size();
  }
}
