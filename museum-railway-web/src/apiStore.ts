import {LocationsResourceApi} from 'museum-railway-api-client';

export class ApiStore {
	public locationsApi: LocationsResourceApi;
}

// somehow we need to pass an empty base url to correctly work with vue proxy ¯\_(ツ)_/¯
const baseUrl = "";

const apiStore: ApiStore = {
	locationsApi: new LocationsResourceApi(undefined, baseUrl)
};

export default apiStore;