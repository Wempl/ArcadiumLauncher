@echo off
chcp 65001 > nul
echo Запуск сборки новой версии Pulse Framework...
echo Проверка на внесенные изменения в код...
echo Запущена очистка файлов...
if exist build rd /s /q build

echo Компиляция новой версии Pulse Framework...
kotlinc src\main\kotlin\com\redictor\pulse\Pulse.kt ^
         src\main\kotlin\com\redictor\pulse\core\PulseRequest.kt ^
         src\main\kotlin\com\redictor\pulse\core\PulseRouter.kt ^
         src\main\kotlin\com\redictor\pulse\dsl\PulseHtml.kt ^
         src\main\kotlin\com\redictor\pulse\dsl\PulseCSS.kt ^
         src\main\kotlin\com\redictor\pulse\server\PulseServer.kt ^
         -include-runtime -d build\libs\pulse.jar

if %errorlevel% equ 0 (
    echo Pulse Framework успешно скомпилирован!
    echo Файл: build\libs\pulse.jar
) else (
    echo Ошибка компиляции!
    pause
)