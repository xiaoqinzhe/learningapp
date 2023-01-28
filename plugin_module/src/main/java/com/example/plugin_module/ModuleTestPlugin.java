package com.example.plugin_module;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ModuleTestPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        System.out.println("ModuleTestPlugin apply");
    }

}