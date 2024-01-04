import * as BoudiccaSearchApi from 'boudicca-search-api-client';

export default defineNuxtPlugin(() => {
    return {
        provide: {
            BoudiccaSearchApi
        }
    }
})