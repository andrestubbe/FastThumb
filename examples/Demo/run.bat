@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "REPO=%USERPROFILE%\.m2\repository"

:: Build Classpath from local Maven repo
set "CP=target\classes"
set "CP=%CP%;%REPO%\com\github\andrestubbe\fastthumb\0.1.0\fastthumb-0.1.0.jar"
set "CP=%CP%;%REPO%\com\github\andrestubbe\fastimage\0.1.0\fastimage-0.1.0.jar"
set "CP=%CP%;%REPO%\com\github\andrestubbe\fasttheme\0.2.0\fasttheme-0.2.0.jar"
set "CP=%CP%;%REPO%\com\github\andrestubbe\fastcore\0.1.0\fastcore-0.1.0.jar"

:: Native Library Paths (FastThumb and FastImage)
set "LIB_PATH=..\..\build;..\..\..\FastImage\build"

echo [+] Starting FastThumb Antigravity Demo...
echo [+] Library Path: %LIB_PATH%

"%JAVA_HOME%\bin\java.exe" -Djava.library.path="%LIB_PATH%" -cp "%CP%" fastthumb.Demo

endlocal
