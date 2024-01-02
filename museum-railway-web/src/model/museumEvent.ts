import { MuseumLocation } from 'museum-railway-api-client';

export class MuseumEvent {
	name: string;
	description: string;
	date: Date;
	locomotiveType: string | undefined; // TODO: migrate to enum
	locationId: string;
	operatorId: string;
	location: MuseumLocation | undefined;
}