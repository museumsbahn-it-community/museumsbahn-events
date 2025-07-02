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

// Map of state codes to full state names
const stateNameMap: { [key: string]: string } = {
    'B': 'Burgenland',
    'K': 'Kärnten',
    'NÖ': 'Niederösterreich',
    'OÖ': 'Oberösterreich',
    'S': 'Salzburg',
    'ST': 'Steiermark',
    'T': 'Tirol',
    'V': 'Vorarlberg',
    'W': 'Wien'
};

export interface StateInfo {
    code: string;
    name: string;
}

export function getStateList(locations: MuseumLocation[]): StateInfo[] {
    const mappedStates = locations.map((value) => value.location.state);
    const uniqueStates = [...new Set<string>(mappedStates)];

    const stateInfoList = uniqueStates.map(stateCode => ({
        code: stateCode,
        name: stateNameMap[stateCode] || stateCode // Fallback to code if name not found
    }));

    stateInfoList.sort((a, b) => a.code.localeCompare(b.code));
    return stateInfoList;
};

export function getLocationById(locations: MuseumLocation[], locationId: string): MuseumLocation | undefined {
    const location = locations.filter((location) => location.locationId === locationId);
    if (location.length > 0) {
        return location[0];
    } else {
        return undefined;
    }
};
