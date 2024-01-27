<style lang="scss">
/* hide the opposite side of the timeline */
.p-timeline-event-opposite {
  display: none;
}
</style>
<template>
  <div class="flex flex-column md:flex-row m-2 w-full">
    <InlineMessage severity="warn"> Achtung! Die Daten werden automatisch erfasst und nicht manuell geprüft.
      Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter kontrollieren!
    </InlineMessage>
  </div>
  <div class="flex flex-column md:flex-row" v-if="showFilters">
    <MultiSelect v-model="selectedStates"
                 display="chip"
                 :options="states"
                 optionLabel="name"
                 placeholder="Bundesland"
                 :maxSelectedLabels="3"
                 class="m-2 md:w-6"
    />
    <Calendar class="m-2 md:w-5" v-model="dates" placeholder="Datum auswählen" selectionMode="range"
              :manualInput="false"/>
    <Button text><i class="pi pi-filter-slash" @click="clearFilters()"/></Button>
  </div>
  <div class="flex flex-grow-1 min-h-0 w-full m-2">
    <div v-if="groupedEvents.length > 0" class="w-full">
      <div v-for="eventGroup in groupedEvents">
        <h1 class="timeline-heading">{{ eventGroup.label }}</h1>
        <Timeline :value="eventGroup.events" align="left" class="customized-timeline">
          <template #content="slotProps">
            <Card class="mt-3" :class="{ selected: isHighlighted(slotProps.item)}"
            >
              <template #title>
                {{ slotProps.item.name }}
              </template>
              <template #subtitle>
                <p> {{ formatDate(slotProps.item.date) }}</p>
                <p> {{ slotProps.item.location?.location?.city }}, {{
                    slotProps.item.location?.location?.state
                  }}</p>
              </template>
              <template #content>
                <div class="flex align-items-end w-full" v-if="showDetailsButton">
                  <div class="flex-grow-1"></div>
                  <Button class="dark-text" @click="selectEvent(slotProps.item)" text>Details</Button>
                </div>
              </template>
            </Card>
          </template>
        </Timeline>
      </div>
    </div>
    <div v-else>
      Keine Veranstaltungen gefunden!
    </div>
  </div>
</template>
<script setup lang="ts">
import { MuseumEvent } from '~/model/museumEvent.ts';
import { eventKey } from '~/model/util.ts';

const EVENT_SELECTED_TOKEN = 'eventSelected';
const emit = defineEmits([EVENT_SELECTED_TOKEN]);
const props = defineProps<{
  highlightedEvent: MuseumEvent | undefined,
  locationIdFilter: string | undefined,
  showFilters: boolean,
  showDetailsButton: boolean,
}>();

const eventsStore = useEventsStore();
const locationsStore = useLocationsStore();
const selectedEvent = ref<undefined | MuseumEvent>(undefined);

const selectedStates = ref<{
  name: string,
  code: string
}[] | undefined>(undefined);
const states = ref<{
  name: string,
  code: string
}[]>([]);
const dates = ref<Date[]>([]);

const groupedEvents = computed(() => {
  let stateFilter: string[] | undefined = selectedStates.value?.map((it) => it.code);
  let startDateFilter = dates.value[0];
  let endDateFilter = dates.value[1];
  const events = eventsStore.filteredEventsGroupedByMonth(startDateFilter,
      endDateFilter,
      stateFilter,
      props.locationIdFilter,
      undefined);
  if (events.length > 0) {
    selectedEvent.value = events[0].events[0];
  }
  return events;
});

computed(() => {
  states.value = locationsStore.stateList.map((state) => ({name: state, code: state}));
})

function clearFilters(): void {
  selectedStates.value = [];
  dates.value = [];
}

function isHighlighted(value: MuseumEvent): boolean {
  return props.highlightedEvent != undefined && eventKey(value) == eventKey(props.highlightedEvent);
}

function selectEvent(value: MuseumEvent): void {
  emit(EVENT_SELECTED_TOKEN, value);
}
</script>