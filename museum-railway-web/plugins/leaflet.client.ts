import L from 'leaflet'

export default defineNuxtPlugin(nuxtApp => {
    return {
        provide: {
            L
        }
    }
})