# Étape 1 : Build de l'application Angular
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Étape 2 : Serveur NGINX
FROM nginx:alpine
# Attention : Angular >= 17 place les fichiers dans dist/AlerteWeb/browser
COPY --from=build /app/dist/AlerteWeb/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
