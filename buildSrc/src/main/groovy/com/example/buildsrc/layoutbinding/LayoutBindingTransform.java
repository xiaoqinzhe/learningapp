package com.example.buildsrc.layoutbinding;

import static com.google.protobuf.CodedOutputStream.DEFAULT_BUFFER_SIZE;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
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
import java.util.Map;
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
        d("isIncremental = " + mIsIncremental);

        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        if (!mIsIncremental) {
            outputProvider.deleteAll();
        }

        Collection<TransformInput> inputs = transformInvocation.getInputs();

        for (TransformInput input : inputs) {
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                System.out.println("dir dir=" + directoryInput.getName() + ", dest=" + dest.getPath());
                FileUtils.mkdirs(dest);
                if (mIsIncremental) {
                    String srcDirPath = directoryInput.getFile().getAbsolutePath();
                    String destDirPath = dest.getAbsolutePath();
                    Map<File, Status> fileStatusMap = directoryInput.getChangedFiles();
                    for (Map.Entry<File, Status> changedFile : fileStatusMap.entrySet()) {
                        Status status = changedFile.getValue();
                        File inputFile = changedFile.getKey();
                        String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath);
                        File destFile = new File(destFilePath);
                        switch (status) {
                            case NOTCHANGED:
                                break;
                            case REMOVED:
                                if(destFile.exists()) {
                                    FileUtils.delete(destFile);
                                }
                                break;
                            case ADDED:
                            case CHANGED:
                                // FileUtils.touch(destFile);
                                transformSingleFile(inputFile, destFile, srcDirPath);
                                break;
                        }
                    }
                } else {
                    transformDir(directoryInput.getFile(), dest);
                }
            }
            for (JarInput jar : input.getJarInputs()) {
                Status status = jar.getStatus();
                File outFile = outputProvider.getContentLocation(jar.getName(), jar.getContentTypes(), jar.getScopes(), Format.JAR);
                // d("transformJar " + jar.getFile().getPath() + ", status=" + status + ", outFile=" + outFile.getPath());
                if (mIsIncremental) {
                    switch(status) {
                        case NOTCHANGED:
                            continue;
                        case ADDED:
                        case CHANGED:
                            transformJar(jar.getFile(), outFile, status);
                            break;
                        case REMOVED:
                            if (outFile.exists()) {
                                FileUtils.delete(outFile);
                            }
                            break;
                    }
                } else {
                    transformJar(jar.getFile(), outFile, status);
                }
            }
        }
    }

    private void transformDir(File inputDir, File outDir) throws IOException {
        // d("transformDir file = " + file.getPath());
        String srcDirPath = inputDir.getAbsolutePath();
        String destDirPath = outDir.getAbsolutePath();
        for (File file : FileUtils.getAllFiles(inputDir)) {
            String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath);
            File destFile = new File(destFilePath);
            transformSingleFile(file, destFile, srcDirPath);
        }
    }

    private void transformSingleFile(File inputFile, File destFile, String srcDirPath) throws IOException {
        destFile.getCanonicalFile().getParentFile().mkdirs();

        if (inputFile.getName().endsWith(".class")) {
            InputStream inputStream = new FileInputStream(inputFile);
            OutputStream outputStream = new FileOutputStream(destFile);
            convertClassFile(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } else {
            FileUtils.copyFile(inputFile, destFile);
        }
    }


    private void transformJar(File inputFile, File outFile, Status status) throws IOException {
        outFile.getCanonicalFile().getParentFile().mkdirs();
        ZipFile zipFile = new ZipFile(inputFile);
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
