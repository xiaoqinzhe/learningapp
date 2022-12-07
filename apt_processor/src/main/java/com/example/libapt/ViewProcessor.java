package com.example.libapt;

import com.example.libannotation.BindActivity;
import com.example.libannotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class ViewProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Types mTypeUtils;
    private Filer mFiler; // 文件生成
    private Messager mMessager; // 日志

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        // some utils
        mElementUtils = processingEnv.getElementUtils();
        mTypeUtils = processingEnv.getTypeUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
//        SourceVersion.latestSupported();
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(BindActivity.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty()) {
            return false;
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process: set = " + set);
        // generate file
        // 1. write file
        try {
            JavaFileObject javaFileObject = mFiler.createSourceFile("com.example.learningapp.GenerateJavaFile");
            Writer writer = javaFileObject.openWriter();
            writer.write("package com.example.learningapp; class GenerateJavaFile {}");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. javapoet
        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindActivity.class)) {
            TypeElement typeElement = (TypeElement) element;
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bindViews")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(typeElement.asType()), "activity");
            for (Element member : mElementUtils.getAllMembers(typeElement)) {
                BindView bindView = member.getAnnotation(BindView.class);
                if (bindView == null) {
                    continue;
                }
                methodBuilder.addStatement(
                        String.format("activity.%s = (%s) activity.findViewById(%d)",
                                member.getSimpleName(), ClassName.get(member.asType()), bindView.viewId())
                );
            }
            TypeSpec typeSpec = TypeSpec.classBuilder("BindView" + typeElement.getSimpleName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(
                    mElementUtils.getPackageOf(typeElement).getQualifiedName().toString(),
                    typeSpec
            ).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

}