import { fetchLocations } from "./locationDataFunctions"

export const useAllLocations = () => {
    const { data, error, refresh } = useAsyncData('locations', () => fetchLocations(), {
        server: true,
        lazy: false,
    });

    if (error.value != null) {
        console.error(error.value);
    }

    return {
        data,
        refresh,
        error
    };
}


