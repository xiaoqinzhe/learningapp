package com.example.learningapp.views.myviews;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewTreeObserver;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.ViewPager;


public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private float initScale;
    private float dcScale;
    private float minScale;
    private float maxScale;
    private float mAccScale;
    private boolean limitScale = false;
    private float zoomInScale = 2.0f;
    private float zoomOutScale = 1.0f;
    private float zoomInScaleThreshhold;

    private float lastX;
    private float lastY;
    private int lastPointCount = 0;
    private boolean isCanDrag;
    private boolean limitZoom = false;

    private boolean isCheckAndMakeCenterInZoomAndDrag = false;

    private float[] matrixValues = null;

    private RectF imageRectF;

    private Matrix mImageMatrix;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector doubleTabGestureDetector;

    private ScaleAnimRunnable scaleAnimRunnable;
    private MoveAnimRunnable moveAnimRunnable;

    private boolean mOnce;

    public ZoomImageView(@NonNull Context context) {
        this(context, null);
    }

    public ZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        mImageMatrix = new Matrix();
        mOnce = false;

        mAccScale = 1.0f;
        minScale = 1.0f;
        maxScale = 2.0f;
        zoomInScaleThreshhold = Math.min(1.5f, maxScale);

        scaleGestureDetector = new ScaleGestureDetector(context, this);
        doubleTabGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return onDoubleClick(e);
            }
        });

        scaleAnimRunnable = new ScaleAnimRunnable();
        moveAnimRunnable = new MoveAnimRunnable();

        setOnTouchListener(this);

    }

    private boolean onDoubleClick(MotionEvent e){
        if (scaleAnimRunnable != null)
            removeCallbacks(scaleAnimRunnable);
        if (mAccScale > zoomInScaleThreshhold){
            // zoom out
//            setImageScale(1f/mAccScale, e.getX(), e.getY(), true);
            // smooth
            scaleAnimRunnable.setState(zoomOutScale, e.getX(), e.getY(), 100, true);
            postDelayed(scaleAnimRunnable, 0);
        }else{
            // zoom in
//            setImageScale(Math.min(2.0f, maxScale), e.getX(), e.getY(), true);
            // smooth
            scaleAnimRunnable.setState(Math.min(zoomInScale, maxScale), e.getX(), e.getY(), 100, true);
            post(scaleAnimRunnable);
        }
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        Log.d("debug", "onGlobalLayout");

        if (mOnce) return;

        Drawable drawable = getDrawable();
        if (drawable == null)
            return;
        int iw = drawable.getIntrinsicWidth();
        int ih = drawable.getIntrinsicHeight();
        int w = getWidth(), h = getHeight();

        // 缩放
        float scale = Math.min(w * 1.0f / iw, h * 1.0f / ih);

        Log.d("debug", "scale "+scale);

        initScale = scale;

        // 平移
        int tx = (w - iw) / 2, ty = (h - ih) / 2;

        mImageMatrix.postScale(scale, scale, iw/2, ih/2);
        mImageMatrix.postTranslate(tx, ty);

        setImageMatrix(mImageMatrix);
        mOnce = true;

    }

    private float getCurrentScale(){
        // 一直是1.0!!!
        if (matrixValues == null)
            matrixValues = new float[9];
        getMatrix().getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    public void setImageScale(float scaleFactor, float focusX, float focusY, boolean check, boolean limitScale){
        float afterScale = mAccScale * scaleFactor;
        if (!limitScale){
            mAccScale = afterScale;
        }else{
            if (afterScale < minScale){
                scaleFactor = minScale / mAccScale;
                mAccScale = minScale;
            }else if (afterScale > maxScale){
                scaleFactor = maxScale / mAccScale;
                mAccScale = maxScale;
            }else{
                mAccScale = afterScale;
            }
        }
        if (scaleFactor != 1)
            mImageMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        // check border and center
        if (check)
            checkBorderAndMakeCenter();
        setImageMatrix(mImageMatrix);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
//        float currentScale = getCurrentScale();
        float scaleFactor = detector.getScaleFactor();
        Log.d("debug", "onScale " + scaleFactor);

        setImageScale(scaleFactor, detector.getFocusX(), detector.getFocusY(), isCheckAndMakeCenterInZoomAndDrag, limitScale);

        return true;
    }

    private RectF getImageRectF(){
        if (imageRectF == null)
            imageRectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            imageRectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mImageMatrix.mapRect(imageRectF);
        }
        return imageRectF;
    }

    private void checkBorderAndMakeCenter() {
        DeltaXY deltaXY = getCheckDeltaXY();
//        Log.d("debug", "deltax "+deltaX+" deltay "+deltaY);
        mImageMatrix.postTranslate(deltaXY.deltaX, deltaXY.deltaY);
    }

    public DeltaXY getCheckDeltaXY(){
        RectF rectF = getImageRectF();
        float deltaX = 0, deltaY = 0;
        int width = getWidth(), height = getHeight();
        // 去白边
        if (rectF.width() >= width){
            if (rectF.left > 0){
                deltaX -= rectF.left;
            }
            if (rectF.right < width)
                deltaX += (width - rectF.right);
        }
        if (rectF.height() >= height){
            if (rectF.top > 0)
                deltaY -= rectF.top;
            if (rectF.bottom < height){
                deltaY += (height - rectF.bottom);
            }
        }
        // 居中
        if (rectF.width() < width){
            deltaX += (width/2 - rectF.left - rectF.width()/2);
        }
        if (rectF.height() < height){
            deltaY += (height/2 - rectF.top - rectF.height()/2);
        }
        return new DeltaXY(deltaX, deltaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.d("debug", "onScaleBegin");
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.d("debug", "onScaleEnd");
//        float scale;
//        if (mAccScale > maxScale){
//            scale = maxScale / mAccScale;
//            mAccScale = maxScale;
//            mImageMatrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
//        }else if (mAccScale < minScale){
//            scale = minScale / mAccScale;
//            mAccScale = minScale;
//            mImageMatrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
//        }
//        checkBorderAndMakeCenter();
//        setImageMatrix(mImageMatrix);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String[] names = new String[10];
        names[MotionEvent.ACTION_DOWN] = "down";
        names[MotionEvent.ACTION_UP] = "up";
        names[MotionEvent.ACTION_MOVE] = "move";

        if (event.getAction() < 10)
            Log.e("error", "imageview event "+names[event.getAction()]+ " "+event.getAction());

        if (doubleTabGestureDetector.onTouchEvent(event))
            return true;

        scaleGestureDetector.onTouchEvent(event);

        onDragEvent(event);

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    private boolean isCanHandleTouchEvent(RectF rectF, float dx, float dy){
//        if (rectF.left > 0 || rectF.right < getWidth())
//            return false;
        if ( (rectF.width() > getWidth())
                || (rectF.height() > getHeight()) ){
            if ((rectF.left >= 0 && dx >= 0) || (rectF.right <= getWidth() && dx <= 0) )
                return false;
            return true;
        }
        return false;
    }

    private boolean onDragEvent(MotionEvent event){

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            removeAllAmimRunnable();
        }



        // 拖动事件检测
        int curPointCount = event.getPointerCount();
        Log.d("debug", "curPointerCount "+curPointCount);
        float accX = 0, accY = 0;
        for (int i=0; i<curPointCount; ++i){
            accX += event.getX(i);
            accY += event.getY(i);
        }
        accX /= curPointCount;
        accY /= curPointCount;

        // 记录上次位置：手指放下时, 松开/增加手指时需要重定位！
        if (curPointCount != lastPointCount){
            isCanDrag = false;
            lastX = accX;
            lastY = accY;
        }else{
            isCanDrag = true;
        }

        lastPointCount = curPointCount;
        RectF rectF = getImageRectF();
        ViewParent vp = getParent();
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                //
//                if (vp != null){
//                    if (isCanHandleTouchEvent(rectF, 0, 0)){
//                        Log.d("debug", "can handle down");
//                        vp.requestDisallowInterceptTouchEvent(true);
//                        return true;
//                    }
////                    else{
////                        vp.requestDisallowInterceptTouchEvent(false);
////                        return true;
////                    }
//                }
                return true;
//                break;
            case MotionEvent.ACTION_MOVE:
                if (isCanDrag){
                    float dx = accX - lastX;
                    float dy = accY - lastY;
                    if (isCanMoveAction(dx, dy)){
                        lastX = accX;
                        lastY = accY;

                        if (vp != null){
                            if (isCanHandleTouchEvent(rectF, dx, dy)){
                                vp.requestDisallowInterceptTouchEvent(true);
                            }
                            else{
                                // 传递move给viewpager 但是会跳动，why（）
                                Log.e("error", "intercept false");
//                                vp.requestDisallowInterceptTouchEvent(false);
                                lastPointCount = 0;
//                                dragImage(dx, dy);
                                return false;
                            }
                        }

                        if (rectF.left < 0 && rectF.left + dx > 0){
                            dx = -rectF.left;
                        }
                        if (rectF.right > getWidth() && rectF.right + dx < getWidth()){
                            dx = getWidth() - rectF.right;
                        }

                        dragImage(dx, dy);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.e("error", "up");
                lastPointCount = 0;
                long duration = 100;
                // Animation smooth back
                if (mAccScale > maxScale) {
//                    float scale = maxScale / mAccScale;
//                    mAccScale = maxScale;
//                    mImageMatrix.postScale(scale, scale, accX, accY);
                    Log.d("debug", "scale " + mAccScale + " to " + maxScale);
                    float fx = accX, fy = accY;
                    scaleAnimRunnable.setState(maxScale, accX, accY, duration, true);
                    postDelayed(scaleAnimRunnable, 0);
                } else if (mAccScale < minScale) {
//                    float scale = minScale / mAccScale;
//                    mAccScale = minScale;
//                    mImageMatrix.postScale(scale, scale, accX, accY);
                    Log.d("debug", "scale " + mAccScale + " to " + minScale);
                    scaleAnimRunnable.setState(minScale, -1, -1, duration, false);
                    postDelayed(scaleAnimRunnable, 0);
                }

                DeltaXY deltaXY = getCheckDeltaXY();
                if (deltaXY.deltaX != 0 || deltaXY.deltaY != 0){
                    rectF = getImageRectF();
                    float targetX = rectF.left + rectF.width()/2 + deltaXY.deltaX;
                    float targetY = rectF.top + rectF.height()/2 + deltaXY.deltaY;
                    moveAnimRunnable.setState(rectF, targetX, targetY, duration);
                    post(moveAnimRunnable);
//                checkBorderAndMakeCenter();
//                setImageMatrix(mImageMatrix);
//                Log.d("debug", "up "+event.getPointerCount());
                }
                break;
            default:
                break;
        }

        return false;
    }

    private void dragImage(float dx, float dy){
        mImageMatrix.postTranslate(dx, dy);

        if (isCheckAndMakeCenterInZoomAndDrag)
            checkBorderAndMakeCenter();

        setImageMatrix(mImageMatrix);
    }

    private boolean isCanMoveAction(float dx, float dy) {
        return true;
//        return Math.sqrt(dx*dx + dy*dy) > ViewConfiguration.get(getContext()).getScaledEdgeSlop();
    }

    private void removeAllAmimRunnable(){
        if (scaleAnimRunnable != null)
            removeCallbacks(scaleAnimRunnable);
        if (moveAnimRunnable != null)
            removeCallbacks(moveAnimRunnable);
    }

    private class DeltaXY{
        public float deltaX;
        public float deltaY;

        public DeltaXY(float deltaX, float deltaY) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }

    class ScaleAnimRunnable implements Runnable{

        private float curScale;
        private float targetScale;
        private float perScale;

        private boolean checkBorder;

        private float x;
        private float y;

        private float duration;
        private int times;
        private float sleepTime;

        private int framesPerSeconds = 60;

        public void setState( float targetScale, float x, float y, long duration, boolean checkBorder) {
            this.curScale = mAccScale;
            this.targetScale = targetScale;
            this.x = x;
            this.y = y;
            this.duration = duration;
            this.checkBorder = checkBorder;
            this.times = (int) (framesPerSeconds * duration/1000);
            this.sleepTime = duration / this.times;
            perScale = (float) Math.pow(this.targetScale/curScale, 1.0/times);
            Log.d("debug", "scale "+targetScale+" "+perScale);
        }

        @Override
        public void run() {
            float cx = x, cy = y;
            if (x < 0 || y < 0){
                RectF rectF = getImageRectF();
                cx = rectF.left + rectF.width()/2;
                cy = rectF.top + rectF.height()/2;
            }
            setImageScale(perScale, cx, cy, checkBorder, limitScale);
            Log.d("debug", "run scale "+mAccScale);
            if ((perScale > 1 && mAccScale < targetScale) || (perScale < 1 && mAccScale > targetScale)){
                postDelayed(this, (long) sleepTime);
            }else{
                float scale = targetScale/mAccScale;
                setImageScale(scale, cx, cy, checkBorder, limitScale);
            }
        }
    }

    class MoveAnimRunnable implements Runnable{

        private long duration;
        private long delayedTime;
        private float targetX;
        private float targetY;
        private float perX;
        private float perY;

        private RectF rectF;

        private int framesPerSeconds = 60;

        public void setState(RectF rectF, float targetX, float targetY, float duration){
            this.rectF = rectF;
            this.targetX = targetX;
            this.targetY = targetY;
            int times = (int) (framesPerSeconds * duration / 1000);
            delayedTime = (long) (duration / times);

            float dx = targetX - rectF.left - rectF.width()/2;
            float dy = targetY - rectF.top - rectF.height()/2;
            perX = dx / times;
            perY = dy / times;
        }

        @Override
        public void run() {
            RectF rectF = getImageRectF();
            float x = rectF.left + rectF.width()/2;
            float y = rectF.top + rectF.height()/2;

            Log.d("debug", "x "+x+" targetX "+targetX);

            if (((perX > 0 && x+perX < targetX) || (perX < 0 && x+perX > targetX)) &&
                    ((perY > 0 && y+perY < targetY) || (perY < 0 && y+perY > targetY))){
                mImageMatrix.postTranslate(perX, perY);
                setImageMatrix(mImageMatrix);
                postDelayed(this, delayedTime);
            }else{
                mImageMatrix.postTranslate(targetX-x, targetY-y);
                setImageMatrix(mImageMatrix);
            }
        }
    }

}
