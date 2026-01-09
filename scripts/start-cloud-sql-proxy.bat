@echo off
REM Script para iniciar Cloud SQL Proxy no Windows
REM Uso: scripts\start-cloud-sql-proxy.bat

set PROJECT_ID=glossy-ally-476722-p5
set REGION=us-central1
set INSTANCE_NAME=feedalert-db
set CREDENTIALS_FILE=glossy-ally-476722-p5-46a7447b9399.json
set CLOUD_SQL_INSTANCE=%PROJECT_ID%:%REGION%:%INSTANCE_NAME%

echo 🔌 Iniciando Cloud SQL Proxy...
echo.

REM Verificar se credentials file existe
if not exist "%CREDENTIALS_FILE%" (
    echo ✗ Arquivo de credenciais não encontrado: %CREDENTIALS_FILE%
    echo 💡 Certifique-se de que o arquivo JSON da service account está na raiz do projeto.
    exit /b 1
)

REM Verificar se cloud-sql-proxy está instalado
where cloud-sql-proxy >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ⚠️  Cloud SQL Proxy não encontrado no PATH.
    echo 💡 Baixe o Cloud SQL Proxy de: https://github.com/GoogleCloudPlatform/cloud-sql-proxy/releases
    echo 💡 Ou use: cloud-sql-proxy.exe
    exit /b 1
)

echo 📋 Configurações:
echo   Instance: %CLOUD_SQL_INSTANCE%
echo   Credentials: %CREDENTIALS_FILE%
echo   Port: 5432 (padrão PostgreSQL)
echo.

echo 🚀 Iniciando proxy...
echo ⚠️  Mantenha este terminal aberto enquanto a aplicação estiver rodando.
echo ⚠️  Pressione Ctrl+C para parar o proxy.
echo.

REM Executar proxy
cloud-sql-proxy.exe --credentials-file="%CREDENTIALS_FILE%" %CLOUD_SQL_INSTANCE%
