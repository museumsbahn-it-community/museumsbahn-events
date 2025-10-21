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
    searchTerm: string;
    selectedStates: string[];
    allStates: string[];
    dateRange?: Date[];
    eventTypes?: string[];
    trainTypes?: string[];
    isVolunteer?: boolean;
    isCommercial?: boolean;
    tags?: string[];
}

/**
 * Filters events by all available filter criteria
 * @param events
 * @param locations
 * @param options The filter options
 * @returns Filtered events
 */
export function filterEvents(events: MuseumEvent[], locations: MuseumLocation[], options: EventFilterOptions): MuseumEvent[] {
    const {
        searchTerm, 
        selectedStates, 
        allStates, 
        dateRange, 
        eventTypes, 
        trainTypes, 
        isVolunteer, 
        isCommercial, 
        tags
    } = options;

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
        const states = selectedStates.map(state => state.toLowerCase());
        filtered = filtered.filter(event => {
                const location = locations.filter(location => location.locationId === event.locationId)[0];
                return location != null && states.includes(location.location.state.toLowerCase());
            }
        );
    }

    // Apply date range filter
    if (dateRange && dateRange.length > 0) {
        if (dateRange[0]) {
            const fromDate = dateRange[0];
            filtered = filtered.filter(event => new Date(event.date) >= fromDate);
        }

        if (dateRange.length > 1 && dateRange[1]) {
            const toDate = dateRange[1];
            filtered = filtered.filter(event => new Date(event.date) <= toDate);
        }
    }

    // Apply event type filter (if any selected, otherwise show all)
    if (eventTypes && eventTypes.length > 0) {
        filtered = filtered.filter(event =>
            eventTypes.some(type => 
                event.eventCategory && event.eventCategory.toLowerCase() === type.toLowerCase()
            ) || !event.eventCategory
        );
    }

    // Apply train type filter (if any selected, otherwise show all)
    if (trainTypes && trainTypes.length > 0) {
        filtered = filtered.filter(event =>
            trainTypes.some(type => 
                event.locomotiveType && event.locomotiveType.toLowerCase() === type.toLowerCase()
            ) || !event.locomotiveType
        );
    }

    // Apply volunteer/commercial filters
    if (isVolunteer && !isCommercial) {
        // Only show volunteer events - look for keywords in name or description
        filtered = filtered.filter(event => 
            hasVolunteerKeywords(event.name) || 
            (event.description && hasVolunteerKeywords(event.description))
        );
    } else if (!isVolunteer && isCommercial) {
        // Only show commercial events - look for keywords in name or description
        filtered = filtered.filter(event => 
            hasCommercialKeywords(event.name) || 
            (event.description && hasCommercialKeywords(event.description))
        );
    }

    // Apply tag filters
    if (tags && tags.length > 0) {
        filtered = filtered.filter(event => {
            // Check if any of the selected tags are in the event name or description
            // TODO: also check the tags themselves
            return tags.some(tag =>
                hasTagKeywords(event.name, tag) ||
                event.description && hasTagKeywords(event.description, tag)
            );
        });
    }

    return filtered;
}

/**
 * Helper function to check if text contains volunteer-related keywords
 */
function hasVolunteerKeywords(text: string): boolean {
    const keywords = ['ehrenamtlich', 'verein', 'freiwillig', 'hobby'];
    const lowerText = text.toLowerCase();
    return keywords.some(keyword => lowerText.includes(keyword));
}

/**
 * Helper function to check if text contains commercial-related keywords
 */
function hasCommercialKeywords(text: string): boolean {
    const keywords = ['kommerziell', 'gewerblich', 'unternehmen', 'firma'];
    const lowerText = text.toLowerCase();
    return keywords.some(keyword => lowerText.includes(keyword));
}

/**
 * Helper function to check if text contains tag-related keywords
 */
function hasTagKeywords(text: string, tag: string): boolean {
    // Map tags to related keywords
    // TODO: remove and replace by proper keywords
    const tagKeywords: {[key: string]: string[]} = {
        'Dampflok': ['dampf', 'dampflok', 'dampflokomotive'],
        'Diesellok': ['diesel', 'diesellok', 'diesellokomotive'],
        'Elektrolok': ['elektro', 'elektrolok', 'elektrische lokomotive', 'e-lok'],
        'Schienenbus': ['schienenbus'],
        'Triebwagen': ['triebwagen', 'triebzug'],
        'Nostalgiezug': ['nostalgie', 'nostalgiezug', 'historisch'],
        'Kinderprogramm': ['kind', 'kinder', 'familie', 'familien'],
        'Führerstandsmitfahrt': ['führerstandsmitfahrt'],
        'Fotohalt': ['foto', 'fotohalt', 'fotostopp']
    };

    const lowerText = text.toLowerCase();

    // Check if the tag itself is in the text
    if (lowerText.includes(tag.toLowerCase())) {
        return true;
    }

    // Check if any of the tag's keywords are in the text
    const keywords = tagKeywords[tag] || [];
    return keywords.some(keyword => lowerText.includes(keyword));
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
