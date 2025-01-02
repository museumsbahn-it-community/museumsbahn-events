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
import { subDays } from "date-fns";
import { ref } from "vue";
import { useEventsStore } from "~/stores/EventsStore";
import { useLocationsStore } from "~/stores/LocationsStore";
import { useAsyncData } from "nuxt/app";
import { storeToRefs } from "pinia";

useI18n()

interface StateOption {

  name: string,
  code: string

}

const locationsStore = useLocationsStore();
const eventsStore = useEventsStore();
await useAsyncData('locations', () => locationsStore.fetchLocations());
await useAsyncData('events', () => eventsStore.fetchAllEvents());

const selectedStates = ref<StateOption[]>([]);
const dates = ref<Date[]>([subDays(new Date(), 1)]);

const events = storeToRefs(eventsStore).filteredEventsGroupedByMonth;

</script>