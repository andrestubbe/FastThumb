@echo off
setlocal

echo ==========================================
echo   FastJava Ecosystem - FastThumb Demo
echo ==========================================

echo [+] Building FastThumb...
call compile.bat
call mvn install -DskipTests

echo [+] Running Demo...
cd examples\Demo
call mvn compile exec:java -Djava.library.path=..\..\build

endlocal
pause
