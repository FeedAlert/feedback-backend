#!/bin/bash
# Script de deploy automatizado para Google Cloud Run
# Uso: ./scripts/deploy.sh

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
SERVICE_NAME="feed-alert"
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"
CLOUD_SQL_INSTANCE="${PROJECT_ID}:${REGION}:feedalert-db"

echo -e "${BLUE}🚀 Iniciando deploy do ${SERVICE_NAME} no Google Cloud Run...${NC}\n"

# Verificar se gcloud está instalado
if ! command -v gcloud &> /dev/null; then
    echo -e "${RED}✗ Google Cloud SDK não encontrado.${NC}"
    echo "Instale em: https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Verificar autenticação
echo -e "${YELLOW}📋 Verificando autenticação...${NC}"
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
    echo -e "${YELLOW}⚠️  Não autenticado. Fazendo login...${NC}"
    gcloud auth login
fi

# Configurar projeto
echo -e "${YELLOW}📋 Configurando projeto...${NC}"
gcloud config set project ${PROJECT_ID}

# Verificar se Docker está rodando
if ! docker info &> /dev/null; then
    echo -e "${RED}✗ Docker não está rodando.${NC}"
    exit 1
fi

# Build da imagem (forçar plataforma linux/amd64 para Cloud Run)
echo -e "${YELLOW}🔨 Fazendo build da imagem Docker...${NC}"
if ! docker build --platform linux/amd64 -t ${IMAGE_NAME}:latest .; then
    echo -e "${RED}✗ Falha no build da imagem Docker${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Build concluído${NC}"

# Autenticar Docker no GCP
echo -e "${YELLOW}🔐 Autenticando Docker no Google Cloud...${NC}"
gcloud auth configure-docker --quiet

# Push da imagem
echo -e "${YELLOW}📤 Enviando imagem para Container Registry...${NC}"
if ! docker push ${IMAGE_NAME}:latest; then
    echo -e "${RED}✗ Falha no push da imagem${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Imagem enviada com sucesso${NC}"

# Verificar se secret existe
echo -e "${YELLOW}🔍 Verificando secret do banco de dados...${NC}"
if ! gcloud secrets describe db-password &> /dev/null; then
    echo -e "${RED}✗ Secret 'db-password' não encontrado.${NC}"
    echo -e "${YELLOW}💡 Criando secret...${NC}"
    read -sp "Digite a senha do banco de dados: " DB_PASSWORD
    echo
    echo -n "${DB_PASSWORD}" | gcloud secrets create db-password \
        --data-file=- \
        --replication-policy="automatic"
    echo -e "${GREEN}✓ Secret criado${NC}"
fi

# Deploy no Cloud Run
echo -e "${YELLOW}🚀 Fazendo deploy no Cloud Run...${NC}"

# Verificar se service account customizada existe, caso contrário usa a padrão
SERVICE_ACCOUNT_OPTION=""
if gcloud iam service-accounts describe feed-alert-sa@${PROJECT_ID}.iam.gserviceaccount.com &> /dev/null; then
    echo -e "${BLUE}📋 Usando service account customizada: feed-alert-sa${NC}"
    SERVICE_ACCOUNT_OPTION="--service-account=feed-alert-sa@${PROJECT_ID}.iam.gserviceaccount.com"
fi

gcloud run deploy ${SERVICE_NAME} \
  --image ${IMAGE_NAME}:latest \
  --region ${REGION} \
  --platform managed \
  --allow-unauthenticated \
  --add-cloudsql-instances ${CLOUD_SQL_INSTANCE} \
  --set-env-vars "SPRING_DATASOURCE_URL=jdbc:postgresql:///feedalert_db?cloudSqlInstance=${CLOUD_SQL_INSTANCE}&socketFactory=com.google.cloud.sql.postgres.SocketFactory" \
  --set-env-vars "SPRING_DATASOURCE_USERNAME=feedback_user" \
  --set-env-vars "GCLOUD_PROJECT=${PROJECT_ID}" \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars "APP_NOTIFICATION_MODE=pubsub" \
  --set-env-vars "SPRING_FLYWAY_ENABLED=false" \
  --set-env-vars "JWT_SECRET=$(openssl rand -base64 32)" \
  --set-secrets "SPRING_DATASOURCE_PASSWORD=db-password:latest" \
  ${SERVICE_ACCOUNT_OPTION} \
  --memory 512Mi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 10 \
  --timeout 300 \
  --quiet

# Obter URL do serviço
SERVICE_URL=$(gcloud run services describe ${SERVICE_NAME} \
  --region ${REGION} \
  --format="value(status.url)")

echo -e "\n${GREEN}✅ Deploy concluído com sucesso!${NC}\n"
echo -e "${BLUE}📍 URL do serviço: ${SERVICE_URL}${NC}\n"

# Testar health check
echo -e "${YELLOW}🏥 Testando health check...${NC}"
echo -e "${BLUE}Aguardando serviço iniciar...${NC}"
sleep 10

RETRY_COUNT=0
MAX_RETRIES=6
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s -f "${SERVICE_URL}/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Health check passou${NC}"
        break
    else
        RETRY_COUNT=$((RETRY_COUNT + 1))
        if [ $RETRY_COUNT -lt $MAX_RETRIES ]; then
            echo -e "${YELLOW}⚠️  Tentativa ${RETRY_COUNT}/${MAX_RETRIES} falhou. Aguardando...${NC}"
            sleep 10
else
            echo -e "${YELLOW}⚠️  Health check falhou após ${MAX_RETRIES} tentativas.${NC}"
            echo -e "${YELLOW}Verifique os logs:${NC}"
            echo "  gcloud run services logs read ${SERVICE_NAME} --region ${REGION} --limit 50"
fi
    fi
done

echo -e "\n${BLUE}📝 Comandos úteis:${NC}"
echo "  Ver logs: gcloud run services logs read ${SERVICE_NAME} --region ${REGION} --follow"
echo "  Ver status: gcloud run services describe ${SERVICE_NAME} --region ${REGION}"
echo "  Abrir no navegador: ${SERVICE_URL}"



