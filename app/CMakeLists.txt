#cmake_minimum_required(VERSION 3.6)
#
#set(pathPROJECT C:/Users/2107/AndroidStudioProjects/Rider)
#set(pathOPENCV ${pathPROJECT}/sdk)
#set(pathLIBOPENCV_JAVA ${pathOPENCV}/native/libs/${ANDROID_ABI}/libopencv_java4.so)
#
#set(CMAKE_VERBOSE_MAKEFILE on)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
#
#include_directories(${pathOPENCV}/native/jni/include)
#
#add_library(
#        native-lib
#        SHARED
#        native-lib.cpp
#)
#
#add_library(lib_opencv SHARED IMPORTED)
#set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathLIBOPENCV_JAVA})
#
#
#find_library(
#        log-lib
#        log
#)
#
#target_link_libraries(
#        native-lib
#        lib_opencv
#        ${log-lib}
#)
#
