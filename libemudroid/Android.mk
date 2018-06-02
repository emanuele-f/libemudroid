LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := libemu
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := -llog
LOCAL_SRC_FILES := \
	$(LOCAL_PATH)/src/main/jni/emulator.cpp \
	$(LOCAL_PATH)/src/main/jni/ticks.c \
	$(LOCAL_PATH)/src/main/jni/Android.mk \
	$(LOCAL_PATH)/src/main/jni/emumedia.cpp \
	$(LOCAL_PATH)/src/main/jni/main.cpp \
	$(LOCAL_PATH)/src/main/jni/libnativehelper/README \
	$(LOCAL_PATH)/src/main/jni/libnativehelper/Android.mk \
	$(LOCAL_PATH)/src/main/jni/libnativehelper/NOTICE \
	$(LOCAL_PATH)/src/main/jni/libnativehelper/JNIHelp.c \
	$(LOCAL_PATH)/src/main/jni/libnativehelper/MODULE_LICENSE_APACHE2 \

LOCAL_C_INCLUDES += $(LOCAL_PATH)/src/main/jni
LOCAL_C_INCLUDES += $(LOCAL_PATH)/src/main/jni/libnativehelper/include/
LOCAL_C_INCLUDES += $(LOCAL_PATH)/src/main/jni/libnativehelper/include/nativehelper
LOCAL_C_INCLUDES += $(LOCAL_PATH)/src/debug/jni

include $(BUILD_SHARED_LIBRARY)
