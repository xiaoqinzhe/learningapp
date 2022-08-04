//
// Created by kimxiao on 2022/7/28.
//

#include<com_example_ndktool_NDKTool.h>
#include "logutil.h"
#include "FileScan.h"

void test(JNIEnv *env) {

}

int getDirSize() {
    int size = getDirSize("cc");
}

extern "C" JNIEXPORT jstring JNICALL Java_com_example_ndktool_NDKTool_getStringFromJni(JNIEnv *env, jclass cls)
{
    LOGD("Java_com_example_ndktool_NDKTool_getStringFromJni");
    test(env);
    return env->NewStringUTF("hello from c++");
}
