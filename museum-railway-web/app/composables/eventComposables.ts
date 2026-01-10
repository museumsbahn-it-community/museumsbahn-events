import {useAsyncData} from "#app";
import {useAllLocations} from "./locationComposables";
import {fetchEvents} from "./eventDataFunctions";

export const useAllEvents = () => {
    const {data: locationsData} = useAllLocations();

    const {data, error, refresh} = useAsyncData(
        'events',
        () => {
            return fetchEvents(locationsData.value ?? []);
        },
        {
            server: true,
            lazy: false,
            watch: [locationsData],
            default: () => []
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
