package com.test.firemomo.firemomo.adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.test.firemomo.firemomo.R;
import com.test.firemomo.firemomo.adapter.views.MomoViewHolder;
import com.test.firemomo.firemomo.models.Momo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MomoFeedAdapter extends RecyclerView.Adapter<MomoViewHolder>  {
    private List<Momo> lstMomo = Collections.EMPTY_LIST;


    public MomoFeedAdapter(List<Momo> lstMenu ){
       this.lstMomo=lstMenu;

    }
    @Override
    public MomoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.momo_feed_item, parent, false);

        MomoViewHolder vh = new MomoViewHolder(v);


        return vh;
    }

    @Override
    public void onBindViewHolder( MomoViewHolder holder, int position) {
            holder.txtUser.setText(lstMomo.get(position).getUsrName());
            holder.txtTime.setText(lstMomo.get(position).getTimeStamp());
            holder.txtTitle.setText(lstMomo.get(position).getTitle());
            holder.buttonLikes.setText(lstMomo.get(position).getLikes());
            holder.buttonComments.setText(lstMomo.get(position).getCommentCount());
            holder.momoImage.setTag(lstMomo.get(position).getImageURL());
            new DownloadImagesTask().execute(holder.momoImage);
    }

     @Override
    public int getItemCount() {
        return  lstMomo.size();
    }


    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

        ImageView imageView = null;

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }


        private Bitmap download_Image(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }
    }
}
