import { useNuxtApp } from 'nuxt/app';
import { useAsyncData, useState, computed } from '#app';
import { compareAsc, format, subDays } from 'date-fns';
import { de } from 'date-fns/locale/de';
import { buildQuery } from '~/composables/queryGenerator';
import { createLocationMap, eventKey, type LocationMap } from '../model/util';
import { CommonKeys } from '~/model/commonKeys';
import { SemanticKeys } from '~/model/semanticKeys';
import { useLocations } from './useLocations';

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

export interface MuseumEventGroupGroup {
    sortKey: number;
    label: string;
    eventGroups: MuseumEventGroup[];
}

function groupEventsByMonth(
    events: MuseumEvent[],
): MuseumEventGroup[] {
    const groupedEvents = events.reduce<GroupedMuseumEvents>((prev, event) => {
        const sortKey = parseInt(format(event.date, "yyyyMM"));
        const label = format(event.date, 'LLLL yyyy', { locale: de });
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

/**
 * Groups events together that have the same date and title, but different departure time.
 * @param events 
 */
function groupEventsByDepartureTime(events: MuseumEvent[]): MuseumEventGroup[] {
    const groupedEvents = events.reduce<GroupedMuseumEvents>((prev, event) => {
        const dateStr = parseInt(format(event.date, "yyyyMMdd"));
        const groupKey = `${dateStr}_${event.name.replace('\s', '')}`;
        const label = event.name;
        const entry = prev[groupKey];
        if (entry == undefined) {
            prev[groupKey] = {
                sortKey: dateStr,
                label,
                events: [],
            };
        }
        prev[groupKey].events.push(event);
        return prev;
    }, {});

    const eventGroups = []
    for (let key in groupedEvents) {
        eventGroups.push(groupedEvents[key])
    }

    return eventGroups;
}

export function useEvents() {
    // Create reactive state
    const events = useState<MuseumEvent[]>('events', () => []);
    const availableFilters = useState<EventTagFilterOption[]>('availableFilters', () => []);
    const eventsLoaded = useState<number>('eventsLoaded', () => 0);
    const totalEvents = useState<number | undefined>('totalEvents', () => 0);
    const allEventsFetched = useState<boolean>('allEventsFetched', () => false);

    // Get locations from the useLocations composable
    const { allLocations } = useLocations();

    // Computed properties (equivalent to getters)
    const filteredEvents = computed(() => events.value);

    const filteredEventsGroupedByMonthAndDepartureTime = computed(() => {
        const groupedByMonth = groupEventsByMonth(events.value);
        const groupedByMonthAndDeparture = groupedByMonth.map<MuseumEventGroupGroup>((eventGroup) => {
            return {
                sortKey: eventGroup.sortKey,
                label: eventGroup.label,
                eventGroups: groupEventsByDepartureTime(eventGroup.events)
            }
        });

        return groupedByMonthAndDeparture;
    });

    const eventsForLocationId = (locationId: string): MuseumEvent[] => {
        return events.value.filter((event) => event.locationId === locationId);
    };

    const eventsForLocationIdGrouped = (locationId: string): MuseumEventGroupGroup[] => {
        const groupedByMonth = groupEventsByMonth(events.value.filter((event) => event.locationId === locationId));
        const groupedByMonthAndDeparture = groupedByMonth.map<MuseumEventGroupGroup>((eventGroup) => {
            return {
                sortKey: eventGroup.sortKey,
                label: eventGroup.label,
                eventGroups: groupEventsByDepartureTime(eventGroup.events)
            }
        });

        return groupedByMonthAndDeparture;
    };

    const eventCountForLocationId = (locationId: string): number => {
        return events.value.filter((event) => event.locationId === locationId).length;
    };

    const getEventByKey = (key: string): MuseumEvent | undefined => {
        let museumEvents = events.value.filter((event) => eventKey(event) === key);
        return museumEvents[0];
    };

    // Function to fetch all events
    const { data: allEventsData, pending: allEventsPending, refresh: refreshAllEvents, error: allEventsError } = useAsyncData(
        'allEvents',
        async () => {
            if (allEventsFetched.value) {
                // event data changes very rarely (at most once a day), so there is no need to
                // refetch it everytime the user changes to another page
                return events.value;
            }

            const query = buildQuery({
                fromDate: subDays(new Date(), 1), // only show events from yesterday or later
            });
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

            const queriedEvents = await queryEventsAndUpdateState(body);
            allEventsFetched.value = true;
            return queriedEvents;
        },
        {
            server: true,
            lazy: false,
        }
    );

    // Function to fetch events for a specific location
    const fetchEventsForLocation = async (locationId: string): Promise<MuseumEvent[]> => {
        if (allEventsFetched.value) {
            // event data changes very rarely (at most once a day), so there is no need to
            // refetch it everytime the user changes to another page
            return eventsForLocationId(locationId);
        }

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

        const queriedEvents = await queryEventsAndUpdateState(body);
        return queriedEvents;
    };

    // Helper function to query events and update state
    async function queryEventsAndUpdateState(body: { query: string | undefined; size: number; }): Promise<MuseumEvent[]> {
        const { $boudiccaSearchApi } = useNuxtApp();
        const locations = allLocations.value;
        const eventsResponse = await $boudiccaSearchApi('/api/search/queryEntries', {
            method: 'POST',
            body
        }).catch(e => {
            console.error("error loading events: ", e);
            return { result: [], totalResults: 0 };
        });
        
        const locationMap = createLocationMap(locations);
        const queriedEvents = mapBoudiccaEntriesToEvents(eventsResponse.result, locationMap);
        
        // Update state
        totalEvents.value = eventsResponse.totalResults;
        events.value = queriedEvents.sort((a, b) => compareAsc(a.date, b.date));
        eventsLoaded.value = queriedEvents.length;
        
        return queriedEvents;
    }

    // Helper function to map API response to events
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

    // Function to set events (useful for testing or manual updates)
    const setEvents = (newEvents: MuseumEvent[], newTotalEvents: number): void => {
        events.value = newEvents;
        eventsLoaded.value = newEvents.length;
        totalEvents.value = newTotalEvents;
    };

    // Function to set available event tag filters
    const setAvailableEventTagFilters = (filters: EventTagFilterOption[]): void => {
        availableFilters.value = filters;
    };

    return {
        // State
        events,
        availableFilters,
        eventsLoaded,
        totalEvents,
        allEventsFetched,
        
        // AsyncData properties
        allEventsData,
        allEventsPending,
        refreshAllEvents,
        allEventsError,
        
        // Methods
        fetchEventsForLocation,
        setEvents,
        setAvailableEventTagFilters,
        
        // Computed properties (getters)
        filteredEvents,
        filteredEventsGroupedByMonthAndDepartureTime,
        eventsForLocationId,
        eventsForLocationIdGrouped,
        eventCountForLocationId,
        getEventByKey,
    };
}