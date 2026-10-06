@echo off
setlocal enabledelayedexpansion
pushd "%~dp0"

set "ARGS=%*"
set ARGS=!ARGS:"=\"!

if "!ARGS!"=="" (
    call gradlew.bat run
) else (
    call gradlew.bat run --args="!ARGS!"
)
popd
pause