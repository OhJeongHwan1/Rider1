@echo off
"C:\\Users\\82107\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\82107\\AndroidStudioProjects\\Rider\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\82107\\AppData\\Local\\Android\\Sdk\\ndk-bundle" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\82107\\AppData\\Local\\Android\\Sdk\\ndk-bundle" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\82107\\AppData\\Local\\Android\\Sdk\\ndk-bundle\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\82107\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\82107\\AndroidStudioProjects\\Rider\\opencv\\build\\intermediates\\cxx\\Debug\\6w5q6k2i\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\82107\\AndroidStudioProjects\\Rider\\opencv\\build\\intermediates\\cxx\\Debug\\6w5q6k2i\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\82107\\AndroidStudioProjects\\Rider\\opencv\\.cxx\\Debug\\6w5q6k2i\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
