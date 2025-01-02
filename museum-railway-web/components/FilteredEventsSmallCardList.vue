<template>
  <div :class="cardClass" v-for="eventEntry in filteredEvents" v-if="filteredEvents.length > 0">
    <EventCardSmall :event="eventEntry">
    </EventCardSmall>
  </div>
  <div :class="`${cardClass} flex h-full w-full bg-grauweiß border-radius-small`" v-else>
    <h2>Keine Veranstaltungen in diesem Zeitraum gefunden.</h2>
  </div>
</template>

<script lang="ts" setup>
import { isBefore } from 'date-fns';
import { useAsyncData } from 'nuxt/app';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useEventsStore } from '~/stores/EventsStore';
import { useLocationsStore } from '~/stores/LocationsStore';

const locationsStore = useLocationsStore();
const eventsStore = useEventsStore();
await useAsyncData('locations', () => locationsStore.fetchLocations());
// we need to fetch all events, because we do not know the location in advance and we cannot just fetch by id :(
await useAsyncData('events', () => eventsStore.fetchAllEvents());

const props = defineProps<{ dateFrom: Date, dateTo?: Date, cardClass?: string }>()

const filteredEvents = computed(() =>
  storeToRefs(eventsStore).filteredEvents.value.filter((event) => isBefore(props.dateFrom, event.date) 
    && (props.dateTo == null || isBefore(event.date, props.dateTo)))
)
</script>

<style></style>