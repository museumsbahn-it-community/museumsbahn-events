<style lang="scss">
@use "../assets/colors" as colors;
@use "../assets/variables_impl" as variables;

.filter-box {
  width: 100%;
  height: 100%;
  background: colors.$color-umbragrau;
  clip-path: polygon(0% 0%, 100% 0%, 88.779% 89.148%, 88.779% 89.148%, 88.506% 91.115%, 88.208% 92.904%, 87.887% 94.509%, 87.544% 95.924%, 87.182% 97.14%, 86.803% 98.151%, 86.411% 98.949%, 86.006% 99.528%, 85.592% 99.881%, 85.171% 100%, 0% 100%, 0% 0%);
}

.details-box {
  min-height: 4rem;
}

label {
  color: colors.$color-grauweiß;
}

</style>

<template>
  <div class="flex flex-row w-full sticky-content justify-content-center">
    <div class="flex flex-column h-full content-center-column mx-2">
      <Message severity="info"> Achtung! Die Daten werden automatisch erfasst und nicht manuell geprüft.
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter
        kontrollieren!
      </Message>
      <div class="h-1rem"></div>
      <div class="flex flex-column h-full mx-2 mb-6 md:mx-5 align-items-center">
        <EventList :events="events"></EventList>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {EMPTY_EVENT_FILTERS, useEventListData} from "~/composables/eventListData.ts";
import {buildQuery} from "~/composables/queryGenerator.ts";
import {subDays} from "date-fns";
import { useLocationsData } from "~/composables/locationsData";
import { computed } from "vue";

useI18n()

interface StateOption {

  name: string,
  code: string

}

const selectedStates = ref<StateOption[]>([]);
const dates = ref<Date[]>([subDays(new Date(), 1)]);
const eventListData = useEventListData();
const locationsData = useLocationsData();

const tagFilterOptions = eventListData.availableEventTagFilters;
const tagFilterOptionsMapped = computed(() => tagFilterOptions.value.map((it) => {
      return {
        key: it.key,
        options: it.options.map(option => {
          return {text: option, selected: false}
        })
      }
    }
))

const events = eventListData.filteredEventsGroupedByMonth;
const stateFilterOptions = computed(() => {
  return locationsData.stateList().value.map((state) => ({name: state, code: state}));
});
const enabledFilters = ref(new Map<string, string[]>)
const filterOptions = computed(() => {
  return {
    fromDate: dates.value[0],
    toDate: dates.value[1],
    tagFilters: getTagFilters(enabledFilters.value, selectedStates.value)
  };
});

const query = computed(() => buildQuery(filterOptions.value))
const icsLink = computed(() => `/ical/calendar.ics?query=${query.value}`)

watchEffect(async () => await eventListData.loadEvents(filterOptions.value))

function getTagFilters(enabledFilters: Map<string, string[]>, selectedStates: StateOption[]): EventTagFilterOption[] {
  const stateNames = selectedStates.map(opt => opt.name);
  const locationIds = locationsData.loationIdsForStates(stateNames)
  const options: EventTagFilterOption[] = []
  if (locationIds.length > 0) {
    options.push({key: 'location_id', options: locationIds})
  }

  Array.from(enabledFilters.keys()).forEach((key) => {
    const values = enabledFilters.get(key);
    if (values != null && values.length > 0) {
      options.push({key, options: values});
    }
  })

  return options;
}

function setTagFilter(key: string, value: string) {
  const enabledFiltersValue = enabledFilters.value;
  if (!enabledFiltersValue.has(key)) {
    enabledFiltersValue.set(key, []);
  }
  if (!enabledFiltersValue.get(key).includes(value)) {
    enabledFiltersValue.get(key).push(value);
    enabledFilters.value = enabledFiltersValue;
  }
}

function removeTagFilter(key: string, value: string) {
  const enabledFiltersValue = enabledFilters.value;
  if (enabledFiltersValue.has(key)) {
    const index = enabledFiltersValue.get(key).indexOf(value, 0);
    enabledFiltersValue.get(key).splice(index, 1)
    enabledFilters.value = enabledFiltersValue;
  }
}

function hasTagFilter(key: string, value: string): boolean {
  if (enabledFilters.value.has(key)) {
    return enabledFilters.value.get(key).includes(value);
  }
  return false;
}

function switchTagFilter(key: string, value: string) {
  if (hasTagFilter(key, value)) {
    removeTagFilter(key, value);
  } else {
    setTagFilter(key, value);
  }
}

function clearFilters(): void {
  selectedStates.value = [];
  dates.value = [subDays(new Date(), 1)];
  enabledFilters.value = new Map<string, string[]>()
}

onMounted(loadData);

async function loadData(): Promise<void> {
  await eventListData.loadEvents(EMPTY_EVENT_FILTERS);
  await locationsData.loadLocations();
  await eventListData.loadFilterOptions(["tags", "locomotive_type", "recurrence.type", "registration"], ["tags"]);
}

</script>