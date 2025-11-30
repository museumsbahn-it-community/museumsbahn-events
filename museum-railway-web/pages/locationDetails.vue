<template>
  <div class="location-details w-full align-items-center flex flex-column m-0 p-2 md:p-0">
    <div class="w-full md:w-8 xl:w-6 m-0 md:m-2 mb-5 p-0">
      <LocationDetails :museumLocation="museumLocation" :eventsGroupedByMonthAndDeparture="events"></LocationDetails>
    </div>
  </div>
  <div class="default-footer"></div>
</template>
<style>
.location-details {
  top: calc(variables.$navbar-height);
}
</style>
<script setup lang="ts">
import {useRoute} from "nuxt/app";
import {getLocationById} from "~/composables/locationDataFunctions";

const {data: locations} = useAllLocations();
const {data: eventsRaw} = useAllEvents();

const route = useRoute();
const locationId = route?.params?.locationId as string;
const viewport = useViewport();

const museumLocation = computed(() => getLocationById(locations.value ?? [], locationId));
watch(museumLocation, () => {
  console.log(museumLocation.value)
})

const events = computed(() => eventsForLocationIdGrouped(eventsRaw.value ?? [], locationId));

useSeoMeta({
  title: () => `${museumLocation.value?.name}`,
  ogTitle: () => `Details und Veranstaltungen zu ${museumLocation.value?.name}`,
  description: 'Details zu den Veranstaltungen des Museums.',
  ogDescription: 'Details zu den Veranstaltungen des Museums.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})
</script>
