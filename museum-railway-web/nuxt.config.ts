// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
	$development: {
		devtools: {
			enabled: true,
			timeline: {
				enabled: true
			}
		},
		sourcemap: true,
	},
	runtimeConfig: {
		public: {
			matomoBase: 'http://localhost:8100' // can be overridden by NUXT_PUBLIC_API_BASE environment variable
		},
		backendApiProxyUrl: 'http://127.0.0.1:8080',
		boudiccaSearchApiProxyUrl: 'http://127.0.0.1:8082'
	},

	css: [
		'~/assets/main.scss',
	],

	modules: [
		'@nuxt/content',
		'@pinia/nuxt',
		'@nuxtjs/device',
		'@nuxtjs/i18n',
		'@nuxtjs/leaflet',
		'nuxt-viewport',
		'nuxt-primevue',
		'nuxt-open-fetch',
	],

	content: {
		api: {
			baseURL: '/contentApi/_content',
		},
		markdown: {
			anchorLinks: false,
		},
	},

	nitro: {
		preset: 'node-server'
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

	compatibilityDate: '2024-12-30'
});