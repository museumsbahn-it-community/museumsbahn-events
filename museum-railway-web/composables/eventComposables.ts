import { useAsyncData } from "#app";
import { useAllLocations } from "./locationComposables";
import { fetchEvents } from "./eventDataFunctions";

export const useAllEvents = () => {
    const { data: locationsData } = useAllLocations();

    const { data, error, refresh } = useAsyncData(
        'events', 
        () => {
            if (locationsData.value) {
                return fetchEvents(locationsData.value);
            }
            return new Promise((resolve) => resolve([]));
        },
        {
            server: true,
            lazy: false,
            watch: [locationsData]
        }
    );

    if (error.value != null) {
        console.error(error.value);
    }

    return {
        data,
        refresh,
        error
    };
}
