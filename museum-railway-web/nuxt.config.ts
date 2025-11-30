// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    $development: {
        debug: true,
        devtools: {
            enabled: true,
            timeline: {
                enabled: true
            }
        },
        sourcemap: true,
        ssr: false,
    },
    runtimeConfig: {
        public: {
            matomoBase: 'http://localhost:8100' // can be overridden by NUXT_PUBLIC_API_BASE environment variable
        },
        backendApiProxyUrl: 'http://localhost:3050',
    },

    app: {
        head: {
            htmlAttrs: {
                lang: 'de'
            },
            meta: [
                {name: 'viewport', content: 'width=device-width, initial-scale=1'},
                {name: 'robots', content: 'index, follow'},
                {name: 'author', content: 'MUSEUMSBAHN-events.at'},
            ],
            link: [
                {
                    rel: 'icon',
                    type: 'image/x-icon',
                    href: '/favicon-light/favicon.ico',
                    media: "(prefers-color-scheme: light)"
                },
                {
                    rel: 'apple-touch-icon',
                    sizes: '180x180',
                    href: '/favicon-light/apple-touch-icon.png',
                    media: "(prefers-color-scheme: light)"
                },
                {
                    rel: 'icon',
                    type: 'image/png',
                    sizes: '32x32',
                    href: '/favicon-light/favicon-32x32.png',
                    media: "(prefers-color-scheme: light)"
                },
                {
                    rel: 'icon',
                    type: 'image/png',
                    sizes: '16x16',
                    href: '/favicon-light/favicon-16x16.png',
                    media: "(prefers-color-scheme: light)"
                },
                {
                    rel: 'icon',
                    type: 'image/x-icon',
                    href: '/favicon-dark/favicon.ico',
                    media: "(prefers-color-scheme: dark)"
                },
                {
                    rel: 'apple-touch-icon',
                    sizes: '180x180',
                    href: '/favicon-dark/apple-touch-icon.png',
                    media: "(prefers-color-scheme: dark)"
                },
                {
                    rel: 'icon',
                    type: 'image/png',
                    sizes: '32x32',
                    href: '/favicon-dark/favicon-32x32.png',
                    media: "(prefers-color-scheme: dark)"
                },
                {
                    rel: 'icon',
                    type: 'image/png',
                    sizes: '16x16',
                    href: '/favicon-dark/favicon-16x16.png',
                    media: "(prefers-color-scheme: dark)"
                },
                {rel: 'manifest', href: '/site.webmanifest'}
            ],
            titleTemplate: '%s %separator %appName',
            templateParams: {
                separator: '|', // choose a separator
                appName: 'MUSEUMSBAHN-events.at' // set a site name
            }
        }
    },

    css: [
        '~/assets/main.scss',
    ],

    modules: [
        '@nuxt/content',
        '@nuxtjs/device',
        '@nuxtjs/leaflet',
        'nuxt-viewport',
        '@primevue/nuxt-module',
        'nuxt-open-fetch',
        'nuxt-time',
        '@nuxtjs/seo',
        '@nuxt/eslint'
    ],

    site: {
        url: 'https://museumsbahn-events.at',
    },

    sitemap: {
        autoLastmod: true,
        xsl: false,
        strictNuxtContentPaths: true,
    },

    robots: {
        rules: {
            UserAgent: '*',
            Allow: '/',
            Disallow: ['/admin', '/_nuxt', '/api'],
            Sitemap: 'https://museumsbahn-events.at/sitemap.xml'
        }
    },

    content: {
        api: {
            baseURL: '/contentApi/_content',
        },
        markdown: {
            anchorLinks: false,
        },
    },

    nitro: {
        preset: 'node-server',
        devProxy: {
            '/api/search/': 'http://localhost:8082/',
            '/api/location': 'http://localhost:8080/api/location',
            '/api/location/*': 'http://localhost:8080/api/location/',
            '/imgcache': 'http://localhost:8080/imgcache',
        }
    },

    openFetch: {
        clients: {
            'boudiccaSearchApi': {
                schema: './openapi/boudiccaSearchApi/openapi.json'
            },
            'museumRailwayBackendApi': {
                schema: './openapi/museumRailwayBackendApi/openapi.yaml'
            }
        }
    },

    viewport: {
        breakpoints: {
            // configure breakpoints, so they match primeflex breakpoints
            'desktop-xxl': 1650, // matches nothing, but is biiiig
            'desktop-xl': 1200, // matches primeflex $xl
            desktop: 992, // matches primeflex $lg
            tablet: 768, // matches primeflex $md
            mobile: 320,
        },
    },
    primevue: {
        importTheme: {from: './assets/theme.ts'},
    },

    compatibilityDate: '2025-01-02'
});
