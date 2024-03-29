import { defineNuxtPlugin } from '#app'
import VueMatomo from 'vue-matomo'

// we have to use vue-matomo as nuxt-matomo does not yet support nuxt v3
// https://www.npmjs.com/package/vue-matomo

export default defineNuxtPlugin((nuxtApp) => {
	const runtimeConfig = useRuntimeConfig()
	nuxtApp.vueApp.use(VueMatomo, {
		host: runtimeConfig.public.matomoBase,
		siteId: 1,
		// Enables automatically registering pageviews on the router
		router: nuxtApp.$router,
		enableLinkTracking: true,
		requireConsent: false,
		trackInitialView: true,
		disableCookies: true,
		requireCookieConsent: false,
	});
})