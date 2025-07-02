import {useNuxtApp} from 'nuxt/app';
import {format, subDays} from 'date-fns';
import {de} from 'date-fns/locale/de';
import {buildQuery} from '~/composables/queryGenerator';
import {createLocationMap, type LocationMap} from '~/model/util';
import {CommonKeys} from '~/model/commonKeys';
import {SemanticKeys} from '~/model/semanticKeys';
import type {EventFilterSettings, MuseumEvent, MuseumLocation} from '~/apiModel/apiModel';

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

export function groupEventsByMonth(
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
    for (const key in groupedEvents) {
        eventGroups.push(groupedEvents[key])
    }

    return eventGroups.sort((a, b) => (a.sortKey - b.sortKey))
}

/**
 * Groups events together that have the same date and title, but different departure time.
 * @param events
 */
export function groupEventsByDepartureTime(events: MuseumEvent[]): MuseumEventGroup[] {
    const groupedEvents = events.reduce<GroupedMuseumEvents>((prev, event) => {
        const dateStr = parseInt(format(event.date, "yyyyMMdd"));
        const groupKey = `${dateStr}_${event.name.replace('s', '')}`; // TODO: does this still work?
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
    for (const key in groupedEvents) {
        eventGroups.push(groupedEvents[key])
    }

    return eventGroups;
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

export async function queryEvents(
    body: { query: string | undefined; size: number; },
    locations: MuseumLocation[]
): Promise<MuseumEvent[]> {
    const {$boudiccaSearchApi} = useNuxtApp();
    const eventsResponse = await $boudiccaSearchApi('/api/search/queryEntries', {
        method: 'POST',
        body
    })
    if (eventsResponse.error != null) {
        console.error("error loading events: ", eventsResponse.error);
        return [];
    }

    const result = eventsResponse.result;
    if (result == null) {
        console.error("event response is invalid: ", result);
        return [];
    }

    const locationMap = createLocationMap(locations);
    return mapBoudiccaEntriesToEvents(result as Entry[], locationMap);
}

export function eventsGroupedByMonthAndDepartureTime(events: MuseumEvent[]): MuseumEventGroupGroup[] {
    const groupedByMonth = groupEventsByMonth(events);
    return groupedByMonth.map<MuseumEventGroupGroup>((eventGroup) => {
        return {
            sortKey: eventGroup.sortKey,
            label: eventGroup.label,
            eventGroups: groupEventsByDepartureTime(eventGroup.events)
        }
    });
}

export function eventsForLocationId(events: MuseumEvent[], locationId: string): MuseumEvent[] {
    return events.filter((event) => event.locationId === locationId);
}

export function eventsForLocationIdGrouped(events: MuseumEvent[], locationId: string): MuseumEventGroupGroup[] {
    const groupedByMonth = groupEventsByMonth(events.filter((event) => event.locationId === locationId));
    return groupedByMonth.map<MuseumEventGroupGroup>((eventGroup) => {
        return {
            sortKey: eventGroup.sortKey,
            label: eventGroup.label,
            eventGroups: groupEventsByDepartureTime(eventGroup.events)
        }
    });
}

export function eventCountForLocationId(events: MuseumEvent[], locationId: string): number {
    return events.filter((event) => event.locationId === locationId).length;
}

/**
 * Interface for event filter options
 */
export interface EventFilterOptions {
    events: MuseumEvent[];
    searchTerm: string;
    selectedStates: string[];
    allStates: string[];
}

/**
 * Filters events by search term and selected states
 * @param options The filter options
 * @returns Filtered events
 */
export function filterEvents(options: EventFilterOptions): MuseumEvent[] {
    const { events, searchTerm, selectedStates, allStates } = options;

    if (!events || events.length === 0) return [];

    let filtered = [...events];

    // Filter by search term
    if (searchTerm.trim()) {
        const term = searchTerm.toLowerCase().trim();
        filtered = filtered.filter(event => 
            event.name.toLowerCase().includes(term) || 
            (event.description && event.description.toLowerCase().includes(term)) ||
            (event.location && event.location.location.city && event.location.location.city.toLowerCase().includes(term))
        );
    }

    // Filter by selected states - only if at least one state is selected but not all
    if (selectedStates.length > 0 && selectedStates.length < allStates.length) {
        filtered = filtered.filter(event => 
            event.location && selectedStates.includes(event.location.location.state)
        );
    }

    return filtered;
}


// Function to fetch all events
export async function fetchEvents(locations: MuseumLocation[]): Promise<MuseumEvent[]> {
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

    return await queryEvents(body, locations);
}

export async function fetchEventsForLocation(locationId: string, locations: MuseumLocation[]): Promise<MuseumEvent[]> {
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

    return await queryEvents(body, locations);
}
