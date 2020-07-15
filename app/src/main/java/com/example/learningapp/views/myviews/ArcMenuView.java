package com.example.learningapp.views.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;

import com.example.learningapp.R;

import java.util.ArrayList;
import java.util.List;

public class ArcMenuView extends ViewGroup implements ViewGroup.OnClickListener {

    public static final int POSITION_LEFT_TOP = 0;
    public static final int POSITION_LEFT_BOTTOM = 1;
    public static final int POSITION_RIGHT_TOP = 2;
    public static final int POSITION_RIGHT_BOTTOM = 3;

    private static final int MIN_CHILD_COUNT = 2;

    public Status mStatus = Status.ClOSE;
    public int mPosition;

    private int mRadius;
    private float angle;

    private View mMainView;
    private List<View> mMenuViews;

    private OnMenuItemClickListener onMenuItemClickListener;

    private long animDuration = 500;


    public enum Status{
        OPEN, ClOSE
    }

    public ArcMenuView(Context context) throws Exception {
        this(context, null);
    }

    public ArcMenuView(Context context, @Nullable AttributeSet attrs) throws Exception {
        this(context, attrs, 0);
    }

    public ArcMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) throws Exception {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenuView, defStyleAttr, 0);
        mPosition = typedArray.getInt(R.styleable.ArcMenuView_position, POSITION_RIGHT_BOTTOM);
        int defRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        mRadius = (int) typedArray.getDimension(R.styleable.ArcMenuView_radius, defRadius);
        typedArray.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        int childCount = getChildCount();
        if (childCount < MIN_CHILD_COUNT + 1){
//            throw new Exception(String.format("ArcMenuView must have a main child and more than %d child", MIN_CHILD_COUNT));
            return;
        }

        if (mMainView == null){
            mMainView = getChildAt(0);
            mMenuViews = new ArrayList<>(childCount - 1);
            for (int i=1; i<childCount; ++i)
                mMenuViews.add(i-1, getChildAt(i));

            angle = (float) (Math.PI/2 / (childCount - 2));
            Log.d("debug", "angle "+angle);
        }

        if (changed){
            layoutMainView(l, t, r, b);
            for (int i = 0; i< mMenuViews.size(); ++i){
                layoutMenuView(mMenuViews.get(i), i);
                if (mStatus == Status.ClOSE)
                    mMenuViews.get(i).setVisibility(View.GONE);
            }
        }

        // OnClick
        mMainView.setOnClickListener(this);


        for (int i = 0; i < mMenuViews.size(); ++i) {
            final int finalI = i;
            mMenuViews.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMenuItemClickListener != null) {
                        onMenuItemClickListener.onMenuItemClick(v);
                    }
                    clickMenuAnim(finalI);
                }
            });
        }

    }

    private void layoutMainView(int l, int t, int r, int b) {
        int left = 0, top = 0;
        switch (mPosition){
            case POSITION_LEFT_TOP:
                left += getPaddingLeft();
                top += getPaddingTop();
                break;
            case POSITION_LEFT_BOTTOM:
                left += getPaddingLeft();
                top = getHeight() - mMainView.getMeasuredHeight() - getPaddingBottom();
                break;
            case POSITION_RIGHT_TOP:
                left = getWidth() - mMainView.getMeasuredWidth() - getPaddingRight();
                top += getPaddingTop();
                break;
            case POSITION_RIGHT_BOTTOM:
                left = getWidth() - mMainView.getMeasuredWidth() - getPaddingRight();
                top = getHeight() - mMainView.getMeasuredHeight() - getPaddingBottom();
                break;
        }
        mMainView.layout(left, top, left+mMainView.getMeasuredWidth(), top + mMainView.getMeasuredHeight());
    }

    private void layoutMenuView(View menu, int i) {
        int centerX = mMainView.getLeft() + mMainView.getWidth()/2;
        int centerY = mMainView.getTop() + mMainView.getHeight()/2;
//        Log.d("debug", "layout "+i+" "+centerX+" "+centerY);
        switch (mPosition){
            case POSITION_LEFT_TOP:
                centerX += mRadius * Math.cos(i*angle);
                centerY += mRadius * Math.sin(i*angle);
                break;
            case POSITION_LEFT_BOTTOM:
                centerX += mRadius * Math.cos(i*angle);
                centerY -= mRadius * Math.sin(i*angle);
                break;
            case POSITION_RIGHT_TOP:
                centerX -= mRadius * Math.cos(i*angle);
                centerY += mRadius * Math.sin(i*angle);
                break;
            case POSITION_RIGHT_BOTTOM:
                centerX -= mRadius * Math.cos(i*angle);
                centerY -= mRadius * Math.sin(i*angle);
                break;
        }
//        Log.d("debug", "center "+centerX +" "+centerY);
        int w = menu.getMeasuredWidth(), h = menu.getMeasuredHeight();
        menu.layout(centerX-w/2, centerY-h/2, centerX+w/2, centerY+h/2);
    }

    @Override
    public void onClick(View v) {
        rotateMainView();
        toggleMenu();
    }

    public void toggleMenu() {
        for (int i=0; i<mMenuViews.size(); ++i){
            final View menu = mMenuViews.get(i);

            menu.setVisibility(VISIBLE);

            float startX = 0, startY = 0, endX = 0, endY = 0;
            startX = - (menu.getLeft() + menu.getWidth()/2 - (mMainView.getLeft() + mMainView.getWidth()/2));
            startY = - (menu.getTop() + menu.getHeight()/2 - (mMainView.getTop() + mMainView.getHeight()/2));
            if (mStatus == Status.OPEN){
                float temp;
                temp = startX;
                startX = endX;
                endX = temp;
                temp = startY;
                startY = endY;
                endY = temp;
            }

            Log.d("debug", String.format("startx %f endx %f starty %f endy %f", startX, endX, startY, endY));

            AnimationSet animationSet = new AnimationSet(true);
            TranslateAnimation translateAnimation = new TranslateAnimation(startX, endX, startY, endY);
            translateAnimation.setFillAfter(false);
            translateAnimation.setDuration(animDuration);

            RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setDuration(animDuration);

            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(translateAnimation);
            animationSet.setStartOffset(i*100/mMenuViews.size());
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mStatus == Status.ClOSE){
                        menu.setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            menu.startAnimation(animationSet);
        }

        changeStatus(false);

    }

    private void changeStatus(boolean check){
        mStatus = mStatus == Status.ClOSE? Status.OPEN : Status.ClOSE;
        if (!check) return;
        if (mStatus == Status.ClOSE){
            for (View menu: mMenuViews){
                menu.setVisibility(GONE);
            }
        }else{
            for (View menu: mMenuViews){
                menu.setVisibility(VISIBLE);
            }
        }
    }

    public boolean isOpened(){
        return mStatus == Status.OPEN;
    }

    private void clickMenuAnim(int clickIndex) {
        long duration = 200;
        for (int i=0; i<mMenuViews.size(); ++i) {
            final View menu = mMenuViews.get(i);
            float scaleTo = 0f, alphaTo = 0f;
            if (i == clickIndex){
                scaleTo = 2.0f;
            }
            ScaleAnimation scaleAnimation = new ScaleAnimation(1, scaleTo, 1, scaleTo, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(duration);
            scaleAnimation.setFillAfter(false);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(duration);
            alphaAnimation.setFillAfter(false);

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    menu.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mStatus == Status.ClOSE){
                        menu.setVisibility(GONE);
                    }
                    menu.setClickable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            menu.startAnimation(animationSet);
        }
        changeStatus(false);
    }

    private void rotateMainView() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(animDuration);
        rotateAnimation.setFillAfter(true);
        mMainView.startAnimation(rotateAnimation);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    public interface OnMenuItemClickListener{

        public void onMenuItemClick(View view);

    }

}
