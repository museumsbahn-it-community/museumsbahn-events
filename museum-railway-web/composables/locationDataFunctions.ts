import {useNuxtApp} from 'nuxt/app';
import type {MuseumLocation} from "~/apiModel/apiModel";

export async function fetchLocations(): Promise<MuseumLocation[]> {
    const {$museumRailwayBackendApi} = useNuxtApp();
    const rawLocations = await $museumRailwayBackendApi('/api/location', {})
        .catch(e => {
            console.error("error loading locations: ", e);
            return [];
        });
    

    return rawLocations as MuseumLocation[];
}

export function getStateList(locations: MuseumLocation[]): string[] {
    const mappedStates = locations.map((value) => value.location.state);
    const states = [...new Set<string>(mappedStates)]
    states.sort((a, b) => a.localeCompare(b));
    return states;
};

export function getLocationById(locations: MuseumLocation[], locationId: string): MuseumLocation | undefined {
    const location = locations.filter((location) => location.locationId === locationId);
    if (location.length > 0) {
        return location[0];
    } else {
        return undefined;
    }
};