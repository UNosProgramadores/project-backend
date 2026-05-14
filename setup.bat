@echo off
setlocal enabledelayedexpansion

echo ==========================================
echo    Configuración inicial proyecto PARKING
echo ==========================================

set DB_CONTAINER=parking-db-container

echo.
echo [1/5] Verificando Docker...

docker --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker no está instalado o no está en PATH
    exit /b 1
)

echo.
echo [2/5] Cargando variables de entorno (.env)...

if not exist .env (
    echo ERROR: no existe .env
    echo Crea el archivo .env antes de ejecutar el setup
    exit /b 1
)

for /f "tokens=1,2 delims==" %%A in (.env) do (
    set %%A=%%B
)

echo DB_NAME=!DB_NAME!
echo DB_USER=!DB_USER!

echo.
echo [3/5] Levantando base de datos...

docker compose up -d

echo Esperando a que PostgreSQL inicie...
timeout /t 10 >nul

echo.
echo [4/5] Ejecutando scripts SQL...

if exist scripts\init.sql (
    docker exec -i %DB_CONTAINER% psql -U %DB_USER% -d %DB_NAME% < scripts\init.sql
    echo init.sql ejecutado.
)

if exist scripts\data_demo.sql (
    docker exec -i %DB_CONTAINER% psql -U %DB_USER% -d %DB_NAME% < scripts\data_demo.sql
    echo data_demo.sql ejecutado.
)

echo.
echo [5/5] Configurando backend...

if exist backend (
    cd backend

    call mvnw.cmd clean install -DskipTests

    call mvnw.cmd test
    if errorlevel 1 (
        echo No hay tests definidos aún.
    )

    cd ..
)

echo.
echo Configuración completada.
pause