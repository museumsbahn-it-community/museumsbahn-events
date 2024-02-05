<style lang="scss">
@use "../assets/colors_impl" as colors;
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

</style>

<template>
  <div class="flex flex-row h-full w-full sticky-content">
    <div class="flex flex-column h-full w-full md:w-7">
      <div class="h-2rem"></div>
      <div class="filter-box w-full h-14rem">
        <div class="h-full w-full flex align-items-center">
          <div class="w-1"></div>
          <div class="w-9 justify-content-center pr-6">
            <div class="flex flex-column md:flex-row-full p-2">
              <InlineMessage severity="warn"> Achtung! Die Daten werden automatisch erfasst und nicht manuell geprüft.
                Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter
                kontrollieren!
              </InlineMessage>
            </div>
            <div class="flex flex-column md:flex-row">
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
              <Button text class="justify-content-center"><i class="pi pi-filter-slash" @click="clearFilters()"/>
              </Button>
            </div>
          </div>
        </div>
      </div>
      <div class="h-2rem"></div>

      <div class="flex flex-column h-full w-full md:w-10"
           v-if="!hasSelectedEvent() || viewport.isGreaterOrEquals('tablet')">
        <EventList
            :highlighted-event="selectedEvent"
            @eventSelected="selectEvent"
            :show-details-button="true"
            :dates="dates"
            :selected-states="selectedStates"
            :location-id-filter="undefined"
        ></EventList>
      </div>
    </div>
    <div class="mx-5 md:w-5 details-box sticky-content">
      <div class="h-2rem sticky-content"></div>
      <div class="sticky-event-details" v-if="hasSelectedEvent()">
        <Button
            class="m-3"
            icon="pi pi-arrow-left" rounded outlined aria-label="Zurück"
            @click="navigateToEventList"
            v-if="viewport.isLessThan('tablet')"/>
        <EventDetails :museum-event="selectedEvent">
          Event Details
        </EventDetails>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { eventKey } from '~/model/util.ts';

const viewport = useViewport();
const eventsStore = useEventsStore();
const selectedEvent = ref<MuseumEvent | undefined>(undefined);
const router = useRouter();
const route = useRoute();
const locationsStore = useLocationsStore();

const selectedStates = ref<{
  name: string,
  code: string
}[] | undefined>();
const dates = ref<Date[]>([]);

const states = computed(() => {
  return locationsStore.stateList.map((state) => ({name: state, code: state}));
});

function clearFilters(): void {
  selectedStates.value = [];
  dates.value = [];
}

const eventKeyParam = route?.params?.eventKey;
if (eventKeyParam != undefined) {
  const eventByKey = eventsStore.getEventByKey(eventKeyParam);
  selectedEvent.value = toRaw(eventByKey);
}

function selectEvent(value: MuseumEvent) {
  router.push({name: 'eventDetails', params: {eventKey: eventKey(value)}});
  selectedEvent.value = toRaw(value);
}

function hasSelectedEvent(): boolean {
  return selectedEvent.value != undefined;
}

function navigateToEventList(): void {
  selectedEvent.value = undefined;
  router.back();
}

function isSelected(value: MuseumEvent) {
  return value == selectedEvent.value;
}

onMounted(loadData);

async function loadData(): Promise<void> {
  await eventsStore.loadEventsWithLocations();
}

</script>