@echo off
pushd "%~dp0"
if "%~1"=="" (
    call gradlew.bat run
) else (
    call gradlew.bat run --args="%*"
)
popd
pause