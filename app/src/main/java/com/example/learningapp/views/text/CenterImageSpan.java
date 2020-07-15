package com.example.learningapp.views.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class CenterImageSpan extends ImageSpan {

    public static final int ALIGN_CENTER = 10;

    public static final int DEFAULT_MARGIN = 2;

    private WeakReference<Drawable> mDrawableRef;

    private int marginLeft;
    private int marginRight;

    public CenterImageSpan(@NonNull Drawable drawable) {
        this(drawable, ALIGN_CENTER);
    }

    public CenterImageSpan(@NonNull Drawable drawable, int verticalAlignment) {
        this(drawable, verticalAlignment, DEFAULT_MARGIN, DEFAULT_MARGIN);
    }

    public CenterImageSpan(@NonNull Drawable drawable, int marginLeft, int marginRight){
        this(drawable, ALIGN_CENTER, marginLeft, marginRight);
    }

    public CenterImageSpan(@NonNull Drawable drawable, int verticalAlignment, int marginLeft, int marginRight){
        super(drawable, verticalAlignment);
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();

        if (fm != null) {
            fm.ascent = -rect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return marginLeft + rect.right + marginRight;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable b = getCachedDrawable();
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        } else if (mVerticalAlignment == ALIGN_CENTER) {
            transY = (bottom - top) / 2 - b.getBounds().height() / 2 + top;
        }

        canvas.translate(marginLeft+x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null) {
            d = wr.get();
        }

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

}
