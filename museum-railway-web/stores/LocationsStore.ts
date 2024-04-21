import {defineStore} from 'pinia';

interface LocationsState {
    locations: MuseumLocation[];
    eventCounts: Map<string, number>,
}

export const useLocationsStore = defineStore('locations', {
    state: (): LocationsState => ({
        locations: [],
        eventCounts: new Map<string, number>(),
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
        eventCountForId(state: LocationsState): (locationId: string) => number {
            return (locationId: string) => {
                return state.eventCounts.get(locationId) ?? 0;
            }
        }
    },
    actions: {
        setLocations(locations: MuseumLocation[]) {
            this.locations = locations
        },
        setLocationEventCounts(eventCounts: Map<string, number>) {
            this.eventCounts = eventCounts;
        }
    },
});