import {useNuxtApp} from 'nuxt/app';
import {format, subDays} from 'date-fns';
import {de} from 'date-fns/locale/de';
import {buildQuery} from '~/composables/queryGenerator';
import {createLocationMap, type LocationMap} from '~/model/util';
import {CommonKeys} from '~/model/commonKeys';
import {SemanticKeys} from '~/model/semanticKeys';
import {
    MuseumEventCategory,
    VehicleType
} from '~/apiModel/apiModel';
import {
    MuseumEventRegistration,
    RecurrenceType
} from '~/apiModel/apiModel';
import {
    type EventFilterSettings,
    type MuseumEvent,
    type MuseumLocation,
    OperationType
} from '~/apiModel/apiModel';
import type {EventFilter} from "~/types/EventFilterTypes";

export type Entry = { [key: string]: string; };
const EVENT_COUNT_STEP_SIZE = 500;

export const VehicleTypeLabels: { [key: string]: string } = {
    [VehicleType.DIESEL_TRAIN]: "Diesel",
    [VehicleType.STEAM_TRAIN]: "Dampfzug",
    [VehicleType.ELECTRIC_TRAIN]: "Elektrisch",
    [VehicleType.TRAM]: "Straßenbahn",
    [VehicleType.SHIP]: "Schiff",
    [VehicleType.UNKNOWN]: "Unbekannt",
}
export const RecurrenceTypeLabels: { [key: string]: string } = {
    [RecurrenceType.REGULARLY]: "Regelmäßíg",
    [RecurrenceType.RARELY]: "Gelegentlich",
    [RecurrenceType.ONCE]: "Einmalig",
}

export const OperationTypeLabels: { [key: string]: string } = {
    [OperationType.VOLUNTEER]: "Ehrenamtlich organisiert",
    [OperationType.COMMERCIAL]: "Kommerziell organisiert",
    [OperationType.UNKNOWN]: "Unbekannt",
}

export const EventCategoryLabels: { [key: string]: string } = {
    [MuseumEventCategory.SPECIAL_TRIP]: "Sonderfahrt", // Sonderfahrt - excursion on public rails or a special event on the museum railway
    [MuseumEventCategory.RAILWAY_MUSEUM]: "Museum", // Museum - opening day of a museum, for museums without regular opening days or special events
    [MuseumEventCategory.MUSEUM_RAILWAY]: "Museumsbahn", // Museumsbahn - running day of a dedicated museum railway
    [MuseumEventCategory.MUSEUM_EVENT]: "Veranstaltung", // Veranstaltung - besondere Veranstaltung, Konzert etc.
    [MuseumEventCategory.MODEL_RAILWAY]: "Modellbahn", // Modellbahn - not in use at the moment
}

export const RegistrationTypeLabels = {
    [MuseumEventRegistration.FREE]: 'Ohne Anmeldung',
    [MuseumEventRegistration.REGISTRATION]: 'Anmeldung erforderlich',
    [MuseumEventRegistration.PRE_SALES_ONLY]: 'Nur Vorverkauf',
    [MuseumEventRegistration.RESERVATION_RECOMMENDED]: 'Reservierung empfohlen',
    [MuseumEventRegistration.PRIVATE_EVENT]: 'Private Veranstaltung',
    [MuseumEventRegistration.TICKET]: 'Ticketpflichtig',
};

export function translateTag(tag: string, table: { [key: string]: string }): string {
    return table[tag] || tag;
}

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

// Add this to eventDataFunctions.ts where the other label maps are defined

// Translation map for tags - will be populated dynamically but starting with common translations
export const TagLabels: { [key: string]: string } = {
  'historic_train': 'Nostalgiezug',
  'historic_train_trip': 'Reise mit Nostalgiezug',
  'history': 'Geschichte',
  'museum': 'Museum',
  'museum_event': 'Veranstaltung im Museum',
  'museum_railway': 'Museumsbahn',
  'narrow_gauge': 'Schmalspurbahn',
  'railway_museum': 'Eisenbahnmuseum',
};

// Function to extract unique tags from all events
export function extractUniqueTagsFromEvents(events: MuseumEvent[]): string[] {
  if (!events || events.length === 0) return [];
  
  const allTags = new Set<string>();
  
  // Extract tags from events
  events.forEach(event => {
    // Add tags from the tags array if it exists
    if (event.tags && Array.isArray(event.tags)) {
      event.tags.forEach(tag => allTags.add(tag));
    }
  });
  
  // Convert Set to sorted Array
  return Array.from(allTags).sort();
}

// Updated function to get tag count with proper tag extraction
function getTagsFromEvent(event: MuseumEvent): string[] {
  const eventTags: string[] = [];
  
  // Add tags from the tags array if it exists
  if (event.tags && Array.isArray(event.tags)) {
    eventTags.push(...event.tags);
  }
  
  return [...new Set(eventTags)]; // Remove duplicates
}

// Modify the mapBoudiccaEntriesToEvents function to extract tags from the TAGS field
function mapBoudiccaEntriesToEvents(entries: Entry[], locations: LocationMap): MuseumEvent[] {
    return entries.map((value) => {
        const locationId = value[CommonKeys.LOCATION_ID];
        const operatorId = value[CommonKeys.OPERATOR_ID];
        const museumLocation = locations[locationId];
        const url = value[SemanticKeys.URL];

        // Extract tags from the TAGS field if it exists
        const tagsString = value[SemanticKeys.TAGS] || '';
        const tags = tagsString ? tagsString.split(',').map(tag => tag.trim()) : [];

        const startDateKeys = Object.keys(value).filter(val => val.startsWith(SemanticKeys.STARTDATE));
        return {
            name: value[SemanticKeys.NAME],
            eventCategory: value[CommonKeys.MUSEUM_EVENTS_CATEGORY] as MuseumEventCategory | undefined,
            date: new Date(value[startDateKeys[0]]),
            description: value[SemanticKeys.DESCRIPTION],
            pictureUrl: value[SemanticKeys.PICTUREURL],
            pictureAltText: value[SemanticKeys.PICTURE_ALT_TEXT],
            pictureCopyright: value[SemanticKeys.PICTURE_COPYRIGHT],
            location: museumLocation,
            vehicleType: value[CommonKeys.VEHICLE_TYPE] as VehicleType | undefined,
            registration: value[CommonKeys.MUSEUM_EVENT_REGISTRATION] as MuseumEventRegistration | undefined,
            recurrenceType: value[SemanticKeys.RECURRENCE_TYPE] as RecurrenceType | undefined,
            operationType: value[CommonKeys.OPERATION_TYPE] as OperationType | undefined,
            url,
            locationId,
            operatorId,
            tags,
            locomotiveType: value[CommonKeys.VEHICLE_TYPE],
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
 * Filters events by all available filter criteria
 * @param events
 * @param locations
 * @param options The filter options
 * @returns Filtered events
 */
export function filterEvents(events: MuseumEvent[], locations: MuseumLocation[], options: EventFilter): MuseumEvent[] {
    const {
        searchTerm,
        selectedStates,
        fromDate,
        toDate,
        eventCategories,
        vehicleTypes,
        recurrenceTypes,
        registrationTypes,
        isVolunteer,
        isCommercial,
        tags
    } = options;

    if (!events || events.length === 0) return [];

    let filtered = [...events];

    // Filter by search term
    if (searchTerm != null && searchTerm?.trim()) {
        const term = searchTerm.toLowerCase().trim();
        filtered = filtered.filter(event =>
            event.name.toLowerCase().includes(term) ||
            (event.description && event.description.toLowerCase().includes(term)) ||
            (event.location && event.location.location.city && event.location.location.city.toLowerCase().includes(term))
        );
    }

    // Filter by selected states - only if at least one state is selected but not all
    if (selectedStates != null && selectedStates.length > 0) {
        const states = selectedStates.map(state => state.toLowerCase());
        filtered = filtered.filter(event => {
                const location = locations.filter(location => location.locationId === event.locationId)[0];
                return location != null && states.includes(location.location.state.toLowerCase());
            }
        );
    }

    // Apply date range filter
    if (fromDate != null) {
        filtered = filtered.filter(event => new Date(event.date) >= fromDate);
    }

    if (toDate != null) {
        filtered = filtered.filter(event => new Date(event.date) <= toDate);
    }

    // Apply event type filter (if any selected, otherwise show all)
    if (eventCategories && eventCategories.length > 0) {
        filtered = filtered.filter(event =>
            eventCategories.some(type =>
                event.eventCategory != null && event.eventCategory.toLowerCase() === type.toLowerCase()
            )
        );
    }

    // Apply train type filter (if any selected, otherwise show all)
    if (vehicleTypes && vehicleTypes.length > 0) {
        filtered = filtered.filter(event =>
            vehicleTypes.some(type =>
                event.vehicleType != null && event.vehicleType.toLowerCase() === type.toLowerCase()
            )
        );
    }

    // Apply recurrence type filter (if any selected, otherwise show all)
    if (recurrenceTypes && recurrenceTypes.length > 0) {
        filtered = filtered.filter(event =>
            recurrenceTypes.some(type =>
                event.recurrenceType != null && event.recurrenceType.toLowerCase() === type.toLowerCase()
            )
        );
    }

    // Apply registration type filter (if any selected, otherwise show all)
    if (registrationTypes && registrationTypes.length > 0) {
        filtered = filtered.filter(event =>
            registrationTypes.some(type =>
                event.registration != null && event.registration.toLowerCase() === type.toLowerCase()
            )
        );
    }

    // Apply volunteer/commercial filters
    if (isVolunteer && !isCommercial) {
        // Only show volunteer events - look for keywords in name or description
        filtered = filtered.filter(event =>
            event.operationType === OperationType.VOLUNTEER
        );
    } else if (!isVolunteer && isCommercial) {
        // Only show commercial events - look for keywords in name or description
        filtered = filtered.filter(event =>
            event.operationType === OperationType.COMMERCIAL
        );
    }

    // Apply tag filters
    if (tags && tags.length > 0) {
      filtered = filtered.filter(event => {
        const eventTags = getTagsFromEvent(event);
        return tags.some(tag => eventTags.includes(tag));
      });
    }

    return filtered;
}

/**
 * Helper function to check if text contains tag-related keywords
 */
function hasTagKeywords(text: string, tag: string): boolean {
    // Map tags to related keywords
    // TODO: remove and replace by proper keywords
    const tagKeywords: { [key: string]: string[] } = {
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