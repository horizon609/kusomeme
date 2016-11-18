package com.echoplex_x.kusomeme.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.echoplex_x.kusomeme.R;
import com.echoplex_x.kusomeme.bean.MemeCollection;

import java.util.ArrayList;
import java.util.Locale;

import static com.echoplex_x.kusomeme.R.id.textView;

/**
 * Created by echoplex_x on 2016/11/10.
 */
public class MemeAdapter extends BaseRecyclerAdapter<MemeCollection.MemeItem, MemeAdapter.MyViewHolder> {
    private static final String TAG = "MemeAdapter";


    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        //        final TextView textView;
        View rootView;
        ImageView imageView;
        int position;

        public MyViewHolder(View v) {
            super(v);
            rootView = v.findViewById(R.id.home_item_root);
            imageView = (ImageView) v.findViewById(R.id.home_item_image);

            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(v, position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != onRecyclerViewListener) {
                return onRecyclerViewListener.onItemLongClick(position);
            }
            return false;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    public MemeAdapter(Context context) {
        super(context);
    }

    public MemeAdapter(Context mContext, ArrayList mArrayList) {
        super(mContext, mArrayList);
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_picture, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(mContext).load(mDataList.get(position).getUrl()).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        e.printStackTrace();

                android.util.Log.d("GLIDE", String.format(Locale.ROOT,
                        "onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                android.util.Log.d("GLIDE", String.format(Locale.ROOT,
                        "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target, isFromMemoryCache, isFirstResource));
                return false;
            }
        }).into(holder.imageView);
        holder.position = position;

    }

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)

    // Replace the contents of a view (invoked by the layout manager)
    // END_INCLUDE(recyclerViewOnCreateViewHolder)


    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
