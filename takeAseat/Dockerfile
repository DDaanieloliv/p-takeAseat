# Etapa de build: imagem com Node usada para compilar o projeto Angular.
FROM node:22-alpine AS builder

# Definimos alguns metadados para a documentação
LABEL authors="nielo"

# Definimos a pasta que os arquivos copiados seram manipulados.
WORKDIR /app

# Copia arquivos de configuração para aproveitar o cache de dependências.
COPY package.json package-lock.json ./
COPY angular.json tsconfig.json tsconfig.app.json tsconfig.spec.json ./

# Instala dependências de forma limpa e reproduzível.
RUN npm ci
#COPY . .
#RUN npm install

# Copia o código-fonte e os recursos públicos da aplicação.
COPY src/ src/
COPY public/ public/

# Compila a aplicação em modo de produção.
RUN npm run build -- --configuration=production


# Etapa final: imagem leve com Nginx para servir a aplicação.
FROM nginx:alpine

# Copia os arquivos compilados da aplicação para o diretório padrão do Nginx.
COPY --from=builder /app/dist/armchair-management-ui/browser usr/share/nginx/html


# Substitui a configuração padrão do Nginx pelos arquivos customizados.
COPY nginx.conf /etc/nginx/nginx.conf
COPY mime.types /etc/nginx/mime.types

# Definimos apenas a nível de documetação a porta em que essa imagem irá rodar.
EXPOSE 80

# Comando para rodar a Ultima Imagem do build mult-stage. (Nginx)
CMD ["nginx", "-g", "daemon off;"]
