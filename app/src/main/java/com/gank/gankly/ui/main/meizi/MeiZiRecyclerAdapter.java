package com.gank.gankly.ui.main.meizi;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gank.gankly.R;
import com.gank.gankly.bean.ResultsBean;
import com.gank.gankly.listener.MeiziOnClick;
import com.gank.gankly.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Create by LingYan on 2016-04-06
 */
public class MeiZiRecyclerAdapter extends RecyclerView.Adapter<MeiZiRecyclerAdapter.GoodsViewHolder> {
    private List<ResultsBean> mResults;
    private Activity mContext;
    private LayoutInflater inflater;
    private int mScreenWidth = AppUtils.getDisplayWidth() / 2;
    private int mScreenHeight = AppUtils.getDisplayWidth() / 2;
    private Map<String, Integer> heights = new HashMap<>();

    private MeiziOnClick mMeiZiOnClick;

    public void setMeiZiOnClick(MeiziOnClick meiZiOnClick) {
        mMeiZiOnClick = meiZiOnClick;
    }

    public MeiZiRecyclerAdapter(Activity context) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mResults = new ArrayList<>();
    }

    @Override
    public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_meizi, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GoodsViewHolder holder, final int position) {
        final ResultsBean bean = mResults.get(position);
        final String url = bean.getUrl();
        BitmapRequestBuilder requestBuilder = Glide.with(mContext)
                .load(url)
                .asBitmap()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        if (heights.containsKey(url)) {
            setCardViewLayoutParams(holder.imgMeizi, mScreenWidth, heights.get(url));
            requestBuilder.into(holder.imgMeizi);
        } else {
            requestBuilder.override(mScreenWidth, mScreenHeight);//设置宽高一致，后期改动不大
            requestBuilder.into(new DriverViewTarget(holder.imgMeizi, url));
        }
    }


    private class DriverViewTarget extends BitmapImageViewTarget {
        private ImageView mImageView;
        private String url;

        public DriverViewTarget(ImageView image, String url) {
            super(image);
            this.mImageView = image;
            this.url = url;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            int viewWidth = mScreenWidth;
            int viewHeight;
            if (!heights.containsKey(url) && url != null) {
                viewHeight = resource.getHeight() * viewWidth / resource.getWidth();
                heights.put(url, viewHeight);
                setCardViewLayoutParams(mImageView, viewWidth, viewHeight);
            }
            super.onResourceReady(resource, glideAnimation);
        }
    }

    private void setCardViewLayoutParams(ImageView mImageView, int width, int height) {
        ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mImageView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void updateItems(List<ResultsBean> goods) {
        clear();
        addItems(goods);
    }

    public void clear() {
        mResults.clear();
        heights.clear();
    }

    public void addItems(List<ResultsBean> goods) {
        mResults.addAll(goods);
        notifyItemRangeInserted(mResults.size(), goods.size());
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.meizi_img_picture)
        ImageView imgMeizi;

        public GoodsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imgMeizi.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mMeiZiOnClick != null) {
                mMeiZiOnClick.onClick(v, getAdapterPosition());
            }
        }
    }
}
