package com.example.learningapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningapp.R;
import com.example.learningapp.views.myviews.ZoomImageView;

import java.util.ArrayList;
import java.util.List;

public class ZoomImageViewActivity extends AppCompatActivity {

    private int[] imageIds = new int[]{R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_background};

    private ViewPager mVp;
    private List<ZoomImageView> zoomImageViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_view);
        mVp = findViewById(R.id.vp_views_vp);
        mVp.setAdapter(new PagerAdapter() {

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ZoomImageView zoomImageView = new ZoomImageView(ZoomImageViewActivity.this);
                zoomImageView.setImageResource(imageIds[position]);
                container.addView(zoomImageView);
                zoomImageViews.add(position, zoomImageView);
                Log.d("debug", "instantiate Item");
                return zoomImageView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {
                return imageIds.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });
    }
}
