//
// Created by kimxiao on 2022/7/28.
//
 #include<com_example_ndktool_NDKTool.h>

void test(JNIEnv *env) {
    env->NewBo
}

extern "C" JNIEXPORT jstring JNICALL Java_com_example_ndktool_NDKTool_getStringFromJni(JNIEnv *env, jclass cls)
{
    test(env);
    return env->NewStringUTF("hello from c++");
}
