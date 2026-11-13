# build
FROM node:20 AS builder
WORKDIR /app

# Build args for API URLs (can be overridden at build time)
ARG VITE_API_BASE=http://localhost:8080
ARG VITE_PYTHON_BASE=http://localhost:5000
ENV VITE_API_BASE=${VITE_API_BASE}
ENV VITE_PYTHON_BASE=${VITE_PYTHON_BASE}

COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# serve
FROM nginx:stable-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]