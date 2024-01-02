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
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter kontrollieren!
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
      <Button text><i class="pi pi-filter-slash" @click="clearFilters()"/></Button>
    </div>
    <div class="flex flex-grow-1 min-h-0 w-full md:w-5 m-2">
      <ScrollPanel v-if="groupedEvents.length > 0" class="w-full">
        <div v-for="eventGroup in groupedEvents">
          <h1 class="timeline-heading">{{ eventGroup.label }}</h1>
          <Timeline :value="eventGroup.events" align="left" class="customized-timeline">
            <template #content="slotProps">
              <Card class="mt-3" :class="{ selected: isSelected(slotProps.item)}"
                     >
                <template #title>
                  {{ slotProps.item.name }}
                </template>
                <template #subtitle>
                  <p> {{ getEventDate(slotProps.item.date).toDateString() }}</p>
                  <p> {{ slotProps.item.location?.location?.city }}, {{
                      slotProps.item.location?.location?.state
                    }}</p>
                </template>
                <template #content>
                  <div class="flex align-items-end w-full">
                    <div class="flex-grow-1"></div>
                    <Button class="dark-text" @click="selectEvent(slotProps.item)" text>Details</Button>
                  </div>
                </template>
              </Card>
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
const eventsStore = useEventsStore();
const locationsStore = useLocationsStore();
const selectedEvent = ref<MuseumEvent | undefined>(undefined);

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

function selectEvent(value: MuseumEvent) {
  console.log("select event");
  console.log(value)
  selectedEvent.value = value;
}

function isSelected(value: MuseumEvent) {
  return value == selectedEvent.value;
}

</script>