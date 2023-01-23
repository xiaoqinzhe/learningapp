package com.example.apt_api;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.example.libannotation.util.LayoutBindingUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class LayoutBinding {

    @VisibleForTesting
    static final SparseArray<Class<?>> BINDINGS = new SparseArray<>();

    // java.lang.NoSuchMethodError: No static method setContentView(Landroidx/appcompat/app/AppCompatActivity;I)V
    @UiThread
    public static void setContentView(AppCompatActivity activity, int layoutId) {
        setContentView((Activity) activity, layoutId);
    }

    @UiThread
    public static void setContentView(Activity activity, int layoutId) {
        Class<?> clz = findClz(activity, layoutId);
        if (clz == null) {
            return;
        }
        try {
            Method method = clz.getMethod("createView", Context.class);
            activity.setContentView((View) method.invoke(null, activity));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(clz.getDeclaringClass() + "'s inflate method is not implemented");
        }
    }

    @UiThread
    public static View inflate(Context context, int layoutId, ViewGroup parent) {
        return inflate(context, layoutId, parent, parent != null);
    }

    @UiThread
    public static View inflate(Context context, int layoutId, ViewGroup parent, boolean attach) {
        return inflate(LayoutInflater.from(context), layoutId, parent, attach);
    }

    @UiThread
    public static View inflate(LayoutInflater inflater, int layoutId, ViewGroup parent) {
        return inflate(inflater, layoutId, parent, parent != null);
    }

    @UiThread
    public static View inflate(LayoutInflater inflater, int layoutId, ViewGroup parent, boolean attach) {
        return getView(inflater, layoutId, parent, attach);
    }

    @UiThread
    public static View getView(LayoutInflater inflater, int layoutId, ViewGroup parent, boolean attach) {
        Context context = inflater.getContext();
        Class<?> clz = findClz(context, layoutId);
        if (clz == null) {
            return null;
        }
        try {
            Method method = clz.getMethod("inflateView", LayoutInflater.class, int.class, ViewGroup.class, boolean.class);
            return (View) method.invoke(null, inflater, layoutId, parent, attach);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(clz.getDeclaringClass() + "'s inflate method is not implemented");
        }
        return null;
    }

    private static Class<?> findClz(Context context, int layoutId) {
        Class<?> clz = BINDINGS.get(layoutId);
        if (clz != null || BINDINGS.indexOfKey(layoutId) >= 0) {
            return clz;
        }
        String layoutName = context.getResources().getResourceName(layoutId);
        layoutName = layoutName.substring(layoutName.lastIndexOf("/") + 1);
        try {
            String clzName = "com.example.generated.layout." + LayoutBindingUtil.getLayoutClassName(layoutName);
            clz = context.getClassLoader().loadClass(clzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        BINDINGS.put(layoutId, clz);
        return clz;
    }

}