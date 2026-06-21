# Étape 1 : Build de l'application
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
# Cette commande génère les fichiers dans /app/dist/front/browser
RUN npm run build --configuration=production

# Étape 2 : Serveur Nginx
FROM nginx:stable-alpine

# AJUSTEMENT ICI : Le chemin spécifique au nouveau builder Angular
COPY --from=build /app/dist/front/browser /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]


