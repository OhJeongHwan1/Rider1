cmake_minimum_required(VERSION 3.6)

# dummy target to bring libc++_shared.so into packages
add_library(opencv_jni_shared STATIC dummy.cpp)

## add_library (native-lib SHARED native-lib.cpp )
#
#add_library(native-lib SHARED
#        src/main/cpp/native-lib.cpp)
#
#target_link_libraries(native-lib
#        android
#        log
#        -ljnigraphics
#        ${OpenCV_LIBS})
#
#set(OpenCV_DIR C:/OpenCV-android-sdk/sdk/native/jni)
#find_package(OpenCV REQUIRED)
#include_directories(${OpenCV_INCLUDE_DIRS})
#
