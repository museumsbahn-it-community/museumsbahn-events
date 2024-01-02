import * as MuseumRailwayApi from 'museum-railway-api-client';

export default defineNuxtPlugin(nuxtApp => {
    return {
        provide: {
            MuseumRailwayApi
        }
    }
})