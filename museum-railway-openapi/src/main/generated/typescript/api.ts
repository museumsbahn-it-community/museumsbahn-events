/* tslint:disable */
/* eslint-disable */
/**
 * museum-railway-backend
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 0.2.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import type { Configuration } from './configuration';
import type { AxiosPromise, AxiosInstance, RawAxiosRequestConfig } from 'axios';
import globalAxios from 'axios';
// Some imports not used depending on template conditions
// @ts-ignore
import { DUMMY_BASE_URL, assertParamExists, setApiKeyToObject, setBasicAuthToObject, setBearerAuthToObject, setOAuthToObject, setSearchParams, serializeDataIfNeeded, toPathString, createRequestFunction } from './common';
import type { RequestArgs } from './base';
// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS, BaseAPI, RequiredError, operationServerMap } from './base';

/**
 * 
 * @export
 * @interface Location
 */
export interface Location {
    /**
     * 
     * @type {number}
     * @memberof Location
     */
    'lat'?: number;
    /**
     * 
     * @type {number}
     * @memberof Location
     */
    'lon'?: number;
    /**
     * 
     * @type {string}
     * @memberof Location
     */
    'country': string;
    /**
     * 
     * @type {string}
     * @memberof Location
     */
    'state': string;
    /**
     * 
     * @type {string}
     * @memberof Location
     */
    'street': string;
    /**
     * 
     * @type {string}
     * @memberof Location
     */
    'city'?: string;
    /**
     * 
     * @type {string}
     * @memberof Location
     */
    'zipCode'?: string;
}
/**
 * 
 * @export
 * @interface MuseumLocation
 */
export interface MuseumLocation {
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'name': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'type': MuseumLocationTypeEnum;
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'operatorId': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'locationId': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'webUrl': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'description': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'imageName': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'eventCollectorType': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumLocation
     */
    'eventCollectionComment': string;
    /**
     * 
     * @type {Location}
     * @memberof MuseumLocation
     */
    'location': Location;
}

export const MuseumLocationTypeEnum = {
    Museum: 'Museum',
    Modellbahn: 'Modellbahn',
    Veranstaltung: 'Veranstaltung',
    Museumsbahn: 'Museumsbahn',
    Sonderfahrten: 'Sonderfahrten',
    Station: 'Station'
} as const;

export type MuseumLocationTypeEnum = typeof MuseumLocationTypeEnum[keyof typeof MuseumLocationTypeEnum];

/**
 * 
 * @export
 * @interface MuseumOperator
 */
export interface MuseumOperator {
    /**
     * 
     * @type {string}
     * @memberof MuseumOperator
     */
    'name': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumOperator
     */
    'identifier': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumOperator
     */
    'webUrl': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumOperator
     */
    'description': string;
    /**
     * 
     * @type {string}
     * @memberof MuseumOperator
     */
    'imageName': string;
    /**
     * 
     * @type {Location}
     * @memberof MuseumOperator
     */
    'location': Location;
}

/**
 * ImageCachingProxyControllerApi - axios parameter creator
 * @export
 */
export const ImageCachingProxyControllerApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * 
         * @param {string} url 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getImage: async (url: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'url' is not null or undefined
            assertParamExists('getImage', 'url', url)
            const localVarPath = `/imgcache`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            if (url !== undefined) {
                localVarQueryParameter['url'] = url;
            }


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * ImageCachingProxyControllerApi - functional programming interface
 * @export
 */
export const ImageCachingProxyControllerApiFp = function(configuration?: Configuration) {
    const localVarAxiosParamCreator = ImageCachingProxyControllerApiAxiosParamCreator(configuration)
    return {
        /**
         * 
         * @param {string} url 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async getImage(url: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<string>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.getImage(url, options);
            const index = configuration?.serverIndex ?? 0;
            const operationBasePath = operationServerMap['ImageCachingProxyControllerApi.getImage']?.[index]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, operationBasePath || basePath);
        },
    }
};

/**
 * ImageCachingProxyControllerApi - factory interface
 * @export
 */
export const ImageCachingProxyControllerApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    const localVarFp = ImageCachingProxyControllerApiFp(configuration)
    return {
        /**
         * 
         * @param {string} url 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getImage(url: string, options?: any): AxiosPromise<string> {
            return localVarFp.getImage(url, options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * ImageCachingProxyControllerApi - object-oriented interface
 * @export
 * @class ImageCachingProxyControllerApi
 * @extends {BaseAPI}
 */
export class ImageCachingProxyControllerApi extends BaseAPI {
    /**
     * 
     * @param {string} url 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ImageCachingProxyControllerApi
     */
    public getImage(url: string, options?: RawAxiosRequestConfig) {
        return ImageCachingProxyControllerApiFp(this.configuration).getImage(url, options).then((request) => request(this.axios, this.basePath));
    }
}



/**
 * LocationsControllerApi - axios parameter creator
 * @export
 */
export const LocationsControllerApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * 
         * @param {string} [operatorId] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        allLocations: async (operatorId?: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            const localVarPath = `/api/location`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            if (operatorId !== undefined) {
                localVarQueryParameter['operatorId'] = operatorId;
            }


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * LocationsControllerApi - functional programming interface
 * @export
 */
export const LocationsControllerApiFp = function(configuration?: Configuration) {
    const localVarAxiosParamCreator = LocationsControllerApiAxiosParamCreator(configuration)
    return {
        /**
         * 
         * @param {string} [operatorId] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async allLocations(operatorId?: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<Array<MuseumLocation>>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.allLocations(operatorId, options);
            const index = configuration?.serverIndex ?? 0;
            const operationBasePath = operationServerMap['LocationsControllerApi.allLocations']?.[index]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, operationBasePath || basePath);
        },
    }
};

/**
 * LocationsControllerApi - factory interface
 * @export
 */
export const LocationsControllerApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    const localVarFp = LocationsControllerApiFp(configuration)
    return {
        /**
         * 
         * @param {string} [operatorId] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        allLocations(operatorId?: string, options?: any): AxiosPromise<Array<MuseumLocation>> {
            return localVarFp.allLocations(operatorId, options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * LocationsControllerApi - object-oriented interface
 * @export
 * @class LocationsControllerApi
 * @extends {BaseAPI}
 */
export class LocationsControllerApi extends BaseAPI {
    /**
     * 
     * @param {string} [operatorId] 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof LocationsControllerApi
     */
    public allLocations(operatorId?: string, options?: RawAxiosRequestConfig) {
        return LocationsControllerApiFp(this.configuration).allLocations(operatorId, options).then((request) => request(this.axios, this.basePath));
    }
}



/**
 * ManagementControllerApi - axios parameter creator
 * @export
 */
export const ManagementControllerApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        clearCaches: async (options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            const localVarPath = `/api/manage/reloadData`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'POST', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * ManagementControllerApi - functional programming interface
 * @export
 */
export const ManagementControllerApiFp = function(configuration?: Configuration) {
    const localVarAxiosParamCreator = ManagementControllerApiAxiosParamCreator(configuration)
    return {
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async clearCaches(options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<void>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.clearCaches(options);
            const index = configuration?.serverIndex ?? 0;
            const operationBasePath = operationServerMap['ManagementControllerApi.clearCaches']?.[index]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, operationBasePath || basePath);
        },
    }
};

/**
 * ManagementControllerApi - factory interface
 * @export
 */
export const ManagementControllerApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    const localVarFp = ManagementControllerApiFp(configuration)
    return {
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        clearCaches(options?: any): AxiosPromise<void> {
            return localVarFp.clearCaches(options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * ManagementControllerApi - object-oriented interface
 * @export
 * @class ManagementControllerApi
 * @extends {BaseAPI}
 */
export class ManagementControllerApi extends BaseAPI {
    /**
     * 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ManagementControllerApi
     */
    public clearCaches(options?: RawAxiosRequestConfig) {
        return ManagementControllerApiFp(this.configuration).clearCaches(options).then((request) => request(this.axios, this.basePath));
    }
}



/**
 * OperatorsControllerApi - axios parameter creator
 * @export
 */
export const OperatorsControllerApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        allOperators: async (options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            const localVarPath = `/api/operator`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * OperatorsControllerApi - functional programming interface
 * @export
 */
export const OperatorsControllerApiFp = function(configuration?: Configuration) {
    const localVarAxiosParamCreator = OperatorsControllerApiAxiosParamCreator(configuration)
    return {
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async allOperators(options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<Array<MuseumOperator>>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.allOperators(options);
            const index = configuration?.serverIndex ?? 0;
            const operationBasePath = operationServerMap['OperatorsControllerApi.allOperators']?.[index]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, operationBasePath || basePath);
        },
    }
};

/**
 * OperatorsControllerApi - factory interface
 * @export
 */
export const OperatorsControllerApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    const localVarFp = OperatorsControllerApiFp(configuration)
    return {
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        allOperators(options?: any): AxiosPromise<Array<MuseumOperator>> {
            return localVarFp.allOperators(options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * OperatorsControllerApi - object-oriented interface
 * @export
 * @class OperatorsControllerApi
 * @extends {BaseAPI}
 */
export class OperatorsControllerApi extends BaseAPI {
    /**
     * 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof OperatorsControllerApi
     */
    public allOperators(options?: RawAxiosRequestConfig) {
        return OperatorsControllerApiFp(this.configuration).allOperators(options).then((request) => request(this.axios, this.basePath));
    }
}



