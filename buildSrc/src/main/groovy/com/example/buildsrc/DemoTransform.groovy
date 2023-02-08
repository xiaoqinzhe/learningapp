import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import com.android.utils.FileUtils

import java.util.concurrent.Callable

class DemoTransform extends Transform {

    private WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();
    boolean emptyRun = false;
    boolean enableParallel = false

    @Override
    String getName() {
        return getClass().getSimpleName()
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() { // 是否支持增量编译
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println("DemoTransform start transform...")

        incrementalTransform(transformInvocation)
    }


    void incrementalTransform(TransformInvocation transformInvocation) {
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        // 是否增量
        boolean isIncremental = transformInvocation.isIncremental();
        isIncremental = false

        System.out.println("incrementalTransform isIncremental = " + isIncremental);

        // 如果非增量，则清空旧的输出内容
        if(!isIncremental) {
            outputProvider.deleteAll();
        }

        for(TransformInput input : inputs) {
            for(JarInput jarInput : input.getJarInputs()) {
                Status status = jarInput.getStatus();
                File dest = outputProvider.getContentLocation(
                        jarInput.getName(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                System.out.println("jar jar=" + jarInput.getName() + ", dest=" + dest.getPath());
                if (isIncremental && !emptyRun) {
                    switch(status) {
                        case NOTCHANGED:
                            continue;
                        case ADDED:
                        case CHANGED:
                            transformJar(jarInput.getFile(), dest, status);
                            break;
                        case REMOVED:
                            if (dest.exists()) {
                                FileUtils.forceDelete(dest);
                            }
                            break;
                    }
                } else {
                    transformJar(jarInput.getFile(), dest, status);
                }
            }
            for(DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                System.out.println("dir dir=" + directoryInput.getName() + ", dest=" + dest.getPath());
                FileUtils.mkdirs(dest);
                if(isIncremental && !emptyRun) {
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
                                FileUtils.touch(destFile);
                                transformSingleFile(inputFile, destFile, srcDirPath);
                                break;
                        }
                    }
                } else {
                    transformDir(directoryInput.getFile(), dest);
                }
            }
        }
        parallel()
    }

    /**
     * custom transform
     */
    private void transformJar(File jarInput, File dest, Status status) {
        if (enableParallel) {
            runOnExecutor(new Callable() {
                @Override
                Object call() throws Exception {
                    FileUtils.copyFile(jarInput, dest)
                    return null
                }
            })
        } else {
            FileUtils.copyFile(jarInput, dest)
        }
    }

    /**
     * custom transform
     */
    private void transformSingleFile(File inputFile, File destFile, String srcDirPath) {
        if (enableParallel) {
            runOnExecutor(new Callable() {
                @Override
                Object call() throws Exception {
                    FileUtils.copyFile(inputFile, destFile)
                    return null
                }
            })
        } else {
            FileUtils.copyFile(inputFile, destFile)
        }
    }

    /**
     * custom transform
     */
    private void transformDir(File inputDir, File destDir) {
        if (enableParallel) {
            runOnExecutor(new Callable() {
                @Override
                Object call() throws Exception {
                    FileUtils.copyDirectory(inputDir, destDir)
                    return null
                }
            })
        } else {
            FileUtils.copyDirectory(inputDir, destDir)
        }
    }

    private void runOnExecutor(Callable callable) {
        waitableExecutor.execute(callable)
    }

    /**
     * 并发
     */
    private void parallel() {
        waitableExecutor.parallelism
        //异步并发处理jar/class
        waitableExecutor.execute(new Callable() {
            @Override
            Object call() throws Exception {
                System.out.println("parallel call 1")
                sleep(10)
//            bytecodeWeaver.weaveJar(srcJar, destJar);
                return null;
            }
        });
        waitableExecutor.execute(new Callable() {
            @Override
            Object call() throws Exception {
                System.out.println("parallel call 2")
                sleep(10)
//            bytecodeWeaver.weaveSingleClassToFile(file, outputFile, inputDirPath);
                return null;
            }
        });
        //等待所有任务结束
        waitableExecutor.waitForTasksWithQuickFail(true);
    }

}