package com.example.buildsrc.layoutbinding

import com.android.build.gradle.BaseExtension
import javassist.ClassPath
import javassist.ClassPool
import javassist.NotFoundException

class LayoutBindingClassPool extends ClassPool {

    LayoutBindingClassPool(BaseExtension extension) {
        super(false)

        appendSystemPath();

        for (File it : extension.getBootClasspath()) {
            appendClassPath(it.getAbsolutePath());
        }
    }

    ClassPath appendClassPath(String pathname) {
        // System.out.println("appendClassPath pathname=" + pathname);
        try {
            return super.appendClassPath(pathname);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
