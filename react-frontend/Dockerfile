    # react-frontend/Dockerfile
    FROM node:18-alpine as build
    WORKDIR /app
    COPY package.json ./
    COPY yarn.lock ./ # Or package-lock.json
    RUN yarn install # Or npm install
    COPY . ./
    RUN yarn build # Or npm run build

    FROM nginx:stable-alpine
    COPY --from=build /app/build /usr/share/nginx/html
    EXPOSE 80
    CMD ["nginx", "-g", "daemon off;"]