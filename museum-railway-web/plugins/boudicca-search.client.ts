import * as BoudiccaSearchApi from 'boudicca-search-api-client';

export default defineNuxtPlugin(nuxtApp => {
    return {
        provide: {
            BoudiccaSearchApi
        }
    }
})