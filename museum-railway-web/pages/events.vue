
<template>
  <div class="grid w-full page-content">
    <div class="col-2 lg:col-3 flex flex-1 content-left-column sticky-sidebar-container" v-if="showFilterSidebar">
      <div class="w-full flex flex-column justify-content-center align-items-start overflow-hidden sticky-sidebar-container">
        <CustomSidebar class="w-full lg:w-11" style="height: 80%;" title="Filter" side="left">
          <EventFilters
              :filter-options="filterOptions"
              :filter-state="filterState"
              :filter-counts="filterCounts"
              :show-states="false"
              @update:filters="updateFilters"/>
        </CustomSidebar>
      </div>
    </div>
    <div class="flex flex-column align-items-center mx-2 col-12 lg:col-9 xl:col-6" :class="{ 'main-content-with-sidebar': viewport.isGreaterOrEquals('tablet') }">
      <Message class="my-2 mx-4" severity="info" icon="pi pi-info-circle"> Achtung! Die Daten werden automatisch erfasst
        und
        nicht manuell geprüft.
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter
        kontrollieren!
      </Message>
      <div class="h-1rem"></div>
      <div class="flex flex-column w-full h-full mx-2 mb-6 md:mx-5 align-items-center">
        <!-- Search Bar (kept in main content for visibility) -->
        <div class="top-filters w-full flex flex-column align-items-center mb-4">
          <InputGroup class="w-full flex mb-3">
            <InputGroupAddon>
              <i class="pi pi-search"></i>
            </InputGroupAddon>
            <InputText v-model="searchTerm" placeholder="Suche nach Veranstaltungen, Orten oder Beschreibungen"
                       class="w-full"/>
          </InputGroup>

          <!-- Filter Chips -->
          <div v-if="hasActiveFilters" class="w-full flex flex-wrap gap-2 mb-3">
            <!-- Search Term Chip -->
            <Chip v-if="filterState.search" 
                  :label="'Suche: ' + filterState.search" 
                  removable 
                  @remove="filterState.search = ''" />

            <!-- Date Range Chip -->
            <Chip v-if="filterState.fromDate || filterState.toDate" 
                  :label="getDateRangeLabel()" 
                  removable 
                  @remove="resetDateRange()" />

            <!-- State Chips -->
            <Chip v-for="stateCode in filterState.states" 
                  :key="'state-' + stateCode" 
                  :label="getStateName(stateCode)" 
                  removable 
                  @remove="toggleState(stateCode)" />

            <!-- Event Type Chips -->
            <Chip v-for="type in filterState.eventCategories"
                  :key="'event-' + type" 
                  :label="'Typ: ' + translateTag(type, EventCategoryLabels)"
                  removable 
                  @remove="removeEventType(type)" />

            <!-- Train Type Chips -->
            <Chip v-for="type in filterState.vehicleTypes"
                  :key="'train-' + type" 
                  :label="'Fahrzeug: ' + translateTag(type, VehicleTypeLabels)"
                  removable 
                  @remove="removeVehicleType(type)" />

            <!-- Volunteer/Commercial Chips -->
            <Chip v-if="filterState.volunteer" 
                  :v-label="translateTag(OperationType.VOLUNTEER, OperationTypeLabels)"
                  removable 
                  @remove="filterState.volunteer = false" />
            <Chip v-if="filterState.commercial"
                  :v-label="translateTag(OperationType.COMMERCIAL, OperationTypeLabels)"
                  removable 
                  @remove="filterState.commercial = false" />

            <!-- Tag Chips -->
            <Chip v-for="tag in filterState.tags" 
                  :key="'tag-' + tag" 
                  :label="'Tag: ' + tag" 
                  removable 
                  @remove="removeTag(tag)" />

            <!-- Clear All Button -->
            <Button v-if="hasActiveFilters" 
                    label="Alle Filter löschen" 
                    size="small" 
                    text 
                    class="ml-2" 
                    @click="clearAllFilters" />
          </div>

          <div class="w-full font-bold align-content-start mb-3">
            <label v-if="filteredEvents.length > 0">{{filteredEvents.length}} Events gefunden</label>
            <label v-if="filteredEvents.length === 0">keine Events gefunden</label>
          </div>

          <!-- State Filter (kept in main content for visibility) -->
          <div class="mb-3 flex flex-row flex-wrap justify-content-center"
               v-if="showFilterSidebar"
          >
            <div v-for="state in stateList" :key="state.code" class="mx-1 mb-2">
              <ToggleButton
                  :modelValue="selectedStates.includes(state.code)"
                  :onLabel="state.name"
                  :offLabel="state.name"
                  class="state-toggle-button"
                  @click="toggleState(state.code)">
                {{state.name}}
              <Badge
                  :value="getEventCountForState(state.code)"
                  severity="primary"
                  class="state-badge"/>
              </ToggleButton>
            </div>
          </div>
        </div>

        <EventList class="h-full" :eventsGroupedByMonthAndDeparture="filteredEventGroups"></EventList>
      </div>
    </div>
    <div class="col-2 lg:col-3 flex flex-1 content-left-column" v-if="showFilterSidebar">
    </div>
  </div>

  <!-- Mobile Footer -->
  <div class="mobile-footer" v-if="!showFilterSidebar">
    <div class="footer-buttons">
      <Button class="footer-button" @click="showFilterDrawer = true">
        <i class="pi pi-filter"></i>
        <span>Filter</span>
      </Button>
      <Button class="footer-button" @click="shareCurrentPage">
        <i class="pi pi-share-alt"></i>
        <span>Teilen</span>
      </Button>
    </div>
  </div>

  <!-- Mobile Filter Drawer -->
  <Sidebar v-model:visible="showFilterDrawer"
           position="bottom"
           class="filter-drawer"
           :modal="true"
           :dismissable="true"
           :showCloseIcon="true"
           :baseZIndex="1001">
    <template #header>
      <h2 class="text-xl font-bold m-0 p-3">Filter</h2>
    </template>
    <div class="p-3">
      <EventFilters
          :filter-options="filterOptions"
          :filter-state="filterState"
          :filter-counts="filterCounts"
          @update:filters="updateFilters"/>
    </div>
  </Sidebar>
</template>

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

// Mobile footer styles
.mobile-footer {
  height: 5rem;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: var(--color-umbragrau);
  display: flex;
  justify-content: center;
  align-items: center;
}

// Custom styles for the bottom drawer
.filter-drawer {
  height: 80% !important;
  overflow-y: auto;
  padding: 1rem;
}

// Style footer buttons
.footer-buttons {
  display: flex;
  justify-content: center;
  padding: 1rem;
  gap: 1rem;
  width: 100%;
}

.footer-button {
  width: 4rem;
  height: 4rem;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: opacity 0.2s;
  color: white;
  background: none;
  border: none;
}

.footer-button i {
  font-size: 1.5rem;
}

.footer-button span {
  font-size: 0.8rem;
}

.state-badge {
  min-width: 1.5rem;
  height: 1.5rem;
  font-size: 0.75rem;
}
</style>

<script setup lang="ts">
import {useAllEvents} from "~/composables/eventComposables";
import {useAllLocations} from "~/composables/locationComposables";
import {
  EventCategoryLabels,
  eventsGroupedByMonthAndDepartureTime,
  filterEvents,
  type MuseumEventGroupGroup, translateTag
} from "~/composables/eventDataFunctions";
import {getStateList, type StateInfo} from "~/composables/locationDataFunctions";
import EventFilters from "~/components/EventFilters.vue";
import CustomSidebar from "~/components/CustomSidebar.vue";
import { useToast } from 'primevue/usetoast';
import type { EventFilter, EventFilterOptions, EventFilterUpdate, StateOption, FilterCounts } from '~/types/EventFilterTypes';
import {MuseumEventCategory, OperationType, VehicleType} from "~/apiModel/apiModel";

const route = useRoute();
const router = useRouter();
const viewport = useViewport();
const toast = useToast();

const {data: events} = useAllEvents();
const {data: locations} = useAllLocations();

const showFilterSidebar = computed(() => viewport.isGreaterOrEquals('desktop'));

// Mobile drawer state
const showFilterDrawer = ref(false);

// Initialize filter state
const filterState = ref<EventFilter>({
  search: route.query.search?.toString() || '',
  states: route.query.states
      ? (Array.isArray(route.query.states)
          ? route.query.states.map(s => s.toString())
          : [route.query.states.toString()])
      : [],
  fromDate: route.query.fromDate ? new Date(route.query.fromDate.toString()) : undefined,
  toDate: route.query.toDate ? new Date(route.query.toDate.toString()) : undefined,
  eventCategories: route.query.eventCategories
      ? (Array.isArray(route.query.eventCategories)
          ? route.query.eventCategories.map(s => s.toString())
          : [route.query.eventCategories.toString()])
      : [],
  vehicleTypes: route.query.vehicleTypes
      ? (Array.isArray(route.query.vehicleTypes)
          ? route.query.vehicleTypes.map(s => s.toString())
          : [route.query.vehicleTypes.toString()])
      : [],
  volunteer: route.query.volunteer === 'true',
  commercial: route.query.commercial === 'true',
  tags: route.query.tags
      ? (Array.isArray(route.query.tags)
          ? route.query.tags.map(s => s.toString())
          : [route.query.tags.toString()])
      : []
});

// Computed properties for backward compatibility
const searchTerm = computed({
  get: () => filterState.value.search || '',
  set: (value: string) => filterState.value.search = value
});

const selectedStates = computed({
  get: () => filterState.value.states || [],
  set: (value: string[]) => filterState.value.states = value
});

const stateList = computed<StateInfo[]>(() => locations.value ? getStateList(locations.value) : []);

// Create filter options
const filterOptions = computed<EventFilterOptions>(() => ({
  states: stateList.value.map((state): StateOption => ({
    code: state.code,
    name: state.name
  })),
  eventCategories: Object.values(MuseumEventCategory),
  vehicleTypes: Object.values(VehicleType),
  tags: [
    'Dampflok', 'Diesellok', 'Elektrolok', 'Schienenbus', 'Triebwagen',
    'Nostalgiezug', 'Kinderprogramm', 'Führerstandsmitfahrt', 'Fotohalt'
  ]
}));

// Function to get event count for a specific state
const getEventCountForState = (stateCode: string): number => {
  if (!events.value || !locations.value) return 0;

  // Create a filter with only the search term and the specific state
  const tempFilter = {
    searchTerm: filterState.value.search || '',
    selectedStates: [stateCode], // Only this state
    allStates: stateList.value.map(state => state.code),
    dateRange: [filterState.value.fromDate, filterState.value.toDate].filter(Boolean) as Date[],
    eventCategories: filterState.value.eventCategories || [],
    vehicleTypes: filterState.value.vehicleTypes || [],
    isVolunteer: filterState.value.volunteer || false,
    isCommercial: filterState.value.commercial || false,
    tags: filterState.value.tags || []
  };

  return filterEvents(events.value, locations.value, tempFilter).length;
};

// Calculate counts for all filter options
const filterCounts = computed<FilterCounts>(() => {
  if (!events.value || !locations.value) {
    return {
      eventCategories: {},
      vehicleTypes: {},
      tags: {},
      states: {}
    };
  }

  const counts: FilterCounts = {
    eventCategories: {},
    vehicleTypes: {},
    tags: {},
    states: {}
  };

  // Initialize counts for all options
  filterOptions.value.eventCategories.forEach(type => {
    counts.eventCategories[type] = 0;
  });

  filterOptions.value.vehicleTypes.forEach(type => {
    counts.vehicleTypes[type] = 0;
  });

  filterOptions.value.tags.forEach(tag => {
    counts.tags[tag] = 0;
  });

  filterOptions.value.states.forEach(state => {
    counts.states[state.code] = 0;
  });

  // Only calculate if we have events and locations
  if (events.value && locations.value) {
    // Calculate counts for each option
    filterOptions.value.eventCategories.forEach(type => {
      const tempFilter = {
        searchTerm: filterState.value.search || '',
        selectedStates: filterState.value.states || [],
        allStates: stateList.value.map(state => state.code),
        dateRange: [filterState.value.fromDate, filterState.value.toDate].filter(Boolean) as Date[],
        eventCategories: [type], // Only this event type
        vehicleTypes: filterState.value.vehicleTypes || [],
        isVolunteer: filterState.value.volunteer || false,
        isCommercial: filterState.value.commercial || false,
        tags: filterState.value.tags || []
      };
      counts.eventCategories[type] = filterEvents(events.value, locations.value as any, tempFilter).length;
    });

    filterOptions.value.vehicleTypes.forEach(type => {
      const tempFilter = {
        searchTerm: filterState.value.search || '',
        selectedStates: filterState.value.states || [],
        allStates: stateList.value.map(state => state.code),
        dateRange: [filterState.value.fromDate, filterState.value.toDate].filter(Boolean) as Date[],
        eventCategories: filterState.value.eventCategories || [],
        vehicleTypes: [type], // Only this train type
        isVolunteer: filterState.value.volunteer || false,
        isCommercial: filterState.value.commercial || false,
        tags: filterState.value.tags || []
      };
      counts.vehicleTypes[type] = filterEvents(events.value, locations.value as any, tempFilter).length;
    });

    filterOptions.value.tags.forEach(tag => {
      const tempFilter = {
        searchTerm: filterState.value.search || '',
        selectedStates: filterState.value.states || [],
        allStates: stateList.value.map(state => state.code),
        dateRange: [filterState.value.fromDate, filterState.value.toDate].filter(Boolean) as Date[],
        eventCategories: filterState.value.eventCategories || [],
        vehicleTypes: filterState.value.vehicleTypes || [],
        isVolunteer: filterState.value.volunteer || false,
        isCommercial: filterState.value.commercial || false,
        tags: [tag] // Only this tag
      };
      counts.tags[tag] = filterEvents(events.value, locations.value as any, tempFilter).length;
    });

    // States are already calculated by getEventCountForState, but we'll store them here too
    filterOptions.value.states.forEach(state => {
      counts.states[state.code] = getEventCountForState(state.code);
    });
  }

  console.log(counts);

  return counts;
});

// Toggle state selection
const toggleState = (stateCode: string) => {
  const currentStates = filterState.value.states || [];
  if (currentStates.includes(stateCode)) {
    filterState.value.states = currentStates.filter(s => s !== stateCode);
  } else {
    filterState.value.states = [...currentStates, stateCode];
  }
};

// Handle filter updates from EventFilters component
const updateFilters = (filters: EventFilterUpdate) => {
  filterState.value = {
    ...filterState.value,
    fromDate: filters.dateRange.length > 0 ? filters.dateRange[0] : undefined,
    toDate: filters.dateRange.length > 1 ? filters.dateRange[1] : undefined,
    states: filters.states,
    eventCategories: filters.eventCategories,
    vehicleTypes: filters.vehicleTypes,
    volunteer: filters.isVolunteer,
    commercial: filters.isCommercial,
    tags: filters.tags
  };
};

// Update query parameters when filters change
watch(filterState, (newFilter) => {
  // Only run on client-side to avoid SSR issues
  if (import.meta.client) {
    const query: Record<string, string | string[]> = {};

    if (newFilter.search) {
      query.search = newFilter.search;
    }
    if (newFilter.states && newFilter.states.length > 0) {
      query.states = newFilter.states;
    }
    if (newFilter.fromDate) {
      query.fromDate = newFilter.fromDate.toISOString();
    }
    if (newFilter.toDate) {
      query.toDate = newFilter.toDate.toISOString();
    }
    if (newFilter.eventCategories && newFilter.eventCategories.length > 0) {
      query.eventCategories = newFilter.eventCategories;
    }
    if (newFilter.vehicleTypes && newFilter.vehicleTypes.length > 0) {
      query.vehicleTypes = newFilter.vehicleTypes;
    }
    if (newFilter.volunteer) {
      query.volunteer = 'true';
    }
    if (newFilter.commercial) {
      query.commercial = 'true';
    }
    if (newFilter.tags && newFilter.tags.length > 0) {
      query.tags = newFilter.tags;
    }

    // Update the URL without reloading the page
    router.replace({query});
  }
}, {deep: true});

// Filter events using the centralized filter object
const filteredEvents = computed(() => {
  if (!events.value) return [];

  return filterEvents(
      events.value ?? [],
      locations.value ?? [],
      {
        searchTerm: filterState.value.search || '',
        selectedStates: filterState.value.states || [],
        allStates: stateList.value.map(state => state.code),
        dateRange: [filterState.value.fromDate, filterState.value.toDate].filter(Boolean) as Date[],
        eventCategories: filterState.value.eventCategories || [],
        vehicleTypes: filterState.value.vehicleTypes || [],
        isVolunteer: filterState.value.volunteer || false,
        isCommercial: filterState.value.commercial || false,
        tags: filterState.value.tags || []
      }
  );
});

// Group filtered events
const filteredEventGroups = computed<MuseumEventGroupGroup[]>(() => {
  return filteredEvents.value.length > 0 ? eventsGroupedByMonthAndDepartureTime(filteredEvents.value) : [];
});

// Check if any filters are active
const hasActiveFilters = computed(() => {
  return !!(
    filterState.value.search || 
    (filterState.value.states && filterState.value.states.length > 0) ||
    filterState.value.fromDate ||
    filterState.value.toDate ||
    (filterState.value.eventCategories && filterState.value.eventCategories.length > 0) ||
    (filterState.value.vehicleTypes && filterState.value.vehicleTypes.length > 0) ||
    filterState.value.volunteer ||
    filterState.value.commercial ||
    (filterState.value.tags && filterState.value.tags.length > 0)
  );
});

// Format date range for display in chip
const getDateRangeLabel = (): string => {
  if (filterState.value.fromDate && filterState.value.toDate) {
    const fromDate = new Date(filterState.value.fromDate).toLocaleDateString('de-DE');
    const toDate = new Date(filterState.value.toDate).toLocaleDateString('de-DE');
    return `Zeitraum: ${fromDate} - ${toDate}`;
  } else if (filterState.value.fromDate) {
    const fromDate = new Date(filterState.value.fromDate).toLocaleDateString('de-DE');
    return `Ab: ${fromDate}`;
  } else if (filterState.value.toDate) {
    const toDate = new Date(filterState.value.toDate).toLocaleDateString('de-DE');
    return `Bis: ${toDate}`;
  }
  return '';
};

// Reset date range filter
const resetDateRange = () => {
  filterState.value.fromDate = undefined;
  filterState.value.toDate = undefined;
};

// Get state name from state code
const getStateName = (stateCode: string): string => {
  const state = stateList.value.find(s => s.code === stateCode);
  return state ? state.name : stateCode;
};

// Remove event type filter
const removeEventType = (type: string) => {
  if (filterState.value.eventCategories) {
    const neweventCategories: string[] = [];
    for (const t of filterState.value.eventCategories) {
      if (t !== type) {
        neweventCategories.push(t);
      }
    }
    filterState.value.eventCategories = neweventCategories;
  }
};

// Remove train type filter
const removeVehicleType = (type: string) => {
  if (filterState.value.vehicleTypes) {
    const newvehicleTypes: string[] = [];
    for (const t of filterState.value.vehicleTypes) {
      if (t !== type) {
        newvehicleTypes.push(t);
      }
    }
    filterState.value.vehicleTypes = newvehicleTypes;
  }
};

// Remove tag filter
const removeTag = (tag: string) => {
  if (filterState.value.tags) {
    const newTags: string[] = [];
    for (const t of filterState.value.tags) {
      if (t !== tag) {
        newTags.push(t);
      }
    }
    filterState.value.tags = newTags;
  }
};

// Clear all filters
const clearAllFilters = () => {
  filterState.value = {
    search: '',
    states: [],
    fromDate: undefined,
    toDate: undefined,
    eventCategories: [],
    vehicleTypes: [],
    volunteer: false,
    commercial: false,
    tags: []
  };
};

// Share functionality (keeping existing implementation)
const shareCurrentPage = () => {
  // Only run on client-side
  if (import.meta.client) {
    // Get the current URL with query parameters
    const currentUrl = window.location.href;

    // Check if the Web Share API is available
    if (navigator.share) {
      navigator.share({
        title: 'Museumsbahn Events',
        url: currentUrl
      })
          .catch(error => {
            console.error('Error sharing:', error);
            // Fall back to clipboard if sharing fails
            copyToClipboard(currentUrl, toast);
          });
    } else {
      // Web Share API not available, use clipboard
      copyToClipboard(currentUrl, toast);
    }
  }
};

const copyToClipboard = (text: string, toast: any) => {
  // Check if Clipboard API is available
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(text)
        .then(() => {
          // Show success message using PrimeVue Toast
          toast.add({
            severity: 'success',
            summary: 'Link kopiert',
            detail: 'Der Link wurde in die Zwischenablage kopiert.',
            life: 3000
          });
        })
        .catch(err => {
          console.error('Failed to copy text: ', err);
          showFallbackCopyMessage(text, toast);
        });
  } else {
    // Clipboard API not available, show manual copy message
    showFallbackCopyMessage(text, toast);
  }
};

const showFallbackCopyMessage = (text: string, toast: any) => {
  // Show message with the URL for manual copying
  toast.add({
    severity: 'info',
    summary: 'Link teilen',
    detail: 'Bitte kopieren Sie den Link manuell: ' + text,
    life: 5000
  });
};

useSeoMeta({
  title: 'Veranstaltungsliste',
  ogTitle: 'Veranstaltungsliste',
  description: 'Hier finden sich Veranstaltungen und Sonderfahrten von österreichs Museumsbahnen.',
  ogDescription: 'Finde Veranstaltungen und Sonderfahrten von Museumsbahnen in deiner Nähe.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})
</script>
