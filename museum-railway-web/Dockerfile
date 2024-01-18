# Stage 1 - build
FROM node:20-alpine AS builder
ARG NPM_REPO_URL
ARG NPM_TOKEN
WORKDIR /app
COPY . .
RUN echo "" >> .npmrc  \
    && printf "@boudicca:registry=https://${NPM_REPO_URL}\n" >> .npmrc \
    && printf "@museumrailwayevents:registry=https://${NPM_REPO_URL}\n" >> .npmrc \
    && printf "//${NPM_REPO_URL}:_authToken=\${NPM_TOKEN}" >> .npmrc
RUN npm install && npm run build

# Stage 2 - production
FROM node:20-alpine AS final
WORKDIR /app
COPY --from=builder /app/.output ./.output
ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000
EXPOSE 3000
CMD ["node", ".output/server/index.mjs"]