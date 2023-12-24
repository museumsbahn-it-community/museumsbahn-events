import { defineStore } from 'pinia';
import apiStore from '../apiStore.ts';

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
		}

	},
	actions: {
		async loadLocations() {
			const locationData = await apiStore.locationsApi.apiLocationGet();
			this.locations = locationData.data;
		},
	},
});