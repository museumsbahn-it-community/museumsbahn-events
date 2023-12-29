import { defineStore } from 'pinia';
import apiStore from '../apiStore.ts';
import { MuseumLocation } from 'museum-railway-api-client';

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

	},
	actions: {
		async loadEvents() {
			const locationData = await apiStore.locationsApi.apiLocationGet();
			this.locations = locationData.data;
		},
	},
});