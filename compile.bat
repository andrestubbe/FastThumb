@echo off
setlocal

set "JAVA_HOME=%JAVA_HOME%"
if "%JAVA_HOME%"=="" (
    echo Error: JAVA_HOME is not set.
    exit /b 1
)

if not exist "build" mkdir build

echo [+] Compiling FastThumb.cpp...
cl.exe /LD /EHsc /O2 /I"%JAVA_HOME%\include" /I"%JAVA_HOME%\include\win32" ^
    native\FastThumb.cpp ^
    /link /OUT:build\fastthumb.dll shell32.lib user32.lib gdi32.lib ole32.lib

if %ERRORLEVEL% neq 0 (
    echo [-] Compilation failed.
    exit /b 1
)

echo [+] Compilation successful: build\fastthumb.dll
copy build\fastthumb.dll .
endlocal
