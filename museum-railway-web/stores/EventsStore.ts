import {defineStore} from 'pinia';
import {createLocationMap, eventKey, type LocationMap} from '../model/util';
import {compareAsc, format, subDays} from 'date-fns';
import {de} from 'date-fns/locale/de';
import { mapBoudiccaEntriesToEvents } from '~/composables/eventListData';
import { useNuxtApp } from 'nuxt/app';
import { buildQuery } from '~/composables/queryGenerator';
import { useLocationsStore } from './LocationsStore';
import { CommonKeys } from '~/model/commonKeys';
import { SemanticKeys } from '~/model/semanticKeys';

export type Entry = { [key: string]: string; };
const EVENT_COUNT_STEP_SIZE = 500;

export const EMPTY_EVENT_FILTERS: EventFilterSettings = {
    fromDate: subDays(new Date(), 1),
    toDate: undefined,
    tagFilters: [],
}

interface GroupedMuseumEvents {
    [key: string]: MuseumEventGroup;
}

export interface MuseumEventGroup {
    sortKey: number;
    label: string;
    events: MuseumEvent[];
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

    return eventGroups.sort((a, b) => (a.sortKey - b.sortKey))
}

interface EventsState {
    queriedEvents: MuseumEvent[];
    availableFilters: EventTagFilterOption[];
    eventsLoaded: number;
    totalEvents: number | undefined;
    allEventsFetched: boolean;
}

export const useEventsStore = defineStore('events', {
    state: (): EventsState => ({
        queriedEvents: [],
        availableFilters: [],
        eventsLoaded: 0,
        totalEvents: 0,
        allEventsFetched: false,
    }),
    getters: {
        filteredEvents: (state: EventsState) => {
            return state.queriedEvents;
        },
        filteredEventsGroupedByMonth: (state: EventsState) => {
            return groupEventsByMonth(state.queriedEvents);
        },
        eventsForLocationId(state: EventsState): (locationId: string) => MuseumEvent[] {
            return (locationId: string) => {
                return state.queriedEvents.filter((event) => event.locationId === locationId);
            }
        },
        eventsForLocationIdGrouped(state: EventsState): (locationId: string) => MuseumEventGroup[] {
            return (locationId: string) => {
                return groupEventsByMonth(state.queriedEvents.filter((event) => event.locationId === locationId));
            }
        },
        eventCountForLocationId(state: EventsState): (locationId: string) => number {
            return (locationId: string) => {
                return state.queriedEvents.filter((event) => event.locationId === locationId).length;
            }
        },
        getEventByKey: (state: EventsState) => {
            return (key: string): MuseumEvent | undefined => {
                let museumEvents = state.queriedEvents.filter((event) => eventKey(event) === key);
                return museumEvents[0];
            }
        }
    },
    actions: {
        async fetchAllEvents(): Promise<MuseumEvent[]> {
            if (this.allEventsFetched) {
                // event data changes very rarely (at most once a day), so there is no need to
                // refetch it everytime the user changes to another page
                return this.queriedEvents;
            }

            // TODO: keep size and offset when filtering
            // TODO: in general we lack pagination right now

            const query = buildQuery(EMPTY_EVENT_FILTERS);
            const body: {
                query: string | undefined,
                size: number,
            } = {
                query: undefined,
                size: EVENT_COUNT_STEP_SIZE,
            }
            if (query.trim().length > 0) {
                body.query = query
            }

            console.log("query: ", query)

            const queriedEvents = await queryEventsAndUpdateState(this, body);
            this.allEventsFetched = true;
            return queriedEvents;
        },
        async fetchEventsForLocation(locationId: string): Promise<MuseumEvent[]> {
            if (this.allEventsFetched) {
                // event data changes very rarely (at most once a day), so there is no need to
                // refetch it everytime the user changes to another page
                return this.eventsForLocationId(locationId);
            }

            // TODO: keep size and offset when filtering
            // TODO: in general we lack pagination right now

            const filters = {
                ...EMPTY_EVENT_FILTERS,
                locationId
            }

            const query = buildQuery(filters);
            const body: {
                query: string | undefined,
                size: number,
            } = {
                query: query,
                size: EVENT_COUNT_STEP_SIZE,
            }

            const queriedEvents = await queryEventsAndUpdateState(this, body);
            return queriedEvents;
        },
        setEvents(events: MuseumEvent[], totalEvents: number): void {
            this.queriedEvents = events;
            this.eventsLoaded = events.length;
            this.totalEvents = totalEvents;
        },
        setAvailableEventTagFilters(filters: EventTagFilterOption[]):void {
            this.availableFilters = filters;
        }
    },
});

async function queryEventsAndUpdateState(state: EventsState,body: { query: string | undefined; size: number; }): Promise<MuseumEvent[]> {
    const {$boudiccaSearchApi} = useNuxtApp()
    const locationsStore = useLocationsStore();
    const locations = locationsStore.allLocations;
    const eventsResponse = await $boudiccaSearchApi('/api/search/queryEntries', {
        method: 'POST',
        body
    }).catch(e => console.error("error loading events: ", e));
    const locationMap = createLocationMap(locations);
    const queriedEvents = mapBoudiccaEntriesToEvents(eventsResponse.result, locationMap);
    // while we do not yet have it, we should keep track of the total events property to enable pagingation later on
    state.totalEvents = eventsResponse.totalResults;
    state.queriedEvents = queriedEvents.sort((a,b) => compareAsc(a.date, b.date));
    state.eventsLoaded = queriedEvents.length;
    return queriedEvents;
}


function mapBoudiccaEntriesToEvents(entries: Entry[], locations: LocationMap): MuseumEvent[] {
    return entries.map((value) => {
        const locationId = value[CommonKeys.LOCATION_ID];
        const operatorId = value[CommonKeys.OPERATOR_ID];
        const museumLocation = locations[locationId];
        const url = value[SemanticKeys.URL];

        const startDateKeys = Object.keys(value).filter(val => val.startsWith(SemanticKeys.STARTDATE));

        return {
            name: value[SemanticKeys.NAME],
            eventCategory: value[SemanticKeys.CATEGORY]?.toLowerCase(),
            date: new Date(value[startDateKeys[0]]),
            description: value[SemanticKeys.DESCRIPTION],
            pictureUrl: value[SemanticKeys.PICTUREURL],
            pictureAltText: value[SemanticKeys.PICTURE_ALT_TEXT],
            pictureCopyright: value[SemanticKeys.PICTURE_COPYRIGHT],
            location: museumLocation,
            url,
            locationId,
            operatorId,
            locomotiveType: value[CommonKeys.LOCOMOTIVE_TYPE],
        };
    });
}