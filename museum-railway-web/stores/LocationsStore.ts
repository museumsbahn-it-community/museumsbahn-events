import { useNuxtApp } from 'nuxt/app';
import {defineStore} from 'pinia';

interface LocationsState {
    locations: MuseumLocation[];
    locationsFetched: boolean,
}

export const useLocationsStore = defineStore('locations', {
    state: (): LocationsState => ({
        locations: [],
        locationsFetched: false,
    }),
    getters: {
        allLocations(state): MuseumLocation[] {
            return state.locations;
        },
        stateList(state: LocationsState): string[] {
            const mappedStates = state.locations.map((value) => value.location.state);
            return [...new Set(mappedStates)].sort((a, b) => a.localeCompare(b));
        },
        locationById(state: LocationsState): (locationId: string) => MuseumLocation | undefined {
            return (locationId: string) => {
                const location = state.locations.filter((location) => location.locationId === locationId);
                if (location.length > 0) {
                    return location[0];
                } else {
                    return undefined;
                }
            }
        },
    },
    actions: {
        async fetchLocations(): Promise<MuseumLocation[]> {
            if (this.locationsFetched) {
                // location data changes almost never, so there is no need to
                // refetch it everytime the user navigates within the app
                return this.locations;
            }
            const {$museumRailwayBackendApi} = useNuxtApp()
            this.locations =  await $museumRailwayBackendApi('/api/location', {})
                .catch(e => console.error("error loading locations: ",e));
                
            this.locationsFetched = true;
            return this.locations;
        },
    },
});