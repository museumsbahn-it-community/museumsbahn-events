<style lang="scss">
/* hide the opposite side of the timeline */
.p-timeline-event-opposite {
  display: none;
}
</style>
<template>
  <div class="flex flex-column h-full w-full">
    <div class="flex flex-column md:w-5 md:flex-row m-2">
      <InlineMessage severity="warn"> Achtung! Die Daten werden automatisch erfasst und nicht manuell geprüft.
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der Vereine kontrollieren!
      </InlineMessage>
    </div>
    <div class="flex flex-column md:w-5 md:flex-row">
      <MultiSelect v-model="selectedStates"
                   display="chip"
                   :options="states"
                   optionLabel="name"
                   placeholder="Bundesland"
                   :maxSelectedLabels="3"
                   class="w-full m-2 md:w-5"
      />
      <Calendar class="m-2 w-full md:w-5" v-model="dates" placeholder="Select Dates" selectionMode="range"
                :manualInput="false"/>
      <PButton text><i class="pi pi-filter-slash" @click="clearFilters()"/></PButton>
    </div>
    <div class="flex flex-grow-1 min-h-0 w-full md:w-5 m-2">
      <ScrollPanel v-if="groupedEvents.length > 0" class="w-full">
        <div v-for="eventGroup in groupedEvents">
          <h1 class="timeline-heading">{{ eventGroup.label }}</h1>
          <Timeline :value="eventGroup.events" align="left" class="customized-timeline">
            <template #content="slotProps">
              <PCard class="mt-3">
                <template #title>
                  {{ slotProps.item.name }}
                </template>
                <template #subtitle>
                  <p> {{ getEventDate(slotProps.item.date).toDateString() }}</p>
                  <p> {{ slotProps.item.location?.location?.city }}, {{ slotProps.item.location?.location?.state }}</p>
                </template>
              </PCard>
            </template>
          </Timeline>
        </div>

      </ScrollPanel>
      <div v-else>
        Keine Veranstaltungen gefunden!
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">

import { computed, onMounted, ref } from 'vue';
import { useEventsStore } from '../store/EventsStore';
import { useLocationsStore } from '../store/LocationsStore.ts';

const eventsStore = useEventsStore();
const locationsStore = useLocationsStore();

const selectedStates = ref<{ name: string, code: string }[] | undefined>(undefined);
const states = ref<{ name: string, code: string }[]>([]);
const dates = ref<Date[]>([]);

const groupedEvents = computed(() => {
  let stateFilter: string[] | undefined = selectedStates.value?.map((it) => it.code);
  let startDateFilter = dates.value[0];
  let endDateFilter = dates.value[1];
  return eventsStore.filteredEventsGroupedByMonth(startDateFilter,
      endDateFilter,
      stateFilter,
      undefined,
      undefined);
});

onMounted(loadData);

async function loadData(): Promise<void> {
  await eventsStore.loadEventsWithLocations();
  states.value = locationsStore.stateList.map((state) => ({name: state, code: state}));
}

function clearFilters(): void {
  selectedStates.value = [];
  dates.value = [];
}

function getEventDate(value: string): Date {
  return new Date(value);
}
</script>