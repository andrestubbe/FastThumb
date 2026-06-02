# Compiling FastThumb

This document provides instructions for building the native Windows Shell DLL and the Java component of the **FastThumb** module.

## Requirements

To build FastThumb from source, you need the following tools installed:

1.  **Windows 10/11 (x64)**
2.  **Visual Studio 2022** (with "Desktop development with C++" workload)
3.  **JDK 17+** (Ensure `JAVA_HOME` is set)
4.  **Maven 3.8+**

## 1. Native Build (C++/JNI)

The native component is located in the `native/` directory. It must be compiled into a 64-bit DLL (`fastthumb.dll`).

### Using the Build Script
The easiest way to compile the native library is to use the provided batch script from a **Developer Command Prompt for VS 2022**:

```batch
compile.bat
```

### Manual Compilation
If you prefer to run the commands manually, use the following steps:

1.  Open the **Developer Command Prompt for VS 2022 (x64)**.
2.  Navigate to the FastThumb root directory.
3.  Run the compiler:

```batch
cl.exe /LD /EHsc /O2 /I"%JAVA_HOME%\include" /I"%JAVA_HOME%\include\win32" ^
    native\FastThumb.cpp ^
    /link /OUT:build\fastthumb.dll shell32.lib user32.lib gdi32.lib ole32.lib
```

**Compiler Flags:**
- `/LD`: Create a Dynamic-Link Library (DLL).
- `/EHsc`: Enable C++ exceptions.
- `/O2`: Maximize speed (Optimization).
- `/I`: Include paths for JNI headers.

**Linked Libraries:**
- `shell32.lib`: For `SHCreateItemFromParsingName` and Shell APIs.
- `ole32.lib`: For COM initialization (`CoInitializeEx`).

---

## 2. Java Build (Maven)

Once the native library is compiled and placed in the root or `build/` directory, you can build the Java JAR:

```batch
mvn clean install -DskipTests
```

This will install the `fastthumb-0.1.0.jar` into your local Maven repository.

---

## 3. Running the Demo

To verify the build, run the root-level demo script:

```batch
run-demo.bat
```

The demo requires the native DLL to be present in the `build/` directory or the project root.

## Troubleshooting

- **"jni.h: No such file or directory"**: Ensure your `JAVA_HOME` environment variable points to a valid JDK installation and contains the `include` folder.
- **"LNK1112: module machine type 'x86' conflicts with target machine type 'x64'"**: You are likely using a 32-bit Command Prompt. Use the **x64 Native Tools Command Prompt**.
- **UnsatisfiedLinkError**: Ensure the `fastthumb.dll` is in the java library path or the project root during execution.

---
**Made with ⚡ by Andre Stubbe**
