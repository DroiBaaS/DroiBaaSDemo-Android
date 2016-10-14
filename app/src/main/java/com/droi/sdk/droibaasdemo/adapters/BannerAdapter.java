package com.droi.sdk.droibaasdemo.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.droi.sdk.droibaasdemo.models.Banner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by chenpei on 2016/5/11.
 */
public class BannerAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Banner> mBanners;

    public BannerAdapter(Context mContext, ArrayList<Banner> mBanners) {
        this.mContext = mContext;
        this.mBanners = mBanners;
    }

    @Override
    public int getCount() {
        return mBanners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String imgUrl = mBanners.get(position).getImgUrl();
        Picasso.with(mContext).load(imgUrl).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
