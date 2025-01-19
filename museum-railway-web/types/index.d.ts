declare global {

    // TODO: this should be generated by the openfetch client; not?
    interface Location {
        lat: number | undefined,
        lon: number | undefined,
        country: string,
        state: string,
        street: string,
        city: string | undefined,
        zipCode: string | undefined,
    }

    // TODO: this should be generated by the openfetch client; not?
    interface MuseumLocation {
        name: string;
        type: string;
        operatorId: string;
        locationId: string;
        webUrl: string;
        description: string;
        imageName: string;
        eventCollectorType: string;
        eventCollectionComment: string;
        location: Location;
    }

    // TODO: this should be generated by the openfetch client; not?
    interface MuseumEvent {
        name: string;
        description: string;
        eventCategory: string;
        date: Date;
        url: string;
        pictureUrl: string | undefined;
        pictureAltText: string | undefined;
        pictureCopyright: string | undefined;
        locomotiveType: string | undefined;
        locationId: string;
        operatorId: string;
        location: MuseumLocation | undefined;
    }

    interface EventFilterSettings {
        fromDate: Date | undefined,
        toDate: Date | undefined,
        tagFilters: EventTagFilterOption[],
        locationId?: string | undefined
    }

    interface EventTagFilterOption {
        key: string,
        options: string[],
    }

    interface FiltersResponse {
        [index: string]: string[];
    }

}

export {};