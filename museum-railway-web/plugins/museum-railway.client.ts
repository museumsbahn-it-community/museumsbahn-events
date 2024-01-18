import * as MuseumRailwayApi from '@museumrailwayevents/museum-railway-client';

export default defineNuxtPlugin(nuxtApp => {
    return {
        provide: {
            MuseumRailwayApi
        }
    }
})