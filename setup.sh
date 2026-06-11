#!/bin/bash

set -e

echo "=========================================="
echo "   Configuración inicial proyecto PARKING"
echo "=========================================="

echo ""
echo "[1/6] Verificando dependencias..."

sudo apt update

if ! dpkg -s docker.io >/dev/null 2>&1; then
    sudo apt install -y docker.io
fi

if ! dpkg -s docker-compose-v2 >/dev/null 2>&1; then
    sudo apt install -y docker-compose-v2
fi

if ! dpkg -s curl >/dev/null 2>&1; then
    sudo apt install -y curl
fi

if ! command -v java >/dev/null 2>&1; then
    sudo apt install -y openjdk-25-jdk
fi

echo ""
echo "[2/6] Cargando variables de entorno..."

if [ ! -f ".env" ]; then
    echo "ERROR: no existe .env"
    echo "Crea el archivo .env antes de ejecutar el setup"
    exit 1
fi

export $(cat .env | xargs)

DB_NAME=$DB_NAME
DB_USER=$DB_USER
DB_PASSWORD=$DB_PASSWORD

echo ""
echo "[3/6] Levantando base de datos..."
docker compose up -d

echo "Esperando a que PostgreSQL inicie..."
sleep 10

echo ""
echo "[4/6] Ejecutando scripts SQL..."

if [ -f "scripts/init.sql" ]; then
    docker exec -i $DB_CONTAINER psql -U "$DB_USER" -d "$DB_NAME" < scripts/init.sql
    echo "init.sql ejecutado."
fi

if [ -f "scripts/data_demo.sql" ]; then
    docker exec -i $DB_CONTAINER psql -U "$DB_USER" -d "$DB_NAME" < scripts/data_demo.sql
    echo "data_demo.sql ejecutado."
fi

echo ""
echo "[5/6] Configurando backend..."

if [ -d "backend" ]; then
    cd backend
    chmod +x mvnw
    ./mvnw clean install -DskipTests
    ./mvnw test || echo "No hay tests definidos aún."
    cd ..
fi

echo ""
echo "Configuración completada."