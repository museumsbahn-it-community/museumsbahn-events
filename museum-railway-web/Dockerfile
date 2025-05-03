# Stage 1 - install dependencies
FROM node:22-alpine AS builder
ARG NPM_REPO_URL
ARG NPM_TOKEN
WORKDIR /app
COPY package.json .
#COPY package-lock.json .
RUN echo "" >> .npmrc  \
    && printf "@boudicca:registry=https://${NPM_REPO_URL}\n" >> .npmrc \
    && printf "@museumrailwayevents:registry=https://${NPM_REPO_URL}\n" >> .npmrc \
    && printf "//${NPM_REPO_URL}:_authToken=\${NPM_TOKEN}" >> .npmrc
RUN npm install --omit=dev

# npm install and build are separated so the result of install
# can be cached as long as package.json does not change
# Stage 2 - run build
COPY . .
RUN npm run build

# Stage 3 - production
FROM node:22-alpine AS final
WORKDIR /app
COPY --from=builder /app/.output ./.output
ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000
EXPOSE 3000
CMD ["node", ".output/server/index.mjs"]