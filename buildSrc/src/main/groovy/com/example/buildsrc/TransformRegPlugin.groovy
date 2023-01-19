package com.example.buildsrc

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.example.buildsrc.layoutbinding.LayoutBindingTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class TransformRegPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("TransformPlugin apply")
//        target.extensions.getByType(AppExtension).registerTransform(new DemoTransform())
        addLayoutBindingTransform(target)
    }

    void addLayoutBindingTransform(Project project) {
        AppExtension androidApp = project.getExtensions().findByType(AppExtension.class);
        LibraryExtension androidLib = project.getExtensions().findByType(LibraryExtension.class);
        TestedExtension extension = androidApp != null ? androidApp : androidLib;
        if (extension == null) {
            System.out.println("TransformRegPlugin project not a android module.");
            return;
        }

        extension.registerTransform(new LayoutBindingTransform(extension));
    }

}