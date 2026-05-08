@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "REPO=%USERPROFILE%\.m2\repository"

echo [+] Building FastThumb Native...
call compile.bat

echo [+] Compiling FastThumb Java...
call mvn compile -DskipTests

echo [+] Running Demo...
cd examples\Demo
call mvn compile -DskipTests

:: Build final CP
set "CP=target\classes"
set "CP=%CP%;..\..\target\classes"
set "CP=%CP%;%REPO%\com\github\andrestubbe\fastimage\0.1.0\fastimage-0.1.0.jar"
set "CP=%CP%;%REPO%\com\github\andrestubbe\fasttheme\0.2.0\fasttheme-0.2.0.jar"
set "CP=%CP%;%REPO%\com\github\andrestubbe\fastcore\0.1.0\fastcore-0.1.0.jar"

set "LIB_PATH=..\..\build;..\..\..\FastImage\build"

"%JAVA_HOME%\bin\java.exe" "-Djava.library.path=%LIB_PATH%" -cp "%CP%" fastthumb.Demo

cd ..\..
endlocal
