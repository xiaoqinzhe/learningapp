package com.example.learningapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class ScreenUtil {
    public static int EDGE_WIDTH = 0;
    public static int EDGE_HEIGHT = 1;
    private static float density = 0;
    private static float scaledDensity = 0;

    public static void setMyCompatDensity(@NonNull final Activity activity, @NotNull final Application application, final int designEdgeDpi, int baseEdge){
        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (density == 0){
            density = appDisplayMetrics.density;
            scaledDensity = appDisplayMetrics.scaledDensity;

            activity.unregisterComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(@NonNull Configuration newConfig) {
//                    final int ded = designEdgeDpi;
                    if (newConfig.fontScale > 0){
                        scaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
//                        setMyCompatDensity(activity, application. ded, );
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        float edgePixel = (baseEdge == EDGE_WIDTH? appDisplayMetrics.widthPixels : appDisplayMetrics.heightPixels);
        float targetDensity = edgePixel / designEdgeDpi;
        float targetDpi = 160 * targetDensity;
        float targetScaledDensity = scaledDensity * targetDensity / density;

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.densityDpi = (int) targetDpi;
        appDisplayMetrics.scaledDensity = targetScaledDensity;

        final DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        displayMetrics.density = targetDensity;
        displayMetrics.densityDpi = (int) targetDpi;
        displayMetrics.scaledDensity = targetScaledDensity;
    }

    public static void setFullScreen(Activity activity){
//        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    public static int getStatusBarHeight(Activity activity){
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resId);
    }

    public static int getWindowHeight(Activity activity){
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.y;
    }

    public static int getWindowWidth(Activity activity){
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.x;
    }

    public static int pixelToDip(Context context, int pixel){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(pixel / density + 0.5f);
    }

    public static int dipToPixel(Context context, int dip){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dip * density + 0.5f);
    }

}
