// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
	$development: {
		devtools: { enabled: true },
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
	},
	runtimeConfig: {
		public: {
			matomoBase: 'http://localhost:8100' // can be overridden by NUXT_PUBLIC_API_BASE environment variable
		}
	},
	css: [
		'~/assets/main.scss',
	],
	modules: [
		'@nuxt/content',
		'nuxt3-leaflet',
		'@pinia/nuxt',
		'@nuxtjs/device',
		'nuxt-viewport',
		'nuxt-primevue',
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
	viewport: {
		breakpoints: {
			// configure breakpoints, so they match primeflex breakpoints
			desktop: 992, // matches primeflex $lg
			tablet: 768, // matches primeflex $md
			mobile: 320,
		},
	}
});