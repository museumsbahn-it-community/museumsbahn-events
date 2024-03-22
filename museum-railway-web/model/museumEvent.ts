import { MuseumLocation } from 'museum-railway-api-client';

export class MuseumEvent {
	name: string;
	description: string;
	date: Date;
	url: string
	pictureUrl: string | undefined;
	locomotiveType: string | undefined;
	locationId: string;
	operatorId: string;
	location: MuseumLocation | undefined;
}