@echo off
chcp 65001 > nul
echo ==============================================
echo ЗАПУСК ПРИЛОЖЕНИЯ В DOCKER
echo ==============================================

docker-compose up --build

echo.
echo Приложение остановлено.
pause
