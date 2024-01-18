import { LocationsResourceApi } from '@museumrailwayevents/museum-railway-client';
import { SearchControllerApi } from '@boudicca/search-api-client/api.ts';

export interface ApiStore {
	locationsApi: LocationsResourceApi;
	boudiccaSearchApi: SearchControllerApi;
}

// somehow we need to pass an empty base url to correctly work with vue proxy ¯\_(ツ)_/¯
const baseUrl = "";

const apiStore: ApiStore = {
	locationsApi: new LocationsResourceApi(undefined, baseUrl),
	boudiccaSearchApi: new SearchControllerApi(undefined, baseUrl + "/searchApi"),
};

export default apiStore;