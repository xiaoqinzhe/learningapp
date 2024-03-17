//
// Created by kimxiao on 2022/7/28.
//

#include<com_example_ndktool_NDKTool.h>
#include "logutil.h"
#include "FileScan.h"

#include <pthread.h>
#include<list>

void test(JNIEnv *env) {
    LOGD("test start");
    std::list<int> a;
    std::list<int>* ptr = nullptr;
    a.push_back(0);
    int* ai = nullptr;
//    *ai = 100;
    ptr->push_back(10);
    LOGD("val %d", abs(10/a.front()));
    LOGD("test end");
}

extern "C" JNIEXPORT jstring JNICALL Java_com_example_ndktool_NDKTool_getStringFromJni(JNIEnv *env, jclass cls)
{
    LOGD("Java_com_example_ndktool_NDKTool_getStringFromJni");
    test(env);
    return env->NewStringUTF("hello from c++");
}

extern "C" JNIEXPORT jobject JNICALL Java_com_example_ndktool_NDKTool_testObj(JNIEnv *env, jclass clazz, jint i, jboolean b, jstring str,
                                                                              jobject jobj)
{
    LOGD("Java_com_example_ndktool_NDKTool_testObj");
    // int   int32
    int ii = i;
    LOGD("%d", ii);
    // jboolean  uint_8
    LOGD("%d", b == JNI_TRUE? 1 : 0);

    // jstring
    const char *str_char = env->GetStringUTFChars(str, nullptr);
    std::string str_string(str_char);
    LOGD("%s 1 ", str_char);
    env->ReleaseStringUTFChars(str, str_char);
    const char *str_char2 = env->GetStringUTFChars(str, nullptr);
    LOGD("%s 2 ", str_char);
    env->ReleaseStringUTFChars(str, str_char2);

    jstring jstrnew = env->NewStringUTF("aaaa");
    env->DeleteLocalRef(jstrnew); // 可提早释放

    // jobject
    jclass ndkCls = env->GetObjectClass(jobj);
    ndkCls = env->FindClass("com/example/ndktool/NDKCls");

    jfieldID aFieldId = env->GetFieldID(ndkCls, "a", "I");
    env->SetIntField(jobj, aFieldId, 11);
    jmethodID printMethodId = env->GetMethodID(ndkCls, "print", "(I)V");
    int ppint = 22;
    env->CallVoidMethod(jobj, printMethodId, ppint);

    jmethodID initMethodId = env->GetMethodID(ndkCls, "<init>", "(I)V");
    jobject ndkObj = env->NewObject(ndkCls, initMethodId, 100);



    return ndkObj;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_ndktool_NDKTool_getDirSizeByNative(JNIEnv *env, jclass clazz, jstring path) {
    const char* pathchar = env->GetStringUTFChars(path, 0);
    std::string stringPath(pathchar);
    env->ReleaseStringUTFChars(path, pathchar);
//    return getDirSize(stringPath);
    return getDirSizeByDuCmd(stringPath);
}
