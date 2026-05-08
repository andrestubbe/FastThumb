@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Java\jdk-25"
call "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat"

if not exist "build" mkdir build

echo [+] Compiling fastthumb.cpp...
cl.exe /LD /EHsc /O2 /I"%JAVA_HOME%\include" /I"%JAVA_HOME%\include\win32" ^
    native\fastthumb.cpp ^
    /link /OUT:build\fastthumb.dll shell32.lib user32.lib gdi32.lib ole32.lib

if %ERRORLEVEL% neq 0 (
    echo [-] Compilation failed.
    exit /b 1
)

echo [+] Compilation successful: build\fastthumb.dll
copy build\fastthumb.dll .
endlocal
