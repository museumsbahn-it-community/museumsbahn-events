import {MuseumEvent} from './museumEvent';
import {MuseumLocation} from 'museum-railway-api-client';

export type LocationMap = { [key: string]: MuseumLocation; };

export function createLocationMap(locations: MuseumLocation[]): LocationMap {
    const map: LocationMap = {};
    locations.forEach((location) => {
        map[location.locationId] = location;
    });
    return map;
}

export function eventKey(event: MuseumEvent): string {
    const escapedName = event.name.trim()
        .replace(/\s+/g, '-') // replace whitespace with dash
        .replace(/[^0-9a-zäöüÄÖÜ_-]/gi, '') // remove non alphanum
        .toLowerCase();
    const dateString = event.date.toISOString().replace(/:/g, '_'); // replace ':' with _
    return `${dateString}-${escapedName}`;
}