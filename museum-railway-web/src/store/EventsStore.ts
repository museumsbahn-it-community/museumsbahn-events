import { defineStore } from 'pinia';
import apiStore from '../apiStore';
import { ResultDTO } from 'boudicca-search-api-client';
import { AxiosResponse } from 'axios';

export type Entry = { [key: string]: string; };

interface EventsState {
	queriedEvents: Entry[];
}

export const useEventsStore = defineStore('events', {
	state: (): EventsState => ({
		queriedEvents: [],
	}),
	getters: {
		allEvents(state): Entry[] {
			return state.queriedEvents;
		},
	},
	actions: {
		async loadEvents() {
			const events: AxiosResponse<ResultDTO> = await apiStore.boudiccaSearchApi.queryEntries({

			});
			this.queriedEvents = events.data.result;
		},
	},
});