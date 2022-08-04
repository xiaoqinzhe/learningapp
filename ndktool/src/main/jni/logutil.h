//
// Created by 1 on 2022/8/4.
//

#ifndef LEARNINGAPP_LOGUTIL_H
#define LEARNINGAPP_LOGUTIL_H

#include<android/log.h>

#define LOG_TAG "native"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#endif //LEARNINGAPP_LOGUTIL_H
