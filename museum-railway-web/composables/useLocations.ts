import { useNuxtApp } from 'nuxt/app';
import { useAsyncData, useState, computed } from '#app';

export function useLocations() {
  // Create reactive state for locations
  const locations = useState<MuseumLocation[]>('locations', () => []);
  const locationsFetched = useState<boolean>('locationsFetched', () => false);

  // Function to fetch locations
  const { data, pending, refresh, error } = useAsyncData(
    'locations',
    async () => {
      if (locationsFetched.value) {
        // location data changes almost never, so there is no need to
        // refetch it everytime the user navigates within the app
        return locations.value;
      }

      const { $museumRailwayBackendApi } = useNuxtApp();
      const fetchedLocations = await $museumRailwayBackendApi('/api/location', {})
        .catch(e => {
          console.error("error loading locations: ", e);
          return [];
        });

      locations.value = fetchedLocations;
      locationsFetched.value = true;
      return fetchedLocations;
    },
    {
      // Cache the result to avoid unnecessary refetches
      server: true,
      lazy: false,
    }
  );

  // Computed properties (equivalent to getters)
  const allLocations = computed(() => locations.value);

  const stateList = computed(() => {
    const mappedStates = locations.value.map((value) => value.location.state);
    return [...new Set(mappedStates)].sort((a, b) => a.localeCompare(b));
  });

  const locationById = (locationId: string): MuseumLocation | undefined => {
    const location = locations.value.filter((location) => location.locationId === locationId);
    if (location.length > 0) {
      return location[0];
    } else {
      return undefined;
    }
  };

  return {
    // State
    locations,
    locationsFetched,

    // AsyncData properties
    pending,
    error,
    refresh,

    // Computed properties (getters)
    allLocations,
    stateList,
    locationById,
  };
}
