import {defineStore} from 'pinia';
import {eventKey} from '../model/util';
import {format} from 'date-fns';
import {de} from 'date-fns/locale/de';

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
    eventsLoaded: number;
    totalEvents: number | undefined;
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

export const useEventsStore = defineStore('events', {
    state: (): EventsState => ({
        queriedEvents: [],
        eventsLoaded: 0,
        totalEvents: 0,
    }),
    getters: {
        filteredEvents: (state: EventsState) => {
            return state.queriedEvents;
        },
        filteredEventsGroupedByMonth: (state: EventsState) => {
            return groupEventsByMonth(state.queriedEvents);
        },
        getEventByKey: (state: EventsState) => {
            return (key: string): MuseumEvent | undefined => {
                let museumEvents = state.queriedEvents.filter((event) => eventKey(event) === key);
                return museumEvents[0];
            }
        },
    },
    actions: {
        setEvents(events: MuseumEvent[], totalEvents: number): void {
            this.queriedEvents = events;
            this.eventsLoaded = events.length;
            this.totalEvents = totalEvents;
        }
    },
});