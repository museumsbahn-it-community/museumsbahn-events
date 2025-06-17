# Replacing Pinia Stores with Composables and useAsyncData

## Current Status

The project currently uses both approaches:

1. **Pinia Stores**: Used in most of the application
   - LocationsStore.ts
   - EventsStore.ts
   - GlobalConfigStore.ts

2. **Composables with useAsyncData**: Implemented but not widely used
   - useLocations.ts
   - useEvents.ts
   - useGlobalConfig.ts
   - Used in events-with-composables.vue (experimental page)

## Implementation Comparison

### Pinia Store Example (LocationsStore.ts)
```typescript
export const useLocationsStore = defineStore('locations', {
    state: (): LocationsState => ({
        locations: [],
        locationsFetched: false,
    }),
    getters: {
        allLocations(state): MuseumLocation[] {
            return state.locations;
        },
        // Other getters...
    },
    actions: {
        async fetchLocations(): Promise<MuseumLocation[]> {
            if (this.locationsFetched) {
                return this.locations;
            }
            const {$museumRailwayBackendApi} = useNuxtApp()
            this.locations = await $museumRailwayBackendApi('/api/location', {})
                .catch(e => console.error("error loading locations: ",e));
                
            this.locationsFetched = true;
            return this.locations;
        },
    },
});
```

### Composable Example (useLocations.ts)
```typescript
export function useLocations() {
  // Create reactive state for locations
  const locations = useState<MuseumLocation[]>('locations', () => []);
  const locationsFetched = useState<boolean>('locationsFetched', () => false);

  // Function to fetch locations
  const { data, pending, refresh, error } = useAsyncData(
    'locations',
    async () => {
      if (locationsFetched.value) {
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
      server: true,
      lazy: false,
    }
  );

  // Computed properties (equivalent to getters)
  const allLocations = computed(() => locations.value);
  // Other computed properties...

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
    // Other properties...
  };
}
```

## Usage Comparison

### Using Pinia Store (events.vue)
```typescript
const locationsStore = useLocationsStore();
const eventsStore = useEventsStore();
await useAsyncData('locations', () => locationsStore.fetchLocations());
await useAsyncData('events', () => eventsStore.fetchAllEvents());

const eventGroups = useState('filtered-events', () => eventsStore.filteredEventsGroupedByMonthAndDepartureTime);
```

### Using Composables (events-with-composables.vue)
```typescript
const { allLocations } = useLocations();
const { filteredEventsGroupedByMonthAndDepartureTime, allEventsData, allEventsPending } = useEvents();

const eventGroups = useState('filtered-events', () => filteredEventsGroupedByMonthAndDepartureTime.value);
```

## Benefits of Using Composables with useAsyncData

1. **Simpler API**: The composable approach provides a cleaner, more intuitive API for components to consume.

2. **Built-in Nuxt Features**: Using useAsyncData leverages Nuxt's built-in data fetching capabilities, including:
   - Automatic server-side rendering support
   - Caching
   - Loading states (pending, error)
   - Refresh functionality

3. **Reduced Boilerplate**: Less code needed in components to fetch and manage data.

4. **Better TypeScript Support**: Composables can provide better type inference and type safety.

5. **Easier Testing**: Composables are typically easier to test than Pinia stores.

6. **No External Dependencies**: Using Nuxt's built-in composables reduces external dependencies.

## Drawbacks of Using Composables with useAsyncData

1. **Migration Effort**: Requires updating all components that currently use Pinia stores.

2. **Learning Curve**: Team members need to learn the composable pattern if they're more familiar with Pinia.

3. **Less Structured**: Pinia provides a more structured approach to state management with clear separation of state, getters, and actions.

4. **Potential for Duplication**: Without careful design, composables might lead to duplicated state or logic.

5. **Debugging**: Pinia DevTools integration might be lost when moving to composables.

## Recommendation

Based on the analysis, replacing Pinia stores with composables and useAsyncData is a viable approach that can simplify the codebase and leverage Nuxt's built-in features. The project already has working implementations of both approaches, with the composable approach showing cleaner component code.

### Migration Strategy

1. Continue using both approaches in parallel during the transition
2. Update components one by one to use the composable approach
3. Once all components are migrated, remove the Pinia stores
4. Update tests and documentation to reflect the new approach

This gradual approach minimizes risk and allows for proper testing at each step.