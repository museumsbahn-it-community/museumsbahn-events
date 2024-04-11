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

.sticky-event-details {
  position: sticky;
  top: calc(variables.$navbar-height + 2rem);
  left: 0;
}

label {
  color: colors.$color-grauweiß;
}
</style>

<template>
  <div class="flex flex-row w-full sticky-content">
    <div class="flex flex-column h-full" :class="{ 'w-full': !hasSelectedEvent(), 'w-6': hasSelectedEvent() }"
         v-if="!hasSelectedEvent() || viewport.isGreaterOrEquals('desktop-xl')">
      <Message severity="info"> Achtung! Die Daten werden automatisch erfasst und nicht manuell geprüft.
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter
        kontrollieren!
      </Message>
      <div class="h-1rem"></div>
      <div class="filter-box w-11">
        <div class="h-full w-full flex align-items-center">
          <div class="w-1"></div>
          <div class="w-9 justify-content-center pr-6">
            <div class="flex flex-column flex-wrap p-3">
              <div class="flex flex-column md:flex-row">
                <div class="flex-grow-1 flex flex-column">
                  <label for="stateFilter">Bundesland:</label>
                  <MultiSelect id="stateFilter"
                               v-model="selectedStates"
                               display="chip"
                               :options="stateFilterOptions"
                               optionLabel="name"
                               placeholder="Bundesland"
                               :maxSelectedLabels="3"
                               class="m-2"
                  />
                </div>
                <div class="flex-grow-1 flex flex-column">
                  <label for="daterange">Datum (von - bis):</label>
                  <Calendar id="daterange" class="m-2" v-model="dates" placeholder="Datum auswählen"
                            selectionMode="range"
                            date-format="dd.mm.yy"
                            :manualInput="false"/>
                </div>
              </div>
              <div class="m-2 flex flex-row flex-wrap">
                <template v-for="key in enabledFilters.keys()">
                  <template v-for="value in enabledFilters.get(key)">
                    <Chip :label="$t(value)" class="m-1" @remove="removeTagFilter(key, value)" removable/>
                  </template>
                </template>
              </div>
              <label for="tagFilters">Filtern nach:</label>
              <Accordion id="tagFilters" class="m-2" multiple>
                <AccordionTab v-for="filterGroup in tagFilterOptionsMapped"
                              class="flex flex-row flex-wrap align-items-center"
                              :header="$t(filterGroup.key)"
                >
                  <Button v-for="option in filterGroup.options"
                          class="m-2 light-button"
                          @click="switchTagFilter(filterGroup.key, option.text)"
                          :outlined="!hasTagFilter(filterGroup.key, option.text)"
                          rounded>
                    {{ $t(option.text) }}
                  </Button>
                </AccordionTab>
              </Accordion>

              <div class="flex flex-row gap-2">
                <NuxtLink :to="icsLink" target="_blank" rel="noopener">
                  <Button class="light-button" label="Aktuelle Liste als .ics Kalender" outlined/>
                </NuxtLink>
                <Button outlined class="justify-content-center light-button" @click="clearFilters()">
                  Filter zurücksetzen <i class="ml-1 pi pi-filter-slash"/>
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="h-2rem"></div>

      <div class="flex flex-column h-full mx-2 mb-6 md:ml-5">
        <EventList
            :events="events"
            :highlighted-event="selectedEvent"
            @eventSelected="selectEvent"
        ></EventList>
      </div>
    </div>
    <div class="px-2 sm:px-5 xl:w-6 py-5 mb-3 w-full details-box sticky-content" v-if="hasSelectedEvent()">
      <div class="sticky-event-details">
        <Button
            class="m-3"
            icon="pi pi-arrow-left" rounded outlined aria-label="Zurück"
            @click="navigateToEventList"/>
        <EventDetails :museum-event="selectedEvent"
                      :no-event-selected-placeholder-text="noEventSelectedPlaceholderText">
          Event Details
        </EventDetails>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {eventKey} from '~/model/util.ts';
import {useGlobalConfigStore} from "~/stores/GlobalConfigStore.ts";
import {EMPTY_EVENT_FILTERS, useEventListData} from "~/composables/eventListData.ts";
import {buildQuery} from "~/composables/queryGenerator.ts";

useI18n()
const noEventSelectedPlaceholderText = "Bitte eine Veranstaltung auswählen um Details zu sehen."
const viewport = useViewport();
const router = useRouter();
const route = useRoute();
const globalConfig = useGlobalConfigStore();

interface StateOption {

  name: string,
  code: string

}

const selectedStates = ref<StateOption[]>([]);
const dates = ref<Date[]>([]);
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
const selectedEventKey = ref<string | undefined>(undefined);
const selectedEvent = computed(() => {
  if (selectedEventKey.value != null) {
    return eventListData.getEventByKey(eventKeyParam);
  }
  return undefined;
});
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
  dates.value = [];
  enabledFilters.value = new Map<string, string[]>()
}

const eventKeyParam = route?.params?.eventKey;
if (eventKeyParam != undefined) {
  selectedEventKey.value = eventKeyParam;
}

function selectEvent(value: string) {
  selectedEventKey.value = value;
  router.push({name: 'eventDetails', params: {eventKey: eventKey(value)}});
}

function hasSelectedEvent(): boolean {
  return selectedEventKey.value != undefined;
}

function navigateToEventList(): void {
  selectedEventKey.value = undefined;
  if (globalConfig.historyIsEmpty) {
    router.push('/events'); // if history is empty go back to the events list
  } else {
    router.back();
  }
}

onMounted(loadData);

async function loadData(): Promise<void> {
  await eventListData.loadEvents(EMPTY_EVENT_FILTERS);
  await locationsData.loadLocations();
  await eventListData.loadFilterOptions(["tags", "locomotive_type", "recurrence.type", "registration"], ["tags"]);
}

</script>