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
    <div class="flex flex-column h-full w-full lg:w-7" v-if="!hasSelectedEvent() || viewport.isGreaterThan('desktop')">
      <div class="h-2rem"></div>
      <div class="filter-box w-11 h-20rem md:h-14rem">
        <div class="h-full w-full flex align-items-center">
          <div class="w-1"></div>
          <div class="w-9 justify-content-center pr-6">
            <div class="flex flex-column md:flex-row-full p-2">
              <InlineMessage severity="warn"> Achtung! Die Daten werden automatisch erfasst und nicht manuell geprüft.
                Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter
                kontrollieren!
              </InlineMessage>
            </div>
            <div class="flex flex-row">
              <MultiSelect v-model="selectedStates"
                           display="chip"
                           :options="states"
                           optionLabel="name"
                           placeholder="Bundesland"
                           :maxSelectedLabels="3"
                           class="m-2 w-5"
              />
              <Calendar class="m-2 w-5" v-model="dates" placeholder="Datum auswählen" selectionMode="range"
                        :manualInput="false"/>
              <Button text class="justify-content-center"><i class="pi pi-filter-slash" @click="clearFilters()"/>
              </Button>
            </div>
          </div>
        </div>
      </div>
      <div class="h-2rem"></div>

      <div class="flex flex-column h-full w-11 lg:w-10">
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
    <div class="mx-5 xl:w-5 details-box sticky-content" v-if="hasSelectedEvent()">
      <div class="h-2rem sticky-content"></div>
      <div class="sticky-event-details">
        <Button
            class="m-3"
            icon="pi pi-arrow-left" rounded outlined aria-label="Zurück"
            @click="navigateToEventList"
            v-if="viewport.isLessThan('desktop')"/>
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
const selectedEventKey = ref<string | undefined>(undefined);
const selectedEvent = computed(() => {
  if (selectedEventKey.value != null) {
    return eventsStore.getEventByKey(eventKeyParam);
  }
  return undefined;
})
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
  console.log('eventKey', eventKeyParam);
  selectedEventKey.value = eventKeyParam
}

function selectEvent(value: string) {
  router.push({name: 'eventDetails', params: {eventKey: eventKey(value)}});
  selectedEventKey.value = value;
}

function hasSelectedEvent(): boolean {
  return selectedEventKey.value != undefined;
}

function navigateToEventList(): void {
  selectedEventKey.value = undefined;

  if (window.history.state.back == null) {
    // if the user has entered via clicking the url back navigation should lead back to the event list
    router.push('/events')
  } else {
    router.back();
  }
}

onMounted(loadData);

async function loadData(): Promise<void> {
  await eventsStore.loadEventsWithLocations();


}

</script>