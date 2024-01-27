<template>
  <div class="flex flex-row h-full w-full">
    <div class="flex flex-column h-full w-full md:w-6"
         v-if="!hasSelectedEvent() || viewport.isGreaterOrEquals('tablet')">
      <EventList :highlighted-event="selectedEvent" @eventSelected="selectEvent" :show-filters="true" :show-details-button="true"></EventList>
    </div>
    <div class="m-5 md:w-6 sticky" v-if="hasSelectedEvent()">
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
</template>
<script setup lang="ts">
import { eventKey } from '~/model/util.ts';

const viewport = useViewport();
const eventsStore = useEventsStore();
const selectedEvent = ref<MuseumEvent | undefined>(undefined);
const router = useRouter();
const route = useRoute();

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