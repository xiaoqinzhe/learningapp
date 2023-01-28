package com.example.buildsrc.layoutbinding;

import static com.google.protobuf.CodedOutputStream.DEFAULT_BUFFER_SIZE;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.tools.r8.v.b.Z;
import com.android.utils.FileUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import kotlin.io.ByteStreamsKt;

public class LayoutBindingTransform extends Transform {

    private LayoutBindingClassPool mClassPool;
    private final BaseExtension mExtension;
    private boolean mIsIncremental;
    private InflateConverter mInflateConverter;

    public LayoutBindingTransform(BaseExtension extension) {
        mExtension = extension;
    }

    private static final String TAG = "LayoutBindingTransform";

    @Override
    public String getName() {
        return "LayoutBindingTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        d("start transform...");

        mClassPool = new LayoutBindingClassPool(mExtension);

        appendClassPath(transformInvocation.getInputs());
        appendClassPath(transformInvocation.getReferencedInputs());

        mInflateConverter = new InflateConverter(mClassPool);

        mIsIncremental = transformInvocation.isIncremental();

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for (TransformInput input : inputs) {
            for (DirectoryInput dir : input.getDirectoryInputs()) {
                convertDir(dir, outputProvider);
            }
            for (JarInput jar : input.getJarInputs()) {
                convertJar(jar, outputProvider);
            }
        }
    }

    private void convertDir(DirectoryInput input, TransformOutputProvider outputProvider) throws IOException {
        File outDir = outputProvider.getContentLocation(input.getName(), input.getContentTypes(), input.getScopes(), Format.DIRECTORY);
        d("convertDir dir=" + input.getFile().getPath() + ", outDir=" + outDir.getPath());
        for (File file : FileUtils.getAllFiles(input.getFile())) {
//            d("convertDir file = " + file.getPath());
            File outputFile = getOutputFile(outputProvider, input, file);
            outputFile.getCanonicalFile().getParentFile().mkdirs();

            if (file.getName().endsWith(".class")) {
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = new FileOutputStream(outputFile);
                convertClassFile(inputStream, outputStream);
                inputStream.close();
                outputStream.close();
            } else {
                FileUtils.copyFile(file, outputFile);
            }
        }
    }

    private void convertJar(JarInput input, TransformOutputProvider outputProvider) throws IOException {
        File outFile = outputProvider.getContentLocation(input.getName(), input.getContentTypes(), input.getScopes(), Format.JAR);
        d("convertJar " + input.getFile().getPath() + ", outFile=" + outFile.getPath());

        outFile.getCanonicalFile().getParentFile().mkdirs();
        ZipFile zipFile = new ZipFile(input.getFile());
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outFile));
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            String entryName = zipEntry.getName();
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            zipOutputStream.putNextEntry(new ZipEntry(entryName));
            if (!zipEntry.isDirectory() && entryName.endsWith(".class")) {
                convertClassFile(inputStream, zipOutputStream);
            } else {
                ByteStreamsKt.copyTo(inputStream, zipOutputStream, DEFAULT_BUFFER_SIZE);
            }
            inputStream.close();
        }
        zipOutputStream.close();
    }

    private void convertClassFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        CtClass ctClass = mClassPool.makeClassIfNew(inputStream);

        if (handleBindLayout(ctClass, outputStream)) {
            return;
        }

        if (handleViewBinding(ctClass, outputStream)) {
            return;
        }

        try {
            ctClass.toBytecode(new DataOutputStream(outputStream));
        } catch (CannotCompileException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean handleBindLayout(CtClass ctClass, OutputStream outputStream) {
        if (ctClass.hasAnnotation("com.example.libannotation.BindLayouts")) {
            d("handleBindLayout match BindLayouts, file=" + ctClass.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            try {
                ctClass.instrument(mInflateConverter);
                ctClass.toBytecode(new DataOutputStream(outputStream));
                ctClass.detach();
            } catch (CannotCompileException | IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            return true;
        }
        return false;
    }

    private boolean handleViewBinding(CtClass ctClass, OutputStream outputStream) {
        if (ViewBindingConverter.needChangedViewBinding(ctClass, mClassPool)) {
            d("handleViewBinding match ViewBinding, file=" + ctClass.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            try {
                ViewBindingConverter.convert(ctClass, mClassPool);
                ctClass.toBytecode(new DataOutputStream(outputStream));
                ctClass.detach();
            } catch (CannotCompileException | IOException | NotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
            return true;
        }
        return false;
    }

    private File getOutputFile(TransformOutputProvider transformOutputProvider, QualifiedContent input, File file) {
        Format format = input instanceof JarInput ? Format.JAR : Format.DIRECTORY;
        File locationFile = transformOutputProvider.getContentLocation(input.getName(), input.getContentTypes(), input.getScopes(), format);
        String path = input.getFile().toURI().relativize(file.toURI()).getPath();
        return new File(locationFile, path);
    }

    private void appendClassPath(Collection<TransformInput> inputs) {
        for (TransformInput transformInput : inputs) {
            for (DirectoryInput dirInput : transformInput.getDirectoryInputs()) {
                mClassPool.appendClassPath(dirInput.getFile().getAbsolutePath());
            }

            for (JarInput jarInput : transformInput.getJarInputs()) {
                mClassPool.appendClassPath(jarInput.getFile().getAbsolutePath());
            }
        }
    }

    private void d(String msg) {
        System.out.println("[LayoutBindingTransform]: " + msg);
    }
}
