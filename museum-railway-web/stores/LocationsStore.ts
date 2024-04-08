import {defineStore} from 'pinia';

interface LocationsState {
    locations: MuseumLocation[];
}

export const useLocationsStore = defineStore('locations', {
    state: (): LocationsState => ({
        locations: [],
    }),
    getters: {
        allLocations(state): MuseumLocation[] {
            return state.locations;
        },
        locationsCount(state): number {
            return state.locations.length;
        },
        stateList(state: LocationsState): string[] {
            const mappedStates = state.locations.map((value) => value.location.state);
            return [...new Set(mappedStates)].sort((a, b) => a.localeCompare(b));
        },
        locationById(state: LocationsState): MuseumLocation | undefined {
            return (locationId: string) => {
                const location = state.locations.filter((location) => location.locationId === locationId);
                if (location.length > 0) {
                    return location[0];
                } else {
                    return undefined;
                }
            }
        }
    },
    actions: {
        async loadLocations(): Promise<void> {
            const {$museumRailwayBackendApi} = useNuxtApp()
            this.locations = await $museumRailwayBackendApi('/api/location',{});
        },
    },
});