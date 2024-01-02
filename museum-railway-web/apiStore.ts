import {LocationsResourceApi} from 'museum-railway-api-client';
import {SearchApiApi} from 'boudicca-search-api-client';

export interface ApiStore {
	locationsApi: LocationsResourceApi;
	boudiccaSearchApi: SearchApiApi;
}

// somehow we need to pass an empty base url to correctly work with vue proxy ¯\_(ツ)_/¯
const baseUrl = "";

const apiStore: ApiStore = {
	locationsApi: new LocationsResourceApi(undefined, baseUrl),
	boudiccaSearchApi: new SearchApiApi(undefined, baseUrl + "/searchApi"),
};

export default apiStore;