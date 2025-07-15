<style lang="scss">
@use "../assets/variables_impl.scss" as variables;

.top-filters {
  max-width: 40rem;
}

.content-left-column {
  flex: auto 2 1;
  padding: 0;
  overflow: hidden;
  height: variables.$min-content-height;
}

.sticky-sidebar-container {
  position: sticky;
  top: variables.$navbar-height;
}

// Add margin to main content to prevent overlap with fixed sidebar
.main-content-with-sidebar {
  margin-left: 16.67%; // Equivalent to col-2 width
}

@media (min-width: 992px) { // lg breakpoint
  .main-content-with-sidebar {
    margin-left: 25%; // Equivalent to col-3 width
  }
}
</style>
<template>
  <div class="grid w-full">
    <div class="col-2 lg:col-3 flex flex-1 content-left-column sticky-sidebar-container" v-if="viewport.isGreaterOrEquals('tablet')">
      <div class="w-full flex flex-column justify-content-center align-items-start overflow-hidden sticky-sidebar-container">
        <CustomSidebar class="w-full lg:w-11" style="height: 80%;" title="Filter" side="left">
          <EventFilters
              :availableTags="availableTags"
              @update:filters="updateFilters"/>
        </CustomSidebar>
      </div>
    </div>
    <div class="flex flex-column align-items-center mx-2 col-12 md:col-9 lg:col-6" :class="{ 'main-content-with-sidebar': viewport.isGreaterOrEquals('tablet') }">
      <Message class="my-2 mx-4" severity="info" icon="pi pi-info-circle"> Achtung! Die Daten werden automatisch erfasst
        und
        nicht manuell geprüft.
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter
        kontrollieren!
      </Message>
      <div class="h-1rem"></div>
      <div class="flex flex-column h-full mx-2 mb-6 md:mx-5 align-items-center">
        <!-- Search Bar (kept in main content for visibility) -->
        <div class="top-filters w-full flex flex-column align-items-center mb-4">
          <InputGroup class="w-full flex mb-3">
            <InputGroupAddon>
              <i class="pi pi-search"></i>
            </InputGroupAddon>
            <InputText v-model="searchTerm" placeholder="Suche nach Veranstaltungen, Orten oder Beschreibungen"
                       class="w-full"/>
          </InputGroup>

          <!-- Mobile Filters -->
          <CustomSidebar class="w-full mb-3" v-if="viewport.isLessThan('tablet')" title="Filter" side="center">
            <EventFilters
                :availableTags="availableTags"
                @update:filters="updateFilters"/>
          </CustomSidebar>

          <!-- State Filter (kept in main content for visibility) -->
          <div class="mb-3 flex flex-row flex-wrap justify-content-center">
            <ToggleButton v-for="state in stateList" :key="state.code"
                          :modelValue="selectedStates.includes(state.code)"
                          :onLabel="state.name"
                          :offLabel="state.name"
                          class="mx-1 mb-2"
                          @click="toggleState(state.code)"/>
          </div>
        </div>

        <EventList class="h-full" :eventsGroupedByMonthAndDeparture="filteredEventGroups"></EventList>
      </div>
    </div>
    <div class="col-2 lg:col-3 flex flex-1 content-left-column" v-if="viewport.isGreaterOrEquals('tablet')">
    </div>
  </div>
</template>
<script setup lang="ts">
import {useAllEvents} from "~/composables/eventComposables";
import {useAllLocations} from "~/composables/locationComposables";
import {
  eventsGroupedByMonthAndDepartureTime,
  filterEvents,
  type MuseumEventGroupGroup
} from "~/composables/eventDataFunctions";
import {getStateList, type StateInfo} from "~/composables/locationDataFunctions";
import EventFilters from "~/components/EventFilters.vue";
import CustomSidebar from "~/components/CustomSidebar.vue";

const route = useRoute();
const router = useRouter();
const viewport = useViewport();

const {data: events} = useAllEvents();
const {data: locations} = useAllLocations();

// Initialize from query parameters if available
const searchTerm = ref(route.query.search?.toString() || '');
const selectedStates = ref<string[]>(
    route.query.states
        ? (Array.isArray(route.query.states)
            ? route.query.states.map(s => s.toString())
            : [route.query.states.toString()])
        : []
);
const stateList = computed<StateInfo[]>(() => locations.value ? getStateList(locations.value) : []);

// Advanced filter states
const dateRange = ref<[Date | null, Date | null]>([null, null]);
const selectedEventTypes = ref<string[]>([]);
const selectedTrainTypes = ref<string[]>([]);
const isVolunteer = ref(false);
const isCommercial = ref(false);
const selectedTags = ref<string[]>([]);

// Sample tags - in a real application, these would come from the backend
const availableTags = ref<string[]>([
  'Dampflok', 'Diesellok', 'Elektrolok', 'Schienenbus', 'Triebwagen',
  'Nostalgiezug', 'Kinderprogramm', 'Führerstandsmitfahrt', 'Fotohalt'
]);

// Toggle state selection
const toggleState = (stateCode: string) => {
  if (selectedStates.value.includes(stateCode)) {
    selectedStates.value = selectedStates.value.filter(s => s !== stateCode);
  } else {
    selectedStates.value.push(stateCode);
  }
};

// Handle filter updates from EventFilters component
const updateFilters = (filters: {
  dateRange: [Date | null, Date | null],
  eventTypes: string[],
  trainTypes: string[],
  isVolunteer: boolean,
  isCommercial: boolean,
  tags: string[]
}) => {
  dateRange.value = filters.dateRange;
  selectedEventTypes.value = filters.eventTypes;
  selectedTrainTypes.value = filters.trainTypes;
  isVolunteer.value = filters.isVolunteer;
  isCommercial.value = filters.isCommercial;
  selectedTags.value = filters.tags;
};

// Update query parameters when search term or selected states change
watch([searchTerm, selectedStates, dateRange, selectedEventTypes, selectedTrainTypes, isVolunteer, isCommercial, selectedTags],
    ([newSearchTerm, newSelectedStates, newDateRange, newEventTypes, newTrainTypes, newIsVolunteer, newIsCommercial, newTags]) => {
      // Only run on client-side to avoid SSR issues
      if (import.meta.client) {
        const query: {
          search?: string,
          states?: string[],
          fromDate?: string,
          toDate?: string,
          eventTypes?: string[],
          trainTypes?: string[],
          volunteer?: string,
          commercial?: string,
          tags?: string[]
        } = {};

        if (newSearchTerm) {
          query.search = newSearchTerm;
        }

        if (newSelectedStates.length > 0) {
          query.states = newSelectedStates;
        }

        if (newDateRange[0]) {
          query.fromDate = newDateRange[0].toISOString();
        }

        if (newDateRange[1]) {
          query.toDate = newDateRange[1].toISOString();
        }

        if (newEventTypes.length > 0) {
          query.eventTypes = newEventTypes;
        }

        if (newTrainTypes.length > 0) {
          query.trainTypes = newTrainTypes;
        }

        if (newIsVolunteer) {
          query.volunteer = 'true';
        }

        if (newIsCommercial) {
          query.commercial = 'true';
        }

        if (newTags.length > 0) {
          query.tags = newTags;
        }

        // Update the URL without reloading the page
        router.replace({query});
      }
    }, {deep: true});

// Filter events by all filter criteria
const filteredEvents = computed(() => {
  if (!events.value) return [];

  let filtered = filterEvents(
      events.value ?? [],
      locations.value ?? [],
      {
        searchTerm: searchTerm.value,
        selectedStates: selectedStates.value,
        allStates: stateList.value.map(state => state.code)
      }
  );

  // Apply date range filter
  if (dateRange.value[0]) {
    const fromDate = dateRange.value[0];
    filtered = filtered.filter(event => new Date(event.date) >= fromDate);
  }

  if (dateRange.value[1]) {
    const toDate = dateRange.value[1];
    filtered = filtered.filter(event => new Date(event.date) <= toDate);
  }

  // Apply event type filter (if any selected, otherwise show all)
  if (selectedEventTypes.value.length > 0) {
    filtered = filtered.filter(event =>
        selectedEventTypes.value.includes(event.eventCategory) || !event.eventCategory
    );
  }

  // Apply train type filter (if any selected, otherwise show all)
  if (selectedTrainTypes.value.length > 0) {
    filtered = filtered.filter(event =>
        selectedTrainTypes.value.includes(event.locomotiveType) || !event.locomotiveType
    );
  }

  // Apply volunteer/commercial filters
  // Note: This is a placeholder implementation since the actual data model doesn't have these fields
  // In a real implementation, you would filter based on actual data fields
  if (isVolunteer.value && !isCommercial.value) {
    // Only show volunteer events
    filtered = filtered.filter(event => event.name.toLowerCase().includes('ehrenamtlich') ||
        (event.description && event.description.toLowerCase().includes('ehrenamtlich')));
  } else if (!isVolunteer.value && isCommercial.value) {
    // Only show commercial events
    filtered = filtered.filter(event => event.name.toLowerCase().includes('kommerziell') ||
        (event.description && event.description.toLowerCase().includes('kommerziell')));
  }

  // Apply tag filters
  if (selectedTags.value.length > 0) {
    filtered = filtered.filter(event => {
      // Check if any of the selected tags are in the event name or description
      return selectedTags.value.some(tag =>
          event.name.toLowerCase().includes(tag.toLowerCase()) ||
          (event.description && event.description.toLowerCase().includes(tag.toLowerCase()))
      );
    });
  }

  return filtered;
});

// Group filtered events
const filteredEventGroups = computed<MuseumEventGroupGroup[]>(() => {
  return filteredEvents.value.length > 0 ? eventsGroupedByMonthAndDepartureTime(filteredEvents.value) : [];
});

useSeoMeta({
  title: 'Veranstaltungsliste',
  ogTitle: 'Veranstaltungsliste',
  description: 'Hier finden sich Veranstaltungen und Sonderfahrten von österreichs Museumsbahnen.',
  ogDescription: 'Finde Veranstaltungen und Sonderfahrten von Museumsbahnen in deiner Nähe.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})

</script>
