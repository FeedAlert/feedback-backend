#!/bin/bash
# Script para iniciar Cloud SQL Proxy
# Uso: ./scripts/start-cloud-sql-proxy.sh

set -e

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configurações
PROJECT_ID="glossy-ally-476722-p5"
REGION="us-central1"
INSTANCE_NAME="feedalert-db"
CREDENTIALS_FILE="./glossy-ally-476722-p5-46a7447b9399.json"
CLOUD_SQL_INSTANCE="${PROJECT_ID}:${REGION}:${INSTANCE_NAME}"

echo -e "${BLUE}🔌 Iniciando Cloud SQL Proxy...${NC}\n"

# Verificar se credentials file existe
if [ ! -f "$CREDENTIALS_FILE" ]; then
    echo -e "${RED}✗ Arquivo de credenciais não encontrado: ${CREDENTIALS_FILE}${NC}"
    echo -e "${YELLOW}💡 Certifique-se de que o arquivo JSON da service account está na raiz do projeto.${NC}"
    exit 1
fi

# Verificar se cloud-sql-proxy está instalado
if ! command -v cloud-sql-proxy &> /dev/null; then
    echo -e "${YELLOW}⚠️  Cloud SQL Proxy não encontrado no PATH.${NC}"
    echo -e "${YELLOW}💡 Tentando usar ./cloud-sql-proxy...${NC}"
    
    if [ ! -f "./cloud-sql-proxy" ]; then
        echo -e "${RED}✗ cloud-sql-proxy não encontrado.${NC}"
        echo -e "${YELLOW}💡 Baixe o Cloud SQL Proxy:${NC}"
        echo "  macOS (ARM64): curl -o cloud-sql-proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.8.0/cloud-sql-proxy.darwin.arm64"
        echo "  macOS (Intel): curl -o cloud-sql-proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.8.0/cloud-sql-proxy.darwin.amd64"
        echo "  Linux: curl -o cloud-sql-proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.8.0/cloud-sql-proxy.linux.amd64"
        echo "  Depois: chmod +x cloud-sql-proxy"
        exit 1
    fi
    
    PROXY_CMD="./cloud-sql-proxy"
else
    PROXY_CMD="cloud-sql-proxy"
fi

# Verificar se gcloud está autenticado
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
    echo -e "${YELLOW}⚠️  Não autenticado no gcloud. Fazendo login...${NC}"
    gcloud auth login
fi

echo -e "${BLUE}📋 Configurações:${NC}"
echo -e "  Instance: ${CLOUD_SQL_INSTANCE}"
echo -e "  Credentials: ${CREDENTIALS_FILE}"
echo -e "  Port: 5432 (padrão PostgreSQL)"
echo ""

echo -e "${YELLOW}🚀 Iniciando proxy...${NC}"
echo -e "${BLUE}⚠️  Mantenha este terminal aberto enquanto a aplicação estiver rodando.${NC}"
echo -e "${BLUE}⚠️  Pressione Ctrl+C para parar o proxy.${NC}\n"

# Executar proxy
${PROXY_CMD} \
  --credentials-file="${CREDENTIALS_FILE}" \
  "${CLOUD_SQL_INSTANCE}"
