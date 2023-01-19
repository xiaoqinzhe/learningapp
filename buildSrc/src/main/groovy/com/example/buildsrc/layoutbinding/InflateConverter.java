package com.example.buildsrc.layoutbinding;

import org.jetbrains.annotations.NotNull;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.convert.TransformCall;

public class InflateConverter extends CodeConverter {

    public InflateConverter(ClassPool classPool) {
        redirectMethods(classPool);
    }

    private void redirectMethods(ClassPool classPool) {
        try {
            redirectActivityMethod(classPool);
            redirectLayoutInflaterMethod(classPool);
            redirectViewMethod(classPool);
        } catch (Exception e) {
            System.out.println("InflateConverter redirectMethods error. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectActivityMethod(ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass ctActivity = classPool.get("android.app.Activity");
        CtClass ctLB = classPool.get("com.example.apt_api.LayoutBinding");
        CtClass ctInt = classPool.get("int");

        CtMethod origMethod = ctActivity.getDeclaredMethod("setContentView", new CtClass[]{ctInt});
        CtMethod substMethod = ctLB.getDeclaredMethod("setContentView", new CtClass[]{ctActivity, ctInt});

        redirectMethodInvokeToStatic(origMethod, substMethod);

        CtClass ctAppCompatActivity = classPool.get("androidx.appcompat.app.AppCompatActivity");
        origMethod = ctAppCompatActivity.getDeclaredMethod("setContentView", new CtClass[]{ctInt});
        redirectMethodInvokeToStatic(origMethod, substMethod);
    }

    private void redirectLayoutInflaterMethod(ClassPool classPool) throws NotFoundException {
        CtClass ctInt = classPool.get("int");
        CtClass ctBoolean = classPool.get("boolean");

        CtClass ctViewGroup = classPool.get("android.view.ViewGroup");
        CtClass ctLayoutInflater = classPool.get("android.view.LayoutInflater");
        CtClass ctX2J = classPool.get("com.example.apt_api.LayoutBinding");

        CtMethod origInflate2 = ctLayoutInflater.getDeclaredMethod("inflate", new CtClass[]{ctInt, ctViewGroup});
        CtMethod substInflate3 = ctX2J.getDeclaredMethod("inflate", new CtClass[]{ctLayoutInflater, ctInt, ctViewGroup});
        redirectMethodInvokeToStatic(origInflate2, substInflate3);

        CtMethod origInflate3 = ctLayoutInflater.getDeclaredMethod("inflate", new CtClass[]{ctInt, ctViewGroup, ctBoolean});
        CtMethod substInflate4 = ctX2J.getDeclaredMethod("inflate", new CtClass[]{ctLayoutInflater, ctInt, ctViewGroup, ctBoolean});
        redirectMethodInvokeToStatic(origInflate3, substInflate4);
    }

    private void redirectViewMethod(ClassPool classPool) throws NotFoundException {
        CtClass ctContext = classPool.get("android.content.Context");
        CtClass ctView = classPool.get("android.view.View");
        CtClass ctViewGroup = classPool.get("android.view.ViewGroup");
        CtClass ctX2J = classPool.get("com.example.apt_api.LayoutBinding");
        CtClass ctInt = classPool.get("int");

        CtMethod origMethod = ctView.getDeclaredMethod("inflate", new CtClass[]{ctContext, ctInt, ctViewGroup});
        CtMethod substMethod = ctX2J.getDeclaredMethod("inflate", new CtClass[]{ctContext, ctInt, ctViewGroup});
        redirectMethodInvokeToStatic(origMethod, substMethod);
    }

    private void redirectMethodInvokeToStatic(@NotNull CtMethod origMethod, @NotNull CtMethod substMethod) {
        if (Modifier.isStatic(origMethod.getModifiers()) ) {
            this.transformers = new TransformCall(this.transformers, origMethod, substMethod);
        } else {
            this.transformers = new StaticTransformCall(this.transformers, origMethod, substMethod);
        }
    }

}
