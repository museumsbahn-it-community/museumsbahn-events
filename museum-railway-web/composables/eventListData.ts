import {Entry, type MuseumEventGroup, useEventsStore} from "~/stores/EventsStore.ts";
import {type ComputedRef} from "vue";
import {CommonKeys} from "~/model/commonKeys.ts";
import {SemanticKeys} from "~/model/semanticKeys.ts";
import {createLocationMap, type LocationMap} from "~/model/util.ts";
import {format, subDays} from "date-fns";
import {buildQuery} from "~/composables/queryGenerator.ts";

const EVENT_COUNT_STEP_SIZE = 500;

export const EMPTY_EVENT_FILTERS = {
    fromDate: subDays(new Date(), 1),
    toDate: undefined,
    tagFilters: [],
}

export function mapEntriesToEvents(entries: Entry[], locations: LocationMap): MuseumEvent[] {
    return entries.map((value) => {
        const locationId = value[CommonKeys.LOCATION_ID];
        const operatorId = value[CommonKeys.OPERATOR_ID];
        const museumLocation = locations[locationId];
        const url = value[SemanticKeys.SOURCES];

        return {
            name: value[SemanticKeys.NAME],
            date: new Date(value[SemanticKeys.STARTDATE]),
            description: value[SemanticKeys.DESCRIPTION],
            pictureUrl: value[SemanticKeys.PICTUREURL],
            location: museumLocation,
            url,
            locationId,
            operatorId,
            locomotiveType: value[CommonKeys.LOCOMOTIVE_TYPE],
        };
    });
}

export interface EventListData {
    filteredEvents: ComputedRef<MuseumEvent[]>;
    filteredEventsGroupedByMonth: ComputedRef<MuseumEventGroup[]>;
    availableEventTagFilters: ComputedRef<EventTagFilterOption[]>;

    getEventByKey(id: string): MuseumEvent | undefined;

    loadFilterOptions(keyNames: string[], splitListsForKeys: string[]): Promise<void>;

    loadEvents(settings: EventFilterSettings): Promise<void>;
}

async function loadLocations(): Promise<MuseumLocation[]> {
    const {$museumRailwayBackendApi} = useNuxtApp()
    return await $museumRailwayBackendApi('/api/location', {});
}

export const useEventListData = (): EventListData => {
    const eventsStore = useEventsStore();

    return {
        filteredEvents: computed(() => eventsStore.queriedEvents),
        filteredEventsGroupedByMonth: computed(() => eventsStore.filteredEventsGroupedByMonth),
        availableEventTagFilters: computed(() => eventsStore.availableFilters),
        getEventByKey(key: string) {
            return eventsStore.getEventByKey(key);
        },
        async loadFilterOptions(keyNames: string[], splitListsForKeys: string[]): Promise<void> {
            if (keyNames.length === 0) {
                return;
            }

            const {$boudiccaSearchApi} = useNuxtApp()

            const requestEntries = keyNames.map((key) => {
                return {
                    name: key
                }
            })

            const filtersResponse: FiltersResponse = await $boudiccaSearchApi('/searchApi/filtersFor', {
                method: 'POST',
                body: {
                    entries: requestEntries
                }
            });

            const eventTagFilters = [];

            for (let key in filtersResponse) {
                const value = filtersResponse[key];
                const options = [];
                if (splitListsForKeys.includes(key)) {
                    // currently we have to do this on the frontend until
                    // https://github.com/boudicca-events/boudicca.events/issues/367 is discussed and implemented
                    value.forEach((entry) => options.push(...entry.split(",")))
                } else {
                    options.push(...value)
                }

                const uniqueOptions = Array.from(new Set(options));
                eventTagFilters.push({key, options: uniqueOptions})
            }

            eventsStore.setAvailableEventTagFilters(eventTagFilters)
        },
        async loadEvents(settings: EventFilterSettings): Promise<void> {
            const {$boudiccaSearchApi} = useNuxtApp()
            const locations = await loadLocations();

            // TODO: keep size and offset when filtering
            // TODO: in general we lack pagination right now

            const query = buildQuery(settings);
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

            const eventsResponse = await $boudiccaSearchApi('/searchApi/queryEntries', {
                method: 'POST',
                body
            });
            const locationMap = createLocationMap(locations);
            const queriedEvents = mapEntriesToEvents(eventsResponse.result, locationMap);
            // while we do not yet have it, we should keep track of the total events property to enable pagingation later on

            const totalEvents = eventsResponse.totalResults;
            eventsStore.setEvents(queriedEvents, totalEvents)
        },
    }
}