@echo off
chcp 65001 > nul
echo Компиляция Pulse Framework...

echo Очистка старых файлов...
if exist build rd /s /q build

echo Компиляция Kotlin...
kotlinc src\main\kotlin\com\redictor\pulse\Pulse.kt ^
         src\main\kotlin\com\redictor\pulse\core\PulseRequest.kt ^
         src\main\kotlin\com\redictor\pulse\core\PulseRouter.kt ^
         src\main\kotlin\com\redictor\pulse\dsl\PulseHtml.kt ^
         src\main\kotlin\com\redictor\pulse\server\PulseServer.kt ^
         -include-runtime -d build\libs\pulse.jar

if %errorlevel% equ 0 (
    echo Готово! JAR создан: build\libs\pulse.jar
) else (
    echo Ошибка компиляции!
)