package com.example.buildsrc

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class TransformRegPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("TransformPlugin apply")
        target.extensions.getByType(AppExtension).registerTransform(new DemoTransform())
    }
}