// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    devtools: {enabled: true},
    css: [
        '~/assets/main.scss'
    ],
    modules: [
        '@nuxt/content',
        '@pinia/nuxt',
        'nuxt-primevue',
    ],
    vite: {
        server: {
            proxy: {
                '/searchApi': {
                    target: 'http://127.0.0.1:8082',
                    secure: false,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/searchApi/, ''),
                },
                '/api': {
                    target: 'http://127.0.0.1:8080',
                    secure: false,
                    changeOrigin: true,
                },
            },
        },
    },
});