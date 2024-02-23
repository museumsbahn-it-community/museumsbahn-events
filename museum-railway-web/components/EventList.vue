<style lang="scss">
@use "../assets/colors" as colors;
/* hide the opposite side of the timeline */
.p-timeline-event-opposite {
  display: none;
}

.timeline-heading {
  color: colors.$text-color-dark;
}

.p-timeline-event-marker {
  width: 1.25rem;
  height: 1.25rem;
  background: colors.$text-color-light;
  border: 2px solid colors.$text-color-dark;
}

.p-timeline-event-connector {
  background: colors.$text-color-dark;
}

.p-timeline.p-timeline-vertical .p-timeline-event-connector {
  width: 4px;
}

</style>
<template>
  <div class="bg-verkehrsrot rounded-corners-big px-5">
    <div class="flex flex-grow-1 min-h-0 w-full">
      <div v-if="groupedEvents.length > 0" class="w-full">
        <div v-for="eventGroup in groupedEvents" class="mb-4">
          <div class="flex flex-row my-4">
            <div class="flex timeline-heading align-items-center my-2">
              <h1 class="mx-6 my-2">{{ eventGroup.label }}</h1>
            </div>
            <div></div>
          </div>
          <Timeline :value="eventGroup.events" align="left">
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
        <div class="w-11 flex timeline-heading align-items-center my-2">
          <h2 class="mx-6 my-2 align-content-center">Keine Veranstaltungen gefunden!</h2>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { MuseumEvent } from '~/model/museumEvent.ts';
import { eventKey } from '~/model/util.ts';
import type { StateEntry } from '~/model/stateEntry.ts';
import type { MuseumEventGroup } from '~/stores/EventsStore.ts';

const EVENT_SELECTED_TOKEN = 'eventSelected';
const emit = defineEmits([EVENT_SELECTED_TOKEN]);
const props = withDefaults(defineProps<{
  highlightedEvent: MuseumEvent | undefined,
  locationIdFilter?: string | undefined,
  showDetailsButton: boolean,
  dates: Date[],
  selectedStates: StateEntry[] | undefined,
}>(), {
  dates: [null, null]
});

const eventsStore = useEventsStore();

function loadFilteredEvents(): MuseumEventGroup[] {
  let stateFilter: string[] | undefined = props.selectedStates?.map((it) => it.code);
  let startDateFilter = props.dates[0];
  let endDateFilter = props.dates[1];
  return eventsStore.filteredEventsGroupedByMonth(
      startDateFilter,
      endDateFilter,
      stateFilter,
      props.locationIdFilter,
      undefined);
}

const groupedEvents = ref<MuseumEventGroup[]>([]);

watch([() => props.selectedStates, () => props.dates, eventsStore.$state], () => {
  groupedEvents.value = loadFilteredEvents();
}, {immediate: true});


function isHighlighted(value: MuseumEvent): boolean {
  return props.highlightedEvent != undefined && eventKey(value) == eventKey(props.highlightedEvent);
}

function selectEvent(value: MuseumEvent): void {
  emit(EVENT_SELECTED_TOKEN, value);
}
</script>