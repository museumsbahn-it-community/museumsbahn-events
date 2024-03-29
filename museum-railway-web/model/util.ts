import { Entry } from '~/stores/EventsStore';
import { MuseumEvent } from './museumEvent';
import { CommonKeys } from './commonKeys';
import { SemanticKeys } from './semanticKeys';
import { MuseumLocation } from 'museum-railway-api-client';

export type LocationMap = { [key: string]: MuseumLocation; };

export function createLocationMap(locations: MuseumLocation[]): LocationMap {
	const map: LocationMap = {};
	locations.forEach((location) => {
		map[location.locationId] = location;
	});
	return map;
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

export function eventKey(event: MuseumEvent): string {
	const escapedName = event.name.trim()
		.replace(/\s+/g, '-') // replace whitespace with dash
		.replace(/[^0-9a-zäöüÄÖÜ_-]/gi, '') // remove non alphanum
		.toLowerCase();
	const dateString = event.date.toISOString().replace(/:/g, '_'); // replace ':' with _
	return `${dateString}-${escapedName}`;
}