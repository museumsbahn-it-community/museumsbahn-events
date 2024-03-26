import {LocationsControllerApi} from '@museumrailwayevents/museum-railway-client';
import { SearchControllerApi } from '@boudicca/search-api-client/api.ts';

export interface ApiStore {
	locationsApi: LocationsControllerApi;
	boudiccaSearchApi: SearchControllerApi;
}

// somehow we need to pass an empty base url to correctly work with vue proxy ¯\_(ツ)_/¯
const baseUrl = "";

const apiStore: ApiStore = {
	locationsApi: new LocationsControllerApi(undefined, baseUrl),
	boudiccaSearchApi: new SearchControllerApi(undefined, baseUrl + "/searchApi"),
};

export default apiStore;