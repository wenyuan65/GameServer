@echo off

del ..\\src\\main\\java\\com\\panda\\game\\proto\\*.* /q

for /f "delims=" %%i in ('dir /b ..\src\main\resources\*.proto') do (
	protoc --proto_path ../src/main/resources/ --java_out ../src/main/java/ %%i
)

echo finish