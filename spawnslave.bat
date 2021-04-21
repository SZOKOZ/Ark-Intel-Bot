@ECHO OFF
set /p license="Enter a license to spawn a slave config: "
java -jar ArkIntelBot.exe --master %license%