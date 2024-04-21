import {useLocationsStore} from "~/stores/LocationsStore"
import type {ComputedRef} from "vue";


interface LocationsData {
    allLocations(): ComputedRef<MuseumLocation[]>;

    stateList(): ComputedRef<string[]>;

    locationById(locationId: string): ComputedRef<MuseumLocation | undefined>;

    eventCountForId(locationId: string): ComputedRef<number>;

    loationIdsForStates(states: string[]): string[];

    loadLocations(): Promise<void>;

    loadLocationEventsCount(): Promise<void>;
}


export const useLocationsData = (): LocationsData => {
    const store = useLocationsStore()

    return {
        allLocations: () => computed(() => store.allLocations),

        stateList: () => computed(() => store.stateList),

        locationById: (locationId: string) => computed(() => store.locationById(locationId)),

        eventCountForId: (locationId: string) => computed(() => store.eventCountForId(locationId)),

        loationIdsForStates(states: string[]): string[] {
            const locations = store.allLocations;
            return locations.filter(location => states.includes(location.location.state)).map(location => location.locationId)
        },

        async loadLocations() {
            if (store.allLocations.length > 0) {
                return;
            }

            const {$museumRailwayBackendApi} = useNuxtApp();
            const locations = await $museumRailwayBackendApi('/api/location', {});
            store.setLocations(locations);
        },

        async loadLocationEventsCount() {
            const {$boudiccaSearchApi} = useNuxtApp();
            // TODO: this should be done in the backend, but boudicca does not support it yet
            const eventsResponse = await $boudiccaSearchApi('/searchApi/queryEntries', {
                method: 'POST',
                body: {
                    size: 1000
                }
            });

            const rawEvents = eventsResponse.result;
            const countMap = new Map<string, number>();
            rawEvents.forEach((event) => {
                const location_id = event.location_id;
                let curVal = countMap.get(location_id);
                if (curVal == null) {
                    curVal = 0
                } else {
                    curVal++
                }
                countMap.set(location_id, curVal);
            })

            store.setLocationEventCounts(countMap);
        }
    }
}