LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ndktool
LOCAL_SRC_FILES := NDKTool.cpp FileScan.cpp util/CmdUtil.cpp

LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)