import type { RouterConfig } from '@nuxt/schema';


export default <RouterConfig>{
	scrollBehavior(to, from, savedPosition) {
		if (savedPosition) {
			return savedPosition;
		} else {
			return {top: 0};
		}
	},
	// https://router.vuejs.org/api/interfaces/routeroptions.html#routes
	routes: (_routes) => [
		..._routes,
		{
			name: 'eventDetails',
			path: '/events/:eventKey',
			component: () => {
				return import('~/pages/events.vue').then(r => r.default || r);
			},
		},
		{
			name: 'locationDetails',
			path: '/locations/:locationId',
			component: () => {
				return import('~/pages/locationDetails.vue').then(r => r.default || r);
			},
		},
		{
			name: 'locationMapDetails',
			path: '/locationMap/:locationId',
			component: () => {
				return import('~/pages/locationMap.vue').then(r => r.default || r);
			},
		},
		{
			name: 'locationMap',
			path: '/locationMap',
			component: () => {
				return import('~/pages/locationMap.vue').then(r => r.default || r);
			},
		},
	],
};