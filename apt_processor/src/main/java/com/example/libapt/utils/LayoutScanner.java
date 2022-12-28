package com.example.libapt.utils;

import java.io.File;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class LayoutScanner {

    public static String findLayout(String rootPath, String fileName, Messager messager) {
        // fast locate
        File layoutsPath = new File(rootPath + "/src/main/res/layout");
        // messager.printMessage(Diagnostic.Kind.NOTE, layoutsPath.getAbsolutePath());
        if (layoutsPath.exists()) {
            String[] filenames = layoutsPath.list();
            if (filenames == null) return null;
            for (String filename : filenames) {
                // messager.printMessage(Diagnostic.Kind.NOTE, filename);
                if (filename.substring(0, filename.lastIndexOf('.')).equals(fileName)) {
                    return layoutsPath.getAbsolutePath() + "/" + filename;
                }
            }
        }
        return null;
    }

}
