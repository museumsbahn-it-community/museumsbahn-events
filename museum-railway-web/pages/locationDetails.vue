<template>
  <div class="flex flex-row justify-content-center">
    <div class="w-full lg:w-10 xl:w-8 xxl:w-6 lg:p-5">
        <LocationDetails
          :museumLocation="museumLocation"
          :eventsGroupedByMonthAndDeparture="events"
        ></LocationDetails>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useAsyncData, useRoute, useState } from "nuxt/app";
import { useEventsStore } from "~/stores/EventsStore";
import { useLocationsStore } from "~/stores/LocationsStore";

const locationsStore = useLocationsStore();
const eventsStore = useEventsStore();

const route = useRoute();
const locationId = route?.params?.locationId as string;

await useAsyncData('locations', () => locationsStore.fetchLocations());
await useAsyncData('events', () => eventsStore.fetchEventsForLocation(locationId));

const museumLocation = locationsStore.locationById(locationId);
const events = useState(`location-${locationId}-events`, () => eventsStore.eventsForLocationIdGrouped(locationId))

useSeoMeta({
  title: () => `${museumLocation?.name}`,
  ogTitle: () => `Details und Veranstaltungen zu ${museumLocation?.name}`,
  description: 'Details zu den Veranstaltungen des Museums.',
  ogDescription: 'Details zu den Veranstaltungen des Museums.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})
</script>