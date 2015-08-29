LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := MainActivity
LOCAL_SRC_FILES := MainActivity.cpp
LOCAL_SHARED_LIBRARIES := libliquidfun_jni

include $(BUILD_SHARED_LIBRARY)

$(call import-add-path,$(LIQUIDFUN_SRC_PATH)/..)
$(call import-module,Box2D/swig/jni)
$(info "Hello Android")

