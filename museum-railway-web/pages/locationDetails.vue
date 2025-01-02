<template>
  <div class="flex flex-row justify-content-center">
    <div class="w-full lg:w-10 xl:w-8 xxl:w-6 lg:p-5">
        <LocationDetails
          :museumLocation="museumLocation"
          :events="events"
        ></LocationDetails>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useAsyncData, useRoute } from "nuxt/app";
import { storeToRefs } from "pinia";
import { useEventsStore } from "~/stores/EventsStore";
import { useLocationsStore } from "~/stores/LocationsStore";

const locationsStore = useLocationsStore();
const eventsStore = useEventsStore();

const route = useRoute();
const locationId = route?.params?.locationId as string;

await useAsyncData('locations', () => locationsStore.fetchLocations());
await useAsyncData('events', () => eventsStore.fetchEventsForLocation(locationId));

const museumLocation = locationsStore.locationById(locationId);
const events = storeToRefs(eventsStore).filteredEventsGroupedByMonth;
</script>