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
	target: 'static',
	css: [
		'~/assets/main.scss',
	],
	modules: [
		'@nuxt/content',
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
	}
});