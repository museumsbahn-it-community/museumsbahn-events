import { defineStore } from 'pinia';
import apiStore from '../apiStore';
import { ResultDTO } from 'boudicca-search-api-client';
import { AxiosResponse } from 'axios';
import { MuseumEvent } from '../model/museumEvent';
import { useLocationsStore } from './LocationsStore';
import { createLocationMap, eventKey, mapEntriesToEvents } from '../model/util';
import { format, isAfter, isBefore } from 'date-fns';
import { de } from 'date-fns/locale/de';
import {toRaw} from 'vue'

export type Entry = { [key: string]: string; };

interface GroupedMuseumEvents {
	[key: string]: MuseumEventGroup;
}

export interface MuseumEventGroup {
	sortKey: number;
	label: string;
	events: MuseumEvent[];
}

interface EventsState {
	queriedEvents: MuseumEvent[];
}

function filterEvents(
	events: MuseumEvent[],
	startDate: Date | undefined,
	endDate: Date | undefined,
	locationStateFilter: string[] | undefined,
	locationIdFilter: string[] | undefined,
	operatorIdFilter: string[] | undefined,
) {
	return events.filter((museumEvent) => {
		if (startDate != undefined && isBefore(museumEvent.date, startDate)) {
			return false;
		}
		if (endDate != undefined && isAfter(museumEvent.date, endDate)) {
			return false;
		}
		if (locationStateFilter != undefined && locationStateFilter.length > 0) {
			const eventLocation = museumEvent.location;
			if (eventLocation == undefined) {
				return false;
			}
			const eventLocationState = eventLocation.location.state;
			if (!locationStateFilter.includes(eventLocationState)) {
				return false;
			}
		}
		if (locationIdFilter != undefined && locationIdFilter.length > 0) {
			if (!locationIdFilter.includes(museumEvent.locationId)) {
				return false;
			}
		}
		if (operatorIdFilter != undefined && operatorIdFilter.length > 0) {
			if (!operatorIdFilter.includes(museumEvent.operatorId)) {
				return false;
			}
		}
		return true;
	});
}

function groupEventsByMonth(
	events: MuseumEvent[],
): MuseumEventGroup[] {
	const groupedEvents = events.reduce<GroupedMuseumEvents>((prev, event) => {
		const sortKey = parseInt(format(event.date, "yyyyMM"));
		const label = format(event.date, 'LLLL yyyy', {locale: de});
		const entry = prev[sortKey];
		if (entry == undefined) {
			prev[sortKey] = {
				sortKey,
				label,
				events: [],
			};
		}
		prev[sortKey].events.push(event);
		return prev;
	}, {});

	const eventGroups = []
	for (let key in groupedEvents) {
		eventGroups.push(groupedEvents[key])
	}

	return eventGroups.sort((a,b) => (a.sortKey - b.sortKey))
}

export const useEventsStore = defineStore('events', {
	state: (): EventsState => ({
		queriedEvents: [],
	}),
	getters: {
		allEvents: (state: EventsState): MuseumEvent[] => {
			return state.queriedEvents;
		},
		filteredEvents: (state: EventsState) => {
			return (startDate: Date | undefined,
							endDate: Date | undefined,
							locationStateFilter: string[] | undefined,
							locationIdFilter: string[] | undefined,
							operatorIdFilter: string[] | undefined) => {
				return filterEvents(state.queriedEvents, startDate, endDate, locationStateFilter, locationIdFilter, operatorIdFilter);
			};
		},
		filteredEventsGroupedByMonth: (state: EventsState) => {
			return (startDate: Date | undefined,
							endDate: Date | undefined,
							locationStateFilter: string[] | undefined,
							locationIdFilter: string[] | undefined,
							operatorIdFilter: string[] | undefined) => {
				const filteredEvents = filterEvents(state.queriedEvents, startDate, endDate, locationStateFilter, locationIdFilter, operatorIdFilter);
				return groupEventsByMonth(filteredEvents);
			};
		},
		getEventByKey: (state: EventsState) => {
			return (key: string): MuseumEvent | undefined => {
				let museumEvents: MuseumEvent | undefined = state.queriedEvents.filter((event) => eventKey(event) === key);
				return museumEvents[0];
			}
		}
	},
	actions: {
		async loadEventsWithLocations() {
			const locationStore = useLocationsStore();
			await locationStore.loadLocations();
			const eventsResponse: AxiosResponse<ResultDTO> = await apiStore.boudiccaSearchApi.queryEntries({});
			const rawEntries = eventsResponse.data.result;
			const locationMap = createLocationMap(toRaw(locationStore.allLocations));
			this.queriedEvents = mapEntriesToEvents(rawEntries, locationMap);
		},
	},
});